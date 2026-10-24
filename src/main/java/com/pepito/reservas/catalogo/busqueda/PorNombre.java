package com.pepito.reservas.catalogo.busqueda;

import com.pepito.reservas.catalogo.ItemCatalogo;

import java.util.Locale;

/*
 * Leaf: el nombre del ítem contiene el texto ingresado, sin distinguir mayúsculas.
 */

public class PorNombre implements CriterioBusqueda {

    private final String texto;

    public PorNombre(String texto) {
        this.texto = texto.toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean cumple(ItemCatalogo item) {
        return item.nombre().toLowerCase(Locale.ROOT).contains(this.texto);
    }
}
