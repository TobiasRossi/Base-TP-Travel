package com.pepito.reservas.reserva;
 
import com.pepito.reservas.catalogo.ItemCatalogo;
import com.pepito.reservas.procesadorpago.MetodoPago;
import com.pepito.reservas.traslado.MetodoTraslado;
 
/*
 * State del patrón State. Todas las operaciones se rechazan por default.
 * Cada Estado redefine únicamente las que le son válidas.
 * Los Estados terminales (Finalizada & Cancelada) no redefinen nada.
 * Es público solo para que los observers puedan consultar el nombre; las operaciones son package-private
 * y los Estados concretos no son visibles fuera del paquete: nadie puede forzar una transición.
 */
public abstract class EstadoReserva {
 
    private final NombreEstado nombre;
 
    EstadoReserva(NombreEstado nombre) {
        this.nombre = nombre;
    }
 
    public NombreEstado getNombre() {
        return nombre;
    }
 
    void agregarItem(Reserva reserva, ItemCatalogo item) {
        throw this.operacionInvalida("agregar ítem");
    }
 
    void quitarItem(Reserva reserva, ItemCatalogo item) {
        throw this.operacionInvalida("quitar ítem");
    }
 
    void elegirTraslado(Reserva reserva, MetodoTraslado metodoTraslado) {
        throw this.operacionInvalida("elegir traslado");
    }
 
    void confirmar(Reserva reserva, MetodoPago metodoPago) {
        throw this.operacionInvalida("confirmar");
    }
 
    void hacerCheckIn(Reserva reserva) {
        throw this.operacionInvalida("hacer check-in");
    }
 
    void iniciarViaje(Reserva reserva) {
        throw this.operacionInvalida("iniciar viaje");
    }
 
    void finalizar(Reserva reserva) {
        throw this.operacionInvalida("finalizar");
    }
 
    void cancelar(Reserva reserva) {
        throw this.operacionInvalida("cancelar");
    }
 
    /*
     * Crea la excepción Cada método la lanza con su propio "throw".
     * Decision tomada porque sino JaCoCo marcaría como no cubiertos los métodos que lo invocan.
     */
    MetodoInvalidoException operacionInvalida(String operacion) {
        return new MetodoInvalidoException(this.nombre.name(), operacion);
    }
 
    @Override
    public String toString() {
        return this.nombre.name();
    }
}
