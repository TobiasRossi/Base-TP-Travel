package com.pepito.reservas.reserva;

import com.pepito.reservas.catalogo.ItemCatalogo;
import com.pepito.reservas.cliente.Cliente;
import com.pepito.reservas.procesadorpago.MetodoPago;
import com.pepito.reservas.reserva.estado.NombreEstado;
import com.pepito.reservas.traslado.MetodoTraslado;
import com.pepito.reservas.traslado.TrasladoNoDisponibleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static com.pepito.reservas.reserva.ReservaFixture.enCheckIn;
import static com.pepito.reservas.reserva.ReservaFixture.item;
import static com.pepito.reservas.reserva.ReservaFixture.nueva;
import static com.pepito.reservas.reserva.ReservaFixture.pago;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        reserva.agregarItem(item(1000));
        reserva.agregarItem(item(500));

        assertEquals(4500, reserva.getTotalServicios(), 0.001);
    }

    @Test
    void confirmarBloqueaUnCupoPorPasajeroEnCadaItem() {
        Reserva reserva = nueva(3);
        ItemCatalogo hotel = item(1000);
        reserva.agregarItem(hotel);

        reserva.confirmar(pago());

        verify(hotel).bloquearCupo(3);
    }

    @Test
    void cancelarLiberaUnCupoPorPasajeroEnCadaItem() {
        Reserva reserva = nueva(3);
        ItemCatalogo hotel = item(1000);
        reserva.agregarItem(hotel);
        reserva.confirmar(pago());

        reserva.cancelar();

        verify(hotel).liberarCupo(3);
    }

    /* Cobro: montos, comprobante y fecha */

    @Test
    void antesDeConfirmarNoHayCobro() {
        assertTrue(nueva().getCobro().isEmpty());
    }

    @Test
    void elCobroGuardaElComprobanteDelMedioDePagoYLaFechaDeConfirmacion() {
        Reserva reserva = nueva(2);
        reserva.agregarItem(item(1000));

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
        reserva.agregarItem(item(1000));
        enCheckIn(reserva);

        reserva.cancelar();

        assertEquals(2000, reserva.getVouchers().get(0).getMonto(), 0.001);
    }

    /* Disponibilidad del traslado */

    @Test
    void siElTrasladoNoEstaDisponibleNoSeBloqueanCuposNiSeCobra() {
        Reserva reserva = nueva();
        ItemCatalogo hotel = item(1000);
        reserva.agregarItem(hotel);
        MetodoTraslado traslado = mock(MetodoTraslado.class);
        doThrow(new TrasladoNoDisponibleException("sin guías")).when(traslado).verificarDisponibilidad(reserva);
        reserva.elegirTraslado(traslado);
        MetodoPago pago = pago();

        assertThrows(TrasladoNoDisponibleException.class, () -> reserva.confirmar(pago));

        verify(hotel, never()).bloquearCupo(anyInt());
        verify(pago, never()).procesarPago(anyDouble());
        assertEquals(NombreEstado.SOLICITUD_PENDIENTE, reserva.getEstado().getNombre());
    }

    @Test
    void confirmarVerificaLaDisponibilidadDelTrasladoElegido() {
        Reserva reserva = nueva();
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

        assertThrows(ItemInvalidoException.class, () -> reserva.agregarItem(invalido));

        assertTrue(reserva.getItems().isEmpty());
    }

    @Test
    void lasExcepcionesDeRelacionesSonExcepcionesDeReserva() {
        ItemCatalogo invalido = item(1);
        when(invalido.validate()).thenReturn(false);

        assertThrows(ReservaException.class, () -> nueva().agregarItem(invalido));
        assertThrows(ReservaException.class, () -> nueva(0));
    }
}
