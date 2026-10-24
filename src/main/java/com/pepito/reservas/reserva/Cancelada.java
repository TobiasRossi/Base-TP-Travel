package com.pepito.reservas.reserva;
 
/*
 * Estado terminal: no redefine ninguna operación, así que todas se rechazan.
 */

class Cancelada extends EstadoReserva {
 
    Cancelada() {
        super(NombreEstado.CANCELADA);
    }
}