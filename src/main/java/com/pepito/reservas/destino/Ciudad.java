package com.pepito.reservas.destino;

public class Ciudad extends LugarGeografico{

    public Ciudad(TipoGeografico ciudad, String nombre) {
        super(TipoGeografico.CIUDAD, nombre);
    }

    @Override
    public Boolean contiene(LugarGeografico otro) {
        return this == otro;
    }

    @Override
    public Boolean puedeContener(LugarGeografico otro) {
        return false;
    }
}
