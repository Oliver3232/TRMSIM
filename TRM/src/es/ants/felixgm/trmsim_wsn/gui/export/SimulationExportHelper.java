package es.ants.felixgm.trmsim_wsn.gui.export;


import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public final class SimulationExportHelper {
    public interface ExportHost {
        boolean ensureSimulationDataAvailable(SimulationResultRepository repository);
        String exportEnergyGraph(Component owner, ExportRequest request) throws Exception;
    }

    private interface ExportAction {
        String execute(ExportRequest request) throws Exception;
    }

    private static final class ExportOption {
        private final String id;
        private final String label;
        private final boolean requiresSimulationData;
        private final ExportAction action;

        private ExportOption(String id, String label, boolean requiresSimulationData, ExportAction action) {
            this.id = id;
            this.label = label;
            this.requiresSimulationData = requiresSimulationData;
            this.action = action;
        }
    }

    private static final class ExportSelectionDialog {
        private final JPanel panel;
        private final JTextField reportNameField;
        private final JTextField directoryField;
        private final JCheckBox[] optionCheckBoxes;
        private final String autoDirectoryPrefix;
        private boolean directoryManuallyEdited;

        private ExportSelectionDialog(Component owner, ExportOption[] exportOptions) {
            panel = new JPanel(new GridBagLayout());
            reportNameField = new JTextField(createDefaultReportName(), 24);
            directoryField = new JTextField();
            optionCheckBoxes = new JCheckBox[exportOptions.length];
            autoDirectoryPrefix = new File("simulation_results").getAbsolutePath() + File.separator;

            directoryField.setText(buildDefaultDirectory(reportNameField.getText()));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 3;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(4, 4, 4, 4);
            panel.add(new JLabel("Choose one or more export formats:"), gbc);

            for (int i = 0; i < exportOptions.length; i++) {
                gbc.gridy++;
                JCheckBox checkBox = new JCheckBox(exportOptions[i].label, isDefaultSelected(exportOptions[i].id));
                optionCheckBoxes[i] = checkBox;
                panel.add(checkBox, gbc);
            }

            gbc.gridy++;
            gbc.gridwidth = 1;
            panel.add(new JLabel("Report name:"), gbc);

            gbc.gridx = 1;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(reportNameField, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 1;
            gbc.fill = GridBagConstraints.NONE;
            panel.add(new JLabel("Target folder:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(directoryField, gbc);

            gbc.gridx = 2;
            gbc.weightx = 0.0;
            gbc.fill = GridBagConstraints.NONE;
            JButton browseButton = new JButton("Browse...");
            browseButton.addActionListener(evt -> chooseDirectory(owner));
            panel.add(browseButton, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 3;
            panel.add(new JLabel("Files will use the report name as a shared base name inside the selected folder."), gbc);

            reportNameField.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) {
                    syncDirectoryFromReportName();
                }

                public void removeUpdate(DocumentEvent e) {
                    syncDirectoryFromReportName();
                }

                public void changedUpdate(DocumentEvent e) {
                    syncDirectoryFromReportName();
                }
            });

            directoryField.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) {
                    markManualDirectoryEdit();
                }

                public void removeUpdate(DocumentEvent e) {
                    markManualDirectoryEdit();
                }

                public void changedUpdate(DocumentEvent e) {
                    markManualDirectoryEdit();
                }
            });
        }

        private void chooseDirectory(Component owner) {
            JFileChooser chooser = new JFileChooser(directoryField.getText());
            chooser.setDialogTitle("Choose Export Folder");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            if (chooser.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {
                directoryManuallyEdited = true;
                directoryField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        }

        private void syncDirectoryFromReportName() {
            if (directoryManuallyEdited) {
                return;
            }
            directoryField.setText(buildDefaultDirectory(reportNameField.getText()));
        }

        private void markManualDirectoryEdit() {
            String directory = directoryField.getText().trim();
            String expected = buildDefaultDirectory(reportNameField.getText());
            directoryManuallyEdited = !directory.isEmpty() && !directory.equals(expected);
        }

        private String buildDefaultDirectory(String reportName) {
            return autoDirectoryPrefix + sanitizeFileName(reportName);
        }

        private static boolean isDefaultSelected(String optionId) {
            return "simple_csv".equals(optionId);
        }
    }

    private SimulationExportHelper() {
    }

    public static void showExportDialog(Component owner, SimulationResultRepository repository, ExportHost host) throws Exception {
        ExportOption[] exportOptions = buildExportOptions(owner, repository, host);
        ExportSelectionDialog dialog = new ExportSelectionDialog(owner, exportOptions);

        while (true) {
            int choice = JOptionPane.showConfirmDialog(
                    owner,
                    dialog.panel,
                    "Export Simulation Data (" + repository.getResultCount() + " results available)",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (choice != JOptionPane.OK_OPTION) {
                return;
            }

            List<ExportOption> selectedOptions = getSelectedOptions(exportOptions, dialog.optionCheckBoxes);
            if (selectedOptions.isEmpty()) {
                JOptionPane.showMessageDialog(owner,
                        "Select at least one export format.",
                        "Export Validation",
                        JOptionPane.WARNING_MESSAGE);
                continue;
            }

            String reportName = sanitizeFileName(dialog.reportNameField.getText());
            if (reportName.isEmpty()) {
                JOptionPane.showMessageDialog(owner,
                        "Report name cannot be empty.",
                        "Export Validation",
                        JOptionPane.WARNING_MESSAGE);
                continue;
            }

            File targetDirectory = new File(dialog.directoryField.getText().trim());
            if (targetDirectory.getPath().trim().isEmpty()) {
                JOptionPane.showMessageDialog(owner,
                        "Target folder cannot be empty.",
                        "Export Validation",
                        JOptionPane.WARNING_MESSAGE);
                continue;
            }

            if (requiresSimulationData(selectedOptions) && !host.ensureSimulationDataAvailable(repository)) {
                return;
            }

            if (!targetDirectory.exists() && !targetDirectory.mkdirs()) {
                JOptionPane.showMessageDialog(owner,
                        "Unable to create target folder: " + targetDirectory.getAbsolutePath(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
                continue;
            }

            ExportRequest request = new ExportRequest(targetDirectory, reportName);
            List<String> exportedFiles = new ArrayList<String>();
            for (ExportOption exportOption : selectedOptions) {
                String exportedFile = exportOption.action.execute(request);
                if ((exportedFile != null) && !exportedFile.trim().isEmpty()) {
                    exportedFiles.add(exportedFile);
                }
            }

            if (exportedFiles.isEmpty()) {
                JOptionPane.showMessageDialog(owner,
                        "No files were exported.",
                        "Export Result",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(owner,
                    buildSuccessMessage(exportedFiles),
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    }

    private static ExportOption[] buildExportOptions(Component owner, SimulationResultRepository repository, ExportHost host) {
        List<ExportOption> exportOptions = new ArrayList<ExportOption>();
        exportOptions.add(new ExportOption("energy_graph", "Energy Consumption Graph (PNG)", false, request -> host.exportEnergyGraph(owner, request)));
        exportOptions.add(new ExportOption("simple_csv", "Simple CSV", true, request -> repository.exportToCSV(owner, request)));
        exportOptions.add(new ExportOption("detailed_csv", "Detailed CSV", true, request -> repository.exportDetailedToCSV(owner, request)));
        exportOptions.add(new ExportOption("formatted_text", "Formatted Text Report", true, request -> repository.exportToFormattedText(owner, request)));
        exportOptions.add(new ExportOption("formatted_tsv", "Formatted TSV (Excel-friendly)", true, request -> repository.exportToFormattedTSV(owner, request)));
        exportOptions.add(new ExportOption("energy_csv", "Energy Consumption (Summary)", true, request -> repository.exportEnergyConsumption(owner, request)));
        exportOptions.add(new ExportOption("energy_text", "Energy Consumption - Text (Summary)", true, request -> repository.exportEnergyConsumptionText(owner, request)));
        exportOptions.add(new ExportOption("node_csv_all", "Node Data CSV (All)", true, request -> repository.exportNodeLevelCSV(owner, request)));
        exportOptions.add(new ExportOption("node_csv_energy", "Node Energy CSV", true, request -> repository.exportNodeLevelEnergyCSV(owner, request)));
        exportOptions.add(new ExportOption("node_text_energy", "Node Energy Text", true, request -> repository.exportNodeLevelEnergyText(owner, request)));
        exportOptions.add(new ExportOption("node_text_all", "Node Data Text (All)", true, request -> repository.exportNodeLevelText(owner, request)));
        return exportOptions.toArray(new ExportOption[0]);
    }

    private static List<ExportOption> getSelectedOptions(ExportOption[] exportOptions, JCheckBox[] checkBoxes) {
        List<ExportOption> selectedOptions = new ArrayList<ExportOption>();
        for (int i = 0; i < exportOptions.length; i++) {
            if (checkBoxes[i].isSelected()) {
                selectedOptions.add(exportOptions[i]);
            }
        }
        return selectedOptions;
    }

    private static boolean requiresSimulationData(List<ExportOption> selectedOptions) {
        for (ExportOption selectedOption : selectedOptions) {
            if (selectedOption.requiresSimulationData) {
                return true;
            }
        }
        return false;
    }

    private static String buildSuccessMessage(List<String> exportedFiles) {
        StringBuilder message = new StringBuilder("Exported files:\n");
        for (String exportedFile : exportedFiles) {
            message.append("- ").append(exportedFile).append("\n");
        }
        return message.toString().trim();
    }

    private static String sanitizeFileName(String value) {
        String sanitized = (value == null) ? "" : value.trim().replaceAll("[\\\\/:*?\"<>|]+", "_");
        sanitized = sanitized.replaceAll("\\s+", "_");
        sanitized = sanitized.replaceAll("_+", "_");
        sanitized = sanitized.replaceAll("^_+|_+$", "");
        return sanitized;
    }

    private static String createDefaultReportName() {
        return "report_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }
}
