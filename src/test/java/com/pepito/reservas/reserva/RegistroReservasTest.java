package com.pepito.reservas.reserva;

import com.pepito.reservas.cliente.Cliente;
import com.pepito.reservas.reserva.suscriptor.ReservaObserver;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.pepito.reservas.reserva.ReservaFixture.PERIODO;
import static com.pepito.reservas.reserva.ReservaFixture.RELOJ;
import static com.pepito.reservas.reserva.ReservaFixture.item;
import static com.pepito.reservas.reserva.ReservaFixture.pago;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class RegistroReservasTest {

    private final RegistroReservas registro = new RegistroReservas(RELOJ);
    private final List<String> eventos = new ArrayList<>();

    private ReservaObserver registrador(String id) {
        return (r, anterior, nuevo) -> eventos.add(id + ":" + nuevo.getNombre());
    }

    @Test
    void crearReservaLaDevuelveConSuClienteYPasajerosYLaGuarda() {
        Cliente cliente = mock(Cliente.class);

        Reserva reserva = registro.crearReserva(cliente, 3);

        assertSame(cliente, reserva.getCliente());
        assertEquals(3, reserva.getCantidadPasajeros());
        assertEquals(List.of(reserva), registro.getReservas());
    }

    @Test
    void lasReservasNuevasQuedanSuscriptasATodosLosSuscriptores() {
        registro.agregarSuscriptor(registrador("email"));
        registro.agregarSuscriptor(registrador("club"));
        Reserva reserva = registro.crearReserva(mock(Cliente.class), 1);

        reserva.cancelar();

        assertEquals(List.of("email:CANCELADA", "club:CANCELADA"), eventos);
    }

    @Test
    void unSuscriptorAgregadoDespuesObservaLasReservasExistentes() {
        Reserva existente = registro.crearReserva(mock(Cliente.class), 1);
        registro.agregarSuscriptor(registrador("factura"));

        existente.cancelar();

        assertEquals(List.of("factura:CANCELADA"), eventos);
    }

    @Test
    void lasReservasDelRegistroUsanSuReloj() {
        Reserva reserva = registro.crearReserva(mock(Cliente.class), 1);
        reserva.agregarItem(item(100), PERIODO);

        reserva.confirmar(pago());

        assertEquals(RELOJ.instant().atZone(RELOJ.getZone()).toLocalDateTime(), reserva.getCobro().orElseThrow().fecha());
    }

    @Test
    void laListaDeReservasExpuestaNoSePuedeModificar() {
        RegistroReservas conRelojDelSistema = new RegistroReservas();

        assertThrows(UnsupportedOperationException.class,
                () -> conRelojDelSistema.getReservas().add(mock(Reserva.class)));
    }
}
