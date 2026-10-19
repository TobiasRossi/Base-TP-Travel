package com.pepito.reservas.reserva;
 
import com.pepito.reservas.catalogo.ItemCatalogo;
import com.pepito.reservas.reserva.estado.Cancelada;
import com.pepito.reservas.reserva.estado.Confirmada;
import com.pepito.reservas.reserva.estado.EnCheckIn;
import com.pepito.reservas.reserva.estado.EnCurso;
import com.pepito.reservas.reserva.estado.EstadoReserva;
import com.pepito.reservas.reserva.estado.Finalizada;
import com.pepito.reservas.reserva.estado.NombreEstado;
import com.pepito.reservas.reserva.estado.MetodoInvalidoException;
import com.pepito.reservas.reserva.estado.SolicitudPendiente;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
 
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
 
import static com.pepito.reservas.reserva.ReservaFixture.cancelada;
import static com.pepito.reservas.reserva.ReservaFixture.confirmada;
import static com.pepito.reservas.reserva.ReservaFixture.enCheckIn;
import static com.pepito.reservas.reserva.ReservaFixture.enCurso;
import static com.pepito.reservas.reserva.ReservaFixture.finalizada;
import static com.pepito.reservas.reserva.ReservaFixture.nueva;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;


class ReservaCicloDeVidaTest {

	/* Estado Inicial */
	
    @Test
    void unaReservaNuevaEmpiezaEnSolicitudPendiente() {
        assertEquals(NombreEstado.SOLICITUD_PENDIENTE, nueva().getEstado().getNombre());
    }

    /* Transacciones Validas */
    
    static Stream<Arguments> transicionesValidas() {
        return Stream.of(
                Arguments.of("pendiente -> confirmar", (Supplier<Reserva>) () -> nueva(),
                        (Consumer<Reserva>) Reserva::confirmar, NombreEstado.CONFIRMADA),
                Arguments.of("pendiente -> cancelar", (Supplier<Reserva>) () -> nueva(),
                        (Consumer<Reserva>) Reserva::cancelar, NombreEstado.CANCELADA),
                Arguments.of("confirmada -> check-in", (Supplier<Reserva>) () -> confirmada(nueva()),
                        (Consumer<Reserva>) Reserva::hacerCheckIn, NombreEstado.EN_CHECKIN),
                Arguments.of("confirmada -> cancelar", (Supplier<Reserva>) () -> confirmada(nueva()),
                        (Consumer<Reserva>) Reserva::cancelar, NombreEstado.CANCELADA),
                Arguments.of("en check-in -> iniciar viaje", (Supplier<Reserva>) () -> enCheckIn(nueva()),
                        (Consumer<Reserva>) Reserva::iniciarViaje, NombreEstado.EN_CURSO),
                Arguments.of("en check-in -> cancelar", (Supplier<Reserva>) () -> enCheckIn(nueva()),
                        (Consumer<Reserva>) Reserva::cancelar, NombreEstado.CANCELADA),
                Arguments.of("en curso -> finalizar", (Supplier<Reserva>) () -> enCurso(nueva()),
                        (Consumer<Reserva>) Reserva::finalizar, NombreEstado.FINALIZADA),
                Arguments.of("en curso -> cancelar", (Supplier<Reserva>) () -> enCurso(nueva()),
                        (Consumer<Reserva>) Reserva::cancelar, NombreEstado.CANCELADA)
        );
    }
 
    @ParameterizedTest(name = "{0}")
    @MethodSource("transicionesValidas")
    void lasTransicionesValidasCambianElEstado(String descripcion, Supplier<Reserva> origen,
                                               Consumer<Reserva> accion, NombreEstado esperado) {
        Reserva reserva = origen.get();
 
        accion.accept(reserva);
 
        assertEquals(esperado, reserva.getEstado().getNombre());
    }
 
    @Test
    void caminoCompletoHastaFinalizada() {
        Reserva reserva = nueva();
 
        reserva.confirmar();
        reserva.hacerCheckIn();
        reserva.iniciarViaje();
        reserva.finalizar();
 
        assertEquals(NombreEstado.FINALIZADA, reserva.getEstado().getNombre());
    }

    /* Operacion X Estado - Toda Operacion NO permitida lanza excepcion. */
    
    static Stream<Arguments> operacionesInvalidas() {
        ItemCatalogo item = mock(ItemCatalogo.class);
 
        // Los nombres de operación deben coincidir con los que usa EstadoReserva.
        Map<String, Consumer<Reserva>> operaciones = new LinkedHashMap<>();
        operaciones.put("agregar ítem", r -> r.agregarItem(item));
        operaciones.put("quitar ítem", r -> r.quitarItem(item));
        operaciones.put("confirmar", Reserva::confirmar);
        operaciones.put("hacer check-in", Reserva::hacerCheckIn);
        operaciones.put("iniciar viaje", Reserva::iniciarViaje);
        operaciones.put("finalizar", Reserva::finalizar);
        operaciones.put("cancelar", Reserva::cancelar);
 
        Map<String, Supplier<Reserva>> estados = new LinkedHashMap<>();
        estados.put("SOLICITUD_PENDIENTE", () -> nueva());
        estados.put("CONFIRMADA", () -> confirmada(nueva()));
        estados.put("EN_CHECKIN", () -> enCheckIn(nueva()));
        estados.put("EN_CURSO", () -> enCurso(nueva()));
        estados.put("FINALIZADA", () -> finalizada(nueva()));
        estados.put("CANCELADA", () -> cancelada(nueva()));
 
        Map<String, Set<String>> validas = new LinkedHashMap<>();
        validas.put("SOLICITUD_PENDIENTE", Set.of("agregar ítem", "quitar ítem", "confirmar", "cancelar"));
        validas.put("CONFIRMADA", Set.of("hacer check-in", "cancelar"));
        validas.put("EN_CHECKIN", Set.of("iniciar viaje", "cancelar"));
        validas.put("EN_CURSO", Set.of("finalizar", "cancelar"));
        validas.put("FINALIZADA", Set.of());
        validas.put("CANCELADA", Set.of());
 
        return estados.entrySet().stream().flatMap(estado ->
                operaciones.entrySet().stream()
                        .filter(op -> !validas.get(estado.getKey()).contains(op.getKey()))
                        .map(op -> Arguments.of(estado.getKey(), estado.getValue(), op.getKey(), op.getValue())));
    }
 
    @ParameterizedTest(name = "{0}: {2} es inválida")
    @MethodSource("operacionesInvalidas")
    void unaOperacionInvalidaLanzaExcepcionDeDominioYNoCambiaElEstado(String estado, Supplier<Reserva> origen,
                                                                       String operacion, Consumer<Reserva> accion) {
        Reserva reserva = origen.get();
 
        MetodoInvalidoException ex =
                assertThrows(MetodoInvalidoException.class, () -> accion.accept(reserva));
 
        assertEquals(estado, ex.getEstado());
        assertEquals(operacion, ex.getOperacion());
        assertEquals(estado, reserva.getEstado().getNombre().name());
    }
 
    @Test
    void elMensajeDeLaExcepcionIndicaOperacionYEstado() {
        Reserva reserva = finalizada(nueva());
 
        MetodoInvalidoException ex = assertThrows(MetodoInvalidoException.class, reserva::cancelar);
 
        assertEquals("No se puede cancelar en el estado 'FINALIZADA'.", ex.getMessage());
    }
 
    @Test
    void laExcepcionDeOperacionInvalidaEsUnaExcepcionDeReserva() {
        Reserva reserva = cancelada(nueva());
 
        assertThrows(ReservaException.class, reserva::confirmar);
    }

    /* Estados Concretos - Nombre y Representacion */
    
    static Stream<Arguments> estadosConcretos() {
        return Stream.of(
                Arguments.of(new SolicitudPendiente(), NombreEstado.SOLICITUD_PENDIENTE),
                Arguments.of(new Confirmada(), NombreEstado.CONFIRMADA),
                Arguments.of(new EnCheckIn(), NombreEstado.EN_CHECKIN),
                Arguments.of(new EnCurso(), NombreEstado.EN_CURSO),
                Arguments.of(new Finalizada(), NombreEstado.FINALIZADA),
                Arguments.of(new Cancelada(), NombreEstado.CANCELADA)
        );
    }
 
    @ParameterizedTest
    @MethodSource("estadosConcretos")
    void cadaEstadoExponeSuNombre(EstadoReserva estado, NombreEstado esperado) {
        assertEquals(esperado, estado.getNombre());
        assertEquals(esperado.name(), estado.toString());
    }
 
    @Test
    void cadaTransicionInstanciaElEstadoConcretoEsperado() {
        assertInstanceOf(Confirmada.class, confirmada(nueva()).getEstado());
        assertInstanceOf(EnCheckIn.class, enCheckIn(nueva()).getEstado());
        assertInstanceOf(EnCurso.class, enCurso(nueva()).getEstado());
        assertInstanceOf(Finalizada.class, finalizada(nueva()).getEstado());
        assertInstanceOf(Cancelada.class, cancelada(nueva()).getEstado());
    }

}
