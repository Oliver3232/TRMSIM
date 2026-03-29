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
import es.ants.felixgm.trmsim_wsn.gui.support.MessageConsoleHelper;
import es.ants.felixgm.trmsim_wsn.gui.events.SimulationEventHelper;
import es.ants.felixgm.trmsim_wsn.gui.graph.SimulationGraphWorkspace;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowActionController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowConfigurationController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowInitializationController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowRenderController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowSimulationController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowTrustModelController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowParametersController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowSimulationControlsController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowUiStateController;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.OutcomesPanel;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.TRMParametersPanel;

import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Service;

import es.ants.felixgm.trmsim_wsn.outcomes.Outcome;
import es.ants.felixgm.trmsim_wsn.trm.btrm_wsn.BTRM_WSN;
import es.ants.felixgm.trmsim_wsn.trm.eigentrust.EigenTrust;
import es.ants.felixgm.trmsim_wsn.trm.lftm.LFTM;
import es.ants.felixgm.trmsim_wsn.trm.peertrust.PeerTrust;
import es.ants.felixgm.trmsim_wsn.trm.powertrust.PowerTrust;
import es.ants.felixgm.trmsim_wsn.trm.trip.TRIP;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.awt.Component;
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
    AppMode appMode = AppMode.SINGLE;
    DualSimulationShellPanel dualSimulationShellPanel;
    Component singleModeNorthComponent;
    Component singleModeWestComponent;
    Component singleModeCenterComponent;
    javax.swing.Timer dualNetworkRefreshTimer;
    boolean singleSimulationStartPending = false;
    boolean dualSessionStartPending = false;
    final Map<SimulationSlot, Boolean> dualSlotTrmActive =
            new EnumMap<SimulationSlot, Boolean>(SimulationSlot.class);
    final Map<SimulationSlot, Boolean> dualSlotBatchActive =
            new EnumMap<SimulationSlot, Boolean>(SimulationSlot.class);
    final Map<SimulationSlot, Boolean> dualSlotStartPending =
            new EnumMap<SimulationSlot, Boolean>(SimulationSlot.class);
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
    final Map<SimulationSlot, Boolean> dualShowIds =
            new EnumMap<SimulationSlot, Boolean>(SimulationSlot.class);
    final Map<SimulationSlot, Boolean> dualShowLinks =
            new EnumMap<SimulationSlot, Boolean>(SimulationSlot.class);
    final Map<SimulationSlot, Boolean> dualShowRanges =
            new EnumMap<SimulationSlot, Boolean>(SimulationSlot.class);
    final Map<SimulationSlot, Boolean> dualShowGrid =
            new EnumMap<SimulationSlot, Boolean>(SimulationSlot.class);
    final Map<SimulationSlot, Integer> dualDelayValues =
            new EnumMap<SimulationSlot, Integer>(SimulationSlot.class);
    final Map<SimulationSlot, Boolean> dualScenarioSelectionSync =
            new EnumMap<SimulationSlot, Boolean>(SimulationSlot.class);
    DualModeCoordinator dualModeCoordinator;
    boolean singleScenarioSelectionSync = false;

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
            dualModeCoordinator = new DualModeCoordinator(this);
            initializeDualModeShell();
            MainWindowInitializationController.initialize(MainWindowHosts.initialization(this));
            syncDualShellFromControllerState();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    void initializeTRModels() { MainWindowTrustModelController.initialize(new MainWindowContext(this)); }

    void initializeDualModeShell() { dualModeCoordinator.initializeDualModeShell(); }

    void initializeDualControllers() {
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
            initializeDualRenderState(SimulationSlot.PRIMARY);
            initializeDualRenderState(SimulationSlot.SECONDARY);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to initialize dual simulation controllers", ex);
        }
    }

    void initializeDualRenderState(SimulationSlot slot) {
        dualShowIds.put(slot, Boolean.valueOf(showIdsCheckBox != null && showIdsCheckBox.isSelected()));
        dualShowLinks.put(slot, Boolean.valueOf(showLinksCheckBox != null && showLinksCheckBox.isSelected()));
        dualShowRanges.put(slot, Boolean.valueOf(showRangesCheckBox != null && showRangesCheckBox.isSelected()));
        dualShowGrid.put(slot, Boolean.valueOf(showGridCheckBox != null && showGridCheckBox.isSelected()));
        dualDelayValues.put(slot, Integer.valueOf(delaySlider != null ? delaySlider.getValue() : 0));
        dualScenarioSelectionSync.put(slot, Boolean.FALSE);
    }

    boolean dualShowIds(SimulationSlot slot) {
        return Boolean.TRUE.equals(dualShowIds.get(slot));
    }

    boolean dualShowLinks(SimulationSlot slot) {
        return Boolean.TRUE.equals(dualShowLinks.get(slot));
    }

    boolean dualShowRanges(SimulationSlot slot) {
        return Boolean.TRUE.equals(dualShowRanges.get(slot));
    }

    boolean dualShowGrid(SimulationSlot slot) {
        return Boolean.TRUE.equals(dualShowGrid.get(slot));
    }

    int dualDelayValue(SimulationSlot slot) {
        Integer value = dualDelayValues.get(slot);
        return value != null ? value.intValue() : delaySlider.getValue();
    }

    void setDualShowIds(SimulationSlot slot, boolean selected) {
        dualShowIds.put(slot, Boolean.valueOf(selected));
    }

    void setDualShowLinks(SimulationSlot slot, boolean selected) {
        dualShowLinks.put(slot, Boolean.valueOf(selected));
    }

    void setDualShowRanges(SimulationSlot slot, boolean selected) {
        dualShowRanges.put(slot, Boolean.valueOf(selected));
    }

    void setDualShowGrid(SimulationSlot slot, boolean selected) {
        dualShowGrid.put(slot, Boolean.valueOf(selected));
    }

    void setDualDelayValue(SimulationSlot slot, int value) {
        dualDelayValues.put(slot, Integer.valueOf(value));
        SimulationApplicationService service = dualSimulationService(slot);
        if (service != null) {
            service.setVisualizationDelay(getDualSelectedDelayMillis(slot));
        }
    }

    long getDualSelectedDelayMillis(SimulationSlot slot) {
        return 1000L * dualDelayValue(slot) / delaySlider.getMaximum();
    }

    Controller dualController(SimulationSlot slot) {
        return dualControllers.get(slot);
    }

    SimulationApplicationService dualSimulationService(SimulationSlot slot) {
        return dualSimulationServices.get(slot);
    }

    boolean isAnyDualSimulationRunning() {
        for (SimulationSlot slot : SimulationSlot.values()) {
            if (isDualSlotSimulationActive(slot)) {
                return true;
            }
        }
        return false;
    }

    boolean isSingleSimulationActive() {
        return singleSimulationStartPending || ((C != null) && C.isSimulationRunning());
    }

    boolean isDualSlotSimulationActive(SimulationSlot slot) {
        return isDualSlotTrmActive(slot) || isDualBatchActive(slot) || Boolean.TRUE.equals(dualSlotStartPending.get(slot));
    }

    boolean isDualSlotTrmActive(SimulationSlot slot) {
        return Boolean.TRUE.equals(dualSlotTrmActive.get(slot));
    }

    boolean isDualBatchActive(SimulationSlot slot) {
        return Boolean.TRUE.equals(dualSlotBatchActive.get(slot));
    }

    boolean isAnyDualSlotTrmActive() {
        for (SimulationSlot slot : SimulationSlot.values()) {
            if (isDualSlotTrmActive(slot)) {
                return true;
            }
        }
        return false;
    }

    boolean isAnyDualBatchSimulationActive() {
        if (dualSessionStartPending) {
            return true;
        }
        for (SimulationSlot slot : SimulationSlot.values()) {
            if (isDualBatchActive(slot)) {
                return true;
            }
        }
        return false;
    }

    void setSingleSimulationStartPending(boolean pending) {
        singleSimulationStartPending = pending;
    }

    void setDualSlotStartPending(SimulationSlot slot, boolean pending) {
        dualSlotStartPending.put(slot, Boolean.valueOf(pending));
    }

    void setDualSlotTrmActive(SimulationSlot slot, boolean active) {
        dualSlotTrmActive.put(slot, Boolean.valueOf(active));
        if (active) {
            dualSlotBatchActive.put(slot, Boolean.FALSE);
        }
    }

    void setDualSlotBatchActive(SimulationSlot slot, boolean active) {
        dualSlotBatchActive.put(slot, Boolean.valueOf(active));
        if (active) {
            dualSlotTrmActive.put(slot, Boolean.FALSE);
        }
    }

    boolean areAllDualRunningSimulationsPaused() {
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

    DualSimulationShellPanel.SlotToolbarPanel dualToolbarPanel(SimulationSlot slot) {
        return slot == SimulationSlot.PRIMARY
                ? dualSimulationShellPanel.getPrimaryToolbarPanel()
                : dualSimulationShellPanel.getSecondaryToolbarPanel();
    }

    es.ants.felixgm.trmsim_wsn.gui.dual.DualSimulationWorkspacePanel dualWorkspacePanel(SimulationSlot slot) {
        return slot == SimulationSlot.PRIMARY
                ? dualSimulationShellPanel.getPrimaryWorkspacePanel()
                : dualSimulationShellPanel.getSecondaryWorkspacePanel();
    }
    public void switchAppMode(AppMode mode) { dualModeCoordinator.switchAppMode(mode); }
    void syncDualShellFromControllerState() { dualModeCoordinator.syncDualShellFromControllerState(); }
    void handleDualSessionRunPauseResume() { dualModeCoordinator.handleDualSessionRunPauseResume(); }
    void handleDualSessionStop() { dualModeCoordinator.handleDualSessionStop(); }
    void updateDualSessionControls() { dualModeCoordinator.updateDualSessionControls(); }
    void refreshDualNetworksIfNeeded() { dualModeCoordinator.refreshDualNetworksIfNeeded(); }
    SimulationEventHelper.EventHost dualSimulationEventHost(SimulationSlot slot) { return dualModeCoordinator.dualSimulationEventHost(slot); }
    String slotLabel(SimulationSlot slot) { return slot == SimulationSlot.PRIMARY ? "Simulation A" : "Simulation B"; }
    long getEffectiveDualVisualizationDelayMillis(SimulationSlot slot) { long selectedDelay = getDualSelectedDelayMillis(slot); return (selectedDelay > 0L) ? selectedDelay : 50L; }
    void prependDualMessage(SimulationSlot slot, String message) {
        es.ants.felixgm.trmsim_wsn.gui.dual.DualSimulationWorkspacePanel workspacePanel = dualWorkspacePanel(slot);
        MessageConsoleHelper.appendMessage(workspacePanel.getMessagesTextArea(), message);
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
            hideSensorPropertiesPanelButton, importScenarioButton, loadScenarioButton, loadWSNButton, newWSNButton, resetWSNButton, runSimulationsButton,
            runTRMButton, saveParametersFileContentButton, saveWSNButton, stopSimulationsButton, stopTRMButton,
            modeSwitchButton, saveScenarioButton;
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
            radioRangePropertyLabel, sensorIdLabel, sensorTypeLabel, xCoordinateLabel, yCoordinateLabel,
            activeScenarioLabel;
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
    javax.swing.JTextArea messagesTextArea, parametersFileContentTextArea, activeScenarioDescriptionTextArea;
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
}
