package es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers;


import es.ants.felixgm.trmsim_wsn.gui.MainWindowContext;
import es.ants.felixgm.trmsim_wsn.gui.MainWindowHosts;
import es.ants.felixgm.trmsim_wsn.gui.TRMSim_WSN;
import es.ants.felixgm.trmsim_wsn.gui.support.UiStateHelper;
import es.ants.felixgm.trmsim_wsn.gui.windows.AboutWindow;
import es.ants.felixgm.trmsim_wsn.gui.windows.HelpWindow;
import es.ants.felixgm.trmsim_wsn.network.Sensor;
import javax.swing.JOptionPane;

public final class MainWindowActionController {
    private MainWindowActionController() {
    }

    public static void showAbout() {
        AboutWindow aboutWindow = new AboutWindow();
        aboutWindow.setVisible(true);
    }

    public static void showHelp() {
        HelpWindow helpWindow = new HelpWindow();
        helpWindow.setVisible(true);
    }

    public static void runSimulationsMenu(TRMSim_WSN window, java.awt.event.ActionEvent evt) {
        try {
            new MainWindowContext(window).prepareEditableParametersForExecution();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(window, ex, "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        runBatch(window, evt);
    }

    public static void runTrmMenu(TRMSim_WSN window, java.awt.event.ActionEvent evt) {
        MainWindowContext context = new MainWindowContext(window);
        if (context.isSingleSimulationActive()) {
            stopTrm(window);
            return;
        }
        try {
            context.prepareEditableParametersForExecution();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(window, ex, "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        runSingle(window, evt);
    }

    public static void stopTrm(TRMSim_WSN window) {
        try {
            MainWindowSimulationController.stopSingleSimulation(new MainWindowContext(window));
        } catch (Exception ex) {
            MainWindowSimulationController.showError(new MainWindowContext(window), ex);
        }
    }

    public static void stopBatch(TRMSim_WSN window) {
        try {
            MainWindowSimulationController.stopBatchSimulation(new MainWindowContext(window));
        } catch (Exception ex) {
            MainWindowSimulationController.showError(new MainWindowContext(window), ex);
        }
    }

    public static void loadNetwork(TRMSim_WSN window) {
        try {
            MainWindowSimulationController.loadNetwork(new MainWindowContext(window));
        } catch (Exception ex) {
            MainWindowSimulationController.showError(new MainWindowContext(window), ex);
        }
    }

    public static void saveNetwork(TRMSim_WSN window) {
        try {
            MainWindowSimulationController.saveNetwork(new MainWindowContext(window));
        } catch (Exception ex) {
            MainWindowSimulationController.showError(new MainWindowContext(window), ex);
        }
    }

    public static void syncSliderValue(javax.swing.JSlider slider, javax.swing.JTextField field) {
        UiStateHelper.syncSliderValue(slider, field);
    }

    public static void runBatch(TRMSim_WSN window, java.awt.event.ActionEvent evt) {
        try {
            MainWindowSimulationController.runBatch(new MainWindowContext(window));
        } catch (Exception ex) {
            MainWindowSimulationController.showError(new MainWindowContext(window), ex);
            stopBatch(window);
        }
    }

    public static void toggleDisplay(TRMSim_WSN window, javax.swing.JCheckBox checkBox, String label) {
        try {
            MainWindowRenderController.toggleDisplay(new MainWindowContext(window), checkBox, label);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(window, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void loadParametersFile(TRMSim_WSN window) {
        try {
            MainWindowParametersController.selectAndLoadParametersFile(MainWindowHosts.parameters(window));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(window, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void runSingle(TRMSim_WSN window, java.awt.event.ActionEvent evt) {
        MainWindowContext context = new MainWindowContext(window);
        if (context.isSingleSimulationActive()) {
            stopTrm(window);
            return;
        }
        try {
            MainWindowSimulationController.runSingle(context);
        } catch (Exception ex) {
            MainWindowSimulationController.showError(context, ex);
            stopTrm(window);
        }
    }

    public static void resetCurrentNetwork(TRMSim_WSN window) {
        try {
            MainWindowSimulationController.resetCurrentNetwork(new MainWindowContext(window));
        } catch (Exception ex) {
            MainWindowSimulationController.showError(new MainWindowContext(window), ex);
        }
    }

    public static void createNewNetwork(TRMSim_WSN window) {
        try {
            MainWindowSimulationController.createNewNetwork(new MainWindowContext(window));
        } catch (Exception ex) {
            MainWindowSimulationController.showError(new MainWindowContext(window), ex);
        }
    }

    public static void saveParametersFile(TRMSim_WSN window) {
        try {
            MainWindowParametersController.selectAndSaveParametersFile(MainWindowHosts.parameters(window));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(window, ex, "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void applyParametersChanges(TRMSim_WSN window) {
        try {
            MainWindowParametersController.applyAndRefreshPanelParameters(MainWindowHosts.parameters(window));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(window, ex, "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void hideSensorProperties(TRMSim_WSN window) {
        new MainWindowContext(window).hideSensorPropertiesPanel();
    }

    public static void selectNeighborOnDoubleClick(TRMSim_WSN window, java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            Sensor sensor = new MainWindowContext(window).getSelectedNeighborSensor();
            if (sensor != null) {
                new MainWindowContext(window).selectNodeById(sensor.id());
            }
        }
    }

    public static void selectNodeFromNetworkPanel(TRMSim_WSN window, java.awt.event.MouseEvent evt) {
        try {
            Sensor sensor = new MainWindowContext(window).getSensorAtNetworkPanelPoint(evt);
            if (sensor != null) {
                new MainWindowContext(window).selectNodeById(sensor.id());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(window, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
