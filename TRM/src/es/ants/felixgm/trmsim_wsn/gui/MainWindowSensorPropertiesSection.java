package es.ants.felixgm.trmsim_wsn.gui;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;

final class MainWindowSensorPropertiesSection {
    private MainWindowSensorPropertiesSection() {
    }

    static void configure(TRMSim_WSN window,
                          JPanel sensorPropertiesPanel,
                          JLabel sensorIdLabel,
                          JTextField sensorIdTextField,
                          JLabel xCoordinateLabel,
                          JTextField xCoordinateTextField,
                          JLabel yCoordinateLabel,
                          JTextField yCoordinateTextField,
                          JLabel radioRangePropertyLabel,
                          JSpinner radioRangeSpinner,
                          JLabel neighborsLabel,
                          JScrollPane neighborsScrollPane,
                          JList neighborsList,
                          JLabel sensorTypeLabel,
                          JComboBox sensorTypeComboBox,
                          JButton applyChangesButton,
                          JButton hideSensorPropertiesPanelButton) {
        sensorPropertiesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Sensor properties"));
        sensorPropertiesPanel.setMinimumSize(new java.awt.Dimension(120, 400));
        sensorPropertiesPanel.setPreferredSize(new java.awt.Dimension(120, 400));

        sensorIdLabel.setText("Sensor Id");

        sensorIdTextField.setAlignmentX(0.0F);
        sensorIdTextField.setEnabled(false);
        sensorIdTextField.setMinimumSize(new java.awt.Dimension(30, 20));
        sensorIdTextField.setPreferredSize(new java.awt.Dimension(30, 20));

        xCoordinateLabel.setText("X");
        xCoordinateTextField.setEditable(false);
        xCoordinateTextField.setMinimumSize(new java.awt.Dimension(30, 20));
        xCoordinateTextField.setPreferredSize(new java.awt.Dimension(30, 20));

        yCoordinateLabel.setText("Y");
        yCoordinateTextField.setEditable(false);
        yCoordinateTextField.setMinimumSize(new java.awt.Dimension(30, 20));
        yCoordinateTextField.setPreferredSize(new java.awt.Dimension(30, 20));

        radioRangePropertyLabel.setText("Radio range");

        neighborsLabel.setText("Neighbor(s)");
        neighborsScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        neighborsScrollPane.setAutoscrolls(true);
        neighborsScrollPane.setMinimumSize(new java.awt.Dimension(33, 80));
        neighborsScrollPane.setPreferredSize(new java.awt.Dimension(33, 80));

        neighborsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        neighborsList.setMaximumSize(new java.awt.Dimension(32767, 32767));
        neighborsList.setMinimumSize(new java.awt.Dimension(33, 80));
        neighborsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                MainWindowActionController.selectNeighborOnDoubleClick(window, evt);
            }
        });
        neighborsScrollPane.setViewportView(neighborsList);

        sensorTypeLabel.setText("Sensor type");
        sensorTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        applyChangesButton.setText("Apply");
        applyChangesButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
        applyChangesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                window.applyChangesButtonActionPerformed(evt);
            }
        });

        hideSensorPropertiesPanelButton.setText("Hide panel");
        hideSensorPropertiesPanelButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        hideSensorPropertiesPanelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainWindowActionController.hideSensorProperties(window);
            }
        });

        GroupLayout sensorPropertiesPanelLayout = new GroupLayout(sensorPropertiesPanel);
        sensorPropertiesPanel.setLayout(sensorPropertiesPanelLayout);
        sensorPropertiesPanelLayout.setHorizontalGroup(
                sensorPropertiesPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(sensorPropertiesPanelLayout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addGroup(sensorPropertiesPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addGroup(sensorPropertiesPanelLayout.createSequentialGroup()
                                                .addComponent(sensorIdLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(sensorIdTextField, GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                                        .addComponent(radioRangePropertyLabel)
                                        .addComponent(sensorTypeLabel)
                                        .addComponent(neighborsLabel)
                                        .addGroup(sensorPropertiesPanelLayout.createSequentialGroup()
                                                .addComponent(xCoordinateLabel, GroupLayout.PREFERRED_SIZE, 9, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(xCoordinateTextField, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(yCoordinateLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(yCoordinateTextField, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(radioRangeSpinner)
                                        .addComponent(neighborsScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(sensorTypeComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(applyChangesButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(hideSensorPropertiesPanelButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(29, Short.MAX_VALUE))
        );
        sensorPropertiesPanelLayout.setVerticalGroup(
                sensorPropertiesPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(sensorPropertiesPanelLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(sensorPropertiesPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(sensorIdLabel)
                                        .addComponent(sensorIdTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(sensorPropertiesPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(xCoordinateLabel)
                                        .addComponent(xCoordinateTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(yCoordinateLabel)
                                        .addComponent(yCoordinateTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(radioRangePropertyLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(radioRangeSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(neighborsLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(neighborsScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sensorTypeLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sensorTypeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(applyChangesButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(hideSensorPropertiesPanelButton)
                                .addContainerGap(37, Short.MAX_VALUE))
        );

        radioRangePropertyLabel.setVisible(false);
        radioRangeSpinner.setVisible(false);
        sensorTypeLabel.setVisible(false);
        sensorTypeComboBox.setVisible(false);
        applyChangesButton.setVisible(false);
        sensorPropertiesPanel.setVisible(false);
    }
}
