package es.ants.felixgm.trmsim_wsn.gui;

import es.ants.felixgm.trmsim_wsn.Controller;
import es.ants.felixgm.trmsim_wsn.app.BatchSimulationConfig;
import es.ants.felixgm.trmsim_wsn.app.NetworkGenerationConfig;
import es.ants.felixgm.trmsim_wsn.app.SimulationApplicationService;
import es.ants.felixgm.trmsim_wsn.app.SimulationConfig;
import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Service;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;

final class MainWindowConfigurationController {
    interface Host {
        Controller getController();
        SimulationApplicationService getSimulationService();
        JSlider getDelaySlider();
        JTextField getDelayTextField();
        JSlider getRadioRangeSlider();
        JTextField getRadioRangeTextField();
        JSpinner getMinNumSensorsSpinner();
        JSpinner getMaxNumSensorsSpinner();
        JSlider getPercentageClientsSlider();
        JSlider getPercentageRelayServersSlider();
        JSlider getPercentageMaliciousServersSlider();
        JCheckBox getDynamicCheckBox();
        JCheckBox getOscillatingCheckBox();
        JCheckBox getCollusionCheckBox();
        JSpinner getNumExecutionsSpinner();
        JSpinner getNumNetworksSpinner();
        void syncEmbeddedAndFullscreenDisplayControls();
        void paintNetwork(Network network, Service requiredService) throws Exception;
        void showError(Exception ex);
    }

    private MainWindowConfigurationController() {
    }

    static void onDelayChanged(Host host) {
        UiStateHelper.syncSliderValue(host.getDelaySlider(), host.getDelayTextField());
        host.getController().set_delay(getSelectedDelayMillis(host));
        host.syncEmbeddedAndFullscreenDisplayControls();
    }

    static void onRadioRangeChanged(Host host) {
        try {
            UiStateHelper.syncSliderValue(host.getRadioRangeSlider(), host.getRadioRangeTextField());
            double radioRange = host.getRadioRangeSlider().getValue() / (double) host.getRadioRangeSlider().getMaximum();
            Network network = host.getSimulationService().rebuildNeighbors(radioRange);
            if (network != null) {
                host.paintNetwork(network, host.getController().get_requiredService());
            }
        } catch (Exception ex) {
            host.showError(ex);
        }
    }

    static void alignMinSensors(Host host) {
        UiStateHelper.alignMinMaxSpinners(host.getMinNumSensorsSpinner(), host.getMaxNumSensorsSpinner(), true);
    }

    static void alignMaxSensors(Host host) {
        UiStateHelper.alignMinMaxSpinners(host.getMinNumSensorsSpinner(), host.getMaxNumSensorsSpinner(), false);
    }

    static long getSelectedDelayMillis(Host host) {
        return 1000L * host.getDelaySlider().getValue() / host.getDelaySlider().getMaximum();
    }

    static SimulationConfig buildSimulationConfig(Host host) {
        return new SimulationConfig(
                host.getDynamicCheckBox().isSelected(),
                host.getOscillatingCheckBox().isSelected(),
                host.getCollusionCheckBox().isSelected(),
                (Integer) host.getNumExecutionsSpinner().getValue());
    }

    static NetworkGenerationConfig buildNetworkGenerationConfig(Host host) {
        return new NetworkGenerationConfig(
                (Integer) host.getMinNumSensorsSpinner().getValue(),
                (Integer) host.getMaxNumSensorsSpinner().getValue(),
                host.getPercentageClientsSlider().getValue() / (double) host.getPercentageClientsSlider().getMaximum(),
                host.getPercentageRelayServersSlider().getValue() / (double) host.getPercentageRelayServersSlider().getMaximum(),
                host.getPercentageMaliciousServersSlider().getValue() / (double) host.getPercentageMaliciousServersSlider().getMaximum(),
                host.getRadioRangeSlider().getValue() / (double) host.getRadioRangeSlider().getMaximum(),
                host.getDynamicCheckBox().isSelected(),
                host.getOscillatingCheckBox().isSelected(),
                host.getCollusionCheckBox().isSelected());
    }

    static BatchSimulationConfig buildBatchSimulationConfig(Host host) {
        return new BatchSimulationConfig(
                buildNetworkGenerationConfig(host),
                (Integer) host.getNumNetworksSpinner().getValue(),
                (Integer) host.getNumExecutionsSpinner().getValue());
    }
}
