package es.ants.felixgm.trmsim_wsn.trm;

import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.BTRM_WSN_ParametersPanel;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.EigenTrust_ParametersPanel;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.LFTM_ParametersPanel;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.PeerTrust_ParametersPanel;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.PowerTrust_ParametersPanel;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.TRMParametersPanel;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.TRIP_ParametersPanel;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.TemplateTRM_ParametersPanel;
import es.ants.felixgm.trmsim_wsn.trm.btrm_wsn.BTRM_WSN;
import es.ants.felixgm.trmsim_wsn.trm.btrm_wsn.BTRM_WSN_Parameters;
import es.ants.felixgm.trmsim_wsn.trm.eigentrust.EigenTrust;
import es.ants.felixgm.trmsim_wsn.trm.eigentrust.EigenTrust_Parameters;
import es.ants.felixgm.trmsim_wsn.trm.lftm.LFTM;
import es.ants.felixgm.trmsim_wsn.trm.lftm.LFTM_Parameters;
import es.ants.felixgm.trmsim_wsn.trm.peertrust.PeerTrust;
import es.ants.felixgm.trmsim_wsn.trm.peertrust.PeerTrust_Parameters;
import es.ants.felixgm.trmsim_wsn.trm.powertrust.PowerTrust;
import es.ants.felixgm.trmsim_wsn.trm.powertrust.PowerTrust_Parameters;
import es.ants.felixgm.trmsim_wsn.trm.templatetrm.TemplateTRM;
import es.ants.felixgm.trmsim_wsn.trm.templatetrm.TemplateTRM_Parameters;
import es.ants.felixgm.trmsim_wsn.trm.trip.TRIP;
import es.ants.felixgm.trmsim_wsn.trm.trip.TRIP_Parameters;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Explicit registry for the trust models supported by this application build.
 */
public final class TrustModelRegistry {
    public interface ParametersFactory {
        TRMParameters create(String parametersFile) throws Exception;

        TRMParameters createDefault() throws Exception;
    }

    public interface ModelFactory {
        TRModel_WSN create(TRMParameters parameters) throws Exception;
    }

    public interface PanelFactory {
        TRMParametersPanel create();
    }

    public static final class Descriptor {
        private final String modelName;
        private final Class<? extends TRModel_WSN> modelClass;
        private final String defaultParametersFile;
        private final ParametersFactory parametersFactory;
        private final ModelFactory modelFactory;
        private final PanelFactory panelFactory;

        Descriptor(
                String modelName,
                Class<? extends TRModel_WSN> modelClass,
                String defaultParametersFile,
                ParametersFactory parametersFactory,
                ModelFactory modelFactory,
                PanelFactory panelFactory) {
            this.modelName = modelName;
            this.modelClass = modelClass;
            this.defaultParametersFile = defaultParametersFile;
            this.parametersFactory = parametersFactory;
            this.modelFactory = modelFactory;
            this.panelFactory = panelFactory;
        }

        public String getModelName() {
            return modelName;
        }

        public Class<? extends TRModel_WSN> getModelClass() {
            return modelClass;
        }

        public String getDefaultParametersFile() {
            return defaultParametersFile;
        }

        public TRMParameters createParameters(String parametersFile) throws Exception {
            return parametersFactory.create(parametersFile);
        }

        public TRMParameters createDefaultParameters() throws Exception {
            return parametersFactory.createDefault();
        }

        public TRModel_WSN createModel(TRMParameters parameters) throws Exception {
            return modelFactory.create(parameters);
        }

        public TRMParametersPanel createPanel() {
            return panelFactory.create();
        }
    }

    private static final Map<String, Descriptor> DESCRIPTORS = createDescriptors();

    private TrustModelRegistry() {
    }

    public static Descriptor get(String modelName) {
        Descriptor descriptor = DESCRIPTORS.get(modelName);
        if (descriptor == null) {
            throw new IllegalArgumentException("Unsupported trust model: " + modelName);
        }
        return descriptor;
    }

    public static Descriptor getByModelClass(Class<? extends TRModel_WSN> modelClass) {
        for (Descriptor descriptor : DESCRIPTORS.values()) {
            if (descriptor.getModelClass().equals(modelClass)) {
                return descriptor;
            }
        }
        throw new IllegalArgumentException("Unsupported trust model class: " + modelClass.getName());
    }

    public static Map<String, Descriptor> all() {
        return Collections.unmodifiableMap(DESCRIPTORS);
    }

    private static Map<String, Descriptor> createDescriptors() {
        Map<String, Descriptor> descriptors = new LinkedHashMap<String, Descriptor>();

        descriptors.put(BTRM_WSN.get_name(), new Descriptor(
                BTRM_WSN.get_name(),
                BTRM_WSN.class,
                BTRM_WSN_Parameters.defaultParametersFileName,
                new ParametersFactory() {
                    public TRMParameters create(String parametersFile) throws Exception {
                        return new BTRM_WSN_Parameters(parametersFile);
                    }

                    public TRMParameters createDefault() {
                        return new BTRM_WSN_Parameters();
                    }
                },
                parameters -> new BTRM_WSN((BTRM_WSN_Parameters) parameters),
                BTRM_WSN_ParametersPanel::new));

        descriptors.put(EigenTrust.get_name(), new Descriptor(
                EigenTrust.get_name(),
                EigenTrust.class,
                EigenTrust_Parameters.defaultParametersFileName,
                new ParametersFactory() {
                    public TRMParameters create(String parametersFile) throws Exception {
                        return new EigenTrust_Parameters(parametersFile);
                    }

                    public TRMParameters createDefault() {
                        return new EigenTrust_Parameters();
                    }
                },
                parameters -> new EigenTrust((EigenTrust_Parameters) parameters),
                EigenTrust_ParametersPanel::new));

        descriptors.put(PeerTrust.get_name(), new Descriptor(
                PeerTrust.get_name(),
                PeerTrust.class,
                PeerTrust_Parameters.defaultParametersFileName,
                new ParametersFactory() {
                    public TRMParameters create(String parametersFile) throws Exception {
                        return new PeerTrust_Parameters(parametersFile);
                    }

                    public TRMParameters createDefault() {
                        return new PeerTrust_Parameters();
                    }
                },
                parameters -> new PeerTrust((PeerTrust_Parameters) parameters),
                PeerTrust_ParametersPanel::new));

        descriptors.put(PowerTrust.get_name(), new Descriptor(
                PowerTrust.get_name(),
                PowerTrust.class,
                PowerTrust_Parameters.defaultParametersFileName,
                new ParametersFactory() {
                    public TRMParameters create(String parametersFile) throws Exception {
                        return new PowerTrust_Parameters(parametersFile);
                    }

                    public TRMParameters createDefault() {
                        return new PowerTrust_Parameters();
                    }
                },
                parameters -> new PowerTrust((PowerTrust_Parameters) parameters),
                PowerTrust_ParametersPanel::new));

        descriptors.put(LFTM.get_name(), new Descriptor(
                LFTM.get_name(),
                LFTM.class,
                LFTM_Parameters.defaultParametersFileName,
                new ParametersFactory() {
                    public TRMParameters create(String parametersFile) throws Exception {
                        return new LFTM_Parameters(parametersFile);
                    }

                    public TRMParameters createDefault() {
                        return new LFTM_Parameters();
                    }
                },
                parameters -> new LFTM((LFTM_Parameters) parameters),
                LFTM_ParametersPanel::new));

        descriptors.put(TRIP.get_name(), new Descriptor(
                TRIP.get_name(),
                TRIP.class,
                TRIP_Parameters.defaultParametersFileName,
                new ParametersFactory() {
                    public TRMParameters create(String parametersFile) throws Exception {
                        return new TRIP_Parameters(parametersFile);
                    }

                    public TRMParameters createDefault() {
                        return new TRIP_Parameters();
                    }
                },
                parameters -> new TRIP((TRIP_Parameters) parameters),
                TRIP_ParametersPanel::new));

        descriptors.put(TemplateTRM.get_name(), new Descriptor(
                TemplateTRM.get_name(),
                TemplateTRM.class,
                TemplateTRM_Parameters.defaultParametersFileName,
                new ParametersFactory() {
                    public TRMParameters create(String parametersFile) throws Exception {
                        return new TemplateTRM_Parameters(parametersFile);
                    }

                    public TRMParameters createDefault() {
                        return new TemplateTRM_Parameters();
                    }
                },
                parameters -> new TemplateTRM((TemplateTRM_Parameters) parameters),
                TemplateTRM_ParametersPanel::new));

        return descriptors;
    }
}
