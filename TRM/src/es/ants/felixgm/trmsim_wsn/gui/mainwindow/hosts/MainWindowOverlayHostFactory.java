package es.ants.felixgm.trmsim_wsn.gui.mainwindow.hosts;


import es.ants.felixgm.trmsim_wsn.gui.MainWindowContext;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowNetworkOverlayController;

public final class MainWindowOverlayHostFactory {
    private MainWindowOverlayHostFactory() {
    }

    public static MainWindowNetworkOverlayController.Host create(MainWindowContext context) {
        return new MainWindowNetworkOverlayController.Host() {
            public javax.swing.JLayeredPane getNetworkOverlayPane() { return context.getNetworkOverlayPane(); }
            public void setNetworkOverlayPane(javax.swing.JLayeredPane value) { context.setNetworkOverlayPane(value); }
            public javax.swing.JPanel getNetworkPanelContainer() { return context.getNetworkPanelContainer(); }
            public es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel getCurrentNetworkPanel() { return context.getCurrentNetworkPanel(); }
            public javax.swing.JPanel getGraphNodeInspectorPanel() { return context.getGraphNodeInspectorPanel(); }
            public int getGraphInspectorCurrentWidth() { return context.getGraphInspectorCurrentWidth(); }
            public int getGraphInspectorMargin() { return context.getGraphInspectorMargin(); }
            public int getGraphInspectorCollapsedWidth() { return context.getGraphInspectorCollapsedWidth(); }
            public int getGraphInspectorMinHeight() { return context.getGraphInspectorMinHeight(); }
            public int getGraphInspectorMaxHeight() { return context.getGraphInspectorMaxHeight(); }
            public void installNetworkPanelSelectionHandler(es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel panel) { context.installNetworkPanelSelectionHandler(panel); }
        };
    }
}
