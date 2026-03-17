package es.ants.felixgm.trmsim_wsn.gui.export;


import java.awt.Component;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

public final class SimulationExportHelper {
    public interface ExportHost {
        boolean ensureSimulationDataAvailable(SimulationResultRepository repository);
        void exportEnergyGraph() throws Exception;
    }

    private interface ExportAction {
        void execute() throws Exception;
    }

    private static final class ExportOption {
        private final String label;
        private final ExportAction action;

        private ExportOption(String label, ExportAction action) {
            this.label = label;
            this.action = action;
        }
    }

    private SimulationExportHelper() {
    }

    public static void showExportDialog(Component owner, SimulationResultRepository repository, ExportHost host) throws Exception {
        ExportOption[] exportOptions = buildExportOptions(owner, repository, host);
        Object[] labels = buildOptionLabels(exportOptions);
        int choice = JOptionPane.showOptionDialog(
                owner,
                "Choose export format (" + repository.getResultCount() + " results available):",
                "Export Simulation Data",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                labels,
                labels[0]);
        if (choice >= 0 && choice < exportOptions.length) {
            exportOptions[choice].action.execute();
        }
    }

    private static ExportOption[] buildExportOptions(Component owner, SimulationResultRepository repository, ExportHost host) {
        List<ExportOption> exportOptions = new ArrayList<ExportOption>();
        exportOptions.add(new ExportOption("Energy Consumption Graph (PNG)", host::exportEnergyGraph));
        exportOptions.add(new ExportOption("Simple CSV", () -> exportIfDataAvailable(repository, host, () -> repository.exportToCSV(owner))));
        exportOptions.add(new ExportOption("Detailed CSV", () -> exportIfDataAvailable(repository, host, () -> repository.exportDetailedToCSV(owner))));
        exportOptions.add(new ExportOption("Formatted Text Report", () -> exportIfDataAvailable(repository, host, () -> repository.exportToFormattedText(owner))));
        exportOptions.add(new ExportOption("Formatted TSV (Excel-friendly)", () -> exportIfDataAvailable(repository, host, () -> repository.exportToFormattedTSV(owner))));
        exportOptions.add(new ExportOption("Energy Consumption (Summary)", () -> exportIfDataAvailable(repository, host, () -> repository.exportEnergyConsumption(owner))));
        exportOptions.add(new ExportOption("Energy Consumption - Text (Summary)", () -> exportIfDataAvailable(repository, host, () -> repository.exportEnergyConsumptionText(owner))));
        exportOptions.add(new ExportOption("--- NEW: Node Data CSV (All) ---", () -> exportIfDataAvailable(repository, host, () -> repository.exportNodeLevelCSV(owner))));
        exportOptions.add(new ExportOption("--- NEW: Node Energy CSV ---", () -> exportIfDataAvailable(repository, host, () -> repository.exportNodeLevelEnergyCSV(owner))));
        exportOptions.add(new ExportOption("--- NEW: Node Energy Text ---", () -> exportIfDataAvailable(repository, host, () -> repository.exportNodeLevelEnergyText(owner))));
        exportOptions.add(new ExportOption("--- NEW: Node Data Text (All) ---", () -> exportIfDataAvailable(repository, host, () -> repository.exportNodeLevelText(owner))));
        exportOptions.add(new ExportOption("Cancel", () -> {
        }));
        return exportOptions.toArray(new ExportOption[0]);
    }

    private static Object[] buildOptionLabels(ExportOption[] exportOptions) {
        Object[] labels = new Object[exportOptions.length];
        for (int i = 0; i < exportOptions.length; i++) {
            labels[i] = exportOptions[i].label;
        }
        return labels;
    }

    private static void exportIfDataAvailable(
            SimulationResultRepository repository,
            ExportHost host,
            ExportAction exportAction) throws Exception {
        if (host.ensureSimulationDataAvailable(repository)) {
            exportAction.execute();
        }
    }
}
