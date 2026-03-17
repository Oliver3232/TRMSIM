package es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers;


import es.ants.felixgm.trmsim_wsn.gui.graph.SimulationGraphWorkspace;
import es.ants.felixgm.trmsim_wsn.gui.layout.CompactLegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.layout.MiniLegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.support.NodeInspectorHelper;
import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Sensor;
import es.ants.felixgm.trmsim_wsn.gui.legendpanels.EigenTrustLegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.legendpanels.LegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.legendpanels.PowerTrustLegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.legendpanels.TRIPLegendPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public final class MainWindowNodeInspectorController {
    public interface Host {
        Integer getSelectedNodeId();
        void setSelectedNodeId(Integer nodeId);
        SimulationGraphWorkspace getGraphWorkspace();
        JLabel getGraphNodeInspectorTitleLabel();
        JTextArea getGraphNodeInspectorTextArea();
        JPanel getSensorPropertiesPanel();
        JTextField getSensorIdTextField();
        JTextField getXCoordinateTextField();
        JTextField getYCoordinateTextField();
        JLabel getNeighborsLabel();
        JList getNeighborsList();
        JScrollPane getNeighborsScrollPane();
        JPanel getGraphInspectorLegendWrapper();
        void setGraphInspectorLegendPanel(MiniLegendPanel panel);
        CompactLegendPanel getDashboardLegendPanel();
        LegendPanel getLegendPanel();
        Network getCurrentNetwork();
        Sensor getSensorById(int sensorId);
        void setGraphInspectorExpanded(boolean expanded);
        void scheduleGraphInspectorAutoHide();
        void stopGraphInspectorAutoHide();
    }

    private MainWindowNodeInspectorController() {
    }

    public static List<MiniLegendPanel.Item> createLegendItems(LegendPanel legendPanel) {
        List<MiniLegendPanel.Item> items = new ArrayList<MiniLegendPanel.Item>();
        items.add(new MiniLegendPanel.Item("Client", Color.ORANGE));
        items.add(new MiniLegendPanel.Item("Benevolent", Color.GREEN));
        items.add(new MiniLegendPanel.Item("Malicious", Color.RED));
        items.add(new MiniLegendPanel.Item("Relay", Color.BLUE));
        items.add(new MiniLegendPanel.Item("Idle", Color.DARK_GRAY));
        if (legendPanel instanceof TRIPLegendPanel) {
            items.add(new MiniLegendPanel.Item("RSU", Color.MAGENTA));
        } else if (legendPanel instanceof EigenTrustLegendPanel) {
            items.add(new MiniLegendPanel.Item("Pre-Trusted", Color.MAGENTA));
        } else if (legendPanel instanceof PowerTrustLegendPanel) {
            items.add(new MiniLegendPanel.Item("Power Node", Color.MAGENTA));
        }
        return items;
    }

    public static void refreshInspectorLegendPanel(Host host) {
        JPanel wrapper = host.getGraphInspectorLegendWrapper();
        if (wrapper == null) {
            return;
        }
        wrapper.removeAll();
        MiniLegendPanel legendPanel = new MiniLegendPanel();
        legendPanel.setItems(createLegendItems(host.getLegendPanel()));
        legendPanel.setPreferredSize(new Dimension(250, 52));
        legendPanel.setOpaque(false);
        host.setGraphInspectorLegendPanel(legendPanel);
        wrapper.add(legendPanel, BorderLayout.CENTER);
        wrapper.revalidate();
        wrapper.repaint();

        CompactLegendPanel dashboardLegendPanel = host.getDashboardLegendPanel();
        if (dashboardLegendPanel != null) {
            dashboardLegendPanel.setItems(createLegendItems(host.getLegendPanel()));
            dashboardLegendPanel.repaint();
        }
    }

    public static void clearNodeInspector(Host host) {
        host.setSelectedNodeId(null);
        SimulationGraphWorkspace workspace = host.getGraphWorkspace();
        if (workspace != null) {
            workspace.setSelectedSensorId(null);
        }
        host.stopGraphInspectorAutoHide();
        JLabel titleLabel = host.getGraphNodeInspectorTitleLabel();
        if (titleLabel != null) {
            titleLabel.setText("No node selected");
        }
        JTextArea textArea = host.getGraphNodeInspectorTextArea();
        String emptyText = "Click any node in the graph to inspect its live state, services and latest node-level metrics.";
        if (textArea != null) {
            textArea.setText(emptyText);
            textArea.setCaretPosition(0);
        }
        if (workspace != null) {
            workspace.updateSelectedNodeSummary("No node selected", emptyText);
        }
    }

    public static void selectNodeById(Host host, int nodeId) {
        host.setSelectedNodeId(Integer.valueOf(nodeId));
        host.setGraphInspectorExpanded(true);
        host.scheduleGraphInspectorAutoHide();
        refreshSelectedNodeDetails(host);
    }

    public static void refreshSelectedNodeDetails(Host host) {
        Integer selectedNodeId = host.getSelectedNodeId();
        if (selectedNodeId == null) {
            clearNodeInspector(host);
            return;
        }
        Sensor sensor = host.getSensorById(selectedNodeId.intValue());
        if (sensor == null) {
            clearNodeInspector(host);
            return;
        }
        updateNodeInspector(host, sensor);
    }

    private static void updateNodeInspector(Host host, Sensor sensor) {
        populateSensorPropertiesPanel(host, sensor);
        SimulationGraphWorkspace workspace = host.getGraphWorkspace();
        if (workspace != null) {
            workspace.setSelectedSensorId(Integer.valueOf(sensor.id()));
        }

        String title = NodeInspectorHelper.buildNodeTitle(sensor);
        String body = NodeInspectorHelper.buildNodeDetailsText(sensor, host.getCurrentNetwork());

        JLabel titleLabel = host.getGraphNodeInspectorTitleLabel();
        if (titleLabel != null) {
            titleLabel.setText(title);
        }
        JTextArea textArea = host.getGraphNodeInspectorTextArea();
        if (textArea != null) {
            textArea.setText(body);
            textArea.setCaretPosition(0);
        }
        if (workspace != null) {
            workspace.updateSelectedNodeSummary(title, body);
        }
    }

    private static void populateSensorPropertiesPanel(Host host, Sensor sensor) {
        JPanel sensorPropertiesPanel = host.getSensorPropertiesPanel();
        if (sensorPropertiesPanel != null) {
            sensorPropertiesPanel.setVisible(true);
        }
        host.getSensorIdTextField().setText(String.valueOf(sensor.id()));
        host.getXCoordinateTextField().setText(String.format(Locale.US, "%.2f", sensor.getX()));
        host.getYCoordinateTextField().setText(String.format(Locale.US, "%.2f", sensor.getY()));
        host.getNeighborsLabel().setText(sensor.getNeighbors().size() + " Neighbor(s)");
        host.getNeighborsList().setListData(NodeInspectorHelper.buildNeighborIds(sensor));
        host.getNeighborsScrollPane().setViewportView(host.getNeighborsList());
    }
}
