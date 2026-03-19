package es.ants.felixgm.trmsim_wsn.gui.export;

import es.ants.felixgm.trmsim_wsn.outcomes.Outcome;
import es.ants.felixgm.trmsim_wsn.outcomes.NodeMetric;
import java.awt.Component;
import javax.swing.*;
import java.io.*;
import java.util.Collection;
import java.util.List;

public class NodeLevelExporter {

    public static String exportNodeData(Component parentFrame, Collection<Outcome> outcomes, int exportType, ExportRequest request) {
        // exportType: 1=CSV All, 2=CSV Energy, 3=Text Energy, 4=Text All
        String ext = "";
        String fileName = "";

        switch(exportType) {
            case 1: ext = ".csv"; fileName = "nodes_all"; break;
            case 2: ext = ".csv"; fileName = "nodes_energy"; break;
            case 3: ext = ".txt"; fileName = "nodes_energy"; break;
            case 4: ext = ".txt"; fileName = "nodes_all"; break;
        }

        try {
            if (!outcomes.isEmpty()) {
                String filename = request.resolveFilePath(fileName, ext);
                if (exportType == 1 || exportType == 2) writeCSV(outcomes, filename, exportType == 2);
                else writeText(outcomes, filename, exportType == 3);
                return filename;
            }
            JOptionPane.showMessageDialog(parentFrame, "No simulation data available for export");
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
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
