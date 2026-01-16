package es.ants.felixgm.trmsim_wsn.gui;

import es.ants.felixgm.trmsim_wsn.outcomes.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.Component;

public class SimulationResultRepository {
    // Odstranený static instance
    private List<Outcome> simulationResults;
    private String baseExportPath = "simulation_results/";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public SimulationResultRepository() {
        simulationResults = new Vector<>();
        new File(baseExportPath).mkdirs();
    }

    public void addOutcome(Outcome outcome) {
        simulationResults.add(outcome);
    }

    public void addAllOutcomes(Collection<Outcome> outcomes) {
        simulationResults.addAll(outcomes);
    }

    public void clearRepository() {
        simulationResults.clear();
    }

    public List<Outcome> getResults() {
        return new Vector<>(simulationResults);
    }

    public int getResultCount() {
        return simulationResults.size();
    }

    public void exportToCSV(Component parentComponent) {
        try {
            JFileChooser fileChooser = new JFileChooser(baseExportPath);
            fileChooser.setDialogTitle("Export Simulation Data to CSV");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter(){
                public boolean accept(java.io.File f) {
                    return (f.isDirectory() || f.getName().toLowerCase().endsWith(".csv"));
                }
                public String getDescription() { return "CSV Files (*.csv)"; }
            });

            if (fileChooser.showSaveDialog(parentComponent) == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filename.toLowerCase().endsWith(".csv")) {
                    filename += ".csv";
                }

                if (!simulationResults.isEmpty()) {
                    Outcome.writeToFile(simulationResults, filename);
                    JOptionPane.showMessageDialog(parentComponent,
                            "Data successfully exported to: " + filename,
                            "Export Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(parentComponent,
                            "No simulation data available for export",
                            "Export Failed",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentComponent,
                    "Error during export: " + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public void exportDetailedToCSV(Component parentComponent) {
        try {
            JFileChooser fileChooser = new JFileChooser(baseExportPath);
            fileChooser.setDialogTitle("Export Detailed Simulation Data to CSV");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter(){
                public boolean accept(java.io.File f) {
                    return (f.isDirectory() || f.getName().toLowerCase().endsWith(".csv"));
                }
                public String getDescription() { return "CSV Files (*.csv)"; }
            });

            if (fileChooser.showSaveDialog(parentComponent) == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filename.toLowerCase().endsWith(".csv")) {
                    filename += ".csv";
                }

                if (!simulationResults.isEmpty()) {
                    exportDetailedCSV(filename);
                    JOptionPane.showMessageDialog(parentComponent,
                            "Detailed data successfully exported to: " + filename,
                            "Export Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(parentComponent,
                            "No simulation data available for export",
                            "Export Failed",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentComponent,
                    "Error during export: " + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void exportDetailedCSV(String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("SimulationID,Timestamp,ModelType,Satisfaction,AvgSatisfaction,AvgPathLength," +
                    "ClientEnergy,MaliciousServerEnergy,BenevolentServerEnergy,RelayServerEnergy,AvgSensorEnergy," +
                    "PreTrustedPeerEnergy,PowerNodeEnergy," +
                    "VeryHighCount,HighCount,MediumCount,LowCount,VeryLowCount," +
                    "VeryHighPercentage,HighPercentage,MediumPercentage,LowPercentage,VeryLowPercentage\n");

            int simulationID = 1;
            for (Outcome outcome : simulationResults) {
                writer.write(String.format("%d,%s,%s,%s,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%d,%d,%d,%d,%d,%.4f,%.4f,%.4f,%.4f,%.4f\n",
                        simulationID++,
                        dateFormat.format(new Date()),
                        outcome.getClass().getSimpleName(),
                        outcome.get_satisfaction().isSatisfied(),
                        getAvgSatisfaction(outcome),
                        getAvgPathLength(outcome),
                        getClientEnergy(outcome),
                        getMaliciousServerEnergy(outcome),
                        getBenevolentServerEnergy(outcome),
                        getRelayServerEnergy(outcome),
                        getAvgSensorEnergy(outcome),
                        getPreTrustedPeerEnergy(outcome),
                        getPowerNodeEnergy(outcome),
                        getVeryHighCount(outcome),
                        getHighCount(outcome),
                        getMediumCount(outcome),
                        getLowCount(outcome),
                        getVeryLowCount(outcome),
                        getVeryHighPercentage(outcome),
                        getHighPercentage(outcome),
                        getMediumPercentage(outcome),
                        getLowPercentage(outcome),
                        getVeryLowPercentage(outcome)
                ));
            }
        }
    }

    // Helper metódy pre extrakciu dát (skrátené pre prehľadnosť, logika ostáva)
    private double getAvgSatisfaction(Outcome o) { try { if (o instanceof BasicOutcome) return ((BasicOutcome) o).get_avgSatisfaction(); } catch (Exception e) {} return 0.0; }
    private double getAvgPathLength(Outcome o) { try { if (o instanceof BasicOutcome) return ((BasicOutcome) o).get_avgPathLength(); } catch (Exception e) {} return 0.0; }
    private double getClientEnergy(Outcome o) { try { if (o instanceof EnergyConsumptionOutcome) return ((EnergyConsumptionOutcome) o).get_clientEnergyConsumption(); } catch (Exception e) {} return 0.0; }
    private double getMaliciousServerEnergy(Outcome o) { try { if (o instanceof EnergyConsumptionOutcome) return ((EnergyConsumptionOutcome) o).get_maliciousServerEnergyConsumption(); } catch (Exception e) {} return 0.0; }
    private double getBenevolentServerEnergy(Outcome o) { try { if (o instanceof EnergyConsumptionOutcome) return ((EnergyConsumptionOutcome) o).get_benevolentServerEnergyConsumption(); } catch (Exception e) {} return 0.0; }
    private double getRelayServerEnergy(Outcome o) { try { if (o instanceof EnergyConsumptionOutcome) return ((EnergyConsumptionOutcome) o).get_relayServerEnergyConsumption(); } catch (Exception e) {} return 0.0; }
    private double getAvgSensorEnergy(Outcome o) { try { if (o instanceof EnergyConsumptionOutcome) return ((EnergyConsumptionOutcome) o).get_avgSensorEnergyConsumption(); } catch (Exception e) {} return 0.0; }
    private double getPreTrustedPeerEnergy(Outcome o) { try { if (o instanceof EigenTrustEnergyConsumptionOutcome) return ((EigenTrustEnergyConsumptionOutcome) o).get_preTrustedPeerEnergyConsumption(); } catch (Exception e) {} return 0.0; }
    private double getPowerNodeEnergy(Outcome o) { try { if (o instanceof PowerTrustEnergyConsumptionOutcome) return ((PowerTrustEnergyConsumptionOutcome) o).get_powerNodeEnergyConsumption(); } catch (Exception e) {} return 0.0; }

    // Fuzzy outcomes helpers
    private int getVeryHighCount(Outcome o) { try { if (o instanceof FuzzyOutcome) return ((FuzzyOutcome) o).getSatisfactionCount().getOrDefault("Very High", 0); } catch (Exception e) {} return 0; }
    private int getHighCount(Outcome o) { try { if (o instanceof FuzzyOutcome) return ((FuzzyOutcome) o).getSatisfactionCount().getOrDefault("High", 0); } catch (Exception e) {} return 0; }
    private int getMediumCount(Outcome o) { try { if (o instanceof FuzzyOutcome) return ((FuzzyOutcome) o).getSatisfactionCount().getOrDefault("Medium", 0); } catch (Exception e) {} return 0; }
    private int getLowCount(Outcome o) { try { if (o instanceof FuzzyOutcome) return ((FuzzyOutcome) o).getSatisfactionCount().getOrDefault("Low", 0); } catch (Exception e) {} return 0; }
    private int getVeryLowCount(Outcome o) { try { if (o instanceof FuzzyOutcome) return ((FuzzyOutcome) o).getSatisfactionCount().getOrDefault("Very Low", 0); } catch (Exception e) {} return 0; }
    private double getVeryHighPercentage(Outcome o) { try { if (o instanceof FuzzyOutcome) return ((FuzzyOutcome) o).getSatisfactionPercentage("Very High"); } catch (Exception e) {} return 0.0; }
    private double getHighPercentage(Outcome o) { try { if (o instanceof FuzzyOutcome) return ((FuzzyOutcome) o).getSatisfactionPercentage("High"); } catch (Exception e) {} return 0.0; }
    private double getMediumPercentage(Outcome o) { try { if (o instanceof FuzzyOutcome) return ((FuzzyOutcome) o).getSatisfactionPercentage("Medium"); } catch (Exception e) {} return 0.0; }
    private double getLowPercentage(Outcome o) { try { if (o instanceof FuzzyOutcome) return ((FuzzyOutcome) o).getSatisfactionPercentage("Low"); } catch (Exception e) {} return 0.0; }
    private double getVeryLowPercentage(Outcome o) { try { if (o instanceof FuzzyOutcome) return ((FuzzyOutcome) o).getSatisfactionPercentage("Very Low"); } catch (Exception e) {} return 0.0; }

    public void exportToFormattedText(Component parentComponent) {
        // FormattedTextExporter.exportToFormattedText(parentComponent, simulationResults);
    }
    public void exportToFormattedTSV(Component parentComponent) {
        // FormattedTSVExporter.exportToFormattedTSV(parentComponent, simulationResults);
    }
    public void exportEnergyConsumption(Component parentComponent) {
        // EnergyConsumptionExporter.exportEnergyConsumption(parentComponent, simulationResults);
    }
    public void exportEnergyConsumptionText(Component parentComponent) {
        // EnergyConsumptionTextExporter.exportEnergyConsumptionText(parentComponent, simulationResults);
    }
}