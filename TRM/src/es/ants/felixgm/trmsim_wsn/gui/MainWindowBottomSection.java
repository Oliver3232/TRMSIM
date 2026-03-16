package es.ants.felixgm.trmsim_wsn.gui;

import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

final class MainWindowBottomSection {
    private MainWindowBottomSection() {
    }

    static void configureSimulationBottom(JPanel bottomPanel,
                                          JPanel outcomesPanelsPanel,
                                          JTabbedPane outcomesTabbedPane,
                                          JPanel messagePanel,
                                          JScrollPane messagesScrollPane,
                                          JTextArea messagesTextArea) {
        bottomPanel.setPreferredSize(new Dimension((int) (java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth()),
                (int) (java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.3)));
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));

        outcomesPanelsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Outcomes"));
        outcomesPanelsPanel.setPreferredSize(new java.awt.Dimension(500, 260));
        GroupLayout outcomesPanelsPanelLayout = new GroupLayout(outcomesPanelsPanel);
        outcomesPanelsPanel.setLayout(outcomesPanelsPanelLayout);
        outcomesPanelsPanelLayout.setHorizontalGroup(
                outcomesPanelsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, outcomesPanelsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(outcomesTabbedPane, GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE)
                                .addContainerGap()));
        outcomesPanelsPanelLayout.setVerticalGroup(
                outcomesPanelsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, outcomesPanelsPanelLayout.createSequentialGroup()
                                .addComponent(outcomesTabbedPane, GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                                .addContainerGap()));
        bottomPanel.add(outcomesPanelsPanel);

        messagePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Messages"));
        messagePanel.setPreferredSize(new java.awt.Dimension(500, 260));
        messagesScrollPane.setAutoscrolls(true);
        messagesTextArea.setColumns(20);
        messagesTextArea.setEditable(false);
        messagesTextArea.setRows(5);
        messagesScrollPane.setViewportView(messagesTextArea);
        GroupLayout messagePanelLayout = new GroupLayout(messagePanel);
        messagePanel.setLayout(messagePanelLayout);
        messagePanelLayout.setHorizontalGroup(
                messagePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(messagePanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(messagesScrollPane, GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE)
                                .addContainerGap()));
        messagePanelLayout.setVerticalGroup(
                messagePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(messagePanelLayout.createSequentialGroup()
                                .addComponent(messagesScrollPane, GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                                .addContainerGap()));
        bottomPanel.add(messagePanel);
    }

    static void configureParametersContent(JPanel parametersPanel,
                                           JPanel bottomParametersContainerPanel,
                                           JScrollPane TRMParametersScrollPane,
                                           JPanel TRM_ParametersPanelAux,
                                           JScrollPane parametersFileContentScrollPane,
                                           JTextArea parametersFileContentTextArea,
                                           javax.swing.JSeparator separator4) {
        bottomParametersContainerPanel.setLayout(new BoxLayout(bottomParametersContainerPanel, BoxLayout.Y_AXIS));
        bottomParametersContainerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 0, 0, 0));
        bottomParametersContainerPanel.setAlignmentX(0.0f);
        bottomParametersContainerPanel.setMinimumSize(new Dimension(0, 0));
        bottomParametersContainerPanel.setPreferredSize(new Dimension(0, 0));
        bottomParametersContainerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        TRMParametersScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Trust & Reputation model parameters"));
        TRMParametersScrollPane.setMinimumSize(new Dimension(0, 0));
        TRMParametersScrollPane.setPreferredSize(new Dimension(0, 0));
        TRMParametersScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        GroupLayout auxLayout = new GroupLayout(TRM_ParametersPanelAux);
        TRM_ParametersPanelAux.setLayout(auxLayout);
        auxLayout.setHorizontalGroup(auxLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        auxLayout.setVerticalGroup(auxLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 766, Short.MAX_VALUE));
        TRMParametersScrollPane.setViewportView(TRM_ParametersPanelAux);

        parametersFileContentScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Parameters file content"));
        parametersFileContentScrollPane.setMinimumSize(new Dimension(0, 0));
        parametersFileContentScrollPane.setPreferredSize(new Dimension(0, 0));
        parametersFileContentScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        parametersFileContentTextArea.setColumns(20);
        parametersFileContentTextArea.setRows(5);
        parametersFileContentTextArea.setAutoscrolls(false);
        parametersFileContentScrollPane.setViewportView(parametersFileContentTextArea);

        bottomParametersContainerPanel.add(TRMParametersScrollPane);
        bottomParametersContainerPanel.add(Box.createVerticalStrut(10));
        bottomParametersContainerPanel.add(parametersFileContentScrollPane);
        parametersPanel.add(bottomParametersContainerPanel);

        separator4.setMinimumSize(new java.awt.Dimension(0, 11));
        separator4.setPreferredSize(new java.awt.Dimension(50, 11));
        parametersPanel.add(separator4);
    }
}
