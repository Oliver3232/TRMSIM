package es.ants.felixgm.trmsim_wsn.gui;


import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowNetworkOverlayController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowNodeInspectorController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowSimulationControlsController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public final class MainWindowEmbeddedInspectorController {
    private MainWindowEmbeddedInspectorController() {
    }

    static void install(TRMSim_WSN window) {
        MainWindowNetworkOverlayController.ensureNetworkOverlayPane(MainWindowHosts.overlay(window));
        MainWindowNetworkOverlayController.attachNetworkPanelToOverlay(MainWindowHosts.overlay(window), window.networkPanel);
        if (window.graphNodeInspectorPanel != null && window.graphNodeInspectorPanel.getParent() != null) {
            window.graphNodeInspectorPanel.getParent().remove(window.graphNodeInspectorPanel);
        }

        JPanel inspectorPanel = MainWindowNetworkOverlayController.createInspectorShellPanel();
        inspectorPanel.setOpaque(false);
        inspectorPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        inspectorPanel.setPreferredSize(new Dimension(window.GRAPH_INSPECTOR_COLLAPSED_WIDTH, window.GRAPH_INSPECTOR_MIN_HEIGHT));
        window.graphNodeInspectorPanel = inspectorPanel;
        window.graphNodeInspectorCurrentWidth = window.GRAPH_INSPECTOR_COLLAPSED_WIDTH;
        window.graphNodeInspectorTargetWidth = window.GRAPH_INSPECTOR_COLLAPSED_WIDTH;

        JCheckBox pinToggleButton = createInspectorPinToggleButton(window);
        window.graphInspectorPinToggleButton = pinToggleButton;

        JPanel header = new JPanel(new BorderLayout(0, 6));
        header.setOpaque(false);
        JPanel headerTop = new JPanel(new BorderLayout(8, 0));
        headerTop.setOpaque(false);
        JLabel badgeLabel = new JLabel("NODE INSPECTOR");
        badgeLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        badgeLabel.setForeground(new Color(193, 213, 240));
        headerTop.add(badgeLabel, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("No node selected");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        window.graphNodeInspectorTitleLabel = titleLabel;

        header.add(headerTop, BorderLayout.NORTH);
        header.add(titleLabel, BorderLayout.CENTER);
        resetInspectorControlReferences(window);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(false);
        textArea.setBackground(new Color(0, 0, 0, 0));
        textArea.setForeground(new Color(236, 242, 252));
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        window.graphNodeInspectorTextArea = textArea;

        JScrollPane inspectorScrollPane = new JScrollPane(textArea);
        inspectorScrollPane.setOpaque(false);
        inspectorScrollPane.getViewport().setOpaque(false);
        inspectorScrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 32), 1, true),
                new EmptyBorder(0, 0, 0, 0)));

        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.X_AXIS));
        JButton clearSelectionButton = new JButton("Clear Selection");
        clearSelectionButton.setFocusable(false);
        clearSelectionButton.addActionListener(evt -> MainWindowNodeInspectorController.clearNodeInspector(MainWindowHosts.nodeInspector(window)));
        footer.add(clearSelectionButton);

        JPanel drawerContentPanel = new JPanel(new BorderLayout(0, 10));
        drawerContentPanel.setOpaque(false);
        drawerContentPanel.add(header, BorderLayout.NORTH);
        drawerContentPanel.add(inspectorScrollPane, BorderLayout.CENTER);
        drawerContentPanel.add(footer, BorderLayout.SOUTH);

        JPanel handlePanel = new JPanel(new BorderLayout());
        handlePanel.setOpaque(false);
        JPanel handleStack = new JPanel();
        handleStack.setOpaque(false);
        handleStack.setLayout(new BoxLayout(handleStack, BoxLayout.Y_AXIS));
        pinToggleButton.setAlignmentX(0.5f);
        handleStack.add(Box.createVerticalStrut(8));
        handleStack.add(pinToggleButton);
        handleStack.add(Box.createVerticalGlue());
        handlePanel.add(handleStack, BorderLayout.CENTER);

        inspectorPanel.add(handlePanel, BorderLayout.WEST);
        inspectorPanel.add(drawerContentPanel, BorderLayout.CENTER);
        inspectorPanel.putClientProperty("drawerContent", drawerContentPanel);
        window.networkOverlayPane.add(inspectorPanel, JLayeredPane.PALETTE_LAYER);

        java.awt.event.MouseAdapter inspectorHoverAdapter = createInspectorHoverAdapter(window);
        inspectorPanel.addMouseListener(inspectorHoverAdapter);
        handlePanel.addMouseListener(inspectorHoverAdapter);
        drawerContentPanel.addMouseListener(inspectorHoverAdapter);
        MainWindowNetworkOverlayController.layoutNetworkOverlayComponents(MainWindowHosts.overlay(window));
        window.networkOverlayPane.revalidate();
        window.networkOverlayPane.repaint();
        MainWindowSimulationControlsController.updateRunSimulationsControls(MainWindowHosts.simulationControls(window));
        window.setGraphInspectorExpanded(window.graphInspectorPinned);
    }

    private static JCheckBox createInspectorPinToggleButton(TRMSim_WSN window) {
        JCheckBox pinToggleButton = new JCheckBox();
        pinToggleButton.setFocusable(false);
        pinToggleButton.setOpaque(false);
        pinToggleButton.setForeground(new Color(220, 245, 255));
        pinToggleButton.setToolTipText("Pin node inspector");
        pinToggleButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        pinToggleButton.setIconTextGap(0);
        pinToggleButton.setSelected(window.graphInspectorPinned);
        pinToggleButton.addActionListener(evt -> {
            window.graphInspectorPinned = pinToggleButton.isSelected();
            MainWindowRuntimeSupport.stopGraphInspectorAutoHide(window);
            window.setGraphInspectorExpanded(window.graphInspectorPinned);
            if (!window.graphInspectorPinned) {
                scheduleGraphInspectorCollapseCheck(window);
            }
        });
        return pinToggleButton;
    }

    private static void resetInspectorControlReferences(TRMSim_WSN window) {
        window.graphInspectorSimulationStateLabel = null;
        window.graphInspectorPauseResumeButton = null;
        window.graphInspectorStopButton = null;
        window.graphInspectorShowIdsCheckBox = null;
        window.graphInspectorShowLinksCheckBox = null;
        window.graphInspectorShowRangesCheckBox = null;
        window.graphInspectorShowGridCheckBox = null;
        window.graphInspectorDelaySlider = null;
        window.graphInspectorLegendPanel = null;
        window.graphInspectorLegendWrapper = null;
    }

    private static java.awt.event.MouseAdapter createInspectorHoverAdapter(TRMSim_WSN window) {
        return new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                MainWindowRuntimeSupport.stopGraphInspectorAutoHide(window);
                window.setGraphInspectorExpanded(true);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                scheduleGraphInspectorCollapseCheck(window);
            }
        };
    }

    static void scheduleGraphInspectorCollapseCheck(TRMSim_WSN window) {
        Timer collapseTimer = new Timer(140, evt -> {
            if (window.graphNodeInspectorPanel == null || window.graphInspectorPinned) {
                return;
            }
            try {
                Point pointer = java.awt.MouseInfo.getPointerInfo().getLocation();
                Point panelLocation = window.graphNodeInspectorPanel.getLocationOnScreen();
                int relX = pointer.x - panelLocation.x;
                int relY = pointer.y - panelLocation.y;
                if (!window.graphNodeInspectorPanel.contains(relX, relY)) {
                    MainWindowRuntimeSupport.stopGraphInspectorAutoHide(window);
                    window.setGraphInspectorExpanded(false);
                }
            } catch (Exception ignored) {
            }
        });
        collapseTimer.setRepeats(false);
        collapseTimer.start();
    }

    static void setGraphInspectorExpanded(TRMSim_WSN window, boolean expanded) {
        if (window.graphNodeInspectorPanel == null) {
            return;
        }
        boolean shouldExpand = expanded || window.graphInspectorPinned;
        window.graphNodeInspectorTargetWidth =
                shouldExpand ? window.GRAPH_INSPECTOR_EXPANDED_WIDTH : window.GRAPH_INSPECTOR_COLLAPSED_WIDTH;
        Object contentObj = window.graphNodeInspectorPanel.getClientProperty("drawerContent");
        if (contentObj instanceof JComponent) {
            ((JComponent) contentObj).setVisible(
                    shouldExpand || window.graphNodeInspectorCurrentWidth > (window.GRAPH_INSPECTOR_COLLAPSED_WIDTH + 10));
        }
        if (window.graphNodeInspectorAnimator == null) {
            window.graphNodeInspectorAnimator = new Timer(16, evt -> {
                int delta = window.graphNodeInspectorTargetWidth - window.graphNodeInspectorCurrentWidth;
                if (Math.abs(delta) <= 1) {
                    window.graphNodeInspectorCurrentWidth = window.graphNodeInspectorTargetWidth;
                } else {
                    int step = Math.max(1, Math.abs(delta) / 4);
                    window.graphNodeInspectorCurrentWidth =
                            window.graphNodeInspectorCurrentWidth + ((delta > 0) ? step : -step);
                }
                window.graphNodeInspectorPanel.setPreferredSize(
                        new Dimension(window.graphNodeInspectorCurrentWidth, window.GRAPH_INSPECTOR_MIN_HEIGHT));
                Object drawerContentObj = window.graphNodeInspectorPanel.getClientProperty("drawerContent");
                if (drawerContentObj instanceof JComponent) {
                    ((JComponent) drawerContentObj).setVisible(
                            window.graphNodeInspectorCurrentWidth > (window.GRAPH_INSPECTOR_COLLAPSED_WIDTH + 10));
                }
                MainWindowNetworkOverlayController.layoutNetworkOverlayComponents(MainWindowHosts.overlay(window));
                if (window.networkOverlayPane != null) {
                    window.networkOverlayPane.revalidate();
                    window.networkOverlayPane.repaint();
                }
                if (window.graphNodeInspectorCurrentWidth == window.graphNodeInspectorTargetWidth) {
                    window.graphNodeInspectorAnimator.stop();
                }
            });
        }
        if (!window.graphNodeInspectorAnimator.isRunning()) {
            window.graphNodeInspectorAnimator.start();
        }
    }
}
