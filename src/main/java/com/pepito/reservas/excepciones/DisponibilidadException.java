package com.pepito.reservas.excepciones;

import com.pepito.reservas.catalogo.servicio.Periodo;

import java.time.LocalDate;

public abstract class DisponibilidadException extends ServiceExceptions {
    protected DisponibilidadException(String mensaje) { super(mensaje); }

    public static class CupoInsuficiente extends DisponibilidadException {
        private final LocalDate fecha;
        private final int solicitados;
        private final int disponibles;

        public CupoInsuficiente(LocalDate fecha, int solicitados, int disponibles) {
            super("Cupo insuficiente el " + fecha + ": se pidieron " + solicitados
                    + " y hay " + disponibles);
            this.fecha = fecha;
            this.solicitados = solicitados;
            this.disponibles = disponibles;
        }
        public LocalDate fecha() { return fecha; }
        public int solicitados() { return solicitados; }
        public int disponibles() { return disponibles; }
    }

    public static class FechaSinDisponibilidad extends DisponibilidadException {
        public FechaSinDisponibilidad(LocalDate fecha) {
            super("No hay disponibilidad cargada para la fecha " + fecha);
        }
    }
    public static class PeriodoSinDisponibilidad extends DisponibilidadException {
        public PeriodoSinDisponibilidad(Periodo periodo) {
            super("No hay disponibilidad entre las fechas " + periodo.fechaDesde() + " - " + periodo.fechaHasta());
        }
    }

    public static class CantidadInvalida extends DisponibilidadException {
        public CantidadInvalida(int cantidad) {
            super("La cantidad debe ser mayor a cero: " + cantidad);
        }
    }

    public static class CupoInvalido extends DisponibilidadException {
        public CupoInvalido(LocalDate fecha, int cupo) {
            super("El cupo para " + fecha + " no puede ser negativo: " + cupo);
        }
    }
}