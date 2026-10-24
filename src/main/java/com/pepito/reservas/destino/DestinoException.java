package com.pepito.reservas.destino;

public abstract class DestinoException extends RuntimeException {
    public DestinoException(String message) {
        super(message);
    }

    public static class IncompatibilidadDestino extends DestinoException{

        public IncompatibilidadDestino(LugarGeografico host, LugarGeografico guest) {
            super(guest.tipo().name() + " no puede agregarse a un " + host.tipo().name());
        }
    }
    public static class InexistenciaDestino extends DestinoException{

        public InexistenciaDestino(LugarGeografico target) {
            super(target.nombre() + " no se encontró en el listado de Destinos");
        }
    }
    public static class DuplicidadDestino extends DestinoException{

        public DuplicidadDestino(LugarGeografico target) {
            super(target.nombre() + " ya se encuentra agregado en el listado de Destinos");
        }
    }
    public static class ViolacionRango extends DestinoException{

        public ViolacionRango(LugarGeografico target) {
            super(target.nombre() + " pertenece a un destino no directo del listado de Destinos");
        }
    }
}
