package es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers;


import es.ants.felixgm.trmsim_wsn.gui.MainWindowContext;
import es.ants.felixgm.trmsim_wsn.gui.export.SimulationExportHelper;
import es.ants.felixgm.trmsim_wsn.gui.export.SimulationResultRepository;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.OutcomesPanel;
import es.ants.felixgm.trmsim_wsn.gui.support.NetworkRenderSupport;
import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Service;
import java.util.Collection;

public final class MainWindowRenderController {
    private MainWindowRenderController() {
    }

    public static void toggleDisplay(MainWindowContext context, javax.swing.JCheckBox sourceCheckBox, String label) throws Exception {
        context.syncEmbeddedAndFullscreenDisplayControls();
        Network network = context.getController().get_currentNetwork();
        if (network == null) {
            return;
        }
        paintNetwork(context, network, context.getController().get_requiredService());
        context.prependMessage((sourceCheckBox.isSelected() ? "Showing " : "Not showing ") + label + "\n");
    }

    public static void showExportDialog(MainWindowContext context) {
        try {
            SimulationExportHelper.showExportDialog(
                    context.window(),
                    SimulationResultRepository.getInstance(),
                    new SimulationExportHelper.ExportHost() {
                        public boolean ensureSimulationDataAvailable(SimulationResultRepository repository) {
                            return MainWindowRenderController.ensureSimulationDataAvailable(context, repository);
                        }

                        public String exportEnergyGraph(java.awt.Component owner, es.ants.felixgm.trmsim_wsn.gui.export.ExportRequest request) throws Exception {
                            return MainWindowRenderController.exportEnergyGraph(context, owner, request);
                        }
                    });
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(context.window(),
                    "Error during export: " + ex.getMessage(),
                    "Export Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void renderCurrentNetworkOnPanel(MainWindowContext context, NetworkPanel targetPanel) {
        if (targetPanel == null || context.getController() == null) {
            return;
        }
        try {
            renderNetwork(targetPanel,
                    context.getController().get_currentNetwork(),
                    context.getController().get_requiredService(),
                    context.getCurrentRenderState());
        } catch (Exception ignored) {
        }
    }

    public static void clearNetworkPanel(MainWindowContext context, NetworkPanel targetPanel) {
        if (targetPanel == null) {
            return;
        }
        try {
            NetworkRenderSupport.RenderState renderState = context.getCurrentRenderState();
            Service requiredService = (context.getController() == null) ? null : context.getController().get_requiredService();
            renderNetwork(targetPanel, null, requiredService, renderState);
            context.renderOnFullscreen(null, requiredService, renderState);
            context.clearNodeInspector();
        } catch (Exception ex) {
            context.logRenderWarning("Unable to clear network panel", ex);
        }
    }

    public static void paintNetwork(MainWindowContext context, Network network, Service requiredService) throws Exception {
        NetworkRenderSupport.RenderState renderState = context.getCurrentRenderState();
        renderNetwork(context.getMainNetworkPanel(), network, requiredService, renderState);
        context.renderOnFullscreen(network, requiredService, renderState);
        context.sleepAfterUiUpdate();
    }

    private static String exportEnergyGraph(
            MainWindowContext context,
            java.awt.Component owner,
            es.ants.felixgm.trmsim_wsn.gui.export.ExportRequest request) throws Exception {
        OutcomesPanel energyOutcomesPanel = getEnergyOutcomesPanel(context.getOutcomesPanels());
        if (energyOutcomesPanel == null) {
            javax.swing.JOptionPane.showMessageDialog(owner,
                    "Energy Consumption graph is not available for the selected model.",
                    "Export Failed",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return es.ants.felixgm.trmsim_wsn.gui.export.GraphImageExporter.exportCurrentGraph(owner, energyOutcomesPanel, energyOutcomesPanel.getLabel(), request);
    }

    private static OutcomesPanel getEnergyOutcomesPanel(Collection<OutcomesPanel> outcomesPanels) {
        if (outcomesPanels == null) {
            return null;
        }
        for (OutcomesPanel outcomesPanel : outcomesPanels) {
            if ((outcomesPanel.getLabel() != null) && outcomesPanel.getLabel().toLowerCase().contains("energy")) {
                return outcomesPanel;
            }
        }
        return null;
    }

    private static boolean ensureSimulationDataAvailable(MainWindowContext context, SimulationResultRepository repository) {
        if (repository.getResultCount() == 0) {
            javax.swing.JOptionPane.showMessageDialog(context.window(),
                    "No simulation data available for export. Please run a simulation first.",
                    "No Data",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private static void renderNetwork(NetworkPanel targetPanel, Network network, Service requiredService, NetworkRenderSupport.RenderState renderState) throws Exception {
        NetworkRenderSupport.renderNetwork(targetPanel, network, requiredService, renderState);
    }
}
