package es.ants.felixgm.trmsim_wsn.gui;

import es.ants.felixgm.trmsim_wsn.Controller;
import es.ants.felixgm.trmsim_wsn.SimulationSlot;
import es.ants.felixgm.trmsim_wsn.gui.dual.DualSimulationWorkspacePanel;
import es.ants.felixgm.trmsim_wsn.gui.export.DualSimulationExportHelper;
import es.ants.felixgm.trmsim_wsn.gui.export.GraphImageExporter;
import es.ants.felixgm.trmsim_wsn.gui.export.SimulationResultRepository;
import es.ants.felixgm.trmsim_wsn.gui.graph.SimulationGraphWorkspace;
import es.ants.felixgm.trmsim_wsn.gui.legendpanels.LegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowNodeInspectorController;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.JavaFXNetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.OutcomesPanel;
import es.ants.felixgm.trmsim_wsn.gui.support.NetworkRenderSupport;
import es.ants.felixgm.trmsim_wsn.gui.support.NodeInspectorHelper;
import es.ants.felixgm.trmsim_wsn.gui.trustmodel.TrustModelUiFactory;
import es.ants.felixgm.trmsim_wsn.network.Network;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;

final class DualModeWorkspaceSupport {
    private final TRMSim_WSN owner;
    private final DualModeParametersSupport parametersSupport;

    DualModeWorkspaceSupport(TRMSim_WSN owner, DualModeParametersSupport parametersSupport) {
        this.owner = owner;
        this.parametersSupport = parametersSupport;
    }

    void rebuildDualWorkspaceUi(SimulationSlot slot) {
        DualSimulationWorkspacePanel workspacePanel = owner.dualWorkspacePanel(slot);
        String trustModelName = owner.dualToolbarPanel(slot).getTrustModelComboBox().getSelectedItem() instanceof String
                ? (String) owner.dualToolbarPanel(slot).getTrustModelComboBox().getSelectedItem()
                : null;
        if (trustModelName == null || trustModelName.trim().isEmpty()) {
            workspacePanel.setNetworkPanel(new NetworkPanel());
            workspacePanel.setOutcomesPanels(new ArrayList<OutcomesPanel>());
            owner.dualOutcomesPanels.put(slot, workspacePanel.getOutcomesPanels());
            return;
        }

        TrustModelUiFactory.Descriptor descriptor = TrustModelUiFactory.get(trustModelName);
        NetworkPanel slotNetworkPanel = createDualNetworkPanel(trustModelName);
        Collection<OutcomesPanel> slotOutcomesPanels = descriptor.createOutcomesPanels();
        LegendPanel slotLegendPanel = descriptor.createLegendPanel();

        workspacePanel.setNetworkPanel(slotNetworkPanel);
        workspacePanel.setOutcomesPanels(slotOutcomesPanels);
        owner.dualOutcomesPanels.put(slot, slotOutcomesPanels);
        owner.dualLegendPanels.put(slot, slotLegendPanel);
        slotLegendPanel.setBackground(Color.white);
        slotLegendPanel.plotLegend();
        workspacePanel.setSettingsContent(new JScrollPane(parametersSupport.createDualSettingsPanel(slot)));
        workspacePanel.setParametersContent(parametersSupport.createDualParametersPanel(slot, trustModelName));
        installDualNetworkPanelSelectionHandler(slot, slotNetworkPanel);

        SimulationGraphWorkspace graphWorkspace = owner.dualGraphWorkspaces.get(slot);
        if (graphWorkspace != null) {
            java.util.List<es.ants.felixgm.trmsim_wsn.gui.layout.MiniLegendPanel.Item> legendItems =
                    MainWindowNodeInspectorController.createLegendItems(slotLegendPanel);
            graphWorkspace.setFullscreenLegendItems(legendItems);
            graphWorkspace.applyVisualizationControlsToPanels(slotNetworkPanel);
            graphWorkspace.updateDisplayControlsState(
                    owner.dualShowIds(slot),
                    owner.dualShowLinks(slot),
                    owner.dualShowRanges(slot),
                    owner.dualShowGrid(slot),
                    owner.dualDelayValue(slot),
                    owner.delaySlider.getMinimum(),
                    owner.delaySlider.getMaximum());
            workspacePanel.setLegendItems(legendItems);
            workspacePanel.updateVisualizationControlsState(
                    (String) graphWorkspace.getVisualThemeComboBox().getSelectedItem(),
                    graphWorkspace.getEnable3DNavigationCheckBox().isSelected(),
                    (String) graphWorkspace.getCameraPresetComboBox().getSelectedItem());
        }
    }

    void clearDualSelectedNodeState(SimulationSlot slot) {
        owner.dualSelectedNodeIds.remove(slot);
        SimulationGraphWorkspace workspace = owner.dualGraphWorkspaces.get(slot);
        if (workspace != null) {
            workspace.setSelectedSensorId(null);
            workspace.updateSelectedNodeSummary("No node selected", "Click any node in the graph to inspect its live state and exported metrics.");
        }
        DualSimulationWorkspacePanel workspacePanel = owner.dualWorkspacePanel(slot);
        if (workspacePanel != null) {
            workspacePanel.updateSelectedNodeSummary("No node selected", "Click any node in the graph to inspect its live state and exported metrics.");
        }
    }

    void renderDualWorkspaceNetwork(SimulationSlot slot, Network network) {
        Controller controller = owner.dualController(slot);
        if (controller == null) {
            return;
        }
        try {
            NetworkPanel slotNetworkPanel = owner.dualWorkspacePanel(slot).getNetworkPanel();
            if (slotNetworkPanel == null) {
                return;
            }
            if (slotNetworkPanel instanceof JavaFXNetworkPanel) {
                ((JavaFXNetworkPanel) slotNetworkPanel).ensureActiveRendering();
            }
            NetworkRenderSupport.renderNetwork(
                    slotNetworkPanel,
                    network,
                    controller.get_requiredService(),
                    NetworkRenderSupport.createState(
                            owner.radioRangeSlider.getValue() / (double) owner.radioRangeSlider.getMaximum(),
                            owner.dualShowRanges(slot),
                            owner.dualShowLinks(slot),
                            owner.dualShowIds(slot),
                            owner.dualShowGrid(slot)));
            slotNetworkPanel.revalidate();
            slotNetworkPanel.repaint();
            owner.dualWorkspacePanel(slot).revalidate();
            owner.dualWorkspacePanel(slot).repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(owner, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    void refreshDualNetworksIfNeeded() {
        if (owner.appMode != AppMode.DUAL) {
            return;
        }
        Controller primaryController = owner.dualController(SimulationSlot.PRIMARY);
        Controller secondaryController = owner.dualController(SimulationSlot.SECONDARY);
        renderDualWorkspaceNetwork(SimulationSlot.PRIMARY, primaryController == null ? null : primaryController.get_currentNetwork(SimulationSlot.PRIMARY));
        renderDualWorkspaceNetwork(SimulationSlot.SECONDARY, secondaryController == null ? null : secondaryController.get_currentNetwork(SimulationSlot.SECONDARY));
        updateSlotDisplayControls(SimulationSlot.PRIMARY);
        updateSlotDisplayControls(SimulationSlot.SECONDARY);
    }

    void renderCurrentDualNetworkOnPanel(SimulationSlot slot, NetworkPanel targetPanel) {
        Controller controller = owner.dualController(slot);
        if (controller == null || targetPanel == null) {
            return;
        }
        try {
            if (targetPanel instanceof JavaFXNetworkPanel) {
                ((JavaFXNetworkPanel) targetPanel).ensureActiveRendering();
            }
            NetworkRenderSupport.renderNetwork(
                    targetPanel,
                    controller.get_currentNetwork(slot),
                    controller.get_requiredService(),
                    NetworkRenderSupport.createState(
                            owner.radioRangeSlider.getValue() / (double) owner.radioRangeSlider.getMaximum(),
                            owner.dualShowRanges(slot),
                            owner.dualShowLinks(slot),
                            owner.dualShowIds(slot),
                            owner.dualShowGrid(slot)));
            targetPanel.revalidate();
            targetPanel.repaint();
        } catch (Exception ignored) {
        }
    }

    void installDualNetworkPanelSelectionHandler(SimulationSlot slot, NetworkPanel panel) {
        if (panel == null) {
            return;
        }
        if (panel instanceof JavaFXNetworkPanel) {
            ((JavaFXNetworkPanel) panel).ensureActiveRendering();
        }
        panel.setSensorSelectionListener(sensor -> {
            if (sensor != null) {
                owner.dualSelectedNodeIds.put(slot, Integer.valueOf(sensor.id()));
                refreshDualSelectedNodeDetails(slot, sensor.id());
            }
        });
    }

    void refreshDualSelectedNodeDetails(SimulationSlot slot, int sensorId) {
        Controller controller = owner.dualController(slot);
        if (controller == null) {
            return;
        }
        es.ants.felixgm.trmsim_wsn.network.Sensor sensor = controller.getSensor(slot, sensorId);
        SimulationGraphWorkspace workspace = owner.dualGraphWorkspaces.get(slot);
        if (sensor == null || workspace == null) {
            return;
        }
        String title = NodeInspectorHelper.buildNodeTitle(sensor);
        String body = NodeInspectorHelper.buildNodeDetailsText(sensor, controller.get_currentNetwork(slot));
        workspace.setSelectedSensorId(Integer.valueOf(sensor.id()));
        workspace.updateSelectedNodeSummary(title, body);
        owner.dualWorkspacePanel(slot).updateSelectedNodeSummary(title, body);
    }

    void openDualFullscreen(SimulationSlot slot) {
        SimulationGraphWorkspace workspace = owner.dualGraphWorkspaces.get(slot);
        if (workspace == null) {
            return;
        }
        if (workspace.isFullscreenOpen()) {
            workspace.getFullscreenGraphButton().doClick();
            return;
        }
        if (owner.isDualSlotTrmActive(slot)) {
            JOptionPane.showMessageDialog(
                    owner,
                    owner.slotLabel(slot) + " is running a T&R simulation. Stop it before opening fullscreen mode.",
                    "Fullscreen Blocked",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        workspace.getFullscreenGraphButton().doClick();
    }

    void showDualExportDialog() {
        try {
            DualSimulationExportHelper.showExportDialog(
                    owner,
                    SimulationResultRepository.getInstance(SimulationSlot.PRIMARY),
                    SimulationResultRepository.getInstance(SimulationSlot.SECONDARY),
                    new DualSimulationExportHelper.ExportHost() {
                        public boolean ensureSimulationDataAvailable(SimulationSlot slot, SimulationResultRepository repository) {
                            if (repository.getResultCount() == 0) {
                                JOptionPane.showMessageDialog(
                                        owner,
                                        owner.slotLabel(slot) + " has no simulation data available for export.",
                                        "No Data",
                                        JOptionPane.WARNING_MESSAGE);
                                return false;
                            }
                            return true;
                        }

                        public String exportEnergyGraph(java.awt.Component dialogOwner, SimulationSlot slot, es.ants.felixgm.trmsim_wsn.gui.export.ExportRequest request) throws Exception {
                            OutcomesPanel energyOutcomesPanel = getEnergyOutcomesPanel(owner.dualWorkspacePanel(slot).getOutcomesPanels());
                            if (energyOutcomesPanel == null) {
                                JOptionPane.showMessageDialog(
                                        dialogOwner,
                                        "Energy Consumption graph is not available for " + owner.slotLabel(slot) + ".",
                                        "Export Failed",
                                        JOptionPane.WARNING_MESSAGE);
                                return null;
                            }
                            return GraphImageExporter.exportCurrentGraph(dialogOwner, energyOutcomesPanel, energyOutcomesPanel.getLabel() + " " + owner.slotLabel(slot), request);
                        }
                    });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(owner, "Error during export: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    void applyAppModeLayout(AppMode mode) {
        BorderLayout layout = (BorderLayout) owner.getContentPane().getLayout();
        if (owner.singleModeNorthComponent == null) {
            owner.singleModeNorthComponent = layout.getLayoutComponent(owner.getContentPane(), BorderLayout.NORTH);
        }
        if (owner.singleModeWestComponent == null) {
            owner.singleModeWestComponent = layout.getLayoutComponent(owner.getContentPane(), BorderLayout.WEST);
        }
        if (owner.singleModeCenterComponent == null) {
            owner.singleModeCenterComponent = layout.getLayoutComponent(owner.getContentPane(), BorderLayout.CENTER);
        }

        if (mode == AppMode.DUAL) {
            owner.syncDualShellFromControllerState();
            if (owner.singleModeNorthComponent != null) {
                owner.getContentPane().remove(owner.singleModeNorthComponent);
            }
            if (owner.singleModeWestComponent != null) {
                owner.getContentPane().remove(owner.singleModeWestComponent);
            }
            if (owner.singleModeCenterComponent != null) {
                owner.getContentPane().remove(owner.singleModeCenterComponent);
            }
            owner.getContentPane().add(owner.dualSimulationShellPanel, BorderLayout.CENTER);
            reattachDualVisualizationHooks();
            refreshDualNetworksIfNeeded();
        } else {
            owner.getContentPane().remove(owner.dualSimulationShellPanel);
            if (owner.singleModeNorthComponent != null && owner.singleModeNorthComponent.getParent() != owner.getContentPane()) {
                owner.getContentPane().add(owner.singleModeNorthComponent, BorderLayout.NORTH);
            }
            if (owner.singleModeWestComponent != null && owner.singleModeWestComponent.getParent() != owner.getContentPane()) {
                owner.getContentPane().add(owner.singleModeWestComponent, BorderLayout.WEST);
            }
            if (owner.singleModeCenterComponent != null && owner.singleModeCenterComponent.getParent() != owner.getContentPane()) {
                owner.getContentPane().add(owner.singleModeCenterComponent, BorderLayout.CENTER);
            }
        }
    }

    void reattachDualVisualizationHooks() {
        for (SimulationSlot slot : SimulationSlot.values()) {
            NetworkPanel panel = owner.dualWorkspacePanel(slot).getNetworkPanel();
            installDualNetworkPanelSelectionHandler(slot, panel);
            SimulationGraphWorkspace workspace = owner.dualGraphWorkspaces.get(slot);
            if (workspace != null && panel != null) {
                workspace.applyVisualizationControlsToPanels(panel);
            }
        }
    }

    OutcomesPanel getEnergyOutcomesPanel(Collection<OutcomesPanel> outcomesPanels) {
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

    private NetworkPanel createDualNetworkPanel(String trustModelName) {
        return TrustModelUiFactory.get(trustModelName).createNetworkPanel();
    }

    private void updateSlotDisplayControls(SimulationSlot slot) {
        SimulationGraphWorkspace workspace = owner.dualGraphWorkspaces.get(slot);
        if (workspace != null) {
            workspace.updateDisplayControlsState(
                    owner.dualShowIds(slot),
                    owner.dualShowLinks(slot),
                    owner.dualShowRanges(slot),
                    owner.dualShowGrid(slot),
                    owner.dualDelayValue(slot),
                    owner.delaySlider.getMinimum(),
                    owner.delaySlider.getMaximum());
        }
        owner.dualWorkspacePanel(slot).updateDisplayControlsState(
                owner.dualShowIds(slot),
                owner.dualShowLinks(slot),
                owner.dualShowRanges(slot),
                owner.dualShowGrid(slot),
                owner.dualDelayValue(slot),
                owner.delaySlider.getMinimum(),
                owner.delaySlider.getMaximum());
    }
}
