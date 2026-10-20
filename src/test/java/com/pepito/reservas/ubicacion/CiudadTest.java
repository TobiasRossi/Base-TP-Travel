package com.pepito.reservas.ubicacion;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CiudadTest {

    @Test
    void dosCiudadesConMismoNombreYPaisSonIguales() {
        assertEquals(new Ciudad("Madrid", "España"), new Ciudad("Madrid", "España"));
    }

    @Test
    void ciudadesConDistintoPaisNoSonIguales() {
        assertNotEquals(new Ciudad("Córdoba", "Argentina"), new Ciudad("Córdoba", "España"));
    }

    @Test
    void conservaNombreYPais() {
        Ciudad madrid = new Ciudad("Madrid", "España");

        assertEquals("Madrid", madrid.nombre());
        assertEquals("España", madrid.pais());
    }
}
