package com.pepito.reservas.destino;

import java.util.ArrayList;
import java.util.List;

public class Destino extends LugarGeografico {

    ArrayList<LugarGeografico> destinos;

    public Destino(TipoGeografico tipo, String nombre) {
        super(tipo, nombre);
        this.destinos = new ArrayList<>(List.of());
    }

    public Destino(TipoGeografico tipo, String nombre, ArrayList<LugarGeografico> destinos) {
        super(tipo, nombre);
        this.destinos = destinos;
    }

    @Override
    public Boolean contiene(LugarGeografico otro) {
        return this == otro || this.destinos.stream().anyMatch(lugar-> lugar.contiene(otro));
    }

    @Override
    public Boolean puedeContener(LugarGeografico otro) {
        return this.tipo().mayorQue(otro.tipo());
    }

    public void agregarDestino(LugarGeografico lugar){

        if (contiene(lugar)){
            throw new DestinoException.DuplicidadDestino(lugar);
        }
        if(!puedeContener(lugar)){
            throw new DestinoException.IncompatibilidadDestino(this,lugar);
        }
            this.destinos.add(lugar);
    }

    public void removerDestino(LugarGeografico lugar, Boolean override){

        if(this.destinos.remove(lugar)){
            return;
        }

        if(lugar == this || !contiene(lugar)){
            throw new DestinoException.InexistenciaDestino(lugar);
        }

        if(!override){
            throw new DestinoException.ViolacionRango(lugar);

        }

        // Metodo de remocion de objetos recursivo
        try {
            this.destinos.stream()
                    .filter(destino -> destino.contiene(lugar))
                    .map(destino -> (Destino) destino)
                    .findFirst()
                    .orElseThrow()
                    .removerDestino(lugar, true);
        }
        catch (Exception e){
            throw new DestinoException.InexistenciaDestino(lugar);

        }
    }
}