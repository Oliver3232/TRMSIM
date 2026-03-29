package es.ants.felixgm.trmsim_wsn.gui.mainwindow.hosts;


import es.ants.felixgm.trmsim_wsn.gui.MainWindowContext;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowUiStateController;

public final class MainWindowUiStateHostFactory {
    private MainWindowUiStateHostFactory() {
    }

    public static MainWindowUiStateController.Host create(MainWindowContext context) {
        return new MainWindowUiStateController.Host() {
            public javax.swing.AbstractButton getRunTrmButton() { return context.getRunTrmButton(); }
            public javax.swing.JMenuItem getRunTrmMenuItem() { return context.getRunTrmMenuItem(); }
            public javax.swing.AbstractButton getResetWsnButton() { return context.getResetWsnButton(); }
            public javax.swing.JMenuItem getResetWsnMenuItem() { return context.getResetWsnMenuItem(); }
            public javax.swing.AbstractButton getSaveWsnButton() { return context.getSaveWsnButton(); }
            public javax.swing.JMenuItem getSaveWsnMenuItem() { return context.getSaveWsnMenuItem(); }
            public javax.swing.AbstractButton getSaveScenarioButton() { return context.getSaveScenarioButton(); }
            public javax.swing.AbstractButton getRunSimulationsButton() { return context.getRunSimulationsButton(); }
            public javax.swing.JMenuItem getRunSimulationsMenuItem() { return context.getRunSimulationsMenuItem(); }
            public javax.swing.AbstractButton getImportScenarioButton() { return context.getImportScenarioButton(); }
            public javax.swing.AbstractButton getLoadScenarioButton() { return context.getLoadScenarioButton(); }
            public javax.swing.AbstractButton getLoadWsnButton() { return context.getLoadWsnButton(); }
            public javax.swing.JMenuItem getLoadWsnMenuItem() { return context.getLoadWsnMenuItem(); }
            public javax.swing.AbstractButton getNewWsnButton() { return context.getNewWsnButton(); }
            public javax.swing.JMenuItem getNewWsnMenuItem() { return context.getNewWsnMenuItem(); }
            public javax.swing.AbstractButton getModeSwitchButton() { return context.getModeSwitchButton(); }
            public javax.swing.JSlider getPercentageMaliciousServersSlider() { return context.getPercentageMaliciousServersSlider(); }
            public javax.swing.JLabel getPercentageMaliciousServersLabel() { return context.getPercentageMaliciousServersLabel(); }
            public javax.swing.JSlider getPercentageRelayServersSlider() { return context.getPercentageRelayServersSlider(); }
            public javax.swing.JLabel getPercentageRelayServersLabel() { return context.getPercentageRelayServersLabel(); }
            public javax.swing.JSlider getRadioRangeSlider() { return context.getRadioRangeSlider(); }
            public javax.swing.JLabel getRadioRangeLabel() { return context.getRadioRangeLabel(); }
            public javax.swing.JSpinner getNumExecutionsSpinner() { return context.getNumExecutionsSpinner(); }
            public javax.swing.JLabel getNumExecutionsLabel() { return context.getNumExecutionsLabel(); }
            public javax.swing.JSpinner getNumNetworksSpinner() { return context.getNumNetworksSpinner(); }
            public javax.swing.JLabel getNumNetworksLabel() { return context.getNumNetworksLabel(); }
            public javax.swing.JSpinner getMinNumSensorsSpinner() { return context.getMinNumSensorsSpinner(); }
            public javax.swing.JLabel getMinNumSensorsLabel() { return context.getMinNumSensorsLabel(); }
            public javax.swing.JSpinner getMaxNumSensorsSpinner() { return context.getMaxNumSensorsSpinner(); }
            public javax.swing.JLabel getMaxNumSensorsLabel() { return context.getMaxNumSensorsLabel(); }
            public javax.swing.JCheckBox getDynamicCheckBox() { return context.getDynamicCheckBox(); }
            public javax.swing.JCheckBox getOscillatingCheckBox() { return context.getOscillatingCheckBox(); }
            public javax.swing.JCheckBox getCollusionCheckBox() { return context.getCollusionCheckBox(); }
            public javax.swing.JLabel getTrModelLabel() { return context.getTrModelLabel(); }
            public javax.swing.JComboBox getTrModelComboBox() { return context.getTrModelComboBox(); }
            public javax.swing.JMenu getTrModelMenu() { return context.getTrModelMenu(); }
            public javax.swing.JPanel getSensorPropertiesPanel() { return context.getSensorPropertiesPanel(); }
            public javax.swing.JSlider getPercentageClientsSlider() { return context.getPercentageClientsSlider(); }
            public javax.swing.JLabel getPercentageClientsLabel() { return context.getPercentageClientsLabel(); }
            public String getSelectedTrustModelName() { return context.getSelectedTrustModelName(); }
        };
    }
}
