package es.ants.felixgm.trmsim_wsn.gui;

import es.ants.felixgm.trmsim_wsn.Controller;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.TRMParametersPanel;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.JTextField;

final class MainWindowParametersController {
    interface Host {
        Controller getController();
        boolean isParametersFileSelected();
        JLabel getParametersFileLabel();
        JTextField getParametersFileTextField();
        AbstractButton getBrowseButton();
        JTextArea getParametersFileContentTextArea();
        AbstractButton getSaveParametersFileContentButton();
        JMenuItem getLoadParametersMenuItem();
        JMenuItem getSaveParametersMenuItem();
        TRMParametersPanel getParametersPanel();
        AbstractButton getApplyParametersChangesButton();
        JMenuItem getApplyParametersChangesMenuItem();
        void updateParametersSourceView();
    }

    private MainWindowParametersController() {
    }

    static void setParameters(Host host) throws Exception {
        applyParameterSourceSelectionState(host);
        if (host.isParametersFileSelected()) {
            loadParametersFromConfiguredFile(host);
        } else {
            applyPanelParametersToController(host);
        }
    }

    static void handleSourceSelectionChanged(Host host) {
        applyParameterSourceSelectionState(host);
        host.updateParametersSourceView();
    }

    static void selectAndLoadParametersFile(Host host) throws Exception {
        ParametersSourceHelper.loadParametersFromSelectedFile(
                host.getParametersFileTextField(),
                host.getController(),
                host.getParametersFileTextField(),
                host.getParametersFileContentTextArea(),
                host.getParametersPanel());
        host.updateParametersSourceView();
    }

    static void selectAndSaveParametersFile(Host host) throws Exception {
        ParametersSourceHelper.saveParametersToSelectedFile(
                host.getParametersFileTextField(),
                host.getController(),
                host.getParametersFileContentTextArea());
    }

    static void applyAndRefreshPanelParameters(Host host) throws Exception {
        ParametersSourceHelper.applyAndRefreshPanelParameters(
                host.getParametersFileTextField(),
                host.getController(),
                host.getParametersPanel(),
                host.getParametersFileContentTextArea());
    }

    private static void applyParameterSourceSelectionState(Host host) {
        ParametersSourceHelper.applySelectionState(
                host.isParametersFileSelected(),
                host.getParametersFileLabel(),
                host.getParametersFileTextField(),
                host.getBrowseButton(),
                host.getParametersFileContentTextArea(),
                host.getSaveParametersFileContentButton(),
                host.getLoadParametersMenuItem(),
                host.getSaveParametersMenuItem(),
                host.getParametersPanel(),
                host.getApplyParametersChangesButton(),
                host.getApplyParametersChangesMenuItem());
    }

    private static void loadParametersFromConfiguredFile(Host host) throws Exception {
        host.getParametersPanel().set_TRMParameters(
                host.getController().set_TRMParameters(host.getController().get_parametersFile()));
        host.getParametersFileContentTextArea().setText(host.getController().get_ParametersFileContent());
    }

    private static void applyPanelParametersToController(Host host) throws Exception {
        host.getController().set_TRMParameters(host.getParametersPanel());
    }
}
