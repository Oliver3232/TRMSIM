package es.ants.felixgm.trmsim_wsn;

import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.trm.TRModel_WSN;

import java.util.concurrent.Future;

/**
 * Mutable runtime state bound to one logical simulation slot.
 */
public final class SimulationWorkspaceState {
    private final SimulationSlot slot;
    private final SimulationContext simulationContext;
    private Network currentNetwork;
    private String parametersFile;
    private TRModel_WSN trustModel;
    private Simulation simulation;
    private Future<?> simulationFuture;

    public SimulationWorkspaceState(SimulationSlot slot, SimulationContext simulationContext) {
        this.slot = slot;
        this.simulationContext = simulationContext;
    }

    public SimulationSlot getSlot() {
        return slot;
    }

    public SimulationContext getSimulationContext() {
        return simulationContext;
    }

    public Network getCurrentNetwork() {
        return currentNetwork;
    }

    public void setCurrentNetwork(Network currentNetwork) {
        this.currentNetwork = currentNetwork;
    }

    public String getParametersFile() {
        return parametersFile;
    }

    public void setParametersFile(String parametersFile) {
        this.parametersFile = parametersFile;
    }

    public TRModel_WSN getTrustModel() {
        return trustModel;
    }

    public void setTrustModel(TRModel_WSN trustModel) {
        this.trustModel = trustModel;
        simulationContext.setTrustModel(trustModel);
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public Future<?> getSimulationFuture() {
        return simulationFuture;
    }

    public void setSimulationFuture(Future<?> simulationFuture) {
        this.simulationFuture = simulationFuture;
    }
}
