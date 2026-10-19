package com.pepito.reservas.procesadorpago;

/*
 * Contrato que la Reserva usa para cobrar. La Reserva solo pide "cobrá este monto":
 * validar los datos, reservar fondos, ejecutar y notificar es responsabilidad del medio de pago.
 * El módulo 2.4 lo implementa con su ProcesadorPago abstracto (Template Method).
 */

public interface MetodoPago {

    /* Devuelve el comprobante del pago. Lanza PagoRechazadoException si el pago no se pudo completar. */
    ComprobantePago procesarPago(double monto);

}
