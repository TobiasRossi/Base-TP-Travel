package com.pepito.reservas.destino;

public enum TipoGeografico {
    CONTINENTE(1),
    PAIS(2),
    CIUDAD(3);

    private final int nivel;

    TipoGeografico(int nivel) {
        this.nivel = nivel;
    }

    // El nivel de la entidad es mas bajo cuanto mas contenedor sea: CONTINENTE > PAIS > CIUDAD
    public boolean mayorQue(TipoGeografico otro) {
        return this.nivel < otro.nivel;
    }
}