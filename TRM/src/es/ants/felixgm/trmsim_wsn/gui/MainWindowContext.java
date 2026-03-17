package es.ants.felixgm.trmsim_wsn.gui;

import es.ants.felixgm.trmsim_wsn.Controller;
import es.ants.felixgm.trmsim_wsn.app.BatchSimulationConfig;
import es.ants.felixgm.trmsim_wsn.app.NetworkGenerationConfig;
import es.ants.felixgm.trmsim_wsn.app.SimulationApplicationService;
import es.ants.felixgm.trmsim_wsn.app.SimulationConfig;
import es.ants.felixgm.trmsim_wsn.gui.events.SimulationEventHelper;
import es.ants.felixgm.trmsim_wsn.gui.graph.SimulationGraphWorkspace;
import es.ants.felixgm.trmsim_wsn.gui.layout.CompactLegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.layout.MiniLegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.legendpanels.LegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowGraphStripController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowRenderController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowNodeInspectorController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowNetworkOverlayController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowSimulationControlsController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.hosts.MainWindowSimulationEventHostFactory;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.OutcomesPanel;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.TRMParametersPanel;
import es.ants.felixgm.trmsim_wsn.gui.support.NetworkRenderSupport;
import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Sensor;
import es.ants.felixgm.trmsim_wsn.network.Service;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Point;
import java.util.Collection;
import java.util.List;

public final class MainWindowContext {
    private final TRMSim_WSN window;

    public MainWindowContext(TRMSim_WSN window) {
        this.window = window;
    }

    public TRMSim_WSN window() { return window; }
    public void setGraphWorkspace(SimulationGraphWorkspace workspace) { window.graphWorkspace = workspace; }
    public SimulationGraphWorkspace getGraphWorkspace() { return window.graphWorkspace; }
    public JComboBox getTrustModelComboBox() { return window.TRModelComboBox; }
    public JMenu getTrustModelMenu() { return window.TRModelMenu; }
    public String getLastAllowedTrustModel() { return window.lastAllowedTRModel; }
    public void setLastAllowedTrustModel(String value) { window.lastAllowedTRModel = value; }
    public NetworkPanel getCurrentNetworkPanel() { return window.networkPanel; }
    public NetworkPanel getMainNetworkPanel() { return window.networkPanel; }
    public void setCurrentNetworkPanel(NetworkPanel panel) { window.networkPanel = panel; }
    public void renderCurrentNetworkOnPanel(NetworkPanel panel) { MainWindowRenderController.renderCurrentNetworkOnPanel(this, panel); }
    public void attachNetworkPanel(NetworkPanel panel) { MainWindowNetworkOverlayController.attachNetworkPanelToOverlay(MainWindowHosts.overlay(window), panel); }
    public boolean isTrustModelDisabled(String modelName) { return MainWindowRuntimeSupport.isTRModelDisabled(modelName); }
    public void clearNodeInspector() { MainWindowNodeInspectorController.clearNodeInspector(MainWindowHosts.nodeInspector(window)); }
    public void selectNodeById(int nodeId) { MainWindowNodeInspectorController.selectNodeById(MainWindowHosts.nodeInspector(window), nodeId); }
    public void refreshSelectedNodeDetails() { MainWindowNodeInspectorController.refreshSelectedNodeDetails(MainWindowHosts.nodeInspector(window)); }
    public void refreshInspectorLegendPanel() { MainWindowNodeInspectorController.refreshInspectorLegendPanel(MainWindowHosts.nodeInspector(window)); }
    public void handlePauseResumeRequest() { MainWindowSimulationControlsController.handlePauseResumeRequest(MainWindowHosts.simulationControls(window)); }
    public void handleStopRequest() { MainWindowSimulationControlsController.handleStopRequest(MainWindowHosts.simulationControls(window)); }
    public JCheckBox getShowIdsCheckBox() { return window.showIdsCheckBox; }
    public JCheckBox getShowLinksCheckBox() { return window.showLinksCheckBox; }
    public JCheckBox getShowRangesCheckBox() { return window.showRangesCheckBox; }
    public JCheckBox getShowGridCheckBox() { return window.showGridCheckBox; }
    public JSlider getDelaySlider() { return window.delaySlider; }
    public void installNetworkPanelSelectionHandler(NetworkPanel panel) { MainWindowRuntimeSupport.installNetworkPanelSelectionHandler(window, panel); }
    public void installGraphInfoStrip() { MainWindowGraphStripController.install(this); }
    public void applyModernLayout() { MainWindowRuntimeSupport.applyModernLayout(window); }
    public void installEmbeddedNodeInspector() { MainWindowEmbeddedInspectorController.install(window); }
    public void applyVisualizationControls(NetworkPanel networkPanel) {
        if (window.graphWorkspace != null) {
            window.graphWorkspace.applyVisualizationControlsToPanels(networkPanel);
        }
    }
    public void updateParametersSourceView() { MainWindowRuntimeSupport.updateParametersSourceView(window); }
    public JTextField getParametersFileTextFieldRaw() { return window.parametersFileTextField; }
    public JTextArea getParametersFileContentTextAreaRaw() { return window.parametersFileContentTextArea; }
    public JPanel getLegendPanelContainer() { return window.legendPanelContainer; }
    public JPanel getNetworkPanelContainerPanel() { return window.networkPanelContainer; }
    public javax.swing.JTabbedPane getOutcomesTabbedPane() { return window.outcomesTabbedPane; }
    public void updateRunSimulationsControls() { MainWindowSimulationControlsController.updateRunSimulationsControls(MainWindowHosts.simulationControls(window)); }
    public List<MiniLegendPanel.Item> createLegendItems() { return MainWindowNodeInspectorController.createLegendItems(window.legendPanel); }
    public java.util.List<MiniLegendPanel.Item> createLegendItemsFromCurrentLegend() { return MainWindowNodeInspectorController.createLegendItems(window.legendPanel); }
    public void syncEmbeddedAndFullscreenDisplayControls() { MainWindowSimulationControlsController.syncEmbeddedAndFullscreenDisplayControls(MainWindowHosts.simulationControls(window)); }
    public void initializeTRModels() { window.initializeTRModels(); }
    public void setController(Controller controller) { TRMSim_WSN.C = controller; }
    public void setSimulationService(SimulationApplicationService service) { window.simulationService = service; }
    public void triggerInitialTrustModelSelection() throws Exception { MainWindowRuntimeSupport.triggerInitialTrustModelSelection(window); }
    public void setSize(int width, int height) { window.setSize(width, height); }
    public void setLocationRelativeTo(Component component) { window.setLocationRelativeTo(component); }
    public Integer getSelectedNodeId() { return window.selectedNodeId; }
    public void setSelectedNodeId(Integer nodeId) { window.selectedNodeId = nodeId; }
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
    public LegendPanel getLegendPanel() { return window.legendPanel; }
    public void setLegendPanel(LegendPanel panel) { window.legendPanel = panel; }
    public Network getCurrentNetwork() { return TRMSim_WSN.C.get_currentNetwork(); }
    public Sensor getSensorById(int sensorId) { return TRMSim_WSN.C.getSensor(sensorId); }
    public void setGraphInspectorExpanded(boolean expanded) { MainWindowEmbeddedInspectorController.setGraphInspectorExpanded(window, expanded); }
    public void scheduleGraphInspectorAutoHide() { MainWindowRuntimeSupport.scheduleGraphInspectorAutoHide(window); }
    public void stopGraphInspectorAutoHide() { MainWindowRuntimeSupport.stopGraphInspectorAutoHide(window); }
    public boolean isBatchRunning() { return window.batchSimulationState == TRMSim_WSN.BatchSimulationState.RUNNING; }
    public boolean isBatchPaused() { return window.batchSimulationState == TRMSim_WSN.BatchSimulationState.PAUSED; }
    public void markBatchIdle() { window.batchSimulationState = TRMSim_WSN.BatchSimulationState.IDLE; }
    public void markBatchRunning() { window.batchSimulationState = TRMSim_WSN.BatchSimulationState.RUNNING; }
    public void markBatchPaused() { window.batchSimulationState = TRMSim_WSN.BatchSimulationState.PAUSED; }
    public void resetBatchSimulationState() { window.resetBatchSimulationState(); }
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
    public JCheckBox getGraphInspectorShowIdsCheckBox() { return window.graphInspectorShowIdsCheckBox; }
    public JCheckBox getGraphInspectorShowLinksCheckBox() { return window.graphInspectorShowLinksCheckBox; }
    public JCheckBox getGraphInspectorShowRangesCheckBox() { return window.graphInspectorShowRangesCheckBox; }
    public JCheckBox getGraphInspectorShowGridCheckBox() { return window.graphInspectorShowGridCheckBox; }
    public JSlider getGraphInspectorDelaySlider() { return window.graphInspectorDelaySlider; }
    public Controller getController() { return TRMSim_WSN.C; }
    public SimulationApplicationService getSimulationService() { return window.simulationService; }
    public long getSelectedDelayMillis() { return window.getSelectedDelayMillis(); }
    public SimulationConfig buildSimulationConfig() { return window.buildSimulationConfig(); }
    public NetworkGenerationConfig buildNetworkGenerationConfig() { return window.buildNetworkGenerationConfig(); }
    public BatchSimulationConfig buildBatchSimulationConfig() { return window.buildBatchSimulationConfig(); }
    public void prepareEditableParametersForExecution() throws Exception { window.prepareEditableParametersForExecution(); }
    public void setTrmParameters() throws Exception { window.set_TRMParameters(); }
    public void setTrmParametersPanel(TRMParametersPanel panel) { window.set_TRMParametersPanel(panel); }
    public void applyCurrentParameters() throws Exception { window.set_TRMParameters(); }
    public void setVisualizationDelay(long delayMillis) { window.simulationService.setVisualizationDelay(delayMillis); }
    public void setMessagesText(String text) { window.messagesTextArea.setText(text); }
    public void prependMessage(String text) { window.messagesTextArea.setText(text + window.messagesTextArea.getText()); }
    public void setSimulationComponentsEnabled(boolean enable) { window.simulationComponentsEnabling(enable); }
    public void setClientsProbabilityControlsEnabled(boolean enabled) { MainWindowRuntimeSupport.setClientsProbabilityControlsEnabled(window, enabled); }
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
    public void paintNetwork(Network network, Service requiredService) throws Exception { window.paintNetwork(network, requiredService); }
    public NetworkRenderSupport.RenderState getCurrentRenderState() { return MainWindowRuntimeSupport.getCurrentRenderState(window); }
    public void renderOnFullscreen(Network network, Service requiredService, NetworkRenderSupport.RenderState renderState) {
        if (window.graphWorkspace != null) {
            window.graphWorkspace.renderOnFullscreen(
                    network,
                    requiredService,
                    renderState.getRadioRange(),
                    renderState.isShowRanges(),
                    renderState.isShowLinks(),
                    renderState.isShowIds(),
                    renderState.isShowGrid());
        }
    }
    public void showError(Exception ex) {
        javax.swing.JOptionPane.showMessageDialog(window, ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
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
    public JLayeredPane getNetworkOverlayPane() { return window.networkOverlayPane; }
    public void setNetworkOverlayPane(JLayeredPane value) { window.networkOverlayPane = value; }
    public JPanel getNetworkPanelContainer() { return window.networkPanelContainer; }
    public JPanel getGraphNodeInspectorPanel() { return window.graphNodeInspectorPanel; }
    public int getGraphInspectorCurrentWidth() { return window.graphNodeInspectorCurrentWidth; }
    public int getGraphInspectorMargin() { return window.GRAPH_INSPECTOR_MARGIN; }
    public int getGraphInspectorCollapsedWidth() { return window.GRAPH_INSPECTOR_COLLAPSED_WIDTH; }
    public int getGraphInspectorMinHeight() { return window.GRAPH_INSPECTOR_MIN_HEIGHT; }
    public int getGraphInspectorMaxHeight() { return window.GRAPH_INSPECTOR_MAX_HEIGHT; }
    public SimulationEventHelper.EventHost getSimulationEventHost() { return MainWindowSimulationEventHostFactory.create(this); }
    public Collection<OutcomesPanel> getOutcomesPanels() { return window.outcomesPanels; }
    public void setOutcomesPanels(Collection<OutcomesPanel> panels) { window.outcomesPanels = panels; }
    public void logRenderWarning(String message, Exception ex) { window.LOGGER.log(java.util.logging.Level.WARNING, message, ex); }
    public void finishSimulationUi() { window.finishSimulationUi(); }
    public void handleSimulationFailure(Exception exception) { window.handleSimulationFailure(exception); }
    public void sleepAfterUiUpdate() { TRMSim_WSN.C.sleep(); }
    public String getSelectedTrustModelName() { return MainWindowRuntimeSupport.getSelectedTrustModelName(window); }
    public JTextArea getMessagesTextArea() { return window.messagesTextArea; }
    public AbstractButton getRunTrmButton() { return window.runTRMButton; }
    public JMenuItem getRunTrmMenuItem() { return window.runTRMmenuItem; }
    public AbstractButton getStopTrmButton() { return window.stopTRMButton; }
    public JMenuItem getStopTrmMenuItem() { return window.stopTRMmenuItem; }
    public AbstractButton getResetWsnButton() { return window.resetWSNButton; }
    public JMenuItem getResetWsnMenuItem() { return window.resetWSNmenuItem; }
    public AbstractButton getSaveWsnButton() { return window.saveWSNButton; }
    public JMenuItem getSaveWsnMenuItem() { return window.saveWSNmenuItem; }
    public AbstractButton getLoadWsnButton() { return window.loadWSNButton; }
    public JMenuItem getLoadWsnMenuItem() { return window.loadWSNmenuItem; }
    public AbstractButton getNewWsnButton() { return window.newWSNButton; }
    public JMenuItem getNewWsnMenuItem() { return window.newWSNmenuItem; }
    public AbstractButton getStopSimulationsButton() { return window.stopSimulationsButton; }
    public JMenuItem getStopSimulationsMenuItem() { return window.stopSimulationsMenuItem; }
    public JLabel getPercentageMaliciousServersLabel() { return window.percentageMaliciousServersLabel; }
    public JLabel getPercentageRelayServersLabel() { return window.percentageRelayServersLabel; }
    public JLabel getRadioRangeLabel() { return window.radioRangeLabel; }
    public JLabel getNumExecutionsLabel() { return window.numExecutionsLabel; }
    public JLabel getNumNetworksLabel() { return window.numNetworksLabel; }
    public JLabel getMinNumSensorsLabel() { return window.minNumSensorsLabel; }
    public JLabel getMaxNumSensorsLabel() { return window.maxNumSensorsLabel; }
    public JLabel getTrModelLabel() { return window.TRModelLabel; }
    public JComboBox getTrModelComboBox() { return window.TRModelComboBox; }
    public JMenu getTrModelMenu() { return window.TRModelMenu; }
    public JLabel getPercentageClientsLabel() { return window.percentageClientsLabel; }
    public void enableResetRunSaveAfterModelSwitch() {
        window.resetWSNButton.setEnabled(true);
        window.resetWSNmenuItem.setEnabled(true);
        window.runTRMButton.setEnabled(true);
        window.runTRMmenuItem.setEnabled(true);
        window.saveWSNButton.setEnabled(true);
        window.saveWSNmenuItem.setEnabled(true);
    }
    public void setGraphStripComponents(JPanel liveControlsPanel, JLabel simulationStateLabel,
                                        javax.swing.JButton runButton, javax.swing.JButton stopButton,
                                        JTextArea infoArea, CompactLegendPanel dashboardLegendPanel) {
        window.graphTopLiveControlsPanel = liveControlsPanel;
        window.graphStripSimulationStateLabel = simulationStateLabel;
        window.graphStripRunButton = runButton;
        window.graphStripStopButton = stopButton;
        window.graphTopControlsInfoArea = infoArea;
        window.dashboardLegendPanel = dashboardLegendPanel;
    }
    public void hideSensorPropertiesPanel() { window.sensorPropertiesPanel.setVisible(false); }
    public Sensor getSelectedNeighborSensor() {
        Object value = window.neighborsList.getSelectedValue();
        if (value == null) {
            return null;
        }
        return TRMSim_WSN.C.getSensor(Integer.valueOf(String.valueOf(value)));
    }
    public Sensor getSensorAtNetworkPanelPoint(java.awt.event.MouseEvent evt) throws Exception {
        Point point = javax.swing.SwingUtilities.convertPoint(window.networkPanelContainer, evt.getPoint(), window.networkPanel);
        return window.networkPanel.getSensorAtPosition(point.x, point.y);
    }
}
