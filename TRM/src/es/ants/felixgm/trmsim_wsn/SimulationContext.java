package es.ants.felixgm.trmsim_wsn;

import es.ants.felixgm.trmsim_wsn.trm.TRModel_WSN;

/**
 * Central runtime state for the active simulation configuration.
 * This is the transition point away from scattered global flags.
 */
public final class SimulationContext {
    private boolean collusion;
    private boolean dynamic;
    private boolean runningSimulation;
    private TRModel_WSN trustModel;

    public boolean isCollusion() {
        return collusion;
    }

    public void setCollusion(boolean collusion) {
        this.collusion = collusion;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public boolean isRunningSimulation() {
        return runningSimulation;
    }

    public void setRunningSimulation(boolean runningSimulation) {
        this.runningSimulation = runningSimulation;
    }

    public TRModel_WSN getTrustModel() {
        return trustModel;
    }

    public void setTrustModel(TRModel_WSN trustModel) {
        this.trustModel = trustModel;
    }
}
