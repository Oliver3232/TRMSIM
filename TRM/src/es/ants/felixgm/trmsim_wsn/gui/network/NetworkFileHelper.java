package es.ants.felixgm.trmsim_wsn.gui.network;


import es.ants.felixgm.trmsim_wsn.app.SimulationApplicationService;
import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Service;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.io.File;

public final class NetworkFileHelper {
    public interface NetworkUiCallbacks {
        void paintNetwork(Network network, Service requiredService) throws Exception;
    }

    private NetworkFileHelper() {
    }

    public static File chooseXmlFile(Component parent, String startDirectory, String dialogTitle, int dialogType) {
        JFileChooser fileChooser = new JFileChooser(startDirectory);
        fileChooser.setDialogTitle(dialogTitle);
        fileChooser.setDialogType(dialogType);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(java.io.File file) {
                return file.isDirectory() || file.getName().toLowerCase().endsWith(".xml");
            }

            public String getDescription() {
                return "XML Files";
            }
        });
        if (dialogType == JFileChooser.OPEN_DIALOG) {
            fileChooser.showOpenDialog(parent);
        } else {
            fileChooser.showSaveDialog(parent);
        }
        return fileChooser.getSelectedFile();
    }

    public static Network loadSelectedNetwork(
            Component parent,
            SimulationApplicationService simulationService,
            String filePath) throws Exception {
        Network network = simulationService.loadNetwork(filePath);
        if (network != null) {
            JOptionPane.showMessageDialog(parent, "WSN loaded successfully", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
        return network;
    }

    public static void saveCurrentNetwork(
            Component parent,
            SimulationApplicationService simulationService,
            String filePath) throws Exception {
        simulationService.saveCurrentNetwork(filePath);
        JOptionPane.showMessageDialog(parent, "WSN saved successfully", "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}
