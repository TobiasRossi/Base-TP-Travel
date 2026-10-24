package com.pepito.reservas.reserva;
 
/*
 * Excepción de dominio base del módulo de reservas.
 */

public class ReservaException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ReservaException(String message) {
        super(message);
    }
}
