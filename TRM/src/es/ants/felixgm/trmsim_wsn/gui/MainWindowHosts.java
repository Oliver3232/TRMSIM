package es.ants.felixgm.trmsim_wsn.gui;

import es.ants.felixgm.trmsim_wsn.Controller;
import es.ants.felixgm.trmsim_wsn.app.SimulationApplicationService;
import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Service;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.TRMParametersPanel;
import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;

final class MainWindowHosts {
    private MainWindowHosts() {
    }

    static MainWindowInitializationController.Host initialization(TRMSim_WSN window) {
        return new MainWindowInitializationController.Host() {
            public void setGraphWorkspace(SimulationGraphWorkspace workspace) { window.graphWorkspace = workspace; }
            public SimulationGraphWorkspace getGraphWorkspace() { return window.graphWorkspace; }
            public es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel getCurrentNetworkPanel() { return window.networkPanel; }
            public void renderCurrentNetworkOnPanel(es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel panel) { MainWindowRenderController.renderCurrentNetworkOnPanel(window, panel); }
            public void clearNodeInspector() { MainWindowNodeInspectorController.clearNodeInspector(nodeInspector(window)); }
            public void selectNodeByIdFromBootstrap(int nodeId) { MainWindowNodeInspectorController.selectNodeById(nodeInspector(window), nodeId); }
            public void handlePauseResumeRequestFromBootstrap() { MainWindowSimulationControlsController.handlePauseResumeRequest(simulationControls(window)); }
            public void handleStopRequestFromBootstrap() { MainWindowSimulationControlsController.handleStopRequest(simulationControls(window)); }
            public JCheckBox getShowIdsCheckBox() { return window.showIdsCheckBox; }
            public JCheckBox getShowLinksCheckBox() { return window.showLinksCheckBox; }
            public JCheckBox getShowRangesCheckBox() { return window.showRangesCheckBox; }
            public JCheckBox getShowGridCheckBox() { return window.showGridCheckBox; }
            public JSlider getDelaySlider() { return window.delaySlider; }
            public void installNetworkPanelSelectionHandler(es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel panel) { MainWindowRuntimeSupport.installNetworkPanelSelectionHandler(window, panel); }
            public void installGraphInfoStrip() { MainWindowGraphStripController.install(window); }
            public void applyModernLayout() { MainWindowRuntimeSupport.applyModernLayout(window); }
            public void installEmbeddedNodeInspector() { MainWindowEmbeddedInspectorController.install(window); }
            public void updateParametersSourceView() { MainWindowRuntimeSupport.updateParametersSourceView(window); }
            public void updateRunSimulationsControls() { MainWindowSimulationControlsController.updateRunSimulationsControls(simulationControls(window)); }
            public java.util.List<MiniLegendPanel.Item> createLegendItems() { return MainWindowNodeInspectorController.createLegendItems(window.legendPanel); }
            public void syncEmbeddedAndFullscreenDisplayControls() { MainWindowSimulationControlsController.syncEmbeddedAndFullscreenDisplayControls(simulationControls(window)); }
            public void initializeTRModels() { window.initializeTRModels(); }
            public void setController(Controller controller) { TRMSim_WSN.C = controller; }
            public void setSimulationService(SimulationApplicationService service) { window.simulationService = service; }
            public void triggerInitialTrustModelSelection() throws Exception { MainWindowRuntimeSupport.triggerInitialTrustModelSelection(window); }
            public void setSize(int width, int height) { window.setSize(width, height); }
            public void setLocationRelativeTo(java.awt.Component component) { window.setLocationRelativeTo(component); }
        };
    }

    static MainWindowNodeInspectorController.Host nodeInspector(TRMSim_WSN window) {
        return new MainWindowNodeInspectorController.Host() {
            public Integer getSelectedNodeId() { return window.selectedNodeId; }
            public void setSelectedNodeId(Integer nodeId) { window.selectedNodeId = nodeId; }
            public SimulationGraphWorkspace getGraphWorkspace() { return window.graphWorkspace; }
            public JLabel getGraphNodeInspectorTitleLabel() { return window.graphNodeInspectorTitleLabel; }
            public JTextArea getGraphNodeInspectorTextArea() { return window.graphNodeInspectorTextArea; }
            public JPanel getSensorPropertiesPanel() { return window.sensorPropertiesPanel; }
            public JTextField getSensorIdTextField() { return window.sensorIdTextField; }
            public JTextField getXCoordinateTextField() { return window.xCoordinateTextField; }
            public JTextField getYCoordinateTextField() { return window.yCoordinateTextField; }
            public JLabel getNeighborsLabel() { return window.neighborsLabel; }
            public javax.swing.JList getNeighborsList() { return window.neighborsList; }
            public JScrollPane getNeighborsScrollPane() { return window.neighborsScrollPane; }
            public JPanel getGraphInspectorLegendWrapper() { return window.graphInspectorLegendWrapper; }
            public void setGraphInspectorLegendPanel(MiniLegendPanel panel) { window.graphInspectorLegendPanel = panel; }
            public CompactLegendPanel getDashboardLegendPanel() { return window.dashboardLegendPanel; }
            public es.ants.felixgm.trmsim_wsn.gui.legendpanels.LegendPanel getLegendPanel() { return window.legendPanel; }
            public Network getCurrentNetwork() { return TRMSim_WSN.C.get_currentNetwork(); }
            public void setGraphInspectorExpanded(boolean expanded) { MainWindowEmbeddedInspectorController.setGraphInspectorExpanded(window, expanded); }
            public void scheduleGraphInspectorAutoHide() { MainWindowRuntimeSupport.scheduleGraphInspectorAutoHide(window); }
            public void stopGraphInspectorAutoHide() { MainWindowRuntimeSupport.stopGraphInspectorAutoHide(window); }
        };
    }

    static MainWindowSimulationControlsController.Host simulationControls(TRMSim_WSN window) {
        return new MainWindowSimulationControlsController.Host() {
            public boolean isBatchRunning() { return window.batchSimulationState == TRMSim_WSN.BatchSimulationState.RUNNING; }
            public boolean isBatchPaused() { return window.batchSimulationState == TRMSim_WSN.BatchSimulationState.PAUSED; }
            public void markBatchIdle() { window.batchSimulationState = TRMSim_WSN.BatchSimulationState.IDLE; }
            public void invokeRunBatch() { window.runSimulationsButtonActionPerformed(null); }
            public void invokeStopBatch() { window.stopSimulationsButtonActionPerformed(null); }
            public AbstractButton getRunSimulationsButton() { return window.runSimulationsButton; }
            public JMenuItem getRunSimulationsMenuItem() { return window.runSimulationsMenuItem; }
            public JLabel getGraphStripSimulationStateLabel() { return window.graphStripSimulationStateLabel; }
            public javax.swing.JButton getGraphStripRunButton() { return window.graphStripRunButton; }
            public javax.swing.JButton getGraphStripStopButton() { return window.graphStripStopButton; }
            public JLabel getGraphInspectorSimulationStateLabel() { return window.graphInspectorSimulationStateLabel; }
            public javax.swing.JButton getGraphInspectorPauseResumeButton() { return window.graphInspectorPauseResumeButton; }
            public javax.swing.JButton getGraphInspectorStopButton() { return window.graphInspectorStopButton; }
            public JCheckBox getShowIdsCheckBox() { return window.showIdsCheckBox; }
            public JCheckBox getShowLinksCheckBox() { return window.showLinksCheckBox; }
            public JCheckBox getShowRangesCheckBox() { return window.showRangesCheckBox; }
            public JCheckBox getShowGridCheckBox() { return window.showGridCheckBox; }
            public JCheckBox getGraphInspectorShowIdsCheckBox() { return window.graphInspectorShowIdsCheckBox; }
            public JCheckBox getGraphInspectorShowLinksCheckBox() { return window.graphInspectorShowLinksCheckBox; }
            public JCheckBox getGraphInspectorShowRangesCheckBox() { return window.graphInspectorShowRangesCheckBox; }
            public JCheckBox getGraphInspectorShowGridCheckBox() { return window.graphInspectorShowGridCheckBox; }
            public JSlider getDelaySlider() { return window.delaySlider; }
            public JSlider getGraphInspectorDelaySlider() { return window.graphInspectorDelaySlider; }
            public SimulationGraphWorkspace getGraphWorkspace() { return window.graphWorkspace; }
        };
    }

    static MainWindowParametersController.Host parameters(TRMSim_WSN window) {
        return new MainWindowParametersController.Host() {
            public Controller getController() { return TRMSim_WSN.C; }
            public boolean isParametersFileSelected() { return MainWindowRuntimeSupport.isParametersFileSelected(window); }
            public JLabel getParametersFileLabel() { return window.parametersFileLabel; }
            public JTextField getParametersFileTextField() { return window.parametersFileTextField; }
            public AbstractButton getBrowseButton() { return window.browseButton; }
            public JTextArea getParametersFileContentTextArea() { return window.parametersFileContentTextArea; }
            public AbstractButton getSaveParametersFileContentButton() { return window.saveParametersFileContentButton; }
            public JMenuItem getLoadParametersMenuItem() { return window.loadParametersMenuItem; }
            public JMenuItem getSaveParametersMenuItem() { return window.saveParametersMenuItem; }
            public TRMParametersPanel getParametersPanel() { return window.TRM_ParametersPanel; }
            public AbstractButton getApplyParametersChangesButton() { return window.applyParametersChangesButton; }
            public JMenuItem getApplyParametersChangesMenuItem() { return window.applyParametersChangesMenuItem; }
            public void updateParametersSourceView() { MainWindowRuntimeSupport.updateParametersSourceView(window); }
        };
    }

    static MainWindowConfigurationController.Host configuration(TRMSim_WSN window) {
        return new MainWindowConfigurationController.Host() {
            public Controller getController() { return TRMSim_WSN.C; }
            public SimulationApplicationService getSimulationService() { return window.simulationService; }
            public JSlider getDelaySlider() { return window.delaySlider; }
            public JTextField getDelayTextField() { return window.delayTextField; }
            public JSlider getRadioRangeSlider() { return window.radioRangeSlider; }
            public JTextField getRadioRangeTextField() { return window.radioRangeTextField; }
            public JSpinner getMinNumSensorsSpinner() { return window.minNumSensorsSpinner; }
            public JSpinner getMaxNumSensorsSpinner() { return window.maxNumSensorsSpinner; }
            public JSlider getPercentageClientsSlider() { return window.percentageClientsSlider; }
            public JSlider getPercentageRelayServersSlider() { return window.percentageRelayServersSlider; }
            public JSlider getPercentageMaliciousServersSlider() { return window.percentageMaliciousServersSlider; }
            public JCheckBox getDynamicCheckBox() { return window.dynamicWSNsCheckBox; }
            public JCheckBox getOscillatingCheckBox() { return window.oscillatingWSNsCheckBox; }
            public JCheckBox getCollusionCheckBox() { return window.collusionCheckBox; }
            public JSpinner getNumExecutionsSpinner() { return window.numExecutionsSpinner; }
            public JSpinner getNumNetworksSpinner() { return window.numNetworksSpinner; }
            public void syncEmbeddedAndFullscreenDisplayControls() { MainWindowSimulationControlsController.syncEmbeddedAndFullscreenDisplayControls(simulationControls(window)); }
            public void paintNetwork(Network network, Service requiredService) throws Exception { window.paintNetwork(network, requiredService); }
            public void showError(Exception ex) {
                JOptionPane.showMessageDialog(window, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        };
    }

    static MainWindowUiStateController.Host uiState(TRMSim_WSN window) {
        return new MainWindowUiStateController.Host() {
            public AbstractButton getRunTrmButton() { return window.runTRMButton; }
            public JMenuItem getRunTrmMenuItem() { return window.runTRMmenuItem; }
            public AbstractButton getResetWsnButton() { return window.resetWSNButton; }
            public JMenuItem getResetWsnMenuItem() { return window.resetWSNmenuItem; }
            public AbstractButton getSaveWsnButton() { return window.saveWSNButton; }
            public JMenuItem getSaveWsnMenuItem() { return window.saveWSNmenuItem; }
            public AbstractButton getRunSimulationsButton() { return window.runSimulationsButton; }
            public JMenuItem getRunSimulationsMenuItem() { return window.runSimulationsMenuItem; }
            public AbstractButton getLoadWsnButton() { return window.loadWSNButton; }
            public JMenuItem getLoadWsnMenuItem() { return window.loadWSNmenuItem; }
            public AbstractButton getNewWsnButton() { return window.newWSNButton; }
            public JMenuItem getNewWsnMenuItem() { return window.newWSNmenuItem; }
            public JSlider getPercentageMaliciousServersSlider() { return window.percentageMaliciousServersSlider; }
            public JLabel getPercentageMaliciousServersLabel() { return window.percentageMaliciousServersLabel; }
            public JSlider getPercentageRelayServersSlider() { return window.percentageRelayServersSlider; }
            public JLabel getPercentageRelayServersLabel() { return window.percentageRelayServersLabel; }
            public JSlider getRadioRangeSlider() { return window.radioRangeSlider; }
            public JLabel getRadioRangeLabel() { return window.radioRangeLabel; }
            public JSpinner getNumExecutionsSpinner() { return window.numExecutionsSpinner; }
            public JLabel getNumExecutionsLabel() { return window.numExecutionsLabel; }
            public JSpinner getNumNetworksSpinner() { return window.numNetworksSpinner; }
            public JLabel getNumNetworksLabel() { return window.numNetworksLabel; }
            public JSpinner getMinNumSensorsSpinner() { return window.minNumSensorsSpinner; }
            public JLabel getMinNumSensorsLabel() { return window.minNumSensorsLabel; }
            public JSpinner getMaxNumSensorsSpinner() { return window.maxNumSensorsSpinner; }
            public JLabel getMaxNumSensorsLabel() { return window.maxNumSensorsLabel; }
            public JCheckBox getDynamicCheckBox() { return window.dynamicWSNsCheckBox; }
            public JCheckBox getOscillatingCheckBox() { return window.oscillatingWSNsCheckBox; }
            public JCheckBox getCollusionCheckBox() { return window.collusionCheckBox; }
            public JLabel getTrModelLabel() { return window.TRModelLabel; }
            public JComboBox getTrModelComboBox() { return window.TRModelComboBox; }
            public JMenu getTrModelMenu() { return window.TRModelMenu; }
            public JPanel getSensorPropertiesPanel() { return window.sensorPropertiesPanel; }
            public JSlider getPercentageClientsSlider() { return window.percentageClientsSlider; }
            public JLabel getPercentageClientsLabel() { return window.percentageClientsLabel; }
            public String getSelectedTrustModelName() { return MainWindowRuntimeSupport.getSelectedTrustModelName(window); }
        };
    }

    static MainWindowNetworkOverlayController.Host overlay(TRMSim_WSN window) {
        return new MainWindowNetworkOverlayController.Host() {
            public javax.swing.JLayeredPane getNetworkOverlayPane() { return window.networkOverlayPane; }
            public void setNetworkOverlayPane(javax.swing.JLayeredPane value) { window.networkOverlayPane = value; }
            public JPanel getNetworkPanelContainer() { return window.networkPanelContainer; }
            public es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel getCurrentNetworkPanel() { return window.networkPanel; }
            public JPanel getGraphNodeInspectorPanel() { return window.graphNodeInspectorPanel; }
            public int getGraphInspectorCurrentWidth() { return window.graphNodeInspectorCurrentWidth; }
            public int getGraphInspectorMargin() { return window.GRAPH_INSPECTOR_MARGIN; }
            public int getGraphInspectorCollapsedWidth() { return window.GRAPH_INSPECTOR_COLLAPSED_WIDTH; }
            public int getGraphInspectorMinHeight() { return window.GRAPH_INSPECTOR_MIN_HEIGHT; }
            public int getGraphInspectorMaxHeight() { return window.GRAPH_INSPECTOR_MAX_HEIGHT; }
            public void installNetworkPanelSelectionHandler(es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel panel) { MainWindowRuntimeSupport.installNetworkPanelSelectionHandler(window, panel); }
        };
    }

    static SimulationEventHelper.EventHost simulationEvents(TRMSim_WSN window) {
        return new SimulationEventHelper.EventHost() {
            public void paintUpdatedNetwork(Network network) throws Exception {
                window.paintNetwork(network, TRMSim_WSN.C.get_requiredService());
            }

            public void refreshSelectedNodeDetails() {
                MainWindowNodeInspectorController.refreshSelectedNodeDetails(nodeInspector(window));
            }

            public java.util.Collection<es.ants.felixgm.trmsim_wsn.gui.outcomespanels.OutcomesPanel> getOutcomesPanels() {
                return window.outcomesPanels;
            }

            public void finishSimulationUi() {
                window.finishSimulationUi();
            }

            public void handleSimulationFailure(Exception exception) {
                window.handleSimulationFailure(exception);
            }

            public void sleepAfterUiUpdate() {
                TRMSim_WSN.C.sleep();
            }

            public String getSelectedTrustModelName() {
                return MainWindowRuntimeSupport.getSelectedTrustModelName(window);
            }

            public JTextArea getMessagesTextArea() {
                return window.messagesTextArea;
            }
        };
    }
}
