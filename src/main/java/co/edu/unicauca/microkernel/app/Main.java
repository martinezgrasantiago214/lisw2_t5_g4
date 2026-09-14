package co.edu.unicauca.microkernel.app;

import co.edu.unicauca.microkernel.common.entities.Question;
import co.edu.unicauca.microkernel.common.entities.QuestionRequest;
import co.edu.unicauca.microkernel.core.QuestionMicrokernel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Punto de entrada de la aplicacion de escritorio (Java Swing) que permite
 * registrar preguntas en el Banco de Preguntas Saber PRO usando la
 * arquitectura Microkernel + Tuberias y Filtros.
 */
public class Main {

    private final QuestionMicrokernel microkernel = new QuestionMicrokernel();

    private JTextField txtTitle;
    private JTextArea txtContent;
    private JComboBox<String> cmbType;
    private JComboBox<String> cmbClassification;
    private JTextField[] txtOptions;
    private JTextField txtCorrectAnswer;
    private JTextArea txtLog;
    private DefaultListModel<String> questionListModel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().createAndShowGui());
    }

    private void createAndShowGui() {
        JFrame frame = new JFrame("Banco de Preguntas Saber PRO - Microkernel + Tuberias y Filtros");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 650);
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));

        root.add(buildFormPanel(), BorderLayout.WEST);
        root.add(buildResultsPanel(), BorderLayout.CENTER);

        frame.setContentPane(root);
        frame.setVisible(true);

        log("Microkernel iniciado. Plugins cargados: " + microkernel.getPlugins().size());
        for (var plugin : microkernel.getPlugins()) {
            log(" -> " + plugin.getName());
        }
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Nueva pregunta"));
        panel.setPreferredSize(new Dimension(380, 600));

        txtTitle = new JTextField();
        txtContent = new JTextArea(4, 20);
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);

        cmbType = new JComboBox<>(new String[]{"MULTIPLE_CHOICE", "CASE", "MULTIMEDIA"});
        cmbClassification = new JComboBox<>(new String[]{
                "Arquitectura de software",
                "Ingenieria de requisitos",
                "Bases de datos",
                "Redes de computadores",
                "Programacion",
                "Estructuras de datos",
                "Matematicas",
                "Ingles"
        });
        cmbClassification.setEditable(true);

        txtOptions = new JTextField[4];
        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 4, 4));
        optionsPanel.setBorder(BorderFactory.createTitledBorder("Opciones (solo aplica a MULTIPLE_CHOICE)"));
        for (int i = 0; i < 4; i++) {
            txtOptions[i] = new JTextField();
            optionsPanel.add(labeled("Opcion " + (i + 1) + ":", txtOptions[i]));
        }

        txtCorrectAnswer = new JTextField();

        JButton btnGenerate = new JButton("Generar pregunta");
        btnGenerate.addActionListener(e -> onGenerateQuestion());

        JButton btnClear = new JButton("Limpiar formulario");
        btnClear.addActionListener(e -> clearForm());

        panel.add(labeled("Titulo:", txtTitle));
        panel.add(Box.createVerticalStrut(6));
        panel.add(new JLabel("Contenido / enunciado:"));
        panel.add(new JScrollPane(txtContent));
        panel.add(Box.createVerticalStrut(6));
        panel.add(labeled("Tipo de pregunta:", cmbType));
        panel.add(Box.createVerticalStrut(6));
        panel.add(labeled("Clasificacion:", cmbClassification));
        panel.add(Box.createVerticalStrut(6));
        panel.add(optionsPanel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(labeled("Respuesta correcta:", txtCorrectAnswer));
        panel.add(Box.createVerticalStrut(12));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.add(btnGenerate);
        buttonsPanel.add(btnClear);
        panel.add(buttonsPanel);

        return panel;
    }

    private JPanel buildResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        questionListModel = new DefaultListModel<>();
        JList<String> questionList = new JList<>(questionListModel);
        JScrollPane listScroll = new JScrollPane(questionList);
        listScroll.setBorder(BorderFactory.createTitledBorder("Banco de preguntas"));
        listScroll.setPreferredSize(new Dimension(480, 300));

        txtLog = new JTextArea();
        txtLog.setEditable(false);
        JScrollPane logScroll = new JScrollPane(txtLog);
        logScroll.setBorder(BorderFactory.createTitledBorder("Log de ejecucion"));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listScroll, logScroll);
        split.setResizeWeight(0.5);

        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private void onGenerateQuestion() {
        try {
            String type = (String) cmbType.getSelectedItem();
            List<String> options = new ArrayList<>();
            for (JTextField field : txtOptions) {
                if (!field.getText().trim().isEmpty()) {
                    options.add(field.getText().trim());
                }
            }

            QuestionRequest request = new QuestionRequest(
                    txtTitle.getText().trim(),
                    txtContent.getText().trim(),
                    type,
                    (String) cmbClassification.getSelectedItem(),
                    options,
                    txtCorrectAnswer.getText().trim()
            );

            Question generated = microkernel.executePlugin(type, request);

            if (generated != null) {
                questionListModel.addElement(generated.toString());
                log("OK -> Pregunta creada: " + generated);
                JOptionPane.showMessageDialog(null, "Pregunta generada correctamente.",
                        "Exito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                log("RECHAZADA -> La solicitud no paso el pipeline de validacion (tipo " + type + ").");
                JOptionPane.showMessageDialog(null,
                        "La pregunta no paso las validaciones del pipeline. Revisa el log.",
                        "Validacion fallida", JOptionPane.WARNING_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            log("ERROR -> " + ex.getMessage());
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtTitle.setText("");
        txtContent.setText("");
        for (JTextField field : txtOptions) {
            field.setText("");
        }
        txtCorrectAnswer.setText("");
    }

    private void log(String message) {
        txtLog.append(message + "\n");
    }

    private JPanel labeled(String label, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(4, 2));
        panel.add(new JLabel(label), BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    /**
     * Metodo de apoyo usado solo para pruebas manuales rapidas desde consola,
     * simulando la solicitud de ejemplo de la guia (no se ejecuta desde la GUI).
     */
    static void demoConsola() {
        QuestionMicrokernel microkernel = new QuestionMicrokernel();
        QuestionRequest requestValida = new QuestionRequest(
                "Pregunta SOLID",
                "¿Que representa la S en SOLID?",
                "MULTIPLE_CHOICE",
                "Arquitectura de software",
                Arrays.asList("Single Responsibility", "Open Closed", "Liskov", "Interface Segregation"),
                "Single Responsibility"
        );
        microkernel.executePlugin("MULTIPLE_CHOICE", requestValida);
        Map<String, Question> bank = microkernel.getQuestions();
        System.out.println("Preguntas en el banco: " + bank.size());
    }
}
