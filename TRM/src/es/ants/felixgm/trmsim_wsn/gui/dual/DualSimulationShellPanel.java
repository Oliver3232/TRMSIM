package es.ants.felixgm.trmsim_wsn.gui.dual;

import es.ants.felixgm.trmsim_wsn.SimulationSlot;
import es.ants.felixgm.trmsim_wsn.gui.layout.WrapLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Dual mode shell with slot-specific controls on the sides and shared session controls in the middle.
 */
public final class DualSimulationShellPanel extends JPanel {
    public static final class SlotToolbarPanel extends JPanel {
        private final JButton newNetworkButton = new JButton("New WSN");
        private final JButton loadNetworkButton = new JButton("Load WSN");
        private final JButton saveNetworkButton = new JButton("Save WSN");
        private final JButton resetNetworkButton = new JButton("Reset WSN");
        private final JButton runStopButton = new JButton("Run T&R");
        private final JComboBox<String> trustModelComboBox = new JComboBox<String>();
        private final boolean alignRight;

        private SlotToolbarPanel(boolean alignRight) {
            this.alignRight = alignRight;
            setOpaque(false);
            setLayout(new WrapLayout(alignRight ? WrapLayout.RIGHT : WrapLayout.LEFT, 4, 4));
            configureButton(newNetworkButton);
            configureButton(loadNetworkButton);
            configureButton(saveNetworkButton);
            configureButton(resetNetworkButton);
            configureButton(runStopButton);
            trustModelComboBox.setPreferredSize(new Dimension(118, 22));
            trustModelComboBox.setMinimumSize(new Dimension(100, 22));
            trustModelComboBox.setMaximumSize(new Dimension(118, 22));
            if (alignRight) {
                add(trustModelComboBox);
                add(runStopButton);
                add(resetNetworkButton);
                add(saveNetworkButton);
                add(loadNetworkButton);
                add(newNetworkButton);
            } else {
                add(newNetworkButton);
                add(loadNetworkButton);
                add(saveNetworkButton);
                add(resetNetworkButton);
                add(runStopButton);
                add(trustModelComboBox);
            }
        }

        private void configureButton(JButton button) {
            button.setMargin(new Insets(2, 4, 2, 4));
            button.setMaximumSize(new Dimension(118, 22));
            button.setMinimumSize(new Dimension(100, 22));
            button.setPreferredSize(new Dimension(118, 22));
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

        public JButton getRunStopButton() {
            return runStopButton;
        }

        public JComboBox<String> getTrustModelComboBox() {
            return trustModelComboBox;
        }
    }

    private final JButton sessionRunButton;
    private final JButton sessionStopButton;
    private final JButton exportButton;
    private final JButton modeSwitchButton;
    private final SlotToolbarPanel primaryToolbarPanel;
    private final SlotToolbarPanel secondaryToolbarPanel;
    private final DualSimulationWorkspacePanel primaryWorkspacePanel;
    private final DualSimulationWorkspacePanel secondaryWorkspacePanel;

    public DualSimulationShellPanel() {
        setLayout(new BorderLayout(0, 8));
        setOpaque(false);

        primaryToolbarPanel = new SlotToolbarPanel(false);
        secondaryToolbarPanel = new SlotToolbarPanel(true);
        sessionRunButton = new JButton("Run");
        sessionStopButton = new JButton("Stop");
        exportButton = new JButton("Export");
        modeSwitchButton = new JButton("Single Mode");
        configureCenterButton(sessionRunButton);
        configureCenterButton(sessionStopButton);
        configureCenterButton(exportButton);
        configureCenterButton(modeSwitchButton);

        JPanel topBar = new JPanel(new GridBagLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));

        JPanel centerToolbar = new JPanel(new WrapLayout(WrapLayout.CENTER, 4, 4));
        centerToolbar.setOpaque(false);
        centerToolbar.add(sessionRunButton);
        centerToolbar.add(sessionStopButton);
        centerToolbar.add(exportButton);
        centerToolbar.add(modeSwitchButton);

        JPanel primaryContainer = new JPanel(new BorderLayout());
        primaryContainer.setOpaque(false);
        primaryContainer.add(primaryToolbarPanel, BorderLayout.WEST);

        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setOpaque(false);
        centerContainer.add(centerToolbar, BorderLayout.CENTER);

        JPanel secondaryContainer = new JPanel(new BorderLayout());
        secondaryContainer.setOpaque(false);
        secondaryContainer.add(secondaryToolbarPanel, BorderLayout.EAST);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);

        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        topBar.add(primaryContainer, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.CENTER;
        topBar.add(centerContainer, gbc);

        gbc.gridx = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        topBar.add(secondaryContainer, gbc);

        primaryWorkspacePanel = new DualSimulationWorkspacePanel(SimulationSlot.PRIMARY);
        secondaryWorkspacePanel = new DualSimulationWorkspacePanel(SimulationSlot.SECONDARY);

        JSplitPane workspaceSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, primaryWorkspacePanel, secondaryWorkspacePanel);
        workspaceSplitPane.setResizeWeight(0.5);
        workspaceSplitPane.setBorder(BorderFactory.createEmptyBorder());
        workspaceSplitPane.setContinuousLayout(true);
        workspaceSplitPane.setPreferredSize(new Dimension(1100, 760));

        add(topBar, BorderLayout.NORTH);
        add(workspaceSplitPane, BorderLayout.CENTER);
    }

    public JButton getSessionRunButton() {
        return sessionRunButton;
    }

    public JButton getSessionStopButton() {
        return sessionStopButton;
    }

    public JButton getExportButton() {
        return exportButton;
    }

    public JButton getModeSwitchButton() {
        return modeSwitchButton;
    }

    public SlotToolbarPanel getPrimaryToolbarPanel() {
        return primaryToolbarPanel;
    }

    public SlotToolbarPanel getSecondaryToolbarPanel() {
        return secondaryToolbarPanel;
    }

    public DualSimulationWorkspacePanel getPrimaryWorkspacePanel() {
        return primaryWorkspacePanel;
    }

    public DualSimulationWorkspacePanel getSecondaryWorkspacePanel() {
        return secondaryWorkspacePanel;
    }

    private void configureCenterButton(JButton button) {
        button.setMargin(new Insets(2, 4, 2, 4));
        button.setMaximumSize(new Dimension(118, 22));
        button.setMinimumSize(new Dimension(100, 22));
        button.setPreferredSize(new Dimension(118, 22));
        button.setAlignmentX(0.5f);
    }
}
