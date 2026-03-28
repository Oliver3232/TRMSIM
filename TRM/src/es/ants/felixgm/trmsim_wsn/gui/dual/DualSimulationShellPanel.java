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
import java.awt.FlowLayout;
import java.awt.Insets;

/**
 * Dual mode shell with slot-specific controls on the sides and shared session controls in the middle.
 */
public final class DualSimulationShellPanel extends JPanel {
    private static final int TOOLBAR_BUTTON_WIDTH = 118;
    private static final int TOOLBAR_BUTTON_HEIGHT = 22;
    private static final int TOOLBAR_BUTTON_GAP = 4;

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
            trustModelComboBox.setPreferredSize(new Dimension(TOOLBAR_BUTTON_WIDTH, TOOLBAR_BUTTON_HEIGHT));
            trustModelComboBox.setMinimumSize(new Dimension(100, TOOLBAR_BUTTON_HEIGHT));
            trustModelComboBox.setMaximumSize(new Dimension(TOOLBAR_BUTTON_WIDTH, TOOLBAR_BUTTON_HEIGHT));
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
            button.setMaximumSize(new Dimension(TOOLBAR_BUTTON_WIDTH, TOOLBAR_BUTTON_HEIGHT));
            button.setMinimumSize(new Dimension(100, TOOLBAR_BUTTON_HEIGHT));
            button.setPreferredSize(new Dimension(TOOLBAR_BUTTON_WIDTH, TOOLBAR_BUTTON_HEIGHT));
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

        JPanel topBar = new JPanel(new BorderLayout(0, 4));
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));

        JPanel centerToolbar = new JPanel(new FlowLayout(FlowLayout.CENTER, TOOLBAR_BUTTON_GAP, 4));
        centerToolbar.setOpaque(false);
        centerToolbar.add(sessionRunButton);
        centerToolbar.add(sessionStopButton);
        centerToolbar.add(exportButton);
        centerToolbar.add(modeSwitchButton);
        Dimension centerToolbarSize = new Dimension(
                (TOOLBAR_BUTTON_WIDTH * 4) + (TOOLBAR_BUTTON_GAP * 3),
                TOOLBAR_BUTTON_HEIGHT + 8);
        centerToolbar.setMinimumSize(centerToolbarSize);
        centerToolbar.setPreferredSize(centerToolbarSize);

        JPanel primaryContainer = new JPanel(new BorderLayout());
        primaryContainer.setOpaque(false);
        primaryContainer.add(primaryToolbarPanel, BorderLayout.WEST);

        JPanel secondaryContainer = new JPanel(new BorderLayout());
        secondaryContainer.setOpaque(false);
        secondaryContainer.add(secondaryToolbarPanel, BorderLayout.EAST);

        JPanel slotToolbarRow = new JPanel(new BorderLayout());
        slotToolbarRow.setOpaque(false);
        slotToolbarRow.add(primaryContainer, BorderLayout.WEST);
        slotToolbarRow.add(secondaryContainer, BorderLayout.EAST);

        topBar.add(centerToolbar, BorderLayout.NORTH);
        topBar.add(slotToolbarRow, BorderLayout.CENTER);

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
        button.setMaximumSize(new Dimension(TOOLBAR_BUTTON_WIDTH, TOOLBAR_BUTTON_HEIGHT));
        button.setMinimumSize(new Dimension(100, TOOLBAR_BUTTON_HEIGHT));
        button.setPreferredSize(new Dimension(TOOLBAR_BUTTON_WIDTH, TOOLBAR_BUTTON_HEIGHT));
        button.setAlignmentX(0.5f);
    }
}
