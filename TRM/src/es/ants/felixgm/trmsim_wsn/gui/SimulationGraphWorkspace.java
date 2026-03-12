package es.ants.felixgm.trmsim_wsn.gui;

import es.ants.felixgm.trmsim_wsn.gui.networkpanels.JavaFXNetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel;
import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Service;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

final class SimulationGraphWorkspace {
    interface PanelRenderer {
        void render(NetworkPanel panel);
    }

    interface NodeSelectionListener {
        void onNodeSelected(Integer nodeId);
    }

    interface SimulationControlListener {
        void onPauseResumeRequested();
        void onStopRequested();
    }

    interface DisplayControlListener {
        void onShowIdsChanged(boolean selected);
        void onShowLinksChanged(boolean selected);
        void onShowRangesChanged(boolean selected);
        void onShowGridChanged(boolean selected);
        void onDelayChanged(int value);
    }

    private final JComboBox<String> visualThemeComboBox = new JComboBox<String>();
    private final JComboBox<String> cameraPresetComboBox = new JComboBox<String>();
    private final JCheckBox enable3DNavigationCheckBox = new JCheckBox("3D navigation");
    private final JButton fullscreenGraphButton = new JButton();
    private final PanelRenderer renderer;
    private NetworkPanel mainNetworkPanel;
    private NodeSelectionListener nodeSelectionListener;
    private SimulationControlListener simulationControlListener;
    private DisplayControlListener displayControlListener;

    private JFrame fullscreenFrame;
    private JavaFXNetworkPanel fullscreenNetworkPanel;
    private JPanel fullscreenDrawerPanel;
    private javax.swing.Timer fullscreenDrawerAnimator;
    private int fullscreenDrawerCurrentWidth = 16;
    private int fullscreenDrawerTargetWidth = 16;
    private JLabel fullscreenInspectorTitleLabel;
    private javax.swing.JTextArea fullscreenInspectorTextArea;
    private JButton fullscreenPauseResumeButton;
    private JButton fullscreenStopButton;
    private JLabel fullscreenSimulationStateLabel;
    private JCheckBox fullscreenPinDrawerCheckBox;
    private CompactLegendPanel fullscreenLegendPanel;
    private JCheckBox fullscreenShowIdsCheckBox;
    private JCheckBox fullscreenShowLinksCheckBox;
    private JCheckBox fullscreenShowRangesCheckBox;
    private JCheckBox fullscreenShowGridCheckBox;
    private javax.swing.JSlider fullscreenDelaySlider;
    private String currentInspectorTitle = "No node selected";
    private String currentInspectorBody = "Click any node in the graph to inspect its live state and exported metrics.";
    private String currentSimulationStateLabel = "Idle";
    private String currentPauseResumeLabel = "Pause";
    private boolean currentShowIds;
    private boolean currentShowLinks;
    private boolean currentShowRanges;
    private boolean currentShowGrid;
    private int currentDelayValue;
    private int currentDelayMin;
    private int currentDelayMax = 100;
    private boolean drawerPinned = false;

    SimulationGraphWorkspace(PanelRenderer renderer) {
        this.renderer = renderer;
    }

    JComboBox<String> getVisualThemeComboBox() {
        return visualThemeComboBox;
    }

    JComboBox<String> getCameraPresetComboBox() {
        return cameraPresetComboBox;
    }

    JCheckBox getEnable3DNavigationCheckBox() {
        return enable3DNavigationCheckBox;
    }

    JButton getFullscreenGraphButton() {
        return fullscreenGraphButton;
    }

    void setNodeSelectionListener(NodeSelectionListener nodeSelectionListener) {
        this.nodeSelectionListener = nodeSelectionListener;
    }

    void setSimulationControlListener(SimulationControlListener simulationControlListener) {
        this.simulationControlListener = simulationControlListener;
    }

    void setDisplayControlListener(DisplayControlListener displayControlListener) {
        this.displayControlListener = displayControlListener;
    }

    void updateSelectedNodeSummary(String title, String body) {
        currentInspectorTitle = (title == null || title.trim().isEmpty()) ? "No node selected" : title;
        currentInspectorBody = (body == null || body.trim().isEmpty())
                ? "Click any node in the graph to inspect its live state and exported metrics."
                : body;
        if (fullscreenInspectorTitleLabel != null) {
            fullscreenInspectorTitleLabel.setText(currentInspectorTitle);
        }
        if (fullscreenInspectorTextArea != null) {
            fullscreenInspectorTextArea.setText(currentInspectorBody);
            fullscreenInspectorTextArea.setCaretPosition(0);
        }
    }

    void updateSimulationControlsState(String stateLabel, String runLabel, String pauseResumeLabel,
                                       boolean canRun, boolean canPauseResume, boolean canStop) {
        currentSimulationStateLabel = stateLabel;
        currentPauseResumeLabel = pauseResumeLabel;
        if (fullscreenSimulationStateLabel != null) {
            fullscreenSimulationStateLabel.setText(stateLabel);
        }
        if (fullscreenPauseResumeButton != null) {
            fullscreenPauseResumeButton.setText(canRun ? runLabel : pauseResumeLabel);
            fullscreenPauseResumeButton.setEnabled(canRun || canPauseResume);
        }
        if (fullscreenStopButton != null) {
            fullscreenStopButton.setEnabled(canStop);
        }
    }

    void setSelectedSensorId(Integer selectedSensorId) {
        if (mainNetworkPanel != null) {
            mainNetworkPanel.setSelectedSensorId(selectedSensorId);
        }
        if (fullscreenNetworkPanel != null) {
            fullscreenNetworkPanel.setSelectedSensorId(selectedSensorId);
        }
    }

    void updateDisplayControlsState(boolean showIds, boolean showLinks, boolean showRanges, boolean showGrid,
                                    int delayValue, int delayMin, int delayMax) {
        currentShowIds = showIds;
        currentShowLinks = showLinks;
        currentShowRanges = showRanges;
        currentShowGrid = showGrid;
        currentDelayValue = delayValue;
        currentDelayMin = delayMin;
        currentDelayMax = delayMax;
        if (fullscreenShowIdsCheckBox != null) {
            fullscreenShowIdsCheckBox.setSelected(showIds);
        }
        if (fullscreenShowLinksCheckBox != null) {
            fullscreenShowLinksCheckBox.setSelected(showLinks);
        }
        if (fullscreenShowRangesCheckBox != null) {
            fullscreenShowRangesCheckBox.setSelected(showRanges);
        }
        if (fullscreenShowGridCheckBox != null) {
            fullscreenShowGridCheckBox.setSelected(showGrid);
        }
        if (fullscreenDelaySlider != null) {
            fullscreenDelaySlider.setMinimum(delayMin);
            fullscreenDelaySlider.setMaximum(delayMax);
            fullscreenDelaySlider.setValue(delayValue);
        }
    }

    void setFullscreenLegendItems(java.util.List<MiniLegendPanel.Item> items) {
        fullscreenLegendPanel = new CompactLegendPanel();
        fullscreenLegendPanel.setItems(items);
    }

    void initializeControls() {
        visualThemeComboBox.setModel(new javax.swing.DefaultComboBoxModel<String>(
                new String[]{"Futuristic", "Classic", "Wireframe"}));
        visualThemeComboBox.setSelectedItem("Futuristic");
        visualThemeComboBox.addActionListener(e -> applyVisualizationControlsToPanels(null));

        enable3DNavigationCheckBox.setSelected(false);
        enable3DNavigationCheckBox.setOpaque(false);
        enable3DNavigationCheckBox.addActionListener(e -> applyVisualizationControlsToPanels(null));

        cameraPresetComboBox.setModel(new javax.swing.DefaultComboBoxModel<String>(
                new String[]{"Isometric", "Top", "Front"}));
        cameraPresetComboBox.setSelectedItem("Isometric");
        cameraPresetComboBox.addActionListener(e -> applyVisualizationControlsToPanels(null));
        cameraPresetComboBox.setEnabled(enable3DNavigationCheckBox.isSelected());

        fullscreenGraphButton.setText("Open Fullscreen");
        fullscreenGraphButton.addActionListener(e -> toggleFullscreenGraphWindow());
    }

    void applyVisualizationControlsToPanels(NetworkPanel mainPanel) {
        if (mainPanel != null) {
            mainNetworkPanel = mainPanel;
        }
        NetworkPanel panelToApply = (mainPanel != null) ? mainPanel : mainNetworkPanel;
        if (panelToApply instanceof JavaFXNetworkPanel) {
            applyVisualizationControls((JavaFXNetworkPanel) panelToApply);
        }
        if (fullscreenNetworkPanel != null) {
            applyVisualizationControls(fullscreenNetworkPanel);
        }
    }

    void renderOnFullscreen(Network network, Service requiredService, double radioRange,
                            boolean showRanges, boolean showLinks, boolean showIds, boolean showGrid) {
        if (fullscreenNetworkPanel == null || !fullscreenNetworkPanel.isShowing()) {
            return;
        }
        try {
            fullscreenNetworkPanel.paintNetwork(network, requiredService, radioRange, showRanges, showLinks, showIds, showGrid);
        } catch (Exception ignored) {}
    }

    private void applyVisualizationControls(JavaFXNetworkPanel panel) {
        boolean enable3D = enable3DNavigationCheckBox.isSelected();
        cameraPresetComboBox.setEnabled(enable3D);
        panel.setVisualTheme((String) visualThemeComboBox.getSelectedItem());
        panel.set3DNavigationEnabled(enable3D);
        if (enable3D) {
            panel.applyCameraPreset((String) cameraPresetComboBox.getSelectedItem());
        }
    }

    private void toggleFullscreenGraphWindow() {
        if (fullscreenFrame != null && fullscreenFrame.isShowing()) {
            closeFullscreenGraphWindow();
            return;
        }

        fullscreenNetworkPanel = new JavaFXNetworkPanel();
        fullscreenNetworkPanel.setBackground(Color.white);
        fullscreenNetworkPanel.setSensorSelectionListener(sensor -> {
            if (nodeSelectionListener != null) {
                nodeSelectionListener.onNodeSelected(sensor == null ? null : Integer.valueOf(sensor.id()));
            }
        });
        if (mainNetworkPanel != null) {
            fullscreenNetworkPanel.setSelectedSensorId(mainNetworkPanel.getSelectedSensorId());
        }
        applyVisualizationControls(fullscreenNetworkPanel);

        fullscreenFrame = new JFrame("TRMSim-WSN Graph");
        fullscreenFrame.setUndecorated(true);
        fullscreenFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JPanel fullscreenContainer = new JPanel(new BorderLayout());
        fullscreenContainer.add(fullscreenNetworkPanel, BorderLayout.CENTER);
        fullscreenDrawerPanel = buildFullscreenHoverDrawer();
        fullscreenContainer.add(fullscreenDrawerPanel, BorderLayout.EAST);
        fullscreenFrame.setContentPane(fullscreenContainer);
        JPopupMenu fullscreenToolbarPopup = buildFullscreenToolbarPopup();
        fullscreenNetworkPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            private void maybeShowPopup(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    fullscreenToolbarPopup.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                maybeShowPopup(e);
            }
        });
        fullscreenNetworkPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                if (fullscreenDrawerPanel == null) {
                    return;
                }
                int threshold = Math.max(24, fullscreenNetworkPanel.getWidth() - 24);
                setFullscreenDrawerExpanded(e.getX() >= threshold);
            }
        });
        fullscreenFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                fullscreenFrame = null;
                fullscreenNetworkPanel = null;
                fullscreenDrawerPanel = null;
                fullscreenGraphButton.setText("Open Fullscreen");
            }
        });
        fullscreenFrame.getRootPane().registerKeyboardAction(
                e -> closeFullscreenGraphWindow(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        fullscreenFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        fullscreenFrame.setVisible(true);
        renderer.render(fullscreenNetworkPanel);
        fullscreenGraphButton.setText("Close Fullscreen");
    }

    private void closeFullscreenGraphWindow() {
        if (fullscreenFrame != null) {
            JFrame frame = fullscreenFrame;
            fullscreenFrame = null;
            fullscreenNetworkPanel = null;
            fullscreenDrawerPanel = null;
            if (fullscreenDrawerAnimator != null) {
                fullscreenDrawerAnimator.stop();
            }
            frame.dispose();
        }
        fullscreenGraphButton.setText("Open Fullscreen");
    }

    private JPanel buildFullscreenHoverDrawer() {
        JPanel drawer = new JPanel(new BorderLayout());
        drawer.setBorder(new EmptyBorder(10, 8, 10, 8));
        drawer.setOpaque(true);
        drawer.setBackground(new Color(20, 29, 44, 220));
        drawer.setPreferredSize(new Dimension(16, 0));
        fullscreenDrawerCurrentWidth = 16;
        fullscreenDrawerTargetWidth = 16;

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Graph Controls");
        title.setForeground(new Color(220, 245, 255));
        title.setAlignmentX(0.0f);
        content.add(title);
        content.add(Box.createVerticalStrut(8));

        JLabel themeLabel = new JLabel("Theme");
        themeLabel.setForeground(new Color(200, 232, 255));
        themeLabel.setAlignmentX(0.0f);
        content.add(themeLabel);

        JComboBox<String> themeCombo = new JComboBox<String>(new String[]{"Futuristic", "Classic", "Wireframe"});
        themeCombo.setSelectedItem(visualThemeComboBox.getSelectedItem());
        themeCombo.setMaximumSize(new Dimension(230, 26));
        themeCombo.setAlignmentX(0.0f);
        themeCombo.addActionListener(e -> {
            visualThemeComboBox.setSelectedItem(themeCombo.getSelectedItem());
            applyVisualizationControlsToPanels(null);
        });
        content.add(themeCombo);
        content.add(Box.createVerticalStrut(8));

        JLabel presetLabel = new JLabel("3D View");
        presetLabel.setForeground(new Color(200, 232, 255));
        presetLabel.setAlignmentX(0.0f);
        content.add(presetLabel);

        JComboBox<String> presetCombo = new JComboBox<String>(new String[]{"Isometric", "Top", "Front"});
        presetCombo.setSelectedItem(cameraPresetComboBox.getSelectedItem());
        presetCombo.setMaximumSize(new Dimension(230, 26));
        presetCombo.setAlignmentX(0.0f);
        presetCombo.setEnabled(enable3DNavigationCheckBox.isSelected());
        presetCombo.addActionListener(e -> {
            cameraPresetComboBox.setSelectedItem(presetCombo.getSelectedItem());
            applyVisualizationControlsToPanels(null);
        });
        content.add(presetCombo);
        content.add(Box.createVerticalStrut(8));

        JCheckBox enable3D = new JCheckBox("Enable 3D navigation");
        enable3D.setOpaque(false);
        enable3D.setForeground(new Color(220, 245, 255));
        enable3D.setSelected(enable3DNavigationCheckBox.isSelected());
        enable3D.setAlignmentX(0.0f);
        enable3D.addActionListener(e -> {
            enable3DNavigationCheckBox.setSelected(enable3D.isSelected());
            presetCombo.setEnabled(enable3D.isSelected());
            applyVisualizationControlsToPanels(null);
        });
        content.add(enable3D);
        content.add(Box.createVerticalStrut(10));

        JButton closeButton = new JButton("Close Fullscreen");
        closeButton.setAlignmentX(0.0f);
        closeButton.addActionListener(e -> closeFullscreenGraphWindow());
        content.add(closeButton);
        content.add(Box.createVerticalStrut(14));

        JLabel liveLabel = new JLabel("Live Simulation");
        liveLabel.setForeground(new Color(200, 232, 255));
        liveLabel.setAlignmentX(0.0f);
        content.add(liveLabel);
        content.add(Box.createVerticalStrut(6));

        fullscreenSimulationStateLabel = new JLabel(currentSimulationStateLabel);
        fullscreenSimulationStateLabel.setForeground(new Color(240, 249, 255));
        fullscreenSimulationStateLabel.setAlignmentX(0.0f);
        content.add(fullscreenSimulationStateLabel);
        content.add(Box.createVerticalStrut(6));

        JPanel simulationButtonsPanel = new JPanel();
        simulationButtonsPanel.setOpaque(false);
        simulationButtonsPanel.setLayout(new BoxLayout(simulationButtonsPanel, BoxLayout.X_AXIS));
        fullscreenPauseResumeButton = new JButton(currentPauseResumeLabel);
        fullscreenPauseResumeButton.addActionListener(e -> {
            if (simulationControlListener != null) {
                simulationControlListener.onPauseResumeRequested();
            }
        });
        fullscreenStopButton = new JButton("Stop Simulations");
        fullscreenStopButton.addActionListener(e -> {
            if (simulationControlListener != null) {
                simulationControlListener.onStopRequested();
            }
        });
        simulationButtonsPanel.add(fullscreenPauseResumeButton);
        simulationButtonsPanel.add(Box.createHorizontalStrut(8));
        simulationButtonsPanel.add(fullscreenStopButton);
        simulationButtonsPanel.setAlignmentX(0.0f);
        content.add(simulationButtonsPanel);
        content.add(Box.createVerticalStrut(12));

        JLabel visibilityLabel = new JLabel("Display");
        visibilityLabel.setForeground(new Color(200, 232, 255));
        visibilityLabel.setAlignmentX(0.0f);
        content.add(visibilityLabel);
        content.add(Box.createVerticalStrut(6));

        fullscreenShowIdsCheckBox = createFullscreenToggle("Show IDs", currentShowIds, selected -> {
            if (displayControlListener != null) {
                displayControlListener.onShowIdsChanged(selected);
            }
        });
        content.add(fullscreenShowIdsCheckBox);

        fullscreenShowLinksCheckBox = createFullscreenToggle("Show links", currentShowLinks, selected -> {
            if (displayControlListener != null) {
                displayControlListener.onShowLinksChanged(selected);
            }
        });
        content.add(fullscreenShowLinksCheckBox);

        fullscreenShowRangesCheckBox = createFullscreenToggle("Show ranges", currentShowRanges, selected -> {
            if (displayControlListener != null) {
                displayControlListener.onShowRangesChanged(selected);
            }
        });
        content.add(fullscreenShowRangesCheckBox);

        fullscreenShowGridCheckBox = createFullscreenToggle("Show grid", currentShowGrid, selected -> {
            if (displayControlListener != null) {
                displayControlListener.onShowGridChanged(selected);
            }
        });
        content.add(fullscreenShowGridCheckBox);
        content.add(Box.createVerticalStrut(8));

        JLabel delayLabel = new JLabel("Delay");
        delayLabel.setForeground(new Color(200, 232, 255));
        delayLabel.setAlignmentX(0.0f);
        content.add(delayLabel);

        fullscreenDelaySlider = new javax.swing.JSlider(currentDelayMin, currentDelayMax, currentDelayValue);
        fullscreenDelaySlider.setOpaque(false);
        fullscreenDelaySlider.setAlignmentX(0.0f);
        fullscreenDelaySlider.addChangeListener(e -> {
            if (displayControlListener != null) {
                displayControlListener.onDelayChanged(fullscreenDelaySlider.getValue());
            }
        });
        content.add(fullscreenDelaySlider);
        content.add(Box.createVerticalStrut(12));

        fullscreenPinDrawerCheckBox = new JCheckBox("Pin drawer");
        fullscreenPinDrawerCheckBox.setOpaque(false);
        fullscreenPinDrawerCheckBox.setForeground(new Color(220, 245, 255));
        fullscreenPinDrawerCheckBox.setSelected(drawerPinned);
        fullscreenPinDrawerCheckBox.setAlignmentX(0.0f);
        fullscreenPinDrawerCheckBox.addActionListener(e -> {
            drawerPinned = fullscreenPinDrawerCheckBox.isSelected();
            setFullscreenDrawerExpanded(drawerPinned);
        });
        content.add(fullscreenPinDrawerCheckBox);
        content.add(Box.createVerticalStrut(12));

        JLabel legendLabel = new JLabel("Mini Legend");
        legendLabel.setForeground(new Color(200, 232, 255));
        legendLabel.setAlignmentX(0.0f);
        content.add(legendLabel);
        content.add(Box.createVerticalStrut(6));
        JPanel legendHolder = new JPanel(new BorderLayout());
        legendHolder.setOpaque(true);
        legendHolder.setBackground(new Color(9, 14, 24, 220));
        legendHolder.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(72, 122, 175, 120), 1, true),
                new EmptyBorder(8, 8, 8, 8)));
        if (fullscreenLegendPanel != null) {
            legendHolder.add(fullscreenLegendPanel, BorderLayout.CENTER);
            fullscreenLegendPanel.setPreferredSize(new Dimension(248, 96));
        }
        legendHolder.setMaximumSize(new Dimension(264, 156));
        legendHolder.setPreferredSize(new Dimension(264, 116));
        legendHolder.setAlignmentX(0.0f);
        content.add(legendHolder);
        content.add(Box.createVerticalStrut(14));

        JLabel inspectorLabel = new JLabel("Selected Node");
        inspectorLabel.setForeground(new Color(200, 232, 255));
        inspectorLabel.setAlignmentX(0.0f);
        content.add(inspectorLabel);
        content.add(Box.createVerticalStrut(6));

        fullscreenInspectorTitleLabel = new JLabel(currentInspectorTitle);
        fullscreenInspectorTitleLabel.setForeground(new Color(240, 249, 255));
        fullscreenInspectorTitleLabel.setAlignmentX(0.0f);
        content.add(fullscreenInspectorTitleLabel);
        content.add(Box.createVerticalStrut(6));

        fullscreenInspectorTextArea = new javax.swing.JTextArea(currentInspectorBody);
        fullscreenInspectorTextArea.setEditable(false);
        fullscreenInspectorTextArea.setLineWrap(true);
        fullscreenInspectorTextArea.setWrapStyleWord(true);
        fullscreenInspectorTextArea.setOpaque(true);
        fullscreenInspectorTextArea.setBackground(new Color(9, 14, 24, 220));
        fullscreenInspectorTextArea.setForeground(new Color(214, 236, 252));
        fullscreenInspectorTextArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(72, 122, 175, 120), 1, true),
                new EmptyBorder(10, 10, 10, 10)));
        fullscreenInspectorTextArea.setMaximumSize(new Dimension(230, 260));
        fullscreenInspectorTextArea.setAlignmentX(0.0f);
        JScrollPane inspectorScrollPane = new JScrollPane(fullscreenInspectorTextArea);
        inspectorScrollPane.setAlignmentX(0.0f);
        inspectorScrollPane.setMaximumSize(new Dimension(230, 260));
        inspectorScrollPane.setPreferredSize(new Dimension(230, 260));
        inspectorScrollPane.setBorder(BorderFactory.createEmptyBorder());
        content.add(inspectorScrollPane);

        drawer.add(content, BorderLayout.NORTH);
        content.setVisible(false);
        javax.swing.Timer collapseTimer = new javax.swing.Timer(140, evt -> {
                if (fullscreenDrawerPanel == null) {
                    return;
                }
                try {
                    if (drawerPinned) {
                        return;
                    }
                    Point pointer = java.awt.MouseInfo.getPointerInfo().getLocation();
                    Point drawerLoc = fullscreenDrawerPanel.getLocationOnScreen();
                    int relX = pointer.x - drawerLoc.x;
                int relY = pointer.y - drawerLoc.y;
                if (!fullscreenDrawerPanel.contains(relX, relY)) {
                    setFullscreenDrawerExpanded(false);
                }
            } catch (Exception ignored) {}
        });
        collapseTimer.setRepeats(false);

        drawer.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                collapseTimer.stop();
                setFullscreenDrawerExpanded(true);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                collapseTimer.restart();
            }
        });

        drawer.putClientProperty("drawerContent", content);
        boolean isIdle = "Idle".equalsIgnoreCase(currentSimulationStateLabel);
        updateSimulationControlsState(
                currentSimulationStateLabel,
                "Run Simulations",
                currentPauseResumeLabel,
                isIdle,
                !isIdle,
                !isIdle);
        setFullscreenDrawerExpanded(drawerPinned);
        return drawer;
    }

    private void setFullscreenDrawerExpanded(boolean expanded) {
        if (fullscreenDrawerPanel == null) {
            return;
        }
        boolean shouldExpand = expanded || drawerPinned;
        fullscreenDrawerTargetWidth = shouldExpand ? 300 : 16;
        Object contentObj = fullscreenDrawerPanel.getClientProperty("drawerContent");
        if (contentObj instanceof JComponent) {
            ((JComponent) contentObj).setVisible(shouldExpand || fullscreenDrawerCurrentWidth > 24);
        }
        if (fullscreenDrawerAnimator == null) {
            fullscreenDrawerAnimator = new javax.swing.Timer(16, e -> {
                if (fullscreenDrawerPanel == null) {
                    fullscreenDrawerAnimator.stop();
                    return;
                }
                int delta = fullscreenDrawerTargetWidth - fullscreenDrawerCurrentWidth;
                if (Math.abs(delta) <= 1) {
                    fullscreenDrawerCurrentWidth = fullscreenDrawerTargetWidth;
                } else {
                    int step = Math.max(1, Math.abs(delta) / 4);
                    fullscreenDrawerCurrentWidth += (delta > 0) ? step : -step;
                }

                fullscreenDrawerPanel.setPreferredSize(new Dimension(fullscreenDrawerCurrentWidth, 0));
                Object drawerContentObj = fullscreenDrawerPanel.getClientProperty("drawerContent");
                if (drawerContentObj instanceof JComponent) {
                    ((JComponent) drawerContentObj).setVisible(fullscreenDrawerCurrentWidth > 32);
                }
                fullscreenDrawerPanel.revalidate();
                fullscreenDrawerPanel.repaint();
                if (fullscreenFrame != null) {
                    fullscreenFrame.revalidate();
                    fullscreenFrame.repaint();
                }

                if (fullscreenDrawerCurrentWidth == fullscreenDrawerTargetWidth) {
                    fullscreenDrawerAnimator.stop();
                }
            });
            fullscreenDrawerAnimator.setRepeats(true);
        }
        if (!fullscreenDrawerAnimator.isRunning()) {
            fullscreenDrawerAnimator.start();
        }
    }

    private JPopupMenu buildFullscreenToolbarPopup() {
        JPopupMenu popup = new JPopupMenu("Graph Controls");

        JMenu themeMenu = new JMenu("Theme");
        ButtonGroup themeGroup = new ButtonGroup();
        addThemeItem(themeMenu, themeGroup, "Futuristic");
        addThemeItem(themeMenu, themeGroup, "Classic");
        addThemeItem(themeMenu, themeGroup, "Wireframe");
        popup.add(themeMenu);

        JMenu presetMenu = new JMenu("3D View");
        presetMenu.setEnabled(enable3DNavigationCheckBox.isSelected());
        ButtonGroup presetGroup = new ButtonGroup();
        addPresetItem(presetMenu, presetGroup, "Isometric");
        addPresetItem(presetMenu, presetGroup, "Top");
        addPresetItem(presetMenu, presetGroup, "Front");
        popup.add(presetMenu);

        JCheckBoxMenuItem enable3DItem = new JCheckBoxMenuItem("Enable 3D navigation");
        enable3DItem.setSelected(enable3DNavigationCheckBox.isSelected());
        enable3DItem.addActionListener(e -> {
            enable3DNavigationCheckBox.setSelected(enable3DItem.isSelected());
            presetMenu.setEnabled(enable3DItem.isSelected());
            applyVisualizationControlsToPanels(null);
        });
        popup.add(enable3DItem);
        popup.addSeparator();

        JMenuItem closeItem = new JMenuItem("Close Fullscreen");
        closeItem.addActionListener(e -> closeFullscreenGraphWindow());
        popup.add(closeItem);

        return popup;
    }

    private void addThemeItem(JMenu menu, ButtonGroup group, String themeName) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(themeName);
        item.setSelected(themeName.equals(visualThemeComboBox.getSelectedItem()));
        item.addActionListener(e -> {
            visualThemeComboBox.setSelectedItem(themeName);
            applyVisualizationControlsToPanels(null);
        });
        group.add(item);
        menu.add(item);
    }

    private void addPresetItem(JMenu menu, ButtonGroup group, String presetName) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(presetName);
        item.setSelected(presetName.equals(cameraPresetComboBox.getSelectedItem()));
        item.setEnabled(enable3DNavigationCheckBox.isSelected());
        item.addActionListener(e -> {
            cameraPresetComboBox.setSelectedItem(presetName);
            applyVisualizationControlsToPanels(null);
        });
        group.add(item);
        menu.add(item);
    }

    private JCheckBox createFullscreenToggle(String label, boolean selected, Consumer<Boolean> consumer) {
        JCheckBox checkBox = new JCheckBox(label);
        checkBox.setOpaque(false);
        checkBox.setForeground(new Color(220, 245, 255));
        checkBox.setAlignmentX(0.0f);
        checkBox.setSelected(selected);
        checkBox.addActionListener(e -> consumer.accept(checkBox.isSelected()));
        return checkBox;
    }
}
