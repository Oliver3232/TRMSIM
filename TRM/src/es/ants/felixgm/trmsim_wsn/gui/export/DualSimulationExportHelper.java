package es.ants.felixgm.trmsim_wsn.gui.export;


import es.ants.felixgm.trmsim_wsn.SimulationSlot;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class DualSimulationExportHelper {
    public interface ExportHost {
        boolean ensureSimulationDataAvailable(SimulationSlot slot, SimulationResultRepository repository);
        String exportEnergyGraph(Component owner, SimulationSlot slot, ExportRequest request) throws Exception;
        SimulationSnapshot getSimulationSnapshot();
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
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        JTextField reportNameField = new JTextField(createDefaultReportName(), 24);
        JTextField directoryField = new JTextField(new File("simulation_results").getAbsolutePath(), 24);
        Map<SimulationSlot, JCheckBox[]> slotSelections = new EnumMap<SimulationSlot, JCheckBox[]>(SimulationSlot.class);

        JPanel documentsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        documentsPanel.setBorder(BorderFactory.createTitledBorder("Documents By Simulation"));
        JCheckBox[] primaryChecks = createExportOptionChecks(exportOptions, true);
        JCheckBox[] secondaryChecks = createExportOptionChecks(exportOptions, true);
        slotSelections.put(SimulationSlot.PRIMARY, primaryChecks);
        slotSelections.put(SimulationSlot.SECONDARY, secondaryChecks);
        documentsPanel.add(createSlotOptionsPanel("Sim A", primaryChecks));
        documentsPanel.add(createSlotOptionsPanel("Sim B", secondaryChecks));

        JPanel metadataPanel = new JPanel(new GridLayout(2, 2, 8, 6));
        metadataPanel.setBorder(BorderFactory.createTitledBorder("Export Metadata"));
        metadataPanel.add(new JLabel("Report name:"));
        metadataPanel.add(reportNameField);
        metadataPanel.add(new JLabel("Target folder:"));
        metadataPanel.add(directoryField);

        panel.add(documentsPanel, BorderLayout.CENTER);
        panel.add(metadataPanel, BorderLayout.SOUTH);

        int choice = JOptionPane.showConfirmDialog(
                owner,
                panel,
                "Export Dual Simulation Data",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        List<Selection> selectedSelections = collectSelectedSelections(slotSelections, exportOptions);
        if (selectedSelections.isEmpty()) {
            JOptionPane.showMessageDialog(owner, "Select at least one document to export.", "Export Validation", JOptionPane.WARNING_MESSAGE);
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

        host.getSimulationSnapshot().saveToDirectory(targetDirectory);

        List<String> exportedFiles = new ArrayList<String>();
        for (Selection selectedSelection : selectedSelections) {
            SimulationResultRepository repository = (selectedSelection.slot == SimulationSlot.PRIMARY)
                    ? primaryRepository
                    : secondaryRepository;
            exportSelection(owner, selectedSelection, repository, host, targetDirectory, reportName, exportedFiles);
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

    private static final class Selection {
        private final SimulationSlot slot;
        private final ExportOption exportOption;

        private Selection(SimulationSlot slot, ExportOption exportOption) {
            this.slot = slot;
            this.exportOption = exportOption;
        }
    }

    private static void exportSelection(
            Component owner,
            Selection planItem,
            SimulationResultRepository repository,
            ExportHost host,
            File targetDirectory,
            String reportName,
            List<String> exportedFiles) throws Exception {
        if (planItem.exportOption.requiresSimulationData && !host.ensureSimulationDataAvailable(planItem.slot, repository)) {
            return;
        }
        ExportRequest request = new ExportRequest(targetDirectory, reportName + "_" + slotSuffix(planItem.slot));
        String exportedFile = planItem.exportOption.action.execute(planItem.slot, repository, request);
        if ((exportedFile != null) && !exportedFile.trim().isEmpty()) {
            exportedFiles.add(exportedFile);
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
        exportOptions.add(new ExportOption("Charts (Satisfaction, Path Length, Energy) PNG", true,
                (slot, repository, request) -> repository.exportCharts(owner, request)));
        return exportOptions.toArray(new ExportOption[0]);
    }

    private static JCheckBox[] createExportOptionChecks(ExportOption[] exportOptions, boolean defaultSelection) {
        JCheckBox[] checkBoxes = new JCheckBox[exportOptions.length];
        for (int i = 0; i < exportOptions.length; i++) {
            checkBoxes[i] = new JCheckBox(exportOptions[i].label, defaultSelection);
            checkBoxes[i].setOpaque(false);
            checkBoxes[i].setMargin(new Insets(2, 2, 2, 2));
        }
        return checkBoxes;
    }

    private static JPanel createSlotOptionsPanel(String title, JCheckBox[] checkBoxes) {
        JPanel panel = new JPanel();
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        JPanel actionsPanel = new JPanel(new GridLayout(1, 2, 6, 0));
        actionsPanel.setOpaque(false);
        javax.swing.JButton selectAllButton = new javax.swing.JButton("Select all");
        javax.swing.JButton clearAllButton = new javax.swing.JButton("Clear all");
        selectAllButton.addActionListener(evt -> setAll(checkBoxes, true));
        clearAllButton.addActionListener(evt -> setAll(checkBoxes, false));
        actionsPanel.add(selectAllButton);
        actionsPanel.add(clearAllButton);
        panel.add(actionsPanel);
        for (JCheckBox checkBox : checkBoxes) {
            panel.add(checkBox);
        }
        return panel;
    }

    private static void setAll(JCheckBox[] checkBoxes, boolean selected) {
        for (JCheckBox checkBox : checkBoxes) {
            checkBox.setSelected(selected);
        }
    }

    private static List<Selection> collectSelectedSelections(
            Map<SimulationSlot, JCheckBox[]> slotSelections,
            ExportOption[] exportOptions) {
        List<Selection> selections = new ArrayList<Selection>();
        for (SimulationSlot slot : SimulationSlot.values()) {
            JCheckBox[] checkBoxes = slotSelections.get(slot);
            if (checkBoxes == null) {
                continue;
            }
            for (int i = 0; i < exportOptions.length; i++) {
                if (checkBoxes[i].isSelected()) {
                    selections.add(new Selection(slot, exportOptions[i]));
                }
            }
        }
        return selections;
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
