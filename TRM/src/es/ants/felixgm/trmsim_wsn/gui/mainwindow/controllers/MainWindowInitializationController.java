package es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers;


import es.ants.felixgm.trmsim_wsn.Controller;
import es.ants.felixgm.trmsim_wsn.app.SimulationApplicationService;
import es.ants.felixgm.trmsim_wsn.gui.graph.SimulationGraphWorkspace;
import es.ants.felixgm.trmsim_wsn.gui.layout.MiniLegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel;
import java.awt.Toolkit;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JSlider;

public final class MainWindowInitializationController {
    public interface Host {
        void setGraphWorkspace(SimulationGraphWorkspace workspace);
        SimulationGraphWorkspace getGraphWorkspace();
        NetworkPanel getCurrentNetworkPanel();
        void renderCurrentNetworkOnPanel(NetworkPanel panel);
        void clearNodeInspector();
        void selectNodeByIdFromBootstrap(int nodeId);
        void handlePauseResumeRequestFromBootstrap();
        void handleStopRequestFromBootstrap();
        JCheckBox getShowIdsCheckBox();
        JCheckBox getShowLinksCheckBox();
        JCheckBox getShowRangesCheckBox();
        JCheckBox getShowGridCheckBox();
        JSlider getDelaySlider();
        void installNetworkPanelSelectionHandler(NetworkPanel panel);
        void installGraphInfoStrip();
        void applyModernLayout();
        void installEmbeddedNodeInspector();
        void updateParametersSourceView();
        void updateRunSimulationsControls();
        List<MiniLegendPanel.Item> createLegendItems();
        void syncEmbeddedAndFullscreenDisplayControls();
        boolean isSingleSimulationActive();
        java.awt.Component getWindowComponent();
        void initializeTRModels();
        void setController(Controller controller);
        void setSimulationService(SimulationApplicationService service);
        void triggerInitialTrustModelSelection() throws Exception;
        void setSize(int width, int height);
        void setLocationRelativeTo(java.awt.Component component);
    }

    private MainWindowInitializationController() {
    }

    public static void initialize(Host host) throws Exception {
        SimulationGraphWorkspace graphWorkspace = buildGraphWorkspace(host);
        host.setGraphWorkspace(graphWorkspace);
        graphWorkspace.initializeControls();

        host.installNetworkPanelSelectionHandler(host.getCurrentNetworkPanel());
        host.installGraphInfoStrip();
        host.applyModernLayout();
        host.installEmbeddedNodeInspector();
        host.updateParametersSourceView();
        host.updateRunSimulationsControls();
        host.clearNodeInspector();
        graphWorkspace.setFullscreenLegendItems(host.createLegendItems());
        host.syncEmbeddedAndFullscreenDisplayControls();

        host.setSize(
                (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.9),
                (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.9));
        host.setLocationRelativeTo(null);
        host.initializeTRModels();

        Controller controller = Controller.C();
        host.setController(controller);
        host.setSimulationService(new SimulationApplicationService(controller));
        host.triggerInitialTrustModelSelection();

        es.ants.felixgm.trmsim_wsn.gui.export.SimulationResultRepository.getInstance().clearRepository();
    }

    private static SimulationGraphWorkspace buildGraphWorkspace(Host host) {
        SimulationGraphWorkspace graphWorkspace = new SimulationGraphWorkspace(
                panel -> host.renderCurrentNetworkOnPanel(panel));
        graphWorkspace.setNodeSelectionListener(nodeId -> {
            if (nodeId == null) {
                host.clearNodeInspector();
            } else {
                host.selectNodeByIdFromBootstrap(nodeId.intValue());
            }
        });
        graphWorkspace.setSimulationControlListener(new SimulationGraphWorkspace.SimulationControlListener() {
            @Override
            public void onPauseResumeRequested() {
                host.handlePauseResumeRequestFromBootstrap();
            }

            @Override
            public void onStopRequested() {
                host.handleStopRequestFromBootstrap();
            }
        });
        graphWorkspace.setDisplayControlListener(new SimulationGraphWorkspace.DisplayControlListener() {
            @Override
            public void onShowIdsChanged(boolean selected) {
                host.getShowIdsCheckBox().setSelected(selected);
            }

            @Override
            public void onShowLinksChanged(boolean selected) {
                host.getShowLinksCheckBox().setSelected(selected);
            }

            @Override
            public void onShowRangesChanged(boolean selected) {
                host.getShowRangesCheckBox().setSelected(selected);
            }

            @Override
            public void onShowGridChanged(boolean selected) {
                host.getShowGridCheckBox().setSelected(selected);
            }

            @Override
            public void onDelayChanged(int value) {
                host.getDelaySlider().setValue(value);
            }
        });
        graphWorkspace.setFullscreenAccessGuard(new SimulationGraphWorkspace.FullscreenAccessGuard() {
            @Override
            public boolean canToggleFullscreen() {
                return !host.isSingleSimulationActive();
            }

            @Override
            public void onFullscreenBlocked() {
                javax.swing.JOptionPane.showMessageDialog(
                        host.getWindowComponent(),
                        "Stop the active T&R simulation before opening fullscreen mode.",
                        "Fullscreen Blocked",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
            }
        });
        return graphWorkspace;
    }
}
