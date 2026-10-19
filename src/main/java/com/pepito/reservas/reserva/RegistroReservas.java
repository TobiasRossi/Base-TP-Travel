package com.pepito.reservas.reserva;

import com.pepito.reservas.cliente.Cliente;
import com.pepito.reservas.reserva.suscriptor.ReservaObserver;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Crea y conoce todas las reservas del sistema.
 * Es el único lugar que conecta los subsistemas (observers del 2.5) con cada Reserva: la Reserva sigue sin conocerlos.
 * También es la fuente de datos de los reportes (2.8).
 */

public class RegistroReservas {

    private final List<Reserva> reservas;
    private final List<ReservaObserver> suscriptores;
    private final Clock reloj;

    public RegistroReservas(Clock reloj) {
        this.reservas = new ArrayList<>();
        this.suscriptores = new ArrayList<>();
        this.reloj = reloj;
    }

    public RegistroReservas() {
        this(Clock.systemDefaultZone());
    }

    /* El suscriptor observa las reservas existentes y las que se creen después. */
    public void agregarSuscriptor(ReservaObserver suscriptor) {
        this.suscriptores.add(suscriptor);
        this.reservas.forEach(r -> r.agregarObservador(suscriptor));
    }

    public Reserva crearReserva(Cliente cliente, int cantidadPasajeros) {
        Reserva reserva = new Reserva(cliente, cantidadPasajeros, this.reloj);
        this.suscriptores.forEach(reserva::agregarObservador);
        this.reservas.add(reserva);
        return reserva;
    }

    public List<Reserva> getReservas() {
        return Collections.unmodifiableList(this.reservas);
    }
}
