package es.ants.felixgm.trmsim_wsn.scenario;

import es.ants.felixgm.trmsim_wsn.app.BatchSimulationConfig;
import es.ants.felixgm.trmsim_wsn.app.NetworkGenerationConfig;
import es.ants.felixgm.trmsim_wsn.gui.DualSettingsPanel;
import es.ants.felixgm.trmsim_wsn.gui.MainWindowContext;
import es.ants.felixgm.trmsim_wsn.gui.dual.DualSimulationWorkspacePanel;

import javax.swing.JComboBox;

/**
 * Applies predefined scenario values into existing UI controls.
 */
public final class ScenarioUiBindingHelper {
    private ScenarioUiBindingHelper() {
    }

    public static void applyToMainWindow(MainWindowContext context, ScenarioDefinition scenario) {
        if (scenario == null) {
            return;
        }
        context.setSingleScenarioSelectionSync(true);
        try {
            selectRecommendedTrustModel(context.getTrustModelComboBox(), scenario.getRecommendedTrustModel());
            applyNetworkConfig(
                    context.getMinNumSensorsSpinner(),
                    context.getMaxNumSensorsSpinner(),
                    context.getPercentageClientsSlider(),
                    context.getPercentageRelayServersSlider(),
                    context.getPercentageMaliciousServersSlider(),
                    context.getRadioRangeSlider(),
                    context.getDynamicCheckBox(),
                    context.getOscillatingCheckBox(),
                    context.getCollusionCheckBox(),
                    scenario.getNetworkGenerationConfig());
            applyBatchConfig(context.getNumNetworksSpinner(), context.getNumExecutionsSpinner(), scenario.getBatchSimulationConfig());
            alignMinMax(context.getMinNumSensorsSpinner(), context.getMaxNumSensorsSpinner());
            disableSingleDisplaySettings(context);
            updateSingleScenarioSummary(context, scenario);
        } finally {
            context.setSingleScenarioSelectionSync(false);
        }
    }

    public static void applyToDualSettingsPanel(
            DualSettingsPanel settingsPanel,
            JComboBox<String> trustModelComboBox,
            DualSimulationWorkspacePanel workspacePanel,
            ScenarioDefinition scenario) {
        if (settingsPanel == null || scenario == null) {
            return;
        }
        selectRecommendedTrustModel(trustModelComboBox, scenario.getRecommendedTrustModel());
        settingsPanel.applyScenario(scenario);
        updateDualScenarioSummary(workspacePanel, scenario);
    }

    public static void updateSingleScenarioSummary(MainWindowContext context, ScenarioDefinition scenario) {
        context.refreshSingleScenarioSummary(scenario.getDisplayName(), scenario.getDescription());
    }

    public static void updateDualScenarioSummary(DualSimulationWorkspacePanel workspacePanel, ScenarioDefinition scenario) {
        if (workspacePanel != null && scenario != null) {
            workspacePanel.updateScenarioSummary(scenario.getDisplayName(), scenario.getDescription());
        }
    }

    private static void applyNetworkConfig(
            javax.swing.JSpinner minSpinner,
            javax.swing.JSpinner maxSpinner,
            javax.swing.JSlider clientsSlider,
            javax.swing.JSlider relaySlider,
            javax.swing.JSlider maliciousSlider,
            javax.swing.JSlider radioRangeSlider,
            javax.swing.JCheckBox dynamicCheckBox,
            javax.swing.JCheckBox oscillatingCheckBox,
            javax.swing.JCheckBox collusionCheckBox,
            NetworkGenerationConfig networkConfig) {
        minSpinner.setValue(Integer.valueOf(networkConfig.getMinNumSensors()));
        maxSpinner.setValue(Integer.valueOf(networkConfig.getMaxNumSensors()));
        clientsSlider.setValue((int) Math.round(networkConfig.getProbClients() * clientsSlider.getMaximum()));
        relaySlider.setValue((int) Math.round(networkConfig.getProbRelay() * relaySlider.getMaximum()));
        maliciousSlider.setValue((int) Math.round(networkConfig.getProbMalicious() * maliciousSlider.getMaximum()));
        radioRangeSlider.setValue((int) Math.round(networkConfig.getRadioRange() * radioRangeSlider.getMaximum()));
        dynamicCheckBox.setSelected(networkConfig.isDynamic());
        oscillatingCheckBox.setSelected(networkConfig.isOscillating());
        collusionCheckBox.setSelected(networkConfig.isCollusion());
    }

    private static void applyBatchConfig(javax.swing.JSpinner networksSpinner, javax.swing.JSpinner executionsSpinner, BatchSimulationConfig batchConfig) {
        networksSpinner.setValue(Integer.valueOf(batchConfig.getNumNetworks()));
        executionsSpinner.setValue(Integer.valueOf(batchConfig.getNumExecutions()));
    }

    private static void selectRecommendedTrustModel(JComboBox trustModelComboBox, String recommendedTrustModel) {
        if (trustModelComboBox == null || recommendedTrustModel == null || recommendedTrustModel.trim().isEmpty()) {
            return;
        }
        trustModelComboBox.setSelectedItem(recommendedTrustModel);
    }

    private static void alignMinMax(javax.swing.JSpinner minSpinner, javax.swing.JSpinner maxSpinner) {
        int minValue = ((Integer) minSpinner.getValue()).intValue();
        int maxValue = ((Integer) maxSpinner.getValue()).intValue();
        if (minValue > maxValue) {
            maxSpinner.setValue(Integer.valueOf(minValue));
        }
    }

    private static void disableSingleDisplaySettings(MainWindowContext context) {
        context.getShowIdsCheckBox().setSelected(false);
        context.getShowLinksCheckBox().setSelected(false);
        context.getShowRangesCheckBox().setSelected(false);
        context.getShowGridCheckBox().setSelected(false);
    }
}
