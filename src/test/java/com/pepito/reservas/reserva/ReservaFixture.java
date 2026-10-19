package com.pepito.reservas.reserva;

import com.pepito.reservas.catalogo.ItemCatalogo;
import com.pepito.reservas.cliente.Cliente;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/*
 * Helpers de test: llevan una reserva a cada estado recorriendo el camino feliz.
 * Cada método recibe la reserva (normal o espía) y la devuelve para poder encadenar.
 */

final class ReservaFixture {

    private ReservaFixture() {
    }

    static Reserva nueva() {
        return new Reserva(mock(Cliente.class));
    }

    /* Reserva real "espiada": permite verificar llamadas a bloquearCupos(), liberarCupos(), etc. */
    static Reserva espia() {
        return spy(new Reserva(mock(Cliente.class)));
    }

    static Reserva confirmada(Reserva r) {
        r.confirmar();
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
        when(item.getPrecioFinal()).thenReturn(precioFinal);
        return item;
    }
}
