package es.ants.felixgm.trmsim_wsn.gui;

import es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.OutcomesPanel;
import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Service;
import java.util.Collection;
import java.util.logging.Level;

final class MainWindowRenderController {
    private MainWindowRenderController() {
    }

    static void toggleDisplay(TRMSim_WSN window, javax.swing.JCheckBox sourceCheckBox, String label) throws Exception {
        window.syncEmbeddedAndFullscreenDisplayControls();
        Network network = TRMSim_WSN.C.get_currentNetwork();
        if (network == null) {
            return;
        }
        paintNetwork(window, network, TRMSim_WSN.C.get_requiredService());
        window.messagesTextArea.setText((sourceCheckBox.isSelected() ? "Showing " : "Not showing ") + label + "\n" + window.messagesTextArea.getText());
    }

    static void showExportDialog(TRMSim_WSN window) {
        try {
            SimulationExportHelper.showExportDialog(
                    window,
                    SimulationResultRepository.getInstance(),
                    new SimulationExportHelper.ExportHost() {
                        public boolean ensureSimulationDataAvailable(SimulationResultRepository repository) {
                            return MainWindowRenderController.ensureSimulationDataAvailable(window, repository);
                        }

                        public void exportEnergyGraph() throws Exception {
                            MainWindowRenderController.exportEnergyGraph(window);
                        }
                    });
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(window,
                    "Error during export: " + ex.getMessage(),
                    "Export Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    static void renderCurrentNetworkOnPanel(TRMSim_WSN window, NetworkPanel targetPanel) {
        if (targetPanel == null || TRMSim_WSN.C == null) {
            return;
        }
        try {
            renderNetwork(targetPanel,
                    TRMSim_WSN.C.get_currentNetwork(),
                    TRMSim_WSN.C.get_requiredService(),
                    MainWindowRuntimeSupport.getCurrentRenderState(window));
        } catch (Exception ignored) {
        }
    }

    static void clearNetworkPanel(TRMSim_WSN window, NetworkPanel targetPanel) {
        if (targetPanel == null) {
            return;
        }
        try {
            NetworkRenderSupport.RenderState renderState = MainWindowRuntimeSupport.getCurrentRenderState(window);
            Service requiredService = (TRMSim_WSN.C == null) ? null : TRMSim_WSN.C.get_requiredService();
            renderNetwork(targetPanel, null, requiredService, renderState);
            if (window.graphWorkspace != null) {
                window.graphWorkspace.renderOnFullscreen(
                        null,
                        requiredService,
                        renderState.getRadioRange(),
                        renderState.isShowRanges(),
                        renderState.isShowLinks(),
                        renderState.isShowIds(),
                        renderState.isShowGrid());
            }
            MainWindowNodeInspectorController.clearNodeInspector(MainWindowHosts.nodeInspector(window));
        } catch (Exception ex) {
            window.LOGGER.log(Level.WARNING, "Unable to clear network panel", ex);
        }
    }

    static void paintNetwork(TRMSim_WSN window, Network network, Service requiredService) throws Exception {
        NetworkRenderSupport.RenderState renderState = MainWindowRuntimeSupport.getCurrentRenderState(window);
        renderNetwork(window.networkPanel, network, requiredService, renderState);
        if (window.graphWorkspace != null) {
            window.graphWorkspace.renderOnFullscreen(
                    network,
                    requiredService,
                    renderState.getRadioRange(),
                    renderState.isShowRanges(),
                    renderState.isShowLinks(),
                    renderState.isShowIds(),
                    renderState.isShowGrid());
        }
        TRMSim_WSN.C.sleep();
    }

    private static void exportEnergyGraph(TRMSim_WSN window) throws Exception {
        OutcomesPanel energyOutcomesPanel = getEnergyOutcomesPanel(window.outcomesPanels);
        if (energyOutcomesPanel == null) {
            javax.swing.JOptionPane.showMessageDialog(window,
                    "Energy Consumption graph is not available for the selected model.",
                    "Export Failed",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        GraphImageExporter.exportCurrentGraph(window, energyOutcomesPanel, energyOutcomesPanel.getLabel());
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

    private static boolean ensureSimulationDataAvailable(TRMSim_WSN window, SimulationResultRepository repository) {
        if (repository.getResultCount() == 0) {
            javax.swing.JOptionPane.showMessageDialog(window,
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
