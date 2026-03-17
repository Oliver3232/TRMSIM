package es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers;


import es.ants.felixgm.trmsim_wsn.gui.support.UiStateHelper;
import es.ants.felixgm.trmsim_wsn.trm.eigentrust.EigenTrust;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;

public final class MainWindowUiStateController {
    public interface Host {
        AbstractButton getRunTrmButton();
        javax.swing.JMenuItem getRunTrmMenuItem();
        AbstractButton getResetWsnButton();
        javax.swing.JMenuItem getResetWsnMenuItem();
        AbstractButton getSaveWsnButton();
        javax.swing.JMenuItem getSaveWsnMenuItem();
        AbstractButton getRunSimulationsButton();
        javax.swing.JMenuItem getRunSimulationsMenuItem();
        AbstractButton getLoadWsnButton();
        javax.swing.JMenuItem getLoadWsnMenuItem();
        AbstractButton getNewWsnButton();
        javax.swing.JMenuItem getNewWsnMenuItem();
        JSlider getPercentageMaliciousServersSlider();
        JLabel getPercentageMaliciousServersLabel();
        JSlider getPercentageRelayServersSlider();
        JLabel getPercentageRelayServersLabel();
        JSlider getRadioRangeSlider();
        JLabel getRadioRangeLabel();
        JSpinner getNumExecutionsSpinner();
        JLabel getNumExecutionsLabel();
        JSpinner getNumNetworksSpinner();
        JLabel getNumNetworksLabel();
        JSpinner getMinNumSensorsSpinner();
        JLabel getMinNumSensorsLabel();
        JSpinner getMaxNumSensorsSpinner();
        JLabel getMaxNumSensorsLabel();
        javax.swing.JCheckBox getDynamicCheckBox();
        javax.swing.JCheckBox getOscillatingCheckBox();
        javax.swing.JCheckBox getCollusionCheckBox();
        JLabel getTrModelLabel();
        JComboBox getTrModelComboBox();
        JMenu getTrModelMenu();
        JPanel getSensorPropertiesPanel();
        JSlider getPercentageClientsSlider();
        JLabel getPercentageClientsLabel();
        String getSelectedTrustModelName();
    }

    private MainWindowUiStateController() {
    }

    public static void setSimulationComponentsEnabled(Host host, boolean simulationRunning) {
        boolean enabled = !simulationRunning;
        UiStateHelper.setComponentsEnabled(enabled,
                host.getRunTrmButton(), host.getRunTrmMenuItem(),
                host.getResetWsnButton(), host.getResetWsnMenuItem(),
                host.getSaveWsnButton(), host.getSaveWsnMenuItem(),
                host.getRunSimulationsButton(), host.getRunSimulationsMenuItem(),
                host.getLoadWsnButton(), host.getLoadWsnMenuItem(),
                host.getNewWsnButton(), host.getNewWsnMenuItem(),
                host.getPercentageMaliciousServersSlider(), host.getPercentageMaliciousServersLabel(),
                host.getPercentageRelayServersSlider(), host.getPercentageRelayServersLabel(),
                host.getRadioRangeSlider(), host.getRadioRangeLabel(),
                host.getNumExecutionsSpinner(), host.getNumExecutionsLabel(),
                host.getNumNetworksSpinner(), host.getNumNetworksLabel(),
                host.getMinNumSensorsSpinner(), host.getMinNumSensorsLabel(),
                host.getMaxNumSensorsSpinner(), host.getMaxNumSensorsLabel(),
                host.getDynamicCheckBox(), host.getOscillatingCheckBox(), host.getCollusionCheckBox(),
                host.getTrModelLabel(), host.getTrModelComboBox(), host.getTrModelMenu());
        host.getSensorPropertiesPanel().setVisible(false);

        if (!EigenTrust.get_name().equals(host.getSelectedTrustModelName())) {
            UiStateHelper.setComponentsEnabled(enabled,
                    host.getPercentageClientsSlider(),
                    host.getPercentageClientsLabel());
        }
    }
}
