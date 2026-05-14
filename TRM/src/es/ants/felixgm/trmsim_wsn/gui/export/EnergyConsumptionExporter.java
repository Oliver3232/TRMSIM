package es.ants.felixgm.trmsim_wsn.gui.export;

import es.ants.felixgm.trmsim_wsn.outcomes.*;
import java.awt.Component;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class EnergyConsumptionExporter {
    public static String exportEnergyConsumption(Component parentFrame, Collection<Outcome> outcomes, ExportRequest request) {
        try {
            if (!outcomes.isEmpty()) {
                String filename = request.resolveFilePath("energy_summary", ".csv");
                exportPureEnergyData(outcomes, filename);
                return filename;
            } else {
                JOptionPane.showMessageDialog(parentFrame,
                        "No simulation data available for export",
                        "Export Failed",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame,
                    "Error during export: " + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        return null;
    }

    private static void exportPureEnergyData(Collection<Outcome> outcomes, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write headers for PURE ENERGY CONSUMPTION only
            writer.println("ModelType,ClientEnergy,MaliciousServerEnergy,BenevolentServerEnergy,RelayServerEnergy,AvgSensorEnergy,PreTrustedPeerEnergy,PowerNodeEnergy");

            for (Outcome outcome : outcomes) {
                StringBuilder line = new StringBuilder();
                String modelLabel = outcome.getModelName().isEmpty()
                        ? outcome.getClass().getSimpleName()
                        : outcome.getModelName();
                line.append(modelLabel).append(",");

                // Extract ONLY energy consumption data
                double clientEnergy = 0.0;
                double maliciousEnergy = 0.0;
                double benevolentEnergy = 0.0;
                double relayEnergy = 0.0;
                double avgSensorEnergy = 0.0;
                double preTrustedEnergy = 0.0;
                double powerNodeEnergy = 0.0;

                // Basic energy metrics - available for all models
                if (outcome instanceof EnergyConsumptionOutcome) {
                    EnergyConsumptionOutcome eco = (EnergyConsumptionOutcome) outcome;
                    clientEnergy = eco.get_clientEnergyConsumption();
                    maliciousEnergy = eco.get_maliciousServerEnergyConsumption();
                    benevolentEnergy = eco.get_benevolentServerEnergyConsumption();
                    relayEnergy = eco.get_relayServerEnergyConsumption();
                    avgSensorEnergy = eco.get_avgSensorEnergyConsumption();
                }

                // EigenTrust specific energy
                if (outcome instanceof EigenTrustEnergyConsumptionOutcome) {
                    preTrustedEnergy = ((EigenTrustEnergyConsumptionOutcome) outcome).get_preTrustedPeerEnergyConsumption();
                }

                // PowerTrust specific energy
                if (outcome instanceof PowerTrustEnergyConsumptionOutcome) {
                    powerNodeEnergy = ((PowerTrustEnergyConsumptionOutcome) outcome).get_powerNodeEnergyConsumption();
                }

                line.append(String.format("%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f",
                        clientEnergy, maliciousEnergy, benevolentEnergy, relayEnergy, avgSensorEnergy, preTrustedEnergy, powerNodeEnergy));

                writer.println(line.toString());
            }
        }
    }
}
