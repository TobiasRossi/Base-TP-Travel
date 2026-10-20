package com.pepito.reservas.catalogo.busqueda;

import com.pepito.reservas.catalogo.ItemCatalogo;
import com.pepito.reservas.ubicacion.Ciudad;

import java.time.LocalDate;

/*
 * Leaf: el ítem está en el destino dado y tiene al menos un cupo libre en la fecha dada.
 * Destino y fecha se evalúan JUNTOS en el ítem, no como dos criterios separados (PorDestino AND PorFecha):
 * un paquete con un hotel en Madrid sin cupo y un tour en Roma con cupo pasaría ambos por separado
 * sin estar disponible en Madrid. Quién combina las dos condiciones lo decide cada ítem.
 */

public class PorDisponibilidad implements CriterioBusqueda {

    private final LocalDate fecha;
    private final Ciudad destino;

    public PorDisponibilidad(LocalDate fecha, Ciudad destino) {
        this.fecha = fecha;
        this.destino = destino;
    }

    @Override
    public boolean cumple(ItemCatalogo item) {
        return item.disponibleEn(this.destino, this.fecha);
    }
}
