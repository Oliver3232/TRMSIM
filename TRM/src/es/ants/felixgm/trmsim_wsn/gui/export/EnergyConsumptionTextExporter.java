package es.ants.felixgm.trmsim_wsn.gui.export;

import es.ants.felixgm.trmsim_wsn.outcomes.*;
import java.awt.Component;
import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class EnergyConsumptionTextExporter {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String exportEnergyConsumptionText(Component parentFrame, Collection<Outcome> outcomes, ExportRequest request) {
        try {
            if (!outcomes.isEmpty()) {
                String filename = request.resolveFilePath("energy_summary", ".txt");
                exportPureEnergyText(outcomes, filename);
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

    private static void exportPureEnergyText(Collection<Outcome> outcomes, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // ===== SUMMARY SECTION - NA ZAČIATOK =====
            writer.println("PURE ENERGY CONSUMPTION REPORT");
            writer.println("==============================");
            writer.println("Generated: " + dateFormat.format(new Date()));
            writer.println("Total simulations: " + outcomes.size());
            writer.println();

            // Calculate averages
            EnergySummary summary = calculateEnergySummary(outcomes);

            writer.println("OVERALL AVERAGES:");
            writer.println("-----------------");
            writer.printf("Average Client Energy:           %12.6f%n", summary.avgClientEnergy);
            writer.printf("Average Malicious Server Energy: %12.6f%n", summary.avgMaliciousEnergy);
            writer.printf("Average Benevolent Server Energy:%12.6f%n", summary.avgBenevolentEnergy);
            writer.printf("Average Relay Server Energy:     %12.6f%n", summary.avgRelayEnergy);
            writer.printf("Average Sensor Energy:           %12.6f%n", summary.avgSensorEnergy);

            if (summary.avgPreTrustedEnergy > 0) {
                writer.printf("Average Pre-trusted Peer Energy:%12.6f%n", summary.avgPreTrustedEnergy);
            }

            if (summary.avgPowerNodeEnergy > 0) {
                writer.printf("Average Power Node Energy:       %12.6f%n", summary.avgPowerNodeEnergy);
            }

            writer.println();
            writer.println("MODEL DISTRIBUTION:");
            writer.println("-------------------");
            for (Map.Entry<String, Integer> entry : summary.modelCounts.entrySet()) {
                writer.printf("%-25s: %3d simulations%n", entry.getKey(), entry.getValue());
            }

            writer.println();
            writer.println("ENERGY CONSUMPTION RANGE:");
            writer.println("-------------------------");
            writer.printf("Minimum Client Energy:           %12.6f%n", summary.minClientEnergy);
            writer.printf("Maximum Client Energy:           %12.6f%n", summary.maxClientEnergy);
            writer.printf("Standard Deviation:              %12.6f%n", summary.stdDevClientEnergy);

            writer.println();
            writer.println("==================================================================================");
            writer.println();

            // ===== DETAILED DATA SECTION =====
            writer.println("DETAILED ENERGY CONSUMPTION DATA:");
            writer.println("=================================");
            writer.println();

            int counter = 1;
            for (Outcome outcome : outcomes) {
                writer.println("SIMULATION " + counter++ + ":");
                writer.println("Model: " + outcome.getClass().getSimpleName());
                writer.println("----------------------------------------");

                if (outcome instanceof EnergyConsumptionOutcome) {
                    EnergyConsumptionOutcome eco = (EnergyConsumptionOutcome) outcome;
                    writer.printf("Client Energy:                %12.6f%n", eco.get_clientEnergyConsumption());
                    writer.printf("Malicious Server Energy:      %12.6f%n", eco.get_maliciousServerEnergyConsumption());
                    writer.printf("Benevolent Server Energy:     %12.6f%n", eco.get_benevolentServerEnergyConsumption());
                    writer.printf("Relay Server Energy:          %12.6f%n", eco.get_relayServerEnergyConsumption());
                    writer.printf("Average Sensor Energy:        %12.6f%n", eco.get_avgSensorEnergyConsumption());
                }

                if (outcome instanceof EigenTrustEnergyConsumptionOutcome) {
                    EigenTrustEnergyConsumptionOutcome et = (EigenTrustEnergyConsumptionOutcome) outcome;
                    writer.printf("Pre-trusted Peer Energy:      %12.6f%n", et.get_preTrustedPeerEnergyConsumption());
                }

                if (outcome instanceof PowerTrustEnergyConsumptionOutcome) {
                    PowerTrustEnergyConsumptionOutcome pt = (PowerTrustEnergyConsumptionOutcome) outcome;
                    writer.printf("Power Node Energy:            %12.6f%n", pt.get_powerNodeEnergyConsumption());
                }

                writer.println();
                writer.println("========================================");
                writer.println();
            }

            // ===== FINAL SUMMARY =====
            writer.println();
            writer.println("FINAL SUMMARY");
            writer.println("=============");
            writer.println("Total simulations analyzed: " + outcomes.size());
            writer.println("Report contains ONLY energy consumption metrics");
            writer.println("All values represent transmitted distance per execution");
            writer.println("File generated by TRMSim-WSN Energy Consumption Analyzer");
        }
    }

    // Helper class to store summary statistics
    private static class EnergySummary {
        double avgClientEnergy = 0;
        double avgMaliciousEnergy = 0;
        double avgBenevolentEnergy = 0;
        double avgRelayEnergy = 0;
        double avgSensorEnergy = 0;
        double avgPreTrustedEnergy = 0;
        double avgPowerNodeEnergy = 0;
        double minClientEnergy = Double.MAX_VALUE;
        double maxClientEnergy = Double.MIN_VALUE;
        double stdDevClientEnergy = 0;
        Map<String, Integer> modelCounts = new HashMap<>();
    }

    private static EnergySummary calculateEnergySummary(Collection<Outcome> outcomes) {
        EnergySummary summary = new EnergySummary();
        int totalOutcomes = outcomes.size();

        // Variables for standard deviation calculation
        double sumClientEnergy = 0;
        double sumSquaredClientEnergy = 0;

        int preTrustedCount = 0;
        int powerNodeCount = 0;

        for (Outcome outcome : outcomes) {
            // Count model types
            String modelName = outcome.getClass().getSimpleName();
            summary.modelCounts.put(modelName, summary.modelCounts.getOrDefault(modelName, 0) + 1);

            // Extract energy values
            if (outcome instanceof EnergyConsumptionOutcome) {
                EnergyConsumptionOutcome eco = (EnergyConsumptionOutcome) outcome;

                double clientEnergy = eco.get_clientEnergyConsumption();
                summary.avgClientEnergy += clientEnergy;
                summary.avgMaliciousEnergy += eco.get_maliciousServerEnergyConsumption();
                summary.avgBenevolentEnergy += eco.get_benevolentServerEnergyConsumption();
                summary.avgRelayEnergy += eco.get_relayServerEnergyConsumption();
                summary.avgSensorEnergy += eco.get_avgSensorEnergyConsumption();

                // For min/max and std dev
                if (clientEnergy < summary.minClientEnergy) summary.minClientEnergy = clientEnergy;
                if (clientEnergy > summary.maxClientEnergy) summary.maxClientEnergy = clientEnergy;
                sumClientEnergy += clientEnergy;
                sumSquaredClientEnergy += clientEnergy * clientEnergy;
            }

            if (outcome instanceof EigenTrustEnergyConsumptionOutcome) {
                EigenTrustEnergyConsumptionOutcome et = (EigenTrustEnergyConsumptionOutcome) outcome;
                summary.avgPreTrustedEnergy += et.get_preTrustedPeerEnergyConsumption();
                preTrustedCount++;
            }

            if (outcome instanceof PowerTrustEnergyConsumptionOutcome) {
                PowerTrustEnergyConsumptionOutcome pt = (PowerTrustEnergyConsumptionOutcome) outcome;
                summary.avgPowerNodeEnergy += pt.get_powerNodeEnergyConsumption();
                powerNodeCount++;
            }
        }

        // Calculate averages
        if (totalOutcomes > 0) {
            summary.avgClientEnergy /= totalOutcomes;
            summary.avgMaliciousEnergy /= totalOutcomes;
            summary.avgBenevolentEnergy /= totalOutcomes;
            summary.avgRelayEnergy /= totalOutcomes;
            summary.avgSensorEnergy /= totalOutcomes;

            // Calculate standard deviation for client energy
            double mean = sumClientEnergy / totalOutcomes;
            double variance = (sumSquaredClientEnergy / totalOutcomes) - (mean * mean);
            summary.stdDevClientEnergy = Math.sqrt(variance);
        }

        if (preTrustedCount > 0) {
            summary.avgPreTrustedEnergy /= preTrustedCount;
        }

        if (powerNodeCount > 0) {
            summary.avgPowerNodeEnergy /= powerNodeCount;
        }

        // Handle edge cases for min/max
        if (summary.minClientEnergy == Double.MAX_VALUE) summary.minClientEnergy = 0;
        if (summary.maxClientEnergy == Double.MIN_VALUE) summary.maxClientEnergy = 0;

        return summary;
    }
}
