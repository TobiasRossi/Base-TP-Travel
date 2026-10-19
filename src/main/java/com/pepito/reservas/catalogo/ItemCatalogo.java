package com.pepito.reservas.catalogo;

import com.pepito.reservas.servicio.Validador;

public interface ItemCatalogo extends Validador {

    public Double precioBase();
    public Double descuento();
    public Double preciFinal();

    @Override
    public Boolean validate();
    
    double getPrecioFinal();

}
