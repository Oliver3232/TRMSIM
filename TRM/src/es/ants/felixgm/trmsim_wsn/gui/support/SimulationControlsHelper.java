package es.ants.felixgm.trmsim_wsn.gui.support;


import es.ants.felixgm.trmsim_wsn.gui.graph.SimulationGraphWorkspace;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JSlider;

public final class SimulationControlsHelper {
    public enum BatchState {
        IDLE,
        RUNNING,
        PAUSED
    }

    public static final class InspectorState {
        private final String stateText;
        private final String runText;
        private final String pauseResumeText;
        private final boolean canRun;
        private final boolean canPauseResume;
        private final boolean canStop;

        InspectorState(String stateText, String runText, String pauseResumeText, boolean canRun, boolean canPauseResume, boolean canStop) {
            this.stateText = stateText;
            this.runText = runText;
            this.pauseResumeText = pauseResumeText;
            this.canRun = canRun;
            this.canPauseResume = canPauseResume;
            this.canStop = canStop;
        }

        public String getStateText() {
            return stateText;
        }

        public String getRunText() {
            return runText;
        }

        public String getPauseResumeText() {
            return pauseResumeText;
        }

        public boolean canRun() {
            return canRun;
        }

        public boolean canPauseResume() {
            return canPauseResume;
        }

        public boolean canStop() {
            return canStop;
        }
    }

    private SimulationControlsHelper() {
    }

    public static String resolveRunButtonLabel(BatchState batchState) {
        if (batchState == BatchState.RUNNING) {
            return "Pause Simulations";
        }
        if (batchState == BatchState.PAUSED) {
            return "Resume Simulations";
        }
        return "Run Simulations";
    }

    public static InspectorState resolveInspectorState(BatchState batchState, boolean runButtonEnabled) {
        String stateText = "Idle";
        String runText = "Run Simulations";
        String pauseResumeText = "Pause";
        boolean canRun = runButtonEnabled;
        boolean canPauseResume = false;
        boolean canStop = false;

        if (batchState == BatchState.RUNNING) {
            stateText = "Running";
            pauseResumeText = "Pause";
            canPauseResume = true;
            canStop = true;
        } else if (batchState == BatchState.PAUSED) {
            stateText = "Paused";
            pauseResumeText = "Resume";
            canPauseResume = true;
            canStop = true;
        }

        if (batchState != BatchState.IDLE) {
            canRun = false;
        }

        return new InspectorState(stateText, runText, pauseResumeText, canRun, canPauseResume, canStop);
    }

    public static void syncDisplayControls(
            AbstractButton showIdsCheckBox,
            AbstractButton showLinksCheckBox,
            AbstractButton showRangesCheckBox,
            AbstractButton showGridCheckBox,
            AbstractButton inspectorShowIdsCheckBox,
            AbstractButton inspectorShowLinksCheckBox,
            AbstractButton inspectorShowRangesCheckBox,
            AbstractButton inspectorShowGridCheckBox,
            JSlider delaySlider,
            JSlider inspectorDelaySlider,
            SimulationGraphWorkspace graphWorkspace) {
        if (inspectorShowIdsCheckBox != null) {
            inspectorShowIdsCheckBox.setSelected(showIdsCheckBox.isSelected());
        }
        if (inspectorShowLinksCheckBox != null) {
            inspectorShowLinksCheckBox.setSelected(showLinksCheckBox.isSelected());
        }
        if (inspectorShowRangesCheckBox != null) {
            inspectorShowRangesCheckBox.setSelected(showRangesCheckBox.isSelected());
        }
        if (inspectorShowGridCheckBox != null) {
            inspectorShowGridCheckBox.setSelected(showGridCheckBox.isSelected());
        }
        if (inspectorDelaySlider != null && inspectorDelaySlider.getValue() != delaySlider.getValue()) {
            inspectorDelaySlider.setValue(delaySlider.getValue());
        }
        if (graphWorkspace != null) {
            graphWorkspace.updateDisplayControlsState(
                    showIdsCheckBox.isSelected(),
                    showLinksCheckBox.isSelected(),
                    showRangesCheckBox.isSelected(),
                    showGridCheckBox.isSelected(),
                    delaySlider.getValue(),
                    delaySlider.getMinimum(),
                    delaySlider.getMaximum());
        }
    }

    public static void applyInspectorState(
            InspectorState inspectorState,
            JLabel graphInspectorSimulationStateLabel,
            AbstractButton graphInspectorPauseResumeButton,
            AbstractButton graphInspectorStopButton,
            JLabel graphStripSimulationStateLabel,
            AbstractButton graphStripRunButton,
            AbstractButton graphStripStopButton,
            AbstractButton runSimulationsButton,
            SimulationGraphWorkspace graphWorkspace) {
        if (graphInspectorSimulationStateLabel != null) {
            graphInspectorSimulationStateLabel.setText(inspectorState.getStateText());
        }
        if (graphInspectorPauseResumeButton != null) {
            graphInspectorPauseResumeButton.setText(inspectorState.canRun() ? inspectorState.getRunText() : inspectorState.getPauseResumeText());
            graphInspectorPauseResumeButton.setEnabled(inspectorState.canRun() || inspectorState.canPauseResume());
        }
        if (graphInspectorStopButton != null) {
            graphInspectorStopButton.setEnabled(inspectorState.canStop());
        }
        if (graphStripSimulationStateLabel != null) {
            graphStripSimulationStateLabel.setText(inspectorState.getStateText());
        }
        if (graphStripRunButton != null) {
            graphStripRunButton.setText(runSimulationsButton.getText());
            graphStripRunButton.setEnabled(inspectorState.canRun() || inspectorState.canPauseResume());
        }
        if (graphStripStopButton != null) {
            graphStripStopButton.setEnabled(inspectorState.canStop());
        }
        if (graphWorkspace != null) {
            graphWorkspace.updateSimulationControlsState(
                    inspectorState.getStateText(),
                    inspectorState.getRunText(),
                    runSimulationsButton.getText(),
                    inspectorState.canRun(),
                    inspectorState.canPauseResume(),
                    inspectorState.canStop());
        }
    }
}
