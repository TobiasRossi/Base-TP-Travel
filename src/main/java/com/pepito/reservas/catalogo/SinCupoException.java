package com.pepito.reservas.catalogo;

/*
 * Se lanza cuando se intenta bloquear el cupo de un ítem que no tiene unidades disponibles.
 */

public class SinCupoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SinCupoException(String message) {
        super(message);
    }
}
