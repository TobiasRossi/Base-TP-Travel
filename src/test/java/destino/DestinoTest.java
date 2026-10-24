package destino;

import com.pepito.reservas.destino.Ciudad;
import com.pepito.reservas.destino.Destino;
import com.pepito.reservas.destino.DestinoException;
import com.pepito.reservas.destino.TipoGeografico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class DestinoTest {

    Destino europa;
    Destino italia;
    Destino francia;
    Ciudad roma;

    private Destino destino(TipoGeografico tipo, String nombre) {
        return new Destino(tipo, nombre) {};
    }

    private Ciudad roma(String nombre) {
        return new Ciudad(TipoGeografico.CIUDAD, nombre);
    }

    @BeforeEach
    void setUp() {
        europa = destino(TipoGeografico.CONTINENTE, "Europa");
        italia = destino(TipoGeografico.PAIS, "Italia");
        francia = destino(TipoGeografico.PAIS, "Francia");
        roma = roma("Roma");
    }

    // ---------- puedeContener (jerarquia) ----------

    @Test
    void un_destino_puede_contener_solo_niveles_mas_bajos() {
        assertTrue(europa.puedeContener(italia));
        assertTrue(europa.puedeContener(roma));
        assertTrue(italia.puedeContener(roma));

        assertFalse(italia.puedeContener(europa));
        assertFalse(italia.puedeContener(francia));
    }

    // ---------- agregar ----------

    @Test
    void agrega_un_destino_compatible() {
        italia.agregarDestino(roma);

        assertTrue(italia.contiene(roma));
    }

    @Test
    void agregar_un_destino_incompatible_lanza_excepcion_y_no_lo_agrega() {
        DestinoException e = assertThrowsExactly(DestinoException.IncompatibilidadDestino.class,
                () -> italia.agregarDestino(europa));

        assertEquals("CONTINENTE no puede agregarse a un PAIS", e.getMessage());
        assertFalse(italia.contiene(europa));
    }

    // ---------- contiene ----------

    @Test
    void un_destino_se_contiene_a_si_mismo() {
        assertTrue(italia.contiene(italia));
    }

    @Test
    void contiene_a_cualquier_profundidad_y_no_contiene_ajenos() {
        italia.agregarDestino(roma);
        europa.agregarDestino(italia);

        assertTrue(europa.contiene(roma));
        assertFalse(europa.contiene(francia));
        assertFalse(italia.contiene(europa));
    }

    // ---------- remover ----------

    @Test
    void remueve_un_hijo_directo() {
        italia.agregarDestino(roma);

        italia.removerDestino(roma,true);

        assertFalse(italia.contiene(roma));
    }

    @Test
    void remover_un_destino_ajeno_lanza_excepcion() {
        DestinoException e = assertThrowsExactly(DestinoException.InexistenciaDestino.class,
                () -> italia.removerDestino(roma,true));

        assertTrue(e.getMessage().contains("Roma"));
    }

    @Test
    void remover_un_destino_que_no_es_hijo_directo_lanza_excepcion() {
        italia.agregarDestino(roma);
        europa.agregarDestino(italia);

        assertThrowsExactly(DestinoException.ViolacionRango.class,
                () -> europa.removerDestino(roma,false));
        assertTrue(europa.contiene(roma)); // sigue donde estaba
    }

    @Test
    void emueve_un_destino_que_no_es_hijo_directo_con_override() {
        italia.agregarDestino(roma);
        europa.agregarDestino(italia);
        europa.removerDestino(roma,true);
        assertFalse(europa.contiene(roma));

    }

    @Test
    void ciudad_siempre_es_de_tipo_ciudad() {
        assertEquals(TipoGeografico.CIUDAD, new Ciudad(TipoGeografico.PAIS, "X").tipo());
    }
}