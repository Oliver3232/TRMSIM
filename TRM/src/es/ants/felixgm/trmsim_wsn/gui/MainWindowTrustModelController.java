package es.ants.felixgm.trmsim_wsn.gui;

import es.ants.felixgm.trmsim_wsn.gui.legendpanels.LegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.OutcomesPanel;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.TRMParametersPanel;
import es.ants.felixgm.trmsim_wsn.trm.TrustModelRegistry;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

final class MainWindowTrustModelController {
    private MainWindowTrustModelController() {
    }

    static void initialize(TRMSim_WSN window) {
        Vector<String> trustModels = new Vector<String>();
        trustModels.addAll(TrustModelRegistry.all().keySet());
        window.TRModelComboBox.setModel(new DefaultComboBoxModel(trustModels));
        window.TRModelComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String modelName = (value == null) ? "" : value.toString();
                if (MainWindowRuntimeSupport.isTRModelDisabled(modelName)) {
                    component.setForeground(new Color(145, 145, 145));
                }
                return component;
            }
        });
        if (!trustModels.isEmpty()) {
            window.lastAllowedTRModel = trustModels.firstElement();
        }

        for (final String trustModel : trustModels) {
            JMenuItem trustModelMenuItem = new JMenuItem(trustModel);
            if (MainWindowRuntimeSupport.isTRModelDisabled(trustModel)) {
                trustModelMenuItem.setEnabled(false);
            }
            trustModelMenuItem.addActionListener(evt -> {
                if (MainWindowRuntimeSupport.isTRModelDisabled(trustModel)) {
                    JOptionPane.showMessageDialog(window,
                            "PowerTrust is temporarily disabled in this build.",
                            "Model Disabled",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                window.TRModelComboBox.setSelectedItem(trustModel);
            });
            window.TRModelMenu.add(trustModelMenuItem);
        }
    }

    static void handleSelection(TRMSim_WSN window, java.awt.event.ItemEvent evt, Logger logger) {
        try {
            if ((evt != null) && (evt.getStateChange() != java.awt.event.ItemEvent.SELECTED)) {
                return;
            }
            String trustModelName = (String) window.TRModelComboBox.getSelectedItem();
            if (MainWindowRuntimeSupport.isTRModelDisabled(trustModelName)) {
                if (window.lastAllowedTRModel != null && !window.lastAllowedTRModel.equals(trustModelName)) {
                    SwingUtilities.invokeLater(() -> window.TRModelComboBox.setSelectedItem(window.lastAllowedTRModel));
                }
                JOptionPane.showMessageDialog(window,
                        "PowerTrust is temporarily disabled in this build.",
                        "Model Disabled",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            logger.info("Model switch requested: " + trustModelName + ", currentPanel="
                    + ((window.networkPanel == null) ? "null"
                    : window.networkPanel.getClass().getSimpleName() + "#" + System.identityHashCode(window.networkPanel))
                    + ", EDT=" + SwingUtilities.isEventDispatchThread() + ", thread=" + Thread.currentThread().getName());

            TrustModelSelectionHelper.updateTrustModelMenuSelection(window.TRModelMenu, trustModelName);
            TrustModelSelectionHelper.configureSelectedTrustModel(
                    TRMSim_WSN.C,
                    trustModelName,
                    window.parametersFileTextField,
                    window.parametersFileContentTextArea,
                    createCallbacks(window));

            TrustModelSelectionHelper.SelectionResult selectionResult = TrustModelSelectionHelper.rebuildTrustModelUi(
                    trustModelName,
                    window.legendPanelContainer,
                    window.networkPanelContainer,
                    window.outcomesTabbedPane,
                    window.graphWorkspace,
                    createCallbacks(window));

            window.legendPanel = selectionResult.getLegendPanel();
            window.networkPanel = selectionResult.getNetworkPanel();
            window.outcomesPanels = selectionResult.getOutcomesPanels();
            if (window.graphWorkspace != null) {
                window.graphWorkspace.applyVisualizationControlsToPanels(window.networkPanel);
            }

            TrustModelSelectionHelper.resetUiAfterTrustModelSwitch(
                    trustModelName,
                    window.resetWSNButton,
                    window.resetWSNmenuItem,
                    window.runTRMButton,
                    window.runTRMmenuItem,
                    window.saveWSNButton,
                    window.saveWSNmenuItem,
                    window.sensorPropertiesPanel,
                    window.messagesTextArea,
                    createCallbacks(window));

            window.lastAllowedTRModel = trustModelName;
            logger.info("Model switch completed: " + trustModelName + ", newPanel="
                    + ((window.networkPanel == null) ? "null"
                    : window.networkPanel.getClass().getSimpleName() + "#" + System.identityHashCode(window.networkPanel)));
            MainWindowRuntimeSupport.updateParametersSourceView(window);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Model switch failed", ex);
            JOptionPane.showMessageDialog(window, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private static TrustModelSelectionHelper.UiCallbacks createCallbacks(TRMSim_WSN window) {
        return new TrustModelSelectionHelper.UiCallbacks() {
            public void setTrmParametersPanel(TRMParametersPanel parametersPanel) {
                window.set_TRMParametersPanel(parametersPanel);
            }

            public void applyCurrentParameters() throws Exception {
                window.set_TRMParameters();
            }

            public void setClientsProbabilityControlsEnabled(boolean enabled) {
                MainWindowRuntimeSupport.setClientsProbabilityControlsEnabled(window, enabled);
            }

            public void setCurrentNetworkPanel(NetworkPanel networkPanel) {
                window.networkPanel = networkPanel;
            }

            public void attachNetworkPanel(NetworkPanel networkPanel) {
                MainWindowNetworkOverlayController.attachNetworkPanelToOverlay(MainWindowHosts.overlay(window), networkPanel);
            }

            public void applyVisualizationControls(NetworkPanel networkPanel) {
                if (window.graphWorkspace != null) {
                    window.graphWorkspace.applyVisualizationControlsToPanels(networkPanel);
                }
            }

            public void refreshInspectorLegendPanel() {
                MainWindowNodeInspectorController.refreshInspectorLegendPanel(MainWindowHosts.nodeInspector(window));
            }

            public void clearNetworkPanel(NetworkPanel networkPanel) {
                MainWindowRenderController.clearNetworkPanel(window, networkPanel);
            }

            public void clearNodeInspector() {
                MainWindowNodeInspectorController.clearNodeInspector(MainWindowHosts.nodeInspector(window));
            }

            public java.util.List<MiniLegendPanel.Item> createLegendItems() {
                return new ArrayList<MiniLegendPanel.Item>(MainWindowNodeInspectorController.createLegendItems(window.legendPanel));
            }
        };
    }
}
