package com.pepito.reservas.catalogo.busqueda;

import com.pepito.reservas.catalogo.ItemCatalogo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/*
 * AND, OR y NOT con criterios fijos (siempre true / siempre false): se prueba solo cómo combinan.
 */

class CriteriosCompuestosTest {

    private static final CriterioBusqueda SI = item -> true;
    private static final CriterioBusqueda NO = item -> false;

    private final ItemCatalogo item = mock(ItemCatalogo.class);

    @Test
    void andCumpleSiTodosLosCriteriosCumplen() {
        assertTrue(new CriterioAnd(SI, SI, SI).cumple(item));
    }

    @Test
    void andNoCumpleSiAlgunCriterioNoCumple() {
        assertFalse(new CriterioAnd(SI, NO, SI).cumple(item));
    }

    @Test
    void orCumpleSiAlgunCriterioCumple() {
        assertTrue(new CriterioOr(NO, SI, NO).cumple(item));
    }

    @Test
    void orNoCumpleSiNingunCriterioCumple() {
        assertFalse(new CriterioOr(NO, NO).cumple(item));
    }

    @Test
    void notInvierteElResultadoDelCriterioEnvuelto() {
        assertFalse(new CriterioNot(SI).cumple(item));
        assertTrue(new CriterioNot(NO).cumple(item));
    }

    @Test
    void andNecesitaAlMenosDosCriterios() {
        assertThrows(CriterioInvalidoException.class, () -> new CriterioAnd(SI));
        assertThrows(CriterioInvalidoException.class, () -> new CriterioAnd());
    }

    @Test
    void orNecesitaAlMenosDosCriterios() {
        assertThrows(CriterioInvalidoException.class, () -> new CriterioOr(SI));
    }

    @Test
    void losCompuestosSePuedenAnidarSinLimite() {
        // NOT (SI AND NO) OR (NO OR NOT NO)  =  NOT false OR true  =  true
        CriterioBusqueda criterio = new CriterioOr(
                new CriterioNot(new CriterioAnd(SI, NO)),
                new CriterioOr(NO, new CriterioNot(NO)));

        assertTrue(criterio.cumple(item));
    }
}
