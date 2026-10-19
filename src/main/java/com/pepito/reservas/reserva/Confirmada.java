package com.pepito.reservas.reserva;


/*
 * Reserva confirmada/pagada: el Cupo ya está bloqueado.
 */

class Confirmada extends EstadoReserva{
	
	Confirmada() {
        super(NombreEstado.CONFIRMADA);
    }

	@Override
    void hacerCheckIn(Reserva reserva) {
        reserva.cambiarEstado(new EnCheckIn());
    }
 
    @Override
    void cancelar(Reserva reserva) {
        reserva.liberarCupos();
        reserva.cambiarEstado(new Cancelada());
    }

}