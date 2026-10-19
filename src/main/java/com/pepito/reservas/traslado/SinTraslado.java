package com.pepito.reservas.traslado;

import com.pepito.reservas.reserva.Reserva;

/*
 * Null Object: la Reserva sin traslado elegido. Evita el null y el "if" al calcular las tasas.
 */

public class SinTraslado implements MetodoTraslado {

	@Override
	public double calcularCosto(Reserva reserva) {
		return 0;
	}

}
