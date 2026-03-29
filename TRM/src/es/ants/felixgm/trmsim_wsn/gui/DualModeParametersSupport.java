package es.ants.felixgm.trmsim_wsn.gui;

import es.ants.felixgm.trmsim_wsn.Controller;
import es.ants.felixgm.trmsim_wsn.SimulationSlot;
import es.ants.felixgm.trmsim_wsn.app.BatchSimulationConfig;
import es.ants.felixgm.trmsim_wsn.app.NetworkGenerationConfig;
import es.ants.felixgm.trmsim_wsn.app.SimulationConfig;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.TRMParametersPanel;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.TRMParametersPanelFactory;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.File;
import java.util.EnumMap;
import java.util.Map;

final class DualModeParametersSupport {
    static final class DualParameterUiState {
        private final JRadioButton fileRadioButton;
        private final JRadioButton customizedRadioButton;
        private final JTextField parametersFileTextField;
        private final JButton browseButton;
        private final JButton saveFileContentButton;
        private final JTextArea parametersFileContentTextArea;
        private final TRMParametersPanel parametersPanel;
        private final JComponent customizedContainer;
        private final JButton applyButton;

        DualParameterUiState(
                JRadioButton fileRadioButton,
                JRadioButton customizedRadioButton,
                JTextField parametersFileTextField,
                JButton browseButton,
                JButton saveFileContentButton,
                JTextArea parametersFileContentTextArea,
                TRMParametersPanel parametersPanel,
                JComponent customizedContainer,
                JButton applyButton) {
            this.fileRadioButton = fileRadioButton;
            this.customizedRadioButton = customizedRadioButton;
            this.parametersFileTextField = parametersFileTextField;
            this.browseButton = browseButton;
            this.saveFileContentButton = saveFileContentButton;
            this.parametersFileContentTextArea = parametersFileContentTextArea;
            this.parametersPanel = parametersPanel;
            this.customizedContainer = customizedContainer;
            this.applyButton = applyButton;
        }
    }

    private final TRMSim_WSN owner;
    private final Map<SimulationSlot, DualParameterUiState> dualParameterUiStates =
            new EnumMap<SimulationSlot, DualParameterUiState>(SimulationSlot.class);

    DualModeParametersSupport(TRMSim_WSN owner) {
        this.owner = owner;
    }

    JPanel createDualSettingsPanel(SimulationSlot slot) {
        DualSettingsPanel settingsPanel = owner.dualSettingsPanels.get(slot);
        if (settingsPanel == null) {
            settingsPanel = new DualSettingsPanel(owner.buildNetworkGenerationConfig(), owner.buildBatchSimulationConfig());
            owner.dualSettingsPanels.put(slot, settingsPanel);
        }
        Controller controller = owner.dualController(slot);
        settingsPanel.setEnabled((controller == null) || !controller.isSimulationRunning(slot));
        return settingsPanel;
    }

    NetworkGenerationConfig buildDualNetworkGenerationConfig(SimulationSlot slot) {
        DualSettingsPanel settingsPanel = owner.dualSettingsPanels.get(slot);
        return (settingsPanel != null) ? settingsPanel.buildNetworkGenerationConfig() : owner.buildNetworkGenerationConfig();
    }

    SimulationConfig buildDualSimulationConfig(SimulationSlot slot) {
        DualSettingsPanel settingsPanel = owner.dualSettingsPanels.get(slot);
        return (settingsPanel != null) ? settingsPanel.buildSimulationConfig() : owner.buildSimulationConfig();
    }

    BatchSimulationConfig buildDualBatchSimulationConfig(SimulationSlot slot) {
        DualSettingsPanel settingsPanel = owner.dualSettingsPanels.get(slot);
        return (settingsPanel != null) ? settingsPanel.buildBatchSimulationConfig() : owner.buildBatchSimulationConfig();
    }

    Component createDualParametersPanel(SimulationSlot slot, String trustModelName) {
        try {
            Controller controller = owner.dualController(slot);
            TRMParametersPanel parametersPanel = TRMParametersPanelFactory.create(trustModelName);
            parametersPanel.set_TRMParameters(controller.get_TRMParameters(slot));
            owner.dualParametersPanels.put(slot, parametersPanel);

            JPanel container = new JPanel(new BorderLayout(0, 8));
            container.setOpaque(false);

            JPanel parameterSourcePanel = new JPanel(new BorderLayout(0, 6));
            parameterSourcePanel.setOpaque(false);
            parameterSourcePanel.setBorder(BorderFactory.createTitledBorder("Parameter Source"));

            JPanel customizedPanel = new JPanel(new BorderLayout(0, 8));
            customizedPanel.setOpaque(false);

            JRadioButton fileRadioButton = new JRadioButton("File");
            JRadioButton customizedRadioButton = new JRadioButton("Customized");
            fileRadioButton.setOpaque(false);
            customizedRadioButton.setOpaque(false);
            fileRadioButton.setSelected(true);

            ButtonGroup sourceButtonGroup = new ButtonGroup();
            sourceButtonGroup.add(fileRadioButton);
            sourceButtonGroup.add(customizedRadioButton);

            JTextField parametersFileTextField = new JTextField(extractFileName(controller.get_parametersFile(slot)));
            JTextArea parametersFileContentTextArea = new JTextArea(8, 24);
            parametersFileContentTextArea.setText(controller.get_ParametersFileContent(slot));

            JButton browseButton = new JButton("Browse");
            JButton saveFileContentButton = new JButton("Save file content");
            JButton applyButton = new JButton("Apply");
            applyButton.setMargin(new java.awt.Insets(2, 6, 2, 6));

            browseButton.addActionListener(evt -> loadDualParametersFromFile(slot, parametersFileTextField, parametersFileContentTextArea, parametersPanel));
            saveFileContentButton.addActionListener(evt -> saveDualParametersFileContent(slot, parametersFileContentTextArea));
            applyButton.addActionListener(evt -> applyDualParameters(slot, parametersPanel, parametersFileContentTextArea));
            owner.dualParameterApplyButtons.put(slot, applyButton);

            fileRadioButton.addActionListener(evt -> applyDualParameterUiState(slot));
            customizedRadioButton.addActionListener(evt -> applyDualParameterUiState(slot));

            JPanel sourceActionsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            sourceActionsRow.setOpaque(false);
            sourceActionsRow.add(fileRadioButton);
            sourceActionsRow.add(customizedRadioButton);
            sourceActionsRow.add(browseButton);
            sourceActionsRow.add(saveFileContentButton);
            parameterSourcePanel.add(sourceActionsRow, BorderLayout.NORTH);
            parameterSourcePanel.add(parametersFileTextField, BorderLayout.CENTER);

            JScrollPane parametersScrollPane = new JScrollPane(parametersPanel);
            parametersScrollPane.setBorder(BorderFactory.createEmptyBorder());
            JScrollPane fileContentScrollPane = new JScrollPane(parametersFileContentTextArea);
            fileContentScrollPane.setBorder(BorderFactory.createTitledBorder("Parameters File Content"));

            JPanel customizedHeaderPanel = new JPanel(new BorderLayout(6, 0));
            customizedHeaderPanel.setOpaque(false);
            customizedHeaderPanel.add(new JLabel("Customized Parameters"), BorderLayout.WEST);
            JPanel customizedActionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            customizedActionsPanel.setOpaque(false);
            customizedActionsPanel.add(applyButton);
            customizedHeaderPanel.add(customizedActionsPanel, BorderLayout.EAST);
            customizedPanel.add(customizedHeaderPanel, BorderLayout.NORTH);
            customizedPanel.add(parametersScrollPane, BorderLayout.CENTER);

            dualParameterUiStates.put(slot, new DualParameterUiState(
                    fileRadioButton,
                    customizedRadioButton,
                    parametersFileTextField,
                    browseButton,
                    saveFileContentButton,
                    parametersFileContentTextArea,
                    parametersPanel,
                    customizedPanel,
                    applyButton));

            JSplitPane contentSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, customizedPanel, fileContentScrollPane);
            contentSplitPane.setResizeWeight(0.62);
            contentSplitPane.setContinuousLayout(true);
            contentSplitPane.setBorder(BorderFactory.createEmptyBorder());

            container.add(parameterSourcePanel, BorderLayout.NORTH);
            container.add(contentSplitPane, BorderLayout.CENTER);

            applyDualParameterUiState(slot);
            javax.swing.SwingUtilities.invokeLater(() -> applyDualParameterUiState(slot));
            return container;
        } catch (Exception ex) {
            JTextArea fallback = new JTextArea("Unable to initialize slot-specific parameters panel.\n\n" + ex.getMessage());
            fallback.setEditable(false);
            fallback.setLineWrap(true);
            fallback.setWrapStyleWord(true);
            return new JScrollPane(fallback);
        }
    }

    void applyDualParameters(SimulationSlot slot, TRMParametersPanel parametersPanel, JTextArea parametersFileContentTextArea) {
        Controller controller = owner.dualController(slot);
        if (parametersPanel == null || controller == null) {
            return;
        }
        try {
            controller.set_TRMParameters(slot, parametersPanel);
            parametersPanel.set_TRMParameters(controller.get_TRMParameters(slot));
            parametersFileContentTextArea.setText(controller.get_TRMParameters(slot).toString());
            owner.prependDualMessage(slot, "Parameters applied for " + owner.slotLabel(slot) + ".\n");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(owner, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    void loadDualParametersFromFile(SimulationSlot slot, JTextField parametersFileTextField, JTextArea parametersFileContentTextArea, TRMParametersPanel parametersPanel) {
        try {
            Controller controller = owner.dualController(slot);
            JFileChooser fileChooser = new JFileChooser(".");
            fileChooser.setDialogTitle("Parameters file selection");
            if (fileChooser.showOpenDialog(owner) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File selectedFile = fileChooser.getSelectedFile();
            parametersFileTextField.setText(selectedFile.getName());
            controller.set_parametersFile(slot, selectedFile.getCanonicalPath());
            parametersPanel.set_TRMParameters(controller.set_TRMParameters(slot, controller.get_parametersFile(slot)));
            parametersFileContentTextArea.setText(controller.get_ParametersFileContent(slot));
            owner.prependDualMessage(slot, "Parameters loaded from file.\n");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(owner, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    void saveDualParametersFileContent(SimulationSlot slot, JTextArea parametersFileContentTextArea) {
        try {
            Controller controller = owner.dualController(slot);
            JFileChooser fileChooser = new JFileChooser(".");
            fileChooser.setDialogTitle("Save parameters file");
            if (fileChooser.showSaveDialog(owner) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File selectedFile = fileChooser.getSelectedFile();
            controller.saveParametersFileContent(selectedFile.getAbsolutePath(), parametersFileContentTextArea.getText());
            owner.prependDualMessage(slot, "Parameters file saved.\n");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(owner, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    void updateDualParameterSourceState(
            boolean parametersFromFile,
            JTextField parametersFileTextField,
            JButton browseButton,
            JButton saveFileContentButton,
            JTextArea parametersFileContentTextArea,
            TRMParametersPanel parametersPanel,
            JComponent customizedContainer,
            JButton applyButton) {
        boolean customizationEnabled = !parametersFromFile;
        parametersFileTextField.setEnabled(parametersFromFile);
        browseButton.setEnabled(parametersFromFile);
        saveFileContentButton.setEnabled(parametersFromFile);
        parametersFileContentTextArea.setEnabled(parametersFromFile);
        setComponentTreeEnabled(parametersPanel, customizationEnabled);
        setComponentTreeEnabled(customizedContainer, customizationEnabled);
        parametersPanel.setFocusable(customizationEnabled);
        customizedContainer.setFocusable(customizationEnabled);
        applyButton.setEnabled(customizationEnabled);
    }

    void setSlotSimulationSettingsEnabled(SimulationSlot slot, boolean enabled) {
        DualSettingsPanel settingsPanel = owner.dualSettingsPanels.get(slot);
        if (settingsPanel != null) {
            settingsPanel.setEnabled(enabled);
        }
    }

    private void applyDualParameterUiState(SimulationSlot slot) {
        DualParameterUiState uiState = dualParameterUiStates.get(slot);
        if (uiState == null) {
            return;
        }
        boolean parametersFromFile = uiState.fileRadioButton.isSelected();
        uiState.fileRadioButton.setEnabled(true);
        uiState.customizedRadioButton.setEnabled(true);
        updateDualParameterSourceState(
                parametersFromFile,
                uiState.parametersFileTextField,
                uiState.browseButton,
                uiState.saveFileContentButton,
                uiState.parametersFileContentTextArea,
                uiState.parametersPanel,
                uiState.customizedContainer,
                uiState.applyButton);
    }

    void setComponentTreeEnabled(Component component, boolean enabled) {
        if (component == null) {
            return;
        }
        component.setEnabled(enabled);
        if (component instanceof java.awt.Container) {
            for (Component child : ((java.awt.Container) component).getComponents()) {
                setComponentTreeEnabled(child, enabled);
            }
        }
    }

    String extractFileName(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return "";
        }
        int separatorIndex = Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf(File.separatorChar));
        return separatorIndex >= 0 ? filePath.substring(separatorIndex + 1) : filePath;
    }
}
