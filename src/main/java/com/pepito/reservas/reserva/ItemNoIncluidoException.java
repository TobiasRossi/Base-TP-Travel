package com.pepito.reservas.reserva;

/*
 * Se lanza al quitar de la Reserva un ítem que no forma parte de ella.
 */

public class ItemNoIncluidoException extends ReservaException {

	private static final long serialVersionUID = 1L;

	public ItemNoIncluidoException(String message) {
        super(message);
    }
}
