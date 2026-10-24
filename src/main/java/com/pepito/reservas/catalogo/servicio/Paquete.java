package com.pepito.reservas.catalogo.servicio;

import com.pepito.reservas.catalogo.ItemCatalogo;
import com.pepito.reservas.excepciones.ServiceExceptions;
import com.pepito.reservas.destino.LugarGeografico;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Paquete extends ItemCatalogo implements GestorServicios {

    String descripcion;
    List<ItemCatalogo> servicios;

    public Paquete(
            String nombre,
            Double descuento,
            String descripcion,
            Disponibilidad disponibilidad,
            LugarGeografico destino
    ) {
        if (descuento < 0.0 || descuento > 1.0) {
            throw new ServiceExceptions.ErrorCreacionPaquete(nombre, "Descuento puede tener un valor entre 0.0 y 1.0");
        }

        super(nombre, descuento, disponibilidad, destino);
        this.descripcion = descripcion;
        this.servicios = new ArrayList<>();
    }

    @Override
    public Disponibilidad disponibilidad() {
        return disponibilidad;
    }

    @Override
    public Double precioBase() {
        return servicios.stream().mapToDouble(ItemCatalogo::precioFinal).sum();
    }

    @Override
    public Double descuento() {
        return 1 - descuento;
    }

    @Override
    public Double precioFinal() {
        return this.precioBase() * this.descuento();
    }

    @Override
    public Boolean disponible(Periodo fechas, Integer cantidad) {
        return disponibilidad.periodoDisponible(fechas, cantidad);
    }

    @Override
    public Boolean disponible(LocalDate fecha, Integer cantidad) {
        return disponibilidad.fechaDisponible(fecha, cantidad);
    }

    @Override
    public void reservar(Periodo fechas, Integer cantidad) {
        disponibilidad.bloquearPeriodo(fechas, cantidad);

        for (ItemCatalogo item : servicios){
            item.disponibilidad().bloquearPeriodo(fechas, cantidad);
        }
    }

    @Override
    public void reservar(LocalDate fecha, Integer cantidad) {
        disponibilidad.bloquearFecha(fecha, cantidad);
        for (ItemCatalogo item : servicios){
            item.disponibilidad().bloquearFecha(fecha, cantidad);
        }
    }

    @Override
    public Boolean validate() {
        if (nombre == null || descripcion == null || disponibilidad.sinFechas()) {
            return false;
        }

        Periodo periodoPaquete = disponibilidad.periodo();

        return servicios.stream().allMatch(item ->
                item.validate() && item.cubre(periodoPaquete));
    }

    @Override
    public void agregarItem(ItemCatalogo item) {
        if (item.validate()) {
            this.servicios.add(item);
        } else {
            throw new ServiceExceptions.MetodoInvalido("Agregar Servicio", "No se pudo agregar el " + item.getClass().getName());
        }
    }

    @Override
    public void removerItem(ItemCatalogo item) {
        this.servicios.remove(item);
    }

    @Override
    public Integer cantidadItems() {
        return servicios.size();
    }
}