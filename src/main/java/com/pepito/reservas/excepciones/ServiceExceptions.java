package com.pepito.reservas.excepciones;


public abstract class ServiceExceptions extends RuntimeException {
    protected ServiceExceptions(String message) {
        super(message);
    }

    public static class ErrorCreacionServicio extends ServiceExceptions {
        public ErrorCreacionServicio(String service, String message) {
            super("Error creando el servicio : " + service + " por atributos inválidos: " + message);
        }
    }

    public static class ErrorCreacionPaquete extends ServiceExceptions {
        public ErrorCreacionPaquete(String paquete, String message) {
            super("Error creando el paquete : " + paquete + " por atributos inválidos: " + message);
        }
    }

    public static class ErrorValidacion extends ServiceExceptions {
        public ErrorValidacion(String item, String message) {
            super("Error de validación del servicio : " + item + ": " + message);
        }
    }

    public static class MetodoInvalido extends ServiceExceptions {
        public MetodoInvalido(String metodo, String message) {
            super("Error de validación del método : " + metodo + ": " + message);
        }
    }

}

