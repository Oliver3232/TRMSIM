package es.ants.felixgm.trmsim_wsn.gui.export;

import es.ants.felixgm.trmsim_wsn.SimulationSlot;
import es.ants.felixgm.trmsim_wsn.outcomes.*;
import java.awt.Component;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JOptionPane;

public class SimulationResultRepository {
    private static final Map<SimulationSlot, SimulationResultRepository> INSTANCES =
            new EnumMap<SimulationSlot, SimulationResultRepository>(SimulationSlot.class);
    private List<Outcome> simulationResults;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final SimulationSlot slot;

    private SimulationResultRepository(SimulationSlot slot) {
        this.slot = slot;
        simulationResults = new Vector<>();
        new File("simulation_results/").mkdirs();
    }

    public static SimulationResultRepository getInstance() {
        return getInstance(SimulationSlot.PRIMARY);
    }

    public static synchronized SimulationResultRepository getInstance(SimulationSlot slot) {
        SimulationResultRepository repository = INSTANCES.get(slot);
        if (repository == null) {
            repository = new SimulationResultRepository(slot);
            INSTANCES.put(slot, repository);
        }
        return repository;
    }

    public static synchronized Collection<SimulationResultRepository> getAllInstances() {
        List<SimulationResultRepository> repositories = new ArrayList<SimulationResultRepository>();
        for (SimulationSlot slot : SimulationSlot.values()) {
            repositories.add(getInstance(slot));
        }
        return repositories;
    }

    public static synchronized void clearAll() {
        for (SimulationResultRepository repository : getAllInstances()) {
            repository.clearRepository();
        }
    }

    public SimulationSlot getSlot() {
        return slot;
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

    public String exportToCSV(Component parentFrame, ExportRequest request) {
        try {
            if (!simulationResults.isEmpty()) {
                String filename = request.resolveFilePath("simulation_results", ".csv");
                Outcome.writeToFile(simulationResults, filename);
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

    public String exportDetailedToCSV(Component parentFrame, ExportRequest request) {
        try {
            if (!simulationResults.isEmpty()) {
                String filename = request.resolveFilePath("simulation_results_detailed", ".csv");
                exportDetailedCSV(filename);
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

    private void exportDetailedCSV(String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("SimulationID,Timestamp,ModelType,Satisfaction,AvgSatisfaction,AvgPathLength," +
                    "ClientEnergy,MaliciousServerEnergy,BenevolentServerEnergy,RelayServerEnergy,AvgSensorEnergy," +
                    "PreTrustedPeerEnergy,PowerNodeEnergy," +
                    "VeryHighCount,HighCount,MediumCount,LowCount,VeryLowCount," +
                    "VeryHighPercentage,HighPercentage,MediumPercentage,LowPercentage,VeryLowPercentage\n");

            int simulationID = 1;
            for (Outcome outcome : simulationResults) {
                String modelLabel = outcome.getModelName().isEmpty()
                        ? outcome.getClass().getSimpleName()
                        : outcome.getModelName();
                writer.write(String.format("%d,%s,%s,%s,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%d,%d,%d,%d,%d,%.4f,%.4f,%.4f,%.4f,%.4f\n",
                        simulationID++,
                        dateFormat.format(new Date()),
                        modelLabel,
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
    public String exportToFormattedText(Component parentFrame, ExportRequest request) {
        return FormattedTextExporter.exportToFormattedText(parentFrame, simulationResults, request);
    }
    public String exportToFormattedTSV(Component parentFrame, ExportRequest request) {
        return FormattedTSVExporter.exportToFormattedTSV(parentFrame, simulationResults, request);
    }
    public String exportEnergyConsumption(Component parentFrame, ExportRequest request) {
        return EnergyConsumptionExporter.exportEnergyConsumption(parentFrame, simulationResults, request);
    }
    public String exportEnergyConsumptionText(Component parentFrame, ExportRequest request) {
        return EnergyConsumptionTextExporter.exportEnergyConsumptionText(parentFrame, simulationResults, request);
    }


    public String exportNodeLevelCSV(Component parentFrame, ExportRequest request) {
        return NodeLevelExporter.exportNodeData(parentFrame, simulationResults, 1, request);
    }

    public String exportNodeLevelEnergyCSV(Component parentFrame, ExportRequest request) {
        return NodeLevelExporter.exportNodeData(parentFrame, simulationResults, 2, request);
    }

    public String exportNodeLevelEnergyText(Component parentFrame, ExportRequest request) {
        return NodeLevelExporter.exportNodeData(parentFrame, simulationResults, 3, request);
    }

    public String exportNodeLevelText(Component parentFrame, ExportRequest request) {
        return NodeLevelExporter.exportNodeData(parentFrame, simulationResults, 4, request);
    }

    public String exportCharts(Component parentFrame, ExportRequest request) {
        return SimulationChartExporter.exportCharts(parentFrame, simulationResults, request);
    }
}
