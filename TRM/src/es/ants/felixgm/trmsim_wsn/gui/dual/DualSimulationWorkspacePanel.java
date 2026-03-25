package es.ants.felixgm.trmsim_wsn.gui.dual;

import es.ants.felixgm.trmsim_wsn.SimulationSlot;
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
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JLayeredPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collection;

/**
 * UI container for one simulation slot in dual mode.
 * The panel keeps the network viewport always visible and places
 * secondary controls into collapsible-ready side/bottom containers.
 */
public final class DualSimulationWorkspacePanel extends JPanel {
    private final SimulationSlot slot;
    private final JLabel titleLabel;
    private final JLabel modelLabel;
    private final JPanel headerPanel;
    private final JPanel headerTopRow;
    private final JButton setupToggleButton;
    private final JButton bottomToggleButton;
    private final JButton bottomOverflowButton;
    private final JButton fullscreenButton;
    private final JPanel networkViewportPanel;
    private final JLayeredPane networkOverlayPane;
    private final JPanel drawerPanel;
    private final JPanel bottomPanel;
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
    private boolean setupExpanded = false;
    private boolean bottomExpanded = true;
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
        bottomOverflowButton = new JButton("...");
        fullscreenButton = new JButton("Open Fullscreen");
        setupToggleButton.addActionListener(evt -> setSetupExpanded(!setupExpanded));
        bottomToggleButton.addActionListener(evt -> setBottomExpanded(!bottomExpanded));
        bottomOverflowButton.addActionListener(evt -> showBottomOverflowMenu());

        headerTopRow = new JPanel(new BorderLayout());
        headerTopRow.setOpaque(false);
        headerTopRow.add(titleLabel, BorderLayout.WEST);
        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        headerActions.setOpaque(false);
        headerActions.add(setupToggleButton);
        headerActions.add(bottomToggleButton);
        headerActions.add(fullscreenButton);
        headerActions.add(bottomOverflowButton);
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

        setupTabbedPane.addTab("Simulation Settings", new JScrollPane(settingsContentPanel));
        setupTabbedPane.addTab("Simulation Parameters", new JScrollPane(parametersContentPanel));
        drawerPanel.add(setupTabbedPane, BorderLayout.CENTER);

        bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Outcomes & Messages"),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        bottomPanel.setPreferredSize(new Dimension(420, 180));
        bottomTabbedPane = new JTabbedPane();
        messagesTextArea = new JTextArea(8, 32);
        messagesTextArea.setEditable(false);
        bottomTabbedPane.addTab("Messages", new JScrollPane(messagesTextArea));
        bottomPanel.add(bottomTabbedPane, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel(new BorderLayout(0, 8));
        centerPanel.setOpaque(false);
        centerPanel.add(drawerPanel, BorderLayout.NORTH);
        contentSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        contentSplitPane.setBorder(BorderFactory.createEmptyBorder());
        contentSplitPane.setResizeWeight(0.82);
        contentSplitPane.setTopComponent(networkViewportPanel);
        contentSplitPane.setBottomComponent(bottomPanel);
        centerPanel.add(contentSplitPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
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

    public JButton getFullscreenButton() {
        return fullscreenButton;
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

    public JTextArea getMessagesTextArea() {
        return messagesTextArea;
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
        setupExpanded = expanded;
        drawerPanel.setVisible(expanded);
        setupToggleButton.setText(expanded ? "Hide Setup" : "Show Setup");
        revalidate();
        repaint();
    }

    public void setBottomExpanded(boolean expanded) {
        bottomExpanded = expanded;
        contentSplitPane.getBottomComponent().setVisible(expanded);
        contentSplitPane.setDividerSize(expanded ? 8 : 0);
        if (expanded) {
            contentSplitPane.setDividerLocation(Math.max(220, getHeight() - 200));
        }
        bottomToggleButton.setText(expanded ? "Hide Results" : "Show Results");
        revalidate();
        repaint();
    }

    public boolean isSetupExpanded() {
        return setupExpanded;
    }

    public boolean isBottomExpanded() {
        return bottomExpanded;
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

    private void showBottomOverflowMenu() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem openResultsWindow = new JMenuItem("Open Results In Window");
        openResultsWindow.addActionListener(evt -> openBottomPanelInWindow());
        menu.add(openResultsWindow);
        menu.show(bottomOverflowButton, 0, bottomOverflowButton.getHeight());
    }

    private void openBottomPanelInWindow() {
        Window owner = javax.swing.SwingUtilities.getWindowAncestor(this);
        javax.swing.JDialog dialog = new javax.swing.JDialog(owner, titleLabel.getText() + " Results", java.awt.Dialog.ModalityType.MODELESS);
        JTabbedPane detachedTabs = new JTabbedPane();
        if (outcomesPanels != null) {
            for (OutcomesPanel outcomesPanel : outcomesPanels) {
                JTextArea summaryArea = new JTextArea(
                        outcomesPanel.getLabel()
                                + "\n\nThis detached window is a larger companion view for dual mode.\n"
                                + "The live chart remains in the main workspace tab to keep the network visible.");
                summaryArea.setEditable(false);
                summaryArea.setLineWrap(true);
                summaryArea.setWrapStyleWord(true);
                detachedTabs.addTab(outcomesPanel.getLabel(), new JScrollPane(summaryArea));
            }
        }
        JTextArea detachedMessagesArea = new JTextArea(messagesTextArea.getText());
        detachedMessagesArea.setEditable(false);
        detachedMessagesArea.setLineWrap(true);
        detachedMessagesArea.setWrapStyleWord(true);
        detachedTabs.addTab("Messages", new JScrollPane(detachedMessagesArea));
        dialog.setContentPane(detachedTabs);
        dialog.setSize(720, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
