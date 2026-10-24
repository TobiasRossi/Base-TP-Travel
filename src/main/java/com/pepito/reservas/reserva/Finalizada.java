package com.pepito.reservas.reserva;
 
/*
 * Estado terminal: no redefine ninguna operación, así que todas se rechazan.
 */

class Finalizada extends EstadoReserva {
 
    Finalizada() {
        super(NombreEstado.FINALIZADA);
    }
}