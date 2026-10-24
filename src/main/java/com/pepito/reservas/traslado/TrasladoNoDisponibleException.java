package com.pepito.reservas.traslado;

/*
 * Se lanza cuando el método de traslado elegido no tiene disponibilidad para la reserva.
 */

public class TrasladoNoDisponibleException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TrasladoNoDisponibleException(String message) {
        super(message);
    }
}
