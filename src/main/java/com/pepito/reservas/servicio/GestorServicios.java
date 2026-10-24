package com.pepito.reservas.servicio;

import com.pepito.reservas.catalogo.ItemCatalogo;

public interface GestorServicios {

    public void agregarItem(ItemCatalogo item);

    public void removerItem(ItemCatalogo item);

}
