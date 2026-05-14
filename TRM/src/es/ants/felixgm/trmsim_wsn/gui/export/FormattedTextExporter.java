package es.ants.felixgm.trmsim_wsn.gui.export;

import es.ants.felixgm.trmsim_wsn.outcomes.*;
import java.awt.Component;
import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JOptionPane;

public class FormattedTextExporter {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DecimalFormat numberFormat = new DecimalFormat("0.######");
    private static final DecimalFormat percentageFormat = new DecimalFormat("0.00%");

    public static String exportToFormattedText(Component parentFrame, List<Outcome> outcomes, ExportRequest request) {
        try {
            if (!outcomes.isEmpty()) {
                String filename = request.resolveFilePath("formatted_report", ".txt");
                exportFormattedText(outcomes, filename);
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

    private static void exportFormattedText(List<Outcome> outcomes, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Header
            writer.println("=".repeat(80));
            writer.println("TRMSim-WSN SIMULATION RESULTS");
            writer.println("=".repeat(80));
            writer.println("Generated: " + dateFormat.format(new Date()));
            writer.println("Total simulations: " + outcomes.size());
            writer.println();

            // Summary section
            writeSummarySection(writer, outcomes);
            writer.println();

            // Detailed results
            writeDetailedResults(writer, outcomes);

            // Footer
            writer.println();
            writer.println("=".repeat(80));
            writer.println("END OF REPORT");
            writer.println("=".repeat(80));
        }
    }

    private static void writeSummarySection(PrintWriter writer, List<Outcome> outcomes) {
        writer.println("SUMMARY STATISTICS");
        writer.println("-".repeat(40));

        if (outcomes.isEmpty()) return;

        // Calculate averages
        double totalSatisfaction = 0;
        double totalPathLength = 0;
        double totalAccuracy = 0;
        int satisfiedCount = 0;

        Map<String, Integer> modelCounts = new HashMap<>();

        for (Outcome outcome : outcomes) {
            String modelType = outcome.getModelName().isEmpty()
                    ? outcome.getClass().getSimpleName().replace("Outcome", "").replace("EnergyConsumption", "")
                    : outcome.getModelName();
            modelCounts.put(modelType, modelCounts.getOrDefault(modelType, 0) + 1);

            if (outcome instanceof BasicOutcome) {
                BasicOutcome basic = (BasicOutcome) outcome;
                totalSatisfaction += basic.get_avgSatisfaction();
                totalPathLength += basic.get_avgPathLength();
                totalAccuracy += basic.get_avgSatisfaction();
            }

            if (outcome.get_satisfaction().isSatisfied()) {
                satisfiedCount++;
            }
        }

        writer.printf("%-25s: %d\n", "Total Simulations", outcomes.size());
        writer.printf("%-25s: %.2f%%\n", "Overall Satisfaction Rate",
                (satisfiedCount / (double) outcomes.size()) * 100);
        writer.printf("%-25s: %.4f\n", "Average Satisfaction",
                totalSatisfaction / outcomes.size());
        writer.printf("%-25s: %.2f\n", "Average Path Length",
                totalPathLength / outcomes.size());
        writer.printf("%-25s: %.2f%%\n", "Average Accuracy",
                (totalAccuracy / outcomes.size()) * 100);

        writer.println();
        writer.println("Models Used:");
        for (Map.Entry<String, Integer> entry : modelCounts.entrySet()) {
            String model = entry.getKey().replace("Outcome", "").replace("EnergyConsumption", "");
            writer.printf("  - %-20s: %d simulations\n", model, entry.getValue());
        }
    }

    private static void writeDetailedResults(PrintWriter writer, List<Outcome> outcomes) {
        writer.println("DETAILED SIMULATION RESULTS");
        writer.println("-".repeat(40));

        for (int i = 0; i < outcomes.size(); i++) {
            Outcome outcome = outcomes.get(i);
            writer.println();
            writer.println("Simulation #" + (i + 1));
            writer.println("~".repeat(30));

            writeOutcomeDetails(writer, outcome, i + 1);

            if (i < outcomes.size() - 1) {
                writer.println(); // Empty line between simulations
            }
        }
    }

    private static void writeOutcomeDetails(PrintWriter writer, Outcome outcome, int simNumber) {
        // Basic information
        writer.printf("%-20s: %s\n", "Timestamp", dateFormat.format(new Date()));
        String modelLabel = outcome.getModelName().isEmpty()
                ? outcome.getClass().getSimpleName().replace("Outcome", "").replace("EnergyConsumption", "")
                : outcome.getModelName();
        writer.printf("%-20s: %s\n", "Model Type", modelLabel);
        writer.printf("%-20s: %s\n", "Satisfaction",
                outcome.get_satisfaction().isSatisfied() ? "SATISFIED" : "NOT SATISFIED");

        // Basic outcome metrics
        if (outcome instanceof BasicOutcome) {
            BasicOutcome basic = (BasicOutcome) outcome;
            writer.printf("%-20s: %.4f\n", "Avg Satisfaction", basic.get_avgSatisfaction());
            writer.printf("%-20s: %.2f\n", "Avg Path Length", basic.get_avgPathLength());
        }

        // Energy consumption
        if (outcome instanceof EnergyConsumptionOutcome) {
            writer.println();
            writer.println("Energy Consumption:");
            EnergyConsumptionOutcome energy = (EnergyConsumptionOutcome) outcome;

            writer.printf("  %-18s: %12s\n", "Client", formatEnergy(energy.get_clientEnergyConsumption()));
            writer.printf("  %-18s: %12s\n", "Malicious Server", formatEnergy(energy.get_maliciousServerEnergyConsumption()));
            writer.printf("  %-18s: %12s\n", "Benevolent Server", formatEnergy(energy.get_benevolentServerEnergyConsumption()));
            writer.printf("  %-18s: %12s\n", "Relay Server", formatEnergy(energy.get_relayServerEnergyConsumption()));
            writer.printf("  %-18s: %12s\n", "Average Sensor", formatEnergy(energy.get_avgSensorEnergyConsumption()));

            // Model-specific energy consumption
            if (outcome instanceof EigenTrustEnergyConsumptionOutcome) {
                EigenTrustEnergyConsumptionOutcome eigen = (EigenTrustEnergyConsumptionOutcome) outcome;
                writer.printf("  %-18s: %12s\n", "Pre-Trusted Peer", formatEnergy(eigen.get_preTrustedPeerEnergyConsumption()));
            }

            if (outcome instanceof PowerTrustEnergyConsumptionOutcome) {
                PowerTrustEnergyConsumptionOutcome power = (PowerTrustEnergyConsumptionOutcome) outcome;
                writer.printf("  %-18s: %12s\n", "Power Node", formatEnergy(power.get_powerNodeEnergyConsumption()));
            }
        }

        // Fuzzy satisfaction
        if (outcome instanceof FuzzyOutcome) {
            writer.println();
            writer.println("Fuzzy Satisfaction Distribution:");
            FuzzyOutcome fuzzy = (FuzzyOutcome) outcome;

            String[] terms = {"Very High", "High", "Medium", "Low", "Very Low"};
            for (String term : terms) {
                int count = fuzzy.getSatisfactionCount().getOrDefault(term, 0);
                double percentage = fuzzy.getSatisfactionPercentage(term);
                writer.printf("  %-12s: %4d clients (%6.2f%%)\n",
                        term, count, percentage * 100);
            }
        }

        // Performance metrics section
        writer.println();
        writer.println("Performance Metrics:");
        if (outcome instanceof BasicOutcome) {
            BasicOutcome basic = (BasicOutcome) outcome;
            double accuracy = basic.get_avgSatisfaction() * 100;
            double efficiency = calculateEfficiency(outcome);

            writer.printf("  %-15s: %6.2f%%\n", "Accuracy", accuracy);
            writer.printf("  %-15s: %6.2f\n", "Path Length", basic.get_avgPathLength());
            writer.printf("  %-15s: %6.2f\n", "Efficiency", efficiency);

            // Rating
            String rating = getPerformanceRating(accuracy, efficiency);
            writer.printf("  %-15s: %s\n", "Overall Rating", rating);
        }
    }

    private static String formatEnergy(double energy) {
        if (energy == 0.0) return "0.0";

        if (Math.abs(energy) < 0.001 || Math.abs(energy) > 1000000) {
            // Scientific notation for very small/large numbers
            return String.format("%8.2e", energy);
        } else if (Math.abs(energy) < 1.0) {
            // 6 decimal places for small numbers
            return String.format("%12.6f", energy);
        } else {
            // 2 decimal places for normal numbers
            return String.format("%12.2f", energy);
        }
    }

    private static double calculateEfficiency(Outcome outcome) {
        // Simple efficiency metric based on satisfaction and energy
        double satisfaction = 0.0;
        double energy = 1.0; // default to avoid division by zero

        if (outcome instanceof BasicOutcome) {
            satisfaction = ((BasicOutcome) outcome).get_avgSatisfaction();
        }

        if (outcome instanceof EnergyConsumptionOutcome) {
            energy = ((EnergyConsumptionOutcome) outcome).get_avgSensorEnergyConsumption();
            if (energy == 0.0) energy = 1.0;
        }

        // Higher satisfaction and lower energy = better efficiency
        return satisfaction / energy;
    }

    private static String getPerformanceRating(double accuracy, double efficiency) {
        if (accuracy >= 90.0 && efficiency >= 0.8) return "EXCELLENT ★★★★";
        if (accuracy >= 80.0 && efficiency >= 0.6) return "GOOD ★★★";
        if (accuracy >= 70.0 && efficiency >= 0.4) return "FAIR ★★";
        if (accuracy >= 60.0) return "POOR ★";
        return "UNSATISFACTORY";
    }
}
