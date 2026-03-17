package es.ants.felixgm.trmsim_wsn.gui.mainwindow.hosts;


import es.ants.felixgm.trmsim_wsn.gui.MainWindowContext;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowParametersController;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.TRMParametersPanel;

public final class MainWindowParametersHostFactory {
    private MainWindowParametersHostFactory() {
    }

    public static MainWindowParametersController.Host create(MainWindowContext context) {
        return new MainWindowParametersController.Host() {
            public es.ants.felixgm.trmsim_wsn.Controller getController() { return context.getController(); }
            public boolean isParametersFileSelected() { return context.isParametersFileSelected(); }
            public javax.swing.JLabel getParametersFileLabel() { return context.getParametersFileLabel(); }
            public javax.swing.JTextField getParametersFileTextField() { return context.getParametersFileTextField(); }
            public javax.swing.AbstractButton getBrowseButton() { return context.getBrowseButton(); }
            public javax.swing.JTextArea getParametersFileContentTextArea() { return context.getParametersFileContentTextArea(); }
            public javax.swing.AbstractButton getSaveParametersFileContentButton() { return context.getSaveParametersFileContentButton(); }
            public javax.swing.JMenuItem getLoadParametersMenuItem() { return context.getLoadParametersMenuItem(); }
            public javax.swing.JMenuItem getSaveParametersMenuItem() { return context.getSaveParametersMenuItem(); }
            public TRMParametersPanel getParametersPanel() { return context.getParametersPanel(); }
            public javax.swing.AbstractButton getApplyParametersChangesButton() { return context.getApplyParametersChangesButton(); }
            public javax.swing.JMenuItem getApplyParametersChangesMenuItem() { return context.getApplyParametersChangesMenuItem(); }
            public void updateParametersSourceView() { context.updateParametersSourceView(); }
        };
    }
}
