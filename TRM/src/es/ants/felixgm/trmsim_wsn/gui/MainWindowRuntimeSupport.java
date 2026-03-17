package es.ants.felixgm.trmsim_wsn.gui;


import es.ants.felixgm.trmsim_wsn.gui.graph.SimulationGraphWorkspace;
import es.ants.felixgm.trmsim_wsn.gui.layout.ModernLayoutInstaller;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowNodeInspectorController;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.support.NetworkRenderSupport;
import es.ants.felixgm.trmsim_wsn.gui.support.UiStateHelper;
import es.ants.felixgm.trmsim_wsn.network.Sensor;
import es.ants.felixgm.trmsim_wsn.trm.powertrust.PowerTrust;

public final class MainWindowRuntimeSupport {
    private MainWindowRuntimeSupport() {
    }

    static void stopGraphInspectorAutoHide(TRMSim_WSN window) {
        if (window.graphNodeInspectorAutoHideTimer != null) {
            window.graphNodeInspectorAutoHideTimer.stop();
        }
    }

    static void scheduleGraphInspectorAutoHide(TRMSim_WSN window) {
        stopGraphInspectorAutoHide(window);
        if (window.graphInspectorPinned) {
            return;
        }
        if (window.graphNodeInspectorAutoHideTimer == null) {
            window.graphNodeInspectorAutoHideTimer = new javax.swing.Timer(2000, evt -> {
                if (!window.graphInspectorPinned) {
                    MainWindowEmbeddedInspectorController.setGraphInspectorExpanded(window, false);
                }
            });
            window.graphNodeInspectorAutoHideTimer.setRepeats(false);
        }
        window.graphNodeInspectorAutoHideTimer.restart();
    }

    static void installNetworkPanelSelectionHandler(TRMSim_WSN window, NetworkPanel panel) {
        if (panel == null) {
            return;
        }
        panel.setSensorSelectionListener(sensor -> {
            if (sensor != null) {
                MainWindowNodeInspectorController.selectNodeById(MainWindowHosts.nodeInspector(window), sensor.id());
            }
        });
    }

    static void applyModernLayout(TRMSim_WSN window) {
        ModernLayoutInstaller.install(
                window,
                window.newWSNButton, window.loadWSNButton, window.saveWSNButton, window.resetWSNButton,
                window.runTRMButton, window.stopTRMButton, window.runSimulationsButton, window.stopSimulationsButton, window.exportDataButton,
                window.TRModelLabel, window.TRModelComboBox,
                window.minNumSensorsLabel, window.minNumSensorsSpinner, window.maxNumSensorsLabel, window.maxNumSensorsSpinner,
                window.radioRangeLabel, window.radioRangeSlider, window.radioRangeTextField,
                window.percentageClientsLabel, window.percentageClientsSlider, window.percentageClientsTextField,
                window.percentageMaliciousServersLabel, window.percentageMaliciousServersSlider, window.percentageMaliciousServersTextField,
                window.percentageRelayServersLabel, window.percentageRelayServersSlider, window.percentageRelayServersTextField,
                window.delayLabel, window.delaySlider, window.delayTextField,
                window.numExecutionsLabel, window.numExecutionsSpinner,
                window.numNetworksLabel, window.numNetworksSpinner,
                window.collusionCheckBox, window.oscillatingWSNsCheckBox, window.dynamicWSNsCheckBox,
                window.showIdsCheckBox, window.showLinksCheckBox, window.showRangesCheckBox, window.showGridCheckBox,
                window.graphWorkspace.getVisualThemeComboBox(), window.graphWorkspace.getCameraPresetComboBox(),
                window.graphWorkspace.getEnable3DNavigationCheckBox(), window.graphWorkspace.getFullscreenGraphButton(),
                window.parametersPanel, window.messagePanel,
                window.networkPanelContainer, window.dashboardLegendPanel, window.outcomesPanelsPanel, window.outcomesTabbedPane
        );
        window.validate();
        window.repaint();
    }

    static NetworkRenderSupport.RenderState getCurrentRenderState(TRMSim_WSN window) {
        return NetworkRenderSupport.createState(
                window.radioRangeSlider.getValue() / (double) window.radioRangeSlider.getMaximum(),
                window.showRangesCheckBox.isSelected(),
                window.showLinksCheckBox.isSelected(),
                window.showIdsCheckBox.isSelected(),
                window.showGridCheckBox.isSelected());
    }

    static void setClientsProbabilityControlsEnabled(TRMSim_WSN window, boolean enabled) {
        UiStateHelper.setClientsProbabilityControlsEnabled(
                enabled,
                window.percentageClientsLabel,
                window.percentageClientsSlider,
                window.percentageClientsTextField);
    }

    static void updateParametersSourceView(TRMSim_WSN window) {
        UiStateHelper.updateParametersSourceView(
                window.parametersFileRadioButton.isSelected(),
                window.parametersFileLabel,
                window.parametersFileTextField,
                window.browseButton,
                window.saveParametersFileContentButton,
                window.TRMParametersScrollPane,
                window.parametersFileContentScrollPane,
                window.bottomParametersSplitPane);
    }

    static boolean isTRModelDisabled(String modelName) {
        return PowerTrust.get_name().equals(modelName);
    }

    static void triggerInitialTrustModelSelection(TRMSim_WSN window) throws Exception {
        window.TRModelComboBoxItemStateChanged(null);
    }

    static String getSelectedTrustModelName(TRMSim_WSN window) {
        return (String) window.TRModelComboBox.getSelectedItem();
    }

    static boolean isParametersFileSelected(TRMSim_WSN window) {
        return window.parametersFileRadioButton.isSelected();
    }
}
