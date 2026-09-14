package co.edu.unicauca.microkernel.pipeline.filters;

import co.edu.unicauca.microkernel.common.entities.QuestionRequest;
import co.edu.unicauca.microkernel.pipeline.base.QuestionFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Valida que existan exactamente 4 opciones de respuesta, que ninguna
 * este vacia y que no existan opciones duplicadas.
 */
public class OptionsValidationFilter implements QuestionFilter {

    private static final int REQUIRED_OPTIONS = 4;

    @Override
    public boolean process(QuestionRequest request) {
        List<String> options = request.getOptions();

        if (options == null || options.size() != REQUIRED_OPTIONS) {
            return false;
        }

        Set<String> uniqueOptions = new HashSet<>();
        for (String option : options) {
            if (option == null || option.trim().isEmpty()) {
                return false;
            }
            if (!uniqueOptions.add(option.trim().toLowerCase())) {
                return false; // opcion duplicada
            }
        }
        return true;
    }
}
