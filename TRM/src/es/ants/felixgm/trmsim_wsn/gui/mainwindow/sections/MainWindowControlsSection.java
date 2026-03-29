package es.ants.felixgm.trmsim_wsn.gui.mainwindow.sections;


import es.ants.felixgm.trmsim_wsn.gui.MainWindowHosts;
import es.ants.felixgm.trmsim_wsn.gui.MainWindowContext;
import es.ants.felixgm.trmsim_wsn.gui.TRMSim_WSN;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowActionController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowConfigurationController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowRenderController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowTrustModelController;
import es.ants.felixgm.trmsim_wsn.gui.support.NumericInputBindingHelper;
import es.ants.felixgm.trmsim_wsn.gui.trustmodel.TrustModelSelectionHelper;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;

public final class MainWindowControlsSection {
    private MainWindowControlsSection() {
    }

    public static void configure(
            TRMSim_WSN window,
            JScrollPane controlsScrollPane,
            JPanel controlsPanel,
            JPanel legendPanelContainer,
            JLabel legendLabel,
            JPanel buttonsControlPanel,
            JButton newWSNButton,
            JButton resetWSNButton,
            JButton runTRMButton,
            JButton stopTRMButton,
            JButton importScenarioButton,
            JButton saveScenarioButton,
            JButton loadScenarioButton,
            JButton loadWSNButton,
            JButton saveWSNButton,
            JButton stopSimulationsButton,
            JButton runSimulationsButton,
            JButton modeSwitchButton,
            JButton exportDataButton,
            JLabel activeScenarioLabel,
            javax.swing.JTextArea activeScenarioDescriptionTextArea,
            JPanel spinnersControlPanel,
            JLabel numExecutionsLabel,
            JSpinner numExecutionsSpinner,
            JLabel numNetworksLabel,
            JSpinner numNetworksSpinner,
            JLabel minNumSensorsLabel,
            JSpinner minNumSensorsSpinner,
            JLabel maxNumSensorsLabel,
            JSpinner maxNumSensorsSpinner,
            JPanel slidersControlsPanel,
            JLabel percentageClientsLabel,
            JSlider percentageClientsSlider,
            JTextField percentageClientsTextField,
            JLabel percentageRelayServersLabel,
            JSlider percentageRelayServersSlider,
            JTextField percentageRelayServersTextField,
            JLabel percentageMaliciousServersLabel,
            JSlider percentageMaliciousServersSlider,
            JTextField percentageMaliciousServersTextField,
            JLabel radioRangeLabel,
            JSlider radioRangeSlider,
            JTextField radioRangeTextField,
            JLabel delayLabel,
            JSlider delaySlider,
            JTextField delayTextField,
            JLabel trModelLabel,
            JComboBox trModelComboBox,
            JPanel displayControlsPanel,
            JCheckBox showIdsCheckBox,
            JCheckBox showLinksCheckBox,
            JCheckBox showRangesCheckBox,
            JCheckBox showGridCheckBox,
            JPanel threatsControlsPanel,
            JCheckBox collusionCheckBox,
            JCheckBox oscillatingWSNsCheckBox,
            JCheckBox dynamicWSNsCheckBox,
            es.ants.felixgm.trmsim_wsn.gui.legendpanels.LegendPanel legendPanel) {
        controlsScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Settings"));
        controlsScrollPane.setMinimumSize(new Dimension(300, 400));
        controlsScrollPane.setPreferredSize(new Dimension(360, (int) (java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.7)));

        controlsPanel.setPreferredSize(new java.awt.Dimension(400, 560));

        legendPanelContainer.setLayout(new BorderLayout());
        legendPanelContainer.removeAll();
        legendPanelContainer.add(legendPanel, BorderLayout.CENTER);
        legendPanel.setBackground(Color.white);
        Dimension legendSize = TrustModelSelectionHelper.computeLegendPreferredSize(legendPanel);
        legendPanel.setPreferredSize(legendSize);
        legendPanel.setMinimumSize(legendSize);
        legendPanelContainer.setPreferredSize(legendSize);
        legendPanelContainer.setMinimumSize(legendSize);

        legendLabel.setText("Legend");
        legendLabel.setPreferredSize(new java.awt.Dimension(100, 15));

        buttonsControlPanel.setMinimumSize(new java.awt.Dimension(250, 210));
        buttonsControlPanel.setLayout(new GridLayout(7, 2, 5, 5));
        configureButton(newWSNButton, "New WSN", evt -> MainWindowActionController.createNewNetwork(window), false);
        buttonsControlPanel.add(newWSNButton);
        configureButton(importScenarioButton, "Import Scenario", evt -> MainWindowActionController.importScenario(window), false);
        buttonsControlPanel.add(importScenarioButton);
        configureButton(saveScenarioButton, "Save Scenario", evt -> MainWindowActionController.saveScenario(window), false);
        buttonsControlPanel.add(saveScenarioButton);
        configureButton(loadScenarioButton, "Load Scenario", evt -> MainWindowActionController.loadScenario(window), false);
        buttonsControlPanel.add(loadScenarioButton);
        configureButton(resetWSNButton, "Reset WSN", evt -> MainWindowActionController.resetCurrentNetwork(window), true);
        buttonsControlPanel.add(resetWSNButton);
        configureButton(runTRMButton, "Run T&R Model", evt -> MainWindowActionController.runSingle(window, evt), true);
        buttonsControlPanel.add(runTRMButton);
        configureButton(stopTRMButton, "Stop T&R Model", evt -> MainWindowActionController.stopTrm(window), true);
        configureButton(loadWSNButton, "Load WSN", evt -> MainWindowActionController.loadNetwork(window), false);
        buttonsControlPanel.add(loadWSNButton);
        configureButton(saveWSNButton, "Save WSN", evt -> MainWindowActionController.saveNetwork(window), true);
        buttonsControlPanel.add(saveWSNButton);
        configureButton(stopSimulationsButton, "Stop Simulations", evt -> MainWindowActionController.stopBatch(window), true);
        buttonsControlPanel.add(stopSimulationsButton);
        configureButton(runSimulationsButton, "Run Simulations", evt -> MainWindowActionController.runBatch(window, evt), false);
        buttonsControlPanel.add(runSimulationsButton);
        configureButton(exportDataButton, "Export Data", evt -> MainWindowRenderController.showExportDialog(new es.ants.felixgm.trmsim_wsn.gui.MainWindowContext(window)), false);
        buttonsControlPanel.add(exportDataButton);
        configureButton(modeSwitchButton, "Dual Mode", evt -> window.switchAppMode(es.ants.felixgm.trmsim_wsn.gui.AppMode.DUAL), false);

        JPanel activeScenarioPanel = new JPanel(new BorderLayout(0, 4));
        activeScenarioPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Active Scenario"));
        activeScenarioPanel.setOpaque(false);
        activeScenarioLabel.setText("Scenario: Custom Configuration");
        activeScenarioLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        activeScenarioDescriptionTextArea.setText("Settings were entered manually. Use Load Scenario to apply a bundled predefined profile.");
        activeScenarioDescriptionTextArea.setEditable(false);
        activeScenarioDescriptionTextArea.setLineWrap(true);
        activeScenarioDescriptionTextArea.setWrapStyleWord(true);
        activeScenarioDescriptionTextArea.setRows(3);
        activeScenarioDescriptionTextArea.setOpaque(false);
        activeScenarioDescriptionTextArea.setAlignmentX(0.5F);
        activeScenarioPanel.add(activeScenarioLabel, BorderLayout.NORTH);
        activeScenarioPanel.add(activeScenarioDescriptionTextArea, BorderLayout.CENTER);

        spinnersControlPanel.setPreferredSize(new java.awt.Dimension(100, 205));
        spinnersControlPanel.setLayout(new javax.swing.BoxLayout(spinnersControlPanel, javax.swing.BoxLayout.Y_AXIS));
        configureSpinnerLabel(numExecutionsLabel, "Num executions");
        spinnersControlPanel.add(numExecutionsLabel);
        numExecutionsSpinner.setModel(new javax.swing.SpinnerNumberModel(100, 1, Integer.MAX_VALUE, 1));
        numExecutionsSpinner.setAlignmentX(0.0F);
        numExecutionsSpinner.setPreferredSize(new java.awt.Dimension(100, 20));
        NumericInputBindingHelper.configureIntegerSpinner(numExecutionsSpinner);
        numExecutionsSpinner.addChangeListener(evt -> MainWindowActionController.invalidateScenarioSelection(window));
        spinnersControlPanel.add(numExecutionsSpinner);
        configureSpinnerLabel(numNetworksLabel, "Num networks");
        spinnersControlPanel.add(numNetworksLabel);
        numNetworksSpinner.setModel(new javax.swing.SpinnerNumberModel(100, 1, Integer.MAX_VALUE, 1));
        numNetworksSpinner.setAlignmentX(0.0F);
        numNetworksSpinner.setPreferredSize(new java.awt.Dimension(100, 20));
        NumericInputBindingHelper.configureIntegerSpinner(numNetworksSpinner);
        numNetworksSpinner.addChangeListener(evt -> MainWindowActionController.invalidateScenarioSelection(window));
        spinnersControlPanel.add(numNetworksSpinner);
        configureSpinnerLabel(minNumSensorsLabel, "Min Num Sensors");
        spinnersControlPanel.add(minNumSensorsLabel);
        minNumSensorsSpinner.setModel(new javax.swing.SpinnerNumberModel(50, 1, Integer.MAX_VALUE, 1));
        minNumSensorsSpinner.setAlignmentX(0.0F);
        minNumSensorsSpinner.setPreferredSize(new java.awt.Dimension(100, 20));
        NumericInputBindingHelper.configureIntegerSpinner(minNumSensorsSpinner);
        minNumSensorsSpinner.addChangeListener(evt -> {
            MainWindowConfigurationController.alignMinSensors(MainWindowHosts.configuration(window));
            MainWindowActionController.invalidateScenarioSelection(window);
        });
        spinnersControlPanel.add(minNumSensorsSpinner);
        configureSpinnerLabel(maxNumSensorsLabel, "Max Num Sensors");
        spinnersControlPanel.add(maxNumSensorsLabel);
        maxNumSensorsSpinner.setModel(new javax.swing.SpinnerNumberModel(50, 1, Integer.MAX_VALUE, 1));
        maxNumSensorsSpinner.setAlignmentX(0.0F);
        maxNumSensorsSpinner.setPreferredSize(new java.awt.Dimension(100, 20));
        NumericInputBindingHelper.configureIntegerSpinner(maxNumSensorsSpinner);
        maxNumSensorsSpinner.addChangeListener(evt -> {
            MainWindowConfigurationController.alignMaxSensors(MainWindowHosts.configuration(window));
            MainWindowActionController.invalidateScenarioSelection(window);
        });
        spinnersControlPanel.add(maxNumSensorsSpinner);

        slidersControlsPanel.setLayout(new java.awt.GridBagLayout());
        addLabeledSlider(slidersControlsPanel, percentageClientsLabel, "% Clients", 0, percentageClientsSlider, 15, percentageClientsTextField);
        NumericInputBindingHelper.bindSliderAndField(percentageClientsSlider, percentageClientsTextField);
        percentageClientsSlider.addChangeListener(evt -> MainWindowActionController.invalidateScenarioSelection(window));
        addLabeledSlider(slidersControlsPanel, percentageRelayServersLabel, "% Relay Servers", 2, percentageRelayServersSlider, 5, percentageRelayServersTextField);
        NumericInputBindingHelper.bindSliderAndField(percentageRelayServersSlider, percentageRelayServersTextField);
        percentageRelayServersSlider.addChangeListener(evt -> MainWindowActionController.invalidateScenarioSelection(window));
        addLabeledSlider(slidersControlsPanel, percentageMaliciousServersLabel, "% Malicious Servers", 4, percentageMaliciousServersSlider, 70, percentageMaliciousServersTextField);
        NumericInputBindingHelper.bindSliderAndField(percentageMaliciousServersSlider, percentageMaliciousServersTextField);
        percentageMaliciousServersSlider.addChangeListener(evt -> MainWindowActionController.invalidateScenarioSelection(window));
        addLabeledSlider(slidersControlsPanel, radioRangeLabel, "Radio Range", 6, radioRangeSlider, 12, radioRangeTextField);
        NumericInputBindingHelper.bindSliderAndField(radioRangeSlider, radioRangeTextField);
        radioRangeSlider.addChangeListener(evt -> {
            MainWindowConfigurationController.onRadioRangeChanged(MainWindowHosts.configuration(window));
            MainWindowActionController.invalidateScenarioSelection(window);
        });
        addLabeledSlider(slidersControlsPanel, delayLabel, "Delay", 8, delaySlider, 0, delayTextField);
        NumericInputBindingHelper.bindSliderAndField(delaySlider, delayTextField);
        delaySlider.addChangeListener(evt -> MainWindowConfigurationController.onDelayChanged(MainWindowHosts.configuration(window)));

        trModelLabel.setText("Trust & Reputation Model");
        trModelComboBox.setPreferredSize(new java.awt.Dimension(140, 25));
        trModelComboBox.addItemListener(evt -> {
            MainWindowTrustModelController.handleSelection(new MainWindowContext(window), evt, Logger.getLogger(TRMSim_WSN.class.getName()));
            if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                MainWindowActionController.invalidateScenarioSelection(window);
            }
        });

        displayControlsPanel.setLayout(new javax.swing.BoxLayout(displayControlsPanel, javax.swing.BoxLayout.Y_AXIS));
        configureDisplayCheckBox(showIdsCheckBox, "Show ids", false, evt -> MainWindowActionController.toggleDisplay(window, showIdsCheckBox, "Ids"));
        displayControlsPanel.add(showIdsCheckBox);
        configureDisplayCheckBox(showLinksCheckBox, "Show links", true, evt -> MainWindowActionController.toggleDisplay(window, showLinksCheckBox, "links"));
        displayControlsPanel.add(showLinksCheckBox);
        configureDisplayCheckBox(showRangesCheckBox, "Show ranges", false, evt -> MainWindowActionController.toggleDisplay(window, showRangesCheckBox, "ranges"));
        displayControlsPanel.add(showRangesCheckBox);
        configureDisplayCheckBox(showGridCheckBox, "Show grid", false, evt -> MainWindowActionController.toggleDisplay(window, showGridCheckBox, "grid"));
        showGridCheckBox.setBorder(null);
        displayControlsPanel.add(showGridCheckBox);

        threatsControlsPanel.setLayout(new javax.swing.BoxLayout(threatsControlsPanel, javax.swing.BoxLayout.Y_AXIS));
        configureThreatCheckBox(collusionCheckBox, "Collusion");
        collusionCheckBox.addItemListener(evt -> MainWindowActionController.invalidateScenarioSelection(window));
        threatsControlsPanel.add(collusionCheckBox);
        configureThreatCheckBox(oscillatingWSNsCheckBox, "Oscillating WSNs");
        oscillatingWSNsCheckBox.addItemListener(evt -> MainWindowActionController.invalidateScenarioSelection(window));
        threatsControlsPanel.add(oscillatingWSNsCheckBox);
        configureThreatCheckBox(dynamicWSNsCheckBox, "Dynamic WSNs");
        dynamicWSNsCheckBox.addItemListener(evt -> MainWindowActionController.invalidateScenarioSelection(window));
        threatsControlsPanel.add(dynamicWSNsCheckBox);

        GroupLayout controlsPanelLayout = new GroupLayout(controlsPanel);
        controlsPanel.setLayout(controlsPanelLayout);
        controlsPanelLayout.setHorizontalGroup(
                controlsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(controlsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(controlsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(controlsPanelLayout.createSequentialGroup()
                                                .addComponent(buttonsControlPanel, GroupLayout.PREFERRED_SIZE, 267, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addGroup(controlsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(legendPanelContainer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(legendLabel, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)))
                                        .addComponent(activeScenarioPanel, GroupLayout.PREFERRED_SIZE, 365, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(trModelLabel)
                                        .addComponent(trModelComboBox, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                                        .addGroup(controlsPanelLayout.createSequentialGroup()
                                                .addGroup(controlsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(displayControlsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(spinnersControlPanel, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)
                                                .addGroup(controlsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(slidersControlsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(threatsControlsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(controlsPanelLayout.createSequentialGroup()
                                                .addComponent(modeSwitchButton, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addGroup(controlsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(trModelLabel)
                                                        .addComponent(trModelComboBox, GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE))))
                                .addContainerGap(16, 16))
        );
        controlsPanelLayout.setVerticalGroup(
                controlsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(controlsPanelLayout.createSequentialGroup()
                                .addGroup(controlsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addGroup(controlsPanelLayout.createSequentialGroup()
                                                .addComponent(legendLabel, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(legendPanelContainer, GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE))
                                        .addComponent(buttonsControlPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(activeScenarioPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(controlsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(spinnersControlPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(slidersControlsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(controlsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(threatsControlsPanel, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(displayControlsPanel, GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(controlsPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(modeSwitchButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addGroup(controlsPanelLayout.createSequentialGroup()
                                                .addComponent(trModelLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(trModelComboBox, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18))
        );

        controlsScrollPane.setViewportView(controlsPanel);
    }

    private static void configureButton(JButton button, String text, java.awt.event.ActionListener listener, boolean disabled) {
        button.setText(text);
        button.setEnabled(!disabled);
        button.setMargin(new java.awt.Insets(2, 5, 2, 5));
        button.setMaximumSize(new java.awt.Dimension(150, 25));
        button.setMinimumSize(new java.awt.Dimension(120, 25));
        button.setPreferredSize(new java.awt.Dimension(150, 25));
        button.addActionListener(listener);
    }

    private static void configureSpinnerLabel(JLabel label, String text) {
        label.setText(text);
        label.setPreferredSize(new java.awt.Dimension(210, 25));
    }

    private static void addLabeledSlider(JPanel panel, JLabel label, String text, int y, JSlider slider, int value, JTextField field) {
        label.setText(text);
        label.setPreferredSize(new java.awt.Dimension(210, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.ipadx = 20;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(label, gbc);

        slider.setValue(value);
        slider.setAlignmentX(0.0F);
        slider.setPreferredSize(new java.awt.Dimension(150, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = y + 1;
        gbc.ipadx = 20;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(slider, gbc);

        field.setEditable(true);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setPreferredSize(new java.awt.Dimension(45, 25));
        field.setText(String.valueOf(slider.getValue()));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = y + 1;
        if (y == 2 || y == 4 || y == 6) {
            gbc.gridheight = 2;
        }
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(field, gbc);
    }

    private static void configureDisplayCheckBox(JCheckBox checkBox, String text, boolean selected, java.awt.event.ItemListener listener) {
        checkBox.setSelected(selected);
        checkBox.setText(text);
        checkBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        checkBox.setMaximumSize(new java.awt.Dimension(120, 15));
        checkBox.setMinimumSize(new java.awt.Dimension(120, 15));
        checkBox.setPreferredSize(new java.awt.Dimension(120, 25));
        checkBox.addItemListener(listener);
    }

    private static void configureThreatCheckBox(JCheckBox checkBox, String text) {
        checkBox.setText(text);
        checkBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        checkBox.setMaximumSize(new java.awt.Dimension(120, 15));
        checkBox.setMinimumSize(new java.awt.Dimension(120, 15));
        checkBox.setPreferredSize(new java.awt.Dimension(120, 25));
    }
}
