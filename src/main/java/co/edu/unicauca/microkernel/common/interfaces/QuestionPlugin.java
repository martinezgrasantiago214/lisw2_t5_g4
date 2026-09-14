package co.edu.unicauca.microkernel.common.interfaces;

import co.edu.unicauca.microkernel.common.entities.Question;
import co.edu.unicauca.microkernel.common.entities.QuestionRequest;

/**
 * Contrato comun que deben implementar todos los plugins de generacion
 * de preguntas. El microkernel solo conoce esta abstraccion (DIP).
 */
public interface QuestionPlugin {

    String getName();

    boolean supports(String type);

    Question generate(QuestionRequest request);
}
