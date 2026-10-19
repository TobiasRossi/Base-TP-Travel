package com.pepito.reservas.reserva.estado;
 
/*
 * Estado terminal: no redefine ninguna operación, así que todas se rechazan.
 */

public class Finalizada extends EstadoReserva {
 
    public Finalizada() {
        super(NombreEstado.FINALIZADA);
    }
}