package com.pepito.reservas.catalogo;

import com.pepito.reservas.catalogo.servicio.Periodo;

import java.time.LocalDate;

public interface Validador extends com.pepito.reservas.catalogo.servicio.Validador {


    @Override
    public Boolean validate();
    public abstract Boolean disponible(Periodo fechas, Integer cantidad);
    public abstract Boolean disponible(LocalDate fecha, Integer cantidad);
}
