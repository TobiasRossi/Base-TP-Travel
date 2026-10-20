package com.pepito.reservas.catalogo.busqueda;

import java.util.List;

/*
 * Base de los Composite que combinan varios criterios (AND, OR).
 * Concentra lo común: guardar los hijos y exigir al menos dos (combinar uno solo no tiene sentido).
 * Cada subclase solo define cómo combina los resultados.
 */

public abstract class CriterioCompuesto implements CriterioBusqueda {

    private final List<CriterioBusqueda> criterios;

    protected CriterioCompuesto(CriterioBusqueda... criterios) {
        if (criterios.length < 2) {
            throw new CriterioInvalidoException("Un criterio compuesto necesita al menos dos criterios (recibió " + criterios.length + ").");
        }
        this.criterios = List.of(criterios);
    }

    protected List<CriterioBusqueda> getCriterios() {
        return this.criterios;
    }
}
