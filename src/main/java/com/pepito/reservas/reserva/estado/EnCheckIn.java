package com.pepito.reservas.reserva.estado;

import com.pepito.reservas.reserva.Reserva;

/*
 * El cliente hizo el check-in previo. Si cancela: se libera el Cupo y se reembolsan Servicios y Tasas.
 */

public class EnCheckIn extends EstadoReserva {
	 
    public EnCheckIn() {
        super(NombreEstado.EN_CHECKIN);
    }
 
    @Override
    public void iniciarViaje(Reserva reserva) {
        reserva.cambiarEstado(new EnCurso());
    }
 
    @Override
    public void cancelar(Reserva reserva) {
        reserva.liberarCupos();
        reserva.generarVoucherReembolsoTotal();
        reserva.cambiarEstado(new Cancelada());
    }
}