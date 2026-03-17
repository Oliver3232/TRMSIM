package es.ants.felixgm.trmsim_wsn.gui.events;


import es.ants.felixgm.trmsim_wsn.outcomes.Outcome;
import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.OutcomesPanel;

import javax.swing.SwingUtilities;
import javax.swing.JTextArea;
import java.util.Collection;

public final class SimulationEventHelper {
    public interface EventHost {
        void paintUpdatedNetwork(Network network) throws Exception;
        void refreshSelectedNodeDetails();
        Collection<OutcomesPanel> getOutcomesPanels();
        void finishSimulationUi();
        void handleSimulationFailure(Exception exception);
        void sleepAfterUiUpdate();
        String getSelectedTrustModelName();
        JTextArea getMessagesTextArea();
    }

    private SimulationEventHelper() {
    }

    public static void runSimulationUpdate(EventHost host, Runnable updateTask) {
        try {
            if (SwingUtilities.isEventDispatchThread()) {
                updateTask.run();
            } else {
                SwingUtilities.invokeAndWait(updateTask);
            }
        } catch (Exception ex) {
            host.handleSimulationFailure(ex);
        } finally {
            host.sleepAfterUiUpdate();
        }
    }

    public static void onNetworkUpdated(EventHost host, Network network) {
        runSimulationUpdate(host, () -> {
            try {
                host.paintUpdatedNetwork(network);
                host.refreshSelectedNodeDetails();
            } catch (Exception ex) {
                host.handleSimulationFailure(ex);
            }
        });
    }

    public static void onOutcomesUpdated(EventHost host, Collection<Outcome> outcomes) {
        runSimulationUpdate(host, () -> {
            es.ants.felixgm.trmsim_wsn.gui.export.SimulationResultRepository repository = es.ants.felixgm.trmsim_wsn.gui.export.SimulationResultRepository.getInstance();
            repository.addAllOutcomes(outcomes);
            for (OutcomesPanel outcomesPanel : host.getOutcomesPanels()) {
                outcomesPanel.plotOutcomes(outcomes);
            }
            host.refreshSelectedNodeDetails();
        });
    }

    public static void onMessage(EventHost host, String message) {
        runSimulationUpdate(host, () -> {
            String normalizedMessage = message.replaceFirst("selected TRM", host.getSelectedTrustModelName());
            JTextArea messagesTextArea = host.getMessagesTextArea();
            messagesTextArea.setText(normalizedMessage + messagesTextArea.getText());
            if (normalizedMessage.startsWith("Finishing")) {
                host.finishSimulationUi();
            }
        });
    }

    public static void onError(EventHost host, Exception exception) {
        runSimulationUpdate(host, () -> host.handleSimulationFailure(exception));
    }
}
