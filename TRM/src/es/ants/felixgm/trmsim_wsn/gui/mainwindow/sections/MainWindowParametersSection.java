package es.ants.felixgm.trmsim_wsn.gui.mainwindow.sections;


import es.ants.felixgm.trmsim_wsn.gui.MainWindowHosts;
import es.ants.felixgm.trmsim_wsn.gui.TRMSim_WSN;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowActionController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowParametersController;
import java.awt.Dimension;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;

public final class MainWindowParametersSection {
    private MainWindowParametersSection() {
    }

    public static void configure(TRMSim_WSN window,
                          JPanel parametersSettingsPanel,
                          JSeparator separator1,
                          JSeparator separator2,
                          JTextField parametersFileTextField,
                          JLabel parametersFileLabel,
                          JRadioButton customizedParametersRadioButton,
                          JRadioButton parametersFileRadioButton,
                          JLabel parametersSourceLabel,
                          JButton applyParametersChangesButton,
                          JButton saveParametersFileContentButton,
                          JButton browseButton) {
        parametersSettingsPanel.setMinimumSize(new Dimension(320, 150));
        parametersSettingsPanel.setPreferredSize(new Dimension(380, 150));
        parametersSettingsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        parametersSettingsPanel.setAlignmentX(0.0f);

        separator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        parametersFileTextField.setText("BTRM-WSNparameters.txt");
        parametersFileLabel.setText("Parameters file");

        customizedParametersRadioButton.setText("Customized");
        customizedParametersRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        customizedParametersRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        parametersFileRadioButton.setSelected(true);
        parametersFileRadioButton.setText("File");
        parametersFileRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        parametersFileRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        parametersFileRadioButton.addItemListener(evt ->
                MainWindowParametersController.handleSourceSelectionChanged(MainWindowHosts.parameters(window)));

        parametersSourceLabel.setText("Source");

        applyParametersChangesButton.setText("Apply changes");
        applyParametersChangesButton.setEnabled(false);
        applyParametersChangesButton.addActionListener(evt -> MainWindowActionController.applyParametersChanges(window));

        saveParametersFileContentButton.setText("Save file content");
        saveParametersFileContentButton.addActionListener(evt -> MainWindowActionController.saveParametersFile(window));

        browseButton.setText("Browse");
        browseButton.addActionListener(evt -> MainWindowActionController.loadParametersFile(window));

        GroupLayout parametersSettingsPanelLayout = new GroupLayout(parametersSettingsPanel);
        parametersSettingsPanel.setLayout(parametersSettingsPanelLayout);
        parametersSettingsPanelLayout.setHorizontalGroup(
                parametersSettingsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(parametersSettingsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(parametersSettingsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(parametersSettingsPanelLayout.createSequentialGroup()
                                                .addGroup(parametersSettingsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(parametersSourceLabel, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(customizedParametersRadioButton, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(parametersFileRadioButton, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(separator1, GroupLayout.PREFERRED_SIZE, 8, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(parametersSettingsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(parametersFileLabel, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(parametersFileTextField, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                                        .addComponent(browseButton)))
                                        .addGroup(parametersSettingsPanelLayout.createSequentialGroup()
                                                .addComponent(applyParametersChangesButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(saveParametersFileContentButton))
                                        .addComponent(separator2))
                                .addContainerGap())
        );
        parametersSettingsPanelLayout.setVerticalGroup(
                parametersSettingsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(parametersSettingsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(parametersSettingsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(parametersSettingsPanelLayout.createSequentialGroup()
                                                .addComponent(parametersSourceLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(parametersFileRadioButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(customizedParametersRadioButton))
                                        .addGroup(parametersSettingsPanelLayout.createSequentialGroup()
                                                .addComponent(parametersFileLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(parametersFileTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(browseButton))
                                        .addComponent(separator1, GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(separator2, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(parametersSettingsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(applyParametersChangesButton)
                                        .addComponent(saveParametersFileContentButton))
                                .addContainerGap(15, Short.MAX_VALUE))
        );
    }
}
