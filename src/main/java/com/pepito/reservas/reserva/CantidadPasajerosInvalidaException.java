package com.pepito.reservas.reserva;

/*
 * Se lanza al crear una Reserva con menos de un pasajero.
 */

public class CantidadPasajerosInvalidaException extends ReservaException {

	private static final long serialVersionUID = 1L;

	public CantidadPasajerosInvalidaException(String message) {
        super(message);
    }
}
