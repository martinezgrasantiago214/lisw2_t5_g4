package co.edu.unicauca.microkernel.pipeline.base;

import co.edu.unicauca.microkernel.common.entities.QuestionRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Coordina la ejecucion secuencial de los filtros de validacion
 * (patron Tuberias y Filtros).
 */
public class QuestionPipeline {

    private List<QuestionFilter> filters = new ArrayList<>();
    private String lastError;

    public void addFilter(QuestionFilter filter) {
        filters.add(filter);
    }

    public boolean execute(QuestionRequest request) {
        for (QuestionFilter filter : filters) {
            if (!filter.process(request)) {
                lastError = "La validacion fallo en el filtro: " + filter.getClass().getSimpleName();
                System.err.println(lastError);
                return false;
            }
        }
        lastError = null;
        return true;
    }

    public String getLastError() {
        return lastError;
    }
}
