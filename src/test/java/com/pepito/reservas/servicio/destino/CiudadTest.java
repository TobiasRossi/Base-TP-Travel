package com.pepito.reservas.servicio.destino;

import com.pepito.reservas.destino.Ciudad;
import com.pepito.reservas.destino.TipoGeografico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocacionTest {

    Ciudad roma;
    Ciudad paris;

    @BeforeEach
    void setUp() {
        roma = new Ciudad(TipoGeografico.CIUDAD, "Roma");
        paris = new Ciudad(TipoGeografico.CIUDAD, "Paris");
    }

    @Test
    void locacion_expone_nombre_y_tipo() {
        assertEquals("Roma", roma.nombre());
        assertEquals(TipoGeografico.CIUDAD, roma.tipo());
    }

    @Test
    void locacion_solo_se_contiene_a_si_misma() {
        assertTrue(roma.contiene(roma));
        assertFalse(roma.contiene(paris));
    }

    @Test
    void locacion_no_puede_contener_nada() {
        assertFalse(roma.puedeContener(paris));
    }
}