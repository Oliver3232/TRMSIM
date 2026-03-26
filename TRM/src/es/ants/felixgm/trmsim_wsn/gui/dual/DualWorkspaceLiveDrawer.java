package es.ants.felixgm.trmsim_wsn.gui.dual;

import es.ants.felixgm.trmsim_wsn.gui.layout.CompactLegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.layout.MiniLegendPanel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

final class DualWorkspaceLiveDrawer extends JPanel {
    private DualSimulationWorkspacePanel.LiveControlsListener liveControlsListener;
    private final JPanel repaintHost;
    private JLabel liveSimulationStateLabel;
    private JCheckBox liveShowIdsCheckBox;
    private JCheckBox liveShowLinksCheckBox;
    private JCheckBox liveShowRangesCheckBox;
    private JCheckBox liveShowGridCheckBox;
    private JSlider liveDelaySlider;
    private Timer liveDrawerAnimator;
    private int liveDrawerCurrentWidth = 16;
    private int liveDrawerTargetWidth = 16;
    private boolean liveDrawerPinned = false;
    private CompactLegendPanel liveLegendPanel;
    private JLabel liveInspectorTitleLabel;
    private JTextArea liveInspectorTextArea;
    private JComboBox<String> liveThemeComboBox;
    private JComboBox<String> liveCameraPresetComboBox;
    private JCheckBox liveEnable3DCheckBox;

    private boolean currentShowIds;
    private boolean currentShowLinks;
    private boolean currentShowRanges;
    private boolean currentShowGrid;
    private int currentDelayValue;
    private int currentDelayMin;
    private int currentDelayMax = 100;
    private String currentInspectorTitle = "No node selected";
    private String currentInspectorBody = "Click any node in the graph to inspect its live state and exported metrics.";
    private String currentThemeName = "Futuristic";
    private String currentCameraPreset = "Isometric";
    private boolean currentEnable3D;

    DualWorkspaceLiveDrawer(JPanel repaintHost) {
        super(new BorderLayout());
        this.repaintHost = repaintHost;
        setBorder(new EmptyBorder(10, 8, 10, 8));
        setOpaque(true);
        setBackground(new Color(20, 29, 44, 220));
        setPreferredSize(new Dimension(16, 0));
        add(buildContent(), BorderLayout.CENTER);
    }

    void setLiveControlsListener(DualSimulationWorkspacePanel.LiveControlsListener liveControlsListener) {
        this.liveControlsListener = liveControlsListener;
    }

    void updateSimulationControlsState(String stateLabel) {
        liveSimulationStateLabel.setText(stateLabel);
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
        liveShowIdsCheckBox.setSelected(showIds);
        liveShowLinksCheckBox.setSelected(showLinks);
        liveShowRangesCheckBox.setSelected(showRanges);
        liveShowGridCheckBox.setSelected(showGrid);
        liveDelaySlider.setMinimum(delayMin);
        liveDelaySlider.setMaximum(delayMax);
        liveDelaySlider.setValue(delayValue);
    }

    void updateSelectedNodeSummary(String title, String body) {
        currentInspectorTitle = (title == null || title.trim().isEmpty()) ? "No node selected" : title;
        currentInspectorBody = (body == null || body.trim().isEmpty())
                ? "Click any node in the graph to inspect its live state and exported metrics."
                : body;
        liveInspectorTitleLabel.setText(currentInspectorTitle);
        liveInspectorTextArea.setText(currentInspectorBody);
        liveInspectorTextArea.setCaretPosition(0);
    }

    void setLegendItems(java.util.List<MiniLegendPanel.Item> items) {
        liveLegendPanel.setItems(items);
    }

    void updateVisualizationControlsState(String themeName, boolean enable3D, String cameraPreset) {
        currentThemeName = (themeName == null || themeName.trim().isEmpty()) ? "Futuristic" : themeName;
        currentEnable3D = enable3D;
        currentCameraPreset = (cameraPreset == null || cameraPreset.trim().isEmpty()) ? "Isometric" : cameraPreset;
        liveThemeComboBox.setSelectedItem(currentThemeName);
        liveEnable3DCheckBox.setSelected(enable3D);
        liveCameraPresetComboBox.setSelectedItem(currentCameraPreset);
        liveCameraPresetComboBox.setEnabled(enable3D);
    }

    private JComponent buildContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Live Simulation");
        title.setForeground(new Color(220, 245, 255));
        title.setAlignmentX(0.0f);
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(8));
        content.add(titlePanel, BorderLayout.NORTH);

        JPanel settingsPanel = new JPanel();
        settingsPanel.setOpaque(false);
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));

        JPanel inspectorPanel = new JPanel(new BorderLayout(0, 10));
        inspectorPanel.setOpaque(false);

        liveSimulationStateLabel = new JLabel("Idle");
        liveSimulationStateLabel.setForeground(new Color(240, 249, 255));
        liveSimulationStateLabel.setAlignmentX(0.0f);
        settingsPanel.add(liveSimulationStateLabel);
        settingsPanel.add(Box.createVerticalStrut(10));

        addViewControls(settingsPanel);
        addDisplayControls(settingsPanel);
        addInspectorControls(inspectorPanel);

        JTabbedPane drawerTabbedPane = new JTabbedPane();
        drawerTabbedPane.addTab("Sim Settings", settingsPanel);
        drawerTabbedPane.addTab("Inspector", inspectorPanel);
        content.add(drawerTabbedPane, BorderLayout.CENTER);
        content.setVisible(false);
        putClientProperty("drawerContent", content);

        Timer collapseTimer = new Timer(140, evt -> {
            if (liveDrawerPinned) {
                return;
            }
            try {
                Point pointer = java.awt.MouseInfo.getPointerInfo().getLocation();
                Point drawerLoc = getLocationOnScreen();
                int relX = pointer.x - drawerLoc.x;
                int relY = pointer.y - drawerLoc.y;
                if (!contains(relX, relY)) {
                    setLiveDrawerExpanded(false);
                }
            } catch (Exception ignored) {
            }
        });
        collapseTimer.setRepeats(false);

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                collapseTimer.stop();
                setLiveDrawerExpanded(true);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                collapseTimer.restart();
            }
        });

        updateSimulationControlsState("Idle");
        updateDisplayControlsState(currentShowIds, currentShowLinks, currentShowRanges, currentShowGrid,
                currentDelayValue, currentDelayMin, currentDelayMax);
        updateSelectedNodeSummary(currentInspectorTitle, currentInspectorBody);
        updateVisualizationControlsState(currentThemeName, currentEnable3D, currentCameraPreset);
        return content;
    }

    private void addViewControls(JPanel settingsPanel) {
        JLabel viewLabel = new JLabel("View");
        viewLabel.setForeground(new Color(200, 232, 255));
        viewLabel.setAlignmentX(0.0f);
        settingsPanel.add(viewLabel);
        settingsPanel.add(Box.createVerticalStrut(6));

        liveThemeComboBox = new JComboBox<String>(new String[]{"Futuristic", "Classic", "Wireframe"});
        liveThemeComboBox.setSelectedItem(currentThemeName);
        liveThemeComboBox.setMaximumSize(new Dimension(230, 26));
        liveThemeComboBox.setAlignmentX(0.0f);
        liveThemeComboBox.addActionListener(evt -> {
            if (liveControlsListener != null && liveThemeComboBox.getSelectedItem() instanceof String) {
                liveControlsListener.onVisualThemeChanged((String) liveThemeComboBox.getSelectedItem());
            }
        });
        settingsPanel.add(liveThemeComboBox);
        settingsPanel.add(Box.createVerticalStrut(8));

        liveEnable3DCheckBox = new JCheckBox("Enable 3D navigation");
        liveEnable3DCheckBox.setOpaque(false);
        liveEnable3DCheckBox.setForeground(new Color(220, 245, 255));
        liveEnable3DCheckBox.setSelected(currentEnable3D);
        liveEnable3DCheckBox.setAlignmentX(0.0f);
        liveEnable3DCheckBox.addActionListener(evt -> {
            boolean enabled = liveEnable3DCheckBox.isSelected();
            liveCameraPresetComboBox.setEnabled(enabled);
            if (liveControlsListener != null) {
                liveControlsListener.onEnable3DChanged(enabled);
            }
        });
        settingsPanel.add(liveEnable3DCheckBox);
        settingsPanel.add(Box.createVerticalStrut(8));

        liveCameraPresetComboBox = new JComboBox<String>(new String[]{"Isometric", "Top", "Front"});
        liveCameraPresetComboBox.setSelectedItem(currentCameraPreset);
        liveCameraPresetComboBox.setMaximumSize(new Dimension(230, 26));
        liveCameraPresetComboBox.setAlignmentX(0.0f);
        liveCameraPresetComboBox.setEnabled(currentEnable3D);
        liveCameraPresetComboBox.addActionListener(evt -> {
            if (liveControlsListener != null && liveCameraPresetComboBox.getSelectedItem() instanceof String) {
                liveControlsListener.onCameraPresetChanged((String) liveCameraPresetComboBox.getSelectedItem());
            }
        });
        settingsPanel.add(liveCameraPresetComboBox);
        settingsPanel.add(Box.createVerticalStrut(12));

        JButton openFullscreenButton = new JButton("Open Fullscreen");
        openFullscreenButton.setAlignmentX(0.0f);
        openFullscreenButton.addActionListener(evt -> {
            if (liveControlsListener != null) {
                liveControlsListener.onOpenFullscreenRequested();
            }
        });
        settingsPanel.add(openFullscreenButton);
        settingsPanel.add(Box.createVerticalStrut(12));
    }

    private void addDisplayControls(JPanel settingsPanel) {
        JLabel displayLabel = new JLabel("Display");
        displayLabel.setForeground(new Color(200, 232, 255));
        displayLabel.setAlignmentX(0.0f);
        settingsPanel.add(displayLabel);
        settingsPanel.add(Box.createVerticalStrut(6));

        liveShowIdsCheckBox = createDrawerToggle("Show IDs", selected -> {
            if (liveControlsListener != null) {
                liveControlsListener.onShowIdsChanged(selected);
            }
        });
        settingsPanel.add(liveShowIdsCheckBox);

        liveShowLinksCheckBox = createDrawerToggle("Show links", selected -> {
            if (liveControlsListener != null) {
                liveControlsListener.onShowLinksChanged(selected);
            }
        });
        settingsPanel.add(liveShowLinksCheckBox);

        liveShowRangesCheckBox = createDrawerToggle("Show ranges", selected -> {
            if (liveControlsListener != null) {
                liveControlsListener.onShowRangesChanged(selected);
            }
        });
        settingsPanel.add(liveShowRangesCheckBox);

        liveShowGridCheckBox = createDrawerToggle("Show grid", selected -> {
            if (liveControlsListener != null) {
                liveControlsListener.onShowGridChanged(selected);
            }
        });
        settingsPanel.add(liveShowGridCheckBox);
        settingsPanel.add(Box.createVerticalStrut(8));

        JLabel delayLabel = new JLabel("Delay");
        delayLabel.setForeground(new Color(200, 232, 255));
        delayLabel.setAlignmentX(0.0f);
        settingsPanel.add(delayLabel);

        liveDelaySlider = new JSlider(currentDelayMin, currentDelayMax, currentDelayValue);
        liveDelaySlider.setOpaque(false);
        liveDelaySlider.setAlignmentX(0.0f);
        liveDelaySlider.addChangeListener(evt -> {
            if (liveControlsListener != null) {
                liveControlsListener.onDelayChanged(liveDelaySlider.getValue());
            }
        });
        settingsPanel.add(liveDelaySlider);
        settingsPanel.add(Box.createVerticalStrut(12));

        JCheckBox pinDrawerCheckBox = new JCheckBox("Pin drawer");
        pinDrawerCheckBox.setOpaque(false);
        pinDrawerCheckBox.setForeground(new Color(220, 245, 255));
        pinDrawerCheckBox.setSelected(liveDrawerPinned);
        pinDrawerCheckBox.setAlignmentX(0.0f);
        pinDrawerCheckBox.addActionListener(evt -> {
            liveDrawerPinned = pinDrawerCheckBox.isSelected();
            setLiveDrawerExpanded(liveDrawerPinned);
        });
        settingsPanel.add(pinDrawerCheckBox);
    }

    private void addInspectorControls(JPanel inspectorPanel) {
        liveLegendPanel = new CompactLegendPanel();
        JPanel legendHolder = new JPanel(new BorderLayout());
        legendHolder.setOpaque(true);
        legendHolder.setBackground(new Color(9, 14, 24, 220));
        legendHolder.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(72, 122, 175, 120), 1, true),
                new EmptyBorder(8, 8, 8, 8)));
        legendHolder.add(liveLegendPanel, BorderLayout.CENTER);
        legendHolder.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        legendHolder.setPreferredSize(new Dimension(252, 132));
        legendHolder.setAlignmentX(0.0f);

        JLabel inspectorLabel = new JLabel("Selected Node");
        inspectorLabel.setForeground(new Color(200, 232, 255));
        JPanel inspectorHeader = new JPanel();
        inspectorHeader.setOpaque(false);
        inspectorHeader.setLayout(new BoxLayout(inspectorHeader, BoxLayout.Y_AXIS));
        inspectorLabel.setAlignmentX(0.0f);
        inspectorHeader.add(inspectorLabel);
        inspectorHeader.add(Box.createVerticalStrut(6));

        liveInspectorTitleLabel = new JLabel(currentInspectorTitle);
        liveInspectorTitleLabel.setForeground(new Color(240, 249, 255));
        liveInspectorTitleLabel.setAlignmentX(0.0f);
        inspectorHeader.add(liveInspectorTitleLabel);
        inspectorHeader.add(Box.createVerticalStrut(6));
        inspectorPanel.add(inspectorHeader, BorderLayout.NORTH);

        liveInspectorTextArea = new JTextArea(currentInspectorBody);
        liveInspectorTextArea.setEditable(false);
        liveInspectorTextArea.setLineWrap(true);
        liveInspectorTextArea.setWrapStyleWord(true);
        liveInspectorTextArea.setOpaque(true);
        liveInspectorTextArea.setBackground(new Color(9, 14, 24, 220));
        liveInspectorTextArea.setForeground(new Color(214, 236, 252));
        liveInspectorTextArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(72, 122, 175, 120), 1, true),
                new EmptyBorder(10, 10, 10, 10)));
        JScrollPane inspectorScrollPane = new JScrollPane(liveInspectorTextArea);
        inspectorScrollPane.setBorder(BorderFactory.createEmptyBorder());
        inspectorScrollPane.getViewport().setOpaque(false);
        inspectorScrollPane.setOpaque(false);

        JPanel inspectorBodyPanel = new JPanel(new BorderLayout(0, 10));
        inspectorBodyPanel.setOpaque(false);
        inspectorBodyPanel.add(inspectorScrollPane, BorderLayout.CENTER);

        JPanel inspectorLegendPanel = new JPanel();
        inspectorLegendPanel.setOpaque(false);
        inspectorLegendPanel.setLayout(new BoxLayout(inspectorLegendPanel, BoxLayout.Y_AXIS));
        JLabel legendLabel = new JLabel("Mini Legend");
        legendLabel.setForeground(new Color(200, 232, 255));
        legendLabel.setAlignmentX(0.0f);
        inspectorLegendPanel.add(legendLabel);
        inspectorLegendPanel.add(Box.createVerticalStrut(6));
        inspectorLegendPanel.add(legendHolder);
        inspectorBodyPanel.add(inspectorLegendPanel, BorderLayout.SOUTH);
        inspectorPanel.add(inspectorBodyPanel, BorderLayout.CENTER);
    }

    private JCheckBox createDrawerToggle(String label, java.util.function.Consumer<Boolean> listener) {
        JCheckBox checkBox = new JCheckBox(label);
        checkBox.setOpaque(false);
        checkBox.setForeground(new Color(220, 245, 255));
        checkBox.setAlignmentX(0.0f);
        checkBox.addActionListener(evt -> listener.accept(Boolean.valueOf(checkBox.isSelected())));
        return checkBox;
    }

    private void setLiveDrawerExpanded(boolean expanded) {
        boolean shouldExpand = expanded || liveDrawerPinned;
        liveDrawerTargetWidth = shouldExpand ? 288 : 16;
        Object contentObj = getClientProperty("drawerContent");
        if (contentObj instanceof JComponent) {
            ((JComponent) contentObj).setVisible(shouldExpand || liveDrawerCurrentWidth > 24);
        }
        if (liveDrawerAnimator == null) {
            liveDrawerAnimator = new Timer(16, evt -> {
                int delta = liveDrawerTargetWidth - liveDrawerCurrentWidth;
                if (Math.abs(delta) <= 1) {
                    liveDrawerCurrentWidth = liveDrawerTargetWidth;
                } else {
                    int step = Math.max(1, Math.abs(delta) / 4);
                    liveDrawerCurrentWidth += (delta > 0) ? step : -step;
                }
                setPreferredSize(new Dimension(liveDrawerCurrentWidth, 0));
                Object drawerContentObj = getClientProperty("drawerContent");
                if (drawerContentObj instanceof JComponent) {
                    ((JComponent) drawerContentObj).setVisible(liveDrawerCurrentWidth > 32);
                }
                revalidate();
                repaint();
                repaintHost.revalidate();
                repaintHost.repaint();
                if (liveDrawerCurrentWidth == liveDrawerTargetWidth) {
                    liveDrawerAnimator.stop();
                }
            });
            liveDrawerAnimator.setRepeats(true);
        }
        if (!liveDrawerAnimator.isRunning()) {
            liveDrawerAnimator.start();
        }
    }
}
