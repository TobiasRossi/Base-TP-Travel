package com.pepito.reservas.reserva.estado;

import com.pepito.reservas.catalogo.Validador;
import com.pepito.reservas.reserva.GestorReservas;

public abstract class EstadoReserva implements GestorReservas {

    NombreEstado nombre;

    @Override
    public void agregarServicio(Validador item) {
        this.rechazar("Agregar Servicio");
    }

    @Override
    public void quitarServicio(Validador item) {
        this.rechazar("Quitar Servicio");

    }

    @Override
    public void confirmar() {
        this.rechazar("Confirmar");

    }

    @Override
    public void checkIn() {
        this.rechazar("Check-In");

    }

    @Override
    public void ejecutar() {
        this.rechazar("Ejecutar");

    }

    @Override
    public void checkOut() {
        this.rechazar("Finalizar");

    }

    @Override
    public void cancelar() {
        this.rechazar("Cancelar");

    }

    @Override
    public void rechazar(String method) throws MetodoInvalidoException{
        throw new MetodoInvalidoException("La acción " + method + "no está disponible en el estado " + this.nombre);
    }


}
