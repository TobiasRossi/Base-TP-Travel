package com.pepito.reservas.catalogo.busqueda;

import com.pepito.reservas.catalogo.ItemCatalogo;

/*
 * Rol Component del patrón Composite.
 * Cada criterio sabe si UN ítem lo satisface; el catálogo recorre sus ítems y le pregunta a un único criterio.
 * Hojas (Leaf): PorNombre, PorPrecioMaximo, PorCategoria, PorDisponibilidad.
 * Compuestos (Composite): CriterioAnd, CriterioOr, CriterioNot. Como también son CriterioBusqueda, se anidan sin límite.
 */

public interface CriterioBusqueda {

    boolean cumple(ItemCatalogo item);

}
