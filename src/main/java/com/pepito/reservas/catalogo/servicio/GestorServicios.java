package com.pepito.reservas.catalogo.servicio;

import com.pepito.reservas.catalogo.ItemCatalogo;
import com.pepito.reservas.catalogo.Validador;

public interface GestorServicios {

    void agregarItem(ItemCatalogo item);

    void removerItem(ItemCatalogo item);

    Integer cantidadItems();
}
