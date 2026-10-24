package com.pepito.reservas.reserva;


/*
 * El cliente hizo el check-in previo. Si cancela: se libera el Cupo y se reembolsan Servicios y Tasas.
 */

class EnCheckIn extends EstadoReserva {
	 
    EnCheckIn() {
        super(NombreEstado.EN_CHECKIN);
    }
 
    @Override
    void iniciarViaje(Reserva reserva) {
        reserva.cambiarEstado(new EnCurso());
    }
 
    @Override
    void cancelar(Reserva reserva) {
        reserva.liberarCupos();
        reserva.generarVoucherReembolsoTotal();
        reserva.cambiarEstado(new Cancelada());
    }
}