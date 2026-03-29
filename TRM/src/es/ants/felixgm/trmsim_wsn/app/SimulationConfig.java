package es.ants.felixgm.trmsim_wsn.app;

/**
 * Immutable runtime configuration for executing a simulation over an existing network.
 */
public final class SimulationConfig {
    private final boolean dynamic;
    private final boolean oscillating;
    private final boolean collusion;
    private final int numExecutions;

    public SimulationConfig(boolean dynamic, boolean oscillating, boolean collusion, int numExecutions) {
        this.dynamic = dynamic;
        this.oscillating = oscillating;
        this.collusion = collusion;
        this.numExecutions = numExecutions;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public boolean isOscillating() {
        return oscillating;
    }

    public boolean isCollusion() {
        return collusion;
    }

    public int getNumExecutions() {
        return numExecutions;
    }
}
