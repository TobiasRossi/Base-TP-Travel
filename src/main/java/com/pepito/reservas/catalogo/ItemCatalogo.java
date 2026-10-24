package com.pepito.reservas.catalogo;

import com.pepito.reservas.catalogo.servicio.Disponibilidad;
import com.pepito.reservas.catalogo.servicio.Periodo;
import com.pepito.reservas.destino.LugarGeografico;

import java.time.LocalDate;

public abstract class ItemCatalogo implements Validador {

    protected String nombre;
    protected Double descuento;
    protected Disponibilidad disponibilidad;
    protected LugarGeografico destino;


    public ItemCatalogo(String nombre, Double descuento, Disponibilidad disponibilidad, LugarGeografico destino) {
        this.nombre = nombre;
        this.descuento = descuento;
        this.disponibilidad = disponibilidad;
        this.destino = destino;
    }

    public abstract Double precioBase();
    public abstract Double descuento();
    public abstract Double precioFinal();
    public abstract void reservar(Periodo fechas, Integer cantidad);
    public abstract void reservar(LocalDate fecha, Integer cantidad);
    public abstract Disponibilidad disponibilidad();

    // Pre-implementados (comunes a todos)
    public Boolean cubre(Periodo periodo) {return disponibilidad.cubre(periodo);}
    public String nombre(){return nombre;}
    public LugarGeografico getDestino() {return destino;}
}
