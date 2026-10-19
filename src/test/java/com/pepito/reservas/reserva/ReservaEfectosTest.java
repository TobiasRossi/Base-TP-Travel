package com.pepito.reservas.reserva;

import com.pepito.reservas.catalogo.ItemCatalogo;
import com.pepito.reservas.catalogo.SinCupoException;
import com.pepito.reservas.cliente.Cliente;
import com.pepito.reservas.procesadorpago.MetodoPago;
import com.pepito.reservas.procesadorpago.PagoRechazadoException;
import com.pepito.reservas.reserva.estado.NombreEstado;
import com.pepito.reservas.traslado.MetodoTraslado;
import com.pepito.reservas.traslado.SinTraslado;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.List;

import static com.pepito.reservas.reserva.ReservaFixture.confirmada;
import static com.pepito.reservas.reserva.ReservaFixture.enCheckIn;
import static com.pepito.reservas.reserva.ReservaFixture.enCurso;
import static com.pepito.reservas.reserva.ReservaFixture.espia;
import static com.pepito.reservas.reserva.ReservaFixture.item;
import static com.pepito.reservas.reserva.ReservaFixture.nueva;
import static com.pepito.reservas.reserva.ReservaFixture.pago;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
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
    void confirmarBloqueaElCupoDeCadaItem() {
        Reserva reserva = nueva();
        ItemCatalogo a = item(100);
        ItemCatalogo b = item(200);
        reserva.agregarItem(a);
        reserva.agregarItem(b);

        reserva.confirmar(pago());

        verify(a).bloquearCupo(1);
        verify(b).bloquearCupo(1);
    }

    @Test
    void siUnItemNoTieneCupoSeLiberanLosYaBloqueadosYNoSeCobra() {
        Reserva reserva = nueva();
        ItemCatalogo conCupo = item(100);
        ItemCatalogo sinCupo = item(200);
        ItemCatalogo noAlcanzado = item(300);
        doThrow(new SinCupoException("sin cupo")).when(sinCupo).bloquearCupo(anyInt());
        reserva.agregarItem(conCupo);
        reserva.agregarItem(sinCupo);
        reserva.agregarItem(noAlcanzado);
        MetodoPago pago = pago();

        assertThrows(SinCupoException.class, () -> reserva.confirmar(pago));

        verify(conCupo).liberarCupo(1);
        verify(sinCupo, never()).liberarCupo(anyInt());
        verify(noAlcanzado, never()).bloquearCupo(anyInt());
        verify(pago, never()).procesarPago(anyDouble());
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
    void cancelarDesdeConfirmadaLiberaElCupoDeCadaItemSinVoucher() {
        Reserva reserva = nueva();
        ItemCatalogo a = item(100);
        reserva.agregarItem(a);
        confirmada(reserva);

        reserva.cancelar();

        verify(a).liberarCupo(1);
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

    /* Pago */

    private Reserva reservaConItemsYTraslado(double tasas) {
        Reserva reserva = nueva();
        reserva.agregarItem(item(1000));
        reserva.agregarItem(item(500));
        MetodoTraslado traslado = mock(MetodoTraslado.class);
        when(traslado.calcularCosto(reserva)).thenReturn(tasas);
        reserva.elegirTraslado(traslado);
        return reserva;
    }

    @Test
    void confirmarCobraServiciosMasTasasAlMetodoDePago() {
        Reserva reserva = reservaConItemsYTraslado(200);
        MetodoPago pago = pago();

        reserva.confirmar(pago);

        verify(pago).procesarPago(1700);
        Cobro cobro = reserva.getCobro().orElseThrow();
        assertEquals(1500, cobro.montoServicios(), 0.001);
        assertEquals(200, cobro.montoTasas(), 0.001);
        assertEquals(1700, cobro.total(), 0.001);
    }

    @Test
    void confirmarBloqueaCuposAntesDeCobrar() {
        Reserva reserva = espia();
        MetodoPago pago = pago();

        reserva.confirmar(pago);

        InOrder orden = inOrder(reserva, pago);
        orden.verify(reserva).bloquearCupos();
        orden.verify(pago).procesarPago(anyDouble());
        orden.verify(reserva).cambiarEstado(any());
    }

    @Test
    void siElPagoSeRechazaSeLiberanLosCuposYLaReservaSiguePendienteSinNotificar() {
        Reserva reserva = nueva();
        ItemCatalogo a = item(100);
        reserva.agregarItem(a);
        MetodoPago pago = pago();
        doThrow(new PagoRechazadoException("fondos insuficientes")).when(pago).procesarPago(anyDouble());
        List<String> eventos = new ArrayList<>();
        reserva.agregarObservador((r, anterior, nuevo) -> eventos.add(nuevo.toString()));

        assertThrows(PagoRechazadoException.class, () -> reserva.confirmar(pago));

        verify(a).bloquearCupo(1);
        verify(a).liberarCupo(1);
        assertEquals(NombreEstado.SOLICITUD_PENDIENTE, reserva.getEstado().getNombre());
        assertTrue(reserva.getCobro().isEmpty());
        assertTrue(eventos.isEmpty());
    }

    @Test
    void trasUnPagoRechazadoSePuedeReintentarConOtroMetodo() {
        Reserva reserva = nueva();
        reserva.agregarItem(item(100));
        MetodoPago rechazado = pago();
        doThrow(new PagoRechazadoException("rechazado")).when(rechazado).procesarPago(anyDouble());
        assertThrows(PagoRechazadoException.class, () -> reserva.confirmar(rechazado));

        reserva.confirmar(pago());

        assertEquals(NombreEstado.CONFIRMADA, reserva.getEstado().getNombre());
    }

    /* Traslado y tasas */

    @Test
    void sinTrasladoElegidoLasTasasSonCero() {
        Reserva reserva = nueva();
        reserva.agregarItem(item(1000));

        assertInstanceOf(SinTraslado.class, reserva.getMetodoTraslado());
        assertEquals(0, reserva.getTotalTasas(), 0.001);
        assertEquals(1000, reserva.getTotal(), 0.001);
    }

    @Test
    void elTotalDeTasasLoCalculaElMetodoDeTrasladoElegido() {
        Reserva reserva = reservaConItemsYTraslado(350);

        assertEquals(350, reserva.getTotalTasas(), 0.001);
        assertEquals(1850, reserva.getTotal(), 0.001);
    }

    /* Reembolsos (vouchers) */

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
        // PROVISORIO: hoy "no disfrutados" == total de servicios cobrados. Ajustar cuando se defina el criterio.
        assertEquals(1500, voucher.getMonto(), 0.001);
        assertNotNull(voucher.getMotivo());
    }

    @Test
    void elReembolsoDevuelveLoCobradoAunqueDespuesCambienLosPrecios() {
        Reserva reserva = nueva();
        ItemCatalogo hotel = item(1000);
        reserva.agregarItem(hotel);
        enCheckIn(reserva);
        when(hotel.getPrecioFinal()).thenReturn(5000.0);

        reserva.cancelar();

        assertEquals(1000, reserva.getVouchers().get(0).getMonto(), 0.001);
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
        Reserva reserva = new Reserva(cliente, 2);

        reserva.elegirTraslado(traslado);

        assertSame(cliente, reserva.getCliente());
        assertEquals(2, reserva.getCantidadPasajeros());
        assertSame(traslado, reserva.getMetodoTraslado());
    }

    @Test
    void elEstadoRechazadoNoEjecutaEfectosSobreLaReserva() {
        Reserva reserva = espia();
        reserva.cancelar();

        assertThrows(RuntimeException.class, () -> reserva.confirmar(pago()));

        verify(reserva, times(1)).cambiarEstado(any());
        verify(reserva, never()).bloquearCupos();
        verify(reserva, never()).cobrar(any());
    }
}
