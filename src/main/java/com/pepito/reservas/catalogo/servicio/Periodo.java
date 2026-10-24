package com.pepito.reservas.catalogo.servicio;

import java.time.LocalDate;
import java.util.stream.Stream;

public class Periodo {

    LocalDate fechaDesde;
    LocalDate fechaHasta;


    public Periodo(LocalDate fechaDesde, LocalDate fechaHasta){
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
    }

    public LocalDate fechaDesde() {
        return fechaDesde;
    }

    public LocalDate fechaHasta() {
        return fechaHasta;
    }

    public Stream<LocalDate> getSetPeriodo(Periodo fechas) {
        return fechas.fechaDesde.datesUntil(fechas.fechaHasta().plusDays(1));
    }


}
