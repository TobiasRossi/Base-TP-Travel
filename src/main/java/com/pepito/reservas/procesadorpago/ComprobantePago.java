package com.pepito.reservas.procesadorpago;

/*
 * Comprobante que genera el medio de pago en su paso "notificar resultado".
 * La Reserva lo guarda en su Cobro; lo pueden usar la factura (2.5) y los reportes (2.8).
 */

public class ComprobantePago {

    private final String codigoTransaccion;
    private final double monto;

    public ComprobantePago(String codigoTransaccion, double monto) {
        this.codigoTransaccion = codigoTransaccion;
        this.monto = monto;
    }

    public String getCodigoTransaccion() {
        return codigoTransaccion;
    }

    public double getMonto() {
        return monto;
    }
}
