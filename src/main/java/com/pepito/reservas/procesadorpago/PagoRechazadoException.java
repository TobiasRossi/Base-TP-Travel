package com.pepito.reservas.procesadorpago;

/*
 * Se lanza cuando un medio de pago no puede completar el cobro (datos inválidos, fondos insuficientes, etc.).
 */

public class PagoRechazadoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PagoRechazadoException(String message) {
        super(message);
    }
}
