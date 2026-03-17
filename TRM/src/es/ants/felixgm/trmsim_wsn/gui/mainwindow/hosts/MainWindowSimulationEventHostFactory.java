package es.ants.felixgm.trmsim_wsn.gui.mainwindow.hosts;


import es.ants.felixgm.trmsim_wsn.gui.MainWindowContext;
import es.ants.felixgm.trmsim_wsn.gui.events.SimulationEventHelper;
import es.ants.felixgm.trmsim_wsn.network.Network;

public final class MainWindowSimulationEventHostFactory {
    private MainWindowSimulationEventHostFactory() {
    }

    public static SimulationEventHelper.EventHost create(MainWindowContext context) {
        return new SimulationEventHelper.EventHost() {
            public void paintUpdatedNetwork(Network network) throws Exception {
                context.paintNetwork(network, context.getController().get_requiredService());
            }

            public void refreshSelectedNodeDetails() {
                es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowNodeInspectorController.refreshSelectedNodeDetails(MainWindowNodeInspectorHostFactory.create(context));
            }

            public java.util.Collection<es.ants.felixgm.trmsim_wsn.gui.outcomespanels.OutcomesPanel> getOutcomesPanels() {
                return context.getOutcomesPanels();
            }

            public void finishSimulationUi() {
                context.finishSimulationUi();
            }

            public void handleSimulationFailure(Exception exception) {
                context.handleSimulationFailure(exception);
            }

            public void sleepAfterUiUpdate() {
                context.sleepAfterUiUpdate();
            }

            public String getSelectedTrustModelName() {
                return context.getSelectedTrustModelName();
            }

            public javax.swing.JTextArea getMessagesTextArea() {
                return context.getMessagesTextArea();
            }
        };
    }
}
