package es.ants.felixgm.trmsim_wsn.gui.export;

import es.ants.felixgm.trmsim_wsn.outcomes.Outcome;
import es.ants.felixgm.trmsim_wsn.outcomes.NodeMetric;
import java.awt.Component;
import javax.swing.*;
import java.io.*;
import java.util.Collection;
import java.util.List;

public class NodeLevelExporter {

    public static void exportNodeData(Component parentFrame, Collection<Outcome> outcomes, int exportType) {
        // exportType: 1=CSV All, 2=CSV Energy, 3=Text Energy, 4=Text All
        String title = "";
        String ext = "";

        switch(exportType) {
            case 1: title = "Export Detailed Node CSV (All Data)"; ext = ".csv"; break;
            case 2: title = "Export Detailed Node CSV (Energy Only)"; ext = ".csv"; break;
            case 3: title = "Export Detailed Node Text (Energy Only)"; ext = ".txt"; break;
            case 4: title = "Export Detailed Node Text (All Data)"; ext = ".txt"; break;
        }

        // ... (Zvyšok metódy ostáva rovnaký, len volá writeCSV/writeText) ...
        try {
            JFileChooser fileChooser = new JFileChooser("simulation_results/");
            fileChooser.setDialogTitle(title);

            if (fileChooser.showSaveDialog(parentFrame) == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filename.toLowerCase().endsWith(ext)) filename += ext;

                if (!outcomes.isEmpty()) {
                    if (exportType == 1 || exportType == 2) writeCSV(outcomes, filename, exportType == 2);
                    else writeText(outcomes, filename, exportType == 3);

                    JOptionPane.showMessageDialog(parentFrame, "Data exported to: " + filename);
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private static void writeCSV(Collection<Outcome> outcomes, String filename, boolean energyOnly) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // UPDATED HEADER
            writer.print("SimulationID,NodeID,NodeType,EnergyConsumed");
            if (!energyOnly) {
                writer.print(",TransmittedDistance,X,Y,NeighborsCount,Goodness"); // Pridané stĺpce
            }
            writer.println();

            int simId = 1;
            for (Outcome outcome : outcomes) {
                List<NodeMetric> metrics = outcome.getNodeMetrics();
                if (metrics != null) {
                    for (NodeMetric nm : metrics) {
                        writer.print(String.format("%d,%d,%s,%.6f",
                                simId, nm.getId(), nm.getType(), nm.getConsumedEnergy()));

                        if (!energyOnly) {
                            // Pridané hodnoty do riadku
                            writer.print(String.format(",%.6f,%.2f,%.2f,%d,%.2f",
                                    nm.getTransmittedDistance(),
                                    nm.getX(),
                                    nm.getY(),
                                    nm.getNeighborsCount(),
                                    nm.getGoodness()));
                        }
                        writer.println();
                    }
                }
                simId++;
            }
        }
    }

    private static void writeText(Collection<Outcome> outcomes, String filename, boolean energyOnly) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("TRMSim-WSN DETAILED NODE REPORT");

            int simId = 1;
            for (Outcome outcome : outcomes) {
                writer.println("Simulation #" + simId);
                writer.println("-".repeat(80));

                List<NodeMetric> metrics = outcome.getNodeMetrics();
                if (metrics != null && !metrics.isEmpty()) {
                    // UPDATED TABLE HEADER
                    if (energyOnly) {
                        writer.printf("  %-6s | %-18s | %12s", "ID", "Type", "Energy");
                    } else {
                        writer.printf("  %-6s | %-18s | %12s | %10s | %6s | %6s | %5s | %5s",
                                "ID", "Type", "Energy", "Dist.", "X", "Y", "Conn.", "Good");
                    }
                    writer.println();

                    for (NodeMetric nm : metrics) {
                        if (energyOnly) {
                            writer.printf("  %-6d | %-18s | %12.6f",
                                    nm.getId(), nm.getType(), nm.getConsumedEnergy());
                        } else {
                            // Pridané stĺpce do tabuľky
                            writer.printf("  %-6d | %-18s | %12.6f | %10.2f | %6.1f | %6.1f | %5d | %5.2f",
                                    nm.getId(),
                                    nm.getType(),
                                    nm.getConsumedEnergy(),
                                    nm.getTransmittedDistance(),
                                    nm.getX(),
                                    nm.getY(),
                                    nm.getNeighborsCount(),
                                    nm.getGoodness());
                        }
                        writer.println();
                    }
                }
                writer.println();
                simId++;
            }
        }
    }
}
