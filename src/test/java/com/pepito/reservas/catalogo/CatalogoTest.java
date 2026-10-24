package com.pepito.reservas.catalogo;

import com.pepito.reservas.catalogo.busqueda.CriterioBusqueda;
import com.pepito.reservas.catalogo.servicio.Categoria;
import com.pepito.reservas.catalogo.servicio.Disponibilidad;
import com.pepito.reservas.catalogo.servicio.Periodo;
import com.pepito.reservas.catalogo.servicio.Servicio;
import com.pepito.reservas.catalogo.servicio.atributos.AtributoDinamico;
import com.pepito.reservas.catalogo.servicio.atributos.TipoBooleano;
import com.pepito.reservas.destino.Ciudad;
import com.pepito.reservas.destino.Destino;
import com.pepito.reservas.destino.TipoGeografico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CatalogoTest {

    LocalDate dia1 = LocalDate.of(2026, 1, 1);
    LocalDate dia2 = LocalDate.of(2026, 1, 2);
    LocalDate dia3 = LocalDate.of(2026, 1, 3);
    Periodo periodo = new Periodo(dia1, dia3);

    Catalogo catalogo;
    Servicio vuelo;       // 123.0
    Servicio alojamiento; //  32.5
    Servicio auto;        //  12.2

    CriterioBusqueda baratos = item -> item.precioBase() <= 40.0;
    CriterioBusqueda todos = item -> true;
    CriterioBusqueda ninguno = item -> false;

    Destino europa;
    Destino america;
    Destino italia;
    Destino francia;
    Ciudad roma;
    Ciudad lyon;
    Ciudad paris;

    // Unico lugar donde se crean los objetos: si cambia un constructor, se toca aca.
    private Destino continente(String nombre) {return new Destino(TipoGeografico.CONTINENTE, nombre);}
    private Destino pais(String nombre) {return new Destino(TipoGeografico.PAIS, nombre);}
    private Ciudad ciudad(String nombre) {return new Ciudad(TipoGeografico.CIUDAD, nombre);}

    private Servicio servicio(String nombre, String codigo, Categoria categoria, Double precio) {
        Disponibilidad d = new Disponibilidad();
        d.agregarCupoMasivo(periodo, 5);
        return new Servicio(nombre, 0.0, d, codigo, "PROVEEDOR", categoria, precio, paris);
    }
    @BeforeEach
    void setUp() {
        roma = ciudad("Roma");
        lyon = ciudad("Lyon");
        paris = ciudad("Paris");

        italia = pais("Italia");
        francia = pais("Francia");
        europa = continente("Europa");
        america = continente("America");

        europa.agregarDestino(italia);
        europa.agregarDestino(francia);

        italia.agregarDestino(roma);
        francia.agregarDestino(lyon);
        francia.agregarDestino(paris);


        catalogo = new Catalogo();

        vuelo = servicio("Vuelo a Aruba", "123VUELO", Categoria.VUELO, 123.0);
        alojamiento = servicio("Hotel en Aruba", "123HOTEL", Categoria.ALOJAMIENTO, 32.5);
        auto = servicio("Alquiler de auto", "123AUTO", Categoria.ALQUILER_AUTO, 12.2);
    }

    private void cargarTodos() {
        catalogo.agregarItem(vuelo);
        catalogo.agregarItem(alojamiento);
        catalogo.agregarItem(auto);
    }
    // ---------- Alta y baja ----------

    @Test
    void catalogo_nuevo_esta_vacio() {
        assertEquals(0, catalogo.cantidadItems());
    }

    @Test
    void catalogo_agrega_items_validos() {
        cargarTodos();

        assertEquals(3, catalogo.cantidadItems());
    }

    @Test
    void catalogo_rechaza_item_invalido() {
        Servicio invalido = servicio("Invalido", "999INV", Categoria.EXCURSION, 10.0);
        invalido.agregarAtributo(new AtributoDinamico("Apto Mascotas", true, new TipoBooleano(), 4.5));

        assertThrowsExactly(CatalogoException.ErrorItemInvalido.class, () -> catalogo.agregarItem(invalido));
        assertEquals(0, catalogo.cantidadItems());
    }

    @Test
    void catalogo_rechaza_item_duplicado() {
        catalogo.agregarItem(vuelo);

        assertThrowsExactly(CatalogoException.ItemExistente.class, () -> catalogo.agregarItem(vuelo));
        assertEquals(1, catalogo.cantidadItems());
    }

    @Test
    void catalogo_informa_si_contiene_un_item() {
        catalogo.agregarItem(vuelo);

        assertTrue(catalogo.contieneItem(vuelo));
        assertFalse(catalogo.contieneItem(auto));
    }


    @Test
    void catalogo_remover_item_inexistente_lanza_excepcion() {
        catalogo.agregarItem(vuelo);

        assertThrowsExactly(CatalogoException.ItemNoEncontrado.class, () -> catalogo.removerItem(alojamiento));
        assertEquals(1, catalogo.cantidadItems());
    }
    // ---------- Busqueda ----------

    @Test
    void buscar_en_catalogo_vacio_devuelve_lista_vacia() {
        assertTrue(catalogo.buscar(todos).isEmpty());
    }

    @Test
    void buscar_devuelve_solo_los_items_que_cumplen_el_criterio() {
        cargarTodos();

        List<ItemCatalogo> resultado = catalogo.buscar(baratos);

        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(alojamiento));
        assertTrue(resultado.contains(auto));
        assertFalse(resultado.contains(vuelo));
    }

    @Test
    void buscar_con_criterio_que_nadie_cumple_devuelve_lista_vacia() {
        cargarTodos();

        assertTrue(catalogo.buscar(ninguno).isEmpty());
    }

    @Test
    void buscar_devuelve_una_copia_que_no_afecta_al_catalogo() {
        cargarTodos();

        List<ItemCatalogo> resultado = catalogo.buscar(todos);

        assertThrows(UnsupportedOperationException.class, resultado::clear);
        assertEquals(3, catalogo.cantidadItems());
    }

    // ---------- Busqueda + disponibilidad ----------

    @Test
    void buscar_disponibles_por_fecha_excluye_los_items_sin_cupo() {
        cargarTodos();
        alojamiento.reservar(dia1, 5); // se agota solo el dia1

        List<ItemCatalogo> resultado = catalogo.buscarDisponibles(baratos, dia1, 1);

        assertEquals(List.of(auto), resultado);
    }

    @Test
    void buscar_disponibles_por_fecha_incluye_a_todos_si_hay_cupo() {
        cargarTodos();

        assertEquals(2, catalogo.buscarDisponibles(baratos, dia2, 5).size());
    }

    @Test
    void buscar_disponibles_por_fecha_respeta_la_cantidad_pedida() {
        cargarTodos();

        assertTrue(catalogo.buscarDisponibles(todos, dia1, 6).isEmpty());
    }

    @Test
    void buscar_disponibles_por_periodo_excluye_los_items_con_algun_dia_sin_cupo() {
        cargarTodos();
        auto.reservar(dia2, 5); // se agota solo el dia2

        List<ItemCatalogo> resultado = catalogo.buscarDisponibles(baratos, periodo, 1);

        assertEquals(List.of(alojamiento), resultado);
    }

    @Test
    void buscar_disponibles_por_periodo_incluye_a_todos_si_hay_cupo() {
        cargarTodos();

        assertEquals(3, catalogo.buscarDisponibles(todos, periodo, 5).size());
    }

    @Test
    void buscar_disponibles_no_devuelve_items_que_no_cumplen_el_criterio_aunque_haya_cupo() {
        cargarTodos();

        assertTrue(catalogo.buscarDisponibles(ninguno, periodo, 1).isEmpty());
    }

    @Test
    void consultar_disponibilidad_no_modifica_el_cupo_de_los_items() {
        cargarTodos();

        catalogo.buscarDisponibles(todos, periodo, 3);

        assertEquals(5, vuelo.disponibilidad().cupo(dia1));
    }
}