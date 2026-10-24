package com.pepito.reservas.servicio;

import com.pepito.reservas.catalogo.servicio.Categoria;
import com.pepito.reservas.catalogo.servicio.Disponibilidad;
import com.pepito.reservas.catalogo.servicio.Periodo;
import com.pepito.reservas.catalogo.servicio.Servicio;
import com.pepito.reservas.catalogo.servicio.atributos.*;
import com.pepito.reservas.excepciones.DisponibilidadException;
import com.pepito.reservas.excepciones.ServiceExceptions;
import com.pepito.reservas.destino.Ciudad;
import com.pepito.reservas.destino.Destino;
import com.pepito.reservas.destino.TipoGeografico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ServicioTest {

    Servicio servicio_vuelo;
    Servicio servicio_vuelo_2;
    Servicio servicio_alojamiento;
    Servicio servicio_excursion;
    Servicio servicio_auto;
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

    Double DELTA = 0.0001;
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


        servicio_vuelo = new Servicio(
                "Vuelo a Aruba",
                0.1,
                new Disponibilidad(),
                "123VUELO",
                "QATAR AIRWAYS",
                Categoria.VUELO,
                123.0, europa
        );

        servicio_alojamiento = new Servicio(
                "Hotel en Aruba",
                0.15,
                new Disponibilidad(),
                "123HOTEL",
                "AIRBNB",
                Categoria.ALOJAMIENTO,
                32.5, paris
        );

        servicio_vuelo_2 = new Servicio(
                "Traslado a Yahib",
                0.05,
                new Disponibilidad(),
                "234VUELO",
                "QATAR MINI-AIRWAYS",
                Categoria.VUELO,
                50.34, paris
        );

        servicio_auto = new Servicio(
                "Alquiler de auto en Yahib",
                0.00,
                new Disponibilidad(),
                "123AUTO",
                "CAMEL TIRE",
                Categoria.ALQUILER_AUTO,
                12.2, paris
        );

        servicio_excursion = new Servicio(
                "Excursion a tumbas de Sazagrev",
                0.2,
                new Disponibilidad(),
                "123TEMPLO",
                "WISE GUY TRAVEL AGENCY",
                Categoria.EXCURSION,
                5.6, paris
        );

    }

    @Test
    void servicio_devuelve_precio_base(){
        assertEquals(5.6,this.servicio_excursion.precioBase(),DELTA);
    }

    @Test
    void servicio_devuelve_precio_final_con_descuento_correcto(){
        assertEquals(110.7,this.servicio_vuelo.precioFinal(),DELTA);
    }

    @Test
    void servicio_posee_descuento_directo(){
        assertEquals(0.85,this.servicio_alojamiento.descuento(),DELTA);
    }

    @Test
    void servicio_posee_atributos_validos() {
        assertTrue(this.servicio_vuelo_2.validate());
    }

    @Test
    void servicio_posee_atributo_fijo_precioBase_invalido() {

        Servicio servicio_excursion_invalid = new Servicio(
                "Excursion a tumbas de Sazagrev Falso",
                0.2,
                new Disponibilidad(),
                "321TEMPLO",
                "UNWISE GUY TRAVEL AGENCY",
                Categoria.EXCURSION,
                0.0, paris
        );

        assertFalse(servicio_excursion_invalid.validate());
    }

    @Test
    void servicio_posee_atributo_fijo_codigoReserva_invalido() {

        Servicio servicio_excursion_invalid = new Servicio(
                "Excursion a tumbas de Sazagrev Falso",
                0.2,
                new Disponibilidad(),
                "",
                "UNWISE GUY TRAVEL AGENCY",
                Categoria.EXCURSION,
                1.0, paris
        );

        assertFalse(servicio_excursion_invalid.validate());
    }

    @Test
    void servicio_posee_atributo_fijo_nombre_invalido() {

        Servicio servicio_excursion_invalid = new Servicio(
                "",
                0.2,
                new Disponibilidad(),
                "TEST",
                "UNWISE GUY TRAVEL AGENCY",
                Categoria.EXCURSION,
                1.0, paris
        );

        assertFalse(servicio_excursion_invalid.validate());
    }

    @Test
    void servicio_posee_atributo_fijo_proveedor_invalido() {

        Servicio servicio_excursion_invalid = new Servicio(
                "Excursion a tumbas de Sazagrev Falso",
                0.2,
                new Disponibilidad(),
                "TEST",
                "",
                Categoria.EXCURSION,
                1.0, paris
        );

        assertFalse(servicio_excursion_invalid.validate());
    }

    @Test
    void servicio_con_error_creacion_precio_negativo() {

        assertThrowsExactly(ServiceExceptions.ErrorCreacionServicio.class, () -> {
            Servicio servicio_excursion_invalid = new Servicio(
                    "Excursion a tumbas de Sazagrev Falso",
                    0.2,
                    new Disponibilidad(),
                    "321TEMPLO",
                    "UNWISE GUY TRAVEL AGENCY",
                    Categoria.EXCURSION,
                    -100.0, paris
            );
        });
    }
    @Test
    void servicio_con_error_creacion_descuento_mayor_a_1() {

        assertThrowsExactly(ServiceExceptions.ErrorCreacionServicio.class, () -> {
            Servicio servicio_excursion_invalid = new Servicio(
                    "Excursion a tumbas de Sazagrev Falso",
                    1.2,
                    new Disponibilidad(),
                    "321TEMPLO",
                    "UNWISE GUY TRAVEL AGENCY",
                    Categoria.EXCURSION,
                    100.0, paris
            );
        });
    }

    @Test
    void servicio_agrega_atributo_dinamico_valido() {
        AtributoDinamico puntuacionEstrella = new AtributoDinamico("Puntuación",true,new TipoNumerico(),4.5);
        AtributoDinamico mejorEmpleado = new AtributoDinamico("Mejor Empleado",true,new TipoTexto(),"Gerardo Rivas");

        assertEquals(0,this.servicio_vuelo_2.cantidadAtributos());

        this.servicio_vuelo_2.agregarAtributo(puntuacionEstrella);
        this.servicio_vuelo_2.agregarAtributo(mejorEmpleado);

        assertTrue(this.servicio_vuelo_2.validate());

        assertEquals(2,this.servicio_vuelo_2.cantidadAtributos());
    }

    @Test
    void servicio_agrega_atributo_dinamico_invalido_tipo_erroneo() {
        AtributoDinamico mascotasHabilitadas = new AtributoDinamico("Apto Mascotas",true,new TipoBooleano(),4.5);

        assertEquals(0,this.servicio_vuelo_2.cantidadAtributos());

        this.servicio_vuelo_2.agregarAtributo(mascotasHabilitadas);

        assertFalse(this.servicio_vuelo_2.validate());
        assertEquals(1,this.servicio_vuelo_2.cantidadAtributos());
    }
    @Test
    void servicio_agrega_atributo_dinamico_invalido_valor_nulo() {
        AtributoDinamico mascotasHabilitadas = new AtributoDinamico("Apto Mascotas",true,new TipoFecha(),null);

        assertEquals(0,this.servicio_vuelo_2.cantidadAtributos());

        this.servicio_vuelo_2.agregarAtributo(mascotasHabilitadas);

        assertFalse(this.servicio_vuelo_2.validate());
        assertEquals(1,this.servicio_vuelo_2.cantidadAtributos());
    }

    @Test
    void servicio_elimina_atributo_dinamico_existente_decrementa_cantidades() {
        AtributoDinamico inicioActividad = new AtributoDinamico("Fecha Inicio Actividad",true,new TipoFecha(), LocalDate.of(2026, 2, 1));
        AtributoDinamico puntuacionEstrella = new AtributoDinamico("Puntuación",true,new TipoNumerico(),4.5);

        this.servicio_vuelo_2.agregarAtributo(inicioActividad);
        this.servicio_vuelo_2.agregarAtributo(puntuacionEstrella);
        assertEquals(2,this.servicio_vuelo_2.cantidadAtributos());

        this.servicio_vuelo_2.eliminarAtributo(inicioActividad);
        assertEquals(1,this.servicio_vuelo_2.cantidadAtributos());
    }

    @Test
    void servicio_elimina_atributo_dinamico_inexistente_tira_excepcion() {
        AtributoDinamico inicioActividad = new AtributoDinamico("Fecha Inicio Actividad",true,new TipoFecha(), LocalDate.of(2026, 2, 1));
        AtributoDinamico puntuacionEstrella = new AtributoDinamico("Puntuación",true,new TipoNumerico(),4.5);

        this.servicio_vuelo_2.agregarAtributo(puntuacionEstrella);
        assertEquals(1,this.servicio_vuelo_2.cantidadAtributos());

        assertThrowsExactly(ServiceExceptions.MetodoInvalido.class, () -> {
            this.servicio_vuelo_2.eliminarAtributo(inicioActividad);
        });


    }

    @Test
    void servicio_con_error_creacion_descuento_negativo() {

        assertThrowsExactly(ServiceExceptions.ErrorCreacionServicio.class, () -> {
            new Servicio(
                    "Excursion a tumbas de Sazagrev Falso",
                    -0.1,
                    new Disponibilidad(),
                    "321TEMPLO",
                    "UNWISE GUY TRAVEL AGENCY",
                    Categoria.EXCURSION,
                    100.0, paris
            );
        });
    }

    @Test
    void servicio_acepta_descuentos_limite_0_y_1() {
        Servicio sinDescuento = new Servicio("Test", 0.0, new Disponibilidad(), "T1", "PROV", Categoria.EXCURSION, 10.0, paris);
        Servicio descuentoTotal = new Servicio("Test", 1.0, new Disponibilidad(), "T2", "PROV", Categoria.EXCURSION, 10.0, paris);

        assertEquals(10.0, sinDescuento.precioFinal(), DELTA);
        assertEquals(0.0, descuentoTotal.precioFinal(), DELTA);
    }

    @Test
    void servicio_creado_con_constructor_completo_devuelve_precios() {
        Servicio servicio = new Servicio(
                "Vuelo a Aruba",
                0.1,
                new Disponibilidad(),
                "123VUELO",
                "QATAR AIRWAYS",
                Categoria.VUELO,
                123.0, paris
        );

        assertEquals(123.0, servicio.precioBase(), DELTA);
        assertEquals(110.7, servicio.precioFinal(), DELTA);
    }

    // Inicializacion de Periodos


    @Test
    void servicio_cupo_disponible_se_puede_consultar() {
        Periodo periodo = new Periodo(
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 2, 5)
        );


        servicio_vuelo.disponibilidad().agregarCupoMasivo(periodo,10);
        assertTrue(this.servicio_vuelo.disponibilidad().periodoDisponible(periodo,5 ));

    }


    @Test
    void servicio_cupo_menor_que_devuelve_false() {
        Periodo periodo = new Periodo(
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 2, 5)
        );
        LocalDate fechaConsulta = LocalDate.of(2026, 2, 2);


        servicio_vuelo.disponibilidad().agregarCupoMasivo(periodo,10);
        assertFalse(this.servicio_vuelo.disponibilidad().periodoDisponible(periodo,15 ));
        assertEquals(10,servicio_vuelo.disponibilidad().cupo(fechaConsulta));

    }

    @Test
    void servicio_reservado_sin_cupo_lanza_excepcion() {
        Periodo periodo = new Periodo(
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 2, 5)
        );

        servicio_vuelo.disponibilidad().agregarCupoMasivo(periodo,10);

        assertThrows(
                DisponibilidadException.PeriodoSinDisponibilidad.class,
                () -> servicio_vuelo.disponibilidad().bloquearPeriodo(periodo, 11)
        );

    }

    @Test
    void servicio_cupo_se_reserva_baja_cupo_disponible() {
        Periodo periodo = new Periodo(
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 2, 5)
        );
        LocalDate fechaConsulta = LocalDate.of(2026, 2, 1);

        servicio_vuelo.disponibilidad().agregarCupoMasivo(periodo,10);
        assertEquals(10,servicio_vuelo.disponibilidad().cupo(fechaConsulta));

        servicio_vuelo.disponibilidad().bloquearPeriodo(periodo, 5);

        assertEquals(5,servicio_vuelo.disponibilidad().cupo(fechaConsulta));

    }

    @Test
    void servicio_cupo_se_libera_sube_cupo_disponible() {
        Periodo periodo = new Periodo(
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 2, 5)
        );
        LocalDate fechaConsulta = LocalDate.of(2026, 2, 1);

        servicio_vuelo.disponibilidad().agregarCupoMasivo(periodo,10);
        assertEquals(10,servicio_vuelo.disponibilidad().cupo(fechaConsulta));

        servicio_vuelo.disponibilidad().liberarPeriodo(periodo, 5);

        assertEquals(15,servicio_vuelo.disponibilidad().cupo(fechaConsulta));

    }

    @Test
    void servicio_cupo_se_libera_fecha_no_existente_lanza_excepcion() {
        Periodo periodo = new Periodo(
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 2, 5)
        );
        Periodo periodo2 = new Periodo(
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 5)
        );
        LocalDate fechaConsulta = LocalDate.of(2026, 2, 1);

        servicio_vuelo.disponibilidad().agregarCupoMasivo(periodo,10);
        assertEquals(10,servicio_vuelo.disponibilidad().cupo(fechaConsulta));

        assertThrowsExactly(
                DisponibilidadException.PeriodoSinDisponibilidad.class,
                () -> servicio_vuelo.disponibilidad().liberarPeriodo(periodo2, 5)
                );
        assertEquals(10,servicio_vuelo.disponibilidad().cupo(fechaConsulta));

    }

    @Test
    void servicio_cupo_con_una_fecha_sin_cupo_lanza_excepcion() {
        Periodo periodo = new Periodo(
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 2, 5)
        );

        LocalDate fechaConsulta = LocalDate.of(2026, 2, 2);

        servicio_vuelo.disponibilidad().agregarCupoMasivo(periodo,10);

        assertEquals(10,servicio_vuelo.disponibilidad().cupo(fechaConsulta));

        servicio_vuelo.disponibilidad().bloquearFecha(fechaConsulta,10);

        assertThrowsExactly(
                DisponibilidadException.FechaSinDisponibilidad.class,
                () -> servicio_vuelo.disponibilidad().bloquearFecha(fechaConsulta, 11)
                );
        assertEquals(0,servicio_vuelo.disponibilidad().cupo(fechaConsulta));
        assertEquals(10,servicio_vuelo.disponibilidad().cupo(fechaConsulta.minusDays(1)));

    }

    Periodo periodoFebrero = new Periodo(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 5));
    LocalDate dia = LocalDate.of(2026, 2, 2);

    @Test
    void servicio_disponible_por_fecha_respeta_el_cupo() {
        servicio_vuelo.disponibilidad().agregarCupo(dia, 10);

        assertTrue(servicio_vuelo.disponible(dia, 10));
        assertFalse(servicio_vuelo.disponible(dia, 11));
    }

    @Test
    void servicio_disponible_por_periodo_respeta_el_cupo() {
        servicio_vuelo.disponibilidad().agregarCupoMasivo(periodoFebrero, 10);

        assertTrue(servicio_vuelo.disponible(periodoFebrero, 10));
        assertFalse(servicio_vuelo.disponible(periodoFebrero, 11));
    }

    @Test
    void servicio_reservar_fecha_descuenta_cupo() {
        servicio_vuelo.disponibilidad().agregarCupo(dia, 10);

        servicio_vuelo.reservar(dia, 4);

        assertEquals(6, servicio_vuelo.disponibilidad().cupo(dia));
    }

    @Test
    void servicio_reservar_periodo_descuenta_cupo_de_cada_dia() {
        servicio_vuelo.disponibilidad().agregarCupoMasivo(periodoFebrero, 10);

        servicio_vuelo.reservar(periodoFebrero, 4);

        assertEquals(6, servicio_vuelo.disponibilidad().cupo(LocalDate.of(2026, 2, 1)));
        assertEquals(6, servicio_vuelo.disponibilidad().cupo(LocalDate.of(2026, 2, 5)));
    }
}