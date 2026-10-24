package com.pepito.reservas.catalogo.servicio.atributos;

public class TipoTexto implements TipoDinamico {

    public TipoTexto(){}

    @Override
    public Boolean acepta(Object valor){
        return valor instanceof String || valor instanceof Character;
    }

}
