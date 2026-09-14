package co.edu.unicauca.microkernel.plugins;

import co.edu.unicauca.microkernel.common.entities.Question;
import co.edu.unicauca.microkernel.common.entities.QuestionRequest;
import co.edu.unicauca.microkernel.common.interfaces.QuestionPlugin;
import co.edu.unicauca.microkernel.pipeline.base.QuestionPipeline;
import co.edu.unicauca.microkernel.pipeline.filters.ContentValidationFilter;

import java.util.UUID;

/**
 * Plugin que genera preguntas con recursos multimedia (imagenes, audio
 * o video referenciados dentro del contenido de la pregunta).
 */
public class MultimediaQuestionPlugin implements QuestionPlugin {

    private QuestionPipeline pipeline;

    public MultimediaQuestionPlugin() {
        pipeline = new QuestionPipeline();
        pipeline.addFilter(new ContentValidationFilter());
    }

    @Override
    public String getName() {
        return "multimedia-question";
    }

    @Override
    public boolean supports(String type) {
        return "MULTIMEDIA".equalsIgnoreCase(type);
    }

    @Override
    public Question generate(QuestionRequest request) {
        if (!pipeline.execute(request)) {
            return null;
        }
        String contenidoMultimedia = "[Recurso multimedia adjunto] " + request.getContent();
        return new Question(
                UUID.randomUUID().toString(),
                request.getTitle(),
                contenidoMultimedia,
                request.getType()
        );
    }
}
