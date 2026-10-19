package com.pepito.reservas.reserva.estado;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Prueba el comportamiento POR DEFECTO de EstadoReserva (rechazar todo),
 * usando una subclase anónima que no redefine ninguna operación.
 * Es lo mismo que le pasa a Finalizada y Cancelada.
 */
class EstadoReservaTest {

    private final EstadoReserva estado = new EstadoReserva(NombreEstado.FINALIZADA) {
    };

    private void verificarRechazo(String operacion, Executable accion) {
        MetodoInvalidoException ex = assertThrows(MetodoInvalidoException.class, accion);
        assertEquals("FINALIZADA", ex.getEstado());
        assertEquals(operacion, ex.getOperacion());
    }

    @Test
    void agregarItemSeRechazaPorDefecto() {
        verificarRechazo("agregar ítem", () -> estado.agregarItem(null, null));
    }

    @Test
    void quitarItemSeRechazaPorDefecto() {
        verificarRechazo("quitar ítem", () -> estado.quitarItem(null, null));
    }

    @Test
    void confirmarSeRechazaPorDefecto() {
        verificarRechazo("confirmar", () -> estado.confirmar(null));
    }

    @Test
    void hacerCheckInSeRechazaPorDefecto() {
        verificarRechazo("hacer check-in", () -> estado.hacerCheckIn(null));
    }

    @Test
    void iniciarViajeSeRechazaPorDefecto() {
        verificarRechazo("iniciar viaje", () -> estado.iniciarViaje(null));
    }

    @Test
    void finalizarSeRechazaPorDefecto() {
        verificarRechazo("finalizar", () -> estado.finalizar(null));
    }

    @Test
    void cancelarSeRechazaPorDefecto() {
        verificarRechazo("cancelar", () -> estado.cancelar(null));
    }
}