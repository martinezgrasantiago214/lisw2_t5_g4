package co.edu.unicauca.microkernel.pipeline.filters;

import co.edu.unicauca.microkernel.common.entities.QuestionRequest;
import co.edu.unicauca.microkernel.pipeline.base.QuestionFilter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Valida que el area de conocimiento (clasificacion) de la pregunta
 * pertenezca a un catalogo de clasificaciones validas para el Saber PRO.
 */
public class ClassificationFilter implements QuestionFilter {

    private static final Set<String> VALID_CLASSIFICATIONS = new HashSet<>(Arrays.asList(
            "arquitectura de software",
            "ingenieria de requisitos",
            "bases de datos",
            "redes de computadores",
            "programacion",
            "estructuras de datos",
            "matematicas",
            "ingles"
    ));

    @Override
    public boolean process(QuestionRequest request) {
        String classification = request.getClassification();
        if (classification == null || classification.trim().isEmpty()) {
            return false;
        }
        String normalized = normalize(classification.trim().toLowerCase());
        return VALID_CLASSIFICATIONS.contains(normalized);
    }

    /**
     * Quita tildes para que "Arquitectura de Software" e "Ingenieria de Requisitos"
     * (con o sin acentos) sean reconocidas por igual.
     */
    private String normalize(String value) {
        return value
                .replace("á", "a").replace("é", "e").replace("í", "i")
                .replace("ó", "o").replace("ú", "u");
    }
}
