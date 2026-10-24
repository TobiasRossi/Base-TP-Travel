package com.pepito.reservas.reserva;

import com.pepito.reservas.catalogo.ItemCatalogo;
import com.pepito.reservas.catalogo.servicio.Periodo;

/*
 * Una línea de la reserva: QUÉ ítem del catálogo y PARA QUÉ fechas.
 * El ítem es uno solo en el catálogo; lo que la reserva compra es "ese ítem en ese período".
 * La cantidad de pasajeros no va acá: es de toda la reserva y vale para todas sus líneas.
 */

public class ItemReservado {

    private final ItemCatalogo item;
    private final Periodo periodo;

    ItemReservado(ItemCatalogo item, Periodo periodo) {
        this.item = item;
        this.periodo = periodo;
    }

    public ItemCatalogo getItem() {
        return this.item;
    }

    public Periodo getPeriodo() {
        return this.periodo;
    }

    /* Mismo ítem y mismas fechas. Compara las fechas por valor: dos Periodo con las mismas fechas son la misma línea. */
    boolean corresponde(ItemCatalogo otroItem, Periodo otroPeriodo) {
        return this.item.equals(otroItem)
                && this.periodo.fechaDesde().equals(otroPeriodo.fechaDesde())
                && this.periodo.fechaHasta().equals(otroPeriodo.fechaHasta());
    }

    /* Un cupo por pasajero, en todas las fechas de la línea. El ítem (2.1) decide cómo. */
    void reservar(int cantidadPasajeros) {
        this.item.reservar(this.periodo, cantidadPasajeros);
    }

    void liberar(int cantidadPasajeros) {
        this.item.liberar(this.periodo, cantidadPasajeros);
    }

    double precioFinal() {
        return this.item.precioFinal();
    }
}
