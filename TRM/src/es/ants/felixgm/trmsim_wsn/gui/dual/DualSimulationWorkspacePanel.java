package es.ants.felixgm.trmsim_wsn.gui.dual;

import es.ants.felixgm.trmsim_wsn.SimulationSlot;
import es.ants.felixgm.trmsim_wsn.gui.layout.MiniLegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.OutcomesPanel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.JLayeredPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;

/**
 * UI container for one simulation slot in dual mode.
 * The panel keeps the network viewport always visible and places
 * secondary controls into collapsible-ready side/bottom containers.
 */
public final class DualSimulationWorkspacePanel extends JPanel {
    private static final double DEFAULT_SETUP_HEIGHT_RATIO = 0.22;
    private static final double MIN_SETUP_HEIGHT_RATIO = 0.16;
    private static final double MAX_SETUP_HEIGHT_RATIO = 0.30;
    private static final double DEFAULT_RESULTS_HEIGHT_RATIO = 0.18;
    private static final double MIN_RESULTS_HEIGHT_RATIO = 0.12;
    private static final double MAX_RESULTS_HEIGHT_RATIO = 0.26;
    private static final int MIN_SECTION_HEIGHT = 96;

    public interface LiveControlsListener {
        void onShowIdsChanged(boolean selected);
        void onShowLinksChanged(boolean selected);
        void onShowRangesChanged(boolean selected);
        void onShowGridChanged(boolean selected);
        void onDelayChanged(int value);
        void onVisualThemeChanged(String themeName);
        void onEnable3DChanged(boolean enabled);
        void onCameraPresetChanged(String presetName);
        void onOpenFullscreenRequested();
    }

    private final SimulationSlot slot;
    private final JLabel titleLabel;
    private final JLabel modelLabel;
    private final JPanel headerPanel;
    private final JPanel headerTopRow;
    private final JButton setupToggleButton;
    private final JButton bottomToggleButton;
    private final JButton detachResultsButton;
    private final JPanel networkViewportPanel;
    private final JLayeredPane networkOverlayPane;
    private final DualWorkspaceLiveDrawer liveDrawerPanel;
    private final JPanel drawerPanel;
    private final JPanel bottomPanel;
    private final JSplitPane setupContentSplitPane;
    private final JSplitPane contentSplitPane;
    private final JComboBox<String> trustModelComboBox;
    private final JButton newNetworkButton;
    private final JButton loadNetworkButton;
    private final JButton saveNetworkButton;
    private final JButton resetNetworkButton;
    private final JTabbedPane setupTabbedPane;
    private final JPanel settingsContentPanel;
    private final JPanel parametersContentPanel;
    private final JTabbedPane bottomTabbedPane;
    private final JTextArea messagesTextArea;
    private javax.swing.JFrame detachedResultsFrame;
    private boolean restoreBottomExpandedAfterDetach;
    private LiveControlsListener liveControlsListener;
    private boolean setupExpanded = false;
    private boolean bottomExpanded = true;
    private boolean restoringBottomLayout = false;
    private int rememberedSetupDividerLocation = -1;
    private int rememberedContentDividerLocation = -1;
    private int rememberedBottomHeight = -1;
    private NetworkPanel networkPanel;
    private Collection<OutcomesPanel> outcomesPanels = new ArrayList<OutcomesPanel>();

    public DualSimulationWorkspacePanel(SimulationSlot slot) {
        this.slot = slot;
        setLayout(new BorderLayout(0, 8));
        setOpaque(false);

        titleLabel = new JLabel(slot == SimulationSlot.PRIMARY ? "Simulation A" : "Simulation B");
        titleLabel.setFont(titleLabel.getFont().deriveFont(15f));

        modelLabel = new JLabel("No trust model selected");
        modelLabel.setForeground(new Color(92, 102, 116));

        setupToggleButton = new JButton("Hide Setup");
        bottomToggleButton = new JButton("Hide Results");
        detachResultsButton = new JButton("Open Results");
        setupToggleButton.addActionListener(evt -> setSetupExpanded(!setupExpanded));
        bottomToggleButton.addActionListener(evt -> setBottomExpanded(!bottomExpanded));
        detachResultsButton.addActionListener(evt -> openBottomPanelInWindow());

        headerTopRow = new JPanel(new BorderLayout());
        headerTopRow.setOpaque(false);
        headerTopRow.add(titleLabel, BorderLayout.WEST);
        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        headerActions.setOpaque(false);
        headerActions.add(setupToggleButton);
        headerActions.add(bottomToggleButton);
        headerActions.add(detachResultsButton);
        headerTopRow.add(headerActions, BorderLayout.EAST);

        headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.add(headerTopRow);
        headerPanel.add(modelLabel);

        networkViewportPanel = new JPanel(new BorderLayout());
        networkViewportPanel.setOpaque(true);
        networkViewportPanel.setBackground(Color.white);
        networkViewportPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Network"),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        networkViewportPanel.setPreferredSize(new Dimension(420, 440));
        networkOverlayPane = new JLayeredPane();
        networkOverlayPane.setOpaque(false);
        networkOverlayPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layoutOverlayNetworkPanel();
            }
        });
        networkViewportPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layoutOverlayNetworkPanel();
            }
        });
        networkViewportPanel.add(networkOverlayPane, BorderLayout.CENTER);
        liveDrawerPanel = new DualWorkspaceLiveDrawer(networkViewportPanel);
        networkViewportPanel.add(liveDrawerPanel, BorderLayout.EAST);

        trustModelComboBox = new JComboBox<String>();
        newNetworkButton = new JButton("New WSN");
        loadNetworkButton = new JButton("Load WSN");
        saveNetworkButton = new JButton("Save WSN");
        resetNetworkButton = new JButton("Reset WSN");

        drawerPanel = new JPanel(new BorderLayout());
        drawerPanel.setOpaque(false);
        drawerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Simulation Setup"),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        setupTabbedPane = new JTabbedPane();
        settingsContentPanel = new JPanel(new BorderLayout());
        settingsContentPanel.setOpaque(false);
        parametersContentPanel = new JPanel(new BorderLayout());
        parametersContentPanel.setOpaque(false);

        JPanel defaultSettingsPanel = new JPanel();
        defaultSettingsPanel.setOpaque(false);
        defaultSettingsPanel.setLayout(new BoxLayout(defaultSettingsPanel, BoxLayout.Y_AXIS));
        defaultSettingsPanel.add(trustModelComboBox);
        defaultSettingsPanel.add(Box.createVerticalStrut(6));
        defaultSettingsPanel.add(newNetworkButton);
        defaultSettingsPanel.add(Box.createVerticalStrut(4));
        defaultSettingsPanel.add(loadNetworkButton);
        defaultSettingsPanel.add(Box.createVerticalStrut(4));
        defaultSettingsPanel.add(saveNetworkButton);
        defaultSettingsPanel.add(Box.createVerticalStrut(4));
        defaultSettingsPanel.add(resetNetworkButton);
        settingsContentPanel.add(defaultSettingsPanel, BorderLayout.NORTH);

        JTextArea parametersPlaceholder = new JTextArea(
                "Slot-specific parameter editor will live here.\n\n"
                        + "Current dual mode still uses the shared simulation configuration baseline.");
        parametersPlaceholder.setEditable(false);
        parametersPlaceholder.setLineWrap(true);
        parametersPlaceholder.setWrapStyleWord(true);
        parametersPlaceholder.setOpaque(false);
        parametersContentPanel.add(parametersPlaceholder, BorderLayout.CENTER);

        JScrollPane settingsScrollPane = new JScrollPane(settingsContentPanel);
        settingsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        settingsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        JScrollPane parametersScrollPane = new JScrollPane(parametersContentPanel);
        parametersScrollPane.setBorder(BorderFactory.createEmptyBorder());
        parametersScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        setupTabbedPane.addTab("Simulation Settings", settingsScrollPane);
        setupTabbedPane.addTab("Simulation Parameters", parametersScrollPane);
        drawerPanel.add(setupTabbedPane, BorderLayout.CENTER);

        bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Outcomes & Messages"),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        bottomPanel.setPreferredSize(new Dimension(420, 160));
        bottomPanel.setMinimumSize(new Dimension(0, MIN_SECTION_HEIGHT));
        bottomTabbedPane = new JTabbedPane();
        bottomTabbedPane.setMinimumSize(new Dimension(0, MIN_SECTION_HEIGHT));
        messagesTextArea = new JTextArea(8, 32);
        messagesTextArea.setEditable(false);
        bottomTabbedPane.addTab("Messages", new JScrollPane(messagesTextArea));
        bottomPanel.add(bottomTabbedPane, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel(new BorderLayout(0, 8));
        centerPanel.setOpaque(false);
        contentSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        contentSplitPane.setBorder(BorderFactory.createEmptyBorder());
        contentSplitPane.setResizeWeight(1.0 - DEFAULT_RESULTS_HEIGHT_RATIO);
        contentSplitPane.setContinuousLayout(true);
        contentSplitPane.setTopComponent(networkViewportPanel);
        contentSplitPane.setBottomComponent(bottomPanel);
        contentSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (!restoringBottomLayout && bottomExpanded && bottomPanel.isVisible()) {
                    int dividerLocation = contentSplitPane.getDividerLocation();
                    if (dividerLocation > 0) {
                        rememberedContentDividerLocation = dividerLocation;
                        rememberCurrentBottomHeight();
                    }
                }
            }
        });
        setupContentSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        setupContentSplitPane.setBorder(BorderFactory.createEmptyBorder());
        setupContentSplitPane.setResizeWeight(DEFAULT_SETUP_HEIGHT_RATIO);
        setupContentSplitPane.setContinuousLayout(true);
        setupContentSplitPane.setTopComponent(drawerPanel);
        setupContentSplitPane.setBottomComponent(contentSplitPane);
        setupContentSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (setupExpanded && drawerPanel.isVisible()) {
                    int dividerLocation = setupContentSplitPane.getDividerLocation();
                    if (dividerLocation > 0) {
                        rememberedSetupDividerLocation = dividerLocation;
                    }
                }
            }
        });
        centerPanel.add(setupContentSplitPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateSetupPanelPreferredSize();
            }
        });
        setSetupExpanded(false);
    }

    public SimulationSlot getSlot() {
        return slot;
    }

    public JPanel getNetworkViewportPanel() {
        return networkViewportPanel;
    }

    public JPanel getDrawerPanel() {
        return drawerPanel;
    }

    public JPanel getBottomPanel() {
        return bottomPanel;
    }

    public JButton getSetupToggleButton() {
        return setupToggleButton;
    }

    public JButton getBottomToggleButton() {
        return bottomToggleButton;
    }

    public void setWorkspaceChromeEnabled(boolean enabled) {
        setupToggleButton.setEnabled(enabled);
        bottomToggleButton.setEnabled(enabled);
        detachResultsButton.setEnabled(enabled && detachedResultsFrame == null);
    }

    public JComboBox<String> getTrustModelComboBox() {
        return trustModelComboBox;
    }

    public JButton getNewNetworkButton() {
        return newNetworkButton;
    }

    public JButton getLoadNetworkButton() {
        return loadNetworkButton;
    }

    public JButton getSaveNetworkButton() {
        return saveNetworkButton;
    }

    public JButton getResetNetworkButton() {
        return resetNetworkButton;
    }

    public void setSetupControlsEnabled(boolean enabled) {
        trustModelComboBox.setEnabled(enabled);
        newNetworkButton.setEnabled(enabled);
        loadNetworkButton.setEnabled(enabled);
        saveNetworkButton.setEnabled(enabled);
        resetNetworkButton.setEnabled(enabled);
    }

    public JTextArea getMessagesTextArea() {
        return messagesTextArea;
    }

    public void setLiveControlsListener(LiveControlsListener liveControlsListener) {
        this.liveControlsListener = liveControlsListener;
        liveDrawerPanel.setLiveControlsListener(liveControlsListener);
    }

    public void updateSimulationControlsState(String stateLabel) {
        liveDrawerPanel.updateSimulationControlsState(stateLabel);
    }

    public void updateDisplayControlsState(boolean showIds, boolean showLinks, boolean showRanges, boolean showGrid,
                                           int delayValue, int delayMin, int delayMax) {
        liveDrawerPanel.updateDisplayControlsState(showIds, showLinks, showRanges, showGrid, delayValue, delayMin, delayMax);
    }

    public void updateSelectedNodeSummary(String title, String body) {
        liveDrawerPanel.updateSelectedNodeSummary(title, body);
    }

    public void setLegendItems(java.util.List<MiniLegendPanel.Item> items) {
        liveDrawerPanel.setLegendItems(items);
    }

    public void updateVisualizationControlsState(String themeName, boolean enable3D, String cameraPreset) {
        liveDrawerPanel.updateVisualizationControlsState(themeName, enable3D, cameraPreset);
    }

    public Collection<OutcomesPanel> getOutcomesPanels() {
        return outcomesPanels;
    }

    public NetworkPanel getNetworkPanel() {
        return networkPanel;
    }

    public void setNetworkPanel(NetworkPanel networkPanel) {
        this.networkPanel = networkPanel;
        networkOverlayPane.removeAll();
        if (networkPanel != null) {
            if (networkPanel.getParent() != null) {
                networkPanel.getParent().remove(networkPanel);
            }
            networkOverlayPane.add(networkPanel, JLayeredPane.DEFAULT_LAYER);
            networkOverlayPane.moveToBack(networkPanel);
            networkPanel.setBackground(Color.white);
            if (networkPanel instanceof es.ants.felixgm.trmsim_wsn.gui.networkpanels.JavaFXNetworkPanel) {
                ((es.ants.felixgm.trmsim_wsn.gui.networkpanels.JavaFXNetworkPanel) networkPanel).ensureActiveRendering();
            }
        }
        layoutOverlayNetworkPanel();
        networkOverlayPane.doLayout();
        if (networkPanel != null) {
            networkPanel.doLayout();
            networkPanel.validate();
        }
        networkOverlayPane.revalidate();
        networkOverlayPane.repaint();
        networkViewportPanel.revalidate();
        networkViewportPanel.repaint();
    }

    private void layoutOverlayNetworkPanel() {
        int width = Math.max(networkOverlayPane.getWidth(), networkViewportPanel.getWidth());
        int height = Math.max(networkOverlayPane.getHeight(), networkViewportPanel.getHeight());
        if (width <= 0 || height <= 0) {
            return;
        }
        if (networkPanel != null && networkPanel.getParent() == networkOverlayPane) {
            networkPanel.setBounds(0, 0, width, height);
            networkPanel.setSize(width, height);
            networkPanel.revalidate();
            networkPanel.doLayout();
        }
    }

    public void setOutcomesPanels(Collection<OutcomesPanel> outcomesPanels) {
        this.outcomesPanels = outcomesPanels;
        bottomTabbedPane.removeAll();
        if (outcomesPanels != null) {
            for (OutcomesPanel outcomesPanel : outcomesPanels) {
                bottomTabbedPane.addTab(outcomesPanel.getLabel(), outcomesPanel);
            }
        }
        bottomTabbedPane.addTab("Messages", new JScrollPane(messagesTextArea));
        bottomPanel.revalidate();
        bottomPanel.repaint();
    }

    public void setSelectedTrustModelName(String trustModelName) {
        if (trustModelName == null || trustModelName.trim().isEmpty()) {
            modelLabel.setText("No trust model selected");
        } else {
            modelLabel.setText(trustModelName);
        }
    }

    public void setSetupExpanded(boolean expanded) {
        if (!expanded && setupExpanded) {
            rememberCurrentSetupDividerLocation();
        }
        setupExpanded = expanded;
        drawerPanel.setVisible(expanded);
        setupContentSplitPane.setDividerSize(expanded ? 8 : 0);
        setupToggleButton.setText(expanded ? "Hide Setup" : "Show Setup");
        if (expanded) {
            updateSetupPanelPreferredSize();
            restoreSetupDividerLocation();
        } else {
            setupContentSplitPane.setDividerLocation(0);
        }
        revalidate();
        repaint();
    }

    private void updateSetupPanelPreferredSize() {
        int panelHeight = getHeight() > 0 ? getHeight() : 760;
        int preferredHeight = defaultSetupHeight(panelHeight);
        drawerPanel.setPreferredSize(new Dimension(420, preferredHeight));
        drawerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, preferredHeight));
        drawerPanel.revalidate();
    }

    public void setBottomExpanded(boolean expanded) {
        if (!expanded && bottomExpanded) {
            rememberCurrentContentDividerLocation();
        }
        bottomExpanded = expanded;
        restoringBottomLayout = true;
        contentSplitPane.getBottomComponent().setVisible(expanded);
        contentSplitPane.setDividerSize(expanded ? 8 : 0);
        revalidate();
        repaint();
        if (expanded) {
            restoreContentDividerLocation();
        } else {
            SwingUtilities.invokeLater(() -> restoringBottomLayout = false);
        }
        bottomToggleButton.setText(expanded ? "Hide Results" : "Show Results");
    }

    public boolean isSetupExpanded() {
        return setupExpanded;
    }

    public boolean isBottomExpanded() {
        return bottomExpanded;
    }

    public void applyBalancedDefaultLayout() {
        setSetupExpanded(true);
        setBottomExpanded(true);
        SwingUtilities.invokeLater(() -> {
            int panelHeight = getHeight() > 0 ? getHeight() : 760;
            int setupHeight = defaultSetupHeight(panelHeight);
            int resultsHeight = defaultResultsHeight(panelHeight);
            drawerPanel.setPreferredSize(new Dimension(420, setupHeight));
            drawerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, setupHeight));
            rememberedSetupDividerLocation = setupHeight;
            setupContentSplitPane.setDividerLocation(setupHeight);
            applyBottomResultsHeight(resultsHeight);
            revalidate();
            repaint();
        });
    }

    public void setSettingsContent(java.awt.Component component) {
        settingsContentPanel.removeAll();
        if (component != null) {
            settingsContentPanel.add(component, BorderLayout.CENTER);
        }
        settingsContentPanel.revalidate();
        settingsContentPanel.repaint();
    }

    public void setParametersContent(java.awt.Component component) {
        parametersContentPanel.removeAll();
        if (component != null) {
            parametersContentPanel.add(component, BorderLayout.CENTER);
        }
        parametersContentPanel.revalidate();
        parametersContentPanel.repaint();
    }

    private void openBottomPanelInWindow() {
        if (detachedResultsFrame != null && detachedResultsFrame.isShowing()) {
            detachedResultsFrame.toFront();
            detachedResultsFrame.requestFocus();
            return;
        }
        Window owner = javax.swing.SwingUtilities.getWindowAncestor(this);
        detachedResultsFrame = new javax.swing.JFrame(titleLabel.getText() + " Results");
        if (owner != null) {
            detachedResultsFrame.setLocationRelativeTo(owner);
        }
        detachedResultsFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        detachedResultsFrame.setResizable(true);
        restoreBottomExpandedAfterDetach = bottomExpanded;
        rememberCurrentContentDividerLocation();
        restoringBottomLayout = true;
        bottomPanel.remove(bottomTabbedPane);
        bottomExpanded = false;
        bottomToggleButton.setText("Show Results");
        detachResultsButton.setEnabled(false);
        contentSplitPane.getBottomComponent().setVisible(false);
        contentSplitPane.setDividerSize(0);
        SwingUtilities.invokeLater(() -> restoringBottomLayout = false);
        detachedResultsFrame.setContentPane(bottomTabbedPane);
        detachedResultsFrame.setSize(720, 420);
        detachedResultsFrame.setMinimumSize(new Dimension(480, 320));
        detachedResultsFrame.setLocationRelativeTo(this);
        detachedResultsFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                restoreDetachedResultsPanel();
            }

            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                restoreDetachedResultsPanel();
            }
        });
        detachedResultsFrame.setVisible(true);
    }

    private void restoreDetachedResultsPanel() {
        if (detachedResultsFrame == null) {
            return;
        }
        if (bottomTabbedPane.getParent() != bottomPanel) {
            bottomPanel.add(bottomTabbedPane, BorderLayout.CENTER);
        }
        detachResultsButton.setEnabled(true);
        bottomPanel.revalidate();
        bottomPanel.repaint();
        revalidate();
        repaint();
        if (restoreBottomExpandedAfterDetach) {
            SwingUtilities.invokeLater(() -> setBottomExpanded(true));
        }
        detachedResultsFrame = null;
    }

    private void rememberCurrentSetupDividerLocation() {
        int dividerLocation = setupContentSplitPane.getDividerLocation();
        if (dividerLocation > 0) {
            rememberedSetupDividerLocation = dividerLocation;
        }
    }

    private void rememberCurrentContentDividerLocation() {
        int dividerLocation = contentSplitPane.getDividerLocation();
        if (dividerLocation > 0) {
            rememberedContentDividerLocation = dividerLocation;
            rememberCurrentBottomHeight();
        }
    }

    private void rememberCurrentBottomHeight() {
        int splitHeight = contentSplitPane.getHeight();
        int dividerLocation = contentSplitPane.getDividerLocation();
        if (splitHeight > 0 && dividerLocation >= 0 && dividerLocation <= splitHeight) {
            int currentBottomHeight = splitHeight - dividerLocation;
            if (currentBottomHeight > 0) {
                rememberedBottomHeight = currentBottomHeight;
            }
        }
    }

    private void restoreSetupDividerLocation() {
        SwingUtilities.invokeLater(() -> {
            int panelHeight = getHeight() > 0 ? getHeight() : 760;
            int fallbackHeight = defaultSetupHeight(panelHeight);
            int dividerLocation = rememberedSetupDividerLocation > 0 ? rememberedSetupDividerLocation : fallbackHeight;
            setupContentSplitPane.setDividerLocation(dividerLocation);
        });
    }

    private void restoreContentDividerLocation() {
        SwingUtilities.invokeLater(() -> {
            SwingUtilities.invokeLater(() -> {
                int splitHeight = contentSplitPane.getHeight() > 0 ? contentSplitPane.getHeight() : 0;
                int dividerLocation;
                if (splitHeight > 0 && rememberedBottomHeight > 0) {
                    int restoredBottomHeight = Math.min(splitHeight - 1, Math.max(MIN_SECTION_HEIGHT, rememberedBottomHeight));
                    dividerLocation = Math.max(0, splitHeight - restoredBottomHeight);
                } else if (splitHeight > 0) {
                    dividerLocation = Math.max(0, splitHeight - defaultResultsHeight(splitHeight));
                } else {
                    int panelHeight = getHeight() > 0 ? getHeight() : 760;
                    dividerLocation = Math.max(0, panelHeight - defaultResultsHeight(panelHeight));
                }
                contentSplitPane.setDividerLocation(dividerLocation);
                rememberCurrentBottomHeight();
                restoringBottomLayout = false;
            });
        });
    }

    private int defaultResultsHeight(int availableHeight) {
        return scaledSectionHeight(
                availableHeight,
                DEFAULT_RESULTS_HEIGHT_RATIO,
                MIN_RESULTS_HEIGHT_RATIO,
                MAX_RESULTS_HEIGHT_RATIO);
    }

    private void applyBottomResultsHeight(int resultsHeight) {
        SwingUtilities.invokeLater(() -> {
            int splitHeight = contentSplitPane.getHeight();
            if (splitHeight <= 0) {
                splitHeight = Math.max(availableContentHeightFallback(), resultsHeight + 220);
            }
            rememberedBottomHeight = resultsHeight;
            rememberedContentDividerLocation = Math.max(0, splitHeight - resultsHeight);
            contentSplitPane.setDividerLocation(rememberedContentDividerLocation);
        });
    }

    private int availableContentHeightFallback() {
        int panelHeight = getHeight() > 0 ? getHeight() : 760;
        int setupHeight = drawerPanel.isVisible() ? drawerPanel.getPreferredSize().height : 0;
        int headerHeight = headerPanel.getPreferredSize() != null ? headerPanel.getPreferredSize().height : 0;
        int gaps = 16;
        return Math.max(220, panelHeight - setupHeight - headerHeight - gaps);
    }

    private int defaultSetupHeight(int availableHeight) {
        return scaledSectionHeight(
                availableHeight,
                DEFAULT_SETUP_HEIGHT_RATIO,
                MIN_SETUP_HEIGHT_RATIO,
                MAX_SETUP_HEIGHT_RATIO);
    }

    private int scaledSectionHeight(int availableHeight, double defaultRatio, double minRatio, double maxRatio) {
        int defaultHeight = (int) Math.round(availableHeight * defaultRatio);
        int minHeight = Math.max(MIN_SECTION_HEIGHT, (int) Math.round(availableHeight * minRatio));
        int maxHeight = Math.max(minHeight + 24, (int) Math.round(availableHeight * maxRatio));
        return Math.max(minHeight, Math.min(maxHeight, defaultHeight));
    }
}
