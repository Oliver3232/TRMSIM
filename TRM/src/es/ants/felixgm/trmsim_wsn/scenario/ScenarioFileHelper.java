package es.ants.felixgm.trmsim_wsn.scenario;

import es.ants.felixgm.trmsim_wsn.app.BatchSimulationConfig;
import es.ants.felixgm.trmsim_wsn.app.NetworkGenerationConfig;

import javax.swing.JFileChooser;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import java.awt.BorderLayout;
import java.awt.Component;
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
        JTextField idField = new JTextField();
        JTextField displayNameField = new JTextField();
        JTextArea descriptionArea = new JTextArea(5, 32);
        JCheckBox includeInSimulatorCheckBox = new JCheckBox("Include in simulator predefined scenarios");
        includeInSimulatorCheckBox.setSelected(false);
        JLabel bundledHintLabel = new JLabel("Saved predefined scenarios are stored in TRM/src/resources/scenarios.");
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        String modelName = (recommendedTrustModel == null || recommendedTrustModel.trim().isEmpty())
                ? "current model"
                : recommendedTrustModel.trim();
        displayNameField.setText("Custom " + modelName + " Scenario");
        idField.setText(("custom-" + modelName).toLowerCase().replaceAll("[^a-z0-9]+", "-"));

        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 1, 0, 6));
        panel.add(new javax.swing.JLabel("Scenario ID"));
        panel.add(idField);
        panel.add(new javax.swing.JLabel("Display name"));
        panel.add(displayNameField);
        panel.add(new javax.swing.JLabel("Description"));
        javax.swing.JPanel descriptionPanel = new javax.swing.JPanel(new BorderLayout());
        descriptionPanel.add(descriptionArea, BorderLayout.CENTER);
        panel.add(descriptionPanel);
        panel.add(includeInSimulatorCheckBox);
        panel.add(bundledHintLabel);

        int choice = JOptionPane.showConfirmDialog(owner, panel, "Save Scenario", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return null;
        }

        String id = sanitizeId(idField.getText());
        String displayName = displayNameField.getText() == null ? "" : displayNameField.getText().trim();
        String description = descriptionArea.getText() == null ? "" : descriptionArea.getText().trim();
        if (id.isEmpty() || displayName.isEmpty()) {
            JOptionPane.showMessageDialog(owner, "Scenario ID and display name are required.", "Invalid Scenario", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (description.isEmpty()) {
            description = "Imported custom scenario saved from the current simulator configuration.";
        }
        return new ScenarioMetadata(id, displayName, description, includeInSimulatorCheckBox.isSelected());
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
}
