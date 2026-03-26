package es.ants.felixgm.trmsim_wsn.gui;

import es.ants.felixgm.trmsim_wsn.Controller;
import es.ants.felixgm.trmsim_wsn.SimulationSlot;
import es.ants.felixgm.trmsim_wsn.app.NetworkGenerationConfig;
import es.ants.felixgm.trmsim_wsn.app.SimulationApplicationService;
import es.ants.felixgm.trmsim_wsn.gui.dual.DualSimulationShellPanel;
import es.ants.felixgm.trmsim_wsn.gui.dual.DualSimulationWorkspacePanel;
import es.ants.felixgm.trmsim_wsn.gui.events.SimulationEventHelper;
import es.ants.felixgm.trmsim_wsn.gui.export.SimulationResultRepository;
import es.ants.felixgm.trmsim_wsn.gui.graph.SimulationGraphWorkspace;
import es.ants.felixgm.trmsim_wsn.gui.network.NetworkFileHelper;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.OutcomesPanel;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.TRMParametersPanel;
import es.ants.felixgm.trmsim_wsn.gui.support.SimulationUiHelper;
import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.trm.TrustModelRegistry;
import es.ants.felixgm.trmsim_wsn.trm.btrm_wsn.BTRM_WSN;
import es.ants.felixgm.trmsim_wsn.trm.eigentrust.EigenTrust;
import es.ants.felixgm.trmsim_wsn.trm.peertrust.PeerTrust;
import es.ants.felixgm.trmsim_wsn.trm.powertrust.PowerTrust;
import es.ants.felixgm.trmsim_wsn.trm.trip.TRIP;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.io.File;
import java.util.Collection;

final class DualModeCoordinator {
    private final TRMSim_WSN owner;
    private final DualModeParametersSupport parametersSupport;
    private final DualModeWorkspaceSupport workspaceSupport;

    DualModeCoordinator(TRMSim_WSN owner) {
        this.owner = owner;
        this.parametersSupport = new DualModeParametersSupport(owner);
        this.workspaceSupport = new DualModeWorkspaceSupport(owner, parametersSupport);
    }

    void initializeDualModeShell() {
        owner.initializeDualControllers();
        owner.dualSimulationShellPanel = new DualSimulationShellPanel();
        initializeDualGraphWorkspace(SimulationSlot.PRIMARY);
        initializeDualGraphWorkspace(SimulationSlot.SECONDARY);
        owner.dualSimulationShellPanel.getSessionRunButton().addActionListener(evt -> handleDualSessionRunPauseResume());
        owner.dualSimulationShellPanel.getSessionStopButton().addActionListener(evt -> handleDualSessionStop());
        owner.dualSimulationShellPanel.getModeSwitchButton().addActionListener(evt -> switchAppMode(AppMode.SINGLE));
        owner.dualSimulationShellPanel.getExportButton().addActionListener(evt -> showDualExportDialog());
        configureDualWorkspace(SimulationSlot.PRIMARY, owner.dualSimulationShellPanel.getPrimaryWorkspacePanel());
        configureDualWorkspace(SimulationSlot.SECONDARY, owner.dualSimulationShellPanel.getSecondaryWorkspacePanel());
        updateDualSessionControls();
    }

    void switchAppMode(AppMode mode) {
        if (mode == owner.appMode) {
            return;
        }
        if (mode == AppMode.DUAL) {
            resetDualModeState();
        }
        owner.appMode = mode;
        workspaceSupport.applyAppModeLayout(mode);
        if (mode == AppMode.DUAL) {
            applyDefaultDualWorkspaceLayout();
        }
        if (owner.modeSwitchButton != null) {
            owner.modeSwitchButton.setText(mode == AppMode.DUAL ? "Single Mode" : "Dual Mode");
        }
        updateDualSessionControls();
        updateDualRefreshTimer();
        owner.getContentPane().revalidate();
        owner.getContentPane().repaint();
    }

    void syncDualShellFromControllerState() {
        if (owner.dualSimulationShellPanel == null) {
            return;
        }
        syncDualWorkspaceFromControllerState(SimulationSlot.PRIMARY);
        syncDualWorkspaceFromControllerState(SimulationSlot.SECONDARY);
    }

    void handleDualSessionRunPauseResume() {
        try {
            if (!owner.isAnyDualSimulationRunning()) {
                startDualSession();
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
            for (SimulationSlot slot : SimulationSlot.values()) {
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
        boolean anyRunning = owner.isAnyDualSimulationRunning();
        if (owner.dualSessionStartPending && anyRunning) {
            owner.dualSessionStartPending = false;
        }
        boolean allPaused = anyRunning && owner.areAllDualRunningSimulationsPaused();
        boolean treatAsRunning = anyRunning || owner.dualSessionStartPending;
        boolean canExport = !treatAsRunning
                && (SimulationResultRepository.getInstance(SimulationSlot.PRIMARY).getResultCount() > 0)
                && (SimulationResultRepository.getInstance(SimulationSlot.SECONDARY).getResultCount() > 0);

        owner.dualSimulationShellPanel.getSessionRunButton().setText(treatAsRunning ? (allPaused ? "Resume" : "Pause") : "Run");
        owner.dualSimulationShellPanel.getSessionStopButton().setEnabled(treatAsRunning);
        owner.dualSimulationShellPanel.getExportButton().setEnabled(canExport);
        owner.dualSimulationShellPanel.getPrimaryToolbarPanel().getRunStopButton().setText(
                ((owner.dualController(SimulationSlot.PRIMARY) != null) && owner.dualController(SimulationSlot.PRIMARY).isSimulationRunning(SimulationSlot.PRIMARY)) ? "Stop T&R" : "Run T&R");
        owner.dualSimulationShellPanel.getSecondaryToolbarPanel().getRunStopButton().setText(
                ((owner.dualController(SimulationSlot.SECONDARY) != null) && owner.dualController(SimulationSlot.SECONDARY).isSimulationRunning(SimulationSlot.SECONDARY)) ? "Stop T&R" : "Run T&R");

        updateDualGraphWorkspaceControls(SimulationSlot.PRIMARY);
        updateDualGraphWorkspaceControls(SimulationSlot.SECONDARY);
        setDualWorkspaceSetupEnabled(!anyRunning);
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

    void showDualExportDialog() {
        workspaceSupport.showDualExportDialog();
    }

    void refreshDualNetworksIfNeeded() {
        workspaceSupport.refreshDualNetworksIfNeeded();
    }

    void rebuildDualWorkspaceUi(SimulationSlot slot) {
        workspaceSupport.rebuildDualWorkspaceUi(slot);
    }

    void renderDualWorkspaceNetwork(SimulationSlot slot, Network network) {
        workspaceSupport.renderDualWorkspaceNetwork(slot, network);
    }

    void renderCurrentDualNetworkOnPanel(SimulationSlot slot, NetworkPanel panel) {
        workspaceSupport.renderCurrentDualNetworkOnPanel(slot, panel);
    }

    void installDualNetworkPanelSelectionHandler(SimulationSlot slot, NetworkPanel panel) {
        workspaceSupport.installDualNetworkPanelSelectionHandler(slot, panel);
    }

    void refreshDualSelectedNodeDetails(SimulationSlot slot, int sensorId) {
        workspaceSupport.refreshDualSelectedNodeDetails(slot, sensorId);
    }

    void openDualFullscreen(SimulationSlot slot) {
        workspaceSupport.openDualFullscreen(slot);
    }

    JPanel createDualSettingsPanel(SimulationSlot slot) {
        return parametersSupport.createDualSettingsPanel(slot);
    }

    java.awt.Component createDualParametersPanel(SimulationSlot slot, String trustModelName) {
        return parametersSupport.createDualParametersPanel(slot, trustModelName);
    }

    NetworkGenerationConfig buildDualNetworkGenerationConfig(SimulationSlot slot) {
        return parametersSupport.buildDualNetworkGenerationConfig(slot);
    }

    es.ants.felixgm.trmsim_wsn.app.SimulationConfig buildDualSimulationConfig(SimulationSlot slot) {
        return parametersSupport.buildDualSimulationConfig(slot);
    }

    es.ants.felixgm.trmsim_wsn.app.BatchSimulationConfig buildDualBatchSimulationConfig(SimulationSlot slot) {
        return parametersSupport.buildDualBatchSimulationConfig(slot);
    }

    void applyDualParameters(SimulationSlot slot, TRMParametersPanel parametersPanel, JTextArea parametersFileContentTextArea) {
        parametersSupport.applyDualParameters(slot, parametersPanel, parametersFileContentTextArea);
    }

    void loadDualParametersFromFile(SimulationSlot slot, javax.swing.JTextField parametersFileTextField, JTextArea parametersFileContentTextArea, TRMParametersPanel parametersPanel) {
        parametersSupport.loadDualParametersFromFile(slot, parametersFileTextField, parametersFileContentTextArea, parametersPanel);
    }

    void saveDualParametersFileContent(SimulationSlot slot, JTextArea parametersFileContentTextArea) {
        parametersSupport.saveDualParametersFileContent(slot, parametersFileContentTextArea);
    }

    void updateDualParameterSourceState(boolean parametersFromFile, javax.swing.JTextField parametersFileTextField, javax.swing.JButton browseButton, javax.swing.JButton saveFileContentButton, JTextArea parametersFileContentTextArea, TRMParametersPanel parametersPanel, javax.swing.JComponent customizedContainer, javax.swing.JButton applyButton) {
        parametersSupport.updateDualParameterSourceState(parametersFromFile, parametersFileTextField, browseButton, saveFileContentButton, parametersFileContentTextArea, parametersPanel, customizedContainer, applyButton);
    }

    void setComponentTreeEnabled(java.awt.Component component, boolean enabled) {
        parametersSupport.setComponentTreeEnabled(component, enabled);
    }

    String extractFileName(String filePath) {
        return parametersSupport.extractFileName(filePath);
    }

    void clearDualSelectedNodeState(SimulationSlot slot) {
        workspaceSupport.clearDualSelectedNodeState(slot);
    }

    SimulationEventHelper.EventHost dualSimulationEventHost(SimulationSlot slot) {
        return new SimulationEventHelper.EventHost() {
            public void paintUpdatedNetwork(Network network) throws Exception {
                workspaceSupport.renderDualWorkspaceNetwork(slot, network);
            }

            public void refreshSelectedNodeDetails() {
                Integer selectedNodeId = owner.dualSelectedNodeIds.get(slot);
                if (selectedNodeId == null) {
                    SimulationGraphWorkspace workspace = owner.dualGraphWorkspaces.get(slot);
                    if (workspace != null) {
                        workspace.updateSelectedNodeSummary("No node selected", "Click any node in the graph to inspect its live state and exported metrics.");
                    }
                    owner.dualWorkspacePanel(slot).updateSelectedNodeSummary("No node selected", "Click any node in the graph to inspect its live state and exported metrics.");
                    return;
                }
                workspaceSupport.refreshDualSelectedNodeDetails(slot, selectedNodeId.intValue());
            }

            public Collection<OutcomesPanel> getOutcomesPanels() {
                return owner.dualWorkspacePanel(slot).getOutcomesPanels();
            }

            public void finishSimulationUi() {
                owner.prependDualMessage(slot, "Simulation completed. " + SimulationResultRepository.getInstance(slot).getResultCount() + " results saved.\n");
                updateDualSessionControls();
            }

            public void handleSimulationFailure(Exception exception) {
                JOptionPane.showMessageDialog(owner, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                try {
                    for (SimulationSlot simulationSlot : SimulationSlot.values()) {
                        Controller controller = owner.dualController(simulationSlot);
                        if (controller != null) {
                            controller.stopSimulations(simulationSlot);
                        }
                    }
                } catch (Exception stopException) {
                    stopException.printStackTrace();
                }
                owner.dualSessionStartPending = false;
                updateDualSessionControls();
                exception.printStackTrace();
            }

            public void sleepAfterUiUpdate() {
                Controller controller = owner.dualController(slot);
                if (controller != null) {
                    controller.sleep();
                }
            }

            public String getSelectedTrustModelName() {
                Controller controller = owner.dualController(slot);
                String trustModelName = (controller == null) ? null : controller.getTrustModelName(slot);
                return trustModelName != null ? trustModelName : "";
            }

            public JTextArea getMessagesTextArea() {
                return owner.dualWorkspacePanel(slot).getMessagesTextArea();
            }

            public SimulationSlot getSimulationSlot() {
                return slot;
            }

            public SimulationResultRepository getSimulationResultsRepository() {
                return SimulationResultRepository.getInstance(slot);
            }
        };
    }

    private void initializeDualGraphWorkspace(SimulationSlot slot) {
        SimulationGraphWorkspace workspace = new SimulationGraphWorkspace(panel -> workspaceSupport.renderCurrentDualNetworkOnPanel(slot, panel));
        workspace.initializeControls();
        workspace.setNodeSelectionListener(nodeId -> {
            owner.dualSelectedNodeIds.put(slot, nodeId);
            if (nodeId == null) {
                workspace.updateSelectedNodeSummary("No node selected", "Click any node in the graph to inspect its live state and exported metrics.");
                return;
            }
            workspaceSupport.refreshDualSelectedNodeDetails(slot, nodeId.intValue());
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
                owner.setDualShowIds(slot, selected);
                workspaceSupport.refreshDualNetworksIfNeeded();
            }

            @Override
            public void onShowLinksChanged(boolean selected) {
                owner.setDualShowLinks(slot, selected);
                workspaceSupport.refreshDualNetworksIfNeeded();
            }

            @Override
            public void onShowRangesChanged(boolean selected) {
                owner.setDualShowRanges(slot, selected);
                workspaceSupport.refreshDualNetworksIfNeeded();
            }

            @Override
            public void onShowGridChanged(boolean selected) {
                owner.setDualShowGrid(slot, selected);
                workspaceSupport.refreshDualNetworksIfNeeded();
            }

            @Override
            public void onDelayChanged(int value) {
                owner.setDualDelayValue(slot, value);
            }
        });
        owner.dualGraphWorkspaces.put(slot, workspace);
    }

    private void configureDualWorkspace(SimulationSlot slot, DualSimulationWorkspacePanel workspacePanel) {
        DualSimulationShellPanel.SlotToolbarPanel toolbarPanel = owner.dualToolbarPanel(slot);
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
        workspacePanel.setLiveControlsListener(new DualSimulationWorkspacePanel.LiveControlsListener() {
            @Override
            public void onShowIdsChanged(boolean selected) {
                owner.setDualShowIds(slot, selected);
                workspaceSupport.refreshDualNetworksIfNeeded();
            }

            @Override
            public void onShowLinksChanged(boolean selected) {
                owner.setDualShowLinks(slot, selected);
                workspaceSupport.refreshDualNetworksIfNeeded();
            }

            @Override
            public void onShowRangesChanged(boolean selected) {
                owner.setDualShowRanges(slot, selected);
                workspaceSupport.refreshDualNetworksIfNeeded();
            }

            @Override
            public void onShowGridChanged(boolean selected) {
                owner.setDualShowGrid(slot, selected);
                workspaceSupport.refreshDualNetworksIfNeeded();
            }

            @Override
            public void onDelayChanged(int value) {
                owner.setDualDelayValue(slot, value);
            }

            @Override
            public void onVisualThemeChanged(String themeName) {
                SimulationGraphWorkspace workspace = owner.dualGraphWorkspaces.get(slot);
                if (workspace != null) {
                    workspace.getVisualThemeComboBox().setSelectedItem(themeName);
                    workspace.applyVisualizationControlsToPanels(owner.dualWorkspacePanel(slot).getNetworkPanel());
                }
            }

            @Override
            public void onEnable3DChanged(boolean enabled) {
                SimulationGraphWorkspace workspace = owner.dualGraphWorkspaces.get(slot);
                if (workspace != null) {
                    workspace.getEnable3DNavigationCheckBox().setSelected(enabled);
                    workspace.applyVisualizationControlsToPanels(owner.dualWorkspacePanel(slot).getNetworkPanel());
                }
            }

            @Override
            public void onCameraPresetChanged(String presetName) {
                SimulationGraphWorkspace workspace = owner.dualGraphWorkspaces.get(slot);
                if (workspace != null) {
                    workspace.getCameraPresetComboBox().setSelectedItem(presetName);
                    workspace.applyVisualizationControlsToPanels(owner.dualWorkspacePanel(slot).getNetworkPanel());
                }
            }

            @Override
            public void onOpenFullscreenRequested() {
                workspaceSupport.openDualFullscreen(slot);
            }
        });
        workspaceSupport.rebuildDualWorkspaceUi(slot);
    }

    private void resetDualModeState() {
        owner.dualSessionStartPending = false;
        for (SimulationSlot slot : SimulationSlot.values()) {
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

    private void syncDualWorkspaceFromControllerState(SimulationSlot slot) {
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

    private void updateDualGraphWorkspaceControls(SimulationSlot slot) {
        SimulationGraphWorkspace workspace = owner.dualGraphWorkspaces.get(slot);
        Controller controller = owner.dualController(slot);
        if (workspace == null || controller == null) {
            return;
        }
        boolean running = controller.isSimulationRunning(slot);
        boolean paused = running && controller.isSimulationPaused(slot);
        workspace.updateSimulationControlsState(running ? (paused ? "Paused" : "Running") : "Idle", "Run T&R", paused ? "Resume" : "Pause", !running, running, running);
        owner.dualWorkspacePanel(slot).updateSimulationControlsState(running ? (paused ? "Paused" : "Running") : "Idle");
    }

    private void setDualWorkspaceSetupEnabled(boolean enabled) {
        setDualWorkspaceSetupEnabled(owner.dualSimulationShellPanel.getPrimaryWorkspacePanel(), enabled);
        setDualWorkspaceSetupEnabled(owner.dualSimulationShellPanel.getSecondaryWorkspacePanel(), enabled);
        setDualToolbarEnabled(owner.dualToolbarPanel(SimulationSlot.PRIMARY), enabled);
        setDualToolbarEnabled(owner.dualToolbarPanel(SimulationSlot.SECONDARY), enabled);
    }

    private void setDualWorkspaceSetupEnabled(DualSimulationWorkspacePanel workspacePanel, boolean enabled) {
        workspacePanel.getSetupToggleButton().setEnabled(true);
        workspacePanel.getBottomToggleButton().setEnabled(true);
    }

    private void setDualToolbarEnabled(DualSimulationShellPanel.SlotToolbarPanel toolbarPanel, boolean enabled) {
        toolbarPanel.getTrustModelComboBox().setEnabled(enabled);
        toolbarPanel.getNewNetworkButton().setEnabled(enabled);
        toolbarPanel.getLoadNetworkButton().setEnabled(enabled);
        toolbarPanel.getSaveNetworkButton().setEnabled(enabled);
        toolbarPanel.getResetNetworkButton().setEnabled(enabled);
        for (SimulationSlot slot : SimulationSlot.values()) {
            DualSettingsPanel settingsPanel = owner.dualSettingsPanels.get(slot);
            if (settingsPanel != null) {
                settingsPanel.setEnabled(enabled);
            }
            TRMParametersPanel parametersPanel = owner.dualParametersPanels.get(slot);
            if (parametersPanel != null) {
                parametersPanel.setEnabled(enabled);
            }
            javax.swing.JButton applyButton = owner.dualParameterApplyButtons.get(slot);
            if (applyButton != null) {
                applyButton.setEnabled(enabled);
            }
        }
    }

    private void applyDualTrustModelSelection(SimulationSlot slot) {
        Controller controller = owner.dualController(slot);
        if (controller == null || Boolean.TRUE.equals(owner.dualTrustModelSelectionSync.get(slot))) {
            return;
        }
        DualSimulationWorkspacePanel workspacePanel = owner.dualWorkspacePanel(slot);
        Object selectedItem = owner.dualToolbarPanel(slot).getTrustModelComboBox().getSelectedItem();
        if (!(selectedItem instanceof String)) {
            return;
        }
        String trustModelName = (String) selectedItem;
        try {
            controller.set_TRModel_WSN(slot, trustModelName);
            controller.clearCurrentNetwork(slot);
            SimulationResultRepository.getInstance(slot).clearRepository();
            workspaceSupport.clearDualSelectedNodeState(slot);
            workspacePanel.setSelectedTrustModelName(trustModelName);
            workspaceSupport.rebuildDualWorkspaceUi(slot);
            SimulationUiHelper.resetOutcomePanels(workspacePanel.getOutcomesPanels());
            workspacePanel.getMessagesTextArea().setText("");
            createDualNetwork(slot, false, "Model changed to " + trustModelName + ". New WSN created automatically.\n");
            updateDualGraphWorkspaceControls(slot);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void createDualNetwork(SimulationSlot slot) {
        createDualNetwork(slot, true, "New WSN created\n");
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

    private void loadDualNetwork(SimulationSlot slot) {
        SimulationApplicationService service = owner.dualSimulationService(slot);
        if (service == null) {
            return;
        }
        try {
            File selectedFile = NetworkFileHelper.chooseXmlFile(owner, "./wsn", "Load WSN", JFileChooser.OPEN_DIALOG);
            if (selectedFile == null) {
                return;
            }
            Network network = service.loadNetwork(slot, selectedFile.getCanonicalPath());
            workspaceSupport.renderDualWorkspaceNetwork(slot, network);
            owner.dualWorkspacePanel(slot).getMessagesTextArea().setText("");
            owner.prependDualMessage(slot, "WSN loaded successfully\n");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void saveDualNetwork(SimulationSlot slot) {
        SimulationApplicationService service = owner.dualSimulationService(slot);
        if (service == null) {
            return;
        }
        try {
            File selectedFile = NetworkFileHelper.chooseXmlFile(owner, "./wsn", "Save WSN", JFileChooser.SAVE_DIALOG);
            if (selectedFile == null) {
                return;
            }
            service.saveCurrentNetwork(slot, selectedFile.getCanonicalPath());
            owner.prependDualMessage(slot, "WSN saved successfully\n");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void resetDualNetwork(SimulationSlot slot) {
        SimulationApplicationService service = owner.dualSimulationService(slot);
        Controller controller = owner.dualController(slot);
        if (service == null || controller == null) {
            return;
        }
        try {
            service.resetCurrentNetwork(slot);
            workspaceSupport.renderDualWorkspaceNetwork(slot, controller.get_currentNetwork(slot));
            owner.prependDualMessage(slot, "Current WSN reset\n");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void startDualSession() throws Exception {
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
        owner.dualSimulationService(SimulationSlot.PRIMARY).runBatchSimulation(SimulationSlot.PRIMARY, owner, parametersSupport.buildDualBatchSimulationConfig(SimulationSlot.PRIMARY));
        owner.dualSimulationService(SimulationSlot.SECONDARY).runBatchSimulation(SimulationSlot.SECONDARY, owner, parametersSupport.buildDualBatchSimulationConfig(SimulationSlot.SECONDARY));
        updateDualSessionControls();
    }

    private void handleDualSlotRunStop(SimulationSlot slot) {
        SimulationApplicationService service = owner.dualSimulationService(slot);
        Controller controller = owner.dualController(slot);
        if (service == null || controller == null) {
            return;
        }
        try {
            if (controller.isSimulationRunning(slot)) {
                controller.stopSimulations(slot);
                owner.prependDualMessage(slot, "Stopped slot simulation.\n");
                updateDualSessionControls();
                return;
            }
            ensureDualNetworkInitializedForRun(slot);
            ensureDualSlotReady(slot);
            SimulationResultRepository.getInstance(slot).clearRepository();
            SimulationUiHelper.resetOutcomePanels(owner.dualWorkspacePanel(slot).getOutcomesPanels());
            owner.dualWorkspacePanel(slot).getMessagesTextArea().setText("");
            service.setVisualizationDelay(owner.getEffectiveDualVisualizationDelayMillis(slot));
            owner.prependDualMessage(slot, "Starting slot simulation...\n");
            service.runSimulation(slot, owner, parametersSupport.buildDualSimulationConfig(slot));
            updateDualSessionControls();
        } catch (Exception ex) {
            showError(ex);
        }
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

    private void applyDefaultDualWorkspaceLayout() {
        owner.dualWorkspacePanel(SimulationSlot.PRIMARY).applyBalancedDefaultLayout();
        owner.dualWorkspacePanel(SimulationSlot.SECONDARY).applyBalancedDefaultLayout();
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(owner, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}
