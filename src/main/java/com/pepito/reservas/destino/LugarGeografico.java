package com.pepito.reservas.destino;

public abstract class LugarGeografico {

    TipoGeografico tipo;
    String nombre;

    public LugarGeografico(TipoGeografico tipo, String nombre) {
        this.tipo = tipo;
        this.nombre = nombre;
    }

    public String nombre(){return nombre;}
    public TipoGeografico tipo(){return tipo;}
    public abstract Boolean contiene(LugarGeografico otro);
    public abstract Boolean puedeContener(LugarGeografico otro);
}
