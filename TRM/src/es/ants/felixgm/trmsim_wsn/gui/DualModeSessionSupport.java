package es.ants.felixgm.trmsim_wsn.gui;

import es.ants.felixgm.trmsim_wsn.Controller;
import es.ants.felixgm.trmsim_wsn.SimulationSlot;
import es.ants.felixgm.trmsim_wsn.app.SimulationApplicationService;
import es.ants.felixgm.trmsim_wsn.gui.dual.DualSimulationShellPanel;
import es.ants.felixgm.trmsim_wsn.gui.dual.DualSimulationWorkspacePanel;
import es.ants.felixgm.trmsim_wsn.gui.export.SimulationResultRepository;
import es.ants.felixgm.trmsim_wsn.gui.graph.SimulationGraphWorkspace;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowRenderController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowTrustModelController;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.OutcomesPanel;
import es.ants.felixgm.trmsim_wsn.gui.support.SimulationUiHelper;
import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.trm.btrm_wsn.BTRM_WSN;
import es.ants.felixgm.trmsim_wsn.trm.eigentrust.EigenTrust;
import es.ants.felixgm.trmsim_wsn.trm.peertrust.PeerTrust;
import es.ants.felixgm.trmsim_wsn.trm.powertrust.PowerTrust;
import es.ants.felixgm.trmsim_wsn.trm.trip.TRIP;

import javax.swing.JOptionPane;
import java.util.Collection;

final class DualModeSessionSupport {
    private final TRMSim_WSN owner;
    private final DualModeParametersSupport parametersSupport;
    private final DualModeWorkspaceSupport workspaceSupport;

    DualModeSessionSupport(TRMSim_WSN owner,
                           DualModeParametersSupport parametersSupport,
                           DualModeWorkspaceSupport workspaceSupport) {
        this.owner = owner;
        this.parametersSupport = parametersSupport;
        this.workspaceSupport = workspaceSupport;
    }

    void handleDualSessionRunPauseResume() {
        try {
            if (!owner.isAnyDualSimulationRunning()) {
                startDualSession();
                return;
            }
            if (!owner.isAnyDualBatchSimulationActive()) {
                return;
            }
            if (owner.areAllDualRunningSimulationsPaused()) {
                for (SimulationSlot slot : SimulationSlot.values()) {
                    Controller controller = owner.dualController(slot);
                    if (controller != null) {
                        controller.resumeSimulation(slot);
                    }
                }
            } else {
                for (SimulationSlot slot : SimulationSlot.values()) {
                    Controller controller = owner.dualController(slot);
                    if (controller != null) {
                        controller.pauseSimulation(slot);
                    }
                }
            }
            updateDualSessionControls();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    void handleDualSessionStop() {
        try {
            if (!owner.isAnyDualBatchSimulationActive()) {
                return;
            }
            for (SimulationSlot slot : SimulationSlot.values()) {
                owner.setDualSlotStartPending(slot, false);
                owner.setDualSlotBatchActive(slot, false);
                Controller controller = owner.dualController(slot);
                if (controller != null) {
                    controller.stopSimulations(slot);
                }
            }
            owner.dualSessionStartPending = false;
            updateDualSessionControls();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    void updateDualSessionControls() {
        if (owner.dualSimulationShellPanel == null) {
            return;
        }
        boolean anySlotActive = owner.isAnyDualSimulationRunning();
        if (owner.dualSessionStartPending && anySlotActive) {
            owner.dualSessionStartPending = false;
        }
        boolean batchActive = owner.isAnyDualBatchSimulationActive();
        boolean anyTrmActive = owner.isAnyDualSlotTrmActive();
        boolean allPaused = batchActive && owner.areAllDualRunningSimulationsPaused();
        boolean sessionActive = batchActive;
        boolean canExport = !anySlotActive
                && (SimulationResultRepository.getInstance(SimulationSlot.PRIMARY).getResultCount() > 0)
                && (SimulationResultRepository.getInstance(SimulationSlot.SECONDARY).getResultCount() > 0);

        owner.dualSimulationShellPanel.getSessionTrmButton().setText(anyTrmActive ? "Stop T&R" : "Run T&R");
        owner.dualSimulationShellPanel.getSessionTrmButton().setEnabled(anyTrmActive || !anySlotActive);
        owner.dualSimulationShellPanel.getSessionRunButton().setText(sessionActive ? (allPaused ? "Resume" : "Pause") : "Run Simulations");
        owner.dualSimulationShellPanel.getSessionRunButton().setEnabled(!anyTrmActive && (!anySlotActive || batchActive));
        owner.dualSimulationShellPanel.getSessionStopButton().setEnabled(batchActive);
        owner.dualSimulationShellPanel.getImportScenarioButton().setEnabled(!anySlotActive);
        owner.dualSimulationShellPanel.getModeSwitchButton().setEnabled(!anySlotActive);
        owner.dualSimulationShellPanel.getExportButton().setEnabled(canExport);

        updateDualGraphWorkspaceControls(SimulationSlot.PRIMARY);
        updateDualGraphWorkspaceControls(SimulationSlot.SECONDARY);
        updateDualSlotControlsEnabled(SimulationSlot.PRIMARY);
        updateDualSlotControlsEnabled(SimulationSlot.SECONDARY);
        updateDualRefreshTimer();
    }

    void updateDualRefreshTimer() {
        boolean shouldRefresh = (owner.appMode == AppMode.DUAL) && (owner.dualSessionStartPending || owner.isAnyDualSimulationRunning());
        if (!shouldRefresh) {
            if (owner.dualNetworkRefreshTimer != null && owner.dualNetworkRefreshTimer.isRunning()) {
                owner.dualNetworkRefreshTimer.stop();
            }
            return;
        }
        if (owner.dualNetworkRefreshTimer == null) {
            owner.dualNetworkRefreshTimer = new javax.swing.Timer(90, evt -> {
                workspaceSupport.refreshDualNetworksIfNeeded();
                repaintDualOutcomePanels();
            });
            owner.dualNetworkRefreshTimer.setRepeats(true);
        }
        if (!owner.dualNetworkRefreshTimer.isRunning()) {
            owner.dualNetworkRefreshTimer.start();
        }
    }

    void resetDualModeState() {
        owner.dualSessionStartPending = false;
        for (SimulationSlot slot : SimulationSlot.values()) {
            owner.setDualSlotStartPending(slot, false);
            owner.setDualSlotTrmActive(slot, false);
            owner.setDualSlotBatchActive(slot, false);
            Controller controller = owner.dualController(slot);
            if (controller != null) {
                controller.stopAllSimulations();
                controller.clearCurrentNetwork(slot);
            }
            SimulationResultRepository.getInstance(slot).clearRepository();
            owner.dualSelectedNodeIds.remove(slot);
            if (owner.dualSimulationShellPanel != null) {
                DualSimulationWorkspacePanel workspacePanel = owner.dualWorkspacePanel(slot);
                workspacePanel.getMessagesTextArea().setText("");
                workspacePanel.setSelectedTrustModelName(null);
                workspacePanel.setSetupExpanded(false);
                workspacePanel.setBottomExpanded(true);
                SimulationUiHelper.resetOutcomePanels(workspacePanel.getOutcomesPanels());
                workspaceSupport.renderDualWorkspaceNetwork(slot, null);
            }
            DualSimulationShellPanel.SlotToolbarPanel toolbarPanel = owner.dualToolbarPanel(slot);
            if (toolbarPanel.getTrustModelComboBox().getItemCount() > 0) {
                owner.dualTrustModelSelectionSync.put(slot, Boolean.TRUE);
                try {
                    toolbarPanel.getTrustModelComboBox().setSelectedIndex(0);
                } finally {
                    owner.dualTrustModelSelectionSync.put(slot, Boolean.FALSE);
                }
            }
            workspaceSupport.rebuildDualWorkspaceUi(slot);
        }
    }

    void resetSingleModeState() {
        MainWindowContext context = new MainWindowContext(owner);
        try {
            if (context.getController() != null) {
                context.getController().stopAllSimulations();
                context.getController().clearCurrentNetwork();
            }
        } catch (Exception ex) {
            showError(ex);
        }

        context.resetBatchSimulationState();
        context.setSimulationComponentsEnabled(false);
        context.getStopTrmButton().setEnabled(false);
        context.getStopTrmMenuItem().setEnabled(false);
        context.getStopSimulationsButton().setEnabled(false);
        context.getStopSimulationsMenuItem().setEnabled(false);
        context.setMessagesText("");
        context.clearNodeInspector();
        SimulationResultRepository.getInstance().clearRepository();
        SimulationUiHelper.resetOutcomePanels(context.getOutcomesPanels());
        MainWindowRenderController.clearNetworkPanel(context, context.getCurrentNetworkPanel());

        if (context.getTrustModelComboBox().getItemCount() > 0) {
            if (context.getTrustModelComboBox().getSelectedIndex() != 0) {
                context.getTrustModelComboBox().setSelectedIndex(0);
            } else {
                MainWindowTrustModelController.handleSelection(context, null, TRMSim_WSN.LOGGER);
            }
        }
        context.setMessagesText("");
    }

    boolean hasActiveSimulation() {
        return owner.isSingleSimulationActive() || owner.isAnyDualSimulationRunning();
    }

    void syncDualWorkspaceFromControllerState(SimulationSlot slot) {
        Controller controller = owner.dualController(slot);
        if (controller == null) {
            return;
        }
        DualSimulationWorkspacePanel workspacePanel = owner.dualWorkspacePanel(slot);
        String trustModelName = controller.getTrustModelName(slot);
        if ((trustModelName == null || trustModelName.trim().isEmpty()) && owner.dualToolbarPanel(slot).getTrustModelComboBox().getItemCount() > 0) {
            trustModelName = owner.dualToolbarPanel(slot).getTrustModelComboBox().getItemAt(0);
            try {
                controller.set_TRModel_WSN(slot, trustModelName);
            } catch (Exception ex) {
                showError(ex);
            }
        }
        if (trustModelName != null) {
            owner.dualTrustModelSelectionSync.put(slot, Boolean.TRUE);
            try {
                owner.dualToolbarPanel(slot).getTrustModelComboBox().setSelectedItem(trustModelName);
            } finally {
                owner.dualTrustModelSelectionSync.put(slot, Boolean.FALSE);
            }
        }
        workspacePanel.setSelectedTrustModelName(trustModelName);
        workspaceSupport.rebuildDualWorkspaceUi(slot);
        workspaceSupport.renderDualWorkspaceNetwork(slot, controller.get_currentNetwork(slot));
    }

    void updateDualGraphWorkspaceControls(SimulationSlot slot) {
        SimulationGraphWorkspace workspace = owner.dualGraphWorkspaces.get(slot);
        Controller controller = owner.dualController(slot);
        if (workspace == null || controller == null) {
            return;
        }
        boolean running = owner.isDualSlotSimulationActive(slot);
        boolean trmActive = owner.isDualSlotTrmActive(slot);
        boolean paused = running && controller.isSimulationPaused(slot);
        workspace.setFullscreenInteractionLocked(trmActive);
        if (trmActive) {
            workspace.updateSimulationControlsState(
                    running ? "T&R Running" : "T&R Starting",
                    "Run Simulations",
                    "Run Simulations",
                    true,
                    false,
                    false);
            owner.dualWorkspacePanel(slot).updateSimulationControlsState(running ? "T&R Running" : "T&R Starting");
            return;
        }
        workspace.updateSimulationControlsState(
                running ? (paused ? "Paused" : "Running") : "Idle",
                "Run Simulations",
                paused ? "Resume" : "Pause",
                !running,
                running,
                running);
        owner.dualWorkspacePanel(slot).updateSimulationControlsState(running ? (paused ? "Paused" : "Running") : "Idle");
    }

    void handleDualSharedTrmToggle() {
        try {
            if (owner.isAnyDualSlotTrmActive()) {
                handleDualSharedTrmStop();
                return;
            }
            startDualSharedTrmRun();
        } catch (Exception ex) {
            for (SimulationSlot slot : SimulationSlot.values()) {
                owner.setDualSlotStartPending(slot, false);
                owner.setDualSlotTrmActive(slot, false);
            }
            showError(ex);
        }
    }

    private void repaintDualOutcomePanels() {
        for (SimulationSlot slot : SimulationSlot.values()) {
            DualSimulationWorkspacePanel workspacePanel = owner.dualWorkspacePanel(slot);
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

    private void updateDualSlotControlsEnabled(SimulationSlot slot) {
        boolean batchActive = owner.isAnyDualBatchSimulationActive();
        boolean anyTrmActive = owner.isAnyDualSlotTrmActive();
        boolean nonSimulationControlsEnabled = !batchActive && !anyTrmActive;
        DualSimulationWorkspacePanel workspacePanel = owner.dualWorkspacePanel(slot);
        DualSimulationShellPanel.SlotToolbarPanel toolbarPanel = owner.dualToolbarPanel(slot);
        workspacePanel.setWorkspaceChromeEnabled(nonSimulationControlsEnabled);
        workspacePanel.setSetupControlsEnabled(nonSimulationControlsEnabled);
        parametersSupport.setSlotSimulationSettingsEnabled(slot, nonSimulationControlsEnabled);
        toolbarPanel.getTrustModelComboBox().setEnabled(nonSimulationControlsEnabled);
        toolbarPanel.getNewNetworkButton().setEnabled(nonSimulationControlsEnabled);
        toolbarPanel.getLoadScenarioButton().setEnabled(nonSimulationControlsEnabled);
        toolbarPanel.getLoadNetworkButton().setEnabled(nonSimulationControlsEnabled);
        toolbarPanel.getSaveNetworkButton().setEnabled(nonSimulationControlsEnabled);
        toolbarPanel.getResetNetworkButton().setEnabled(nonSimulationControlsEnabled);
    }

    private void startDualSession() throws Exception {
        if (owner.isDualSlotTrmActive(SimulationSlot.PRIMARY) || owner.isDualSlotTrmActive(SimulationSlot.SECONDARY)) {
            return;
        }
        ensureDualNetworkInitializedForRun(SimulationSlot.PRIMARY);
        ensureDualNetworkInitializedForRun(SimulationSlot.SECONDARY);
        ensureDualBatchSlotReady(SimulationSlot.PRIMARY);
        ensureDualBatchSlotReady(SimulationSlot.SECONDARY);

        SimulationResultRepository.clearAll();
        SimulationUiHelper.resetOutcomePanels(owner.dualWorkspacePanel(SimulationSlot.PRIMARY).getOutcomesPanels());
        SimulationUiHelper.resetOutcomePanels(owner.dualWorkspacePanel(SimulationSlot.SECONDARY).getOutcomesPanels());
        owner.dualWorkspacePanel(SimulationSlot.PRIMARY).getMessagesTextArea().setText("");
        owner.dualWorkspacePanel(SimulationSlot.SECONDARY).getMessagesTextArea().setText("");

        owner.dualSimulationService(SimulationSlot.PRIMARY).setVisualizationDelay(owner.getEffectiveDualVisualizationDelayMillis(SimulationSlot.PRIMARY));
        owner.dualSimulationService(SimulationSlot.SECONDARY).setVisualizationDelay(owner.getEffectiveDualVisualizationDelayMillis(SimulationSlot.SECONDARY));
        String startMessage = "Starting simulations at " + (new java.util.Date()) + "...\n";
        owner.prependDualMessage(SimulationSlot.PRIMARY, startMessage);
        owner.prependDualMessage(SimulationSlot.SECONDARY, startMessage);
        owner.dualSessionStartPending = true;
        owner.setDualSlotBatchActive(SimulationSlot.PRIMARY, true);
        owner.setDualSlotBatchActive(SimulationSlot.SECONDARY, true);
        owner.dualSimulationService(SimulationSlot.PRIMARY).runBatchSimulation(SimulationSlot.PRIMARY, owner, parametersSupport.buildDualBatchSimulationConfig(SimulationSlot.PRIMARY));
        owner.dualSimulationService(SimulationSlot.SECONDARY).runBatchSimulation(SimulationSlot.SECONDARY, owner, parametersSupport.buildDualBatchSimulationConfig(SimulationSlot.SECONDARY));
        updateDualSessionControls();
    }

    private void handleDualSharedTrmStop() {
        try {
            if (!owner.isAnyDualSlotTrmActive()) {
                return;
            }
            for (SimulationSlot slot : SimulationSlot.values()) {
                owner.setDualSlotStartPending(slot, false);
                owner.setDualSlotTrmActive(slot, false);
                Controller controller = owner.dualController(slot);
                if (controller != null) {
                    controller.stopSimulations(slot);
                }
                owner.prependDualMessage(slot, "Stopped T&R simulation.\n");
            }
            updateDualSessionControls();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void startDualSharedTrmRun() throws Exception {
        if (owner.isAnyDualBatchSimulationActive()) {
            return;
        }
        ensureDualNetworkInitializedForRun(SimulationSlot.PRIMARY);
        ensureDualNetworkInitializedForRun(SimulationSlot.SECONDARY);
        ensureDualSlotReady(SimulationSlot.PRIMARY);
        ensureDualSlotReady(SimulationSlot.SECONDARY);

        SimulationResultRepository.getInstance(SimulationSlot.PRIMARY).clearRepository();
        SimulationResultRepository.getInstance(SimulationSlot.SECONDARY).clearRepository();
        SimulationUiHelper.resetOutcomePanels(owner.dualWorkspacePanel(SimulationSlot.PRIMARY).getOutcomesPanels());
        SimulationUiHelper.resetOutcomePanels(owner.dualWorkspacePanel(SimulationSlot.SECONDARY).getOutcomesPanels());
        owner.dualWorkspacePanel(SimulationSlot.PRIMARY).getMessagesTextArea().setText("");
        owner.dualWorkspacePanel(SimulationSlot.SECONDARY).getMessagesTextArea().setText("");

        owner.dualSimulationService(SimulationSlot.PRIMARY).setVisualizationDelay(owner.getEffectiveDualVisualizationDelayMillis(SimulationSlot.PRIMARY));
        owner.dualSimulationService(SimulationSlot.SECONDARY).setVisualizationDelay(owner.getEffectiveDualVisualizationDelayMillis(SimulationSlot.SECONDARY));
        owner.prependDualMessage(SimulationSlot.PRIMARY, "Starting T&R simulation...\n");
        owner.prependDualMessage(SimulationSlot.SECONDARY, "Starting T&R simulation...\n");
        owner.setDualSlotStartPending(SimulationSlot.PRIMARY, true);
        owner.setDualSlotStartPending(SimulationSlot.SECONDARY, true);
        owner.setDualSlotTrmActive(SimulationSlot.PRIMARY, true);
        owner.setDualSlotTrmActive(SimulationSlot.SECONDARY, true);
        owner.dualSimulationService(SimulationSlot.PRIMARY).runSimulation(SimulationSlot.PRIMARY, owner, parametersSupport.buildDualSimulationConfig(SimulationSlot.PRIMARY));
        owner.dualSimulationService(SimulationSlot.SECONDARY).runSimulation(SimulationSlot.SECONDARY, owner, parametersSupport.buildDualSimulationConfig(SimulationSlot.SECONDARY));
        updateDualSessionControls();
    }

    private void ensureDualSlotReady(SimulationSlot slot) {
        Controller controller = owner.dualController(slot);
        if (controller == null) {
            throw new IllegalStateException(owner.slotLabel(slot) + " controller is not initialized.");
        }
        String trustModelName = controller.getTrustModelName(slot);
        if (trustModelName == null || trustModelName.trim().isEmpty()) {
            throw new IllegalStateException(owner.slotLabel(slot) + " has no selected trust model.");
        }
        Network currentNetwork = controller.get_currentNetwork(slot);
        if (currentNetwork == null) {
            throw new IllegalStateException(owner.slotLabel(slot) + " has no loaded/generated network.");
        }
        String networkType = currentNetwork.getClass().getName().toLowerCase();
        if (trustModelName.equals(PowerTrust.get_name()) && !networkType.contains("powertrust")) {
            throw new IllegalStateException(owner.slotLabel(slot) + " network does not match PowerTrust. Create or load the WSN again.");
        }
        if (trustModelName.equals(EigenTrust.get_name()) && !networkType.contains("eigentrust")) {
            throw new IllegalStateException(owner.slotLabel(slot) + " network does not match EigenTrust. Create or load the WSN again.");
        }
        if (trustModelName.equals(TRIP.get_name()) && !networkType.contains("trip")) {
            throw new IllegalStateException(owner.slotLabel(slot) + " network does not match TRIP. Create or load the WSN again.");
        }
        if (trustModelName.equals(PeerTrust.get_name()) && !networkType.contains("peertrust")) {
            throw new IllegalStateException(owner.slotLabel(slot) + " network does not match PeerTrust. Create or load the WSN again.");
        }
        if (trustModelName.equals(BTRM_WSN.get_name()) && !networkType.contains("btrm_wsn")) {
            throw new IllegalStateException(owner.slotLabel(slot) + " network does not match BTRM-WSN. Create or load the WSN again.");
        }
    }

    private void ensureDualBatchSlotReady(SimulationSlot slot) {
        Controller controller = owner.dualController(slot);
        if (controller == null) {
            throw new IllegalStateException(owner.slotLabel(slot) + " controller is not initialized.");
        }
        String trustModelName = controller.getTrustModelName(slot);
        if (trustModelName == null || trustModelName.trim().isEmpty()) {
            throw new IllegalStateException(owner.slotLabel(slot) + " has no selected trust model.");
        }
    }

    private void ensureDualNetworkInitializedForRun(SimulationSlot slot) {
        Controller controller = owner.dualController(slot);
        if (controller == null) {
            throw new IllegalStateException(owner.slotLabel(slot) + " controller is not initialized.");
        }
        String trustModelName = controller.getTrustModelName(slot);
        if (trustModelName == null || trustModelName.trim().isEmpty()) {
            throw new IllegalStateException(owner.slotLabel(slot) + " has no selected trust model.");
        }
        if (controller.get_currentNetwork(slot) == null) {
            createDualNetwork(slot, false, "New WSN created automatically for run.\n");
        }
    }

    private void createDualNetwork(SimulationSlot slot, boolean resetMessages, String successMessage) {
        SimulationApplicationService service = owner.dualSimulationService(slot);
        if (service == null) {
            return;
        }
        try {
            workspaceSupport.clearDualSelectedNodeState(slot);
            service.setVisualizationDelay(owner.getDualSelectedDelayMillis(slot));
            Network network = service.createRandomNetwork(slot, parametersSupport.buildDualNetworkGenerationConfig(slot));
            workspaceSupport.renderDualWorkspaceNetwork(slot, network);
            SimulationUiHelper.resetOutcomePanels(owner.dualWorkspacePanel(slot).getOutcomesPanels());
            if (resetMessages) {
                owner.dualWorkspacePanel(slot).getMessagesTextArea().setText("");
            }
            owner.prependDualMessage(slot, successMessage);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(owner, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}
