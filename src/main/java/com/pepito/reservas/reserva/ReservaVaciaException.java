package com.pepito.reservas.reserva;

/*
 * Se lanza al confirmar una Reserva sin ítems. Un ítem de precio 0 (regalo, cupón del 100 %) sí es válido.
 */

public class ReservaVaciaException extends ReservaException {

	private static final long serialVersionUID = 1L;

	public ReservaVaciaException(String message) {
        super(message);
    }
}
