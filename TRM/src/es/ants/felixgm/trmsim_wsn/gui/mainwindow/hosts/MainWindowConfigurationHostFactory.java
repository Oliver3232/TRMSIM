package es.ants.felixgm.trmsim_wsn.gui.mainwindow.hosts;


import es.ants.felixgm.trmsim_wsn.gui.MainWindowContext;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowConfigurationController;
import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Service;

public final class MainWindowConfigurationHostFactory {
    private MainWindowConfigurationHostFactory() {
    }

    public static MainWindowConfigurationController.Host create(MainWindowContext context) {
        return new MainWindowConfigurationController.Host() {
            public es.ants.felixgm.trmsim_wsn.Controller getController() { return context.getController(); }
            public es.ants.felixgm.trmsim_wsn.app.SimulationApplicationService getSimulationService() { return context.getSimulationService(); }
            public javax.swing.JSlider getDelaySlider() { return context.getDelaySlider(); }
            public javax.swing.JTextField getDelayTextField() { return context.getDelayTextField(); }
            public javax.swing.JSlider getRadioRangeSlider() { return context.getRadioRangeSlider(); }
            public javax.swing.JTextField getRadioRangeTextField() { return context.getRadioRangeTextField(); }
            public javax.swing.JSpinner getMinNumSensorsSpinner() { return context.getMinNumSensorsSpinner(); }
            public javax.swing.JSpinner getMaxNumSensorsSpinner() { return context.getMaxNumSensorsSpinner(); }
            public javax.swing.JSlider getPercentageClientsSlider() { return context.getPercentageClientsSlider(); }
            public javax.swing.JSlider getPercentageRelayServersSlider() { return context.getPercentageRelayServersSlider(); }
            public javax.swing.JSlider getPercentageMaliciousServersSlider() { return context.getPercentageMaliciousServersSlider(); }
            public javax.swing.JCheckBox getDynamicCheckBox() { return context.getDynamicCheckBox(); }
            public javax.swing.JCheckBox getOscillatingCheckBox() { return context.getOscillatingCheckBox(); }
            public javax.swing.JCheckBox getCollusionCheckBox() { return context.getCollusionCheckBox(); }
            public javax.swing.JSpinner getNumExecutionsSpinner() { return context.getNumExecutionsSpinner(); }
            public javax.swing.JSpinner getNumNetworksSpinner() { return context.getNumNetworksSpinner(); }
            public void syncEmbeddedAndFullscreenDisplayControls() { context.syncEmbeddedAndFullscreenDisplayControls(); }
            public void paintNetwork(Network network, Service requiredService) throws Exception { context.paintNetwork(network, requiredService); }
            public void showError(Exception ex) { context.showError(ex); }
        };
    }
}
