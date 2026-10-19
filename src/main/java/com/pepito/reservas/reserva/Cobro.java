package com.pepito.reservas.reserva;

import com.pepito.reservas.procesadorpago.ComprobantePago;

import java.time.LocalDateTime;

/*
 * Lo que se congela al confirmar/pagar: montos cobrados, comprobante y fecha.
 * Los reembolsos, la factura y los reportes usan esto y no los precios actuales del catálogo.
 */

public record Cobro(double montoServicios, double montoTasas, ComprobantePago comprobante, LocalDateTime fecha) {

    public double total() {
        return this.montoServicios + this.montoTasas;
    }
}
