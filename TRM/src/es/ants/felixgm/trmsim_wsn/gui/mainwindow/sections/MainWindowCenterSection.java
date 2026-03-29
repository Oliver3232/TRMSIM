package es.ants.felixgm.trmsim_wsn.gui.mainwindow.sections;


import es.ants.felixgm.trmsim_wsn.gui.TRMSim_WSN;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowActionController;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public final class MainWindowCenterSection {
    private MainWindowCenterSection() {
    }

    public static void configure(
            TRMSim_WSN window,
            JSplitPane upperSplitPane,
            JPanel networkAndSensorPropertiesContainerPanel,
            JPanel networkPanelContainer,
            NetworkPanel networkPanel,
            JPanel sensorPropertiesPanel,
            javax.swing.JLabel sensorIdLabel,
            javax.swing.JTextField sensorIdTextField,
            javax.swing.JLabel xCoordinateLabel,
            javax.swing.JTextField xCoordinateTextField,
            javax.swing.JLabel yCoordinateLabel,
            javax.swing.JTextField yCoordinateTextField,
            javax.swing.JLabel radioRangePropertyLabel,
            javax.swing.JSpinner radioRangeSpinner,
            javax.swing.JLabel neighborsLabel,
            javax.swing.JScrollPane neighborsScrollPane,
            javax.swing.JList neighborsList,
            javax.swing.JLabel sensorTypeLabel,
            javax.swing.JComboBox sensorTypeComboBox,
            javax.swing.JButton applyChangesButton,
            javax.swing.JButton hideSensorPropertiesPanelButton) {
        networkAndSensorPropertiesContainerPanel.setLayout(new javax.swing.BoxLayout(networkAndSensorPropertiesContainerPanel, javax.swing.BoxLayout.LINE_AXIS));

        networkPanelContainer.setLayout(new BorderLayout());
        networkPanelContainer.add(networkPanel, BorderLayout.CENTER);
        networkPanel.setBackground(Color.white);
        networkPanelContainer.setBorder(javax.swing.BorderFactory.createTitledBorder("Network"));
        networkPanelContainer.setMinimumSize(new java.awt.Dimension(100, 100));
        networkPanelContainer.setPreferredSize(new Dimension(
                (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.5),
                (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.7)));
        networkPanelContainer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                MainWindowActionController.selectNodeFromNetworkPanel(window, evt);
            }
        });
        networkAndSensorPropertiesContainerPanel.add(networkPanelContainer);

        MainWindowSensorPropertiesSection.configure(
                window,
                sensorPropertiesPanel,
                sensorIdLabel,
                sensorIdTextField,
                xCoordinateLabel,
                xCoordinateTextField,
                yCoordinateLabel,
                yCoordinateTextField,
                radioRangePropertyLabel,
                radioRangeSpinner,
                neighborsLabel,
                neighborsScrollPane,
                neighborsList,
                sensorTypeLabel,
                sensorTypeComboBox,
                applyChangesButton,
                hideSensorPropertiesPanelButton);

        networkAndSensorPropertiesContainerPanel.add(sensorPropertiesPanel);
        upperSplitPane.setRightComponent(networkAndSensorPropertiesContainerPanel);
    }
}
