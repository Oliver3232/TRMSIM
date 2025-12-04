package es.ants.felixgm.trmsim_wsn.gui;

import es.ants.felixgm.trmsim_wsn.outcomes.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class SimulationResultRepository {
    private static SimulationResultRepository instance;
    private List<Outcome> simulationResults;
    private String baseExportPath = "simulation_results/";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private SimulationResultRepository() {
        simulationResults = new Vector<>();
        new File(baseExportPath).mkdirs();
    }

    public static SimulationResultRepository getInstance() {
        if (instance == null) {
            instance = new SimulationResultRepository();
        }
        return instance;
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

    public void exportToCSV(TRMSim_WSN parentFrame) {
        try {
            JFileChooser fileChooser = new JFileChooser(baseExportPath);
            fileChooser.setDialogTitle("Export Simulation Data to CSV");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter(){
                public boolean accept(java.io.File f) {
                    return (f.isDirectory() || f.getName().toLowerCase().endsWith(".csv"));
                }
                public String getDescription() { return "CSV Files (*.csv)"; }
            });

            if (fileChooser.showSaveDialog(parentFrame) == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filename.toLowerCase().endsWith(".csv")) {
                    filename += ".csv";
                }

                if (!simulationResults.isEmpty()) {
                    Outcome.writeToFile(simulationResults, filename);
                    JOptionPane.showMessageDialog(parentFrame,
                            "Data successfully exported to: " + filename,
                            "Export Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(parentFrame,
                            "No simulation data available for export",
                            "Export Failed",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame,
                    "Error during export: " + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public void exportDetailedToCSV(TRMSim_WSN parentFrame) {
        try {
            JFileChooser fileChooser = new JFileChooser(baseExportPath);
            fileChooser.setDialogTitle("Export Detailed Simulation Data to CSV");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter(){
                public boolean accept(java.io.File f) {
                    return (f.isDirectory() || f.getName().toLowerCase().endsWith(".csv"));
                }
                public String getDescription() { return "CSV Files (*.csv)"; }
            });

            if (fileChooser.showSaveDialog(parentFrame) == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filename.toLowerCase().endsWith(".csv")) {
                    filename += ".csv";
                }

                if (!simulationResults.isEmpty()) {
                    exportDetailedCSV(filename);
                    JOptionPane.showMessageDialog(parentFrame,
                            "Detailed data successfully exported to: " + filename,
                            "Export Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(parentFrame,
                            "No simulation data available for export",
                            "Export Failed",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame,
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


    private double getAvgSatisfaction(Outcome outcome) {
        try {
            if (outcome instanceof BasicOutcome) {
                return ((BasicOutcome) outcome).get_avgSatisfaction();
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private double getAvgPathLength(Outcome outcome) {
        try {
            if (outcome instanceof BasicOutcome) {
                return ((BasicOutcome) outcome).get_avgPathLength();
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private double getClientEnergy(Outcome outcome) {
        try {
            if (outcome instanceof EnergyConsumptionOutcome) {
                return ((EnergyConsumptionOutcome) outcome).get_clientEnergyConsumption();
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private double getMaliciousServerEnergy(Outcome outcome) {
        try {
            if (outcome instanceof EnergyConsumptionOutcome) {
                return ((EnergyConsumptionOutcome) outcome).get_maliciousServerEnergyConsumption();
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private double getBenevolentServerEnergy(Outcome outcome) {
        try {
            if (outcome instanceof EnergyConsumptionOutcome) {
                return ((EnergyConsumptionOutcome) outcome).get_benevolentServerEnergyConsumption();
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private double getRelayServerEnergy(Outcome outcome) {
        try {
            if (outcome instanceof EnergyConsumptionOutcome) {
                return ((EnergyConsumptionOutcome) outcome).get_relayServerEnergyConsumption();
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private double getAvgSensorEnergy(Outcome outcome) {
        try {
            if (outcome instanceof EnergyConsumptionOutcome) {
                return ((EnergyConsumptionOutcome) outcome).get_avgSensorEnergyConsumption();
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private double getPreTrustedPeerEnergy(Outcome outcome) {
        try {
            if (outcome instanceof EigenTrustEnergyConsumptionOutcome) {
                return ((EigenTrustEnergyConsumptionOutcome) outcome).get_preTrustedPeerEnergyConsumption();
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private double getPowerNodeEnergy(Outcome outcome) {
        try {
            if (outcome instanceof PowerTrustEnergyConsumptionOutcome) {
                return ((PowerTrustEnergyConsumptionOutcome) outcome).get_powerNodeEnergyConsumption();
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private int getVeryHighCount(Outcome outcome) {
        try {
            if (outcome instanceof FuzzyOutcome) {
                HashMap<String, Integer> counts = ((FuzzyOutcome) outcome).getSatisfactionCount();
                return counts.getOrDefault("Very High", 0);
            }
        } catch (Exception e) {}
        return 0;
    }

    private int getHighCount(Outcome outcome) {
        try {
            if (outcome instanceof FuzzyOutcome) {
                HashMap<String, Integer> counts = ((FuzzyOutcome) outcome).getSatisfactionCount();
                return counts.getOrDefault("High", 0);
            }
        } catch (Exception e) {}
        return 0;
    }

    private int getMediumCount(Outcome outcome) {
        try {
            if (outcome instanceof FuzzyOutcome) {
                HashMap<String, Integer> counts = ((FuzzyOutcome) outcome).getSatisfactionCount();
                return counts.getOrDefault("Medium", 0);
            }
        } catch (Exception e) {}
        return 0;
    }

    private int getLowCount(Outcome outcome) {
        try {
            if (outcome instanceof FuzzyOutcome) {
                HashMap<String, Integer> counts = ((FuzzyOutcome) outcome).getSatisfactionCount();
                return counts.getOrDefault("Low", 0);
            }
        } catch (Exception e) {}
        return 0;
    }

    private int getVeryLowCount(Outcome outcome) {
        try {
            if (outcome instanceof FuzzyOutcome) {
                HashMap<String, Integer> counts = ((FuzzyOutcome) outcome).getSatisfactionCount();
                return counts.getOrDefault("Very Low", 0);
            }
        } catch (Exception e) {}
        return 0;
    }

    private double getVeryHighPercentage(Outcome outcome) {
        try {
            if (outcome instanceof FuzzyOutcome) {
                return ((FuzzyOutcome) outcome).getSatisfactionPercentage("Very High");
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private double getHighPercentage(Outcome outcome) {
        try {
            if (outcome instanceof FuzzyOutcome) {
                return ((FuzzyOutcome) outcome).getSatisfactionPercentage("High");
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private double getMediumPercentage(Outcome outcome) {
        try {
            if (outcome instanceof FuzzyOutcome) {
                return ((FuzzyOutcome) outcome).getSatisfactionPercentage("Medium");
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private double getLowPercentage(Outcome outcome) {
        try {
            if (outcome instanceof FuzzyOutcome) {
                return ((FuzzyOutcome) outcome).getSatisfactionPercentage("Low");
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private double getVeryLowPercentage(Outcome outcome) {
        try {
            if (outcome instanceof FuzzyOutcome) {
                return ((FuzzyOutcome) outcome).getSatisfactionPercentage("Very Low");
            }
        } catch (Exception e) {}
        return 0.0;
    }
    public void exportToFormattedText(TRMSim_WSN parentFrame) {
        FormattedTextExporter.exportToFormattedText(parentFrame, simulationResults);
    }
    public void exportToFormattedTSV(TRMSim_WSN parentFrame) {
        FormattedTSVExporter.exportToFormattedTSV(parentFrame, simulationResults);
    }
    public void exportEnergyConsumption(TRMSim_WSN parentFrame) {
        EnergyConsumptionExporter.exportEnergyConsumption(parentFrame, simulationResults);
    }
    public void exportEnergyConsumptionText(TRMSim_WSN parentFrame) {
        EnergyConsumptionTextExporter.exportEnergyConsumptionText(parentFrame, simulationResults);
    }


    public void exportNodeLevelCSV(TRMSim_WSN parentFrame) {
        NodeLevelExporter.exportNodeData(parentFrame, simulationResults, 1);
    }

    public void exportNodeLevelEnergyCSV(TRMSim_WSN parentFrame) {
        NodeLevelExporter.exportNodeData(parentFrame, simulationResults, 2);
    }

    public void exportNodeLevelEnergyText(TRMSim_WSN parentFrame) {
        NodeLevelExporter.exportNodeData(parentFrame, simulationResults, 3);
    }

    public void exportNodeLevelText(TRMSim_WSN parentFrame) {
        NodeLevelExporter.exportNodeData(parentFrame, simulationResults, 4);
    }
}