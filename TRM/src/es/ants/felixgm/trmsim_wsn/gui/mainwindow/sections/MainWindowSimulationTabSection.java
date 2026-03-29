package es.ants.felixgm.trmsim_wsn.gui.mainwindow.sections;


import es.ants.felixgm.trmsim_wsn.gui.TRMSim_WSN;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

public final class MainWindowSimulationTabSection {
    private MainWindowSimulationTabSection() {
    }

    public static void configure(
            TRMSim_WSN window,
            JTabbedPane tabbedPane,
            JPanel simulationsPanel,
            JSplitPane simulationsSplitPane,
            JPanel upperPanel,
            JSplitPane upperSplitPane,
            JScrollPane controlsScrollPane,
            JPanel bottomPanel,
            JPanel outcomesPanelsPanel,
            JTabbedPane outcomesTabbedPane,
            JPanel messagePanel,
            JScrollPane messagesScrollPane,
            javax.swing.JTextArea messagesTextArea) {
        simulationsPanel.setPreferredSize(new Dimension(
                (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()));
        simulationsPanel.setLayout(new javax.swing.BoxLayout(simulationsPanel, javax.swing.BoxLayout.Y_AXIS));

        simulationsSplitPane.setDividerLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.65));
        simulationsSplitPane.setDividerSize(3);
        simulationsSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        simulationsSplitPane.setPreferredSize(new Dimension(
                (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()));

        upperPanel.setPreferredSize(new Dimension(
                (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
                (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.6)));
        upperPanel.setLayout(new javax.swing.BoxLayout(upperPanel, javax.swing.BoxLayout.X_AXIS));

        upperSplitPane.setDividerLocation(360);
        upperSplitPane.setDividerSize(7);
        upperSplitPane.setPreferredSize(upperPanel.getPreferredSize());
        upperSplitPane.setLeftComponent(controlsScrollPane);
        upperPanel.add(upperSplitPane);
        simulationsSplitPane.setTopComponent(upperPanel);

        MainWindowBottomSection.configureSimulationBottom(
                bottomPanel,
                outcomesPanelsPanel,
                outcomesTabbedPane,
                messagePanel,
                messagesScrollPane,
                messagesTextArea);

        simulationsSplitPane.setBottomComponent(bottomPanel);
        bottomPanel.getAccessibleContext().setAccessibleParent(simulationsPanel);
        simulationsPanel.add(simulationsSplitPane);
        tabbedPane.addTab("Simulations", simulationsPanel);
    }
}
