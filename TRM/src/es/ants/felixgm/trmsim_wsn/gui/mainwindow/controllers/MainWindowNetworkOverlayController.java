package es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers;


import es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public final class MainWindowNetworkOverlayController {
    public interface Host {
        JLayeredPane getNetworkOverlayPane();
        void setNetworkOverlayPane(JLayeredPane value);
        JPanel getNetworkPanelContainer();
        NetworkPanel getCurrentNetworkPanel();
        JPanel getGraphNodeInspectorPanel();
        int getGraphInspectorCurrentWidth();
        int getGraphInspectorMargin();
        int getGraphInspectorCollapsedWidth();
        int getGraphInspectorMinHeight();
        int getGraphInspectorMaxHeight();
        void installNetworkPanelSelectionHandler(NetworkPanel panel);
    }

    private MainWindowNetworkOverlayController() {
    }

    public static JPanel createInspectorShellPanel() {
        return new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(19, 28, 42, 172));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.setColor(new Color(255, 255, 255, 44));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
    }

    public static void ensureNetworkOverlayPane(Host host) {
        JLayeredPane overlayPane = host.getNetworkOverlayPane();
        if (overlayPane == null) {
            overlayPane = new JLayeredPane();
            overlayPane.setOpaque(false);
            JLayeredPane finalOverlayPane = overlayPane;
            overlayPane.addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override
                public void componentResized(java.awt.event.ComponentEvent e) {
                    layoutNetworkOverlayComponents(host);
                    finalOverlayPane.revalidate();
                }
            });
            host.setNetworkOverlayPane(overlayPane);
        }
        if (overlayPane.getParent() != host.getNetworkPanelContainer()) {
            host.getNetworkPanelContainer().removeAll();
            host.getNetworkPanelContainer().add(overlayPane, BorderLayout.CENTER);
        }
        layoutNetworkOverlayComponents(host);
    }

    public static void attachNetworkPanelToOverlay(Host host, NetworkPanel panel) {
        if (panel == null) {
            return;
        }
        ensureNetworkOverlayPane(host);
        JLayeredPane overlayPane = host.getNetworkOverlayPane();
        for (java.awt.Component component : overlayPane.getComponentsInLayer(JLayeredPane.DEFAULT_LAYER.intValue())) {
            overlayPane.remove(component);
        }
        if (panel.getParent() != null) {
            panel.getParent().remove(panel);
        }
        overlayPane.add(panel, JLayeredPane.DEFAULT_LAYER);
        overlayPane.moveToBack(panel);
        host.installNetworkPanelSelectionHandler(panel);
        panel.setBackground(Color.white);
        panel.setSize(host.getNetworkPanelContainer().getSize());
        layoutNetworkOverlayComponents(host);
        overlayPane.revalidate();
        overlayPane.repaint();
    }

    public static void layoutNetworkOverlayComponents(Host host) {
        JLayeredPane overlayPane = host.getNetworkOverlayPane();
        if (overlayPane == null) {
            return;
        }
        int width = Math.max(overlayPane.getWidth(), host.getNetworkPanelContainer().getWidth());
        int height = Math.max(overlayPane.getHeight(), host.getNetworkPanelContainer().getHeight());
        if (width <= 0 || height <= 0) {
            return;
        }
        NetworkPanel networkPanel = host.getCurrentNetworkPanel();
        if (networkPanel != null && networkPanel.getParent() == overlayPane) {
            networkPanel.setBounds(0, 0, width, height);
        }
        JPanel inspectorPanel = host.getGraphNodeInspectorPanel();
        if (inspectorPanel != null && inspectorPanel.getParent() == overlayPane) {
            int inspectorHeight = Math.min(host.getGraphInspectorMaxHeight(),
                    Math.max(host.getGraphInspectorMinHeight(), height - (host.getGraphInspectorMargin() * 2)));
            int inspectorWidth = Math.max(host.getGraphInspectorCollapsedWidth(), host.getGraphInspectorCurrentWidth());
            int x = Math.max(0, width - inspectorWidth - host.getGraphInspectorMargin());
            inspectorPanel.setBounds(x, host.getGraphInspectorMargin(), inspectorWidth, inspectorHeight);
            inspectorPanel.revalidate();
        }
    }
}
