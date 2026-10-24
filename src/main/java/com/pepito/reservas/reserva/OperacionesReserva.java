package com.pepito.reservas.reserva;
 
import com.pepito.reservas.catalogo.ItemCatalogo;
import com.pepito.reservas.catalogo.servicio.Periodo;
import com.pepito.reservas.procesadorpago.MetodoPago;
import com.pepito.reservas.traslado.MetodoTraslado;
 
/*
 * Operaciones que se le pueden pedir a una Reserva.
 * Es el contrato del Contexto (Reserva); NO es el contrato de los estados, que reciben la Reserva como parámetro (en EstadoReserva).
 */

public interface OperacionesReserva {
 
    void agregarItem(ItemCatalogo item, Periodo periodo);
 
    void quitarItem(ItemCatalogo item, Periodo periodo);
 
    void elegirTraslado(MetodoTraslado metodoTraslado);
 
    void confirmar(MetodoPago metodoPago);
 
    void hacerCheckIn();
 
    void iniciarViaje();
 
    void finalizar();
 
    void cancelar();
}