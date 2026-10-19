package com.pepito.reservas.reserva;

/*
 * Se lanza al agregar a la Reserva un ítem que no pasa su validación (atributos obligatorios o tipos).
 */

public class ItemInvalidoException extends ReservaException {

	private static final long serialVersionUID = 1L;

	public ItemInvalidoException(String message) {
        super(message);
    }
}
