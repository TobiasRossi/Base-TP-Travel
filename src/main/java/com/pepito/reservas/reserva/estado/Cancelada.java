package com.pepito.reservas.reserva.estado;
 
/*
 * Estado terminal: no redefine ninguna operación, así que todas se rechazan.
 */

public class Cancelada extends EstadoReserva {
 
    public Cancelada() {
        super(NombreEstado.CANCELADA);
    }
}