package com.pepito.reservas.catalogo.busqueda;

import com.pepito.reservas.catalogo.ItemCatalogo;

/*
 * Composite: el ítem cumple si satisface AL MENOS UNO de los criterios combinados.
 */

public class CriterioOr extends CriterioCompuesto {

    public CriterioOr(CriterioBusqueda... criterios) {
        super(criterios);
    }

    @Override
    public boolean cumple(ItemCatalogo item) {
        return this.getCriterios().stream().anyMatch(c -> c.cumple(item));
    }
}
