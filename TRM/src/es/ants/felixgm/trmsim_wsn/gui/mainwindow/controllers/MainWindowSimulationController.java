package es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers;


import es.ants.felixgm.trmsim_wsn.gui.MainWindowContext;
import es.ants.felixgm.trmsim_wsn.gui.export.SimulationResultRepository;
import es.ants.felixgm.trmsim_wsn.gui.network.NetworkFileHelper;
import es.ants.felixgm.trmsim_wsn.gui.network.WirelessSensorNetworkHelper;
import es.ants.felixgm.trmsim_wsn.gui.support.SimulationUiHelper;
import es.ants.felixgm.trmsim_wsn.network.Network;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public final class MainWindowSimulationController {
    private MainWindowSimulationController() {
    }

    public static void stopSingleSimulation(MainWindowContext context) throws Exception {
        stopRunningSimulations(context, false, true, false);
    }

    public static void stopBatchSimulation(MainWindowContext context) throws Exception {
        stopRunningSimulations(context, true, false, true);
    }

    public static void loadNetwork(MainWindowContext context) throws Exception {
        File selectedFile = NetworkFileHelper.chooseXmlFile(context.window(), "./wsn", "Load WSN", JFileChooser.OPEN_DIALOG);
        if (selectedFile != null) {
            loadSelectedNetwork(context, selectedFile);
        }
    }

    public static void saveNetwork(MainWindowContext context) throws Exception {
        File selectedFile = NetworkFileHelper.chooseXmlFile(context.window(), "./wsn", "Save WSN", JFileChooser.SAVE_DIALOG);
        if (selectedFile != null) {
            saveCurrentNetwork(context, selectedFile);
        }
    }

    public static void runBatch(MainWindowContext context) throws Exception {
        if (handleBatchSimulationStateTransition(context)) {
            return;
        }
        startBatchSimulation(context);
    }

    public static void runSingle(MainWindowContext context) throws Exception {
        startSingleSimulation(context);
    }

    public static void resetCurrentNetwork(MainWindowContext context) throws Exception {
        context.getSimulationService().resetCurrentNetwork();
        context.prependMessage("Current WSN reset\n");
    }

    public static void createNewNetwork(MainWindowContext context) throws Exception {
        Network network = context.getSimulationService().createRandomNetwork(context.buildNetworkGenerationConfig());
        context.setVisualizationDelay(context.getSelectedDelayMillis());

        WirelessSensorNetworkHelper.handleNewNetworkCreated(
                network,
                new WirelessSensorNetworkHelper.NetworkUiHost() {
                    public void paintCurrentNetwork(Network currentNetwork) throws Exception {
                        context.paintNetwork(currentNetwork, context.getController().get_requiredService());
                    }

                    public void clearNodeInspector() {
                        context.clearNodeInspector();
                    }
                },
                context.getResetWsnButton(),
                context.getResetWsnMenuItem(),
                context.getRunTrmButton(),
                context.getRunTrmMenuItem(),
                context.getSaveWsnButton(),
                context.getSaveWsnMenuItem(),
                context.getSensorPropertiesPanel(),
                context.getMessagesTextArea(),
                context.getOutcomesPanels());
    }

    public static void finishSimulationUi(MainWindowContext context) {
        SimulationUiHelper.finishSimulationUi(
                context::resetBatchSimulationState,
                () -> context.setSimulationComponentsEnabled(false),
                context.getStopTrmButton(),
                context.getStopTrmMenuItem(),
                context.getStopSimulationsButton(),
                context.getStopSimulationsMenuItem(),
                context.getMessagesTextArea());
    }

    public static void handleSimulationFailure(MainWindowContext context, Exception exception) {
        SimulationUiHelper.handleSimulationFailure(
                context.window(),
                exception,
                context.getController(),
                context::resetBatchSimulationState,
                () -> context.setSimulationComponentsEnabled(false),
                context.getStopTrmButton(),
                context.getStopTrmMenuItem(),
                context.getStopSimulationsButton(),
                context.getStopSimulationsMenuItem());
    }

    public static void prepareEditableParametersForExecution(MainWindowContext context) throws Exception {
        if (!context.isParametersFileSelected()) {
            context.setTrmParameters();
        }
    }

    public static void showError(MainWindowContext context, Exception ex) {
        JOptionPane.showMessageDialog(context.window(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }

    private static void loadSelectedNetwork(MainWindowContext context, File selectedFile) throws Exception {
        Network network = NetworkFileHelper.loadSelectedNetwork(context.window(), context.getSimulationService(), selectedFile.getCanonicalPath());
        if (network != null) {
            context.paintNetwork(network, context.getController().get_requiredService());
            context.getSaveWsnButton().setEnabled(true);
            context.getSaveWsnMenuItem().setEnabled(true);
            context.getResetWsnButton().setEnabled(true);
            context.getResetWsnMenuItem().setEnabled(true);
            context.getRunTrmButton().setEnabled(true);
            context.getRunTrmMenuItem().setEnabled(true);
        }
    }

    private static void saveCurrentNetwork(MainWindowContext context, File selectedFile) throws Exception {
        NetworkFileHelper.saveCurrentNetwork(context.window(), context.getSimulationService(), selectedFile.getCanonicalPath());
        context.paintNetwork(context.getController().get_currentNetwork(), context.getController().get_requiredService());
    }

    private static boolean handleBatchSimulationStateTransition(MainWindowContext context) throws Exception {
        if (context.isBatchRunning()) {
            updateBatchSimulationState(context, true, "Simulations paused.\n");
            return true;
        }
        if (context.isBatchPaused()) {
            updateBatchSimulationState(context, false, "Simulations resumed.\n");
            return true;
        }
        return false;
    }

    private static void updateBatchSimulationState(MainWindowContext context, boolean pause, String messagePrefix) throws Exception {
        if (pause) {
            context.getController().pauseSimulation();
            context.markBatchPaused();
        } else {
            context.getController().resumeSimulation();
            context.markBatchRunning();
        }
        context.updateRunSimulationsControls();
        context.refreshSelectedNodeDetails();
        context.prependMessage(messagePrefix);
    }

    private static void startBatchSimulation(MainWindowContext context) throws Exception {
        SimulationResultRepository.getInstance().clearRepository();
        context.setVisualizationDelay(context.getSelectedDelayMillis());

        context.prependMessage("Starting simulations at " + (new java.util.Date()) + "...\n");
        context.setSimulationComponentsEnabled(true);
        context.markBatchRunning();
        context.updateRunSimulationsControls();
        context.getRunSimulationsButton().setEnabled(true);
        context.getRunSimulationsMenuItem().setEnabled(true);
        context.getStopSimulationsButton().setEnabled(true);
        context.getStopSimulationsMenuItem().setEnabled(true);
        SimulationUiHelper.resetOutcomePanels(context.getOutcomesPanels());

        context.getSimulationService().runBatchSimulation(context.window(), context.buildBatchSimulationConfig());
    }

    private static void startSingleSimulation(MainWindowContext context) throws Exception {
        SimulationResultRepository.getInstance().clearRepository();
        context.setVisualizationDelay(context.getSelectedDelayMillis());

        context.setSimulationComponentsEnabled(true);
        context.getStopTrmButton().setEnabled(true);
        context.getStopTrmMenuItem().setEnabled(true);
        SimulationUiHelper.resetOutcomePanels(context.getOutcomesPanels());
        context.getSimulationService().runSimulation(context.window(), context.buildSimulationConfig());
    }

    private static void stopRunningSimulations(MainWindowContext context, boolean resetBatchStateOnStop, boolean disableSingleStopControls, boolean disableBatchStopControls) throws Exception {
        context.getController().stopSimulations();
        if (resetBatchStateOnStop) {
            context.resetBatchSimulationState();
        }
        context.setSimulationComponentsEnabled(false);
        if (disableSingleStopControls) {
            context.getStopTrmButton().setEnabled(false);
            context.getStopTrmMenuItem().setEnabled(false);
        }
        if (disableBatchStopControls) {
            context.getStopSimulationsButton().setEnabled(false);
            context.getStopSimulationsMenuItem().setEnabled(false);
        }
    }
}
