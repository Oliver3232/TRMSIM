package es.ants.felixgm.trmsim_wsn.gui;

import es.ants.felixgm.trmsim_wsn.app.BatchSimulationConfig;
import es.ants.felixgm.trmsim_wsn.app.NetworkGenerationConfig;
import es.ants.felixgm.trmsim_wsn.app.SimulationConfig;
import es.ants.felixgm.trmsim_wsn.network.Network;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

final class MainWindowSimulationController {
    private MainWindowSimulationController() {
    }

    static void stopSingleSimulation(TRMSim_WSN window) throws Exception {
        stopRunningSimulations(window, false, true, false);
    }

    static void stopBatchSimulation(TRMSim_WSN window) throws Exception {
        stopRunningSimulations(window, true, false, true);
    }

    static void loadNetwork(TRMSim_WSN window) throws Exception {
        File selectedFile = NetworkFileHelper.chooseXmlFile(window, "./wsn", "Load WSN", JFileChooser.OPEN_DIALOG);
        if (selectedFile != null) {
            loadSelectedNetwork(window, selectedFile);
        }
    }

    static void saveNetwork(TRMSim_WSN window) throws Exception {
        File selectedFile = NetworkFileHelper.chooseXmlFile(window, "./wsn", "Save WSN", JFileChooser.SAVE_DIALOG);
        if (selectedFile != null) {
            saveCurrentNetwork(window, selectedFile);
        }
    }

    static void runBatch(TRMSim_WSN window) throws Exception {
        if (handleBatchSimulationStateTransition(window)) {
            return;
        }
        startBatchSimulation(window);
    }

    static void runSingle(TRMSim_WSN window) throws Exception {
        startSingleSimulation(window);
    }

    static void resetCurrentNetwork(TRMSim_WSN window) throws Exception {
        window.simulationService.resetCurrentNetwork();
        window.messagesTextArea.setText("Current WSN reset\n" + window.messagesTextArea.getText());
    }

    static void createNewNetwork(TRMSim_WSN window) throws Exception {
        NetworkGenerationConfig networkConfig = window.buildNetworkGenerationConfig();
        window.simulationService.setVisualizationDelay(window.getSelectedDelayMillis());

        Network network = window.simulationService.createRandomNetwork(networkConfig);
        WirelessSensorNetworkHelper.handleNewNetworkCreated(
                network,
                new WirelessSensorNetworkHelper.NetworkUiHost() {
                    public void paintCurrentNetwork(Network currentNetwork) throws Exception {
                        window.paintNetwork(currentNetwork, TRMSim_WSN.C.get_requiredService());
                    }

                    public void clearNodeInspector() {
                        MainWindowNodeInspectorController.clearNodeInspector(MainWindowHosts.nodeInspector(window));
                    }
                },
                window.resetWSNButton,
                window.resetWSNmenuItem,
                window.runTRMButton,
                window.runTRMmenuItem,
                window.saveWSNButton,
                window.saveWSNmenuItem,
                window.sensorPropertiesPanel,
                window.messagesTextArea,
                window.outcomesPanels);
    }

    static void finishSimulationUi(TRMSim_WSN window) {
        SimulationUiHelper.finishSimulationUi(
                window::resetBatchSimulationState,
                () -> window.simulationComponentsEnabling(false),
                window.stopTRMButton,
                window.stopTRMmenuItem,
                window.stopSimulationsButton,
                window.stopSimulationsMenuItem,
                window.messagesTextArea);
    }

    static void handleSimulationFailure(TRMSim_WSN window, Exception exception) {
        SimulationUiHelper.handleSimulationFailure(
                window,
                exception,
                TRMSim_WSN.C,
                window::resetBatchSimulationState,
                () -> window.simulationComponentsEnabling(false),
                window.stopTRMButton,
                window.stopTRMmenuItem,
                window.stopSimulationsButton,
                window.stopSimulationsMenuItem);
    }

    static void prepareEditableParametersForExecution(TRMSim_WSN window) throws Exception {
        if (!MainWindowRuntimeSupport.isParametersFileSelected(window)) {
            window.set_TRMParameters();
        }
    }

    private static void loadSelectedNetwork(TRMSim_WSN window, File selectedFile) throws Exception {
        Network network = NetworkFileHelper.loadSelectedNetwork(window, window.simulationService, selectedFile.getCanonicalPath());
        if (network != null) {
            window.paintNetwork(network, TRMSim_WSN.C.get_requiredService());
            window.saveWSNButton.setEnabled(true);
            window.saveWSNmenuItem.setEnabled(true);
            window.resetWSNButton.setEnabled(true);
            window.resetWSNmenuItem.setEnabled(true);
            window.runTRMButton.setEnabled(true);
            window.runTRMmenuItem.setEnabled(true);
        }
    }

    private static void saveCurrentNetwork(TRMSim_WSN window, File selectedFile) throws Exception {
        NetworkFileHelper.saveCurrentNetwork(window, window.simulationService, selectedFile.getCanonicalPath());
        window.paintNetwork(TRMSim_WSN.C.get_currentNetwork(), TRMSim_WSN.C.get_requiredService());
    }

    private static boolean handleBatchSimulationStateTransition(TRMSim_WSN window) throws Exception {
        if (window.batchSimulationState == TRMSim_WSN.BatchSimulationState.RUNNING) {
            updateBatchSimulationState(window, true, "Simulations paused.\n");
            return true;
        }
        if (window.batchSimulationState == TRMSim_WSN.BatchSimulationState.PAUSED) {
            updateBatchSimulationState(window, false, "Simulations resumed.\n");
            return true;
        }
        return false;
    }

    private static void updateBatchSimulationState(TRMSim_WSN window, boolean pause, String messagePrefix) throws Exception {
        if (pause) {
            TRMSim_WSN.C.pauseSimulation();
            window.batchSimulationState = TRMSim_WSN.BatchSimulationState.PAUSED;
        } else {
            TRMSim_WSN.C.resumeSimulation();
            window.batchSimulationState = TRMSim_WSN.BatchSimulationState.RUNNING;
        }
        window.updateRunSimulationsControls();
        MainWindowNodeInspectorController.refreshSelectedNodeDetails(MainWindowHosts.nodeInspector(window));
        window.messagesTextArea.setText(messagePrefix + window.messagesTextArea.getText());
    }

    private static void startBatchSimulation(TRMSim_WSN window) throws Exception {
        SimulationResultRepository.getInstance().clearRepository();
        BatchSimulationConfig batchConfig = window.buildBatchSimulationConfig();
        window.simulationService.setVisualizationDelay(window.getSelectedDelayMillis());

        window.messagesTextArea.setText("Starting simulations at " + (new java.util.Date()) + "...\n" + window.messagesTextArea.getText());
        window.simulationComponentsEnabling(true);
        window.batchSimulationState = TRMSim_WSN.BatchSimulationState.RUNNING;
        window.updateRunSimulationsControls();
        window.runSimulationsButton.setEnabled(true);
        window.runSimulationsMenuItem.setEnabled(true);
        window.stopSimulationsButton.setEnabled(true);
        window.stopSimulationsMenuItem.setEnabled(true);
        SimulationUiHelper.resetOutcomePanels(window.outcomesPanels);

        window.simulationService.runBatchSimulation(window, batchConfig);
    }

    private static void startSingleSimulation(TRMSim_WSN window) throws Exception {
        SimulationResultRepository.getInstance().clearRepository();
        SimulationConfig simulationConfig = window.buildSimulationConfig();
        window.simulationService.setVisualizationDelay(window.getSelectedDelayMillis());

        window.simulationComponentsEnabling(true);
        window.stopTRMButton.setEnabled(true);
        window.stopTRMmenuItem.setEnabled(true);
        SimulationUiHelper.resetOutcomePanels(window.outcomesPanels);
        window.simulationService.runSimulation(window, simulationConfig);
    }

    private static void stopRunningSimulations(TRMSim_WSN window, boolean resetBatchStateOnStop, boolean disableSingleStopControls, boolean disableBatchStopControls) throws Exception {
        TRMSim_WSN.C.stopSimulations();
        if (resetBatchStateOnStop) {
            window.resetBatchSimulationState();
        }
        window.simulationComponentsEnabling(false);
        if (disableSingleStopControls) {
            window.stopTRMButton.setEnabled(false);
            window.stopTRMmenuItem.setEnabled(false);
        }
        if (disableBatchStopControls) {
            window.stopSimulationsButton.setEnabled(false);
            window.stopSimulationsMenuItem.setEnabled(false);
        }
    }

    static void showError(TRMSim_WSN window, Exception ex) {
        JOptionPane.showMessageDialog(window, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}
