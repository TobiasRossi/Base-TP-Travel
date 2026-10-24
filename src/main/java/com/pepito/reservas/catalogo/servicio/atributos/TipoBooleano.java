package com.pepito.reservas.catalogo.servicio.atributos;

public class TipoBooleano implements  TipoDinamico {
    public TipoBooleano(){}

    @Override
    public Boolean acepta(Object valor){
        return valor instanceof Boolean;
    }

}
