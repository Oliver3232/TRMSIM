package es.ants.felixgm.trmsim_wsn.scenario;

import es.ants.felixgm.trmsim_wsn.app.BatchSimulationConfig;
import es.ants.felixgm.trmsim_wsn.app.NetworkGenerationConfig;

/**
 * Immutable predefined scenario definition loaded from bundled resources.
 */
public final class ScenarioDefinition {
    private final String id;
    private final String displayName;
    private final String description;
    private final NetworkGenerationConfig networkGenerationConfig;
    private final BatchSimulationConfig batchSimulationConfig;
    private final String recommendedTrustModel;

    public ScenarioDefinition(
            String id,
            String displayName,
            String description,
            NetworkGenerationConfig networkGenerationConfig,
            BatchSimulationConfig batchSimulationConfig,
            String recommendedTrustModel) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.networkGenerationConfig = networkGenerationConfig;
        this.batchSimulationConfig = batchSimulationConfig;
        this.recommendedTrustModel = recommendedTrustModel;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public NetworkGenerationConfig getNetworkGenerationConfig() {
        return networkGenerationConfig;
    }

    public BatchSimulationConfig getBatchSimulationConfig() {
        return batchSimulationConfig;
    }

    public String getRecommendedTrustModel() {
        return recommendedTrustModel;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
