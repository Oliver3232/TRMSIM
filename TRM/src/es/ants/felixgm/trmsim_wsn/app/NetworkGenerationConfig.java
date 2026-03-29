package es.ants.felixgm.trmsim_wsn.app;

/**
 * Immutable configuration for generating a random network.
 */
public final class NetworkGenerationConfig {
    private final int minNumSensors;
    private final int maxNumSensors;
    private final double probClients;
    private final double probRelay;
    private final double probMalicious;
    private final double radioRange;
    private final boolean dynamic;
    private final boolean oscillating;
    private final boolean collusion;

    public NetworkGenerationConfig(
            int minNumSensors,
            int maxNumSensors,
            double probClients,
            double probRelay,
            double probMalicious,
            double radioRange,
            boolean dynamic,
            boolean oscillating,
            boolean collusion) {
        this.minNumSensors = minNumSensors;
        this.maxNumSensors = maxNumSensors;
        this.probClients = probClients;
        this.probRelay = probRelay;
        this.probMalicious = probMalicious;
        this.radioRange = radioRange;
        this.dynamic = dynamic;
        this.oscillating = oscillating;
        this.collusion = collusion;
    }

    public int getMinNumSensors() {
        return minNumSensors;
    }

    public int getMaxNumSensors() {
        return maxNumSensors;
    }

    public double getProbClients() {
        return probClients;
    }

    public double getProbRelay() {
        return probRelay;
    }

    public double getProbMalicious() {
        return probMalicious;
    }

    public double getRadioRange() {
        return radioRange;
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

    public SimulationConfig toSimulationConfig(int numExecutions) {
        return new SimulationConfig(dynamic, oscillating, collusion, numExecutions);
    }
}
