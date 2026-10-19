package com.pepito.reservas.reserva;
 
import com.pepito.reservas.catalogo.ItemCatalogo;
import com.pepito.reservas.cliente.Cliente;
import com.pepito.reservas.reserva.estado.EstadoReserva;
import com.pepito.reservas.reserva.estado.SolicitudPendiente;
import com.pepito.reservas.reserva.suscriptor.ReservaObserver;
import com.pepito.reservas.traslado.MetodoTraslado;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/*
 * Contexto del Patrón State y Sujeto del Patrón Observer.
 * No decide operaciones válidas, las delega al Estado vigente. 
 */

public class Reserva implements GestorReservas {

	private final Cliente cliente;
    private final List<ItemCatalogo> items;
    private final List<VoucherCredito> vouchers;
    private final List<ReservaObserver> observadores;
    private EstadoReserva estado;
    private MetodoTraslado metodoTraslado;

    public Reserva(Cliente cliente){
    	this.cliente = cliente;
        this.items = new ArrayList<>();
        this.vouchers = new ArrayList<>();
        this.observadores = new ArrayList<>();
        this.estado = new SolicitudPendiente();
    }
    
    /* Operaciones Publicas. Solo delegan en el Estado */

    @Override
    public void agregarItem(ItemCatalogo item) {
        this.estado.agregarItem(this, item);
    }
 
    @Override
    public void quitarItem(ItemCatalogo item) {
        this.estado.quitarItem(this, item);
    }
 
    @Override
    public void confirmar() {
        this.estado.confirmar(this);
    }

    @Override
    public void hacerCheckIn() {
        this.estado.hacerCheckIn(this);
    }
 
    @Override
    public void iniciarViaje() {
        this.estado.iniciarViaje(this);
    }
 
    @Override
    public void finalizar() {
        this.estado.finalizar(this);
    }
 
    @Override
    public void cancelar() {
        this.estado.cancelar(this);
    }
    
    /* Cambio de Estado + Observer (Sujeto) - Lo invocan los estados concretos al hacer una transición */

    public void cambiarEstado(EstadoReserva nuevoEstado) {
        EstadoReserva estadoAnterior = this.estado;
        this.estado = nuevoEstado;
        this.notificar(estadoAnterior, nuevoEstado);
    }
 
    public void agregarObservador(ReservaObserver observador) {
        this.observadores.add(observador);
    }
 
    public void quitarObservador(ReservaObserver observador) {
        this.observadores.remove(observador);
    }

    private void notificar(EstadoReserva anterior, EstadoReserva nuevo) {
        new ArrayList<>(this.observadores)
                .forEach(o -> o.cambioDeEstado(this, anterior, nuevo));
    }

    /* Efectos causados por los Estados. La Reserva saber HACER, el Estado CUANDO */
    
    public void efectivizarAgregadoDeItem(ItemCatalogo item) {
        this.items.add(item);
    }
 
    public void efectivizarEliminacionDeItem(ItemCatalogo item) {
        this.items.remove(item);
    }

    public void bloquearCupos() {
        // TODO: decrementar la disponibilidad de los items (Cupo.bloquear) cuando esté el módulo 2.1.
        // Si no hay cupo, debe lanzar una excepción ANTES de que el estado haga la transición.
    }
 
    public void liberarCupos() {
        // TODO: incrementar la disponibilidad de los items (Cupo.liberar) cuando esté el módulo 2.1.
    }

    public void generarVoucherReembolsoTotal() {
        double monto = this.getTotalServicios() + this.getTotalTasas();
        this.registrarVoucher(new VoucherCredito(monto, "Cancelación en check-in: reembolso de servicios y tasas"));
    }
 
    public void generarVoucherReembolsoParcial() {
        double monto = this.getTotalServiciosNoDisfrutados();
        this.registrarVoucher(new VoucherCredito(monto, "Cancelación en curso: reembolso de servicios no disfrutados"));
    }
 
    public void registrarVoucher(VoucherCredito voucher) {
        this.vouchers.add(voucher);
    }

    /* Calculo de montos */
    
    public double getTotalServicios() {
        return this.items.stream().mapToDouble(ItemCatalogo::getPrecioFinal).sum();
    }
 
    public double getTotalTasas() {
        return this.metodoTraslado == null ? 0 : this.metodoTraslado.calcularCosto(this);
    }

    public double getTotalServiciosNoDisfrutados() {
        // Falta definir el criterio de "servicio disfrutado"
    	return this.getTotalServicios();
    }

    /* Getters & Setters */
    
    public void serviciosDo(Consumer<ItemCatalogo> bloque) {
        this.items.forEach(bloque);
    }
 
    public EstadoReserva getEstado() {
        return this.estado;
    }
 
    public Cliente getCliente() {
        return this.cliente;
    }
 
    public List<ItemCatalogo> getItems() {
        return Collections.unmodifiableList(this.items);
    }
 
    public List<VoucherCredito> getVouchers() {
        return Collections.unmodifiableList(this.vouchers);
    }
 
    public MetodoTraslado getMetodoTraslado() {
        return this.metodoTraslado;
    }
 
    public void setMetodoTraslado(MetodoTraslado metodoTraslado) {
        this.metodoTraslado = metodoTraslado;
    }
    
}