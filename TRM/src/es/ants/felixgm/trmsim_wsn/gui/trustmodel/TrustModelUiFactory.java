package es.ants.felixgm.trmsim_wsn.gui.trustmodel;


import es.ants.felixgm.trmsim_wsn.gui.legendpanels.EigenTrustLegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.legendpanels.LegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.legendpanels.PowerTrustLegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.legendpanels.TRIPLegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.BTRMFXNetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.EigenTrustFXNetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.JavaFXNetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.LFTMFXNetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.PeerTrustFXNetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.PowerTrustFXNetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.TRIPFXNetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.TemplateTRMFXNetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.AccuracyPanel;
import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.EigenTrustEnergyConsumptionPanel;
import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.EnergyConsumptionPanel;
import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.LFTM_SatisfactionPanel;
import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.OutcomesPanel;
import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.PathLengthPanel;
import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.PowerTrustEnergyConsumptionPanel;
import es.ants.felixgm.trmsim_wsn.trm.TrustModelRegistry;
import es.ants.felixgm.trmsim_wsn.trm.btrm_wsn.BTRM_WSN;
import es.ants.felixgm.trmsim_wsn.trm.bayestrust.BayesTrust;
import es.ants.felixgm.trmsim_wsn.trm.eigentrust.EigenTrust;
import es.ants.felixgm.trmsim_wsn.trm.lftm.LFTM;
import es.ants.felixgm.trmsim_wsn.trm.peertrust.PeerTrust;
import es.ants.felixgm.trmsim_wsn.trm.powertrust.PowerTrust;
import es.ants.felixgm.trmsim_wsn.trm.svmtrust.SVMTrust;
import es.ants.felixgm.trmsim_wsn.trm.templatetrm.TemplateTRM;
import es.ants.felixgm.trmsim_wsn.trm.trip.TRIP;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Explicit UI descriptor registry for supported trust models.
 */
public final class TrustModelUiFactory {
    public interface LegendFactory {
        LegendPanel create();
    }

    public interface NetworkPanelFactory {
        NetworkPanel create();
    }

    public interface OutcomesFactory {
        List<OutcomesPanel> create();
    }

    public static final class Descriptor {
        private final boolean clientsPercentageEnabled;
        private final LegendFactory legendFactory;
        private final NetworkPanelFactory networkPanelFactory;
        private final OutcomesFactory outcomesFactory;

        Descriptor(
                boolean clientsPercentageEnabled,
                LegendFactory legendFactory,
                NetworkPanelFactory networkPanelFactory,
                OutcomesFactory outcomesFactory) {
            this.clientsPercentageEnabled = clientsPercentageEnabled;
            this.legendFactory = legendFactory;
            this.networkPanelFactory = networkPanelFactory;
            this.outcomesFactory = outcomesFactory;
        }

        public boolean isClientsPercentageEnabled() {
            return clientsPercentageEnabled;
        }

        public LegendPanel createLegendPanel() {
            return legendFactory.create();
        }

        public NetworkPanel createNetworkPanel() {
            return networkPanelFactory.create();
        }

        public List<OutcomesPanel> createOutcomesPanels() {
            return outcomesFactory.create();
        }
    }

    private static final Map<String, Descriptor> DESCRIPTORS = createDescriptors();

    private TrustModelUiFactory() {
    }

    public static Descriptor get(String modelName) {
        Descriptor descriptor = DESCRIPTORS.get(modelName);
        if (descriptor == null) {
            throw new IllegalArgumentException("Unsupported trust model UI: " + modelName);
        }
        return descriptor;
    }

    private static Map<String, Descriptor> createDescriptors() {
        Map<String, Descriptor> descriptors = new LinkedHashMap<String, Descriptor>();

        descriptors.put(BTRM_WSN.get_name(), new Descriptor(
                true,
                LegendPanel::new,
                BTRMFXNetworkPanel::new,
                () -> createPanels(new AccuracyPanel(), new PathLengthPanel(), new EnergyConsumptionPanel())));

        descriptors.put(BayesTrust.get_name(), new Descriptor(
                true,
                LegendPanel::new,
                JavaFXNetworkPanel::new,
                () -> createPanels(new AccuracyPanel(), new PathLengthPanel(), new EnergyConsumptionPanel())));

        descriptors.put(EigenTrust.get_name(), new Descriptor(
                false,
                EigenTrustLegendPanel::new,
                EigenTrustFXNetworkPanel::new,
                () -> createPanels(new AccuracyPanel(), new PathLengthPanel(), new EigenTrustEnergyConsumptionPanel())));

        descriptors.put(PeerTrust.get_name(), new Descriptor(
                true,
                LegendPanel::new,
                PeerTrustFXNetworkPanel::new,
                () -> createPanels(new AccuracyPanel(), new PathLengthPanel(), new EnergyConsumptionPanel())));

        descriptors.put(PowerTrust.get_name(), new Descriptor(
                true,
                PowerTrustLegendPanel::new,
                PowerTrustFXNetworkPanel::new,
                () -> createPanels(new AccuracyPanel(), new PathLengthPanel(), new PowerTrustEnergyConsumptionPanel())));

        descriptors.put(SVMTrust.get_name(), new Descriptor(
                true,
                LegendPanel::new,
                JavaFXNetworkPanel::new,
                () -> createPanels(new AccuracyPanel(), new PathLengthPanel(), new EnergyConsumptionPanel())));

        descriptors.put(LFTM.get_name(), new Descriptor(
                true,
                LegendPanel::new,
                LFTMFXNetworkPanel::new,
                () -> createPanels(new AccuracyPanel(), new PathLengthPanel(), new EnergyConsumptionPanel(), new LFTM_SatisfactionPanel())));

        descriptors.put(TRIP.get_name(), new Descriptor(
                false,
                TRIPLegendPanel::new,
                TRIPFXNetworkPanel::new,
                () -> createPanels(new AccuracyPanel(), new PathLengthPanel(), new EnergyConsumptionPanel())));

        descriptors.put(TemplateTRM.get_name(), new Descriptor(
                true,
                LegendPanel::new,
                TemplateTRMFXNetworkPanel::new,
                () -> createPanels(new AccuracyPanel(), new PathLengthPanel())));

        for (String modelName : TrustModelRegistry.all().keySet()) {
            descriptors.putIfAbsent(modelName, new Descriptor(
                    true,
                    LegendPanel::new,
                    JavaFXNetworkPanel::new,
                    () -> createPanels(new AccuracyPanel(), new PathLengthPanel())));
        }

        return descriptors;
    }

    private static List<OutcomesPanel> createPanels(OutcomesPanel... panels) {
        List<OutcomesPanel> list = new ArrayList<OutcomesPanel>();
        for (OutcomesPanel panel : panels) {
            list.add(panel);
        }
        return list;
    }
}
