package com.pepito.reservas.traslado;

import com.pepito.reservas.reserva.Reserva;

public interface MetodoTraslado {

	double calcularCosto(Reserva reserva);

	/* Lanza TrasladoNoDisponibleException si el traslado no se puede garantizar (ej.: sin cupo de guías). */
	void verificarDisponibilidad(Reserva reserva);
	
}
