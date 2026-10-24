package com.pepito.reservas.reserva.suscriptor;

import com.pepito.reservas.reserva.EstadoReserva;
import com.pepito.reservas.reserva.Reserva;

/*
 * Rol Observer: los subsistemas del módulo 2.5 (email, voucher/factura, club) implementan esta interfaz. La Reserva no los conoce.
 */

public interface ReservaObserver {

    public void cambioDeEstado(Reserva reserva, EstadoReserva anterior, EstadoReserva nuevo);

}