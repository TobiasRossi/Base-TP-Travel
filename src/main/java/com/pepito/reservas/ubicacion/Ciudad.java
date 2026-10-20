package com.pepito.reservas.ubicacion;

/*
 * Value object: una ciudad de destino (ej.: Madrid, España).
 * Es un record: dos Ciudad con el mismo nombre y país son iguales (equals por valor),
 * así PorDisponibilidad puede comparar destinos sin depender de cómo se escriban los strings en cada lugar.
 * La Direccion completa (calle, número, ciudad) la necesita el 2.3; la búsqueda solo necesita la ciudad.
 */

public record Ciudad(String nombre, String pais) {
}
