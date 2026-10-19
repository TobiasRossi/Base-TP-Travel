package com.pepito.reservas.catalogo;

import com.pepito.reservas.servicio.Validador;

public interface ItemCatalogo extends Validador {

    public Double precioBase();
    public Double descuento();
    public Double preciFinal();

    @Override
    public Boolean validate();
    
    double getPrecioFinal();

    /* Cupos: los usa la Reserva al confirmar/cancelar. Un Paquete los propaga a sus ítems. */

    /* Lanza SinCupoException si no hay unidades disponibles. */
    void bloquearCupo();

    void liberarCupo();

}
