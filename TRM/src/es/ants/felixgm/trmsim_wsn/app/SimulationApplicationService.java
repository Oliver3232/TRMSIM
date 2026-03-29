package es.ants.felixgm.trmsim_wsn.app;

import es.ants.felixgm.trmsim_wsn.Controller;
import es.ants.felixgm.trmsim_wsn.SimulationListener;
import es.ants.felixgm.trmsim_wsn.SimulationSlot;
import es.ants.felixgm.trmsim_wsn.network.Network;

/**
 * Small application-layer facade used by the UI to build and execute simulations
 * without talking directly to low-level controller methods.
 */
public final class SimulationApplicationService {
    private final Controller controller;

    public SimulationApplicationService(Controller controller) {
        this.controller = controller;
    }

    public void setVisualizationDelay(long delayMillis) {
        controller.set_delay(delayMillis);
    }

    public Network createRandomNetwork(NetworkGenerationConfig config) {
        return controller.createNewNetwork(config);
    }

    public Network createRandomNetwork(SimulationSlot slot, NetworkGenerationConfig config) {
        return controller.createNewNetwork(slot, config);
    }

    public Network loadNetwork(String filePath) throws Exception {
        return controller.loadCurrentNetwork(filePath);
    }

    public Network loadNetwork(SimulationSlot slot, String filePath) throws Exception {
        return controller.loadCurrentNetwork(slot, filePath);
    }

    public void saveCurrentNetwork(String filePath) throws Exception {
        controller.saveCurrentNetwork(filePath);
    }

    public void saveCurrentNetwork(SimulationSlot slot, String filePath) throws Exception {
        controller.saveCurrentNetwork(slot, filePath);
    }

    public void resetCurrentNetwork() {
        controller.resetCurrentNetwork();
    }

    public void resetCurrentNetwork(SimulationSlot slot) {
        controller.resetCurrentNetwork(slot);
    }

    public Network rebuildNeighbors(double radioRange) {
        return controller.setNewNeighborsNetwork(radioRange);
    }

    public Network rebuildNeighbors(SimulationSlot slot, double radioRange) {
        return controller.setNewNeighborsNetwork(slot, radioRange);
    }

    public void runSimulation(SimulationListener listener, SimulationConfig config) {
        controller.runTRM_WSN(listener, config);
    }

    public void runSimulation(SimulationSlot slot, SimulationListener listener, SimulationConfig config) {
        controller.runTRM_WSN(slot, listener, config);
    }

    public void runBatchSimulation(SimulationListener listener, BatchSimulationConfig config) {
        controller.runSimulations(listener, config);
    }

    public void runBatchSimulation(SimulationSlot slot, SimulationListener listener, BatchSimulationConfig config) {
        controller.runSimulations(slot, listener, config);
    }
}
