package es.ants.felixgm.trmsim_wsn.app;

/**
 * Immutable configuration for batch simulation runs over generated networks.
 */
public final class BatchSimulationConfig {
    private final NetworkGenerationConfig networkGenerationConfig;
    private final int numNetworks;
    private final int numExecutions;

    public BatchSimulationConfig(NetworkGenerationConfig networkGenerationConfig, int numNetworks, int numExecutions) {
        this.networkGenerationConfig = networkGenerationConfig;
        this.numNetworks = numNetworks;
        this.numExecutions = numExecutions;
    }

    public NetworkGenerationConfig getNetworkGenerationConfig() {
        return networkGenerationConfig;
    }

    public int getNumNetworks() {
        return numNetworks;
    }

    public int getNumExecutions() {
        return numExecutions;
    }
}
