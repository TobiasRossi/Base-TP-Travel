package com.pepito.reservas.catalogo.servicio.atributos;

import com.pepito.reservas.catalogo.servicio.Validador;

public class AtributoDinamico implements Validador {

    String nombre;
    Boolean obligatorio;
    TipoDinamico tipo;
    Object valor;

    public AtributoDinamico(
            String nombre,
            Boolean obligatorio,
            TipoDinamico tipo,
            Object valor)
    {
        this.nombre = nombre;
        this.obligatorio = obligatorio;
        this.tipo = tipo;
        this.valor = valor;
    }

    @Override
    public Boolean validate() {
        return      obligatorio
                &&  valor != null
                &&  tipo.acepta(valor)
                ;
    }
}
