package com.pepito.reservas.catalogo.busqueda;

import com.pepito.reservas.catalogo.Categoria;
import com.pepito.reservas.catalogo.ItemCatalogo;
import com.pepito.reservas.ubicacion.Ciudad;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/*
 * Hojas: cada una le pregunta UNA cosa al ítem. Los ítems son mocks de ItemCatalogo (los reales son del 2.1).
 */

class CriteriosSimplesTest {

    private static final Ciudad MADRID = new Ciudad("Madrid", "España");
    private static final LocalDate DIA_23 = LocalDate.of(2026, 10, 23);

    private ItemCatalogo itemLlamado(String nombre) {
        ItemCatalogo item = mock(ItemCatalogo.class);
        when(item.getNombre()).thenReturn(nombre);
        return item;
    }

    private ItemCatalogo itemConPrecioBase(double precio) {
        ItemCatalogo item = mock(ItemCatalogo.class);
        when(item.precioBase()).thenReturn(precio);
        return item;
    }

    /* PorNombre */

    @Test
    void porNombreCumpleSiElNombreContieneElTexto() {
        assertTrue(new PorNombre("central").cumple(itemLlamado("Noche Hotel central")));
    }

    @Test
    void porNombreNoDistingueMayusculas() {
        assertTrue(new PorNombre("HoTeL").cumple(itemLlamado("Noche Hotel Central")));
    }

    @Test
    void porNombreNoCumpleSiElNombreNoContieneElTexto() {
        assertFalse(new PorNombre("resort").cumple(itemLlamado("Noche Hotel Central")));
    }

    /* PorPrecioMaximo */

    @Test
    void porPrecioMaximoCumpleSiElPrecioBaseEsMenor() {
        assertTrue(new PorPrecioMaximo(20000).cumple(itemConPrecioBase(15000)));
    }

    @Test
    void porPrecioMaximoCumpleSiElPrecioBaseEsIgual() {
        assertTrue(new PorPrecioMaximo(20000).cumple(itemConPrecioBase(20000)));
    }

    @Test
    void porPrecioMaximoNoCumpleSiElPrecioBaseEsMayor() {
        assertFalse(new PorPrecioMaximo(20000).cumple(itemConPrecioBase(20000.01)));
    }

    /* PorCategoria */

    @Test
    void porCategoriaLePreguntaAlItemSiPerteneceALaCategoria() {
        Categoria categoria = Categoria.EXCURSION;
        ItemCatalogo pertenece = mock(ItemCatalogo.class);
        ItemCatalogo noPertenece = mock(ItemCatalogo.class);
        when(pertenece.perteneceA(categoria)).thenReturn(true);
        when(noPertenece.perteneceA(categoria)).thenReturn(false);

        assertTrue(new PorCategoria(categoria).cumple(pertenece));
        assertFalse(new PorCategoria(categoria).cumple(noPertenece));
    }

    /* PorDisponibilidad */

    @Test
    void porDisponibilidadCumpleSiElItemEstaDisponibleEnEseDestinoYFecha() {
        ItemCatalogo item = mock(ItemCatalogo.class);
        when(item.disponibleEn(MADRID, DIA_23)).thenReturn(true);

        assertTrue(new PorDisponibilidad(DIA_23, MADRID).cumple(item));
    }

    @Test
    void porDisponibilidadNoCumpleSiNoEstaDisponibleEnEseDestinoYFecha() {
        ItemCatalogo item = mock(ItemCatalogo.class);
        when(item.disponibleEn(MADRID, DIA_23)).thenReturn(false);
        when(item.disponibleEn(MADRID, DIA_23.plusDays(1))).thenReturn(true);

        assertFalse(new PorDisponibilidad(DIA_23, MADRID).cumple(item));
    }

    @Test
    void porDisponibilidadComparaElDestinoPorValor() {
        ItemCatalogo item = mock(ItemCatalogo.class);
        when(item.disponibleEn(MADRID, DIA_23)).thenReturn(true);

        assertTrue(new PorDisponibilidad(DIA_23, new Ciudad("Madrid", "España")).cumple(item));
    }

    /* Ejemplos combinados del enunciado */

    @Test
    void nombrePackYNoExcursionYPrecioMaximo() {
        // Nombre contiene "pack" AND NOT categoría = Excursión AND precio <= $25.000
        Categoria excursion = Categoria.EXCURSION;
        ItemCatalogo pack = mock(ItemCatalogo.class);
        when(pack.getNombre()).thenReturn("Pack Escapada Urbana");
        when(pack.perteneceA(excursion)).thenReturn(false);
        when(pack.precioBase()).thenReturn(21000.0);

        CriterioBusqueda criterio = new CriterioAnd(
                new PorNombre("pack"),
                new CriterioNot(new PorCategoria(excursion)),
                new PorPrecioMaximo(25000));

        assertTrue(criterio.cumple(pack));
        when(pack.precioBase()).thenReturn(30000.0);
        assertFalse(criterio.cumple(pack));
    }

    @Test
    void hotelOResort() {
        // Nombre contiene "hotel" OR nombre contiene "resort"
        CriterioBusqueda criterio = new CriterioOr(new PorNombre("hotel"), new PorNombre("resort"));

        assertTrue(criterio.cumple(itemLlamado("Resort Bahamas")));
        assertTrue(criterio.cumple(itemLlamado("Hotel Central")));
        assertFalse(criterio.cumple(itemLlamado("Excursión Museo")));
    }
}
