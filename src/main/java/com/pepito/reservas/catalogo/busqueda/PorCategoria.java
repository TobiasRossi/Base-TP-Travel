package com.pepito.reservas.catalogo.busqueda;

import com.pepito.reservas.catalogo.Categoria;
import com.pepito.reservas.catalogo.ItemCatalogo;

/*
 * Leaf: el ítem pertenece a la categoría dada.
 * Le pregunta al ítem (perteneceA) en vez de pedirle su categoría y comparar:
 * así cada ítem decide (un Paquete puede responder distinto que un Servicio) sin "instanceof" acá.
 */

public class PorCategoria implements CriterioBusqueda {

    private final Categoria categoria;

    public PorCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public boolean cumple(ItemCatalogo item) {
        return item.perteneceA(this.categoria);
    }
}
