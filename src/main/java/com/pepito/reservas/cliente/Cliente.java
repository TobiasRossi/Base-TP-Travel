package com.pepito.reservas.cliente;

/*
 * Titular de la reserva. El email lo usan las notificaciones (2.5).
 */

public class Cliente {

    private final String nombre;
    private final String email;

    public Cliente(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }
}
