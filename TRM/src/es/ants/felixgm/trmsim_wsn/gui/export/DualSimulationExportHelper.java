package es.ants.felixgm.trmsim_wsn.gui.export;


import es.ants.felixgm.trmsim_wsn.SimulationSlot;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class DualSimulationExportHelper {
    public interface ExportHost {
        boolean ensureSimulationDataAvailable(SimulationSlot slot, SimulationResultRepository repository);
        String exportEnergyGraph(Component owner, SimulationSlot slot, ExportRequest request) throws Exception;
    }

    private interface ExportAction {
        String execute(SimulationSlot slot, SimulationResultRepository repository, ExportRequest request) throws Exception;
    }

    private static final class ExportOption {
        private final String label;
        private final boolean requiresSimulationData;
        private final ExportAction action;

        private ExportOption(String label, boolean requiresSimulationData, ExportAction action) {
            this.label = label;
            this.requiresSimulationData = requiresSimulationData;
            this.action = action;
        }
    }

    private DualSimulationExportHelper() {
    }

    public static void showExportDialog(
            Component owner,
            SimulationResultRepository primaryRepository,
            SimulationResultRepository secondaryRepository,
            ExportHost host) throws Exception {
        ExportOption[] exportOptions = buildExportOptions(owner, host);
        JPanel panel = new JPanel(new GridBagLayout());
        JComboBox<String> targetSelector = new JComboBox<String>(new String[] {
                "Simulation A",
                "Simulation B",
                "Both"
        });
        javax.swing.JTextField reportNameField = new javax.swing.JTextField(createDefaultReportName(), 24);
        javax.swing.JTextField directoryField = new javax.swing.JTextField(new File("simulation_results").getAbsolutePath(), 24);
        javax.swing.JCheckBox[] checkBoxes = new javax.swing.JCheckBox[exportOptions.length];

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 4, 4, 4);
        panel.add(new javax.swing.JLabel("Export target:"), gbc);

        gbc.gridy++;
        panel.add(targetSelector, gbc);

        gbc.gridy++;
        panel.add(new javax.swing.JLabel("Choose one or more export formats:"), gbc);

        for (int i = 0; i < exportOptions.length; i++) {
            gbc.gridy++;
            checkBoxes[i] = new javax.swing.JCheckBox(exportOptions[i].label, i == 1);
            panel.add(checkBoxes[i], gbc);
        }

        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(new javax.swing.JLabel("Report name:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(reportNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        panel.add(new javax.swing.JLabel("Target folder:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(directoryField, gbc);

        int choice = JOptionPane.showConfirmDialog(
                owner,
                panel,
                "Export Dual Simulation Data",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        List<ExportOption> selectedOptions = new ArrayList<ExportOption>();
        for (int i = 0; i < exportOptions.length; i++) {
            if (checkBoxes[i].isSelected()) {
                selectedOptions.add(exportOptions[i]);
            }
        }
        if (selectedOptions.isEmpty()) {
            JOptionPane.showMessageDialog(owner, "Select at least one export format.", "Export Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String reportName = sanitizeFileName(reportNameField.getText());
        if (reportName.isEmpty()) {
            JOptionPane.showMessageDialog(owner, "Report name cannot be empty.", "Export Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        File targetDirectory = new File(directoryField.getText().trim());
        if (!targetDirectory.exists() && !targetDirectory.mkdirs()) {
            JOptionPane.showMessageDialog(owner, "Unable to create target folder: " + targetDirectory.getAbsolutePath(), "Export Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> exportedFiles = new ArrayList<String>();
        String target = (String) targetSelector.getSelectedItem();
        if ("Simulation A".equals(target) || "Both".equals(target)) {
            exportForSlot(owner, SimulationSlot.PRIMARY, primaryRepository, host, selectedOptions, targetDirectory, reportName, exportedFiles);
        }
        if ("Simulation B".equals(target) || "Both".equals(target)) {
            exportForSlot(owner, SimulationSlot.SECONDARY, secondaryRepository, host, selectedOptions, targetDirectory, reportName, exportedFiles);
        }

        if (exportedFiles.isEmpty()) {
            JOptionPane.showMessageDialog(owner, "No files were exported.", "Export Result", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder message = new StringBuilder("Exported files:\n");
        for (String exportedFile : exportedFiles) {
            message.append("- ").append(exportedFile).append("\n");
        }
        JOptionPane.showMessageDialog(owner, message.toString().trim(), "Export Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void exportForSlot(
            Component owner,
            SimulationSlot slot,
            SimulationResultRepository repository,
            ExportHost host,
            List<ExportOption> selectedOptions,
            File targetDirectory,
            String reportName,
            List<String> exportedFiles) throws Exception {
        if (requiresSimulationData(selectedOptions) && !host.ensureSimulationDataAvailable(slot, repository)) {
            return;
        }
        ExportRequest request = new ExportRequest(targetDirectory, reportName + "_" + slotSuffix(slot));
        for (ExportOption exportOption : selectedOptions) {
            String exportedFile = exportOption.action.execute(slot, repository, request);
            if ((exportedFile != null) && !exportedFile.trim().isEmpty()) {
                exportedFiles.add(exportedFile);
            }
        }
    }

    private static ExportOption[] buildExportOptions(Component owner, ExportHost host) {
        List<ExportOption> exportOptions = new ArrayList<ExportOption>();
        exportOptions.add(new ExportOption("Energy Consumption Graph (PNG)", false,
                (slot, repository, request) -> host.exportEnergyGraph(owner, slot, request)));
        exportOptions.add(new ExportOption("Simple CSV", true,
                (slot, repository, request) -> repository.exportToCSV(owner, request)));
        exportOptions.add(new ExportOption("Detailed CSV", true,
                (slot, repository, request) -> repository.exportDetailedToCSV(owner, request)));
        exportOptions.add(new ExportOption("Formatted Text Report", true,
                (slot, repository, request) -> repository.exportToFormattedText(owner, request)));
        exportOptions.add(new ExportOption("Formatted TSV (Excel-friendly)", true,
                (slot, repository, request) -> repository.exportToFormattedTSV(owner, request)));
        exportOptions.add(new ExportOption("Energy Consumption (Summary)", true,
                (slot, repository, request) -> repository.exportEnergyConsumption(owner, request)));
        exportOptions.add(new ExportOption("Energy Consumption - Text (Summary)", true,
                (slot, repository, request) -> repository.exportEnergyConsumptionText(owner, request)));
        exportOptions.add(new ExportOption("Node Data CSV (All)", true,
                (slot, repository, request) -> repository.exportNodeLevelCSV(owner, request)));
        exportOptions.add(new ExportOption("Node Energy CSV", true,
                (slot, repository, request) -> repository.exportNodeLevelEnergyCSV(owner, request)));
        exportOptions.add(new ExportOption("Node Energy Text", true,
                (slot, repository, request) -> repository.exportNodeLevelEnergyText(owner, request)));
        exportOptions.add(new ExportOption("Node Data Text (All)", true,
                (slot, repository, request) -> repository.exportNodeLevelText(owner, request)));
        return exportOptions.toArray(new ExportOption[0]);
    }

    private static boolean requiresSimulationData(List<ExportOption> selectedOptions) {
        for (ExportOption selectedOption : selectedOptions) {
            if (selectedOption.requiresSimulationData) {
                return true;
            }
        }
        return false;
    }

    private static String sanitizeFileName(String value) {
        String sanitized = (value == null) ? "" : value.trim().replaceAll("[\\\\/:*?\"<>|]+", "_");
        sanitized = sanitized.replaceAll("\\s+", "_");
        sanitized = sanitized.replaceAll("_+", "_");
        sanitized = sanitized.replaceAll("^_+|_+$", "");
        return sanitized;
    }

    private static String createDefaultReportName() {
        return "dual_report_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }

    private static String slotSuffix(SimulationSlot slot) {
        return slot == SimulationSlot.PRIMARY ? "simulation_a" : "simulation_b";
    }
}
