package es.ants.felixgm.trmsim_wsn.trm;

/**
 * Reflection-backed bundle describing a trust model instance and its parameter metadata.
 */
public final class TrustModelBundle {
    private final String modelName;
    private final TRModel_WSN model;
    private final TRMParameters parameters;
    private final String defaultParametersFile;

    public TrustModelBundle(String modelName, TRModel_WSN model, TRMParameters parameters, String defaultParametersFile) {
        this.modelName = modelName;
        this.model = model;
        this.parameters = parameters;
        this.defaultParametersFile = defaultParametersFile;
    }

    public String getModelName() {
        return modelName;
    }

    public TRModel_WSN getModel() {
        return model;
    }

    public TRMParameters getParameters() {
        return parameters;
    }

    public String getDefaultParametersFile() {
        return defaultParametersFile;
    }
}
