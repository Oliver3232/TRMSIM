package es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers;


import es.ants.felixgm.trmsim_wsn.gui.graph.SimulationGraphWorkspace;
import es.ants.felixgm.trmsim_wsn.gui.support.SimulationControlsHelper;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JMenuItem;

public final class MainWindowSimulationControlsController {
    public interface Host {
        boolean isBatchRunning();
        boolean isBatchPaused();
        void markBatchIdle();
        void invokeRunBatch();
        void invokeStopBatch();
        AbstractButton getRunSimulationsButton();
        JMenuItem getRunSimulationsMenuItem();
        JLabel getGraphStripSimulationStateLabel();
        JButton getGraphStripRunButton();
        JButton getGraphStripStopButton();
        JLabel getGraphInspectorSimulationStateLabel();
        JButton getGraphInspectorPauseResumeButton();
        JButton getGraphInspectorStopButton();
        JCheckBox getShowIdsCheckBox();
        JCheckBox getShowLinksCheckBox();
        JCheckBox getShowRangesCheckBox();
        JCheckBox getShowGridCheckBox();
        JCheckBox getGraphInspectorShowIdsCheckBox();
        JCheckBox getGraphInspectorShowLinksCheckBox();
        JCheckBox getGraphInspectorShowRangesCheckBox();
        JCheckBox getGraphInspectorShowGridCheckBox();
        JSlider getDelaySlider();
        JSlider getGraphInspectorDelaySlider();
        SimulationGraphWorkspace getGraphWorkspace();
    }

    private MainWindowSimulationControlsController() {
    }

    public static void updateRunSimulationsControls(Host host) {
        String buttonLabel = SimulationControlsHelper.resolveRunButtonLabel(toBatchState(host));
        host.getRunSimulationsButton().setText(buttonLabel);
        host.getRunSimulationsMenuItem().setText(buttonLabel);
        updateInspectorSimulationControls(host);
    }

    public static void resetBatchSimulationState(Host host) {
        host.markBatchIdle();
        updateRunSimulationsControls(host);
        updateInspectorSimulationControls(host);
    }

    public static void syncEmbeddedAndFullscreenDisplayControls(Host host) {
        SimulationControlsHelper.syncDisplayControls(
                host.getShowIdsCheckBox(),
                host.getShowLinksCheckBox(),
                host.getShowRangesCheckBox(),
                host.getShowGridCheckBox(),
                host.getGraphInspectorShowIdsCheckBox(),
                host.getGraphInspectorShowLinksCheckBox(),
                host.getGraphInspectorShowRangesCheckBox(),
                host.getGraphInspectorShowGridCheckBox(),
                host.getDelaySlider(),
                host.getGraphInspectorDelaySlider(),
                host.getGraphWorkspace());
    }

    public static void handlePauseResumeRequest(Host host) {
        host.invokeRunBatch();
    }

    public static void handleStopRequest(Host host) {
        host.invokeStopBatch();
    }

    private static void updateInspectorSimulationControls(Host host) {
        SimulationControlsHelper.InspectorState inspectorState =
                SimulationControlsHelper.resolveInspectorState(
                        toBatchState(host),
                        host.getRunSimulationsButton().isEnabled());
        SimulationControlsHelper.applyInspectorState(
                inspectorState,
                host.getGraphInspectorSimulationStateLabel(),
                host.getGraphInspectorPauseResumeButton(),
                host.getGraphInspectorStopButton(),
                host.getGraphStripSimulationStateLabel(),
                host.getGraphStripRunButton(),
                host.getGraphStripStopButton(),
                host.getRunSimulationsButton(),
                host.getGraphWorkspace());
    }

    private static SimulationControlsHelper.BatchState toBatchState(Host host) {
        if (host.isBatchRunning()) {
            return SimulationControlsHelper.BatchState.RUNNING;
        }
        if (host.isBatchPaused()) {
            return SimulationControlsHelper.BatchState.PAUSED;
        }
        return SimulationControlsHelper.BatchState.IDLE;
    }
}
