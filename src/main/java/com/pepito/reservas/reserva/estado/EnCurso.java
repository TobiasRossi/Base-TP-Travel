package com.pepito.reservas.reserva.estado;

import com.pepito.reservas.reserva.Reserva;

/*
 * El viaje está en ejecución. Si cancela:
 * 		- Solo se reembolsan los Servicios no disfrutados (sin tasas administrativas).
 * 		- No libera Cupos: parte del Servicio ya se consumió.
 */

public class EnCurso extends EstadoReserva {
	 
    public EnCurso() {
        super(NombreEstado.EN_CURSO);
    }
 
    @Override
    public void finalizar(Reserva reserva) {
        reserva.cambiarEstado(new Finalizada());
    }
 
    @Override
    public void cancelar(Reserva reserva) {
        reserva.generarVoucherReembolsoParcial();
        reserva.cambiarEstado(new Cancelada());
    }
}