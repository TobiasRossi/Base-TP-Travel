package com.pepito.reservas.reserva.estado;

import com.pepito.reservas.reserva.Reserva;
import com.pepito.reservas.catalogo.ItemCatalogo;
import com.pepito.reservas.procesadorpago.MetodoPago;
import com.pepito.reservas.traslado.MetodoTraslado;

/*
 * El cliente está armando la reserva. Único estado donde se agregan/quitan ítems y se elige el traslado.
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
    public void elegirTraslado(Reserva reserva, MetodoTraslado metodoTraslado) {
        reserva.efectivizarEleccionDeTraslado(metodoTraslado);
    }

    /*
     * Confirmar = pagar. Primero los Cupos (bloquearlos es reversible sin costo para el cliente),
     * después el cobro (si falla, la Reserva libera los Cupos). La transición va al final:
     * si algo falla, la Reserva sigue en SOLICITUD_PENDIENTE y no se notifica nada.
     */
    @Override
    public void confirmar(Reserva reserva, MetodoPago metodoPago) {
        reserva.bloquearCupos();
        reserva.cobrar(metodoPago);
        reserva.cambiarEstado(new Confirmada());
    }

    @Override
    public void cancelar(Reserva reserva) {
        reserva.cambiarEstado(new Cancelada());
    }

}
