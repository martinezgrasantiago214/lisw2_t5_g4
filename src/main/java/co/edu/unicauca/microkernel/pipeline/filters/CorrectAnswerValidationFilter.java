package co.edu.unicauca.microkernel.pipeline.filters;

import co.edu.unicauca.microkernel.common.entities.QuestionRequest;
import co.edu.unicauca.microkernel.pipeline.base.QuestionFilter;

import java.util.List;

/**
 * Valida que la respuesta correcta exista y este presente dentro de la
 * lista de opciones de la pregunta.
 */
public class CorrectAnswerValidationFilter implements QuestionFilter {

    @Override
    public boolean process(QuestionRequest request) {
        String correctAnswer = request.getCorrectAnswer();
        List<String> options = request.getOptions();

        if (correctAnswer == null || correctAnswer.trim().isEmpty() || options == null) {
            return false;
        }

        for (String option : options) {
            if (option != null && option.trim().equalsIgnoreCase(correctAnswer.trim())) {
                return true;
            }
        }
        return false;
    }
}
