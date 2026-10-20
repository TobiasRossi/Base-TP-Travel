package com.pepito.reservas.catalogo.busqueda;

import com.pepito.reservas.catalogo.ItemCatalogo;

/*
 * Composite: el ítem cumple si satisface TODOS los criterios combinados.
 */

public class CriterioAnd extends CriterioCompuesto {

    public CriterioAnd(CriterioBusqueda... criterios) {
        super(criterios);
    }

    @Override
    public boolean cumple(ItemCatalogo item) {
        return this.getCriterios().stream().allMatch(c -> c.cumple(item));
    }
}
