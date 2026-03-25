/**
 *  "TRMSim-WSN, Trust and Reputation Models Simulator for Wireless 
 * Sensor Networks" is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of 
 * the License, or (at your option) any later version always keeping 
 * the additional terms specified in this license.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * 
 * Additional Terms of this License
 * --------------------------------
 * 
 * 1. It is Required the preservation of specified reasonable legal notices
 *   and author attributions in that material and in the Appropriate Legal
 *   Notices displayed by works containing it.
 * 
 * 2. It is limited the use for publicity purposes of names of licensors or
 *   authors of the material.
 * 
 * 3. It is Required indemnification of licensors and authors of that material
 *   by anyone who conveys the material (or modified versions of it) with
 *   contractual assumptions of liability to the recipient, for any liability
 *   that these contractual assumptions directly impose on those licensors
 *   and authors.
 * 
 * 4. It is Prohibited misrepresentation of the origin of that material, and it is
 *   required that modified versions of such material be marked in reasonable
 *   ways as different from the original version.
 * 
 * 5. It is Declined to grant rights under trademark law for use of some trade
 *   names, trademarks, or service marks.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program (lgpl.txt).  If not, see <http://www.gnu.org/licenses/>
*/

package es.ants.felixgm.trmsim_wsn.gui;


import es.ants.felixgm.trmsim_wsn.Controller;
import es.ants.felixgm.trmsim_wsn.app.BatchSimulationConfig;
import es.ants.felixgm.trmsim_wsn.app.NetworkGenerationConfig;
import es.ants.felixgm.trmsim_wsn.app.SimulationApplicationService;
import es.ants.felixgm.trmsim_wsn.app.SimulationConfig;
import es.ants.felixgm.trmsim_wsn.SimulationListener;
import es.ants.felixgm.trmsim_wsn.SimulationSlot;
import es.ants.felixgm.trmsim_wsn.VerboseSimulationRunner;

import es.ants.felixgm.trmsim_wsn.gui.dual.DualSimulationShellPanel;
import es.ants.felixgm.trmsim_wsn.gui.layout.CompactLegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.layout.MiniLegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.legendpanels.LegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.events.SimulationEventHelper;
import es.ants.felixgm.trmsim_wsn.gui.export.DualSimulationExportHelper;
import es.ants.felixgm.trmsim_wsn.gui.export.SimulationResultRepository;
import es.ants.felixgm.trmsim_wsn.gui.graph.SimulationGraphWorkspace;
import es.ants.felixgm.trmsim_wsn.gui.legendpanels.LegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowActionController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowConfigurationController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowInitializationController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowNodeInspectorController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowRenderController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowSimulationController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowTrustModelController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowParametersController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowSimulationControlsController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowUiStateController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.hosts.MainWindowNodeInspectorHostFactory;
import es.ants.felixgm.trmsim_wsn.gui.network.NetworkFileHelper;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.EigenTrustNetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.PowerTrustNetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.TRIPNetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.OutcomesPanel;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.TRMParametersPanel;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.TRMParametersPanelFactory;
import es.ants.felixgm.trmsim_wsn.gui.support.NetworkRenderSupport;
import es.ants.felixgm.trmsim_wsn.gui.support.SimulationUiHelper;
import es.ants.felixgm.trmsim_wsn.gui.trustmodel.TrustModelUiFactory;

import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Service;

import es.ants.felixgm.trmsim_wsn.outcomes.Outcome;
import es.ants.felixgm.trmsim_wsn.trm.TrustModelRegistry;
import es.ants.felixgm.trmsim_wsn.trm.btrm_wsn.BTRM_WSN;
import es.ants.felixgm.trmsim_wsn.trm.eigentrust.EigenTrust;
import es.ants.felixgm.trmsim_wsn.trm.lftm.LFTM;
import es.ants.felixgm.trmsim_wsn.trm.peertrust.PeerTrust;
import es.ants.felixgm.trmsim_wsn.trm.powertrust.PowerTrust;
import es.ants.felixgm.trmsim_wsn.trm.trip.TRIP;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLayeredPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.util.logging.Logger;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
/**
 * <p>This class represents the main window of TRMSim-WSN</p>
 * <ul>
 *   <li>F&eacute;lix G&oacute;mez M&aacute;rmol, Gregorio Mart&iacute;nez
 *       P&eacute;rez, "<strong>TRMSim-WSN, Trust and Reputation Models
 *       Simulator for Wireless Sensor Networks</strong>",
 *       <a href="http://www.ieee-icc.org/2009" target="_blank"> IEEE International
 *       Conference on Communications (IEEE ICC 2009), Communication and Information
 *       Systems Security Symposium</a>, Dresden, Germany, 14-18 June 2009
 *       <a href="http://ants.dif.um.es/~felixgm/pub/conferences/09/GomezMarmol-ICC09.pdf" target="_blank"><img src="http://ants.dif.um.es/~felixgm/img/adobe.gif" border="0"></a>
 *   </li>
 * </ul>
 * @author <a href="http://ants.dif.um.es/~felixgm/en" target="_blank">F&eacute;lix G&oacute;mez M&aacute;rmol</a>, <a href="http://webs.um.es/gregorio" target="_blank">Gregorio Mart&iacute;nez P&eacute;rez</a>
 * @version 0.5
 * @since 0.1
 */
public class TRMSim_WSN extends javax.swing.JFrame implements SimulationListener {
    static final Logger LOGGER = Logger.getLogger(TRMSim_WSN.class.getName());

    enum BatchSimulationState {
        IDLE,
        RUNNING,
        PAUSED
    }
    /**
    * Current version of TRMSim-WSN: {@value}
    */
    public static final String CURRENT_VERSION = "0.5";
    
    protected static Controller C;
    SimulationApplicationService simulationService;
    AppMode appMode = AppMode.SINGLE;
    DualSimulationShellPanel dualSimulationShellPanel;
    Component singleModeNorthComponent;
    Component singleModeWestComponent;
    Component singleModeCenterComponent;
    javax.swing.Timer dualNetworkRefreshTimer;
    boolean dualSessionStartPending = false;
    final Map<SimulationSlot, Collection<OutcomesPanel>> dualOutcomesPanels =
            new EnumMap<SimulationSlot, Collection<OutcomesPanel>>(SimulationSlot.class);
    final Map<SimulationSlot, LegendPanel> dualLegendPanels =
            new EnumMap<SimulationSlot, LegendPanel>(SimulationSlot.class);
    final Map<SimulationSlot, SimulationGraphWorkspace> dualGraphWorkspaces =
            new EnumMap<SimulationSlot, SimulationGraphWorkspace>(SimulationSlot.class);
    final Map<SimulationSlot, Integer> dualSelectedNodeIds =
            new EnumMap<SimulationSlot, Integer>(SimulationSlot.class);
    final Map<SimulationSlot, TRMParametersPanel> dualParametersPanels =
            new EnumMap<SimulationSlot, TRMParametersPanel>(SimulationSlot.class);
    final Map<SimulationSlot, JButton> dualParameterApplyButtons =
            new EnumMap<SimulationSlot, JButton>(SimulationSlot.class);
    final Map<SimulationSlot, Boolean> dualTrustModelSelectionSync =
            new EnumMap<SimulationSlot, Boolean>(SimulationSlot.class);
    final Map<SimulationSlot, DualSettingsPanel> dualSettingsPanels =
            new EnumMap<SimulationSlot, DualSettingsPanel>(SimulationSlot.class);
    final Map<SimulationSlot, Controller> dualControllers =
            new EnumMap<SimulationSlot, Controller>(SimulationSlot.class);
    final Map<SimulationSlot, SimulationApplicationService> dualSimulationServices =
            new EnumMap<SimulationSlot, SimulationApplicationService>(SimulationSlot.class);

    public static void main(String[] args) {
        if ((args != null) && (args.length > 0) && "--verbose".equals(args[0])) {
            VerboseSimulationRunner.runVerbose(
                    new String[] {BTRM_WSN.get_name(), LFTM.get_name()},
                    new Service("My service"),
                    1,
                    20,
                    100,
                    100,
                    0.15,
                    0.05,
                    0.5,
                    false,
                    false,
                    false,
                    CURRENT_VERSION);
            return;
        }
        TRMSim_WSN_GUI();
    }

    public static void TRMSim_WSN_GUI() {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(new FlatIntelliJLaf()); } catch (Exception ignored) {}
            TRMSim_WSN window = new TRMSim_WSN();
            window.setVisible(true);
        });
    }

    /**
     * Creates new form TRMSim_WSN
     */
    public TRMSim_WSN() {
        try {
            initComponents();
            initializeDualModeShell();
            MainWindowInitializationController.initialize(MainWindowHosts.initialization(this));
            syncDualShellFromControllerState();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    void initializeTRModels() { MainWindowTrustModelController.initialize(new MainWindowContext(this)); }

    void initializeDualModeShell() {
        initializeDualControllers();
        dualSimulationShellPanel = new DualSimulationShellPanel();
        initializeDualGraphWorkspace(SimulationSlot.PRIMARY);
        initializeDualGraphWorkspace(SimulationSlot.SECONDARY);
        dualSimulationShellPanel.getSessionRunButton().addActionListener(evt -> handleDualSessionRunPauseResume());
        dualSimulationShellPanel.getSessionStopButton().addActionListener(evt -> handleDualSessionStop());
        dualSimulationShellPanel.getModeSwitchButton().addActionListener(evt -> switchAppMode(AppMode.SINGLE));
        dualSimulationShellPanel.getExportButton().addActionListener(evt -> showDualExportDialog());
        configureDualWorkspace(SimulationSlot.PRIMARY, dualSimulationShellPanel.getPrimaryWorkspacePanel());
        configureDualWorkspace(SimulationSlot.SECONDARY, dualSimulationShellPanel.getSecondaryWorkspacePanel());
        updateDualSessionControls();
    }

    private void initializeDualControllers() {
        if (!dualControllers.isEmpty()) {
            return;
        }
        try {
            Controller primaryController = Controller.createIndependent();
            Controller secondaryController = Controller.createIndependent();
            dualControllers.put(SimulationSlot.PRIMARY, primaryController);
            dualControllers.put(SimulationSlot.SECONDARY, secondaryController);
            dualSimulationServices.put(SimulationSlot.PRIMARY, new SimulationApplicationService(primaryController));
            dualSimulationServices.put(SimulationSlot.SECONDARY, new SimulationApplicationService(secondaryController));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to initialize dual simulation controllers", ex);
        }
    }

    private Controller dualController(SimulationSlot slot) {
        return dualControllers.get(slot);
    }

    private SimulationApplicationService dualSimulationService(SimulationSlot slot) {
        return dualSimulationServices.get(slot);
    }

    private boolean isAnyDualSimulationRunning() {
        for (SimulationSlot slot : SimulationSlot.values()) {
            Controller controller = dualController(slot);
            if (controller != null && controller.isSimulationRunning(slot)) {
                return true;
            }
        }
        return false;
    }

    private boolean areAllDualRunningSimulationsPaused() {
        boolean hasRunningSimulation = false;
        for (SimulationSlot slot : SimulationSlot.values()) {
            Controller controller = dualController(slot);
            if (controller != null && controller.isSimulationRunning(slot)) {
                hasRunningSimulation = true;
                if (!controller.isSimulationPaused(slot)) {
                    return false;
                }
            }
        }
        return hasRunningSimulation;
    }

    private void initializeDualGraphWorkspace(SimulationSlot slot) {
        SimulationGraphWorkspace workspace = new SimulationGraphWorkspace(
                panel -> renderCurrentDualNetworkOnPanel(slot, panel));
        workspace.initializeControls();
        workspace.setNodeSelectionListener(nodeId -> {
            dualSelectedNodeIds.put(slot, nodeId);
            if (nodeId == null) {
                workspace.updateSelectedNodeSummary(
                        "No node selected",
                        "Click any node in the graph to inspect its live state and exported metrics.");
                return;
            }
            refreshDualSelectedNodeDetails(slot, nodeId.intValue());
        });
        workspace.setSimulationControlListener(new SimulationGraphWorkspace.SimulationControlListener() {
            @Override
            public void onPauseResumeRequested() {
                handleDualSessionRunPauseResume();
            }

            @Override
            public void onStopRequested() {
                handleDualSessionStop();
            }
        });
        workspace.setDisplayControlListener(new SimulationGraphWorkspace.DisplayControlListener() {
            @Override
            public void onShowIdsChanged(boolean selected) {
                showIdsCheckBox.setSelected(selected);
                refreshDualNetworksIfNeeded();
            }

            @Override
            public void onShowLinksChanged(boolean selected) {
                showLinksCheckBox.setSelected(selected);
                refreshDualNetworksIfNeeded();
            }

            @Override
            public void onShowRangesChanged(boolean selected) {
                showRangesCheckBox.setSelected(selected);
                refreshDualNetworksIfNeeded();
            }

            @Override
            public void onShowGridChanged(boolean selected) {
                showGridCheckBox.setSelected(selected);
                refreshDualNetworksIfNeeded();
            }

            @Override
            public void onDelayChanged(int value) {
                delaySlider.setValue(value);
            }
        });
        dualGraphWorkspaces.put(slot, workspace);
    }

    private void configureDualWorkspace(SimulationSlot slot, es.ants.felixgm.trmsim_wsn.gui.dual.DualSimulationWorkspacePanel workspacePanel) {
        DualSimulationShellPanel.SlotToolbarPanel toolbarPanel = dualToolbarPanel(slot);
        toolbarPanel.getTrustModelComboBox().removeAllItems();
        for (String modelName : TrustModelRegistry.all().keySet()) {
            toolbarPanel.getTrustModelComboBox().addItem(modelName);
        }
        toolbarPanel.getTrustModelComboBox().addActionListener(evt -> applyDualTrustModelSelection(slot));
        toolbarPanel.getNewNetworkButton().addActionListener(evt -> createDualNetwork(slot));
        toolbarPanel.getLoadNetworkButton().addActionListener(evt -> loadDualNetwork(slot));
        toolbarPanel.getSaveNetworkButton().addActionListener(evt -> saveDualNetwork(slot));
        toolbarPanel.getResetNetworkButton().addActionListener(evt -> resetDualNetwork(slot));
        toolbarPanel.getRunStopButton().addActionListener(evt -> handleDualSlotRunStop(slot));
        workspacePanel.getFullscreenButton().addActionListener(evt -> openDualFullscreen(slot));
        rebuildDualWorkspaceUi(slot);
    }

    private DualSimulationShellPanel.SlotToolbarPanel dualToolbarPanel(SimulationSlot slot) {
        return slot == SimulationSlot.PRIMARY
                ? dualSimulationShellPanel.getPrimaryToolbarPanel()
                : dualSimulationShellPanel.getSecondaryToolbarPanel();
    }

    public void switchAppMode(AppMode mode) {
        if (mode == appMode) {
            return;
        }
        if (mode == AppMode.DUAL) {
            resetDualModeState();
        }
        appMode = mode;
        applyAppModeLayout(mode);
        if (modeSwitchButton != null) {
            modeSwitchButton.setText(mode == AppMode.DUAL ? "Single Mode" : "Dual Mode");
        }
        updateDualSessionControls();
        updateDualRefreshTimer();
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    private void resetDualModeState() {
        dualSessionStartPending = false;
        for (SimulationSlot slot : SimulationSlot.values()) {
            Controller controller = dualController(slot);
            if (controller != null) {
                controller.stopAllSimulations();
                controller.clearCurrentNetwork(slot);
            }
            SimulationResultRepository.getInstance(slot).clearRepository();
            dualSelectedNodeIds.remove(slot);
            if (dualSimulationShellPanel != null) {
                es.ants.felixgm.trmsim_wsn.gui.dual.DualSimulationWorkspacePanel workspacePanel = dualWorkspacePanel(slot);
                workspacePanel.getMessagesTextArea().setText("");
                workspacePanel.setSelectedTrustModelName(null);
                workspacePanel.setSetupExpanded(false);
                workspacePanel.setBottomExpanded(true);
                SimulationUiHelper.resetOutcomePanels(workspacePanel.getOutcomesPanels());
                renderDualWorkspaceNetwork(slot, null);
            }
            DualSimulationShellPanel.SlotToolbarPanel toolbarPanel = dualToolbarPanel(slot);
            if (toolbarPanel.getTrustModelComboBox().getItemCount() > 0) {
                dualTrustModelSelectionSync.put(slot, Boolean.TRUE);
                try {
                    toolbarPanel.getTrustModelComboBox().setSelectedIndex(0);
                } finally {
                    dualTrustModelSelectionSync.put(slot, Boolean.FALSE);
                }
            }
            rebuildDualWorkspaceUi(slot);
        }
    }

    void syncDualShellFromControllerState() {
        if (dualSimulationShellPanel == null) {
            return;
        }
        syncDualWorkspaceFromControllerState(SimulationSlot.PRIMARY);
        syncDualWorkspaceFromControllerState(SimulationSlot.SECONDARY);
    }

    private void syncDualWorkspaceFromControllerState(SimulationSlot slot) {
        Controller controller = dualController(slot);
        if (controller == null) {
            return;
        }
        es.ants.felixgm.trmsim_wsn.gui.dual.DualSimulationWorkspacePanel workspacePanel = dualWorkspacePanel(slot);
        String trustModelName = controller.getTrustModelName(slot);
        if ((trustModelName == null || trustModelName.trim().isEmpty())
                && dualToolbarPanel(slot).getTrustModelComboBox().getItemCount() > 0) {
            trustModelName = dualToolbarPanel(slot).getTrustModelComboBox().getItemAt(0);
            try {
                controller.set_TRModel_WSN(slot, trustModelName);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
        if (trustModelName != null) {
            dualTrustModelSelectionSync.put(slot, Boolean.TRUE);
            try {
                dualToolbarPanel(slot).getTrustModelComboBox().setSelectedItem(trustModelName);
            } finally {
                dualTrustModelSelectionSync.put(slot, Boolean.FALSE);
            }
        }
        workspacePanel.setSelectedTrustModelName(trustModelName);
        rebuildDualWorkspaceUi(slot);
        renderDualWorkspaceNetwork(slot, controller.get_currentNetwork(slot));
    }

    void handleDualSessionRunPauseResume() {
        try {
            if (!isAnyDualSimulationRunning()) {
                startDualSession();
                return;
            }
            if (areAllDualRunningSimulationsPaused()) {
                for (SimulationSlot slot : SimulationSlot.values()) {
                    Controller controller = dualController(slot);
                    if (controller != null) {
                        controller.resumeSimulation(slot);
                    }
                }
            } else {
                for (SimulationSlot slot : SimulationSlot.values()) {
                    Controller controller = dualController(slot);
                    if (controller != null) {
                        controller.pauseSimulation(slot);
                    }
                }
            }
            updateDualSessionControls();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    void handleDualSessionStop() {
        try {
            for (SimulationSlot slot : SimulationSlot.values()) {
                Controller controller = dualController(slot);
                if (controller != null) {
                    controller.stopSimulations(slot);
                }
            }
            dualSessionStartPending = false;
            updateDualSessionControls();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    void updateDualSessionControls() {
        if (dualSimulationShellPanel == null) {
            return;
        }
        boolean anyRunning = isAnyDualSimulationRunning();
        if (dualSessionStartPending && anyRunning) {
            dualSessionStartPending = false;
        }
        boolean allPaused = anyRunning && areAllDualRunningSimulationsPaused();
        boolean treatAsRunning = anyRunning || dualSessionStartPending;
        dualSimulationShellPanel.getSessionRunButton().setText(treatAsRunning ? (allPaused ? "Resume" : "Pause") : "Run");
        dualSimulationShellPanel.getSessionStopButton().setEnabled(treatAsRunning);
        dualSimulationShellPanel.getPrimaryToolbarPanel().getRunStopButton().setText(
                ((dualController(SimulationSlot.PRIMARY) != null) && dualController(SimulationSlot.PRIMARY).isSimulationRunning(SimulationSlot.PRIMARY)) ? "Stop T&R" : "Run T&R");
        dualSimulationShellPanel.getSecondaryToolbarPanel().getRunStopButton().setText(
                ((dualController(SimulationSlot.SECONDARY) != null) && dualController(SimulationSlot.SECONDARY).isSimulationRunning(SimulationSlot.SECONDARY)) ? "Stop T&R" : "Run T&R");
        updateDualGraphWorkspaceControls(SimulationSlot.PRIMARY);
        updateDualGraphWorkspaceControls(SimulationSlot.SECONDARY);
        setDualWorkspaceSetupEnabled(!anyRunning);
        updateDualRefreshTimer();
    }

    private void updateDualRefreshTimer() {
        boolean shouldRefresh = (appMode == AppMode.DUAL)
                && (dualSessionStartPending || isAnyDualSimulationRunning());
        if (!shouldRefresh) {
            if (dualNetworkRefreshTimer != null && dualNetworkRefreshTimer.isRunning()) {
                dualNetworkRefreshTimer.stop();
            }
            return;
        }
        if (dualNetworkRefreshTimer == null) {
            dualNetworkRefreshTimer = new javax.swing.Timer(90, evt -> {
                refreshDualNetworksIfNeeded();
                repaintDualOutcomePanels();
            });
            dualNetworkRefreshTimer.setRepeats(true);
        }
        if (!dualNetworkRefreshTimer.isRunning()) {
            dualNetworkRefreshTimer.start();
        }
    }

    private void repaintDualOutcomePanels() {
        for (SimulationSlot slot : SimulationSlot.values()) {
            es.ants.felixgm.trmsim_wsn.gui.dual.DualSimulationWorkspacePanel workspacePanel = dualWorkspacePanel(slot);
            if (workspacePanel == null) {
                continue;
            }
            Collection<OutcomesPanel> slotOutcomesPanels = workspacePanel.getOutcomesPanels();
            if (slotOutcomesPanels == null) {
                continue;
            }
            for (OutcomesPanel outcomesPanel : slotOutcomesPanels) {
                outcomesPanel.revalidate();
                outcomesPanel.repaint();
            }
        }
    }

    private void updateDualGraphWorkspaceControls(SimulationSlot slot) {
        SimulationGraphWorkspace workspace = dualGraphWorkspaces.get(slot);
        Controller controller = dualController(slot);
        if (workspace == null || controller == null) {
            return;
        }
        boolean running = controller.isSimulationRunning(slot);
        boolean paused = running && controller.isSimulationPaused(slot);
        workspace.updateSimulationControlsState(
                running ? (paused ? "Paused" : "Running") : "Idle",
                "Run T&R",
                paused ? "Resume" : "Pause",
                !running,
                running,
                running);
    }

    private void setDualWorkspaceSetupEnabled(boolean enabled) {
        setDualWorkspaceSetupEnabled(dualSimulationShellPanel.getPrimaryWorkspacePanel(), enabled);
        setDualWorkspaceSetupEnabled(dualSimulationShellPanel.getSecondaryWorkspacePanel(), enabled);
        setDualToolbarEnabled(dualToolbarPanel(SimulationSlot.PRIMARY), enabled);
        setDualToolbarEnabled(dualToolbarPanel(SimulationSlot.SECONDARY), enabled);
    }

    private void setDualWorkspaceSetupEnabled(
            es.ants.felixgm.trmsim_wsn.gui.dual.DualSimulationWorkspacePanel workspacePanel,
            boolean enabled) {
        if (!enabled) {
            workspacePanel.setSetupExpanded(false);
            workspacePanel.setBottomExpanded(true);
        }
    }

    private void setDualToolbarEnabled(DualSimulationShellPanel.SlotToolbarPanel toolbarPanel, boolean enabled) {
        toolbarPanel.getTrustModelComboBox().setEnabled(enabled);
        toolbarPanel.getNewNetworkButton().setEnabled(enabled);
        toolbarPanel.getLoadNetworkButton().setEnabled(enabled);
        toolbarPanel.getSaveNetworkButton().setEnabled(enabled);
        toolbarPanel.getResetNetworkButton().setEnabled(enabled);
        for (SimulationSlot slot : SimulationSlot.values()) {
            DualSettingsPanel settingsPanel = dualSettingsPanels.get(slot);
            if (settingsPanel != null) {
                settingsPanel.setEnabled(enabled);
            }
            TRMParametersPanel parametersPanel = dualParametersPanels.get(slot);
            if (parametersPanel != null) {
                parametersPanel.setEnabled(enabled);
            }
            JButton applyButton = dualParameterApplyButtons.get(slot);
            if (applyButton != null) {
                applyButton.setEnabled(enabled);
            }
        }
    }

    private es.ants.felixgm.trmsim_wsn.gui.dual.DualSimulationWorkspacePanel dualWorkspacePanel(SimulationSlot slot) {
        return slot == SimulationSlot.PRIMARY
                ? dualSimulationShellPanel.getPrimaryWorkspacePanel()
                : dualSimulationShellPanel.getSecondaryWorkspacePanel();
    }

    private void applyDualTrustModelSelection(SimulationSlot slot) {
        Controller controller = dualController(slot);
        if (controller == null) {
            return;
        }
        if (Boolean.TRUE.equals(dualTrustModelSelectionSync.get(slot))) {
            return;
        }
        es.ants.felixgm.trmsim_wsn.gui.dual.DualSimulationWorkspacePanel workspacePanel = dualWorkspacePanel(slot);
        Object selectedItem = dualToolbarPanel(slot).getTrustModelComboBox().getSelectedItem();
        if (!(selectedItem instanceof String)) {
            return;
        }
        String trustModelName = (String) selectedItem;
        try {
            controller.set_TRModel_WSN(slot, trustModelName);
            controller.clearCurrentNetwork(slot);
            SimulationResultRepository.getInstance(slot).clearRepository();
            dualSelectedNodeIds.remove(slot);
            workspacePanel.setSelectedTrustModelName(trustModelName);
            rebuildDualWorkspaceUi(slot);
            renderDualWorkspaceNetwork(slot, null);
            SimulationUiHelper.resetOutcomePanels(workspacePanel.getOutcomesPanels());
            workspacePanel.getMessagesTextArea().setText(
                    "Model changed to " + trustModelName + ". Network state cleared. Create or load a new WSN.\n");
            updateDualGraphWorkspaceControls(slot);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void rebuildDualWorkspaceUi(SimulationSlot slot) {
        es.ants.felixgm.trmsim_wsn.gui.dual.DualSimulationWorkspacePanel workspacePanel = dualWorkspacePanel(slot);
        String trustModelName = dualToolbarPanel(slot).getTrustModelComboBox().getSelectedItem() instanceof String
                ? (String) dualToolbarPanel(slot).getTrustModelComboBox().getSelectedItem()
                : null;
        if (trustModelName == null || trustModelName.trim().isEmpty()) {
            workspacePanel.setNetworkPanel(new NetworkPanel());
            workspacePanel.setOutcomesPanels(new ArrayList<OutcomesPanel>());
            dualOutcomesPanels.put(slot, workspacePanel.getOutcomesPanels());
            return;
        }
        TrustModelUiFactory.Descriptor descriptor = TrustModelUiFactory.get(trustModelName);
        NetworkPanel slotNetworkPanel = createDualNetworkPanel(trustModelName);
        Collection<OutcomesPanel> slotOutcomesPanels = descriptor.createOutcomesPanels();
        LegendPanel slotLegendPanel = descriptor.createLegendPanel();
        workspacePanel.setNetworkPanel(slotNetworkPanel);
        workspacePanel.setOutcomesPanels(slotOutcomesPanels);
        dualOutcomesPanels.put(slot, slotOutcomesPanels);
        dualLegendPanels.put(slot, slotLegendPanel);
        slotLegendPanel.setBackground(Color.white);
        slotLegendPanel.plotLegend();
        workspacePanel.setSettingsContent(new JScrollPane(createDualSettingsPanel(slot)));
        workspacePanel.setParametersContent(createDualParametersPanel(slot, trustModelName));
        installDualNetworkPanelSelectionHandler(slot, slotNetworkPanel);
        SimulationGraphWorkspace graphWorkspace = dualGraphWorkspaces.get(slot);
        if (graphWorkspace != null) {
            graphWorkspace.setFullscreenLegendItems(MainWindowNodeInspectorController.createLegendItems(slotLegendPanel));
            graphWorkspace.applyVisualizationControlsToPanels(slotNetworkPanel);
            graphWorkspace.updateDisplayControlsState(
                    showIdsCheckBox.isSelected(),
                    showLinksCheckBox.isSelected(),
                    showRangesCheckBox.isSelected(),
                    showGridCheckBox.isSelected(),
                    delaySlider.getValue(),
                    delaySlider.getMinimum(),
                    delaySlider.getMaximum());
        }
    }

    private NetworkPanel createDualNetworkPanel(String trustModelName) {
        return TrustModelUiFactory.get(trustModelName).createNetworkPanel();
    }

    private void createDualNetwork(SimulationSlot slot) {
        SimulationApplicationService service = dualSimulationService(slot);
        if (service == null) {
            return;
        }
        try {
            service.setVisualizationDelay(getSelectedDelayMillis());
            Network network = service.createRandomNetwork(slot, buildDualNetworkGenerationConfig(slot));
            renderDualWorkspaceNetwork(slot, network);
            SimulationUiHelper.resetOutcomePanels(dualWorkspacePanel(slot).getOutcomesPanels());
            dualWorkspacePanel(slot).getMessagesTextArea().setText("");
            prependDualMessage(slot, "New WSN created\n");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void loadDualNetwork(SimulationSlot slot) {
        SimulationApplicationService service = dualSimulationService(slot);
        if (service == null) {
            return;
        }
        try {
            File selectedFile = NetworkFileHelper.chooseXmlFile(this, "./wsn", "Load WSN", JFileChooser.OPEN_DIALOG);
            if (selectedFile == null) {
                return;
            }
            Network network = service.loadNetwork(slot, selectedFile.getCanonicalPath());
            renderDualWorkspaceNetwork(slot, network);
            dualWorkspacePanel(slot).getMessagesTextArea().setText("");
            prependDualMessage(slot, "WSN loaded successfully\n");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void saveDualNetwork(SimulationSlot slot) {
        SimulationApplicationService service = dualSimulationService(slot);
        if (service == null) {
            return;
        }
        try {
            File selectedFile = NetworkFileHelper.chooseXmlFile(this, "./wsn", "Save WSN", JFileChooser.SAVE_DIALOG);
            if (selectedFile == null) {
                return;
            }
            service.saveCurrentNetwork(slot, selectedFile.getCanonicalPath());
            prependDualMessage(slot, "WSN saved successfully\n");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void resetDualNetwork(SimulationSlot slot) {
        SimulationApplicationService service = dualSimulationService(slot);
        Controller controller = dualController(slot);
        if (service == null || controller == null) {
            return;
        }
        try {
            service.resetCurrentNetwork(slot);
            renderDualWorkspaceNetwork(slot, controller.get_currentNetwork(slot));
            prependDualMessage(slot, "Current WSN reset\n");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void startDualSession() throws Exception {
        ensureDualBatchSlotReady(SimulationSlot.PRIMARY);
        ensureDualBatchSlotReady(SimulationSlot.SECONDARY);

        SimulationResultRepository.clearAll();
        SimulationUiHelper.resetOutcomePanels(dualWorkspacePanel(SimulationSlot.PRIMARY).getOutcomesPanels());
        SimulationUiHelper.resetOutcomePanels(dualWorkspacePanel(SimulationSlot.SECONDARY).getOutcomesPanels());
        dualWorkspacePanel(SimulationSlot.PRIMARY).getMessagesTextArea().setText("");
        dualWorkspacePanel(SimulationSlot.SECONDARY).getMessagesTextArea().setText("");

        long dualVisualizationDelay = getEffectiveDualVisualizationDelayMillis();
        dualSimulationService(SimulationSlot.PRIMARY).setVisualizationDelay(dualVisualizationDelay);
        dualSimulationService(SimulationSlot.SECONDARY).setVisualizationDelay(dualVisualizationDelay);
        String startMessage = "Starting simulations at " + (new java.util.Date()) + "...\n";
        prependDualMessage(SimulationSlot.PRIMARY, startMessage);
        prependDualMessage(SimulationSlot.SECONDARY, startMessage);
        dualWorkspacePanel(SimulationSlot.PRIMARY).setSetupExpanded(false);
        dualWorkspacePanel(SimulationSlot.SECONDARY).setSetupExpanded(false);
        dualWorkspacePanel(SimulationSlot.PRIMARY).setBottomExpanded(true);
        dualWorkspacePanel(SimulationSlot.SECONDARY).setBottomExpanded(true);
        dualSessionStartPending = true;
        dualSimulationService(SimulationSlot.PRIMARY).runBatchSimulation(SimulationSlot.PRIMARY, this, buildDualBatchSimulationConfig(SimulationSlot.PRIMARY));
        dualSimulationService(SimulationSlot.SECONDARY).runBatchSimulation(SimulationSlot.SECONDARY, this, buildDualBatchSimulationConfig(SimulationSlot.SECONDARY));
        updateDualSessionControls();
    }

    private void handleDualSlotRunStop(SimulationSlot slot) {
        SimulationApplicationService service = dualSimulationService(slot);
        Controller controller = dualController(slot);
        if (service == null || controller == null) {
            return;
        }
        try {
            if (controller.isSimulationRunning(slot)) {
                controller.stopSimulations(slot);
                prependDualMessage(slot, "Stopped slot simulation.\n");
                updateDualSessionControls();
                return;
            }
            ensureDualSlotReady(slot);
            SimulationResultRepository.getInstance(slot).clearRepository();
            SimulationUiHelper.resetOutcomePanels(dualWorkspacePanel(slot).getOutcomesPanels());
            dualWorkspacePanel(slot).getMessagesTextArea().setText("");
            service.setVisualizationDelay(getEffectiveDualVisualizationDelayMillis());
            prependDualMessage(slot, "Starting slot simulation...\n");
            service.runSimulation(slot, this, buildDualSimulationConfig(slot));
            updateDualSessionControls();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void ensureDualSlotReady(SimulationSlot slot) {
        Controller controller = dualController(slot);
        if (controller == null) {
            throw new IllegalStateException(slotLabel(slot) + " controller is not initialized.");
        }
        String trustModelName = controller.getTrustModelName(slot);
        if (trustModelName == null || trustModelName.trim().isEmpty()) {
            throw new IllegalStateException(slotLabel(slot) + " has no selected trust model.");
        }
        Network currentNetwork = controller.get_currentNetwork(slot);
        if (currentNetwork == null) {
            throw new IllegalStateException(slotLabel(slot) + " has no loaded/generated network.");
        }
        String networkType = currentNetwork.getClass().getName().toLowerCase();
        if (trustModelName.equals(PowerTrust.get_name()) && !networkType.contains("powertrust")) {
            throw new IllegalStateException(slotLabel(slot) + " network does not match PowerTrust. Create or load the WSN again.");
        }
        if (trustModelName.equals(EigenTrust.get_name()) && !networkType.contains("eigentrust")) {
            throw new IllegalStateException(slotLabel(slot) + " network does not match EigenTrust. Create or load the WSN again.");
        }
        if (trustModelName.equals(TRIP.get_name()) && !networkType.contains("trip")) {
            throw new IllegalStateException(slotLabel(slot) + " network does not match TRIP. Create or load the WSN again.");
        }
        if (trustModelName.equals(PeerTrust.get_name()) && !networkType.contains("peertrust")) {
            throw new IllegalStateException(slotLabel(slot) + " network does not match PeerTrust. Create or load the WSN again.");
        }
        if (trustModelName.equals(BTRM_WSN.get_name()) && !networkType.contains("btrm_wsn")) {
            throw new IllegalStateException(slotLabel(slot) + " network does not match BTRM-WSN. Create or load the WSN again.");
        }
    }

    private void ensureDualBatchSlotReady(SimulationSlot slot) {
        Controller controller = dualController(slot);
        if (controller == null) {
            throw new IllegalStateException(slotLabel(slot) + " controller is not initialized.");
        }
        String trustModelName = controller.getTrustModelName(slot);
        if (trustModelName == null || trustModelName.trim().isEmpty()) {
            throw new IllegalStateException(slotLabel(slot) + " has no selected trust model.");
        }
    }

    private String slotLabel(SimulationSlot slot) {
        return slot == SimulationSlot.PRIMARY ? "Simulation A" : "Simulation B";
    }

    private long getEffectiveDualVisualizationDelayMillis() {
        long selectedDelay = getSelectedDelayMillis();
        return (selectedDelay > 0L) ? selectedDelay : 50L;
    }

    private void prependDualMessage(SimulationSlot slot, String message) {
        es.ants.felixgm.trmsim_wsn.gui.dual.DualSimulationWorkspacePanel workspacePanel = dualWorkspacePanel(slot);
        workspacePanel.getMessagesTextArea().setText(message + workspacePanel.getMessagesTextArea().getText());
    }

    private void renderDualWorkspaceNetwork(SimulationSlot slot, Network network) {
        Controller controller = dualController(slot);
        if (controller == null) {
            return;
        }
        try {
            NetworkPanel slotNetworkPanel = dualWorkspacePanel(slot).getNetworkPanel();
            if (slotNetworkPanel == null) {
                return;
            }
            if (slotNetworkPanel instanceof es.ants.felixgm.trmsim_wsn.gui.networkpanels.JavaFXNetworkPanel) {
                ((es.ants.felixgm.trmsim_wsn.gui.networkpanels.JavaFXNetworkPanel) slotNetworkPanel).ensureActiveRendering();
            }
            NetworkRenderSupport.renderNetwork(
                    slotNetworkPanel,
                    network,
                    controller.get_requiredService(),
                    NetworkRenderSupport.createState(
                            radioRangeSlider.getValue() / (double) radioRangeSlider.getMaximum(),
                            showRangesCheckBox.isSelected(),
                            showLinksCheckBox.isSelected(),
                            showIdsCheckBox.isSelected(),
                            showGridCheckBox.isSelected()));
            slotNetworkPanel.revalidate();
            slotNetworkPanel.repaint();
            dualWorkspacePanel(slot).revalidate();
            dualWorkspacePanel(slot).repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void refreshDualNetworksIfNeeded() {
        if (appMode != AppMode.DUAL) {
            return;
        }
        Controller primaryController = dualController(SimulationSlot.PRIMARY);
        Controller secondaryController = dualController(SimulationSlot.SECONDARY);
        renderDualWorkspaceNetwork(SimulationSlot.PRIMARY, primaryController == null ? null : primaryController.get_currentNetwork(SimulationSlot.PRIMARY));
        renderDualWorkspaceNetwork(SimulationSlot.SECONDARY, secondaryController == null ? null : secondaryController.get_currentNetwork(SimulationSlot.SECONDARY));
        SimulationGraphWorkspace primaryWorkspace = dualGraphWorkspaces.get(SimulationSlot.PRIMARY);
        SimulationGraphWorkspace secondaryWorkspace = dualGraphWorkspaces.get(SimulationSlot.SECONDARY);
        if (primaryWorkspace != null) {
            primaryWorkspace.updateDisplayControlsState(
                    showIdsCheckBox.isSelected(),
                    showLinksCheckBox.isSelected(),
                    showRangesCheckBox.isSelected(),
                    showGridCheckBox.isSelected(),
                    delaySlider.getValue(),
                    delaySlider.getMinimum(),
                    delaySlider.getMaximum());
        }
        if (secondaryWorkspace != null) {
            secondaryWorkspace.updateDisplayControlsState(
                    showIdsCheckBox.isSelected(),
                    showLinksCheckBox.isSelected(),
                    showRangesCheckBox.isSelected(),
                    showGridCheckBox.isSelected(),
                    delaySlider.getValue(),
                    delaySlider.getMinimum(),
                    delaySlider.getMaximum());
        }
    }

    private void renderCurrentDualNetworkOnPanel(SimulationSlot slot, NetworkPanel targetPanel) {
        Controller controller = dualController(slot);
        if (controller == null || targetPanel == null) {
            return;
        }
        try {
            if (targetPanel instanceof es.ants.felixgm.trmsim_wsn.gui.networkpanels.JavaFXNetworkPanel) {
                ((es.ants.felixgm.trmsim_wsn.gui.networkpanels.JavaFXNetworkPanel) targetPanel).ensureActiveRendering();
            }
            NetworkRenderSupport.renderNetwork(
                    targetPanel,
                    controller.get_currentNetwork(slot),
                    controller.get_requiredService(),
                    NetworkRenderSupport.createState(
                            radioRangeSlider.getValue() / (double) radioRangeSlider.getMaximum(),
                            showRangesCheckBox.isSelected(),
                            showLinksCheckBox.isSelected(),
                            showIdsCheckBox.isSelected(),
                            showGridCheckBox.isSelected()));
            targetPanel.revalidate();
            targetPanel.repaint();
        } catch (Exception ignored) {
        }
    }

    private void installDualNetworkPanelSelectionHandler(SimulationSlot slot, NetworkPanel panel) {
        if (panel == null) {
            return;
        }
        if (panel instanceof es.ants.felixgm.trmsim_wsn.gui.networkpanels.JavaFXNetworkPanel) {
            ((es.ants.felixgm.trmsim_wsn.gui.networkpanels.JavaFXNetworkPanel) panel).ensureActiveRendering();
        }
        panel.setSensorSelectionListener(sensor -> {
            if (sensor != null) {
                dualSelectedNodeIds.put(slot, Integer.valueOf(sensor.id()));
                refreshDualSelectedNodeDetails(slot, sensor.id());
            }
        });
    }

    private void refreshDualSelectedNodeDetails(SimulationSlot slot, int sensorId) {
        Controller controller = dualController(slot);
        if (controller == null) {
            return;
        }
        es.ants.felixgm.trmsim_wsn.network.Sensor sensor = controller.getSensor(slot, sensorId);
        SimulationGraphWorkspace workspace = dualGraphWorkspaces.get(slot);
        if (sensor == null || workspace == null) {
            return;
        }
        String title = es.ants.felixgm.trmsim_wsn.gui.support.NodeInspectorHelper.buildNodeTitle(sensor);
        String body = es.ants.felixgm.trmsim_wsn.gui.support.NodeInspectorHelper.buildNodeDetailsText(sensor, controller.get_currentNetwork(slot));
        workspace.setSelectedSensorId(Integer.valueOf(sensor.id()));
        workspace.updateSelectedNodeSummary(title, body);
    }

    private void openDualFullscreen(SimulationSlot slot) {
        SimulationGraphWorkspace workspace = dualGraphWorkspaces.get(slot);
        if (workspace == null) {
            return;
        }
        workspace.getFullscreenGraphButton().doClick();
    }

    private JPanel createDualSettingsPanel(SimulationSlot slot) {
        DualSettingsPanel settingsPanel = dualSettingsPanels.get(slot);
        if (settingsPanel == null) {
            settingsPanel = new DualSettingsPanel(buildNetworkGenerationConfig(), buildBatchSimulationConfig());
            dualSettingsPanels.put(slot, settingsPanel);
        }
        Controller controller = dualController(slot);
        settingsPanel.setEnabled((controller == null) || !controller.isSimulationRunning(slot));
        return settingsPanel;
    }

    private NetworkGenerationConfig buildDualNetworkGenerationConfig(SimulationSlot slot) {
        DualSettingsPanel settingsPanel = dualSettingsPanels.get(slot);
        return (settingsPanel != null) ? settingsPanel.buildNetworkGenerationConfig() : buildNetworkGenerationConfig();
    }

    private SimulationConfig buildDualSimulationConfig(SimulationSlot slot) {
        DualSettingsPanel settingsPanel = dualSettingsPanels.get(slot);
        return (settingsPanel != null) ? settingsPanel.buildSimulationConfig() : buildSimulationConfig();
    }

    private BatchSimulationConfig buildDualBatchSimulationConfig(SimulationSlot slot) {
        DualSettingsPanel settingsPanel = dualSettingsPanels.get(slot);
        if (settingsPanel != null) {
            return settingsPanel.buildBatchSimulationConfig();
        }
        return buildBatchSimulationConfig();
    }

    private java.awt.Component createDualParametersPanel(SimulationSlot slot, String trustModelName) {
        try {
            Controller controller = dualController(slot);
            TRMParametersPanel parametersPanel = TRMParametersPanelFactory.create(trustModelName);
            parametersPanel.set_TRMParameters(controller.get_TRMParameters(slot));
            parametersPanel.setEnabled(!controller.isSimulationRunning(slot));
            dualParametersPanels.put(slot, parametersPanel);
            JPanel container = new JPanel(new BorderLayout(0, 8));
            container.setOpaque(false);
            JPanel parameterSourcePanel = new JPanel();
            parameterSourcePanel.setOpaque(false);
            parameterSourcePanel.setLayout(new BoxLayout(parameterSourcePanel, BoxLayout.Y_AXIS));
            parameterSourcePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Parameter Source"));
            JRadioButton fileRadioButton = new JRadioButton("File");
            JRadioButton customizedRadioButton = new JRadioButton("Customized");
            fileRadioButton.setOpaque(false);
            customizedRadioButton.setOpaque(false);
            fileRadioButton.setSelected(true);
            ButtonGroup sourceButtonGroup = new ButtonGroup();
            sourceButtonGroup.add(fileRadioButton);
            sourceButtonGroup.add(customizedRadioButton);
            JTextField parametersFileTextField = new JTextField(extractFileName(controller.get_parametersFile(slot)));
            JTextArea parametersFileContentTextArea = new JTextArea(8, 24);
            parametersFileContentTextArea.setText(controller.get_ParametersFileContent(slot));
            JButton browseButton = new JButton("Browse");
            JButton saveFileContentButton = new JButton("Save file content");
            JButton applyButton = new JButton("Apply changes");
            applyButton.setEnabled(!controller.isSimulationRunning(slot));
            browseButton.addActionListener(evt -> loadDualParametersFromFile(slot, parametersFileTextField, parametersFileContentTextArea, parametersPanel));
            saveFileContentButton.addActionListener(evt -> saveDualParametersFileContent(slot, parametersFileContentTextArea));
            applyButton.addActionListener(evt -> applyDualParameters(slot, parametersPanel, parametersFileContentTextArea));
            dualParameterApplyButtons.put(slot, applyButton);
            fileRadioButton.addActionListener(evt -> updateDualParameterSourceState(true, parametersFileTextField, browseButton, saveFileContentButton, parametersFileContentTextArea, parametersPanel, applyButton));
            customizedRadioButton.addActionListener(evt -> updateDualParameterSourceState(false, parametersFileTextField, browseButton, saveFileContentButton, parametersFileContentTextArea, parametersPanel, applyButton));
            parameterSourcePanel.add(fileRadioButton);
            parameterSourcePanel.add(customizedRadioButton);
            parameterSourcePanel.add(parametersFileTextField);
            parameterSourcePanel.add(browseButton);
            parameterSourcePanel.add(saveFileContentButton);

            JPanel bottomContainer = new JPanel(new BorderLayout(0, 8));
            bottomContainer.setOpaque(false);
            bottomContainer.add(new JScrollPane(parametersFileContentTextArea), BorderLayout.CENTER);
            bottomContainer.add(applyButton, BorderLayout.SOUTH);

            container.add(parameterSourcePanel, BorderLayout.NORTH);
            container.add(new JScrollPane(parametersPanel), BorderLayout.CENTER);
            container.add(bottomContainer, BorderLayout.SOUTH);
            updateDualParameterSourceState(true, parametersFileTextField, browseButton, saveFileContentButton, parametersFileContentTextArea, parametersPanel, applyButton);
            return container;
        } catch (Exception ex) {
            JTextArea fallback = new JTextArea(
                    "Unable to initialize slot-specific parameters panel.\n\n" + ex.getMessage());
            fallback.setEditable(false);
            fallback.setLineWrap(true);
            fallback.setWrapStyleWord(true);
            return new JScrollPane(fallback);
        }
    }

    private void applyDualParameters(SimulationSlot slot, TRMParametersPanel parametersPanel, JTextArea parametersFileContentTextArea) {
        Controller controller = dualController(slot);
        if (parametersPanel == null || controller == null) {
            return;
        }
        try {
            controller.set_TRMParameters(slot, parametersPanel);
            parametersPanel.set_TRMParameters(controller.get_TRMParameters(slot));
            parametersFileContentTextArea.setText(controller.get_TRMParameters(slot).toString());
            prependDualMessage(slot, "Parameters applied for " + slotLabel(slot) + ".\n");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void loadDualParametersFromFile(
            SimulationSlot slot,
            JTextField parametersFileTextField,
            JTextArea parametersFileContentTextArea,
            TRMParametersPanel parametersPanel) {
        try {
            Controller controller = dualController(slot);
            JFileChooser fileChooser = new JFileChooser(".");
            fileChooser.setDialogTitle("Parameters file selection");
            if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            java.io.File selectedFile = fileChooser.getSelectedFile();
            parametersFileTextField.setText(selectedFile.getName());
            controller.set_parametersFile(slot, selectedFile.getCanonicalPath());
            parametersPanel.set_TRMParameters(controller.set_TRMParameters(slot, controller.get_parametersFile(slot)));
            parametersFileContentTextArea.setText(controller.get_ParametersFileContent(slot));
            prependDualMessage(slot, "Parameters loaded from file.\n");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void saveDualParametersFileContent(SimulationSlot slot, JTextArea parametersFileContentTextArea) {
        try {
            Controller controller = dualController(slot);
            JFileChooser fileChooser = new JFileChooser(".");
            fileChooser.setDialogTitle("Save parameters file");
            if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            java.io.File selectedFile = fileChooser.getSelectedFile();
            controller.saveParametersFileContent(selectedFile.getAbsolutePath(), parametersFileContentTextArea.getText());
            prependDualMessage(slot, "Parameters file saved.\n");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void updateDualParameterSourceState(
            boolean parametersFromFile,
            JTextField parametersFileTextField,
            JButton browseButton,
            JButton saveFileContentButton,
            JTextArea parametersFileContentTextArea,
            TRMParametersPanel parametersPanel,
            JButton applyButton) {
        parametersFileTextField.setEnabled(parametersFromFile);
        browseButton.setEnabled(parametersFromFile);
        saveFileContentButton.setEnabled(parametersFromFile);
        parametersFileContentTextArea.setEnabled(parametersFromFile);
        parametersPanel.setEnabled(!parametersFromFile);
        applyButton.setEnabled(!parametersFromFile);
    }

    private String extractFileName(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return "";
        }
        int separatorIndex = Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf(java.io.File.separatorChar));
        return separatorIndex >= 0 ? filePath.substring(separatorIndex + 1) : filePath;
    }

    private SimulationEventHelper.EventHost dualSimulationEventHost(SimulationSlot slot) {
        return new SimulationEventHelper.EventHost() {
            public void paintUpdatedNetwork(Network network) throws Exception {
                renderDualWorkspaceNetwork(slot, network);
            }

            public void refreshSelectedNodeDetails() {
                Integer selectedNodeId = dualSelectedNodeIds.get(slot);
                if (selectedNodeId == null) {
                    SimulationGraphWorkspace workspace = dualGraphWorkspaces.get(slot);
                    if (workspace != null) {
                        workspace.updateSelectedNodeSummary(
                                "No node selected",
                                "Click any node in the graph to inspect its live state and exported metrics.");
                    }
                    return;
                }
                refreshDualSelectedNodeDetails(slot, selectedNodeId.intValue());
            }

            public Collection<OutcomesPanel> getOutcomesPanels() {
                return dualWorkspacePanel(slot).getOutcomesPanels();
            }

            public void finishSimulationUi() {
                prependDualMessage(
                        slot,
                        "Simulation completed. "
                                + SimulationResultRepository.getInstance(slot).getResultCount()
                                + " results saved.\n");
                updateDualSessionControls();
            }

            public void handleSimulationFailure(Exception exception) {
                JOptionPane.showMessageDialog(TRMSim_WSN.this, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                try {
                    for (SimulationSlot simulationSlot : SimulationSlot.values()) {
                        Controller controller = dualController(simulationSlot);
                        if (controller != null) {
                            controller.stopSimulations(simulationSlot);
                        }
                    }
                } catch (Exception stopException) {
                    stopException.printStackTrace();
                }
                dualSessionStartPending = false;
                updateDualSessionControls();
                exception.printStackTrace();
            }

            public void sleepAfterUiUpdate() {
                Controller controller = dualController(slot);
                if (controller != null) {
                    controller.sleep();
                }
            }

            public String getSelectedTrustModelName() {
                Controller controller = dualController(slot);
                String trustModelName = (controller == null) ? null : controller.getTrustModelName(slot);
                return trustModelName != null ? trustModelName : "";
            }

            public JTextArea getMessagesTextArea() {
                return dualWorkspacePanel(slot).getMessagesTextArea();
            }

            public SimulationSlot getSimulationSlot() {
                return slot;
            }

            public SimulationResultRepository getSimulationResultsRepository() {
                return SimulationResultRepository.getInstance(slot);
            }
        };
    }

    private void showDualExportDialog() {
        try {
            DualSimulationExportHelper.showExportDialog(
                    this,
                    SimulationResultRepository.getInstance(SimulationSlot.PRIMARY),
                    SimulationResultRepository.getInstance(SimulationSlot.SECONDARY),
                    new DualSimulationExportHelper.ExportHost() {
                        public boolean ensureSimulationDataAvailable(SimulationSlot slot, SimulationResultRepository repository) {
                            if (repository.getResultCount() == 0) {
                                JOptionPane.showMessageDialog(
                                        TRMSim_WSN.this,
                                        slotLabel(slot) + " has no simulation data available for export.",
                                        "No Data",
                                        JOptionPane.WARNING_MESSAGE);
                                return false;
                            }
                            return true;
                        }

                        public String exportEnergyGraph(java.awt.Component owner, SimulationSlot slot, es.ants.felixgm.trmsim_wsn.gui.export.ExportRequest request) throws Exception {
                            OutcomesPanel energyOutcomesPanel = getEnergyOutcomesPanel(dualWorkspacePanel(slot).getOutcomesPanels());
                            if (energyOutcomesPanel == null) {
                                JOptionPane.showMessageDialog(
                                        owner,
                                        "Energy Consumption graph is not available for " + slotLabel(slot) + ".",
                                        "Export Failed",
                                        JOptionPane.WARNING_MESSAGE);
                                return null;
                            }
                            return es.ants.felixgm.trmsim_wsn.gui.export.GraphImageExporter.exportCurrentGraph(
                                    owner,
                                    energyOutcomesPanel,
                                    energyOutcomesPanel.getLabel() + " " + slotLabel(slot),
                                    request);
                        }
                    });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error during export: " + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void applyAppModeLayout(AppMode mode) {
        BorderLayout layout = (BorderLayout) getContentPane().getLayout();
        if (singleModeNorthComponent == null) {
            singleModeNorthComponent = layout.getLayoutComponent(getContentPane(), BorderLayout.NORTH);
        }
        if (singleModeWestComponent == null) {
            singleModeWestComponent = layout.getLayoutComponent(getContentPane(), BorderLayout.WEST);
        }
        if (singleModeCenterComponent == null) {
            singleModeCenterComponent = layout.getLayoutComponent(getContentPane(), BorderLayout.CENTER);
        }

        if (mode == AppMode.DUAL) {
            syncDualShellFromControllerState();
            if (singleModeNorthComponent != null) {
                getContentPane().remove(singleModeNorthComponent);
            }
            if (singleModeWestComponent != null) {
                getContentPane().remove(singleModeWestComponent);
            }
            if (singleModeCenterComponent != null) {
                getContentPane().remove(singleModeCenterComponent);
            }
            getContentPane().add(dualSimulationShellPanel, BorderLayout.CENTER);
            reattachDualVisualizationHooks();
            refreshDualNetworksIfNeeded();
        } else {
            getContentPane().remove(dualSimulationShellPanel);
            if (singleModeNorthComponent != null && singleModeNorthComponent.getParent() != getContentPane()) {
                getContentPane().add(singleModeNorthComponent, BorderLayout.NORTH);
            }
            if (singleModeWestComponent != null && singleModeWestComponent.getParent() != getContentPane()) {
                getContentPane().add(singleModeWestComponent, BorderLayout.WEST);
            }
            if (singleModeCenterComponent != null && singleModeCenterComponent.getParent() != getContentPane()) {
                getContentPane().add(singleModeCenterComponent, BorderLayout.CENTER);
            }
        }
    }

    private void reattachDualVisualizationHooks() {
        for (SimulationSlot slot : SimulationSlot.values()) {
            NetworkPanel panel = dualWorkspacePanel(slot).getNetworkPanel();
            installDualNetworkPanelSelectionHandler(slot, panel);
            SimulationGraphWorkspace workspace = dualGraphWorkspaces.get(slot);
            if (workspace != null && panel != null) {
                workspace.applyVisualizationControlsToPanels(panel);
            }
        }
    }

    private OutcomesPanel getEnergyOutcomesPanel(Collection<OutcomesPanel> outcomesPanels) {
        if (outcomesPanels == null) {
            return null;
        }
        for (OutcomesPanel outcomesPanel : outcomesPanels) {
            if ((outcomesPanel.getLabel() != null) && outcomesPanel.getLabel().toLowerCase().contains("energy")) {
                return outcomesPanel;
            }
        }
        return null;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        MainWindowAssemblyController.configure(this);
    }// </editor-fold>//GEN-END:initComponents

    void TRModelComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_TRModelComboBoxItemStateChanged
        MainWindowTrustModelController.handleSelection(new MainWindowContext(this), evt, LOGGER); }//GEN-LAST:event_TRModelComboBoxItemStateChanged

    void set_TRMParametersPanel(TRMParametersPanel trmParametersPanel) {
        TRM_ParametersPanel = trmParametersPanel;
        TRM_ParametersPanelAux = TRM_ParametersPanel;
        TRMParametersScrollPane.setViewportView(TRM_ParametersPanelAux);
    }
    
    private void stopTRMButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopTRMButtonActionPerformed
        MainWindowActionController.stopTrm(this); }//GEN-LAST:event_stopTRMButtonActionPerformed

    void stopSimulationsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopSimulationsButtonActionPerformed
        MainWindowActionController.stopBatch(this); }//GEN-LAST:event_stopSimulationsButtonActionPerformed

    private void loadWSNButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadWSNButtonActionPerformed
        MainWindowActionController.loadNetwork(this); }//GEN-LAST:event_loadWSNButtonActionPerformed

    private void saveWSNButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveWSNButtonActionPerformed
        MainWindowActionController.saveNetwork(this); }//GEN-LAST:event_saveWSNButtonActionPerformed

    private void percentageMaliciousServersSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_percentageMaliciousServersSliderStateChanged
        MainWindowActionController.syncSliderValue(percentageMaliciousServersSlider, percentageMaliciousServersTextField); }//GEN-LAST:event_percentageMaliciousServersSliderStateChanged

    private void percentageRelayServersSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_percentageRelayServersSliderStateChanged
        MainWindowActionController.syncSliderValue(percentageRelayServersSlider, percentageRelayServersTextField); }//GEN-LAST:event_percentageRelayServersSliderStateChanged

    private void percentageClientsSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_percentageClientsSliderStateChanged
        MainWindowActionController.syncSliderValue(percentageClientsSlider, percentageClientsTextField); }//GEN-LAST:event_percentageClientsSliderStateChanged

    public void onNetworkUpdated(Network network) {
        SimulationEventHelper.onNetworkUpdated(MainWindowHosts.simulationEvents(this), network);
    }

    public void onNetworkUpdated(SimulationSlot slot, Network network) {
        SimulationEventHelper.onNetworkUpdated(
                appMode == AppMode.DUAL ? dualSimulationEventHost(slot) : MainWindowHosts.simulationEvents(this, slot),
                slot,
                network);
    }

    public void onOutcomesUpdated(Collection<Outcome> outcomes) {
        SimulationEventHelper.onOutcomesUpdated(MainWindowHosts.simulationEvents(this), outcomes);
    }

    public void onOutcomesUpdated(SimulationSlot slot, Collection<Outcome> outcomes) {
        SimulationEventHelper.onOutcomesUpdated(
                appMode == AppMode.DUAL ? dualSimulationEventHost(slot) : MainWindowHosts.simulationEvents(this, slot),
                slot,
                outcomes);
    }

    public void onMessage(String message) {
        SimulationEventHelper.onMessage(MainWindowHosts.simulationEvents(this), message);
    }

    public void onMessage(SimulationSlot slot, String message) {
        SimulationEventHelper.onMessage(
                appMode == AppMode.DUAL ? dualSimulationEventHost(slot) : MainWindowHosts.simulationEvents(this, slot),
                slot,
                message);
    }

    public void onError(Exception exception) {
        SimulationEventHelper.onError(MainWindowHosts.simulationEvents(this), exception);
    }

    public void onError(SimulationSlot slot, Exception exception) {
        SimulationEventHelper.onError(
                appMode == AppMode.DUAL ? dualSimulationEventHost(slot) : MainWindowHosts.simulationEvents(this, slot),
                slot,
                exception);
    }
    
    void simulationComponentsEnabling(boolean enable) {
        MainWindowUiStateController.setSimulationComponentsEnabled(MainWindowHosts.uiState(this), enable);
    }
    
    void runSimulationsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runSimulationsButtonActionPerformed
        MainWindowActionController.runBatch(this, evt); }//GEN-LAST:event_runSimulationsButtonActionPerformed

    private void showIdsCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_showIdsCheckBoxItemStateChanged
        MainWindowActionController.toggleDisplay(this, showIdsCheckBox, "Ids");
        refreshDualNetworksIfNeeded();
    }//GEN-LAST:event_showIdsCheckBoxItemStateChanged

    void set_TRMParameters() throws Exception {
        MainWindowParametersController.setParameters(MainWindowHosts.parameters(this));
    }

    private void runTRMButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runTRMButtonActionPerformed
        MainWindowActionController.runSingle(this, evt); }//GEN-LAST:event_runTRMButtonActionPerformed

    private void resetWSNButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetWSNButtonActionPerformed
        MainWindowActionController.resetCurrentNetwork(this); }//GEN-LAST:event_resetWSNButtonActionPerformed

    private void showLinksCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_showLinksCheckBoxItemStateChanged
        MainWindowActionController.toggleDisplay(this, showLinksCheckBox, "links");
        refreshDualNetworksIfNeeded();
    }//GEN-LAST:event_showLinksCheckBoxItemStateChanged

    private void showRangesCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_showRangesCheckBoxItemStateChanged
        MainWindowActionController.toggleDisplay(this, showRangesCheckBox, "ranges");
        refreshDualNetworksIfNeeded();
    }//GEN-LAST:event_showRangesCheckBoxItemStateChanged

    private void delaySliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_delaySliderStateChanged
        MainWindowConfigurationController.onDelayChanged(MainWindowHosts.configuration(this)); }//GEN-LAST:event_delaySliderStateChanged

    private void radioRangeSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_radioRangeSliderStateChanged
        MainWindowConfigurationController.onRadioRangeChanged(MainWindowHosts.configuration(this)); }//GEN-LAST:event_radioRangeSliderStateChanged

    private void newWSNButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newWSNButtonActionPerformed
        MainWindowActionController.createNewNetwork(this); }//GEN-LAST:event_newWSNButtonActionPerformed

    long getSelectedDelayMillis() {
        return MainWindowConfigurationController.getSelectedDelayMillis(MainWindowHosts.configuration(this));
    }

    SimulationConfig buildSimulationConfig() {
        return MainWindowConfigurationController.buildSimulationConfig(MainWindowHosts.configuration(this));
    }

    NetworkGenerationConfig buildNetworkGenerationConfig() {
        return MainWindowConfigurationController.buildNetworkGenerationConfig(MainWindowHosts.configuration(this));
    }

    BatchSimulationConfig buildBatchSimulationConfig() {
        return MainWindowConfigurationController.buildBatchSimulationConfig(MainWindowHosts.configuration(this));
    }

    private void minNumSensorsSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_minNumSensorsSpinnerStateChanged
        MainWindowConfigurationController.alignMinSensors(MainWindowHosts.configuration(this)); }//GEN-LAST:event_minNumSensorsSpinnerStateChanged

    private void maxNumSensorsSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_maxNumSensorsSpinnerStateChanged
        MainWindowConfigurationController.alignMaxSensors(MainWindowHosts.configuration(this)); }//GEN-LAST:event_maxNumSensorsSpinnerStateChanged

    void prepareEditableParametersForExecution() throws Exception {
        MainWindowSimulationController.prepareEditableParametersForExecution(new MainWindowContext(this));
    }

    void finishSimulationUi() {
        MainWindowSimulationController.finishSimulationUi(new MainWindowContext(this));
    }

    void handleSimulationFailure(Exception exception) {
        MainWindowSimulationController.handleSimulationFailure(new MainWindowContext(this), exception);
    }

    private void networkPanelContainerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_networkPanelContainerMouseClicked
        MainWindowActionController.selectNodeFromNetworkPanel(this, evt); }//GEN-LAST:event_networkPanelContainerMouseClicked

    public void applyChangesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyChangesButtonActionPerformed
    }//GEN-LAST:event_applyChangesButtonActionPerformed

    private void showGridCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_showGridCheckBoxItemStateChanged
        MainWindowActionController.toggleDisplay(this, showGridCheckBox, "grid");
        refreshDualNetworksIfNeeded();
    }//GEN-LAST:event_showGridCheckBoxItemStateChanged

    /**
     * This method plots a Wireless Sensor Network
     * @param network Wireless Sensor Network to be plotted
     * @param requiredService Service requested by the clients (needed in order to paint and
     * distinguish benevolent and malicious servers)
     * @throws Exception If any error occurs while plotting a WSN
     */
    protected void paintNetwork(Network network, Service requiredService) throws Exception {
        MainWindowRenderController.paintNetwork(new MainWindowContext(this), network, requiredService);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables

    javax.swing.JButton exportDataButton, applyChangesButton, applyParametersChangesButton, browseButton,
            hideSensorPropertiesPanelButton, loadWSNButton, newWSNButton, resetWSNButton, runSimulationsButton,
            runTRMButton, saveParametersFileContentButton, saveWSNButton, stopSimulationsButton, stopTRMButton,
            modeSwitchButton;
    javax.swing.JMenuItem exportDataMenuItem, aboutTRMSim_WSNmenuItem, applyParametersChangesMenuItem,
            helpMenuItem, loadParametersMenuItem, loadWSNmenuItem, newWSNmenuItem, resetWSNmenuItem,
            runSimulationsMenuItem, runTRMmenuItem, saveParametersMenuItem, saveWSNmenuItem,
            stopSimulationsMenuItem, stopTRMmenuItem;
    javax.swing.JScrollPane TRMParametersScrollPane, controlsScrollPane, messagesScrollPane, neighborsScrollPane,
            parametersFileContentScrollPane;
    javax.swing.JPanel TRM_ParametersPanelAux, bottomPanel, bottomParametersContainerPanel, buttonsControlPanel,
            controlsPanel, displayControlsPanel, legendPanelContainer, messagePanel,
            networkAndSensorPropertiesContainerPanel, networkPanelContainer, outcomesPanelsPanel, parametersPanel,
            parametersSettingsPanel, sensorPropertiesPanel, simulationsPanel, slidersControlsPanel,
            spinnersControlPanel, threatsControlsPanel, upperPanel;
    javax.swing.JComboBox TRModelComboBox, sensorTypeComboBox;
    javax.swing.JLabel TRModelLabel, delayLabel, legendLabel, maxNumSensorsLabel, minNumSensorsLabel,
            neighborsLabel, numExecutionsLabel, numNetworksLabel, parametersFileLabel, parametersSourceLabel,
            percentageClientsLabel, percentageMaliciousServersLabel, percentageRelayServersLabel, radioRangeLabel,
            radioRangePropertyLabel, sensorIdLabel, sensorTypeLabel, xCoordinateLabel, yCoordinateLabel;
    javax.swing.JMenu TRModelMenu, helpMenu, parametersMenu, simulationsMenu, wsnMenu;
    javax.swing.JCheckBox collusionCheckBox, dynamicWSNsCheckBox, oscillatingWSNsCheckBox, showGridCheckBox,
            showIdsCheckBox, showLinksCheckBox, showRangesCheckBox;
    javax.swing.JRadioButton customizedParametersRadioButton, parametersFileRadioButton;
    javax.swing.JTextField delayTextField, parametersFileTextField, percentageClientsTextField,
            percentageMaliciousServersTextField, percentageRelayServersTextField, radioRangeTextField,
            sensorIdTextField, xCoordinateTextField, yCoordinateTextField;
    javax.swing.JSpinner maxNumSensorsSpinner, minNumSensorsSpinner, numExecutionsSpinner, numNetworksSpinner,
            radioRangeSpinner;
    javax.swing.JMenuBar menuBar;
    javax.swing.JTextArea messagesTextArea, parametersFileContentTextArea;
    javax.swing.JList neighborsList;
    javax.swing.JTabbedPane outcomesTabbedPane, tabbedPane;
    javax.swing.JSlider delaySlider, percentageClientsSlider, percentageMaliciousServersSlider,
            percentageRelayServersSlider, radioRangeSlider;
    javax.swing.JSplitPane bottomParametersSplitPane, simulationsSplitPane, upperSplitPane;
    javax.swing.ButtonGroup parametersSourceButtonGroup;
    javax.swing.JSeparator separator1, separator2, separator3, separator4;
    // End of variables declaration//GEN-END:variables
    TRMParametersPanel TRM_ParametersPanel;
    NetworkPanel networkPanel = new NetworkPanel();
    SimulationGraphWorkspace graphWorkspace;
    String lastAllowedTRModel;
    BatchSimulationState batchSimulationState = BatchSimulationState.IDLE;
    Integer selectedNodeId;
    JLayeredPane networkOverlayPane;
    JPanel graphNodeInspectorPanel, graphInfoStripPanel, graphTopLiveControlsPanel, graphInspectorLegendWrapper;
    JLabel graphNodeInspectorTitleLabel, graphStripSimulationStateLabel, graphInspectorSimulationStateLabel;
    JTextArea graphNodeInspectorTextArea, graphTopControlsInfoArea;
    JButton graphStripRunButton, graphStripStopButton, graphInspectorPauseResumeButton, graphInspectorStopButton;
    javax.swing.Timer graphNodeInspectorAnimator, graphNodeInspectorAutoHideTimer;
    int graphNodeInspectorCurrentWidth = 16, graphNodeInspectorTargetWidth = 16;
    boolean graphInspectorPinned = false;
    JCheckBox graphInspectorPinToggleButton, graphInspectorShowIdsCheckBox, graphInspectorShowLinksCheckBox,
            graphInspectorShowRangesCheckBox, graphInspectorShowGridCheckBox;
    JSlider graphInspectorDelaySlider;
    MiniLegendPanel graphInspectorLegendPanel;
    CompactLegendPanel dashboardLegendPanel;
    static final int GRAPH_INSPECTOR_MARGIN = 14, GRAPH_INSPECTOR_COLLAPSED_WIDTH = 18,
            GRAPH_INSPECTOR_EXPANDED_WIDTH = 364, GRAPH_INSPECTOR_MIN_HEIGHT = 260, GRAPH_INSPECTOR_MAX_HEIGHT = 430;

    Collection<OutcomesPanel> outcomesPanels;
    LegendPanel legendPanel = new LegendPanel();

    void updateRunSimulationsControls() {
        MainWindowSimulationControlsController.updateRunSimulationsControls(MainWindowHosts.simulationControls(this));
        updateDualSessionControls();
    }
    void resetBatchSimulationState() { MainWindowSimulationControlsController.resetBatchSimulationState(MainWindowHosts.simulationControls(this)); }
    void syncEmbeddedAndFullscreenDisplayControls() { MainWindowSimulationControlsController.syncEmbeddedAndFullscreenDisplayControls(MainWindowHosts.simulationControls(this)); }
    void handlePauseResumeRequest() { MainWindowSimulationControlsController.handlePauseResumeRequest(MainWindowHosts.simulationControls(this)); }
    void handleStopRequest() { MainWindowSimulationControlsController.handleStopRequest(MainWindowHosts.simulationControls(this)); }
    public void syncTabbedPanePreferredSize() { tabbedPane.setPreferredSize(getSize()); }

    void setGraphInspectorExpanded(boolean expanded) { MainWindowEmbeddedInspectorController.setGraphInspectorExpanded(this, expanded); }

    private static final class DualSettingsPanel extends JPanel {
        private final JSpinner minSensorsSpinner;
        private final JSpinner maxSensorsSpinner;
        private final JSpinner networksSpinner;
        private final JSpinner executionsSpinner;
        private final JSlider clientsSlider;
        private final JSlider relaySlider;
        private final JSlider maliciousSlider;
        private final JSlider radioRangeSliderLocal;
        private final JTextField clientsValue;
        private final JTextField relayValue;
        private final JTextField maliciousValue;
        private final JTextField radioRangeValue;
        private final JCheckBox dynamicCheckBox;
        private final JCheckBox oscillatingCheckBox;
        private final JCheckBox collusionCheckBox;

        private DualSettingsPanel(NetworkGenerationConfig networkConfig, BatchSimulationConfig batchConfig) {
            setOpaque(false);
            setLayout(new GridLayout(1, 2, 12, 0));

            minSensorsSpinner = new JSpinner(new javax.swing.SpinnerNumberModel(networkConfig.getMinNumSensors(), 1, Integer.MAX_VALUE, 1));
            maxSensorsSpinner = new JSpinner(new javax.swing.SpinnerNumberModel(networkConfig.getMaxNumSensors(), 1, Integer.MAX_VALUE, 1));
            networksSpinner = new JSpinner(new javax.swing.SpinnerNumberModel(batchConfig.getNumNetworks(), 1, Integer.MAX_VALUE, 1));
            executionsSpinner = new JSpinner(new javax.swing.SpinnerNumberModel(batchConfig.getNumExecutions(), 1, Integer.MAX_VALUE, 1));
            clientsSlider = createPercentSlider(networkConfig.getProbClients());
            relaySlider = createPercentSlider(networkConfig.getProbRelay());
            maliciousSlider = createPercentSlider(networkConfig.getProbMalicious());
            radioRangeSliderLocal = createPercentSlider(networkConfig.getRadioRange());
            clientsValue = createValueField(clientsSlider.getValue());
            relayValue = createValueField(relaySlider.getValue());
            maliciousValue = createValueField(maliciousSlider.getValue());
            radioRangeValue = createValueField(radioRangeSliderLocal.getValue());
            dynamicCheckBox = new JCheckBox("Dynamic WSNs", networkConfig.isDynamic());
            oscillatingCheckBox = new JCheckBox("Oscillating WSNs", networkConfig.isOscillating());
            collusionCheckBox = new JCheckBox("Collusion", networkConfig.isCollusion());
            dynamicCheckBox.setOpaque(false);
            oscillatingCheckBox.setOpaque(false);
            collusionCheckBox.setOpaque(false);

            minSensorsSpinner.addChangeListener(evt -> alignMinMax(true));
            maxSensorsSpinner.addChangeListener(evt -> alignMinMax(false));
            clientsSlider.addChangeListener(evt -> clientsValue.setText(String.valueOf(clientsSlider.getValue())));
            relaySlider.addChangeListener(evt -> relayValue.setText(String.valueOf(relaySlider.getValue())));
            maliciousSlider.addChangeListener(evt -> maliciousValue.setText(String.valueOf(maliciousSlider.getValue())));
            radioRangeSliderLocal.addChangeListener(evt -> radioRangeValue.setText(String.valueOf(radioRangeSliderLocal.getValue())));

            JPanel networkSection = createSettingsSection(
                    "Network",
                    createSpinnerRow("Min Num Sensors", minSensorsSpinner),
                    createSpinnerRow("Max Num Sensors", maxSensorsSpinner),
                    createSliderRow("% Clients", clientsSlider, clientsValue),
                    createSliderRow("% Relay Servers", relaySlider, relayValue),
                    createSliderRow("% Malicious Servers", maliciousSlider, maliciousValue));
            JPanel simulationSection = createSettingsSection(
                    "Simulation",
                    createSpinnerRow("Num Networks", networksSpinner),
                    createSpinnerRow("Executions", executionsSpinner),
                    createSliderRow("Radio Range", radioRangeSliderLocal, radioRangeValue),
                    dynamicCheckBox,
                    oscillatingCheckBox,
                    collusionCheckBox);

            add(networkSection);
            add(simulationSection);
        }

        private JPanel createSettingsSection(String title, java.awt.Component... rows) {
            JPanel section = new JPanel();
            section.setOpaque(false);
            section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
            section.setAlignmentX(Component.LEFT_ALIGNMENT);
            section.setBorder(javax.swing.BorderFactory.createTitledBorder(title));
            for (java.awt.Component row : rows) {
                if (row instanceof JComponent) {
                    ((JComponent) row).setAlignmentX(Component.LEFT_ALIGNMENT);
                }
                section.add(row);
            }
            section.add(Box.createVerticalStrut(2));
            return section;
        }

        private JPanel createSpinnerRow(String label, JSpinner spinner) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
            row.setOpaque(false);
            JLabel rowLabel = new JLabel(label);
            rowLabel.setPreferredSize(new Dimension(128, 24));
            spinner.setPreferredSize(new Dimension(96, 24));
            row.add(rowLabel);
            row.add(spinner);
            return row;
        }

        private JPanel createSliderRow(String label, JSlider slider, JTextField valueField) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
            row.setOpaque(false);
            JLabel rowLabel = new JLabel(label);
            rowLabel.setPreferredSize(new Dimension(128, 24));
            slider.setPreferredSize(new Dimension(140, 24));
            valueField.setHorizontalAlignment(JTextField.CENTER);
            row.add(rowLabel);
            row.add(slider);
            row.add(valueField);
            return row;
        }

        private JSlider createPercentSlider(double ratio) {
            return new JSlider(0, 100, (int) Math.round(ratio * 100.0));
        }

        private JTextField createValueField(int value) {
            JTextField field = new JTextField(String.valueOf(value));
            field.setEditable(false);
            field.setPreferredSize(new Dimension(48, 24));
            return field;
        }

        private void alignMinMax(boolean minChanged) {
            int minValue = ((Integer) minSensorsSpinner.getValue()).intValue();
            int maxValue = ((Integer) maxSensorsSpinner.getValue()).intValue();
            if (minChanged && minValue > maxValue) {
                maxSensorsSpinner.setValue(Integer.valueOf(minValue));
            } else if (!minChanged && maxValue < minValue) {
                minSensorsSpinner.setValue(Integer.valueOf(maxValue));
            }
        }

        private NetworkGenerationConfig buildNetworkGenerationConfig() {
            return new NetworkGenerationConfig(
                    ((Integer) minSensorsSpinner.getValue()).intValue(),
                    ((Integer) maxSensorsSpinner.getValue()).intValue(),
                    clientsSlider.getValue() / 100.0,
                    relaySlider.getValue() / 100.0,
                    maliciousSlider.getValue() / 100.0,
                    radioRangeSliderLocal.getValue() / 100.0,
                    dynamicCheckBox.isSelected(),
                    oscillatingCheckBox.isSelected(),
                    collusionCheckBox.isSelected());
        }

        private SimulationConfig buildSimulationConfig() {
            return new SimulationConfig(
                    dynamicCheckBox.isSelected(),
                    oscillatingCheckBox.isSelected(),
                    collusionCheckBox.isSelected(),
                    ((Integer) executionsSpinner.getValue()).intValue());
        }

        private BatchSimulationConfig buildBatchSimulationConfig() {
            return new BatchSimulationConfig(
                    buildNetworkGenerationConfig(),
                    ((Integer) networksSpinner.getValue()).intValue(),
                    ((Integer) executionsSpinner.getValue()).intValue());
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            minSensorsSpinner.setEnabled(enabled);
            maxSensorsSpinner.setEnabled(enabled);
            networksSpinner.setEnabled(enabled);
            executionsSpinner.setEnabled(enabled);
            clientsSlider.setEnabled(enabled);
            relaySlider.setEnabled(enabled);
            maliciousSlider.setEnabled(enabled);
            radioRangeSliderLocal.setEnabled(enabled);
            dynamicCheckBox.setEnabled(enabled);
            oscillatingCheckBox.setEnabled(enabled);
            collusionCheckBox.setEnabled(enabled);
        }
    }
}
