package com.pepito.reservas.catalogo.busqueda;

/*
 * Se lanza al construir un criterio de búsqueda inválido (ej.: un AND/OR con menos de dos criterios).
 */

public class CriterioInvalidoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CriterioInvalidoException(String message) {
        super(message);
    }
}
