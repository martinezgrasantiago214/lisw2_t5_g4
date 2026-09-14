package co.edu.unicauca.microkernel.pipeline.base;

import co.edu.unicauca.microkernel.common.entities.QuestionRequest;

/**
 * Abstraccion del patron Tuberias y Filtros. Cada filtro concreto valida
 * una regla especifica sobre la solicitud de pregunta.
 */
public interface QuestionFilter {
    boolean process(QuestionRequest request);
}
