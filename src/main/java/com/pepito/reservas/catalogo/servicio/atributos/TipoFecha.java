package com.pepito.reservas.catalogo.servicio.atributos;

import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Date;

public class TipoFecha implements TipoDinamico{

    public TipoFecha(){}

    @Override
    public Boolean acepta(Object valor){
        return valor instanceof Date
                || valor instanceof Temporal
                || valor instanceof Calendar;
    }
}
