package com.pepito.reservas.catalogo.busqueda;

import com.pepito.reservas.catalogo.ItemCatalogo;
import com.pepito.reservas.catalogo.Validador;

public interface CriterioBusqueda {
    boolean cumple(ItemCatalogo itemCatalogo);
}
