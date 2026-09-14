package co.edu.unicauca.microkernel.plugins;

import co.edu.unicauca.microkernel.common.entities.Question;
import co.edu.unicauca.microkernel.common.entities.QuestionRequest;
import co.edu.unicauca.microkernel.common.interfaces.QuestionPlugin;
import co.edu.unicauca.microkernel.pipeline.base.QuestionPipeline;
import co.edu.unicauca.microkernel.pipeline.filters.ContentValidationFilter;
import co.edu.unicauca.microkernel.pipeline.filters.ClassificationFilter;

import java.util.UUID;

/**
 * Plugin que genera preguntas de analisis de caso (escenarios extensos
 * seguidos de una o varias preguntas asociadas).
 * Aplica una validacion mas ligera del pipeline (contenido + clasificacion),
 * ya que este tipo de pregunta no siempre maneja opciones de respuesta.
 */
public class CaseQuestionPlugin implements QuestionPlugin {

    private QuestionPipeline pipeline;

    public CaseQuestionPlugin() {
        pipeline = new QuestionPipeline();
        pipeline.addFilter(new ContentValidationFilter());
        pipeline.addFilter(new ClassificationFilter());
    }

    @Override
    public String getName() {
        return "case-question";
    }

    @Override
    public boolean supports(String type) {
        return "CASE".equalsIgnoreCase(type);
    }

    @Override
    public Question generate(QuestionRequest request) {
        if (!pipeline.execute(request)) {
            return null;
        }
        String contenidoCaso = "[Analisis de caso] " + request.getContent();
        return new Question(
                UUID.randomUUID().toString(),
                request.getTitle(),
                contenidoCaso,
                request.getType()
        );
    }
}
