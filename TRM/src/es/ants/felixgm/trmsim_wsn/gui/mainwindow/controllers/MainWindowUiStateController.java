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
        AbstractButton getSaveScenarioButton();
        AbstractButton getRunSimulationsButton();
        javax.swing.JMenuItem getRunSimulationsMenuItem();
        AbstractButton getImportScenarioButton();
        AbstractButton getLoadScenarioButton();
        AbstractButton getLoadWsnButton();
        javax.swing.JMenuItem getLoadWsnMenuItem();
        AbstractButton getNewWsnButton();
        javax.swing.JMenuItem getNewWsnMenuItem();
        AbstractButton getModeSwitchButton();
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
                host.getResetWsnButton(), host.getResetWsnMenuItem(),
                host.getSaveWsnButton(), host.getSaveWsnMenuItem(),
                host.getSaveScenarioButton(),
                host.getRunSimulationsButton(), host.getRunSimulationsMenuItem(),
                host.getImportScenarioButton(),
                host.getLoadScenarioButton(),
                host.getLoadWsnButton(), host.getLoadWsnMenuItem(),
                host.getNewWsnButton(), host.getNewWsnMenuItem(),
                host.getModeSwitchButton(),
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

        host.getRunTrmButton().setText(simulationRunning ? "Stop T&R Model" : "Run T&R Model");
        host.getRunTrmMenuItem().setText(simulationRunning ? "Stop T&R Model" : "Run T&R Model");
        if (simulationRunning) {
            host.getRunTrmButton().setEnabled(true);
            host.getRunTrmMenuItem().setEnabled(true);
        }

        if (!EigenTrust.get_name().equals(host.getSelectedTrustModelName())) {
            UiStateHelper.setComponentsEnabled(enabled,
                    host.getPercentageClientsSlider(),
                    host.getPercentageClientsLabel());
        }
    }
}
