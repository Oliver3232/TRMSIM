package es.ants.felixgm.trmsim_wsn.gui.parameterpanels;

import es.ants.felixgm.trmsim_wsn.trm.TrustModelRegistry;

/**
 * Central factory for parameter panel instantiation.
 */
public final class TRMParametersPanelFactory {
    private TRMParametersPanelFactory() {
    }

    public static TRMParametersPanel create(String modelName) throws Exception {
        return TrustModelRegistry.get(modelName).createPanel();
    }
}
