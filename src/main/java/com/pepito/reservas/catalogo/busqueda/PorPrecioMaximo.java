package com.pepito.reservas.catalogo.busqueda;

import com.pepito.reservas.catalogo.ItemCatalogo;

/*
 * Leaf: el precio base del ítem es menor o igual al valor indicado.
 */

public class PorPrecioMaximo implements CriterioBusqueda {

    private final double maximo;

    public PorPrecioMaximo(double maximo) {
        this.maximo = maximo;
    }

    @Override
    public boolean cumple(ItemCatalogo item) {
        return item.precioBase() <= this.maximo;
    }
}
