# Taller 05 – Microkernel + Tuberías y Filtros
Laboratorio de Ingeniería de Software II — Universidad del Cauca
#
Integrantes:
Camilo Ramirez
Andres Florin
Santiago Martinez

## Resumen del proyecto

Este proyecto implementa el **Banco de Preguntas Saber PRO** usando una
arquitectura **Microkernel** combinada con el patrón **Tuberías y Filtros**:

- El **núcleo** (`QuestionMicrokernel`) almacena el banco de preguntas en un
  `Map<String, Question>` y carga dinámicamente los plugins declarados en
  `plugins.properties` usando **Reflexión** (`Class.forName` +
  `getDeclaredConstructor().newInstance()`), sin conocer sus clases concretas.
- Se implementan **3 plugins** que cumplen el contrato común `QuestionPlugin`:
  - `MultipleChoiceQuestionPlugin`: preguntas de selección múltiple. **Este
    plugin ejecuta el pipeline completo de 4 filtros.**
  - `CaseQuestionPlugin`: preguntas de análisis de caso.
  - `MultimediaQuestionPlugin`: preguntas con recursos multimedia.
- El patrón **Tuberías y Filtros** (`pipeline.base` / `pipeline.filters`)
  valida cada `QuestionRequest` mediante 4 filtros encadenados:
  1. `ContentValidationFilter` – título y contenido no vacíos.
  2. `OptionsValidationFilter` – exactamente 4 opciones, sin vacías ni
     duplicadas.
  3. `ClassificationFilter` – la clasificación pertenece a un catálogo válido
     (ej. "Arquitectura de software").
  4. `CorrectAnswerValidationFilter` – la respuesta correcta está entre las
     opciones.
- La interfaz de usuario (`app.Main`) es una aplicación de **escritorio Java
  Swing** que permite diligenciar el formulario de una pregunta, ejecutarla
  contra el microkernel y ver el banco de preguntas resultante y el log de
  ejecución.
- Se incluyen **pruebas unitarias con JUnit 5** para los filtros, el pipeline
  completo y el núcleo (carga de plugins vía reflexión, ejecución exitosa,
  solicitud inválida y tipo no soportado).

## Estructura de carpetas

```
mi-proyecto-taller05/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/co/edu/unicauca/microkernel/
│   │   │   ├── app/Main.java
│   │   │   ├── common/entities/{Question,QuestionRequest}.java
│   │   │   ├── common/interfaces/QuestionPlugin.java
│   │   │   ├── core/QuestionMicrokernel.java
│   │   │   ├── pipeline/base/{QuestionFilter,QuestionPipeline}.java
│   │   │   ├── pipeline/filters/{ContentValidationFilter,OptionsValidationFilter,
│   │   │   │                     ClassificationFilter,CorrectAnswerValidationFilter}.java
│   │   │   └── plugins/{MultipleChoiceQuestionPlugin,CaseQuestionPlugin,
│   │   │                MultimediaQuestionPlugin}.java
│   │   └── resources/plugins.properties
│   └── test/java/co/edu/unicauca/microkernel/
│       ├── core/QuestionMicrokernelTest.java
│       └── pipeline/QuestionPipelineTest.java
```

## Cómo ejecutar

Requisitos: JDK 11+ y Maven.

```bash
# Compilar y correr las pruebas
mvn test

# Ejecutar la aplicación de escritorio (Swing)
mvn exec:java

# Generar el .jar ejecutable
mvn package
java -jar target/microkernel-taller05.jar
```


