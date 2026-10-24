package com.pepito.reservas.reserva;

import com.pepito.reservas.catalogo.ItemCatalogo;
import com.pepito.reservas.reserva.estado.EstadoReserva;
import com.pepito.reservas.reserva.estado.MetodoInvalidoException;
import com.pepito.reservas.reserva.suscriptor.ReservaObserver;
import com.pepito.reservas.catalogo.servicio.GestorServicios;
import com.pepito.reservas.catalogo.Validador;
import com.pepito.reservas.cliente.Cliente;
import com.pepito.reservas.traslado.MetodoTraslado;

import java.util.List;

public class Reserva implements GestorServicios, GestorReservas {

    Cliente cliente;
    List<Validador> items;
    EstadoReserva estado;
    MetodoTraslado metodoTraslado;
    List<VoucherCredito> vouchers;
    List<ReservaObserver> observadores;

    public Reserva(){}

    @Override
    public void agregarItem(ItemCatalogo item) {

    }

    @Override
    public void removerItem(ItemCatalogo item) {

    }

    @Override
    public Integer cantidadItems() {
        return 0;
    }

    @Override
    public void agregarServicio(Validador item) {

    }

    @Override
    public void quitarServicio(Validador item) {

    }

    @Override
    public void confirmar() {

    }

    @Override
    public void checkIn() {

    }

    @Override
    public void ejecutar() {

    }

    @Override
    public void checkOut() {

    }

    @Override
    public void cancelar() {

    }

    @Override
    public void rechazar(String message) throws MetodoInvalidoException {
        this.estado.rechazar(message);
    }

    public void agregarSuscriptor(ReservaObserver observador){

    }
    public void quitarSuscriptor(ReservaObserver observador){

    }
    public void notificar(EstadoReserva anterior, EstadoReserva nuevo){

    }

}
