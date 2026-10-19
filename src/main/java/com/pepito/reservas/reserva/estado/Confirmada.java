package com.pepito.reservas.reserva.estado;

import com.pepito.reservas.reserva.Reserva;

/*
 * Reserva confirmada/pagada: el Cupo ya está bloqueado.
 */

public class Confirmada extends EstadoReserva{
	
	public Confirmada() {
        super(NombreEstado.CONFIRMADA);
    }

	@Override
    public void hacerCheckIn(Reserva reserva) {
        reserva.cambiarEstado(new EnCheckIn());
    }
 
    @Override
    public void cancelar(Reserva reserva) {
        reserva.liberarCupos();
        reserva.cambiarEstado(new Cancelada());
    }

}