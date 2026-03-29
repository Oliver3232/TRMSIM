package es.ants.felixgm.trmsim_wsn.gui.mainwindow.sections;


import es.ants.felixgm.trmsim_wsn.gui.TRMSim_WSN;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public final class MainWindowParametersTabSection {
    private MainWindowParametersTabSection() {
    }

    public static void configure(
            TRMSim_WSN window,
            JTabbedPane tabbedPane,
            JPanel parametersPanel,
            JPanel parametersSettingsPanel,
            javax.swing.JSeparator separator1,
            javax.swing.JSeparator separator2,
            javax.swing.JTextField parametersFileTextField,
            javax.swing.JLabel parametersFileLabel,
            javax.swing.JRadioButton customizedParametersRadioButton,
            javax.swing.JRadioButton parametersFileRadioButton,
            javax.swing.JLabel parametersSourceLabel,
            javax.swing.JButton applyParametersChangesButton,
            javax.swing.JButton saveParametersFileContentButton,
            javax.swing.JButton browseButton,
            javax.swing.ButtonGroup parametersSourceButtonGroup,
            javax.swing.JSeparator separator3,
            JPanel bottomParametersContainerPanel,
            javax.swing.JScrollPane TRMParametersScrollPane,
            JPanel TRM_ParametersPanelAux,
            javax.swing.JScrollPane parametersFileContentScrollPane,
            javax.swing.JTextArea parametersFileContentTextArea,
            javax.swing.JSeparator separator4) {
        parametersPanel.setLayout(new javax.swing.BoxLayout(parametersPanel, javax.swing.BoxLayout.Y_AXIS));

        parametersSourceButtonGroup.add(customizedParametersRadioButton);
        parametersSourceButtonGroup.add(parametersFileRadioButton);
        MainWindowParametersSection.configure(
                window,
                parametersSettingsPanel,
                separator1,
                separator2,
                parametersFileTextField,
                parametersFileLabel,
                customizedParametersRadioButton,
                parametersFileRadioButton,
                parametersSourceLabel,
                applyParametersChangesButton,
                saveParametersFileContentButton,
                browseButton);

        parametersPanel.add(parametersSettingsPanel);
        parametersPanel.add(separator3);

        MainWindowBottomSection.configureParametersContent(
                parametersPanel,
                bottomParametersContainerPanel,
                TRMParametersScrollPane,
                TRM_ParametersPanelAux,
                parametersFileContentScrollPane,
                parametersFileContentTextArea,
                separator4);

        tabbedPane.addTab("Parameters", parametersPanel);
    }
}
