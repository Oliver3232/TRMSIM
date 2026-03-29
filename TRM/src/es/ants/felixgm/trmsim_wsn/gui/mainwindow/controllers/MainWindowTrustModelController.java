package es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers;


import es.ants.felixgm.trmsim_wsn.gui.MainWindowContext;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.TRMParametersPanel;
import es.ants.felixgm.trmsim_wsn.gui.trustmodel.TrustModelSelectionHelper;
import es.ants.felixgm.trmsim_wsn.trm.TrustModelRegistry;
import java.awt.Color;
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

public final class MainWindowTrustModelController {
    private MainWindowTrustModelController() {
    }

    public static void initialize(MainWindowContext context) {
        Vector<String> trustModels = new Vector<String>();
        trustModels.addAll(TrustModelRegistry.all().keySet());
        context.getTrustModelComboBox().setModel(new DefaultComboBoxModel(trustModels));
        context.getTrustModelComboBox().setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String modelName = (value == null) ? "" : value.toString();
                if (context.isTrustModelDisabled(modelName)) {
                    component.setForeground(new Color(145, 145, 145));
                }
                return component;
            }
        });
        if (!trustModels.isEmpty()) {
            context.setLastAllowedTrustModel(trustModels.firstElement());
        }

        for (final String trustModel : trustModels) {
            JMenuItem trustModelMenuItem = new JMenuItem(trustModel);
            if (context.isTrustModelDisabled(trustModel)) {
                trustModelMenuItem.setEnabled(false);
            }
            trustModelMenuItem.addActionListener(evt -> {
                if (context.isTrustModelDisabled(trustModel)) {
                    JOptionPane.showMessageDialog(context.window(),
                            "PowerTrust is temporarily disabled in this build.",
                            "Model Disabled",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                context.getTrustModelComboBox().setSelectedItem(trustModel);
            });
            context.getTrustModelMenu().add(trustModelMenuItem);
        }
    }

    public static void handleSelection(MainWindowContext context, java.awt.event.ItemEvent evt, Logger logger) {
        try {
            if ((evt != null) && (evt.getStateChange() != java.awt.event.ItemEvent.SELECTED)) {
                return;
            }
            String trustModelName = (String) context.getTrustModelComboBox().getSelectedItem();
            if (context.isSingleSimulationActive()) {
                if (context.getLastAllowedTrustModel() != null && !context.getLastAllowedTrustModel().equals(trustModelName)) {
                    SwingUtilities.invokeLater(() -> context.getTrustModelComboBox().setSelectedItem(context.getLastAllowedTrustModel()));
                }
                JOptionPane.showMessageDialog(context.window(),
                        "Stop the active simulation before changing the trust model.",
                        "Model Switch Blocked",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (context.isTrustModelDisabled(trustModelName)) {
                if (context.getLastAllowedTrustModel() != null && !context.getLastAllowedTrustModel().equals(trustModelName)) {
                    SwingUtilities.invokeLater(() -> context.getTrustModelComboBox().setSelectedItem(context.getLastAllowedTrustModel()));
                }
                JOptionPane.showMessageDialog(context.window(),
                        "PowerTrust is temporarily disabled in this build.",
                        "Model Disabled",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            logger.info("Model switch requested: " + trustModelName + ", currentPanel="
                    + ((context.getCurrentNetworkPanel() == null) ? "null"
                    : context.getCurrentNetworkPanel().getClass().getSimpleName() + "#" + System.identityHashCode(context.getCurrentNetworkPanel()))
                    + ", EDT=" + SwingUtilities.isEventDispatchThread() + ", thread=" + Thread.currentThread().getName());

            TrustModelSelectionHelper.updateTrustModelMenuSelection(context.getTrustModelMenu(), trustModelName);
            TrustModelSelectionHelper.configureSelectedTrustModel(
                    context.getController(),
                    trustModelName,
                    context.getParametersFileTextFieldRaw(),
                    context.getParametersFileContentTextAreaRaw(),
                    createCallbacks(context));

            TrustModelSelectionHelper.SelectionResult selectionResult = TrustModelSelectionHelper.rebuildTrustModelUi(
                    trustModelName,
                    context.getLegendPanelContainer(),
                    context.getNetworkPanelContainerPanel(),
                    context.getOutcomesTabbedPane(),
                    context.getGraphWorkspace(),
                    createCallbacks(context));

            context.setLegendPanel(selectionResult.getLegendPanel());
            context.setCurrentNetworkPanel(selectionResult.getNetworkPanel());
            context.setOutcomesPanels(selectionResult.getOutcomesPanels());
            context.refreshInspectorLegendPanel();
            if (context.getGraphWorkspace() != null) {
                context.getGraphWorkspace().setFullscreenLegendItems(context.createLegendItemsFromCurrentLegend());
                context.applyVisualizationControls(context.getCurrentNetworkPanel());
            }

            TrustModelSelectionHelper.resetUiAfterTrustModelSwitch(
                    trustModelName,
                    context.getResetWsnButton(),
                    context.getResetWsnMenuItem(),
                    context.getRunTrmButton(),
                    context.getRunTrmMenuItem(),
                    context.getSaveWsnButton(),
                    context.getSaveWsnMenuItem(),
                    context.getSensorPropertiesPanel(),
                    context.getMessagesTextArea(),
                    createCallbacks(context));

            context.setLastAllowedTrustModel(trustModelName);
            logger.info("Model switch completed: " + trustModelName + ", newPanel="
                    + ((context.getCurrentNetworkPanel() == null) ? "null"
                    : context.getCurrentNetworkPanel().getClass().getSimpleName() + "#" + System.identityHashCode(context.getCurrentNetworkPanel())));
            context.updateParametersSourceView();
            context.refreshSingleScenarioHeaderFromCurrentSelection();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Model switch failed", ex);
            JOptionPane.showMessageDialog(context.window(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private static TrustModelSelectionHelper.UiCallbacks createCallbacks(MainWindowContext context) {
        return new TrustModelSelectionHelper.UiCallbacks() {
            public void setTrmParametersPanel(TRMParametersPanel parametersPanel) {
                context.setTrmParametersPanel(parametersPanel);
            }

            public void applyCurrentParameters() throws Exception {
                context.applyCurrentParameters();
            }

            public void setClientsProbabilityControlsEnabled(boolean enabled) {
                context.setClientsProbabilityControlsEnabled(enabled);
            }

            public void setCurrentNetworkPanel(es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel networkPanel) {
                context.setCurrentNetworkPanel(networkPanel);
            }

            public void attachNetworkPanel(es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel networkPanel) {
                context.attachNetworkPanel(networkPanel);
            }

            public void applyVisualizationControls(es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel networkPanel) {
                context.applyVisualizationControls(networkPanel);
            }

            public void refreshInspectorLegendPanel() {
                context.refreshInspectorLegendPanel();
            }

            public void clearNetworkPanel(es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel networkPanel) {
                MainWindowRenderController.clearNetworkPanel(context, networkPanel);
            }

            public void clearNodeInspector() {
                context.clearNodeInspector();
            }
        };
    }
}
