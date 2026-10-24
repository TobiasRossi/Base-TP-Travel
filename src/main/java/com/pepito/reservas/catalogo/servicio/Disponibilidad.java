package com.pepito.reservas.catalogo.servicio;

import com.pepito.reservas.excepciones.DisponibilidadException;
import com.pepito.reservas.excepciones.ServiceExceptions;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;

public class Disponibilidad {

    HashMap<LocalDate, Integer> disponibilidad;

    public Disponibilidad() {
        this.disponibilidad = new HashMap<>();
    }

    public Disponibilidad(Periodo periodo, Integer disponibilidad) {
        this.disponibilidad = new HashMap<>();

        this.agregarCupoMasivo(periodo, disponibilidad);
    }

    public Periodo periodo() {
        return new Periodo(fechaInicio(), fechaFin());
    }

    public Boolean cubre(Periodo periodo) {
        if (sinFechas()) {
            return false;
        }
        return !fechaInicio().isBefore(periodo.fechaDesde())
                && !fechaFin().isAfter(periodo.fechaHasta());
    }

    public LocalDate fechaInicio() {
        return disponibilidad.keySet().stream()
                .min(Comparator.naturalOrder())
                .orElseThrow(() -> new ServiceExceptions.MetodoInvalido(
                        "fechaInicio", "No hay fechas disponibles"));
    }

    public LocalDate fechaFin() {
        return disponibilidad.keySet().stream()
                .max(Comparator.naturalOrder())
                .orElseThrow(() -> new ServiceExceptions.MetodoInvalido(
                        "fechaFin", "No hay fechas disponibles"));
    }

    public void agregarCupoMasivo(Periodo fechas, Integer cupo){
        // Agrega un cupo determinado durante un período, agregándose el mismo todos los días
        // desde inicio a fin.
        fechas.getSetPeriodo(fechas).forEach(fecha -> agregarCupo(fecha, cupo));
    }

    public void agregarCupo(LocalDate fecha, Integer cupos){
        // Agrega un cupo a una fecha especifica dentro del map
        // En caso de que ya exista una key fecha dentro del hashmap, se agrega el cupo a la disponibilidad
        // Sn caso de que NO exista, se agrega la fecha en el HashMap con el cupo pasado por parámetro

        this.disponibilidad.compute(fecha, (k, cuposActuales) ->
                (cuposActuales == null) ? cupos : cuposActuales + cupos);

    }

    public Boolean sinFechas() {
        return disponibilidad.isEmpty();
    }

    public void liberarFecha(LocalDate fecha, Integer cupos){
        if(!disponibilidad.containsKey(fecha)){
            throw new DisponibilidadException.FechaSinDisponibilidad(fecha);
        }
        agregarCupo(fecha, cupos);
    }

    public void liberarPeriodo(Periodo fechas, Integer cantidad){
        try {
            fechas.getSetPeriodo(fechas).forEach(fecha -> liberarFecha(fecha, cantidad));
        }
        catch (Exception e) {
            throw new DisponibilidadException.PeriodoSinDisponibilidad(fechas);
        }

    }

    public void bloquearPeriodo(Periodo fechas, Integer cantidad){

        if(periodoDisponible(fechas, cantidad)){
            fechas.getSetPeriodo(fechas)
                    .forEach(fecha -> bloquearFecha(fecha,cantidad));

        }
        else {
            throw new DisponibilidadException.PeriodoSinDisponibilidad(fechas);
        }
    }

    public void bloquearFecha(LocalDate fecha, Integer cupos){
        // Decrementa un cupo a una fecha especifica dentro del map
        // Sn caso de que NO exista, se agrega la fecha en el HashMap con el cupo pasado por parámetro

        if(fechaDisponible(fecha,cupos)){
            this.disponibilidad.compute(fecha, (k, cuposActuales) -> cuposActuales - cupos);
        }
        else{
            throw new DisponibilidadException.FechaSinDisponibilidad(fecha);
        }
    }

    public Boolean periodoDisponible(Periodo fechas, Integer cantidad){
        return fechas.fechaDesde()
                .datesUntil(
                        fechas.fechaHasta().plusDays(1)
                        // Se obtiene una lista de todas las fechas que se comprenden hasta la final inclusive

                ).allMatch(localDate -> fechaDisponible(localDate, cantidad));
        // Se obtiene un bool resultante de verificar que todas las fechas obtenidas tengan cupo
    }


    public Boolean fechaDisponible(LocalDate fecha, Integer cantidad){
        Integer cupoActual = cupo(fecha);
        return disponibilidad.containsKey(fecha) && cupoActual != null && cupoActual >= cantidad;
    }

    public Integer cupo(LocalDate fecha){
        return disponibilidad.getOrDefault(fecha,0);
    }
}
