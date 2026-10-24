package com.pepito.reservas.catalogo.servicio;

import com.pepito.reservas.catalogo.ItemCatalogo;
import com.pepito.reservas.excepciones.ServiceExceptions;
import com.pepito.reservas.catalogo.servicio.atributos.AtributoDinamico;
import com.pepito.reservas.destino.LugarGeografico;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Servicio extends ItemCatalogo {
    String codigoReserva;
    String proveedor;
    Categoria categoria;
    Double precioBase;


    List<AtributoDinamico> atributosAdicionales;

    public Servicio(
            String nombre,
            Double descuento,
            Disponibilidad disponibilidad,
            String codigoReserva,
            String proveedor,
            Categoria categoria,
            Double precioBase,
            LugarGeografico destino
        )
    {
        if (precioBase < 0.0){throw new ServiceExceptions.ErrorCreacionServicio(nombre, "PrecioBase no puede ser negativo");}
        if (descuento < 0.0 || descuento > 1.0 ){throw new ServiceExceptions.ErrorCreacionServicio(nombre, "Descuento puede tener un valor entre 0.0 y 1.0");}

        super(nombre, descuento, disponibilidad, destino);
        this.codigoReserva = codigoReserva;
        this.proveedor = proveedor;
        this.categoria = categoria;
        this.precioBase = precioBase;
        this.atributosAdicionales = new ArrayList<>(List.of());
    }

    @Override
    public Disponibilidad disponibilidad() {
        return disponibilidad;
    }

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
    }
    @Override
    public void reservar(LocalDate fecha, Integer cantidad) {
        disponibilidad.bloquearFecha(fecha, cantidad);
    }

    @Override
    public Double precioBase() {
        return precioBase;
    }

    @Override
    public Double descuento() {
        return 1 - this.descuento;
    }

    @Override
    public Double precioFinal() {
        return precioBase * descuento();
    }

    public void agregarAtributo(AtributoDinamico atributo){
        this.atributosAdicionales.add(atributo);
    }

    public Integer cantidadAtributos(){
        return atributosAdicionales.size();
    }

    public void eliminarAtributo(AtributoDinamico atributo) throws ServiceExceptions{

        if (!atributosAdicionales.contains(atributo)){throw new ServiceExceptions.MetodoInvalido("EliminarAtributo", "El atributo a remover no se encuentra dentro del Servicio");}

        this.atributosAdicionales.remove(atributo);
    }

    @Override
    public Boolean validate() throws ServiceExceptions{
        if (
                codigoReserva.isBlank()
                || nombre.isBlank()
                || proveedor.isBlank()
                || precioBase == 0.0
        )
        {
            return false;
        }

        return atributosAdicionales.stream().allMatch(AtributoDinamico::validate);
    }

}
