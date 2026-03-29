package es.ants.felixgm.trmsim_wsn.gui.mainwindow.hosts;


import es.ants.felixgm.trmsim_wsn.gui.MainWindowContext;
import es.ants.felixgm.trmsim_wsn.gui.graph.SimulationGraphWorkspace;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowSimulationControlsController;

public final class MainWindowSimulationControlsHostFactory {
    private MainWindowSimulationControlsHostFactory() {
    }

    public static MainWindowSimulationControlsController.Host create(MainWindowContext context) {
        return new MainWindowSimulationControlsController.Host() {
            public boolean isBatchRunning() { return context.isBatchRunning(); }
            public boolean isBatchPaused() { return context.isBatchPaused(); }
            public void markBatchIdle() { context.markBatchIdle(); }
            public void invokeRunBatch() { context.invokeRunBatch(); }
            public void invokeStopBatch() { context.invokeStopBatch(); }
            public javax.swing.AbstractButton getRunSimulationsButton() { return context.getRunSimulationsButton(); }
            public javax.swing.JMenuItem getRunSimulationsMenuItem() { return context.getRunSimulationsMenuItem(); }
            public javax.swing.JLabel getGraphStripSimulationStateLabel() { return context.getGraphStripSimulationStateLabel(); }
            public javax.swing.JButton getGraphStripRunButton() { return context.getGraphStripRunButton(); }
            public javax.swing.JButton getGraphStripStopButton() { return context.getGraphStripStopButton(); }
            public javax.swing.JLabel getGraphInspectorSimulationStateLabel() { return context.getGraphInspectorSimulationStateLabel(); }
            public javax.swing.JButton getGraphInspectorPauseResumeButton() { return context.getGraphInspectorPauseResumeButton(); }
            public javax.swing.JButton getGraphInspectorStopButton() { return context.getGraphInspectorStopButton(); }
            public javax.swing.JCheckBox getShowIdsCheckBox() { return context.getShowIdsCheckBox(); }
            public javax.swing.JCheckBox getShowLinksCheckBox() { return context.getShowLinksCheckBox(); }
            public javax.swing.JCheckBox getShowRangesCheckBox() { return context.getShowRangesCheckBox(); }
            public javax.swing.JCheckBox getShowGridCheckBox() { return context.getShowGridCheckBox(); }
            public javax.swing.JCheckBox getGraphInspectorShowIdsCheckBox() { return context.getGraphInspectorShowIdsCheckBox(); }
            public javax.swing.JCheckBox getGraphInspectorShowLinksCheckBox() { return context.getGraphInspectorShowLinksCheckBox(); }
            public javax.swing.JCheckBox getGraphInspectorShowRangesCheckBox() { return context.getGraphInspectorShowRangesCheckBox(); }
            public javax.swing.JCheckBox getGraphInspectorShowGridCheckBox() { return context.getGraphInspectorShowGridCheckBox(); }
            public javax.swing.JSlider getDelaySlider() { return context.getDelaySlider(); }
            public javax.swing.JSlider getGraphInspectorDelaySlider() { return context.getGraphInspectorDelaySlider(); }
            public SimulationGraphWorkspace getGraphWorkspace() { return context.getGraphWorkspace(); }
        };
    }
}
