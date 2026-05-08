package es.ants.felixgm.trmsim_wsn.scenario;

import es.ants.felixgm.trmsim_wsn.app.BatchSimulationConfig;
import es.ants.felixgm.trmsim_wsn.app.NetworkGenerationConfig;

import javax.swing.JFileChooser;
import javax.swing.JDialog;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Imports and saves external scenario definitions using the same .properties format as bundled presets.
 */
public final class ScenarioFileHelper {
    private ScenarioFileHelper() {
    }

    public static File chooseScenarioFile(Component parent, String startDirectory, String dialogTitle, int dialogType) {
        JFileChooser fileChooser = new JFileChooser(startDirectory);
        fileChooser.setDialogTitle(dialogTitle);
        fileChooser.setDialogType(dialogType);
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().toLowerCase().endsWith(".properties");
            }

            @Override
            public String getDescription() {
                return "Scenario Properties Files";
            }
        });
        int result = (dialogType == JFileChooser.OPEN_DIALOG)
                ? fileChooser.showOpenDialog(parent)
                : fileChooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        return fileChooser.getSelectedFile();
    }

    public static ScenarioDefinition loadScenarioFromFile(File file) throws IOException {
        Properties properties = new Properties();
        FileInputStream inputStream = new FileInputStream(file);
        try {
            properties.load(inputStream);
        } finally {
            inputStream.close();
        }
        return PredefinedScenarioLoader.buildScenarioDefinition(properties);
    }

    public static boolean saveScenario(
            Component owner,
            String recommendedTrustModel,
            BatchSimulationConfig batchSimulationConfig) throws IOException {
        ScenarioMetadata metadata = promptScenarioMetadata(owner, recommendedTrustModel);
        if (metadata == null) {
            return false;
        }
        Properties properties = buildProperties(metadata, recommendedTrustModel, batchSimulationConfig);
        File targetFile = resolveSaveTarget(owner, metadata);
        if (targetFile == null) {
            return false;
        }
        ensureParentDirectory(targetFile);
        FileOutputStream outputStream = new FileOutputStream(targetFile);
        try {
            properties.store(outputStream, "TRMSim-WSN scenario");
        } finally {
            outputStream.close();
        }
        if (metadata.addToBundledScenarios) {
            registerBundledScenarioId(metadata.id);
        }
        JOptionPane.showMessageDialog(owner, "Scenario saved successfully", "Scenario Saved", JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    private static Properties buildProperties(
            ScenarioMetadata metadata,
            String recommendedTrustModel,
            BatchSimulationConfig batchSimulationConfig) {
        NetworkGenerationConfig networkConfig = batchSimulationConfig.getNetworkGenerationConfig();
        Properties properties = new Properties();
        properties.setProperty("id", metadata.id);
        properties.setProperty("displayName", metadata.displayName);
        properties.setProperty("description", metadata.description);
        properties.setProperty("network.minNumSensors", String.valueOf(networkConfig.getMinNumSensors()));
        properties.setProperty("network.maxNumSensors", String.valueOf(networkConfig.getMaxNumSensors()));
        properties.setProperty("network.probClients", String.valueOf(networkConfig.getProbClients()));
        properties.setProperty("network.probRelay", String.valueOf(networkConfig.getProbRelay()));
        properties.setProperty("network.probMalicious", String.valueOf(networkConfig.getProbMalicious()));
        properties.setProperty("network.radioRange", String.valueOf(networkConfig.getRadioRange()));
        properties.setProperty("network.dynamic", String.valueOf(networkConfig.isDynamic()));
        properties.setProperty("network.oscillating", String.valueOf(networkConfig.isOscillating()));
        properties.setProperty("network.collusion", String.valueOf(networkConfig.isCollusion()));
        properties.setProperty("simulation.numNetworks", String.valueOf(batchSimulationConfig.getNumNetworks()));
        properties.setProperty("simulation.numExecutions", String.valueOf(batchSimulationConfig.getNumExecutions()));
        properties.setProperty("recommendedTrustModel", recommendedTrustModel == null ? "" : recommendedTrustModel.trim());
        return properties;
    }

    private static ScenarioMetadata promptScenarioMetadata(Component owner, String recommendedTrustModel) {
        String modelName = (recommendedTrustModel == null || recommendedTrustModel.trim().isEmpty())
                ? "current model"
                : recommendedTrustModel.trim();
        SaveScenarioDialog dialog = new SaveScenarioDialog(owner, modelName);
        dialog.showDialog();
        return dialog.getMetadata();
    }

    private static String sanitizeId(String rawId) {
        if (rawId == null) {
            return "";
        }
        return rawId.trim().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-+|-+$)", "");
    }

    private static File ensurePropertiesExtension(File file) {
        if (file.getName().toLowerCase().endsWith(".properties")) {
            return file;
        }
        return new File(file.getParentFile(), file.getName() + ".properties");
    }

    private static File resolveSaveTarget(Component owner, ScenarioMetadata metadata) {
        if (metadata.addToBundledScenarios) {
            File scenariosDirectory = PredefinedScenarioLoader.filesystemScenariosDirectory();
            return ensurePropertiesExtension(new File(scenariosDirectory, metadata.id));
        }
        return chooseScenarioFile(owner, ".", "Save Scenario", JFileChooser.SAVE_DIALOG);
    }

    private static void ensureParentDirectory(File file) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Unable to create directory: " + parent.getAbsolutePath());
        }
    }

    private static void registerBundledScenarioId(String scenarioId) throws IOException {
        File indexFile = new File(PredefinedScenarioLoader.filesystemScenariosDirectory(), "index.properties");
        ensureParentDirectory(indexFile);
        Properties properties = new Properties();
        if (indexFile.isFile()) {
            FileInputStream inputStream = new FileInputStream(indexFile);
            try {
                properties.load(inputStream);
            } finally {
                inputStream.close();
            }
        }
        List<String> ids = new ArrayList<String>();
        String idsValue = properties.getProperty("scenario.ids", "").trim();
        if (!idsValue.isEmpty()) {
            ids.addAll(Arrays.asList(idsValue.split(",")));
        }
        String trimmedScenarioId = scenarioId == null ? "" : scenarioId.trim();
        boolean alreadyPresent = false;
        for (String id : ids) {
            if (trimmedScenarioId.equals(id.trim())) {
                alreadyPresent = true;
                break;
            }
        }
        if (!alreadyPresent && !trimmedScenarioId.isEmpty()) {
            ids.add(trimmedScenarioId);
        }
        List<String> normalizedIds = new ArrayList<String>();
        for (String id : ids) {
            String trimmedId = id == null ? "" : id.trim();
            if (!trimmedId.isEmpty()) {
                normalizedIds.add(trimmedId);
            }
        }
        properties.setProperty("scenario.ids", joinScenarioIds(normalizedIds));
        FileOutputStream outputStream = new FileOutputStream(indexFile);
        try {
            properties.store(outputStream, "TRMSim-WSN bundled scenario index");
        } finally {
            outputStream.close();
        }
    }

    private static String joinScenarioIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return "";
        }
        return String.join(",", ids);
    }

    private static final class ScenarioMetadata {
        private final String id;
        private final String displayName;
        private final String description;
        private final boolean addToBundledScenarios;

        private ScenarioMetadata(String id, String displayName, String description, boolean addToBundledScenarios) {
            this.id = id;
            this.displayName = displayName;
            this.description = description;
            this.addToBundledScenarios = addToBundledScenarios;
        }
    }

    private static final class SaveScenarioDialog extends JDialog {
        private static final Color BORDER_COLOR = new Color(216, 220, 228);
        private static final Color BACKGROUND_COLOR = Color.WHITE;

        private final JTextField idField = new JTextField();
        private final JTextField displayNameField = new JTextField();
        private final JTextArea descriptionArea = new JTextArea(5, 32);
        private final JCheckBox includeInSimulatorCheckBox = new JCheckBox("Include in simulator predefined scenarios");
        private final JLabel bundledHintLabel = new JLabel("Saved predefined scenarios are stored in TRM/src/resources/scenarios.");

        private ScenarioMetadata metadata;

        private SaveScenarioDialog(Component owner, String modelName) {
            super(resolveWindow(owner), "Save Scenario", Dialog.ModalityType.APPLICATION_MODAL);
            buildUi(modelName);
        }

        private void buildUi(String modelName) {
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setResizable(false);

            displayNameField.setText("Custom " + modelName + " Scenario");
            idField.setText(("custom-" + modelName).toLowerCase().replaceAll("[^a-z0-9]+", "-"));
            descriptionArea.setLineWrap(true);
            descriptionArea.setWrapStyleWord(true);
            descriptionArea.setBorder(new EmptyBorder(8, 8, 8, 8));
            includeInSimulatorCheckBox.setOpaque(false);
            bundledHintLabel.setForeground(new Color(72, 72, 72));
            bundledHintLabel.setVisible(false);
            includeInSimulatorCheckBox.addActionListener(evt -> bundledHintLabel.setVisible(includeInSimulatorCheckBox.isSelected()));

            JPanel content = new JPanel(new BorderLayout(0, 18));
            content.setBackground(BACKGROUND_COLOR);
            content.setBorder(new EmptyBorder(14, 14, 10, 14));
            content.add(buildFormPanel(), BorderLayout.CENTER);
            content.add(buildFooterPanel(), BorderLayout.SOUTH);

            setContentPane(content);
            pack();
            setMinimumSize(new Dimension(430, 360));
            setLocationRelativeTo(getOwner());
        }

        private JPanel buildFormPanel() {
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(BACKGROUND_COLOR);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.insets = new Insets(0, 0, 12, 0);

            gbc.gridy = 0;
            formPanel.add(createSection("Scenario ID", wrapField(idField)), gbc);
            gbc.gridy = 1;
            formPanel.add(createSection("Display name", wrapField(displayNameField)), gbc);
            gbc.gridy = 2;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1.0;
            formPanel.add(createSection("Description", wrapDescription()), gbc);
            gbc.gridy = 3;
            gbc.weighty = 0.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(0, 0, 0, 0);
            formPanel.add(includeInSimulatorCheckBox, gbc);

            return formPanel;
        }

        private JPanel buildFooterPanel() {
            JPanel footerPanel = new JPanel(new BorderLayout(0, 16));
            footerPanel.setBackground(BACKGROUND_COLOR);

            footerPanel.add(bundledHintLabel, BorderLayout.NORTH);

            JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            actionsPanel.setOpaque(false);

            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");
            okButton.setPreferredSize(new Dimension(74, 24));
            cancelButton.setPreferredSize(new Dimension(82, 24));
            okButton.addActionListener(evt -> onConfirm());
            cancelButton.addActionListener(evt -> dispose());
            getRootPane().setDefaultButton(okButton);

            actionsPanel.add(okButton);
            actionsPanel.add(cancelButton);
            footerPanel.add(actionsPanel, BorderLayout.SOUTH);
            return footerPanel;
        }

        private JPanel createSection(String labelText, Component field) {
            JPanel sectionPanel = new JPanel(new BorderLayout(0, 8));
            sectionPanel.setOpaque(false);
            sectionPanel.add(new JLabel(labelText), BorderLayout.NORTH);
            sectionPanel.add(field, BorderLayout.CENTER);
            return sectionPanel;
        }

        private Component wrapField(JTextField field) {
            field.setBorder(new EmptyBorder(6, 8, 6, 8));
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setBackground(BACKGROUND_COLOR);
            wrapper.setBorder(new LineBorder(BORDER_COLOR));
            Dimension fieldSize = field.getPreferredSize();
            wrapper.setPreferredSize(new Dimension(380, fieldSize.height + 12));
            wrapper.add(field, BorderLayout.CENTER);
            return wrapper;
        }

        private Component wrapDescription() {
            JScrollPane scrollPane = new JScrollPane(descriptionArea);
            scrollPane.setBorder(new LineBorder(BORDER_COLOR));
            scrollPane.setPreferredSize(new Dimension(380, 88));
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            return scrollPane;
        }

        private void onConfirm() {
            String id = sanitizeId(idField.getText());
            String displayName = displayNameField.getText() == null ? "" : displayNameField.getText().trim();
            String description = descriptionArea.getText() == null ? "" : descriptionArea.getText().trim();

            if (id.isEmpty() || displayName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Scenario ID and display name are required.", "Invalid Scenario", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (description.isEmpty()) {
                description = "Imported custom scenario saved from the current simulator configuration.";
            }

            metadata = new ScenarioMetadata(id, displayName, description, includeInSimulatorCheckBox.isSelected());
            dispose();
        }

        private void showDialog() {
            setVisible(true);
        }

        private ScenarioMetadata getMetadata() {
            return metadata;
        }

        private static Window resolveWindow(Component owner) {
            if (owner == null) {
                return new Frame();
            }
            Window window = owner instanceof Window ? (Window) owner : SwingUtilities.getWindowAncestor(owner);
            return window != null ? window : new Frame();
        }
    }
}
