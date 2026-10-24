package com.pepito.reservas.catalogo.busqueda;

import com.pepito.reservas.catalogo.ItemCatalogo;
import com.pepito.reservas.destino.LugarGeografico;

import java.time.LocalDate;

/*
 * Leaf: el ítem está en el destino dado y tiene al menos un cupo libre en la fecha dada.
 * El destino es un LugarGeografico (2.1): continente, país o ciudad. Buscar en "España"
 * encuentra los ítems de Madrid, porque España contiene a Madrid (destino.contiene).
 * Destino y fecha se evalúan juntos en el mismo criterio: separarlos en dos criterios
 * (PorDestino AND PorFecha) daría por bueno un ítem que está en el destino pero sin cupo ese día.
 */

public class PorDisponibilidad implements CriterioBusqueda {

    private final LocalDate fecha;
    private final LugarGeografico destino;

    public PorDisponibilidad(LocalDate fecha, LugarGeografico destino) {
        this.fecha = fecha;
        this.destino = destino;
    }

    @Override
    public boolean cumple(ItemCatalogo item) {
        return this.destino.contiene(item.getDestino()) && item.disponible(this.fecha, 1);
    }
}
