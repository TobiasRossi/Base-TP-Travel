package com.pepito.reservas.reserva;
 
import com.pepito.reservas.catalogo.ItemCatalogo;
import com.pepito.reservas.catalogo.SinCupoException;
import com.pepito.reservas.cliente.Cliente;
import com.pepito.reservas.procesadorpago.ComprobantePago;
import com.pepito.reservas.procesadorpago.MetodoPago;
import com.pepito.reservas.procesadorpago.PagoRechazadoException;
import com.pepito.reservas.reserva.suscriptor.ReservaObserver;
import com.pepito.reservas.traslado.MetodoTraslado;
import com.pepito.reservas.traslado.SinTraslado;
 
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/*
 * Contexto del Patrón State y Sujeto del Patrón Observer.
 * No decide operaciones válidas, las delega al Estado vigente.
 * Los efectos que ejecutan los Estados (cambiarEstado, cobrar, bloquearCupos, ...) son package-private:
 * desde afuera del paquete la única forma de modificar una Reserva son las operaciones de OperacionesReserva.
 */

public class Reserva implements OperacionesReserva {

	private final Cliente cliente;
    private final int cantidadPasajeros;
    private final Clock reloj;
    private final List<ItemCatalogo> items;
    private final List<VoucherCredito> vouchers;
    private final List<ReservaObserver> observadores;
    private EstadoReserva estado;
    private MetodoTraslado metodoTraslado;
    private Optional<Cobro> cobro;

    public Reserva(Cliente cliente, int cantidadPasajeros){
        this(cliente, cantidadPasajeros, Clock.systemDefaultZone());
    }

    /* El reloj se inyecta para poder fijar la fecha del cobro en los tests. */
    public Reserva(Cliente cliente, int cantidadPasajeros, Clock reloj){
        if (cantidadPasajeros < 1) {
            throw new CantidadPasajerosInvalidaException("Una reserva necesita al menos un pasajero (recibió " + cantidadPasajeros + ").");
        }
    	this.cliente = cliente;
        this.cantidadPasajeros = cantidadPasajeros;
        this.reloj = reloj;
        this.items = new ArrayList<>();
        this.vouchers = new ArrayList<>();
        this.observadores = new ArrayList<>();
        this.estado = new SolicitudPendiente();
        this.metodoTraslado = new SinTraslado();
        this.cobro = Optional.empty();
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

    void cambiarEstado(EstadoReserva nuevoEstado) {
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
    
    /* El Estado decide CUÁNDO se puede agregar; agregar incluye no aceptar ítems que no pasan su validación (2.1). */
    void efectivizarAgregadoDeItem(ItemCatalogo item) {
        if (!item.validate()) {
            throw new ItemInvalidoException("El ítem no pasa su validación y no se puede agregar a la reserva.");
        }
        this.items.add(item);
    }
 
    /* Quitar un ítem que no está es un error del que llama: se avisa en vez de ignorarlo en silencio. */
    void efectivizarEliminacionDeItem(ItemCatalogo item) {
        if (!this.items.remove(item)) {
            throw new ItemNoIncluidoException("El ítem no forma parte de la reserva.");
        }
    }

    void efectivizarEleccionDeTraslado(MetodoTraslado metodoTraslado) {
        this.metodoTraslado = metodoTraslado;
    }

    /* Un ítem de precio 0 es válido; lo que no se puede confirmar es una reserva sin ítems. */
    void verificarQueTengaItems() {
        if (this.items.isEmpty()) {
            throw new ReservaVaciaException("No se puede confirmar una reserva sin ítems.");
        }
    }

    /* Lanza TrasladoNoDisponibleException si el traslado elegido no tiene disponibilidad. */
    void verificarDisponibilidadTraslado() {
        this.metodoTraslado.verificarDisponibilidad(this);
    }

    /*
     * Se bloquea una unidad por pasajero en cada ítem.
     * Todo o nada: si un ítem no tiene Cupo, se liberan los que ya se bloquearon y se relanza.
     * Cada ítem sabe bloquear su propio Cupo (un Paquete lo propaga a sus partes).
     */
    void bloquearCupos() {
        List<ItemCatalogo> bloqueados = new ArrayList<>();
        try {
            for (ItemCatalogo item : this.items) {
                item.bloquearCupo(this.cantidadPasajeros);
                bloqueados.add(item);
            }
        } catch (SinCupoException e) {
            bloqueados.forEach(item -> item.liberarCupo(this.cantidadPasajeros));
            throw e;
        }
    }
 
    void liberarCupos() {
        this.items.forEach(item -> item.liberarCupo(this.cantidadPasajeros));
    }

    /*
     * El medio de pago valida y procesa; la Reserva solo le pide el monto. Si el pago se rechaza, se liberan los Cupos.
     * Montos, comprobante y fecha se congelan en un Cobro: los reembolsos devuelven lo que el cliente efectivamente pagó.
     */
    void cobrar(MetodoPago metodoPago) {
        double servicios = this.getTotalServicios();
        double tasas = this.getTotalTasas();
        ComprobantePago comprobante;
        try {
            comprobante = metodoPago.procesarPago(servicios + tasas);
        } catch (PagoRechazadoException e) {
            this.liberarCupos();
            throw e;
        }
        this.cobro = Optional.of(new Cobro(servicios, tasas, comprobante, LocalDateTime.now(this.reloj)));
    }

    void generarVoucherReembolsoTotal() {
        double monto = this.cobroRealizado().total();
        this.registrarVoucher(new VoucherCredito(monto, "Cancelación en check-in: reembolso de servicios y tasas"));
    }
 
    void generarVoucherReembolsoParcial() {
        double monto = this.getTotalServiciosNoDisfrutados();
        this.registrarVoucher(new VoucherCredito(monto, "Cancelación en curso: reembolso de servicios no disfrutados"));
    }
 
    void registrarVoucher(VoucherCredito voucher) {
        this.vouchers.add(voucher);
    }

    /* Calculo de montos (actuales: lo que se cobraría si se confirma ahora). Cada pasajero paga una unidad de cada ítem. */
    
    public double getTotalServicios() {
        return this.items.stream().mapToDouble(ItemCatalogo::getPrecioFinal).sum() * this.cantidadPasajeros;
    }
 
    public double getTotalTasas() {
        return this.metodoTraslado.calcularCosto(this);
    }

    public double getTotal() {
        return this.getTotalServicios() + this.getTotalTasas();
    }

    /* Lo cobrado al confirmar (base de los reembolsos). Vacío mientras la reserva no se confirmó. */

    public Optional<Cobro> getCobro() {
        return this.cobro;
    }

    public double getTotalServiciosNoDisfrutados() {
        // Falta definir el criterio de "servicio disfrutado"
    	return this.cobroRealizado().montoServicios();
    }

    /* Los reembolsos solo se generan desde estados posteriores a confirmar, donde el Cobro siempre existe. */
    private Cobro cobroRealizado() {
        return this.cobro.orElseThrow(() -> new ReservaException("La reserva todavía no tiene un cobro registrado."));
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

    public int getCantidadPasajeros() {
        return this.cantidadPasajeros;
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
