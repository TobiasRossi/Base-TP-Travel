package com.pepito.reservas.reserva;

import com.pepito.reservas.catalogo.servicio.Periodo;
import com.pepito.reservas.catalogo.ItemCatalogo;
import com.pepito.reservas.cliente.Cliente;
import com.pepito.reservas.procesadorpago.MetodoPago;
import com.pepito.reservas.traslado.MetodoTraslado;
import com.pepito.reservas.traslado.TrasladoNoDisponibleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.pepito.reservas.reserva.ReservaFixture.PERIODO;
import static com.pepito.reservas.reserva.ReservaFixture.enCheckIn;
import static com.pepito.reservas.reserva.ReservaFixture.item;
import static com.pepito.reservas.reserva.ReservaFixture.nueva;
import static com.pepito.reservas.reserva.ReservaFixture.pago;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/*
 * Relaciones de la Reserva con los otros módulos: pasajeros y cupos (2.1), comprobante de pago (2.4),
 * disponibilidad del traslado (2.3), validación de ítems (2.1) y fecha del cobro (2.8).
 */

class ReservaRelacionesTest {

    /* Pasajeros */

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void unaReservaNecesitaAlMenosUnPasajero(int pasajeros) {
        assertThrows(CantidadPasajerosInvalidaException.class, () -> new Reserva(mock(Cliente.class), pasajeros));
    }

    @Test
    void cadaPasajeroPagaUnaUnidadDeCadaItem() {
        Reserva reserva = nueva(3);
        reserva.agregarItem(item(1000), PERIODO);
        reserva.agregarItem(item(500), PERIODO);

        assertEquals(4500, reserva.getTotalServicios(), 0.001);
    }

    @Test
    void confirmarBloqueaUnCupoPorPasajeroEnCadaItem() {
        Reserva reserva = nueva(3);
        ItemCatalogo hotel = item(1000);
        reserva.agregarItem(hotel, PERIODO);

        reserva.confirmar(pago());

        verify(hotel).reservar(PERIODO, 3);
    }

    @Test
    void cancelarLiberaUnCupoPorPasajeroEnCadaItem() {
        Reserva reserva = nueva(3);
        ItemCatalogo hotel = item(1000);
        reserva.agregarItem(hotel, PERIODO);
        reserva.confirmar(pago());

        reserva.cancelar();

        verify(hotel).liberar(PERIODO, 3);
    }

    /* Cobro: montos, comprobante y fecha */

    @Test
    void antesDeConfirmarNoHayCobro() {
        assertTrue(nueva().getCobro().isEmpty());
    }

    @Test
    void elCobroGuardaElComprobanteDelMedioDePagoYLaFechaDeConfirmacion() {
        Reserva reserva = nueva(2);
        reserva.agregarItem(item(1000), PERIODO);

        reserva.confirmar(pago());

        Cobro cobro = reserva.getCobro().orElseThrow();
        assertEquals(2000, cobro.montoServicios(), 0.001);
        assertEquals(0, cobro.montoTasas(), 0.001);
        assertEquals("TX-1", cobro.comprobante().getCodigoTransaccion());
        assertEquals(2000, cobro.comprobante().getMonto(), 0.001);
        assertEquals(LocalDateTime.of(2026, 11, 12, 10, 0), cobro.fecha());
    }

    @Test
    void elReembolsoTotalUsaLoCobradoPorTodosLosPasajeros() {
        Reserva reserva = nueva(2);
        reserva.agregarItem(item(1000), PERIODO);
        enCheckIn(reserva);

        reserva.cancelar();

        assertEquals(2000, reserva.getVouchers().get(0).getMonto(), 0.001);
    }

    /* Disponibilidad del traslado */

    @Test
    void siElTrasladoNoEstaDisponibleNoSeBloqueanCuposNiSeCobra() {
        Reserva reserva = nueva();
        ItemCatalogo hotel = item(1000);
        reserva.agregarItem(hotel, PERIODO);
        MetodoTraslado traslado = mock(MetodoTraslado.class);
        doThrow(new TrasladoNoDisponibleException("sin guías")).when(traslado).verificarDisponibilidad(reserva);
        reserva.elegirTraslado(traslado);
        MetodoPago pago = pago();

        assertThrows(TrasladoNoDisponibleException.class, () -> reserva.confirmar(pago));

        verify(hotel, never()).reservar(any(Periodo.class), anyInt());
        verify(pago, never()).procesarPago(anyDouble());
        assertEquals(NombreEstado.SOLICITUD_PENDIENTE, reserva.getEstado().getNombre());
    }

    @Test
    void confirmarVerificaLaDisponibilidadDelTrasladoElegido() {
        Reserva reserva = ReservaFixture.conItem(nueva());
        MetodoTraslado traslado = mock(MetodoTraslado.class);
        reserva.elegirTraslado(traslado);

        reserva.confirmar(pago());

        verify(traslado).verificarDisponibilidad(reserva);
    }

    /* Validación de ítems */

    @Test
    void noSePuedeAgregarUnItemQueNoPasaSuValidacion() {
        Reserva reserva = nueva();
        ItemCatalogo invalido = item(1000);
        when(invalido.validate()).thenReturn(false);

        assertThrows(ItemInvalidoException.class, () -> reserva.agregarItem(invalido, PERIODO));

        assertTrue(reserva.getItems().isEmpty());
    }

    @Test
    void lasExcepcionesDeRelacionesSonExcepcionesDeReserva() {
        ItemCatalogo invalido = item(1);
        when(invalido.validate()).thenReturn(false);

        assertThrows(ReservaException.class, () -> nueva().agregarItem(invalido, PERIODO));
        assertThrows(ReservaException.class, () -> nueva(0));
    }

    /* Fechas por línea (ItemReservado) */

    private static final Periodo OTRO_PERIODO = new Periodo(LocalDate.of(2026, 12, 26), LocalDate.of(2026, 12, 27));

    @Test
    void cadaItemSeGuardaConSuPeriodo() {
        Reserva reserva = nueva();
        ItemCatalogo hotel = item(1000);

        reserva.agregarItem(hotel, PERIODO);

        ItemReservado linea = reserva.getItems().get(0);
        assertSame(hotel, linea.getItem());
        assertSame(PERIODO, linea.getPeriodo());
    }

    @Test
    void elMismoItemEnDosPeriodosSonDosLineasYSeQuitaSoloLaPedida() {
        Reserva reserva = nueva();
        ItemCatalogo hotel = item(1000);
        reserva.agregarItem(hotel, PERIODO);
        reserva.agregarItem(hotel, OTRO_PERIODO);

        reserva.quitarItem(hotel, new Periodo(PERIODO.fechaDesde(), PERIODO.fechaHasta()));

        assertEquals(1, reserva.getItems().size());
        assertSame(OTRO_PERIODO, reserva.getItems().get(0).getPeriodo());
    }

    @Test
    void quitarUnItemConUnPeriodoQueNoTieneLanzaExcepcion() {
        Reserva reserva = nueva();
        ItemCatalogo hotel = item(1000);
        reserva.agregarItem(hotel, PERIODO);

        assertThrows(ItemNoIncluidoException.class, () -> reserva.quitarItem(hotel, OTRO_PERIODO));

        assertEquals(1, reserva.getItems().size());
    }

    @Test
    void confirmarReservaCadaItemParaLasFechasDeSuLinea() {
        Reserva reserva = nueva(2);
        ItemCatalogo hotel = item(1000);
        ItemCatalogo excursion = item(300);
        reserva.agregarItem(hotel, PERIODO);
        reserva.agregarItem(excursion, OTRO_PERIODO);

        reserva.confirmar(pago());

        verify(hotel).reservar(PERIODO, 2);
        verify(excursion).reservar(OTRO_PERIODO, 2);
    }

    @Test
    void cancelarLiberaCadaItemParaLasFechasDeSuLinea() {
        Reserva reserva = nueva(2);
        ItemCatalogo hotel = item(1000);
        reserva.agregarItem(hotel, OTRO_PERIODO);
        reserva.confirmar(pago());

        reserva.cancelar();

        verify(hotel).liberar(OTRO_PERIODO, 2);
    }
}
