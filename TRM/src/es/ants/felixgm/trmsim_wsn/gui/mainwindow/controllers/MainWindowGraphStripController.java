package es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers;


import es.ants.felixgm.trmsim_wsn.gui.MainWindowContext;
import es.ants.felixgm.trmsim_wsn.gui.layout.CompactLegendPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public final class MainWindowGraphStripController {
    private MainWindowGraphStripController() {
    }

    public static void install(MainWindowContext context) {
        JPanel liveControlsPanel = new JPanel();
        liveControlsPanel.setOpaque(false);
        liveControlsPanel.setLayout(new BoxLayout(liveControlsPanel, BoxLayout.Y_AXIS));

        JLabel liveLabel = new JLabel("Live Simulation");
        liveLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        liveLabel.setForeground(new Color(77, 111, 153));
        liveLabel.setAlignmentX(0.0f);

        JLabel simulationStateLabel = new JLabel("Idle");
        simulationStateLabel.setForeground(new Color(44, 56, 77));
        simulationStateLabel.setAlignmentX(0.0f);

        JPanel controlsButtons = new JPanel();
        controlsButtons.setOpaque(false);
        controlsButtons.setLayout(new BoxLayout(controlsButtons, BoxLayout.X_AXIS));

        JButton runButton = new JButton("Run Simulations");
        runButton.addActionListener(evt -> context.handlePauseResumeRequest());
        JButton stopButton = new JButton("Stop Simulations");
        stopButton.addActionListener(evt -> context.handleStopRequest());
        controlsButtons.add(runButton);
        controlsButtons.add(Box.createHorizontalStrut(8));
        controlsButtons.add(stopButton);
        controlsButtons.setAlignmentX(0.0f);

        JTextArea infoArea = new JTextArea(
                "Use Run/Pause/Resume for the current batch. Stop ends the active batch. " +
                "Display toggles, delay, render mode and fullscreen stay available next to this block.");
        infoArea.setEditable(false);
        infoArea.setOpaque(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setRows(2);
        infoArea.setFont(new Font("SansSerif", Font.PLAIN, 11));
        infoArea.setForeground(new Color(77, 88, 105));

        liveControlsPanel.add(liveLabel);
        liveControlsPanel.add(Box.createVerticalStrut(2));
        liveControlsPanel.add(simulationStateLabel);
        liveControlsPanel.add(Box.createVerticalStrut(6));
        liveControlsPanel.add(controlsButtons);
        liveControlsPanel.add(Box.createVerticalStrut(6));
        liveControlsPanel.add(infoArea);
        liveControlsPanel.setPreferredSize(new Dimension(280, 82));
        liveControlsPanel.setMinimumSize(new Dimension(240, 82));

        CompactLegendPanel dashboardLegendPanel = new CompactLegendPanel();
        dashboardLegendPanel.setItems(context.createLegendItemsFromCurrentLegend());
        dashboardLegendPanel.setOpaque(false);

        context.setGraphStripComponents(liveControlsPanel, simulationStateLabel, runButton, stopButton, infoArea, dashboardLegendPanel);
    }
}
