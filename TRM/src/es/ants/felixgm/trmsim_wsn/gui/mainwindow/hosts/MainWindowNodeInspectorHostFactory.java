package es.ants.felixgm.trmsim_wsn.gui.mainwindow.hosts;


import es.ants.felixgm.trmsim_wsn.gui.MainWindowContext;
import es.ants.felixgm.trmsim_wsn.gui.graph.SimulationGraphWorkspace;
import es.ants.felixgm.trmsim_wsn.gui.layout.CompactLegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.layout.MiniLegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowNodeInspectorController;
import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Sensor;

public final class MainWindowNodeInspectorHostFactory {
    private MainWindowNodeInspectorHostFactory() {
    }

    public static MainWindowNodeInspectorController.Host create(MainWindowContext context) {
        return new MainWindowNodeInspectorController.Host() {
            public Integer getSelectedNodeId() { return context.getSelectedNodeId(); }
            public void setSelectedNodeId(Integer nodeId) { context.setSelectedNodeId(nodeId); }
            public SimulationGraphWorkspace getGraphWorkspace() { return context.getGraphWorkspace(); }
            public javax.swing.JLabel getGraphNodeInspectorTitleLabel() { return context.getGraphNodeInspectorTitleLabel(); }
            public javax.swing.JTextArea getGraphNodeInspectorTextArea() { return context.getGraphNodeInspectorTextArea(); }
            public javax.swing.JPanel getSensorPropertiesPanel() { return context.getSensorPropertiesPanel(); }
            public javax.swing.JTextField getSensorIdTextField() { return context.getSensorIdTextField(); }
            public javax.swing.JTextField getXCoordinateTextField() { return context.getXCoordinateTextField(); }
            public javax.swing.JTextField getYCoordinateTextField() { return context.getYCoordinateTextField(); }
            public javax.swing.JLabel getNeighborsLabel() { return context.getNeighborsLabel(); }
            public javax.swing.JList getNeighborsList() { return context.getNeighborsList(); }
            public javax.swing.JScrollPane getNeighborsScrollPane() { return context.getNeighborsScrollPane(); }
            public javax.swing.JPanel getGraphInspectorLegendWrapper() { return context.getGraphInspectorLegendWrapper(); }
            public void setGraphInspectorLegendPanel(MiniLegendPanel panel) { context.setGraphInspectorLegendPanel(panel); }
            public CompactLegendPanel getDashboardLegendPanel() { return context.getDashboardLegendPanel(); }
            public es.ants.felixgm.trmsim_wsn.gui.legendpanels.LegendPanel getLegendPanel() { return context.getLegendPanel(); }
            public Network getCurrentNetwork() { return context.getCurrentNetwork(); }
            public Sensor getSensorById(int sensorId) { return context.getSensorById(sensorId); }
            public void setGraphInspectorExpanded(boolean expanded) { context.setGraphInspectorExpanded(expanded); }
            public void scheduleGraphInspectorAutoHide() { context.scheduleGraphInspectorAutoHide(); }
            public void stopGraphInspectorAutoHide() { context.stopGraphInspectorAutoHide(); }
        };
    }
}
