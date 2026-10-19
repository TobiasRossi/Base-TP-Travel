package com.pepito.reservas.reserva.estado;
 
import com.pepito.reservas.catalogo.ItemCatalogo;
import com.pepito.reservas.reserva.Reserva;
 
/*
 * State del patrón State. Todas las operaciones se rechazan por default.
 * Cada Estado redefine únicamente las que le son válidas.
 * Los Estados terminales (Finalizada & Cancelada) no redefinen nada.
 */
public abstract class EstadoReserva {
 
    private final NombreEstado nombre;
 
    protected EstadoReserva(NombreEstado nombre) {
        this.nombre = nombre;
    }
 
    public NombreEstado getNombre() {
        return nombre;
    }
 
    public void agregarItem(Reserva reserva, ItemCatalogo item) {
        throw this.operacionInvalida("agregar ítem");
    }
 
    public void quitarItem(Reserva reserva, ItemCatalogo item) {
        throw this.operacionInvalida("quitar ítem");
    }
 
    public void confirmar(Reserva reserva) {
        throw this.operacionInvalida("confirmar");
    }
 
    public void hacerCheckIn(Reserva reserva) {
        throw this.operacionInvalida("hacer check-in");
    }
 
    public void iniciarViaje(Reserva reserva) {
        throw this.operacionInvalida("iniciar viaje");
    }
 
    public void finalizar(Reserva reserva) {
        throw this.operacionInvalida("finalizar");
    }
 
    public void cancelar(Reserva reserva) {
        throw this.operacionInvalida("cancelar");
    }
 
    /*
     * Crea la excepción Cada método la lanza con su propio "throw".
     * Decision tomada porque sino JaCoCo marcaría como no cubiertos los métodos que lo invocan.
     */
    protected MetodoInvalidoException operacionInvalida(String operacion) {
        return new MetodoInvalidoException(this.nombre.name(), operacion);
    }
 
    @Override
    public String toString() {
        return this.nombre.name();
    }
}
