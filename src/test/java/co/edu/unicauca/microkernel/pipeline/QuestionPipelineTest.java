package co.edu.unicauca.microkernel.pipeline;

import co.edu.unicauca.microkernel.common.entities.QuestionRequest;
import co.edu.unicauca.microkernel.pipeline.base.QuestionPipeline;
import co.edu.unicauca.microkernel.pipeline.filters.ClassificationFilter;
import co.edu.unicauca.microkernel.pipeline.filters.ContentValidationFilter;
import co.edu.unicauca.microkernel.pipeline.filters.CorrectAnswerValidationFilter;
import co.edu.unicauca.microkernel.pipeline.filters.OptionsValidationFilter;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias de cada filtro individual y del pipeline completo
 * (patron Tuberias y Filtros).
 */
public class QuestionPipelineTest {

    private QuestionRequest requestValido() {
        return new QuestionRequest(
                "Pregunta SOLID",
                "¿Que representa la S en SOLID?",
                "MULTIPLE_CHOICE",
                "Arquitectura de software",
                Arrays.asList("Single Responsibility", "Open Closed", "Liskov", "Interface Segregation"),
                "Single Responsibility"
        );
    }

    // ---------- ContentValidationFilter ----------

    @Test
    public void testContentValidationFilterInvalido() {
        ContentValidationFilter filter = new ContentValidationFilter();
        QuestionRequest requestInvalido = new QuestionRequest("", "", "MULTIPLE_CHOICE", "Arquitectura", null, "");
        assertFalse(filter.process(requestInvalido));
    }

    @Test
    public void testContentValidationFilterValido() {
        ContentValidationFilter filter = new ContentValidationFilter();
        assertTrue(filter.process(requestValido()));
    }

    // ---------- OptionsValidationFilter ----------

    @Test
    public void testOptionsValidationFilterCantidadIncorrecta() {
        OptionsValidationFilter filter = new OptionsValidationFilter();
        QuestionRequest request = new QuestionRequest("T", "Contenido valido", "MULTIPLE_CHOICE",
                "Arquitectura de software", Arrays.asList("A", "B"), "A");
        assertFalse(filter.process(request));
    }

    @Test
    public void testOptionsValidationFilterDuplicadas() {
        OptionsValidationFilter filter = new OptionsValidationFilter();
        QuestionRequest request = new QuestionRequest("T", "Contenido valido", "MULTIPLE_CHOICE",
                "Arquitectura de software", Arrays.asList("A", "A", "B", "C"), "A");
        assertFalse(filter.process(request));
    }

    @Test
    public void testOptionsValidationFilterValido() {
        OptionsValidationFilter filter = new OptionsValidationFilter();
        assertTrue(filter.process(requestValido()));
    }

    // ---------- ClassificationFilter ----------

    @Test
    public void testClassificationFilterInvalida() {
        ClassificationFilter filter = new ClassificationFilter();
        QuestionRequest request = new QuestionRequest("T", "Contenido valido", "MULTIPLE_CHOICE",
                "Cocina Molecular", Arrays.asList("A", "B", "C", "D"), "A");
        assertFalse(filter.process(request));
    }

    @Test
    public void testClassificationFilterValida() {
        ClassificationFilter filter = new ClassificationFilter();
        assertTrue(filter.process(requestValido()));
    }

    // ---------- CorrectAnswerValidationFilter ----------

    @Test
    public void testCorrectAnswerValidationFilterNoPerteneceAOpciones() {
        CorrectAnswerValidationFilter filter = new CorrectAnswerValidationFilter();
        QuestionRequest request = new QuestionRequest("T", "Contenido valido", "MULTIPLE_CHOICE",
                "Arquitectura de software", Arrays.asList("A", "B", "C", "D"), "Z");
        assertFalse(filter.process(request));
    }

    @Test
    public void testCorrectAnswerValidationFilterValido() {
        CorrectAnswerValidationFilter filter = new CorrectAnswerValidationFilter();
        assertTrue(filter.process(requestValido()));
    }

    // ---------- Pipeline completo ----------

    @Test
    public void testPipelineCompletoConSolicitudValida() {
        QuestionPipeline pipeline = new QuestionPipeline();
        pipeline.addFilter(new ContentValidationFilter());
        pipeline.addFilter(new OptionsValidationFilter());
        pipeline.addFilter(new ClassificationFilter());
        pipeline.addFilter(new CorrectAnswerValidationFilter());

        assertTrue(pipeline.execute(requestValido()));
    }

    @Test
    public void testPipelineCompletoConSolicitudInvalidaSeDetieneEnElPrimerFiltroQueFalla() {
        QuestionPipeline pipeline = new QuestionPipeline();
        pipeline.addFilter(new ContentValidationFilter());
        pipeline.addFilter(new OptionsValidationFilter());
        pipeline.addFilter(new ClassificationFilter());
        pipeline.addFilter(new CorrectAnswerValidationFilter());

        QuestionRequest requestSinTitulo = new QuestionRequest("", "Contenido valido", "MULTIPLE_CHOICE",
                "Arquitectura de software", Arrays.asList("A", "B", "C", "D"), "A");

        assertFalse(pipeline.execute(requestSinTitulo));
        assertNotNull(pipeline.getLastError());
    }

    @Test
    public void testPipelineCompletoConOpcionesVacias() {
        QuestionPipeline pipeline = new QuestionPipeline();
        pipeline.addFilter(new ContentValidationFilter());
        pipeline.addFilter(new OptionsValidationFilter());

        QuestionRequest requestSinOpciones = new QuestionRequest("T", "Contenido valido", "MULTIPLE_CHOICE",
                "Arquitectura de software", Collections.emptyList(), "A");

        assertFalse(pipeline.execute(requestSinOpciones));
    }
}
