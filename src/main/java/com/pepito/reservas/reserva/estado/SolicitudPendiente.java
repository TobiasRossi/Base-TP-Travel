package com.pepito.reservas.reserva.estado;

import com.pepito.reservas.reserva.Reserva;
import com.pepito.reservas.catalogo.ItemCatalogo;

/*
 * El cliente está armando la reserva. Único estado donde se agregan/quitan ítems.
 */

public class SolicitudPendiente extends EstadoReserva {

	public SolicitudPendiente() {
        super(NombreEstado.SOLICITUD_PENDIENTE);
    }

    @Override
    public void agregarItem(Reserva reserva, ItemCatalogo item) {
        reserva.efectivizarAgregadoDeItem(item);
    }

    @Override
    public void quitarItem(Reserva reserva, ItemCatalogo item) {
        reserva.efectivizarEliminacionDeItem(item);
    }

    @Override
    public void confirmar(Reserva reserva) {
        reserva.bloquearCupos(); //Primero, ya que puede fallar (no hay Cupos).
        reserva.cambiarEstado(new Confirmada());
    }

    @Override
    public void cancelar(Reserva reserva) {
        reserva.cambiarEstado(new Cancelada());
    }
    
}