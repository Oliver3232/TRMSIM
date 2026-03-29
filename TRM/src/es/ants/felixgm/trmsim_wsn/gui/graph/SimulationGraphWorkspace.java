package es.ants.felixgm.trmsim_wsn.gui.graph;

import es.ants.felixgm.trmsim_wsn.gui.layout.CompactLegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.layout.MiniLegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.JavaFXNetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel;
import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Service;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public final class SimulationGraphWorkspace {
    public interface PanelRenderer {
        void render(NetworkPanel panel);
    }

    public interface NodeSelectionListener {
        void onNodeSelected(Integer nodeId);
    }

    public interface SimulationControlListener {
        void onPauseResumeRequested();
        void onStopRequested();
    }

    public interface DisplayControlListener {
        void onShowIdsChanged(boolean selected);
        void onShowLinksChanged(boolean selected);
        void onShowRangesChanged(boolean selected);
        void onShowGridChanged(boolean selected);
        void onDelayChanged(int value);
    }

    public interface FullscreenAccessGuard {
        boolean canToggleFullscreen();
        void onFullscreenBlocked();
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
    private FullscreenAccessGuard fullscreenAccessGuard;

    private JFrame fullscreenFrame;
    private JavaFXNetworkPanel fullscreenNetworkPanel;
    private JPanel fullscreenDrawerPanel;
    private JPopupMenu fullscreenToolbarPopup;
    private javax.swing.Timer fullscreenDrawerAnimator;
    private int fullscreenDrawerCurrentWidth = 16;
    private int fullscreenDrawerTargetWidth = 16;
    private JLabel fullscreenInspectorTitleLabel;
    private javax.swing.JTextArea fullscreenInspectorTextArea;
    private JButton fullscreenPauseResumeButton;
    private JButton fullscreenStopButton;
    private JButton fullscreenCloseButton;
    private JLabel fullscreenSimulationStateLabel;
    private JCheckBox fullscreenPinDrawerCheckBox;
    private JComboBox<String> fullscreenThemeComboBox;
    private JComboBox<String> fullscreenPresetComboBox;
    private JCheckBox fullscreenEnable3DCheckBox;
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
    private boolean currentCanPauseResume;
    private boolean currentCanStop;
    private boolean currentShowIds;
    private boolean currentShowLinks;
    private boolean currentShowRanges;
    private boolean currentShowGrid;
    private int currentDelayValue;
    private int currentDelayMin;
    private int currentDelayMax = 100;
    private boolean drawerPinned = false;
    private boolean fullscreenInteractionLocked = false;

    public SimulationGraphWorkspace(PanelRenderer renderer) {
        this.renderer = renderer;
    }

    public JComboBox<String> getVisualThemeComboBox() {
        return visualThemeComboBox;
    }

    public JComboBox<String> getCameraPresetComboBox() {
        return cameraPresetComboBox;
    }

    public JCheckBox getEnable3DNavigationCheckBox() {
        return enable3DNavigationCheckBox;
    }

    public JButton getFullscreenGraphButton() {
        return fullscreenGraphButton;
    }

    public boolean isFullscreenOpen() {
        return fullscreenFrame != null && fullscreenFrame.isShowing();
    }

    public void setNodeSelectionListener(NodeSelectionListener nodeSelectionListener) {
        this.nodeSelectionListener = nodeSelectionListener;
    }

    public void setSimulationControlListener(SimulationControlListener simulationControlListener) {
        this.simulationControlListener = simulationControlListener;
    }

    public void setDisplayControlListener(DisplayControlListener displayControlListener) {
        this.displayControlListener = displayControlListener;
    }

    public void setFullscreenAccessGuard(FullscreenAccessGuard fullscreenAccessGuard) {
        this.fullscreenAccessGuard = fullscreenAccessGuard;
    }

    public void updateSelectedNodeSummary(String title, String body) {
        currentInspectorTitle = (title == null || title.trim().isEmpty()) ? "No node selected" : title;
        currentInspectorBody = (body == null || body.trim().isEmpty())
                ? "Click any node in the graph to inspect its live state and exported metrics."
                : body;
        SimulationGraphStateSupport.applySelectedNodeSummary(
                currentInspectorTitle,
                currentInspectorBody,
                fullscreenInspectorTitleLabel,
                fullscreenInspectorTextArea);
    }

    public void updateSimulationControlsState(String stateLabel, String runLabel, String pauseResumeLabel,
                                       boolean canRun, boolean canPauseResume, boolean canStop) {
        currentSimulationStateLabel = stateLabel;
        currentPauseResumeLabel = pauseResumeLabel;
        currentCanPauseResume = canRun || canPauseResume;
        currentCanStop = canStop;
        SimulationGraphStateSupport.applySimulationControlsState(
                stateLabel,
                runLabel,
                pauseResumeLabel,
                canRun,
                canPauseResume,
                canStop,
                fullscreenSimulationStateLabel,
                fullscreenPauseResumeButton,
                fullscreenStopButton);
        if (fullscreenInteractionLocked) {
            applyFullscreenInteractionLockState();
        }
    }

    public void setSelectedSensorId(Integer selectedSensorId) {
        if (mainNetworkPanel != null) {
            mainNetworkPanel.setSelectedSensorId(selectedSensorId);
        }
        if (fullscreenNetworkPanel != null) {
            fullscreenNetworkPanel.setSelectedSensorId(selectedSensorId);
        }
    }

    public void updateDisplayControlsState(boolean showIds, boolean showLinks, boolean showRanges, boolean showGrid,
                                    int delayValue, int delayMin, int delayMax) {
        currentShowIds = showIds;
        currentShowLinks = showLinks;
        currentShowRanges = showRanges;
        currentShowGrid = showGrid;
        currentDelayValue = delayValue;
        currentDelayMin = delayMin;
        currentDelayMax = delayMax;
        SimulationGraphStateSupport.applyDisplayControlsState(
                showIds,
                showLinks,
                showRanges,
                showGrid,
                delayValue,
                delayMin,
                delayMax,
                fullscreenShowIdsCheckBox,
                fullscreenShowLinksCheckBox,
                fullscreenShowRangesCheckBox,
                fullscreenShowGridCheckBox,
                fullscreenDelaySlider);
    }

    public void setFullscreenInteractionLocked(boolean locked) {
        fullscreenInteractionLocked = locked;
        applyFullscreenInteractionLockState();
    }

    private void applyFullscreenInteractionLockState() {
        SimulationGraphStateSupport.applyFullscreenInteractionLockState(
                fullscreenInteractionLocked,
                currentCanPauseResume,
                currentCanStop,
                fullscreenEnable3DCheckBox,
                fullscreenThemeComboBox,
                fullscreenPresetComboBox,
                fullscreenPauseResumeButton,
                fullscreenStopButton,
                fullscreenShowIdsCheckBox,
                fullscreenShowLinksCheckBox,
                fullscreenShowRangesCheckBox,
                fullscreenShowGridCheckBox,
                fullscreenDelaySlider,
                fullscreenPinDrawerCheckBox,
                fullscreenCloseButton);
    }

    public void setFullscreenLegendItems(java.util.List<MiniLegendPanel.Item> items) {
        fullscreenLegendPanel = new CompactLegendPanel();
        fullscreenLegendPanel.setItems(items);
    }

    public void initializeControls() {
        SimulationGraphStateSupport.initializeControls(
                visualThemeComboBox,
                enable3DNavigationCheckBox,
                cameraPresetComboBox,
                fullscreenGraphButton,
                () -> applyVisualizationControlsToPanels(null),
                this::toggleFullscreenGraphWindow);
    }

    public void applyVisualizationControlsToPanels(NetworkPanel mainPanel) {
        mainNetworkPanel = SimulationGraphStateSupport.applyVisualizationControlsToPanels(
                mainPanel,
                mainNetworkPanel,
                fullscreenNetworkPanel,
                enable3DNavigationCheckBox,
                visualThemeComboBox,
                cameraPresetComboBox);
    }

    public void renderOnFullscreen(Network network, Service requiredService, double radioRange,
                            boolean showRanges, boolean showLinks, boolean showIds, boolean showGrid) {
        if (fullscreenNetworkPanel == null || !fullscreenNetworkPanel.isShowing()) {
            return;
        }
        try {
            fullscreenNetworkPanel.paintNetwork(network, requiredService, radioRange, showRanges, showLinks, showIds, showGrid);
        } catch (Exception ignored) {}
    }

    private void applyVisualizationControls(JavaFXNetworkPanel panel) {
        SimulationGraphStateSupport.applyVisualizationControls(
                panel,
                enable3DNavigationCheckBox,
                visualThemeComboBox,
                cameraPresetComboBox);
    }

    private void toggleFullscreenGraphWindow() {
        if (fullscreenFrame != null && fullscreenFrame.isShowing()) {
            closeFullscreenGraphWindow();
            return;
        }
        if (fullscreenAccessGuard != null && !fullscreenAccessGuard.canToggleFullscreen()) {
            fullscreenAccessGuard.onFullscreenBlocked();
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
        fullscreenToolbarPopup = buildFullscreenToolbarPopup();
        fullscreenNetworkPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            private void maybeShowPopup(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    if (!fullscreenInteractionLocked && fullscreenToolbarPopup != null) {
                        fullscreenToolbarPopup.show(e.getComponent(), e.getX(), e.getY());
                    }
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
                fullscreenToolbarPopup = null;
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
        applyFullscreenInteractionLockState();
    }

    private void closeFullscreenGraphWindow() {
        if (fullscreenFrame != null) {
            JFrame frame = fullscreenFrame;
            fullscreenFrame = null;
            fullscreenNetworkPanel = null;
            fullscreenDrawerPanel = null;
            fullscreenToolbarPopup = null;
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

        fullscreenThemeComboBox = new JComboBox<String>(new String[]{"Futuristic", "Classic", "Wireframe"});
        fullscreenThemeComboBox.setSelectedItem(visualThemeComboBox.getSelectedItem());
        fullscreenThemeComboBox.setMaximumSize(new Dimension(230, 26));
        fullscreenThemeComboBox.setAlignmentX(0.0f);
        fullscreenThemeComboBox.addActionListener(e -> {
            visualThemeComboBox.setSelectedItem(fullscreenThemeComboBox.getSelectedItem());
            applyVisualizationControlsToPanels(null);
        });
        content.add(fullscreenThemeComboBox);
        content.add(Box.createVerticalStrut(8));

        JLabel presetLabel = new JLabel("3D View");
        presetLabel.setForeground(new Color(200, 232, 255));
        presetLabel.setAlignmentX(0.0f);
        content.add(presetLabel);

        fullscreenPresetComboBox = new JComboBox<String>(new String[]{"Isometric", "Top", "Front"});
        fullscreenPresetComboBox.setSelectedItem(cameraPresetComboBox.getSelectedItem());
        fullscreenPresetComboBox.setMaximumSize(new Dimension(230, 26));
        fullscreenPresetComboBox.setAlignmentX(0.0f);
        fullscreenPresetComboBox.setEnabled(enable3DNavigationCheckBox.isSelected());
        fullscreenPresetComboBox.addActionListener(e -> {
            cameraPresetComboBox.setSelectedItem(fullscreenPresetComboBox.getSelectedItem());
            applyVisualizationControlsToPanels(null);
        });
        content.add(fullscreenPresetComboBox);
        content.add(Box.createVerticalStrut(8));

        fullscreenEnable3DCheckBox = new JCheckBox("Enable 3D navigation");
        fullscreenEnable3DCheckBox.setOpaque(false);
        fullscreenEnable3DCheckBox.setForeground(new Color(220, 245, 255));
        fullscreenEnable3DCheckBox.setSelected(enable3DNavigationCheckBox.isSelected());
        fullscreenEnable3DCheckBox.setAlignmentX(0.0f);
        fullscreenEnable3DCheckBox.addActionListener(e -> {
            enable3DNavigationCheckBox.setSelected(fullscreenEnable3DCheckBox.isSelected());
            fullscreenPresetComboBox.setEnabled(fullscreenEnable3DCheckBox.isSelected());
            applyVisualizationControlsToPanels(null);
        });
        content.add(fullscreenEnable3DCheckBox);
        content.add(Box.createVerticalStrut(10));

        fullscreenCloseButton = new JButton("Close Fullscreen");
        fullscreenCloseButton.setAlignmentX(0.0f);
        fullscreenCloseButton.addActionListener(e -> closeFullscreenGraphWindow());
        content.add(fullscreenCloseButton);
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

        fullscreenShowIdsCheckBox = SimulationGraphFullscreenSupport.createFullscreenToggle("Show IDs", currentShowIds, selected -> {
            if (displayControlListener != null) {
                displayControlListener.onShowIdsChanged(selected);
            }
        });
        content.add(fullscreenShowIdsCheckBox);

        fullscreenShowLinksCheckBox = SimulationGraphFullscreenSupport.createFullscreenToggle("Show links", currentShowLinks, selected -> {
            if (displayControlListener != null) {
                displayControlListener.onShowLinksChanged(selected);
            }
        });
        content.add(fullscreenShowLinksCheckBox);

        fullscreenShowRangesCheckBox = SimulationGraphFullscreenSupport.createFullscreenToggle("Show ranges", currentShowRanges, selected -> {
            if (displayControlListener != null) {
                displayControlListener.onShowRangesChanged(selected);
            }
        });
        content.add(fullscreenShowRangesCheckBox);

        fullscreenShowGridCheckBox = SimulationGraphFullscreenSupport.createFullscreenToggle("Show grid", currentShowGrid, selected -> {
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
        return SimulationGraphFullscreenSupport.buildToolbarPopup(
                visualThemeComboBox,
                cameraPresetComboBox,
                enable3DNavigationCheckBox,
                () -> applyVisualizationControlsToPanels(null),
                this::closeFullscreenGraphWindow);
    }
}
