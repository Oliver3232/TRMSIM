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
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.OutcomesPanel;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.TRMParametersPanel;
import es.ants.felixgm.trmsim_wsn.network.Network;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.util.Collection;

final class DualModeCoordinator {
    private final TRMSim_WSN owner;
    private final DualModeParametersSupport parametersSupport;
    private final DualModeWorkspaceSupport workspaceSupport;
    private final DualModeSessionSupport sessionSupport;
    private final DualModeScenarioSupport scenarioSupport;

    DualModeCoordinator(TRMSim_WSN owner) {
        this.owner = owner;
        this.parametersSupport = new DualModeParametersSupport(owner);
        this.workspaceSupport = new DualModeWorkspaceSupport(owner, parametersSupport);
        this.sessionSupport = new DualModeSessionSupport(owner, parametersSupport, workspaceSupport);
        this.scenarioSupport = new DualModeScenarioSupport(owner, parametersSupport, workspaceSupport, sessionSupport);
    }

    void initializeDualModeShell() {
        owner.initializeDualControllers();
        owner.dualSimulationShellPanel = new DualSimulationShellPanel();
        initializeDualGraphWorkspace(SimulationSlot.PRIMARY);
        initializeDualGraphWorkspace(SimulationSlot.SECONDARY);
        owner.dualSimulationShellPanel.getSessionTrmButton().addActionListener(evt -> sessionSupport.handleDualSharedTrmToggle());
        owner.dualSimulationShellPanel.getSessionRunButton().addActionListener(evt -> sessionSupport.handleDualSessionRunPauseResume());
        owner.dualSimulationShellPanel.getSessionStopButton().addActionListener(evt -> sessionSupport.handleDualSessionStop());
        owner.dualSimulationShellPanel.getImportScenarioButton().addActionListener(evt -> scenarioSupport.importDualScenarioForChosenSlot());
        owner.dualSimulationShellPanel.getModeSwitchButton().addActionListener(evt -> switchAppMode(AppMode.SINGLE));
        owner.dualSimulationShellPanel.getExportButton().addActionListener(evt -> showDualExportDialog());
        scenarioSupport.configureDualWorkspace(SimulationSlot.PRIMARY, owner.dualSimulationShellPanel.getPrimaryWorkspacePanel());
        scenarioSupport.configureDualWorkspace(SimulationSlot.SECONDARY, owner.dualSimulationShellPanel.getSecondaryWorkspacePanel());
        sessionSupport.updateDualSessionControls();
    }

    void switchAppMode(AppMode mode) {
        if (mode == owner.appMode) {
            return;
        }
        if (sessionSupport.hasActiveSimulation()) {
            JOptionPane.showMessageDialog(owner,
                    "Stop the active simulation before switching between Single and Dual mode.",
                    "Mode Switch Blocked",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (mode == AppMode.DUAL) {
            sessionSupport.resetDualModeState();
        } else {
            sessionSupport.resetSingleModeState();
        }
        owner.appMode = mode;
        workspaceSupport.applyAppModeLayout(mode);
        if (mode == AppMode.DUAL) {
            applyDefaultDualWorkspaceLayout();
        }
        if (owner.modeSwitchButton != null) {
            owner.modeSwitchButton.setText(mode == AppMode.DUAL ? "Single Mode" : "Dual Mode");
        }
        sessionSupport.updateDualSessionControls();
        sessionSupport.updateDualRefreshTimer();
        owner.getContentPane().revalidate();
        owner.getContentPane().repaint();
    }

    void syncDualShellFromControllerState() {
        if (owner.dualSimulationShellPanel == null) {
            return;
        }
        sessionSupport.syncDualWorkspaceFromControllerState(SimulationSlot.PRIMARY);
        sessionSupport.syncDualWorkspaceFromControllerState(SimulationSlot.SECONDARY);
    }

    void handleDualSessionRunPauseResume() {
        sessionSupport.handleDualSessionRunPauseResume();
    }

    void handleDualSessionStop() {
        sessionSupport.handleDualSessionStop();
    }

    void updateDualSessionControls() {
        sessionSupport.updateDualSessionControls();
    }

    void updateDualRefreshTimer() {
        sessionSupport.updateDualRefreshTimer();
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

    void invalidateDualScenarioSelection(SimulationSlot slot) {
        scenarioSupport.invalidateDualScenarioSelection(slot);
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
                owner.setDualSlotStartPending(slot, false);
                owner.setDualSlotTrmActive(slot, false);
                owner.setDualSlotBatchActive(slot, false);
                owner.prependDualMessage(slot, "Simulation completed. " + SimulationResultRepository.getInstance(slot).getResultCount() + " results saved.\n");
                sessionSupport.updateDualSessionControls();
            }

            public void handleSimulationFailure(Exception exception) {
                JOptionPane.showMessageDialog(owner, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                try {
                    for (SimulationSlot simulationSlot : SimulationSlot.values()) {
                        owner.setDualSlotStartPending(simulationSlot, false);
                        owner.setDualSlotTrmActive(simulationSlot, false);
                        owner.setDualSlotBatchActive(simulationSlot, false);
                        Controller controller = owner.dualController(simulationSlot);
                        if (controller != null) {
                            controller.stopSimulations(simulationSlot);
                        }
                    }
                } catch (Exception stopException) {
                    stopException.printStackTrace();
                }
                owner.dualSessionStartPending = false;
                sessionSupport.updateDualSessionControls();
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
                sessionSupport.handleDualSessionRunPauseResume();
            }

            @Override
            public void onStopRequested() {
                sessionSupport.handleDualSessionStop();
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

    private void applyDefaultDualWorkspaceLayout() {
        owner.dualWorkspacePanel(SimulationSlot.PRIMARY).applyBalancedDefaultLayout();
        owner.dualWorkspacePanel(SimulationSlot.SECONDARY).applyBalancedDefaultLayout();
    }
}
