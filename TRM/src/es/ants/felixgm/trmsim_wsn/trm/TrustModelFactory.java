package es.ants.felixgm.trmsim_wsn.trm;

/**
 * Centralized factory for trust model and parameter instantiation.
 */
public final class TrustModelFactory {
    private TrustModelFactory() {
    }

    public static TrustModelBundle create(String modelName) throws Exception {
        TrustModelRegistry.Descriptor descriptor = TrustModelRegistry.get(modelName);
        TRMParameters parameters = descriptor.createParameters(descriptor.getDefaultParametersFile());
        TRModel_WSN model = descriptor.createModel(parameters);
        return new TrustModelBundle(modelName, model, parameters, descriptor.getDefaultParametersFile());
    }

    public static TRMParameters instantiateParameters(Class<? extends TRModel_WSN> modelClass, String parametersFile) throws Exception {
        TrustModelRegistry.Descriptor descriptor = TrustModelRegistry.getByModelClass(modelClass);
        try {
            return descriptor.createParameters(parametersFile);
        } catch (Exception ex) {
            return descriptor.createDefaultParameters();
        }
    }

    public static TRModel_WSN instantiateModel(String modelName, TRMParameters parameters) throws Exception {
        return TrustModelRegistry.get(modelName).createModel(parameters);
    }
}
