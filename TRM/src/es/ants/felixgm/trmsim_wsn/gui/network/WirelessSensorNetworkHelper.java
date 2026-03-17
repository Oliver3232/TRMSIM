package es.ants.felixgm.trmsim_wsn.gui.network;


import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.OutcomesPanel;
import es.ants.felixgm.trmsim_wsn.network.Network;

import javax.swing.AbstractButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.util.Collection;

public final class WirelessSensorNetworkHelper {
    private WirelessSensorNetworkHelper() {
    }

    public static void handleNewNetworkCreated(
            Network network,
            NetworkUiHost host,
            AbstractButton resetWSNButton,
            JMenuItem resetWSNmenuItem,
            AbstractButton runTRMButton,
            JMenuItem runTRMmenuItem,
            AbstractButton saveWSNButton,
            JMenuItem saveWSNmenuItem,
            JPanel sensorPropertiesPanel,
            JTextArea messagesTextArea,
            Collection<OutcomesPanel> outcomesPanels) throws Exception {
        if (network == null) {
            return;
        }
        host.paintCurrentNetwork(network);
        resetWSNButton.setEnabled(true);
        resetWSNmenuItem.setEnabled(true);
        runTRMButton.setEnabled(true);
        runTRMmenuItem.setEnabled(true);
        saveWSNButton.setEnabled(true);
        saveWSNmenuItem.setEnabled(true);
        sensorPropertiesPanel.setVisible(false);
        host.clearNodeInspector();
        messagesTextArea.setText("New WSN created\n" + messagesTextArea.getText());
        for (OutcomesPanel outcomesPanel : outcomesPanels) {
            outcomesPanel.setOutcomes(null);
            if (outcomesPanel.isShowing()) {
                outcomesPanel.clearPanel();
                outcomesPanel.drawAxes();
            }
        }
    }

    public interface NetworkUiHost {
        void paintCurrentNetwork(Network network) throws Exception;
        void clearNodeInspector();
    }
}
