package com.pepito.reservas.reserva.estado;

import com.pepito.reservas.reserva.ReservaException;

/*
 * Se lanza cuando se pide una operación que el Estado vigente de la Reserva no permite.
 */

public class MetodoInvalidoException extends ReservaException {

	private static final long serialVersionUID = 1L;
	private final String estado;
    private final String operacion;

    public MetodoInvalidoException(String estado, String operacion) {
        super("No se puede " + operacion + " en el estado '" + estado + "'.");
        this.estado = estado;
        this.operacion = operacion;
    }
 
    public String getEstado() {
        return estado;
    }
 
    public String getOperacion() {
        return operacion;
    }

}