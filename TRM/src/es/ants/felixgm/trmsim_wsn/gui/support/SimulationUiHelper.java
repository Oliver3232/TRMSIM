package es.ants.felixgm.trmsim_wsn.gui.support;


import es.ants.felixgm.trmsim_wsn.Controller;
import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.OutcomesPanel;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.util.Collection;

public final class SimulationUiHelper {
    private SimulationUiHelper() {
    }

    public static void resetOutcomePanels(Collection<OutcomesPanel> outcomesPanels) {
        for (OutcomesPanel outcomesPanel : outcomesPanels) {
            outcomesPanel.clearPanel();
            outcomesPanel.drawAxes();
        }
    }

    public static void finishSimulationUi(
            Runnable resetBatchSimulationState,
            Runnable disableSimulationComponents,
            Component stopTRMButton,
            JMenuItem stopTRMmenuItem,
            Component stopSimulationsButton,
            JMenuItem stopSimulationsMenuItem,
            javax.swing.JTextArea messagesTextArea) {
        resetBatchSimulationState.run();
        disableSimulationComponents.run();
        stopTRMButton.setEnabled(false);
        stopTRMmenuItem.setEnabled(false);
        stopSimulationsButton.setEnabled(false);
        stopSimulationsMenuItem.setEnabled(false);

        es.ants.felixgm.trmsim_wsn.gui.export.SimulationResultRepository repository = es.ants.felixgm.trmsim_wsn.gui.export.SimulationResultRepository.getInstance();
        MessageConsoleHelper.appendMessage(
                messagesTextArea,
                "Simulation completed. " + repository.getResultCount() + " results saved. Use 'Export Data' to save to file.\n");
    }

    public static void handleSimulationFailure(
            Component parent,
            Exception exception,
            Controller controller,
            Runnable resetBatchSimulationState,
            Runnable disableSimulationComponents,
            Component stopTRMButton,
            JMenuItem stopTRMmenuItem,
            Component stopSimulationsButton,
            JMenuItem stopSimulationsMenuItem) {
        JOptionPane.showMessageDialog(parent, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        try {
            controller.stopSimulations();
            resetBatchSimulationState.run();
            disableSimulationComponents.run();
            stopTRMButton.setEnabled(false);
            stopTRMmenuItem.setEnabled(false);
            stopSimulationsButton.setEnabled(false);
            stopSimulationsMenuItem.setEnabled(false);
        } catch (Exception stopException) {
            stopException.printStackTrace();
        }
        exception.printStackTrace();
    }
}
