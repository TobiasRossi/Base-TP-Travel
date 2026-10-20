package com.pepito.reservas.catalogo.busqueda;

import com.pepito.reservas.catalogo.ItemCatalogo;

/*
 * Composite de un solo hijo: el ítem cumple si NO satisface el criterio envuelto.
 */

public class CriterioNot implements CriterioBusqueda {

    private final CriterioBusqueda criterio;

    public CriterioNot(CriterioBusqueda criterio) {
        this.criterio = criterio;
    }

    @Override
    public boolean cumple(ItemCatalogo item) {
        return !this.criterio.cumple(item);
    }
}
