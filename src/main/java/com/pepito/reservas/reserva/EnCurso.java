package com.pepito.reservas.reserva;


/*
 * El viaje está en ejecución. Si cancela:
 * 		- Solo se reembolsan los Servicios no disfrutados (sin tasas administrativas).
 * 		- No libera Cupos: parte del Servicio ya se consumió.
 */

class EnCurso extends EstadoReserva {
	 
    EnCurso() {
        super(NombreEstado.EN_CURSO);
    }
 
    @Override
    void finalizar(Reserva reserva) {
        reserva.cambiarEstado(new Finalizada());
    }
 
    @Override
    void cancelar(Reserva reserva) {
        reserva.generarVoucherReembolsoParcial();
        reserva.cambiarEstado(new Cancelada());
    }
}