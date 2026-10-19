package com.pepito.reservas.reserva;
 
/*
 * Comprobante de crédito/reembolso que se registra en la reserva al cancelar.
 */
public class VoucherCredito {
 
    private final double monto;
    private final String motivo;
 
    public VoucherCredito(double monto, String motivo) {
        this.monto = monto;
        this.motivo = motivo;
    }
 
    public double getMonto() {
        return monto;
    }
 
    public String getMotivo() {
        return motivo;
    }
}