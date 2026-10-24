package com.pepito.reservas.traslado;

import com.pepito.reservas.reserva.Reserva;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class SinTrasladoTest {

    @Test
    void elCostoDeNoTenerTrasladoEsCero() {
        assertEquals(0, new SinTraslado().calcularCosto(mock(Reserva.class)), 0.001);
    }

    @Test
    void noTenerTrasladoSiempreEstaDisponible() {
        assertDoesNotThrow(() -> new SinTraslado().verificarDisponibilidad(mock(Reserva.class)));
    }
}
