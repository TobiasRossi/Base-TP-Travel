package com.pepito.reservas.servicio;

import com.pepito.reservas.catalogo.servicio.*;
import com.pepito.reservas.excepciones.DisponibilidadException;
import com.pepito.reservas.excepciones.ServiceExceptions;
import com.pepito.reservas.catalogo.servicio.atributos.AtributoDinamico;
import com.pepito.reservas.catalogo.servicio.atributos.TipoBooleano;
import com.pepito.reservas.destino.Ciudad;
import com.pepito.reservas.destino.Destino;
import com.pepito.reservas.destino.TipoGeografico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PaqueteTest {

    static final Double DELTA = 0.0001;

    Disponibilidad disponibilidad;
    Paquete paquete_aruba;
    Servicio servicio_vuelo;
    Servicio servicio_alojamiento;
    Servicio servicio_excursion;
    Servicio servicio_auto;

    LocalDate dia1 = LocalDate.of(2026, 1, 1);
    LocalDate dia2 = LocalDate.of(2026, 1, 2);
    LocalDate dia3 = LocalDate.of(2026, 1, 3);

    Destino europa;
    Destino italia;
    Destino francia;
    Ciudad roma;
    Ciudad lyon;
    Ciudad paris;

    // Unico lugar donde se crean los objetos: si cambia un constructor, se toca aca.
    private Destino continente(String nombre) {return new Destino(TipoGeografico.CONTINENTE, nombre);}
    private Destino pais(String nombre) {return new Destino(TipoGeografico.PAIS, nombre);}
    private Ciudad ciudad(String nombre) {return new Ciudad(TipoGeografico.CIUDAD, nombre);}


    @BeforeEach
    void setUp() {

        roma = ciudad("Roma");
        lyon = ciudad("Lyon");
        paris = ciudad("Paris");

        italia = pais("Italia");
        francia = pais("Francia");
        europa = continente("Europa");

        europa.agregarDestino(italia);
        europa.agregarDestino(francia);

        italia.agregarDestino(roma);
        francia.agregarDestino(lyon);
        francia.agregarDestino(paris);

        servicio_vuelo = new Servicio("Vuelo a Aruba", 0.1, new Disponibilidad(),
                "123VUELO", "QATAR AIRWAYS", Categoria.VUELO, 123.0,roma);

        servicio_alojamiento = new Servicio("Hotel en Aruba", 0.15, new Disponibilidad(),
                "123HOTEL", "AIRBNB", Categoria.ALOJAMIENTO, 32.5, lyon);

        servicio_auto = new Servicio("Alquiler de auto en Yahib", 0.00, new Disponibilidad(),
                "123AUTO", "CAMEL TIRE", Categoria.ALQUILER_AUTO, 12.2, italia);

        servicio_excursion = new Servicio("Excursion a tumbas de Sazagrev", 0.2, new Disponibilidad(),
                "123TEMPLO", "WISE GUY TRAVEL AGENCY", Categoria.EXCURSION, 5.6, europa);

        disponibilidad = new Disponibilidad();
        paquete_aruba = new Paquete("Pack Escapada Aruba", 0.1, "Paquete de prueba", disponibilidad,paris);
    }

    // Servicio invalido: tiene un atributo dinamico cuyo valor no corresponde al tipo declarado
    private Servicio servicioInvalido() {
        Servicio s = new Servicio("Servicio invalido", 0.0, new Disponibilidad(),
                "999INV", "PROVEEDOR", Categoria.EXCURSION, 10.0,paris);
        s.agregarAtributo(new AtributoDinamico("Apto Mascotas", true, new TipoBooleano(), 4.5));
        return s;
    }

    // ---------- Creacion ----------

    @Test
    void paquete_con_error_creacion_descuento_mayor_a_1() {
        assertThrowsExactly(ServiceExceptions.ErrorCreacionPaquete.class,
                () -> new Paquete("Pack Falso", 1.2, "Paquete invalido", new Disponibilidad(), europa));
    }

    @Test
    void paquete_con_error_creacion_descuento_negativo() {
        assertThrowsExactly(ServiceExceptions.ErrorCreacionPaquete.class,
                () -> new Paquete("Pack Falso", -0.1, "Paquete invalido", new Disponibilidad(), europa));
    }

    @Test
    void paquete_acepta_descuentos_limite_0_y_1() {
        Paquete sinDescuento = new Paquete("Test", 0.0, "Sin descuento", new Disponibilidad(), lyon);
        Paquete descuentoTotal = new Paquete("Test", 1.0, "Descuento total", new Disponibilidad(), italia);

        sinDescuento.agregarItem(servicio_auto);
        descuentoTotal.agregarItem(servicio_auto);

        assertEquals(12.2, sinDescuento.precioFinal(), DELTA);
        assertEquals(0.0, descuentoTotal.precioFinal(), DELTA);
    }

    // ---------- Precios ----------

    @Test
    void paquete_vacio_tiene_precio_base_cero() {
        assertEquals(0.0, paquete_aruba.precioBase(), DELTA);
    }

    @Test
    void paquete_precio_base_es_la_suma_de_los_precios_finales_de_sus_servicios() {
        paquete_aruba.agregarItem(servicio_vuelo);       // 123.0 * 0.90 = 110.7
        paquete_aruba.agregarItem(servicio_alojamiento); //  32.5 * 0.85 = 27.625

        assertEquals(138.325, paquete_aruba.precioBase(), DELTA);
    }

    @Test
    void paquete_posee_descuento_directo() {
        assertEquals(0.9, paquete_aruba.descuento(), DELTA);
    }

    @Test
    void paquete_devuelve_precio_final_con_descuento_correcto() {
        paquete_aruba.agregarItem(servicio_vuelo);
        paquete_aruba.agregarItem(servicio_alojamiento);

        assertEquals(124.4925, paquete_aruba.precioFinal(), DELTA);
    }

    @Test
    void paquete_vacio_tiene_precio_final_cero() {
        assertEquals(0.0, paquete_aruba.precioFinal(), DELTA);
    }

    @Test
    void paquete_calcula_precio_segun_ejemplo_del_enunciado() {
        Servicio hotel = new Servicio("Noche de Hotel Central", 0.0, new Disponibilidad(), "HC1", "HOTELES SA", Categoria.ALOJAMIENTO, 15000.0, europa);
        Servicio museo = new Servicio("Excursion Guiada Museo", 0.0, new Disponibilidad(), "EM1", "MUSEOS SA", Categoria.EXCURSION, 4000.0, europa);
        Servicio traslado = new Servicio("Traslado Aeropuerto-Hotel", 0.0, new Disponibilidad(), "TR1", "TRASLADOS SA", Categoria.ALQUILER_AUTO, 2000.0, europa);
        Paquete escapadaUrbana = new Paquete("Pack Escapada Urbana", 0.1, "Escapada urbana", new Disponibilidad(), europa);

        escapadaUrbana.agregarItem(hotel);
        escapadaUrbana.agregarItem(museo);
        escapadaUrbana.agregarItem(traslado);

        assertEquals(21000.0, escapadaUrbana.precioBase(), DELTA);
        assertEquals(18900.0, escapadaUrbana.precioFinal(), DELTA);
    }

    @Test
    void paquete_anidado_calcula_precio_final_de_forma_recursiva() {

        Paquete paqueteInterno = new Paquete("Pack Interno", 0.1, "Paquete interno", new Disponibilidad(), europa);

        paqueteInterno.disponibilidad().agregarCupoMasivo(new Periodo(dia1,dia3),5);
        servicio_alojamiento.disponibilidad().agregarCupoMasivo(new Periodo(dia1,dia1),5);
        servicio_excursion.disponibilidad().agregarCupoMasivo(new Periodo(dia1,dia1),5);

        paqueteInterno.agregarItem(servicio_alojamiento); // 27.625
        paqueteInterno.agregarItem(servicio_excursion);   //  4.48
        // interno: (27.625 + 4.48) * 0.9 = 28.8945

        Paquete paqueteExterno = new Paquete("Pack Externo", 0.2, "Paquete externo", new Disponibilidad(), europa);
        paqueteExterno.agregarItem(paqueteInterno);
        paqueteExterno.agregarItem(servicio_vuelo);       // 110.7
        // externo: (28.8945 + 110.7) * 0.8 = 111.6756

        assertEquals(28.8945, paqueteInterno.precioFinal(), DELTA);
        assertEquals(139.5945, paqueteExterno.precioBase(), DELTA);
        assertEquals(111.6756, paqueteExterno.precioFinal(), DELTA);
    }

    // ---------- Validacion ----------

    @Test
    void paquete_con_servicios_validos_es_valido() {

        paquete_aruba.disponibilidad().agregarCupoMasivo(new Periodo(dia1,dia3),5);
        servicio_vuelo.disponibilidad().agregarCupoMasivo(new Periodo(dia1,dia1),5);
        servicio_alojamiento.disponibilidad().agregarCupoMasivo(new Periodo(dia2,dia2),5);

        paquete_aruba.agregarItem(servicio_vuelo);
        paquete_aruba.agregarItem(servicio_alojamiento);

        assertTrue(paquete_aruba.validate());
    }

    @Test
    void paquete_vacio_es_valido() {
        paquete_aruba.disponibilidad().agregarCupoMasivo(new Periodo(dia1,dia1),5);
        assertTrue(paquete_aruba.validate());
    }

    @Test
    void paquete_sin_nombre_es_invalido() {
        Paquete paqueteInvalido = new Paquete(null, 0.1, "Paquete sin nombre", new Disponibilidad(), europa);

        assertFalse(paqueteInvalido.validate());
    }

    @Test
    void paquete_sin_descripcion_es_invalido() {
        Paquete paqueteInvalido = new Paquete("Pack sin descripcion", 0.1, null, new Disponibilidad(), europa);

        assertFalse(paqueteInvalido.validate());
    }

    @Test
    void paquete_es_invalido_si_un_servicio_deja_de_ser_valido() {

        paquete_aruba.disponibilidad().agregarCupoMasivo(new Periodo(dia1,dia3),5);
        servicio_vuelo.disponibilidad().agregarCupoMasivo(new Periodo(dia1,dia1),5);

        paquete_aruba.agregarItem(servicio_vuelo);
        assertTrue(paquete_aruba.validate());

        servicio_vuelo.agregarAtributo(new AtributoDinamico("Apto Mascotas", true, new TipoBooleano(), 4.5));

        assertFalse(paquete_aruba.validate());
    }

    @Test
    void paquete_es_invalido_si_un_paquete_anidado_deja_de_ser_valido() {
        Paquete paqueteInterno = new Paquete("Pack Interno", 0.1, "Paquete interno", new Disponibilidad(), europa);

        paqueteInterno.disponibilidad().agregarCupoMasivo(new Periodo(dia1,dia3),5);
        paquete_aruba.disponibilidad().agregarCupoMasivo(new Periodo(dia1,dia3),5);
        servicio_auto.disponibilidad().agregarCupoMasivo(new Periodo(dia1,dia2),5);

        paqueteInterno.agregarItem(servicio_auto);

        paquete_aruba.agregarItem(paqueteInterno);
        assertTrue(paquete_aruba.validate());

        paqueteInterno.agregarItem(servicio_excursion);
        servicio_excursion.agregarAtributo(new AtributoDinamico("Apto Mascotas", true, new TipoBooleano(), 4.5));

        assertFalse(paquete_aruba.validate());
    }

    // ---------- Items ----------

    @Test
    void paquete_nuevo_no_tiene_items() {
        assertEquals(0, paquete_aruba.cantidadItems());
    }

    @Test
    void paquete_agrega_servicio_valido_incrementa_cantidad() {
        paquete_aruba.agregarItem(servicio_vuelo);
        paquete_aruba.agregarItem(servicio_alojamiento);

        assertEquals(2, paquete_aruba.cantidadItems());
    }

    @Test
    void paquete_agrega_paquete_valido_incrementa_cantidad() {
        Paquete paqueteInterno = new Paquete("Pack Interno", 0.1, "Paquete interno", new Disponibilidad(), europa);

        paqueteInterno.disponibilidad().agregarCupoMasivo(new Periodo(dia1,dia3),5);
        servicio_auto.disponibilidad().agregarCupoMasivo(new Periodo(dia1,dia3),5);
        paquete_aruba.disponibilidad().agregarCupoMasivo(new Periodo(dia1,dia3),5);

        paqueteInterno.agregarItem(servicio_auto);

        paquete_aruba.agregarItem(paqueteInterno);

        assertEquals(1, paquete_aruba.cantidadItems());
    }

    @Test
    void paquete_agrega_servicio_invalido_tira_excepcion() {
        assertThrowsExactly(ServiceExceptions.MetodoInvalido.class,
                () -> paquete_aruba.agregarItem(servicioInvalido()));
        assertEquals(0, paquete_aruba.cantidadItems());
    }

    @Test
    void paquete_agrega_paquete_invalido_tira_excepcion() {
        Paquete paqueteInvalido = new Paquete("Pack sin descripcion", 0.1, null, new Disponibilidad(), europa);

        assertThrowsExactly(ServiceExceptions.MetodoInvalido.class,
                () -> paquete_aruba.agregarItem(paqueteInvalido));
        assertEquals(0, paquete_aruba.cantidadItems());
    }

    @Test
    void paquete_remueve_item_existente_decrementa_cantidad() {
        paquete_aruba.agregarItem(servicio_vuelo);
        paquete_aruba.agregarItem(servicio_alojamiento);

        paquete_aruba.removerItem(servicio_vuelo);

        assertEquals(1, paquete_aruba.cantidadItems());
        assertEquals(27.625, paquete_aruba.precioBase(), DELTA);
    }

    @Test
    void paquete_remueve_item_inexistente_no_modifica_cantidad() {
        paquete_aruba.agregarItem(servicio_vuelo);

        paquete_aruba.removerItem(servicio_alojamiento);

        assertEquals(1, paquete_aruba.cantidadItems());
    }

    // ---------- Disponibilidad (delegacion) ----------

    @Test
    void paquete_sin_cupo_cargado_no_esta_disponible_en_una_fecha() {
        assertFalse(paquete_aruba.disponible(dia1, 1));
    }

    @Test
    void paquete_con_cupo_esta_disponible_en_una_fecha_hasta_el_limite() {
        disponibilidad.agregarCupo(dia1, 5);

        assertTrue(paquete_aruba.disponible(dia1, 5));
        assertFalse(paquete_aruba.disponible(dia1, 6));
    }

    @Test
    void paquete_con_cupo_en_todo_el_periodo_esta_disponible() {
        disponibilidad.agregarCupoMasivo(new Periodo(dia1, dia3), 4);

        assertTrue(paquete_aruba.disponible(new Periodo(dia1, dia3), 4));
        assertFalse(paquete_aruba.disponible(new Periodo(dia1, dia3), 5));
    }

    @Test
    void paquete_no_esta_disponible_en_un_periodo_si_falta_cupo_un_dia() {
        disponibilidad.agregarCupo(dia1, 4);
        disponibilidad.agregarCupo(dia3, 4); // dia2 sin cupo

        assertFalse(paquete_aruba.disponible(new Periodo(dia1, dia3), 1));
    }

    @Test
    void paquete_reservar_fecha_descuenta_cupo() {
        disponibilidad.agregarCupo(dia1, 5);

        paquete_aruba.reservar(dia1, 2);

        assertEquals(3, disponibilidad.cupo(dia1));
    }

    @Test
    void paquete_reservar_fecha_sin_cupo_tira_excepcion() {
        disponibilidad.agregarCupo(dia1, 1);

        assertThrowsExactly(DisponibilidadException.FechaSinDisponibilidad.class,
                () -> paquete_aruba.reservar(dia1, 2));
        assertEquals(1, disponibilidad.cupo(dia1));
    }

    @Test
    void paquete_reservar_periodo_descuenta_cupo_de_cada_dia() {
        disponibilidad.agregarCupoMasivo(new Periodo(dia1, dia3), 5);

        paquete_aruba.reservar(new Periodo(dia1, dia3), 2);

        assertEquals(3, disponibilidad.cupo(dia1));
        assertEquals(3, disponibilidad.cupo(dia2));
        assertEquals(3, disponibilidad.cupo(dia3));
    }

    @Test
    void paquete_reservar_periodo_sin_cupo_tira_excepcion_y_no_modifica_nada() {
        disponibilidad.agregarCupo(dia1, 5);
        disponibilidad.agregarCupo(dia2, 1); // no alcanza para 2
        disponibilidad.agregarCupo(dia3, 5);

        assertThrowsExactly(DisponibilidadException.PeriodoSinDisponibilidad.class,
                () -> paquete_aruba.reservar(new Periodo(dia1, dia3), 2));
        assertEquals(5, disponibilidad.cupo(dia1));
        assertEquals(1, disponibilidad.cupo(dia2));
        assertEquals(5, disponibilidad.cupo(dia3));
    }
}