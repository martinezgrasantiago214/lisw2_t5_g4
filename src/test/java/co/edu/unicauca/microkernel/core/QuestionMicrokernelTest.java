package co.edu.unicauca.microkernel.core;

import co.edu.unicauca.microkernel.common.entities.Question;
import co.edu.unicauca.microkernel.common.entities.QuestionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del nucleo (Microkernel): carga dinamica de plugins
 * via Reflexion, ejecucion de plugins y manejo de tipos no soportados.
 */
public class QuestionMicrokernelTest {

    private QuestionMicrokernel microkernel;

    @BeforeEach
    public void setUp() {
        microkernel = new QuestionMicrokernel();
    }

    @Test
    public void testCargaDinamicaDeAlMenosTresPluginsViaReflexion() {
        assertTrue(microkernel.getPlugins().size() >= 3,
                "El microkernel debe cargar al menos 3 plugins desde plugins.properties");
    }

    @Test
    public void testEjecutarPluginMultipleChoiceValidoAgregaPreguntaAlBanco() {
        QuestionRequest request = new QuestionRequest(
                "Pregunta SOLID",
                "¿Que representa la S en SOLID?",
                "MULTIPLE_CHOICE",
                "Arquitectura de software",
                Arrays.asList("Single Responsibility", "Open Closed", "Liskov", "Interface Segregation"),
                "Single Responsibility"
        );

        Question generated = microkernel.executePlugin("MULTIPLE_CHOICE", request);

        assertNotNull(generated);
        assertEquals(1, microkernel.getQuestions().size());
        assertTrue(microkernel.getQuestions().containsKey(generated.getId()));
    }

    @Test
    public void testEjecutarPluginConSolicitudInvalidaNoAgregaPreguntaAlBanco() {
        QuestionRequest requestInvalido = new QuestionRequest(
                "", "", "MULTIPLE_CHOICE", "Arquitectura de software", Collections.emptyList(), ""
        );

        Question generated = microkernel.executePlugin("MULTIPLE_CHOICE", requestInvalido);

        assertNull(generated);
        assertEquals(0, microkernel.getQuestions().size());
    }

    @Test
    public void testEjecutarPluginCaseValido() {
        QuestionRequest request = new QuestionRequest(
                "Caso de estudio: microservicios",
                "Una empresa desea migrar su monolito a microservicios...",
                "CASE",
                "Arquitectura de software",
                null,
                null
        );

        Question generated = microkernel.executePlugin("CASE", request);

        assertNotNull(generated);
        assertEquals("CASE", generated.getType());
    }

    @Test
    public void testTipoNoSoportadoLanzaExcepcion() {
        QuestionRequest request = new QuestionRequest(
                "Titulo", "Contenido", "TIPO_INEXISTENTE", "Arquitectura de software", null, null
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> microkernel.executePlugin("TIPO_INEXISTENTE", request));

        assertTrue(ex.getMessage().contains("TIPO_INEXISTENTE"));
    }
}
