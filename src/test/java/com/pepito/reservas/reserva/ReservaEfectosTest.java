package com.pepito.reservas.reserva;

import com.pepito.reservas.catalogo.ItemCatalogo;
import com.pepito.reservas.cliente.Cliente;
import com.pepito.reservas.reserva.estado.NombreEstado;
import com.pepito.reservas.traslado.MetodoTraslado;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.pepito.reservas.reserva.ReservaFixture.confirmada;
import static com.pepito.reservas.reserva.ReservaFixture.enCheckIn;
import static com.pepito.reservas.reserva.ReservaFixture.enCurso;
import static com.pepito.reservas.reserva.ReservaFixture.espia;
import static com.pepito.reservas.reserva.ReservaFixture.item;
import static com.pepito.reservas.reserva.ReservaFixture.nueva;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReservaEfectosTest {

    /* Ítems (solo en SOLICITUD_PENDIENTE) */

    @Test
    void enSolicitudPendienteSePuedenAgregarYQuitarItems() {
        Reserva reserva = nueva();
        ItemCatalogo a = item(100);
        ItemCatalogo b = item(200);

        reserva.agregarItem(a);
        reserva.agregarItem(b);
        assertEquals(List.of(a, b), reserva.getItems());

        reserva.quitarItem(a);
        assertEquals(List.of(b), reserva.getItems());
    }

    @Test
    void serviciosDoRecorreTodosLosItems() {
        Reserva reserva = nueva();
        ItemCatalogo a = item(100);
        ItemCatalogo b = item(200);
        reserva.agregarItem(a);
        reserva.agregarItem(b);

        List<ItemCatalogo> visitados = new ArrayList<>();
        reserva.serviciosDo(visitados::add);

        assertEquals(List.of(a, b), visitados);
    }

    @Test
    void laListaDeItemsExpuestaNoSePuedeModificar() {
        Reserva reserva = nueva();

        assertThrows(UnsupportedOperationException.class, () -> reserva.getItems().add(item(1)));
    }

    /* Cupos */

    @Test
    void confirmarBloqueaLosCupos() {
        Reserva reserva = espia();

        reserva.confirmar();

        verify(reserva, times(1)).bloquearCupos();
    }

    @Test
    void siFallaElBloqueoDeCuposLaReservaNoCambiaDeEstado() {
        Reserva reserva = espia();
        doThrow(new IllegalStateException("sin cupo")).when(reserva).bloquearCupos();

        assertThrows(IllegalStateException.class, reserva::confirmar);

        assertEquals(NombreEstado.SOLICITUD_PENDIENTE, reserva.getEstado().getNombre());
    }

    @Test
    void cancelarDesdePendienteNoLiberaCuposNiGeneraVouchers() {
        Reserva reserva = espia();

        reserva.cancelar();

        verify(reserva, never()).liberarCupos();
        assertTrue(reserva.getVouchers().isEmpty());
    }

    @Test
    void cancelarDesdeConfirmadaLiberaCuposSinVoucher() {
        Reserva reserva = confirmada(espia());

        reserva.cancelar();

        verify(reserva, times(1)).liberarCupos();
        assertTrue(reserva.getVouchers().isEmpty());
    }

    @Test
    void cancelarDesdeCheckInLiberaCupos() {
        Reserva reserva = enCheckIn(espia());

        reserva.cancelar();

        verify(reserva, times(1)).liberarCupos();
    }

    @Test
    void cancelarDesdeEnCursoNoLiberaCupos() {
        Reserva reserva = enCurso(espia());

        reserva.cancelar();

        verify(reserva, never()).liberarCupos();
    }

    /* Reembolsos (vouchers) */

    private Reserva reservaConItemsYTraslado(double tasas) {
        Reserva reserva = nueva();
        reserva.agregarItem(item(1000));
        reserva.agregarItem(item(500));
        MetodoTraslado traslado = mock(MetodoTraslado.class);
        when(traslado.calcularCosto(reserva)).thenReturn(tasas);
        reserva.setMetodoTraslado(traslado);
        return reserva;
    }

    @Test
    void cancelarDesdeCheckInReembolsaServiciosYTasas() {
        Reserva reserva = enCheckIn(reservaConItemsYTraslado(200));

        reserva.cancelar();

        assertEquals(1, reserva.getVouchers().size());
        VoucherCredito voucher = reserva.getVouchers().get(0);
        assertEquals(1700, voucher.getMonto(), 0.001);
        assertNotNull(voucher.getMotivo());
    }

    @Test
    void cancelarDesdeEnCursoReembolsaSoloServiciosSinTasas() {
        Reserva reserva = enCurso(reservaConItemsYTraslado(200));

        reserva.cancelar();

        assertEquals(1, reserva.getVouchers().size());
        VoucherCredito voucher = reserva.getVouchers().get(0);
        // PROVISORIO: hoy "no disfrutados" == total de servicios. Ajustar cuando se defina el criterio.
        assertEquals(1500, voucher.getMonto(), 0.001);
        assertNotNull(voucher.getMotivo());
    }

    @Test
    void sinMetodoDeTrasladoLasTasasSonCero() {
        Reserva reserva = nueva();
        reserva.agregarItem(item(1000));

        assertNull(reserva.getMetodoTraslado());
        assertEquals(0, reserva.getTotalTasas(), 0.001);
        assertEquals(1000, reserva.getTotalServicios(), 0.001);
    }

    @Test
    void elTotalDeTasasLoCalculaElMetodoDeTraslado() {
        Reserva reserva = reservaConItemsYTraslado(350);

        assertEquals(350, reserva.getTotalTasas(), 0.001);
    }

    @Test
    void sePuedeRegistrarUnVoucherDirectamente() {
        Reserva reserva = nueva();
        VoucherCredito voucher = new VoucherCredito(99, "ajuste manual");

        reserva.registrarVoucher(voucher);

        assertEquals(List.of(voucher), reserva.getVouchers());
        assertEquals(99, voucher.getMonto(), 0.001);
        assertEquals("ajuste manual", voucher.getMotivo());
    }

    @Test
    void laListaDeVouchersExpuestaNoSePuedeModificar() {
        Reserva reserva = nueva();

        assertThrows(UnsupportedOperationException.class,
                () -> reserva.getVouchers().add(new VoucherCredito(1, "x")));
    }

    /* Accesos */

    @Test
    void laReservaConservaSuClienteYSuMetodoDeTraslado() {
        Cliente cliente = mock(Cliente.class);
        MetodoTraslado traslado = mock(MetodoTraslado.class);
        Reserva reserva = new Reserva(cliente);

        reserva.setMetodoTraslado(traslado);

        assertSame(cliente, reserva.getCliente());
        assertSame(traslado, reserva.getMetodoTraslado());
    }

    @Test
    void elEstadoRechazadoNoEjecutaEfectosSobreLaReserva() {
        Reserva reserva = espia();
        reserva.cancelar();

        assertThrows(RuntimeException.class, reserva::confirmar);

        verify(reserva, times(1)).cambiarEstado(any());
        verify(reserva, never()).bloquearCupos();
    }
}
