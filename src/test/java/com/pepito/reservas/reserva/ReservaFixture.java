package com.pepito.reservas.reserva;

import com.pepito.reservas.catalogo.ItemCatalogo;
import com.pepito.reservas.cliente.Cliente;
import com.pepito.reservas.procesadorpago.ComprobantePago;
import com.pepito.reservas.procesadorpago.MetodoPago;

import com.pepito.reservas.catalogo.servicio.Periodo;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/*
 * Helpers de test: llevan una reserva a cada estado recorriendo el camino feliz.
 * Cada método recibe la reserva (normal o espía) y la devuelve para poder encadenar.
 */

final class ReservaFixture {

    /* Reloj fijo: la fecha del cobro es siempre 2026-11-12T10:00. */
    static final Clock RELOJ = Clock.fixed(Instant.parse("2026-11-12T10:00:00Z"), ZoneOffset.UTC);

    /* Período por defecto de los ítems de los tests: 21/12 al 23/12. */
    static final Periodo PERIODO = new Periodo(LocalDate.of(2026, 12, 21), LocalDate.of(2026, 12, 23));

    private ReservaFixture() {
    }

    static Reserva nueva() {
        return nueva(1);
    }

    static Reserva nueva(int pasajeros) {
        return new Reserva(mock(Cliente.class), pasajeros, RELOJ);
    }

    /* Reserva real "espiada": permite verificar llamadas a bloquearCupos(), liberarCupos(), etc. */
    static Reserva espia() {
        return spy(nueva());
    }

    /* Medio de pago que siempre acepta y devuelve un comprobante. */
    static MetodoPago pago() {
        MetodoPago pago = mock(MetodoPago.class);
        when(pago.procesarPago(anyDouble())).thenAnswer(inv -> new ComprobantePago("TX-1", inv.getArgument(0)));
        return pago;
    }

    /* Una reserva necesita al menos un ítem para confirmarse: si no tiene, se le agrega uno de precio 0. */
    static Reserva conItem(Reserva r) {
        if (r.getItems().isEmpty()) {
            r.agregarItem(item(0), PERIODO);
        }
        return r;
    }

    static Reserva confirmada(Reserva r) {
        conItem(r);
        r.confirmar(pago());
        return r;
    }

    static Reserva enCheckIn(Reserva r) {
        confirmada(r);
        r.hacerCheckIn();
        return r;
    }

    static Reserva enCurso(Reserva r) {
        enCheckIn(r);
        r.iniciarViaje();
        return r;
    }

    static Reserva finalizada(Reserva r) {
        enCurso(r);
        r.finalizar();
        return r;
    }

    static Reserva cancelada(Reserva r) {
        r.cancelar();
        return r;
    }

    static ItemCatalogo item(double precioFinal) {
        ItemCatalogo item = mock(ItemCatalogo.class);
        when(item.precioFinal()).thenReturn(precioFinal);
        when(item.validate()).thenReturn(true);
        return item;
    }
}
