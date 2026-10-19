package com.pepito.reservas.reserva;
 
import com.pepito.reservas.catalogo.ItemCatalogo;
import com.pepito.reservas.catalogo.SinCupoException;
import com.pepito.reservas.cliente.Cliente;
import com.pepito.reservas.procesadorpago.MetodoPago;
import com.pepito.reservas.procesadorpago.PagoRechazadoException;
import com.pepito.reservas.reserva.estado.EstadoReserva;
import com.pepito.reservas.reserva.estado.SolicitudPendiente;
import com.pepito.reservas.reserva.suscriptor.ReservaObserver;
import com.pepito.reservas.traslado.MetodoTraslado;
import com.pepito.reservas.traslado.SinTraslado;
 
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
    private double montoServiciosCobrado;
    private double montoTasasCobrado;

    public Reserva(Cliente cliente){
    	this.cliente = cliente;
        this.items = new ArrayList<>();
        this.vouchers = new ArrayList<>();
        this.observadores = new ArrayList<>();
        this.estado = new SolicitudPendiente();
        this.metodoTraslado = new SinTraslado();
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
    public void elegirTraslado(MetodoTraslado metodoTraslado) {
        this.estado.elegirTraslado(this, metodoTraslado);
    }
 
    @Override
    public void confirmar(MetodoPago metodoPago) {
        this.estado.confirmar(this, metodoPago);
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

    public void efectivizarEleccionDeTraslado(MetodoTraslado metodoTraslado) {
        this.metodoTraslado = metodoTraslado;
    }

    /*
     * Todo o nada: si un ítem no tiene Cupo, se liberan los que ya se bloquearon y se relanza.
     * Cada ítem sabe bloquear su propio Cupo (un Paquete lo propaga a sus partes).
     */
    public void bloquearCupos() {
        List<ItemCatalogo> bloqueados = new ArrayList<>();
        try {
            for (ItemCatalogo item : this.items) {
                item.bloquearCupo();
                bloqueados.add(item);
            }
        } catch (SinCupoException e) {
            bloqueados.forEach(ItemCatalogo::liberarCupo);
            throw e;
        }
    }
 
    public void liberarCupos() {
        this.items.forEach(ItemCatalogo::liberarCupo);
    }

    /*
     * El medio de pago valida y procesa; la Reserva solo le pide el monto. Si el pago se rechaza, se liberan los Cupos.
     * Los montos se congelan al cobrar: los reembolsos devuelven lo que el cliente efectivamente pagó.
     */
    public void cobrar(MetodoPago metodoPago) {
        double servicios = this.getTotalServicios();
        double tasas = this.getTotalTasas();
        try {
            metodoPago.procesarPago(servicios + tasas);
        } catch (PagoRechazadoException e) {
            this.liberarCupos();
            throw e;
        }
        this.montoServiciosCobrado = servicios;
        this.montoTasasCobrado = tasas;
    }

    public void generarVoucherReembolsoTotal() {
        double monto = this.montoServiciosCobrado + this.montoTasasCobrado;
        this.registrarVoucher(new VoucherCredito(monto, "Cancelación en check-in: reembolso de servicios y tasas"));
    }
 
    public void generarVoucherReembolsoParcial() {
        double monto = this.getTotalServiciosNoDisfrutados();
        this.registrarVoucher(new VoucherCredito(monto, "Cancelación en curso: reembolso de servicios no disfrutados"));
    }
 
    public void registrarVoucher(VoucherCredito voucher) {
        this.vouchers.add(voucher);
    }

    /* Calculo de montos (actuales: lo que se cobraría si se confirma ahora) */
    
    public double getTotalServicios() {
        return this.items.stream().mapToDouble(ItemCatalogo::getPrecioFinal).sum();
    }
 
    public double getTotalTasas() {
        return this.metodoTraslado.calcularCosto(this);
    }

    public double getTotal() {
        return this.getTotalServicios() + this.getTotalTasas();
    }

    /* Montos cobrados al confirmar (base de los reembolsos) */

    public double getMontoServiciosCobrado() {
        return this.montoServiciosCobrado;
    }

    public double getMontoTasasCobrado() {
        return this.montoTasasCobrado;
    }

    public double getTotalServiciosNoDisfrutados() {
        // Falta definir el criterio de "servicio disfrutado"
    	return this.montoServiciosCobrado;
    }

    /* Getters */
    
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
    
}
