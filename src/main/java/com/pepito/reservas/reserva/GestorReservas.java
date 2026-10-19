package com.pepito.reservas.reserva;
 
import com.pepito.reservas.catalogo.ItemCatalogo;
import com.pepito.reservas.procesadorpago.MetodoPago;
import com.pepito.reservas.traslado.MetodoTraslado;
 
/*
 * Operaciones que se le pueden pedir a una Reserva.
 * Es el contrato del Contexto (Reserva); NO es el contrato de los estados, que reciben la Reserva como parámetro (en EstadoReserva).
 */

public interface GestorReservas {
 
    void agregarItem(ItemCatalogo item);
 
    void quitarItem(ItemCatalogo item);
 
    void elegirTraslado(MetodoTraslado metodoTraslado);
 
    void confirmar(MetodoPago metodoPago);
 
    void hacerCheckIn();
 
    void iniciarViaje();
 
    void finalizar();
 
    void cancelar();
}