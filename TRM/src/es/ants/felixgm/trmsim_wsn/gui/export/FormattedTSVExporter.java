package es.ants.felixgm.trmsim_wsn.gui.export;

import es.ants.felixgm.trmsim_wsn.outcomes.*;
import java.awt.Component;
import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JOptionPane;

public class FormattedTSVExporter {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DecimalFormat numberFormat = new DecimalFormat("0.#####");
    private static final DecimalFormat percentageFormat = new DecimalFormat("0.00%");
    private static final DecimalFormat scientificFormat = new DecimalFormat("0.###E0");

    public static String exportToFormattedTSV(Component parentFrame, List<Outcome> outcomes, ExportRequest request) {
        try {
            if (!outcomes.isEmpty()) {
                String filename = request.resolveFilePath("formatted_report", ".tsv");
                exportFormattedTSV(outcomes, filename);
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

    private static void exportFormattedTSV(List<Outcome> outcomes, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write informative header
            writer.println("TRMSim-WSN Simulation Results - Tab-Separated Values");
            writer.println("Generated: " + dateFormat.format(new Date()));
            writer.println("Total Records: " + outcomes.size());
            writer.println();

            // Define clean column headers
            String[] headers = {
                    "ID",
                    "Timestamp",
                    "Model",
                    "Satisfied",
                    "Avg_Satisfaction",
                    "Avg_Path_Length",
                    "Client_Energy",
                    "Malicious_Energy",
                    "Benevolent_Energy",
                    "Relay_Energy",
                    "Avg_Sensor_Energy",
                    "PreTrusted_Energy",
                    "PowerNode_Energy",
                    "VeryHigh_Count",
                    "High_Count",
                    "Medium_Count",
                    "Low_Count",
                    "VeryLow_Count",
                    "VeryHigh_%",
                    "High_%",
                    "Medium_%",
                    "Low_%",
                    "VeryLow_%"
            };

            // Write header row
            writer.println(String.join("\t", headers));

            // Write data rows
            for (int i = 0; i < outcomes.size(); i++) {
                Outcome outcome = outcomes.get(i);
                String[] row = createFormattedRow(i + 1, outcome);
                writer.println(String.join("\t", row));
            }

            // Write summary section
            writer.println();
            writer.println("SUMMARY:");
            writer.println("Total_Simulations\t" + outcomes.size());
            writer.println("Satisfied_Count\t" + countSatisfied(outcomes));
            writer.println("Satisfaction_Rate\t" + formatPercentage(countSatisfied(outcomes) / (double) outcomes.size()));

            // Model distribution
            Map<String, Integer> modelCounts = getModelDistribution(outcomes);
            writer.println("Model_Distribution:");
            for (Map.Entry<String, Integer> entry : modelCounts.entrySet()) {
                writer.println(entry.getKey() + "\t" + entry.getValue());
            }
        }
    }

    private static String[] createFormattedRow(int id, Outcome outcome) {
        return new String[] {
                // Basic info
                String.valueOf(id),
                dateFormat.format(new Date()),
                getModelName(outcome),
                outcome.get_satisfaction().isSatisfied() ? "YES" : "NO",

                // Satisfaction metrics
                formatDecimal(getAvgSatisfaction(outcome)),
                formatDecimal(getAvgPathLength(outcome)),

                // Energy consumption (formatted consistently)
                formatEnergy(getClientEnergy(outcome)),
                formatEnergy(getMaliciousServerEnergy(outcome)),
                formatEnergy(getBenevolentServerEnergy(outcome)),
                formatEnergy(getRelayServerEnergy(outcome)),
                formatEnergy(getAvgSensorEnergy(outcome)),
                formatEnergy(getPreTrustedPeerEnergy(outcome)),
                formatEnergy(getPowerNodeEnergy(outcome)),

                // Fuzzy counts
                String.valueOf(getVeryHighCount(outcome)),
                String.valueOf(getHighCount(outcome)),
                String.valueOf(getMediumCount(outcome)),
                String.valueOf(getLowCount(outcome)),
                String.valueOf(getVeryLowCount(outcome)),

                // Fuzzy percentages
                formatPercentage(getVeryHighPercentage(outcome)),
                formatPercentage(getHighPercentage(outcome)),
                formatPercentage(getMediumPercentage(outcome)),
                formatPercentage(getLowPercentage(outcome)),
                formatPercentage(getVeryLowPercentage(outcome))
        };
    }

    // Formátovacie pomocné metódy
    private static String formatDecimal(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) return "0.0";
        return numberFormat.format(value);
    }

    private static String formatPercentage(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) return "0.00%";
        return percentageFormat.format(value);
    }

    private static String formatEnergy(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value) || value == 0.0) {
            return "0.0";
        }

        // Pre veľmi malé alebo veľké čísla použijeme vedecký zápis
        if (Math.abs(value) < 0.0001 || Math.abs(value) > 10000) {
            return scientificFormat.format(value).toLowerCase();
        } else {
            return numberFormat.format(value);
        }
    }

    private static String getModelName(Outcome outcome) {
        String className = outcome.getClass().getSimpleName();
        // Odstráňme nepotrebné časti názvu triedy
        return className.replace("Outcome", "")
                .replace("EnergyConsumption", "")
                .replace("Basic", "Standard");
    }

    private static int countSatisfied(List<Outcome> outcomes) {
        int count = 0;
        for (Outcome outcome : outcomes) {
            if (outcome.get_satisfaction().isSatisfied()) {
                count++;
            }
        }
        return count;
    }

    private static Map<String, Integer> getModelDistribution(List<Outcome> outcomes) {
        Map<String, Integer> distribution = new HashMap<>();
        for (Outcome outcome : outcomes) {
            String modelName = getModelName(outcome);
            distribution.put(modelName, distribution.getOrDefault(modelName, 0) + 1);
        }
        return distribution;
    }

    // Helper methods na extrakciu hodnôt (rovnaké ako predtým)
    private static double getAvgSatisfaction(Outcome outcome) {
        try {
            if (outcome instanceof BasicOutcome) {
                return ((BasicOutcome) outcome).get_avgSatisfaction();
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private static double getAvgPathLength(Outcome outcome) {
        try {
            if (outcome instanceof BasicOutcome) {
                return ((BasicOutcome) outcome).get_avgPathLength();
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private static double getClientEnergy(Outcome outcome) {
        try {
            if (outcome instanceof EnergyConsumptionOutcome) {
                return ((EnergyConsumptionOutcome) outcome).get_clientEnergyConsumption();
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private static double getMaliciousServerEnergy(Outcome outcome) {
        try {
            if (outcome instanceof EnergyConsumptionOutcome) {
                return ((EnergyConsumptionOutcome) outcome).get_maliciousServerEnergyConsumption();
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private static double getBenevolentServerEnergy(Outcome outcome) {
        try {
            if (outcome instanceof EnergyConsumptionOutcome) {
                return ((EnergyConsumptionOutcome) outcome).get_benevolentServerEnergyConsumption();
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private static double getRelayServerEnergy(Outcome outcome) {
        try {
            if (outcome instanceof EnergyConsumptionOutcome) {
                return ((EnergyConsumptionOutcome) outcome).get_relayServerEnergyConsumption();
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private static double getAvgSensorEnergy(Outcome outcome) {
        try {
            if (outcome instanceof EnergyConsumptionOutcome) {
                return ((EnergyConsumptionOutcome) outcome).get_avgSensorEnergyConsumption();
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private static double getPreTrustedPeerEnergy(Outcome outcome) {
        try {
            if (outcome instanceof EigenTrustEnergyConsumptionOutcome) {
                return ((EigenTrustEnergyConsumptionOutcome) outcome).get_preTrustedPeerEnergyConsumption();
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private static double getPowerNodeEnergy(Outcome outcome) {
        try {
            if (outcome instanceof PowerTrustEnergyConsumptionOutcome) {
                return ((PowerTrustEnergyConsumptionOutcome) outcome).get_powerNodeEnergyConsumption();
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private static int getVeryHighCount(Outcome outcome) {
        try {
            if (outcome instanceof FuzzyOutcome) {
                HashMap<String, Integer> counts = ((FuzzyOutcome) outcome).getSatisfactionCount();
                return counts.getOrDefault("Very High", 0);
            }
        } catch (Exception e) {}
        return 0;
    }

    private static int getHighCount(Outcome outcome) {
        try {
            if (outcome instanceof FuzzyOutcome) {
                HashMap<String, Integer> counts = ((FuzzyOutcome) outcome).getSatisfactionCount();
                return counts.getOrDefault("High", 0);
            }
        } catch (Exception e) {}
        return 0;
    }

    private static int getMediumCount(Outcome outcome) {
        try {
            if (outcome instanceof FuzzyOutcome) {
                HashMap<String, Integer> counts = ((FuzzyOutcome) outcome).getSatisfactionCount();
                return counts.getOrDefault("Medium", 0);
            }
        } catch (Exception e) {}
        return 0;
    }

    private static int getLowCount(Outcome outcome) {
        try {
            if (outcome instanceof FuzzyOutcome) {
                HashMap<String, Integer> counts = ((FuzzyOutcome) outcome).getSatisfactionCount();
                return counts.getOrDefault("Low", 0);
            }
        } catch (Exception e) {}
        return 0;
    }

    private static int getVeryLowCount(Outcome outcome) {
        try {
            if (outcome instanceof FuzzyOutcome) {
                HashMap<String, Integer> counts = ((FuzzyOutcome) outcome).getSatisfactionCount();
                return counts.getOrDefault("Very Low", 0);
            }
        } catch (Exception e) {}
        return 0;
    }

    private static double getVeryHighPercentage(Outcome outcome) {
        try {
            if (outcome instanceof FuzzyOutcome) {
                return ((FuzzyOutcome) outcome).getSatisfactionPercentage("Very High");
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private static double getHighPercentage(Outcome outcome) {
        try {
            if (outcome instanceof FuzzyOutcome) {
                return ((FuzzyOutcome) outcome).getSatisfactionPercentage("High");
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private static double getMediumPercentage(Outcome outcome) {
        try {
            if (outcome instanceof FuzzyOutcome) {
                return ((FuzzyOutcome) outcome).getSatisfactionPercentage("Medium");
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private static double getLowPercentage(Outcome outcome) {
        try {
            if (outcome instanceof FuzzyOutcome) {
                return ((FuzzyOutcome) outcome).getSatisfactionPercentage("Low");
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private static double getVeryLowPercentage(Outcome outcome) {
        try {
            if (outcome instanceof FuzzyOutcome) {
                return ((FuzzyOutcome) outcome).getSatisfactionPercentage("Very Low");
            }
        } catch (Exception e) {}
        return 0.0;
    }
}
