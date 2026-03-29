package es.ants.felixgm.trmsim_wsn.gui;

import es.ants.felixgm.trmsim_wsn.Controller;
import es.ants.felixgm.trmsim_wsn.SimulationSlot;
import es.ants.felixgm.trmsim_wsn.app.SimulationApplicationService;
import es.ants.felixgm.trmsim_wsn.gui.dual.DualSimulationShellPanel;
import es.ants.felixgm.trmsim_wsn.gui.dual.DualSimulationWorkspacePanel;
import es.ants.felixgm.trmsim_wsn.gui.export.SimulationResultRepository;
import es.ants.felixgm.trmsim_wsn.gui.graph.SimulationGraphWorkspace;
import es.ants.felixgm.trmsim_wsn.gui.network.NetworkFileHelper;
import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.OutcomesPanel;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.TRMParametersPanel;
import es.ants.felixgm.trmsim_wsn.gui.support.SimulationUiHelper;
import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.scenario.PredefinedScenarioLoader;
import es.ants.felixgm.trmsim_wsn.scenario.ScenarioDefinition;
import es.ants.felixgm.trmsim_wsn.scenario.ScenarioFileHelper;
import es.ants.felixgm.trmsim_wsn.scenario.ScenarioSelectionHelper;
import es.ants.felixgm.trmsim_wsn.scenario.ScenarioUiBindingHelper;
import es.ants.felixgm.trmsim_wsn.trm.TrustModelRegistry;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import java.io.File;

final class DualModeScenarioSupport {
    private final TRMSim_WSN owner;
    private final DualModeParametersSupport parametersSupport;
    private final DualModeWorkspaceSupport workspaceSupport;
    private final DualModeSessionSupport sessionSupport;

    DualModeScenarioSupport(TRMSim_WSN owner,
                            DualModeParametersSupport parametersSupport,
                            DualModeWorkspaceSupport workspaceSupport,
                            DualModeSessionSupport sessionSupport) {
        this.owner = owner;
        this.parametersSupport = parametersSupport;
        this.workspaceSupport = workspaceSupport;
        this.sessionSupport = sessionSupport;
    }

    void configureDualWorkspace(SimulationSlot slot, DualSimulationWorkspacePanel workspacePanel) {
        DualSimulationShellPanel.SlotToolbarPanel toolbarPanel = owner.dualToolbarPanel(slot);
        toolbarPanel.getTrustModelComboBox().removeAllItems();
        for (String modelName : TrustModelRegistry.all().keySet()) {
            toolbarPanel.getTrustModelComboBox().addItem(modelName);
        }
        toolbarPanel.getTrustModelComboBox().addActionListener(evt -> applyDualTrustModelSelection(slot));
        toolbarPanel.getNewNetworkButton().addActionListener(evt -> createDualNetwork(slot));
        toolbarPanel.getLoadScenarioButton().addActionListener(evt -> loadDualScenario(slot));
        toolbarPanel.getLoadNetworkButton().addActionListener(evt -> loadDualNetwork(slot));
        toolbarPanel.getSaveNetworkButton().addActionListener(evt -> saveDualNetwork(slot));
        toolbarPanel.getResetNetworkButton().addActionListener(evt -> resetDualNetwork(slot));
        workspacePanel.getSaveScenarioButton().addActionListener(evt -> saveDualScenario(slot));
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

    void invalidateDualScenarioSelection(SimulationSlot slot) {
        if (!Boolean.TRUE.equals(owner.dualScenarioSelectionSync.get(slot))) {
            owner.dualWorkspacePanel(slot).updateScenarioSummary("Custom Configuration", "");
        }
    }

    void applyDualTrustModelSelection(SimulationSlot slot) {
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
        if (owner.isDualSlotSimulationActive(slot)) {
            String currentTrustModel = controller.getTrustModelName(slot);
            if (currentTrustModel != null && !currentTrustModel.equals(trustModelName)) {
                owner.dualTrustModelSelectionSync.put(slot, Boolean.TRUE);
                try {
                    owner.dualToolbarPanel(slot).getTrustModelComboBox().setSelectedItem(currentTrustModel);
                } finally {
                    owner.dualTrustModelSelectionSync.put(slot, Boolean.FALSE);
                }
            }
            JOptionPane.showMessageDialog(owner,
                    owner.slotLabel(slot) + " simulation is active. Stop it before changing the trust model.",
                    "Model Switch Blocked",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        invalidateDualScenarioSelection(slot);
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
            sessionSupport.updateDualGraphWorkspaceControls(slot);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    void createDualNetwork(SimulationSlot slot) {
        createDualNetwork(slot, true, "New WSN created\n");
    }

    void loadDualNetwork(SimulationSlot slot) {
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
            ScenarioUiBindingHelper.updateDualScenarioSummary(owner.dualWorkspacePanel(slot), PredefinedScenarioLoader.customScenarioPlaceholder());
            workspaceSupport.renderDualWorkspaceNetwork(slot, network);
            owner.dualWorkspacePanel(slot).getMessagesTextArea().setText("");
            owner.prependDualMessage(slot, "WSN loaded successfully\n");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    void loadDualScenario(SimulationSlot slot) {
        try {
            ScenarioDefinition scenario = ScenarioSelectionHelper.chooseScenario(owner);
            if (scenario == null) {
                return;
            }
            owner.dualScenarioSelectionSync.put(slot, Boolean.TRUE);
            ScenarioUiBindingHelper.applyToDualSettingsPanel(
                    (DualSettingsPanel) parametersSupport.createDualSettingsPanel(slot),
                    owner.dualToolbarPanel(slot).getTrustModelComboBox(),
                    owner.dualWorkspacePanel(slot),
                    scenario);
            disableScenarioDisplaySettings(slot);
            workspaceSupport.refreshDualNetworksIfNeeded();
            createDualNetwork(slot, true, "Predefined scenario loaded: " + scenario.getDisplayName() + "\n");
            owner.dualScenarioSelectionSync.put(slot, Boolean.FALSE);
        } catch (Exception ex) {
            owner.dualScenarioSelectionSync.put(slot, Boolean.FALSE);
            showError(ex);
        }
    }

    void importDualScenarioForChosenSlot() {
        SimulationSlot slot = chooseDualScenarioTargetSlot();
        if (slot != null) {
            importDualScenario(slot);
        }
    }

    void saveDualScenario(SimulationSlot slot) {
        try {
            DualSettingsPanel settingsPanel = (DualSettingsPanel) parametersSupport.createDualSettingsPanel(slot);
            String trustModelName = null;
            Object selectedTrustModel = owner.dualToolbarPanel(slot).getTrustModelComboBox().getSelectedItem();
            if (selectedTrustModel instanceof String) {
                trustModelName = (String) selectedTrustModel;
            }
            if (ScenarioFileHelper.saveScenario(owner, trustModelName, settingsPanel.buildBatchSimulationConfig())) {
                owner.prependDualMessage(slot, "Scenario saved successfully\n");
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    void saveDualNetwork(SimulationSlot slot) {
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

    void resetDualNetwork(SimulationSlot slot) {
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

    private void importDualScenario(SimulationSlot slot) {
        try {
            File selectedFile = ScenarioFileHelper.chooseScenarioFile(owner, ".", "Import Scenario", JFileChooser.OPEN_DIALOG);
            if (selectedFile == null) {
                return;
            }
            ScenarioDefinition scenario = ScenarioFileHelper.loadScenarioFromFile(selectedFile);
            owner.dualScenarioSelectionSync.put(slot, Boolean.TRUE);
            ScenarioUiBindingHelper.applyToDualSettingsPanel(
                    (DualSettingsPanel) parametersSupport.createDualSettingsPanel(slot),
                    owner.dualToolbarPanel(slot).getTrustModelComboBox(),
                    owner.dualWorkspacePanel(slot),
                    scenario);
            disableScenarioDisplaySettings(slot);
            workspaceSupport.refreshDualNetworksIfNeeded();
            createDualNetwork(slot, true, "Scenario imported: " + scenario.getDisplayName() + "\n");
            owner.dualScenarioSelectionSync.put(slot, Boolean.FALSE);
        } catch (Exception ex) {
            owner.dualScenarioSelectionSync.put(slot, Boolean.FALSE);
            showError(ex);
        }
    }

    private SimulationSlot chooseDualScenarioTargetSlot() {
        Object[] options = {"Simulation A", "Simulation B"};
        int selection = JOptionPane.showOptionDialog(
                owner,
                "Import the scenario into which simulation slot?",
                "Import Scenario",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);
        if (selection == 0) {
            return SimulationSlot.PRIMARY;
        }
        if (selection == 1) {
            return SimulationSlot.SECONDARY;
        }
        return null;
    }

    private void disableScenarioDisplaySettings(SimulationSlot slot) {
        owner.setDualShowIds(slot, false);
        owner.setDualShowLinks(slot, false);
        owner.setDualShowRanges(slot, false);
        owner.setDualShowGrid(slot, false);
        SimulationGraphWorkspace workspace = owner.dualGraphWorkspaces.get(slot);
        if (workspace != null) {
            workspace.updateDisplayControlsState(
                    false,
                    false,
                    false,
                    false,
                    owner.dualDelayValue(slot),
                    owner.delaySlider.getMinimum(),
                    owner.delaySlider.getMaximum());
        }
        owner.dualWorkspacePanel(slot).updateDisplayControlsState(
                false,
                false,
                false,
                false,
                owner.dualDelayValue(slot),
                owner.delaySlider.getMinimum(),
                owner.delaySlider.getMaximum());
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
