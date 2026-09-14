package co.edu.unicauca.microkernel.core;

import co.edu.unicauca.microkernel.common.entities.Question;
import co.edu.unicauca.microkernel.common.entities.QuestionRequest;
import co.edu.unicauca.microkernel.common.interfaces.QuestionPlugin;

import java.io.InputStream;
import java.util.*;

/**
 * Nucleo (Microkernel) del sistema de Banco de Preguntas.
 *
 * Responsabilidades:
 *  - Almacenar el banco de preguntas en un Map.
 *  - Registrar los plugins declarados en plugins.properties.
 *  - Cargar dinamicamente los plugins mediante Reflexion (obligatorio).
 *  - Ejecutar el plugin adecuado segun el tipo de pregunta solicitado.
 */
public class QuestionMicrokernel {

    private static final String PLUGINS_CONFIG_FILE = "plugins.properties";

    private Map<String, Question> questions = new HashMap<>();
    private List<QuestionPlugin> plugins = new ArrayList<>();

    public QuestionMicrokernel() {
        loadPlugins();
    }

    /**
     * Lee plugins.properties y, mediante Reflexion, instancia cada plugin
     * registrado sin que el nucleo conozca sus clases concretas (DIP).
     */
    private void loadPlugins() {
        try {
            Properties prop = new Properties();
            InputStream input = getClass().getClassLoader().getResourceAsStream(PLUGINS_CONFIG_FILE);
            if (input == null) {
                System.err.println("No se encontro el archivo plugins.properties");
                return;
            }
            prop.load(input);

            for (String key : prop.stringPropertyNames()) {
                String className = prop.getProperty(key);
                try {
                    // Uso obligatorio de Reflexion para instanciacion dinamica
                    Class<?> clazz = Class.forName(className);
                    QuestionPlugin plugin = (QuestionPlugin) clazz.getDeclaredConstructor().newInstance();
                    plugins.add(plugin);
                    System.out.println("Plugin cargado via reflexion: " + plugin.getName() + " (" + className + ")");
                } catch (Exception e) {
                    System.err.println("Error al cargar el plugin '" + className + "': " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al leer plugins.properties: " + e.getMessage());
        }
    }

    /**
     * Busca el plugin que soporte el tipo indicado y delega en el
     * la generacion (y validacion) de la pregunta.
     */
    public Question executePlugin(String type, QuestionRequest request) {
        for (QuestionPlugin plugin : plugins) {
            if (plugin.supports(type)) {
                Question question = plugin.generate(request);
                if (question != null) {
                    questions.put(question.getId(), question);
                    System.out.println("Pregunta agregada exitosamente al banco con ID: " + question.getId());
                }
                return question;
            }
        }
        throw new IllegalArgumentException("No hay ningun plugin registrado que soporte el tipo: " + type);
    }

    public Map<String, Question> getQuestions() {
        return questions;
    }

    public List<QuestionPlugin> getPlugins() {
        return plugins;
    }
}
