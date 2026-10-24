package com.pepito.reservas.reserva;

import com.pepito.reservas.reserva.suscriptor.ReservaObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.pepito.reservas.reserva.ReservaFixture.PERIODO;
import static com.pepito.reservas.reserva.ReservaFixture.conItem;
import static com.pepito.reservas.reserva.ReservaFixture.nueva;
import static com.pepito.reservas.reserva.ReservaFixture.pago;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservaObserverTest {

    private Reserva reserva;
    private List<String> eventos;

    @BeforeEach
    void setUp() {
        reserva = conItem(nueva());
        eventos = new ArrayList<>();
    }

    private ReservaObserver registrador(String id) {
        return (r, anterior, nuevo) ->
                eventos.add(id + ":" + anterior.getNombre() + "->" + nuevo.getNombre());
    }

    @Test
    void elObserverRecibeLaReservaElEstadoAnteriorYElNuevo() {
        Reserva[] recibida = new Reserva[1];
        EstadoReserva[] anterior = new EstadoReserva[1];
        EstadoReserva[] nuevo = new EstadoReserva[1];
        reserva.agregarObservador((r, a, n) -> {
            recibida[0] = r;
            anterior[0] = a;
            nuevo[0] = n;
        });

        reserva.confirmar(pago());

        assertSame(reserva, recibida[0]);
        assertEquals("SOLICITUD_PENDIENTE", anterior[0].getNombre().name());
        assertEquals("CONFIRMADA", nuevo[0].getNombre().name());
    }

    @Test
    void seNotificaCadaTransicionEnOrden() {
        reserva.agregarObservador(registrador("A"));

        reserva.confirmar(pago());
        reserva.hacerCheckIn();
        reserva.iniciarViaje();
        reserva.finalizar();

        assertEquals(List.of(
                "A:SOLICITUD_PENDIENTE->CONFIRMADA",
                "A:CONFIRMADA->EN_CHECKIN",
                "A:EN_CHECKIN->EN_CURSO",
                "A:EN_CURSO->FINALIZADA"), eventos);
    }

    @Test
    void seNotificaATodosLosObservers() {
        reserva.agregarObservador(registrador("A"));
        reserva.agregarObservador(registrador("B"));

        reserva.cancelar();

        assertEquals(List.of(
                "A:SOLICITUD_PENDIENTE->CANCELADA",
                "B:SOLICITUD_PENDIENTE->CANCELADA"), eventos);
    }

    @Test
    void unObserverQuitadoYaNoRecibeNotificaciones() {
        ReservaObserver a = registrador("A");
        ReservaObserver b = registrador("B");
        reserva.agregarObservador(a);
        reserva.agregarObservador(b);

        reserva.quitarObservador(a);
        reserva.confirmar(pago());

        assertEquals(List.of("B:SOLICITUD_PENDIENTE->CONFIRMADA"), eventos);
    }

    @Test
    void quitarUnObserverNoRegistradoNoHaceNada() {
        reserva.quitarObservador(registrador("X"));

        reserva.confirmar(pago());

        assertTrue(eventos.isEmpty());
    }

    @Test
    void unaOperacionRechazadaNoNotificaNada() {
        reserva.agregarObservador(registrador("A"));

        assertThrows(RuntimeException.class, reserva::hacerCheckIn);

        assertTrue(eventos.isEmpty());
    }

    @Test
    void agregarOQuitarItemsNoNotificaPorqueNoHayCambioDeEstado() {
        reserva.agregarObservador(registrador("A"));

        reserva.agregarItem(ReservaFixture.item(100), PERIODO);

        assertTrue(eventos.isEmpty());
    }

    @Test
    void unObserverPuedeQuitarseDuranteLaNotificacionSinAfectarALosDemas() {
        ReservaObserver[] autoQuitable = new ReservaObserver[1];
        autoQuitable[0] = (r, anterior, nuevo) -> {
            eventos.add("A");
            r.quitarObservador(autoQuitable[0]);
        };
        reserva.agregarObservador(autoQuitable[0]);
        reserva.agregarObservador(registrador("B"));

        reserva.confirmar(pago());
        reserva.hacerCheckIn();

        assertEquals(List.of("A", "B:SOLICITUD_PENDIENTE->CONFIRMADA", "B:CONFIRMADA->EN_CHECKIN"), eventos);
    }
}
