package es.ants.felixgm.trmsim_wsn.gui;

import es.ants.felixgm.trmsim_wsn.network.Sensor;
import java.awt.Point;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

final class MainWindowActionController {
    private MainWindowActionController() {
    }

    static void showAbout() {
        AboutWindow aboutWindow = new AboutWindow();
        aboutWindow.setVisible(true);
    }

    static void showHelp() {
        HelpWindow helpWindow = new HelpWindow();
        helpWindow.setVisible(true);
    }

    static void runSimulationsMenu(TRMSim_WSN window, java.awt.event.ActionEvent evt) {
        try {
            window.prepareEditableParametersForExecution();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(window, ex, "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        runBatch(window, evt);
    }

    static void runTrmMenu(TRMSim_WSN window, java.awt.event.ActionEvent evt) {
        try {
            window.prepareEditableParametersForExecution();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(window, ex, "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        runSingle(window, evt);
    }

    static void stopTrm(TRMSim_WSN window) {
        try {
            MainWindowSimulationController.stopSingleSimulation(window);
        } catch (Exception ex) {
            MainWindowSimulationController.showError(window, ex);
        }
    }

    static void stopBatch(TRMSim_WSN window) {
        try {
            MainWindowSimulationController.stopBatchSimulation(window);
        } catch (Exception ex) {
            MainWindowSimulationController.showError(window, ex);
        }
    }

    static void loadNetwork(TRMSim_WSN window) {
        try {
            MainWindowSimulationController.loadNetwork(window);
        } catch (Exception ex) {
            MainWindowSimulationController.showError(window, ex);
        }
    }

    static void saveNetwork(TRMSim_WSN window) {
        try {
            MainWindowSimulationController.saveNetwork(window);
        } catch (Exception ex) {
            MainWindowSimulationController.showError(window, ex);
        }
    }

    static void syncSliderValue(javax.swing.JSlider slider, javax.swing.JTextField field) {
        UiStateHelper.syncSliderValue(slider, field);
    }

    static void runBatch(TRMSim_WSN window, java.awt.event.ActionEvent evt) {
        try {
            MainWindowSimulationController.runBatch(window);
        } catch (Exception ex) {
            MainWindowSimulationController.showError(window, ex);
            stopBatch(window);
        }
    }

    static void toggleDisplay(TRMSim_WSN window, javax.swing.JCheckBox checkBox, String label) {
        try {
            MainWindowRenderController.toggleDisplay(window, checkBox, label);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(window, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    static void loadParametersFile(TRMSim_WSN window) {
        try {
            MainWindowParametersController.selectAndLoadParametersFile(MainWindowHosts.parameters(window));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(window, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    static void runSingle(TRMSim_WSN window, java.awt.event.ActionEvent evt) {
        try {
            MainWindowSimulationController.runSingle(window);
        } catch (Exception ex) {
            MainWindowSimulationController.showError(window, ex);
            stopTrm(window);
        }
    }

    static void resetCurrentNetwork(TRMSim_WSN window) {
        try {
            MainWindowSimulationController.resetCurrentNetwork(window);
        } catch (Exception ex) {
            MainWindowSimulationController.showError(window, ex);
        }
    }

    static void createNewNetwork(TRMSim_WSN window) {
        try {
            MainWindowSimulationController.createNewNetwork(window);
        } catch (Exception ex) {
            MainWindowSimulationController.showError(window, ex);
        }
    }

    static void saveParametersFile(TRMSim_WSN window) {
        try {
            MainWindowParametersController.selectAndSaveParametersFile(MainWindowHosts.parameters(window));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(window, ex, "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    static void applyParametersChanges(TRMSim_WSN window) {
        try {
            MainWindowParametersController.applyAndRefreshPanelParameters(MainWindowHosts.parameters(window));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(window, ex, "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    static void hideSensorProperties(TRMSim_WSN window) {
        window.sensorPropertiesPanel.setVisible(false);
    }

    static void selectNeighborOnDoubleClick(TRMSim_WSN window, java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            Sensor sensor = TRMSim_WSN.C.getSensor(Integer.valueOf((String) window.neighborsList.getSelectedValue()));
            if (sensor != null) {
                MainWindowNodeInspectorController.selectNodeById(MainWindowHosts.nodeInspector(window), sensor.id());
            }
        }
    }

    static void selectNodeFromNetworkPanel(TRMSim_WSN window, java.awt.event.MouseEvent evt) {
        try {
            Point point = SwingUtilities.convertPoint(window.networkPanelContainer, evt.getPoint(), window.networkPanel);
            Sensor sensor = window.networkPanel.getSensorAtPosition(point.x, point.y);
            if (sensor != null) {
                MainWindowNodeInspectorController.selectNodeById(MainWindowHosts.nodeInspector(window), sensor.id());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(window, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
