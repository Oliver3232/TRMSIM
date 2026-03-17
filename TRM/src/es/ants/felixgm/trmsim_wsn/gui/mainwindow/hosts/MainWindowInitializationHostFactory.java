package es.ants.felixgm.trmsim_wsn.gui.mainwindow.hosts;


import es.ants.felixgm.trmsim_wsn.gui.MainWindowContext;
import es.ants.felixgm.trmsim_wsn.gui.graph.SimulationGraphWorkspace;
import es.ants.felixgm.trmsim_wsn.gui.layout.MiniLegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowInitializationController;

public final class MainWindowInitializationHostFactory {
    private MainWindowInitializationHostFactory() {
    }

    public static MainWindowInitializationController.Host create(MainWindowContext context) {
        return new MainWindowInitializationController.Host() {
            public void setGraphWorkspace(SimulationGraphWorkspace workspace) { context.setGraphWorkspace(workspace); }
            public SimulationGraphWorkspace getGraphWorkspace() { return context.getGraphWorkspace(); }
            public es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel getCurrentNetworkPanel() { return context.getCurrentNetworkPanel(); }
            public void renderCurrentNetworkOnPanel(es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel panel) { context.renderCurrentNetworkOnPanel(panel); }
            public void clearNodeInspector() { context.clearNodeInspector(); }
            public void selectNodeByIdFromBootstrap(int nodeId) { context.selectNodeById(nodeId); }
            public void handlePauseResumeRequestFromBootstrap() { context.handlePauseResumeRequest(); }
            public void handleStopRequestFromBootstrap() { context.handleStopRequest(); }
            public javax.swing.JCheckBox getShowIdsCheckBox() { return context.getShowIdsCheckBox(); }
            public javax.swing.JCheckBox getShowLinksCheckBox() { return context.getShowLinksCheckBox(); }
            public javax.swing.JCheckBox getShowRangesCheckBox() { return context.getShowRangesCheckBox(); }
            public javax.swing.JCheckBox getShowGridCheckBox() { return context.getShowGridCheckBox(); }
            public javax.swing.JSlider getDelaySlider() { return context.getDelaySlider(); }
            public void installNetworkPanelSelectionHandler(es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel panel) { context.installNetworkPanelSelectionHandler(panel); }
            public void installGraphInfoStrip() { context.installGraphInfoStrip(); }
            public void applyModernLayout() { context.applyModernLayout(); }
            public void installEmbeddedNodeInspector() { context.installEmbeddedNodeInspector(); }
            public void updateParametersSourceView() { context.updateParametersSourceView(); }
            public void updateRunSimulationsControls() { context.updateRunSimulationsControls(); }
            public java.util.List<MiniLegendPanel.Item> createLegendItems() { return context.createLegendItems(); }
            public void syncEmbeddedAndFullscreenDisplayControls() { context.syncEmbeddedAndFullscreenDisplayControls(); }
            public void initializeTRModels() { context.initializeTRModels(); }
            public void setController(es.ants.felixgm.trmsim_wsn.Controller controller) { context.setController(controller); }
            public void setSimulationService(es.ants.felixgm.trmsim_wsn.app.SimulationApplicationService service) { context.setSimulationService(service); }
            public void triggerInitialTrustModelSelection() throws Exception { context.triggerInitialTrustModelSelection(); }
            public void setSize(int width, int height) { context.setSize(width, height); }
            public void setLocationRelativeTo(java.awt.Component component) { context.setLocationRelativeTo(component); }
        };
    }
}
