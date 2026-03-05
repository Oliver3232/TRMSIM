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
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

final class SimulationGraphWorkspace {
    interface PanelRenderer {
        void render(NetworkPanel panel);
    }

    private final JComboBox<String> visualThemeComboBox = new JComboBox<String>();
    private final JComboBox<String> cameraPresetComboBox = new JComboBox<String>();
    private final JCheckBox enable3DNavigationCheckBox = new JCheckBox("3D navigation");
    private final JButton fullscreenGraphButton = new JButton();
    private final PanelRenderer renderer;

    private JFrame fullscreenFrame;
    private JavaFXNetworkPanel fullscreenNetworkPanel;
    private JPanel fullscreenDrawerPanel;
    private javax.swing.Timer fullscreenDrawerAnimator;
    private int fullscreenDrawerCurrentWidth = 16;
    private int fullscreenDrawerTargetWidth = 16;

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

        fullscreenGraphButton.setText("Open Fullscreen");
        fullscreenGraphButton.addActionListener(e -> toggleFullscreenGraphWindow());
    }

    void applyVisualizationControlsToPanels(NetworkPanel mainPanel) {
        if (mainPanel instanceof JavaFXNetworkPanel) {
            applyVisualizationControls((JavaFXNetworkPanel) mainPanel);
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
        panel.setVisualTheme((String) visualThemeComboBox.getSelectedItem());
        panel.set3DNavigationEnabled(enable3DNavigationCheckBox.isSelected());
        panel.applyCameraPreset((String) cameraPresetComboBox.getSelectedItem());
    }

    private void toggleFullscreenGraphWindow() {
        if (fullscreenFrame != null && fullscreenFrame.isShowing()) {
            closeFullscreenGraphWindow();
            return;
        }

        fullscreenNetworkPanel = new JavaFXNetworkPanel();
        fullscreenNetworkPanel.setBackground(Color.white);
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
        fullscreenFrame.getRootPane().registerKeyboardAction(
                e -> {
                    if (fullscreenFrame != null) {
                        fullscreenToolbarPopup.show(fullscreenNetworkPanel, 14, 14);
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_T, 0),
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
            applyVisualizationControlsToPanels(null);
        });
        content.add(enable3D);
        content.add(Box.createVerticalStrut(10));

        JButton closeButton = new JButton("Close Fullscreen");
        closeButton.setAlignmentX(0.0f);
        closeButton.addActionListener(e -> closeFullscreenGraphWindow());
        content.add(closeButton);

        drawer.add(content, BorderLayout.NORTH);
        content.setVisible(false);
        javax.swing.Timer collapseTimer = new javax.swing.Timer(140, evt -> {
            if (fullscreenDrawerPanel == null) {
                return;
            }
            try {
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
        setFullscreenDrawerExpanded(false);
        return drawer;
    }

    private void setFullscreenDrawerExpanded(boolean expanded) {
        if (fullscreenDrawerPanel == null) {
            return;
        }
        fullscreenDrawerTargetWidth = expanded ? 260 : 16;
        Object contentObj = fullscreenDrawerPanel.getClientProperty("drawerContent");
        if (contentObj instanceof JComponent) {
            ((JComponent) contentObj).setVisible(expanded || fullscreenDrawerCurrentWidth > 24);
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
        ButtonGroup presetGroup = new ButtonGroup();
        addPresetItem(presetMenu, presetGroup, "Isometric");
        addPresetItem(presetMenu, presetGroup, "Top");
        addPresetItem(presetMenu, presetGroup, "Front");
        popup.add(presetMenu);

        JCheckBoxMenuItem enable3DItem = new JCheckBoxMenuItem("Enable 3D navigation");
        enable3DItem.setSelected(enable3DNavigationCheckBox.isSelected());
        enable3DItem.addActionListener(e -> {
            enable3DNavigationCheckBox.setSelected(enable3DItem.isSelected());
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
        item.addActionListener(e -> {
            cameraPresetComboBox.setSelectedItem(presetName);
            applyVisualizationControlsToPanels(null);
        });
        group.add(item);
        menu.add(item);
    }
}
