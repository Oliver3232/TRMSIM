package es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers;


import es.ants.felixgm.trmsim_wsn.gui.MainWindowContext;
import es.ants.felixgm.trmsim_wsn.gui.TRMSim_WSN;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public final class MainWindowMenuSetupController {
    private MainWindowMenuSetupController() {
    }

    public static void install(TRMSim_WSN window,
                               JMenuBar menuBar,
                               JMenu wsnMenu,
                               JMenuItem newWSNmenuItem,
                               JMenuItem resetWSNmenuItem,
                               JMenuItem loadWSNmenuItem,
                               JMenuItem saveWSNmenuItem,
                               JMenu simulationsMenu,
                               JMenuItem runTRMmenuItem,
                               JMenuItem stopTRMmenuItem,
                               JMenuItem runSimulationsMenuItem,
                               JMenuItem stopSimulationsMenuItem,
                               JMenuItem exportDataMenuItem,
                               JMenu parametersMenu,
                               JMenuItem loadParametersMenuItem,
                               JMenuItem saveParametersMenuItem,
                               JMenuItem applyParametersChangesMenuItem,
                               JMenu TRModelMenu,
                               JMenu helpMenu,
                               JMenuItem helpMenuItem,
                               JMenuItem aboutTRMSim_WSNmenuItem) {
        menuBar.setPreferredSize(new java.awt.Dimension(300, 20));

        wsnMenu.setText("WSN");

        newWSNmenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newWSNmenuItem.setIcon(new ImageIcon(ClassLoader.getSystemResource("resources/images/new.gif")));
        newWSNmenuItem.setText("New WSN");
        newWSNmenuItem.addActionListener(evt -> MainWindowActionController.createNewNetwork(window));
        wsnMenu.add(newWSNmenuItem);

        resetWSNmenuItem.setIcon(new ImageIcon(ClassLoader.getSystemResource("resources/images/reset.gif")));
        resetWSNmenuItem.setText("Reset WSN");
        resetWSNmenuItem.setEnabled(false);
        resetWSNmenuItem.addActionListener(evt -> MainWindowActionController.resetCurrentNetwork(window));
        wsnMenu.add(resetWSNmenuItem);

        loadWSNmenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        loadWSNmenuItem.setIcon(new ImageIcon(ClassLoader.getSystemResource("resources/images/load.gif")));
        loadWSNmenuItem.setText("Load WSN");
        loadWSNmenuItem.addActionListener(evt -> MainWindowActionController.loadNetwork(window));
        wsnMenu.add(loadWSNmenuItem);

        saveWSNmenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveWSNmenuItem.setIcon(new ImageIcon(ClassLoader.getSystemResource("resources/images/save.gif")));
        saveWSNmenuItem.setText("Save WSN");
        saveWSNmenuItem.setEnabled(false);
        saveWSNmenuItem.addActionListener(evt -> MainWindowActionController.saveNetwork(window));
        wsnMenu.add(saveWSNmenuItem);

        menuBar.add(wsnMenu);

        simulationsMenu.setText("Simulations");

        runTRMmenuItem.setIcon(new ImageIcon(ClassLoader.getSystemResource("resources/images/run.gif")));
        runTRMmenuItem.setText("Run T&R Model");
        runTRMmenuItem.setEnabled(false);
        runTRMmenuItem.addActionListener(evt -> MainWindowActionController.runTrmMenu(window, evt));
        simulationsMenu.add(runTRMmenuItem);

        stopTRMmenuItem.setIcon(new ImageIcon(ClassLoader.getSystemResource("resources/images/stop.gif")));
        stopTRMmenuItem.setText("Stop T&R Model");
        stopTRMmenuItem.setEnabled(false);
        stopTRMmenuItem.addActionListener(evt -> MainWindowActionController.stopTrm(window));

        runSimulationsMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        runSimulationsMenuItem.setIcon(new ImageIcon(ClassLoader.getSystemResource("resources/images/run.gif")));
        runSimulationsMenuItem.setText("Run simulations");
        runSimulationsMenuItem.addActionListener(evt -> MainWindowActionController.runSimulationsMenu(window, evt));
        simulationsMenu.add(runSimulationsMenuItem);

        stopSimulationsMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        stopSimulationsMenuItem.setIcon(new ImageIcon(ClassLoader.getSystemResource("resources/images/stop.gif")));
        stopSimulationsMenuItem.setText("Stop simulations");
        stopSimulationsMenuItem.setEnabled(false);
        stopSimulationsMenuItem.addActionListener(evt -> MainWindowActionController.stopBatch(window));
        simulationsMenu.add(stopSimulationsMenuItem);

        exportDataMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        exportDataMenuItem.setIcon(new ImageIcon(ClassLoader.getSystemResource("resources/images/save.gif")));
        exportDataMenuItem.setText("Export Data");
        exportDataMenuItem.addActionListener(evt -> MainWindowRenderController.showExportDialog(new MainWindowContext(window)));
        simulationsMenu.add(exportDataMenuItem);

        menuBar.add(simulationsMenu);

        parametersMenu.setText("Parameters");

        loadParametersMenuItem.setIcon(new javax.swing.ImageIcon(window.getClass().getResource("/resources/images/load.gif")));
        loadParametersMenuItem.setText("Load parameters");
        loadParametersMenuItem.addActionListener(evt -> MainWindowActionController.loadParametersFile(window));
        parametersMenu.add(loadParametersMenuItem);

        saveParametersMenuItem.setIcon(new javax.swing.ImageIcon(window.getClass().getResource("/resources/images/save.gif")));
        saveParametersMenuItem.setText("Save parameters");
        saveParametersMenuItem.addActionListener(evt -> MainWindowActionController.saveParametersFile(window));
        parametersMenu.add(saveParametersMenuItem);

        applyParametersChangesMenuItem.setIcon(new javax.swing.ImageIcon(window.getClass().getResource("/resources/images/apply.gif")));
        applyParametersChangesMenuItem.setText("Apply changes");
        applyParametersChangesMenuItem.setEnabled(false);
        applyParametersChangesMenuItem.addActionListener(evt -> MainWindowActionController.applyParametersChanges(window));
        parametersMenu.add(applyParametersChangesMenuItem);

        menuBar.add(parametersMenu);

        TRModelMenu.setText("T&R Model");
        menuBar.add(TRModelMenu);

        helpMenu.setText("Help");

        helpMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        helpMenuItem.setIcon(new ImageIcon(ClassLoader.getSystemResource("resources/images/help.gif")));
        helpMenuItem.setText("Help");
        helpMenuItem.addActionListener(evt -> MainWindowActionController.showHelp());
        helpMenu.add(helpMenuItem);

        aboutTRMSim_WSNmenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        aboutTRMSim_WSNmenuItem.setIcon(new ImageIcon(ClassLoader.getSystemResource("resources/images/about.gif")));
        aboutTRMSim_WSNmenuItem.setText("About TRMSim-WSN");
        aboutTRMSim_WSNmenuItem.addActionListener(evt -> MainWindowActionController.showAbout());
        helpMenu.add(aboutTRMSim_WSNmenuItem);

        menuBar.add(helpMenu);
        window.setJMenuBar(menuBar);
    }
}
