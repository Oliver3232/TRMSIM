package es.ants.felixgm.trmsim_wsn.gui;

import es.ants.felixgm.trmsim_wsn.app.BatchSimulationConfig;
import es.ants.felixgm.trmsim_wsn.app.NetworkGenerationConfig;
import es.ants.felixgm.trmsim_wsn.app.SimulationConfig;
import es.ants.felixgm.trmsim_wsn.gui.support.NumericInputBindingHelper;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public final class DualSettingsPanel extends JPanel {
    private final JSpinner minSensorsSpinner;
    private final JSpinner maxSensorsSpinner;
    private final JSpinner networksSpinner;
    private final JSpinner executionsSpinner;
    private final JSlider clientsSlider;
    private final JSlider relaySlider;
    private final JSlider maliciousSlider;
    private final JSlider radioRangeSliderLocal;
    private final JTextField clientsValue;
    private final JTextField relayValue;
    private final JTextField maliciousValue;
    private final JTextField radioRangeValue;
    private final JCheckBox dynamicCheckBox;
    private final JCheckBox oscillatingCheckBox;
    private final JCheckBox collusionCheckBox;
    private Runnable scenarioDirtyListener;
    private boolean scenarioSyncInProgress = false;

    private static final Dimension LABEL_SIZE = new Dimension(110, 20);
    private static final Dimension SPINNER_SIZE = new Dimension(84, 22);
    private static final Dimension SLIDER_SIZE = new Dimension(120, 22);
    private static final Dimension VALUE_SIZE = new Dimension(42, 22);

    DualSettingsPanel(NetworkGenerationConfig networkConfig, BatchSimulationConfig batchConfig) {
        setOpaque(false);
        setLayout(new GridLayout(1, 2, 8, 0));

        minSensorsSpinner = new JSpinner(new javax.swing.SpinnerNumberModel(networkConfig.getMinNumSensors(), 1, Integer.MAX_VALUE, 1));
        maxSensorsSpinner = new JSpinner(new javax.swing.SpinnerNumberModel(networkConfig.getMaxNumSensors(), 1, Integer.MAX_VALUE, 1));
        networksSpinner = new JSpinner(new javax.swing.SpinnerNumberModel(batchConfig.getNumNetworks(), 1, Integer.MAX_VALUE, 1));
        executionsSpinner = new JSpinner(new javax.swing.SpinnerNumberModel(batchConfig.getNumExecutions(), 1, Integer.MAX_VALUE, 1));
        clientsSlider = createPercentSlider(networkConfig.getProbClients());
        relaySlider = createPercentSlider(networkConfig.getProbRelay());
        maliciousSlider = createPercentSlider(networkConfig.getProbMalicious());
        radioRangeSliderLocal = createPercentSlider(networkConfig.getRadioRange());
        clientsValue = createValueField(clientsSlider.getValue());
        relayValue = createValueField(relaySlider.getValue());
        maliciousValue = createValueField(maliciousSlider.getValue());
        radioRangeValue = createValueField(radioRangeSliderLocal.getValue());
        dynamicCheckBox = new JCheckBox("Dynamic WSNs", networkConfig.isDynamic());
        oscillatingCheckBox = new JCheckBox("Oscillating WSNs", networkConfig.isOscillating());
        collusionCheckBox = new JCheckBox("Collusion", networkConfig.isCollusion());

        configureCheckBox(dynamicCheckBox);
        configureCheckBox(oscillatingCheckBox);
        configureCheckBox(collusionCheckBox);
        NumericInputBindingHelper.configureIntegerSpinner(minSensorsSpinner);
        NumericInputBindingHelper.configureIntegerSpinner(maxSensorsSpinner);
        NumericInputBindingHelper.configureIntegerSpinner(networksSpinner);
        NumericInputBindingHelper.configureIntegerSpinner(executionsSpinner);

        minSensorsSpinner.addChangeListener(evt -> {
            alignMinMax(true);
            notifyScenarioDirty();
        });
        maxSensorsSpinner.addChangeListener(evt -> {
            alignMinMax(false);
            notifyScenarioDirty();
        });
        networksSpinner.addChangeListener(evt -> notifyScenarioDirty());
        executionsSpinner.addChangeListener(evt -> notifyScenarioDirty());
        NumericInputBindingHelper.bindSliderAndField(clientsSlider, clientsValue);
        NumericInputBindingHelper.bindSliderAndField(relaySlider, relayValue);
        NumericInputBindingHelper.bindSliderAndField(maliciousSlider, maliciousValue);
        NumericInputBindingHelper.bindSliderAndField(radioRangeSliderLocal, radioRangeValue);
        clientsSlider.addChangeListener(evt -> notifyScenarioDirty());
        relaySlider.addChangeListener(evt -> notifyScenarioDirty());
        maliciousSlider.addChangeListener(evt -> notifyScenarioDirty());
        radioRangeSliderLocal.addChangeListener(evt -> notifyScenarioDirty());
        dynamicCheckBox.addItemListener(evt -> notifyScenarioDirty());
        oscillatingCheckBox.addItemListener(evt -> notifyScenarioDirty());
        collusionCheckBox.addItemListener(evt -> notifyScenarioDirty());

        JPanel networkSection = createSettingsSection(
                "Network",
                createSpinnerRow("Min Num Sensors", minSensorsSpinner),
                createSpinnerRow("Max Num Sensors", maxSensorsSpinner),
                createSliderRow("% Clients", clientsSlider, clientsValue),
                createSliderRow("% Relay Servers", relaySlider, relayValue),
                createSliderRow("% Malicious Servers", maliciousSlider, maliciousValue));
        JPanel simulationSection = createSettingsSection(
                "Simulation",
                createSpinnerRow("Num Networks", networksSpinner),
                createSpinnerRow("Executions", executionsSpinner),
                createSliderRow("Radio Range", radioRangeSliderLocal, radioRangeValue),
                createCheckBoxRow(dynamicCheckBox),
                createCheckBoxRow(oscillatingCheckBox),
                createCheckBoxRow(collusionCheckBox));

        add(networkSection);
        add(simulationSection);
    }

    NetworkGenerationConfig buildNetworkGenerationConfig() {
        return new NetworkGenerationConfig(
                ((Integer) minSensorsSpinner.getValue()).intValue(),
                ((Integer) maxSensorsSpinner.getValue()).intValue(),
                clientsSlider.getValue() / 100.0,
                relaySlider.getValue() / 100.0,
                maliciousSlider.getValue() / 100.0,
                radioRangeSliderLocal.getValue() / 100.0,
                dynamicCheckBox.isSelected(),
                oscillatingCheckBox.isSelected(),
                collusionCheckBox.isSelected());
    }

    SimulationConfig buildSimulationConfig() {
        return new SimulationConfig(
                dynamicCheckBox.isSelected(),
                oscillatingCheckBox.isSelected(),
                collusionCheckBox.isSelected(),
                ((Integer) executionsSpinner.getValue()).intValue());
    }

    BatchSimulationConfig buildBatchSimulationConfig() {
        return new BatchSimulationConfig(
                buildNetworkGenerationConfig(),
                ((Integer) networksSpinner.getValue()).intValue(),
                ((Integer) executionsSpinner.getValue()).intValue());
    }

    public void applyScenario(es.ants.felixgm.trmsim_wsn.scenario.ScenarioDefinition scenario) {
        if (scenario == null) {
            return;
        }
        scenarioSyncInProgress = true;
        NetworkGenerationConfig networkConfig = scenario.getNetworkGenerationConfig();
        BatchSimulationConfig batchConfig = scenario.getBatchSimulationConfig();
        minSensorsSpinner.setValue(Integer.valueOf(networkConfig.getMinNumSensors()));
        maxSensorsSpinner.setValue(Integer.valueOf(networkConfig.getMaxNumSensors()));
        clientsSlider.setValue((int) Math.round(networkConfig.getProbClients() * 100.0));
        relaySlider.setValue((int) Math.round(networkConfig.getProbRelay() * 100.0));
        maliciousSlider.setValue((int) Math.round(networkConfig.getProbMalicious() * 100.0));
        radioRangeSliderLocal.setValue((int) Math.round(networkConfig.getRadioRange() * 100.0));
        dynamicCheckBox.setSelected(networkConfig.isDynamic());
        oscillatingCheckBox.setSelected(networkConfig.isOscillating());
        collusionCheckBox.setSelected(networkConfig.isCollusion());
        networksSpinner.setValue(Integer.valueOf(batchConfig.getNumNetworks()));
        executionsSpinner.setValue(Integer.valueOf(batchConfig.getNumExecutions()));
        alignMinMax(true);
        scenarioSyncInProgress = false;
    }

    void setScenarioDirtyListener(Runnable scenarioDirtyListener) {
        this.scenarioDirtyListener = scenarioDirtyListener;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        updateChildrenEnabled(enabled);
    }

    private void configureCheckBox(JCheckBox checkBox) {
        checkBox.setOpaque(false);
        checkBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    }

    private JPanel createSettingsSection(String title, Component... rows) {
        JPanel section = new JPanel();
        section.setOpaque(false);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setBorder(BorderFactory.createTitledBorder(title));
        for (Component row : rows) {
            if (row instanceof JComponent) {
                ((JComponent) row).setAlignmentX(Component.LEFT_ALIGNMENT);
            }
            section.add(row);
        }
        return section;
    }

    private JPanel createSpinnerRow(String label, JSpinner spinner) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 1));
        row.setOpaque(false);
        JLabel rowLabel = new JLabel(label);
        rowLabel.setPreferredSize(LABEL_SIZE);
        spinner.setPreferredSize(SPINNER_SIZE);
        row.add(rowLabel);
        row.add(spinner);
        return row;
    }

    private JPanel createSliderRow(String label, JSlider slider, JTextField valueField) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 1));
        row.setOpaque(false);
        JLabel rowLabel = new JLabel(label);
        rowLabel.setPreferredSize(LABEL_SIZE);
        slider.setPreferredSize(SLIDER_SIZE);
        valueField.setHorizontalAlignment(JTextField.CENTER);
        valueField.setPreferredSize(VALUE_SIZE);
        row.add(rowLabel);
        row.add(slider);
        row.add(valueField);
        return row;
    }

    private JPanel createCheckBoxRow(JCheckBox checkBox) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 1));
        row.setOpaque(false);
        row.add(checkBox);
        return row;
    }

    private JSlider createPercentSlider(double ratio) {
        return new JSlider(0, 100, (int) Math.round(ratio * 100.0));
    }

    private JTextField createValueField(int value) {
        JTextField field = new JTextField(String.valueOf(value));
        field.setEditable(true);
        field.setPreferredSize(VALUE_SIZE);
        return field;
    }

    private void alignMinMax(boolean minChanged) {
        int minValue = ((Integer) minSensorsSpinner.getValue()).intValue();
        int maxValue = ((Integer) maxSensorsSpinner.getValue()).intValue();
        if (minChanged && minValue > maxValue) {
            maxSensorsSpinner.setValue(Integer.valueOf(minValue));
        } else if (!minChanged && maxValue < minValue) {
            minSensorsSpinner.setValue(Integer.valueOf(maxValue));
        }
    }

    private void updateChildrenEnabled(boolean enabled) {
        for (Component child : getComponents()) {
            setComponentTreeEnabled(child, enabled);
        }
    }

    private void notifyScenarioDirty() {
        if (!scenarioSyncInProgress && scenarioDirtyListener != null) {
            scenarioDirtyListener.run();
        }
    }

    private void setComponentTreeEnabled(Component component, boolean enabled) {
        component.setEnabled(enabled);
        if (component instanceof java.awt.Container) {
            for (Component child : ((java.awt.Container) component).getComponents()) {
                setComponentTreeEnabled(child, enabled);
            }
        }
    }

}
