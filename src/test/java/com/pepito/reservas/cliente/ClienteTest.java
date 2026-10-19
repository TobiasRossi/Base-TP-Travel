package com.pepito.reservas.cliente;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClienteTest {

    @Test
    void elClienteConservaSuNombreYEmail() {
        Cliente cliente = new Cliente("Ana Pérez", "ana@mail.com");

        assertEquals("Ana Pérez", cliente.getNombre());
        assertEquals("ana@mail.com", cliente.getEmail());
    }
}
