package es.ants.felixgm.trmsim_wsn.gui.events;


import es.ants.felixgm.trmsim_wsn.SimulationSlot;
import es.ants.felixgm.trmsim_wsn.gui.export.SimulationResultRepository;
import es.ants.felixgm.trmsim_wsn.outcomes.Outcome;
import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.OutcomesPanel;
import es.ants.felixgm.trmsim_wsn.gui.support.MessageConsoleHelper;

import javax.swing.SwingUtilities;
import javax.swing.JTextArea;
import java.util.ArrayList;
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
        SimulationSlot getSimulationSlot();
        SimulationResultRepository getSimulationResultsRepository();
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
        onNetworkUpdated(host, host.getSimulationSlot(), network);
    }

    public static void onNetworkUpdated(EventHost host, SimulationSlot slot, Network network) {
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
        onOutcomesUpdated(host, host.getSimulationSlot(), outcomes);
    }

    public static void onOutcomesUpdated(EventHost host, SimulationSlot slot, Collection<Outcome> outcomes) {
        Collection<Outcome> snapshot = new ArrayList<Outcome>(outcomes);
        runSimulationUpdate(host, () -> {
            SimulationResultRepository repository = host.getSimulationResultsRepository();
            repository.addAllOutcomes(snapshot);
            for (OutcomesPanel outcomesPanel : host.getOutcomesPanels()) {
                outcomesPanel.plotOutcomes(snapshot);
            }
            host.refreshSelectedNodeDetails();
        });
    }

    public static void onMessage(EventHost host, String message) {
        onMessage(host, host.getSimulationSlot(), message);
    }

    public static void onMessage(EventHost host, SimulationSlot slot, String message) {
        runSimulationUpdate(host, () -> {
            String normalizedMessage = message.replaceFirst("selected TRM", host.getSelectedTrustModelName());
            JTextArea messagesTextArea = host.getMessagesTextArea();
            MessageConsoleHelper.appendMessage(messagesTextArea, normalizedMessage);
            if (normalizedMessage.startsWith("Finishing")) {
                host.finishSimulationUi();
            }
        });
    }

    public static void onError(EventHost host, Exception exception) {
        onError(host, host.getSimulationSlot(), exception);
    }

    public static void onError(EventHost host, SimulationSlot slot, Exception exception) {
        runSimulationUpdate(host, () -> host.handleSimulationFailure(exception));
    }
}
