package com.pepito.reservas.reserva.suscriptor;

import com.pepito.reservas.reserva.Reserva;
import com.pepito.reservas.reserva.estado.EstadoReserva;

public interface ReservaObserver {

    public void CambioDeEstado(Reserva reserva, EstadoReserva anterior, EstadoReserva nuevo);

}
