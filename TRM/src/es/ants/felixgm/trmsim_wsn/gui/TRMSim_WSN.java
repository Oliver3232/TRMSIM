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
import es.ants.felixgm.trmsim_wsn.VerboseSimulationRunner;

import es.ants.felixgm.trmsim_wsn.gui.legendpanels.LegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.OutcomesPanel;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.TRMParametersPanel;

import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Service;

import es.ants.felixgm.trmsim_wsn.outcomes.Outcome;
import es.ants.felixgm.trmsim_wsn.trm.btrm_wsn.BTRM_WSN;
import es.ants.felixgm.trmsim_wsn.trm.lftm.LFTM;
import java.util.Collection;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLayeredPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.util.logging.Logger;
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
            MainWindowInitializationController.initialize(MainWindowHosts.initialization(this));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    void initializeTRModels() { MainWindowTrustModelController.initialize(this); }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() { initComponentsInternal(); }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponentsInternal() {
        MainWindowAssemblyController.configure(this);
    }// </editor-fold>//GEN-END:initComponents

    void TRModelComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_TRModelComboBoxItemStateChanged
        MainWindowTrustModelController.handleSelection(this, evt, LOGGER); }//GEN-LAST:event_TRModelComboBoxItemStateChanged

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

    public void onOutcomesUpdated(Collection<Outcome> outcomes) {
        SimulationEventHelper.onOutcomesUpdated(MainWindowHosts.simulationEvents(this), outcomes);
    }

    public void onMessage(String message) {
        SimulationEventHelper.onMessage(MainWindowHosts.simulationEvents(this), message);
    }

    public void onError(Exception exception) {
        SimulationEventHelper.onError(MainWindowHosts.simulationEvents(this), exception);
    }
    
    void simulationComponentsEnabling(boolean enable) {
        MainWindowUiStateController.setSimulationComponentsEnabled(MainWindowHosts.uiState(this), enable);
    }
    
    void runSimulationsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runSimulationsButtonActionPerformed
        MainWindowActionController.runBatch(this, evt); }//GEN-LAST:event_runSimulationsButtonActionPerformed

    private void showIdsCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_showIdsCheckBoxItemStateChanged
        MainWindowActionController.toggleDisplay(this, showIdsCheckBox, "Ids"); }//GEN-LAST:event_showIdsCheckBoxItemStateChanged

    void set_TRMParameters() throws Exception {
        MainWindowParametersController.setParameters(MainWindowHosts.parameters(this));
    }

    private void runTRMButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runTRMButtonActionPerformed
        MainWindowActionController.runSingle(this, evt); }//GEN-LAST:event_runTRMButtonActionPerformed

    private void resetWSNButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetWSNButtonActionPerformed
        MainWindowActionController.resetCurrentNetwork(this); }//GEN-LAST:event_resetWSNButtonActionPerformed

    private void showLinksCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_showLinksCheckBoxItemStateChanged
        MainWindowActionController.toggleDisplay(this, showLinksCheckBox, "links"); }//GEN-LAST:event_showLinksCheckBoxItemStateChanged

    private void showRangesCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_showRangesCheckBoxItemStateChanged
        MainWindowActionController.toggleDisplay(this, showRangesCheckBox, "ranges"); }//GEN-LAST:event_showRangesCheckBoxItemStateChanged

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
        MainWindowSimulationController.prepareEditableParametersForExecution(this);
    }

    void finishSimulationUi() {
        MainWindowSimulationController.finishSimulationUi(this);
    }

    void handleSimulationFailure(Exception exception) {
        MainWindowSimulationController.handleSimulationFailure(this, exception);
    }

    private void networkPanelContainerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_networkPanelContainerMouseClicked
        MainWindowActionController.selectNodeFromNetworkPanel(this, evt); }//GEN-LAST:event_networkPanelContainerMouseClicked

    void applyChangesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyChangesButtonActionPerformed
    }//GEN-LAST:event_applyChangesButtonActionPerformed

    private void showGridCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_showGridCheckBoxItemStateChanged
        MainWindowActionController.toggleDisplay(this, showGridCheckBox, "grid"); }//GEN-LAST:event_showGridCheckBoxItemStateChanged

    /**
     * This method plots a Wireless Sensor Network
     * @param network Wireless Sensor Network to be plotted
     * @param requiredService Service requested by the clients (needed in order to paint and
     * distinguish benevolent and malicious servers)
     * @throws Exception If any error occurs while plotting a WSN
     */
    protected void paintNetwork(Network network, Service requiredService) throws Exception {
        MainWindowRenderController.paintNetwork(this, network, requiredService);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables

    //new vars
    javax.swing.JButton exportDataButton;
    javax.swing.JMenuItem exportDataMenuItem;

    javax.swing.JScrollPane TRMParametersScrollPane;
    javax.swing.JPanel TRM_ParametersPanelAux;
    javax.swing.JComboBox TRModelComboBox;
    javax.swing.JLabel TRModelLabel;
    javax.swing.JMenu TRModelMenu;
    javax.swing.JMenuItem aboutTRMSim_WSNmenuItem;
    javax.swing.JButton applyChangesButton;
    javax.swing.JButton applyParametersChangesButton;
    javax.swing.JMenuItem applyParametersChangesMenuItem;
    javax.swing.JPanel bottomPanel;
    javax.swing.JPanel bottomParametersContainerPanel;
    javax.swing.JSplitPane bottomParametersSplitPane;
    javax.swing.JButton browseButton;
    javax.swing.JPanel buttonsControlPanel;
    javax.swing.JCheckBox collusionCheckBox;
    javax.swing.JPanel controlsPanel;
    javax.swing.JScrollPane controlsScrollPane;
    javax.swing.JRadioButton customizedParametersRadioButton;
    javax.swing.JLabel delayLabel;
    javax.swing.JSlider delaySlider;
    javax.swing.JTextField delayTextField;
    javax.swing.JPanel displayControlsPanel;
    javax.swing.JCheckBox dynamicWSNsCheckBox;
    javax.swing.JMenu helpMenu;
    javax.swing.JMenuItem helpMenuItem;
    javax.swing.JButton hideSensorPropertiesPanelButton;
    javax.swing.JLabel legendLabel;
    javax.swing.JPanel legendPanelContainer;
    javax.swing.JMenuItem loadParametersMenuItem;
    javax.swing.JButton loadWSNButton;
    javax.swing.JMenuItem loadWSNmenuItem;
    javax.swing.JLabel maxNumSensorsLabel;
    javax.swing.JSpinner maxNumSensorsSpinner;
    javax.swing.JMenuBar menuBar;
    javax.swing.JPanel messagePanel;
    javax.swing.JScrollPane messagesScrollPane;
    javax.swing.JTextArea messagesTextArea;
    javax.swing.JLabel minNumSensorsLabel;
    javax.swing.JSpinner minNumSensorsSpinner;
    javax.swing.JLabel neighborsLabel;
    javax.swing.JList neighborsList;
    javax.swing.JScrollPane neighborsScrollPane;
    javax.swing.JPanel networkAndSensorPropertiesContainerPanel;
    javax.swing.JPanel networkPanelContainer;
    javax.swing.JButton newWSNButton;
    javax.swing.JMenuItem newWSNmenuItem;
    javax.swing.JLabel numExecutionsLabel;
    javax.swing.JSpinner numExecutionsSpinner;
    javax.swing.JLabel numNetworksLabel;
    javax.swing.JSpinner numNetworksSpinner;
    javax.swing.JCheckBox oscillatingWSNsCheckBox;
    javax.swing.JPanel outcomesPanelsPanel;
    javax.swing.JTabbedPane outcomesTabbedPane;
    javax.swing.JScrollPane parametersFileContentScrollPane;
    javax.swing.JTextArea parametersFileContentTextArea;
    javax.swing.JLabel parametersFileLabel;
    javax.swing.JRadioButton parametersFileRadioButton;
    javax.swing.JTextField parametersFileTextField;
    javax.swing.JMenu parametersMenu;
    javax.swing.JPanel parametersPanel;
    javax.swing.JPanel parametersSettingsPanel;
    javax.swing.ButtonGroup parametersSourceButtonGroup;
    javax.swing.JLabel parametersSourceLabel;
    javax.swing.JLabel percentageClientsLabel;
    javax.swing.JSlider percentageClientsSlider;
    javax.swing.JTextField percentageClientsTextField;
    javax.swing.JLabel percentageMaliciousServersLabel;
    javax.swing.JSlider percentageMaliciousServersSlider;
    javax.swing.JTextField percentageMaliciousServersTextField;
    javax.swing.JLabel percentageRelayServersLabel;
    javax.swing.JSlider percentageRelayServersSlider;
    javax.swing.JTextField percentageRelayServersTextField;
    javax.swing.JLabel radioRangeLabel;
    javax.swing.JLabel radioRangePropertyLabel;
    javax.swing.JSlider radioRangeSlider;
    javax.swing.JSpinner radioRangeSpinner;
    javax.swing.JTextField radioRangeTextField;
    javax.swing.JButton resetWSNButton;
    javax.swing.JMenuItem resetWSNmenuItem;
    javax.swing.JButton runSimulationsButton;
    javax.swing.JMenuItem runSimulationsMenuItem;
    javax.swing.JButton runTRMButton;
    javax.swing.JMenuItem runTRMmenuItem;
    javax.swing.JButton saveParametersFileContentButton;
    javax.swing.JMenuItem saveParametersMenuItem;
    javax.swing.JButton saveWSNButton;
    javax.swing.JMenuItem saveWSNmenuItem;
    javax.swing.JLabel sensorIdLabel;
    javax.swing.JTextField sensorIdTextField;
    javax.swing.JPanel sensorPropertiesPanel;
    javax.swing.JComboBox sensorTypeComboBox;
    javax.swing.JLabel sensorTypeLabel;
    javax.swing.JSeparator separator1;
    javax.swing.JSeparator separator2;
    javax.swing.JSeparator separator3;
    javax.swing.JSeparator separator4;
    javax.swing.JCheckBox showGridCheckBox;
    javax.swing.JCheckBox showIdsCheckBox;
    javax.swing.JCheckBox showLinksCheckBox;
    javax.swing.JCheckBox showRangesCheckBox;
    javax.swing.JMenu simulationsMenu;
    javax.swing.JPanel simulationsPanel;
    javax.swing.JSplitPane simulationsSplitPane;
    javax.swing.JPanel slidersControlsPanel;
    javax.swing.JPanel spinnersControlPanel;
    javax.swing.JButton stopSimulationsButton;
    javax.swing.JMenuItem stopSimulationsMenuItem;
    javax.swing.JButton stopTRMButton;
    javax.swing.JMenuItem stopTRMmenuItem;
    javax.swing.JTabbedPane tabbedPane;
    javax.swing.JPanel threatsControlsPanel;
    javax.swing.JPanel upperPanel;
    javax.swing.JSplitPane upperSplitPane;
    javax.swing.JMenu wsnMenu;
    javax.swing.JLabel xCoordinateLabel;
    javax.swing.JTextField xCoordinateTextField;
    javax.swing.JLabel yCoordinateLabel;
    javax.swing.JTextField yCoordinateTextField;
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

    void updateRunSimulationsControls() { MainWindowSimulationControlsController.updateRunSimulationsControls(MainWindowHosts.simulationControls(this)); }
    void resetBatchSimulationState() { MainWindowSimulationControlsController.resetBatchSimulationState(MainWindowHosts.simulationControls(this)); }
    void syncEmbeddedAndFullscreenDisplayControls() { MainWindowSimulationControlsController.syncEmbeddedAndFullscreenDisplayControls(MainWindowHosts.simulationControls(this)); }
    void handlePauseResumeRequest() { MainWindowSimulationControlsController.handlePauseResumeRequest(MainWindowHosts.simulationControls(this)); }
    void handleStopRequest() { MainWindowSimulationControlsController.handleStopRequest(MainWindowHosts.simulationControls(this)); }

    void setGraphInspectorExpanded(boolean expanded) { MainWindowEmbeddedInspectorController.setGraphInspectorExpanded(this, expanded); }
}
