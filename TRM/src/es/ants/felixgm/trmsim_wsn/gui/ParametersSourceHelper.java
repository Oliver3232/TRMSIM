package es.ants.felixgm.trmsim_wsn.gui;

import es.ants.felixgm.trmsim_wsn.Controller;
import es.ants.felixgm.trmsim_wsn.trm.TRMParameters;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.TRMParametersPanel;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Component;
import java.io.File;

final class ParametersSourceHelper {
    private ParametersSourceHelper() {
    }

    static void applySelectionState(
            boolean parametersFromFile,
            JLabel parametersFileLabel,
            JTextField parametersFileTextField,
            Component browseButton,
            JTextArea parametersFileContentTextArea,
            Component saveParametersFileContentButton,
            JMenuItem loadParametersMenuItem,
            JMenuItem saveParametersMenuItem,
            TRMParametersPanel parametersPanel,
            Component applyParametersChangesButton,
            JMenuItem applyParametersChangesMenuItem) {
        parametersFileLabel.setEnabled(parametersFromFile);
        parametersFileTextField.setEnabled(parametersFromFile);
        browseButton.setEnabled(parametersFromFile);
        parametersFileContentTextArea.setEnabled(parametersFromFile);
        saveParametersFileContentButton.setEnabled(parametersFromFile);
        loadParametersMenuItem.setEnabled(parametersFromFile);
        saveParametersMenuItem.setEnabled(parametersFromFile);
        parametersPanel.setEnabled(!parametersFromFile);
        applyParametersChangesButton.setEnabled(!parametersFromFile);
        applyParametersChangesMenuItem.setEnabled(!parametersFromFile);
    }

    static void loadParametersFromSelectedFile(
            Component parent,
            Controller controller,
            JTextField parametersFileTextField,
            JTextArea parametersFileContentTextArea,
            TRMParametersPanel parametersPanel) throws Exception {
        File selectedFile = chooseParametersFile(parent, JFileChooser.OPEN_DIALOG);
        if (selectedFile == null) {
            return;
        }
        parametersFileTextField.setText(selectedFile.getName());
        controller.set_parametersFile(selectedFile.getCanonicalPath());
        TRMParameters trmParameters = controller.set_TRMParameters(controller.get_parametersFile());
        parametersPanel.set_TRMParameters(trmParameters);
        parametersFileContentTextArea.setText(controller.get_ParametersFileContent());
        JOptionPane.showMessageDialog(parent, "Parameters loaded successfully from file", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    static void saveParametersToSelectedFile(
            Component parent,
            Controller controller,
            JTextArea parametersFileContentTextArea) throws Exception {
        File selectedFile = chooseParametersFile(parent, JFileChooser.SAVE_DIALOG);
        if (selectedFile == null) {
            return;
        }
        controller.saveParametersFileContent(selectedFile.getAbsolutePath(), parametersFileContentTextArea.getText());
        JOptionPane.showMessageDialog(parent, "Parameters file successfully saved", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    static void applyAndRefreshPanelParameters(
            Component parent,
            Controller controller,
            TRMParametersPanel parametersPanel,
            JTextArea parametersFileContentTextArea) throws Exception {
        controller.set_TRMParameters(parametersPanel);
        parametersPanel.set_TRMParameters(controller.get_TRMParameters());
        parametersFileContentTextArea.setText(controller.get_TRMParameters().toString());
        JOptionPane.showMessageDialog(parent, "Parameters changes successfully saved", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private static File chooseParametersFile(Component parent, int dialogType) {
        JFileChooser fileChooser = new JFileChooser(".");
        fileChooser.setDialogType(dialogType);
        fileChooser.setDialogTitle("Parameters file selection");
        int result;
        if (dialogType == JFileChooser.OPEN_DIALOG) {
            result = fileChooser.showOpenDialog(parent);
        } else {
            result = fileChooser.showSaveDialog(parent);
        }
        if (result != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        return fileChooser.getSelectedFile();
    }
}
