package es.ants.felixgm.trmsim_wsn.gui.parameterpanels;

import es.ants.felixgm.trmsim_wsn.trm.TRMParameters;
import es.ants.felixgm.trmsim_wsn.trm.bayestrust.BayesTrust_Parameters;

/**
 * Parameters panel for BayesTrust.
 */
public class BayesTrust_ParametersPanel extends TRMParametersPanel {
    private javax.swing.JLabel priorAlphaLabel;
    private javax.swing.JTextField priorAlphaTextField;
    private javax.swing.JLabel priorBetaLabel;
    private javax.swing.JTextField priorBetaTextField;
    private javax.swing.JLabel directEvidenceWeightLabel;
    private javax.swing.JTextField directEvidenceWeightTextField;
    private javax.swing.JLabel witnessEvidenceWeightLabel;
    private javax.swing.JTextField witnessEvidenceWeightTextField;
    private javax.swing.JLabel pathLengthPenaltyLabel;
    private javax.swing.JTextField pathLengthPenaltyTextField;
    private javax.swing.JLabel selectionThresholdLabel;
    private javax.swing.JTextField selectionThresholdTextField;
    private javax.swing.JLabel windowSizeLabel;
    private javax.swing.JSpinner windowSizeSpinner;

    public BayesTrust_ParametersPanel() {
        initComponents();
    }

    private void initComponents() {
        priorAlphaLabel = new javax.swing.JLabel("Prior alpha");
        priorAlphaTextField = new javax.swing.JTextField("2.0");
        priorBetaLabel = new javax.swing.JLabel("Prior beta");
        priorBetaTextField = new javax.swing.JTextField("1.0");
        directEvidenceWeightLabel = new javax.swing.JLabel("Direct evidence weight");
        directEvidenceWeightTextField = new javax.swing.JTextField("1.5");
        witnessEvidenceWeightLabel = new javax.swing.JLabel("Witness evidence weight");
        witnessEvidenceWeightTextField = new javax.swing.JTextField("0.75");
        pathLengthPenaltyLabel = new javax.swing.JLabel("Path length penalty");
        pathLengthPenaltyTextField = new javax.swing.JTextField("0.12");
        selectionThresholdLabel = new javax.swing.JLabel("Selection threshold");
        selectionThresholdTextField = new javax.swing.JTextField("0.35");
        windowSizeLabel = new javax.swing.JLabel("Witness window size");
        windowSizeSpinner = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(20, 1, Integer.MAX_VALUE, 1));

        java.awt.Dimension fieldSize = new java.awt.Dimension(80, 25);
        priorAlphaTextField.setPreferredSize(fieldSize);
        priorBetaTextField.setPreferredSize(fieldSize);
        directEvidenceWeightTextField.setPreferredSize(fieldSize);
        witnessEvidenceWeightTextField.setPreferredSize(fieldSize);
        pathLengthPenaltyTextField.setPreferredSize(fieldSize);
        selectionThresholdTextField.setPreferredSize(fieldSize);
        windowSizeSpinner.setPreferredSize(new java.awt.Dimension(120, 25));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(priorAlphaLabel)
                        .addComponent(priorAlphaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(priorBetaLabel)
                        .addComponent(priorBetaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(directEvidenceWeightLabel)
                        .addComponent(directEvidenceWeightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(witnessEvidenceWeightLabel)
                        .addComponent(witnessEvidenceWeightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(pathLengthPenaltyLabel)
                        .addComponent(pathLengthPenaltyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(selectionThresholdLabel)
                        .addComponent(selectionThresholdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(windowSizeLabel)
                        .addComponent(windowSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(priorAlphaLabel)
                        .addComponent(priorAlphaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(priorBetaLabel)
                        .addComponent(priorBetaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(directEvidenceWeightLabel)
                        .addComponent(directEvidenceWeightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(witnessEvidenceWeightLabel)
                        .addComponent(witnessEvidenceWeightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(pathLengthPenaltyLabel)
                        .addComponent(pathLengthPenaltyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(selectionThresholdLabel)
                        .addComponent(selectionThresholdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(windowSizeLabel)
                        .addComponent(windowSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)));
    }

    @Override
    public TRMParameters get_TRMParameters() {
        BayesTrust_Parameters parameters = new BayesTrust_Parameters();
        parameters.set_priorAlpha(Double.parseDouble(priorAlphaTextField.getText()));
        parameters.set_priorBeta(Double.parseDouble(priorBetaTextField.getText()));
        parameters.set_directEvidenceWeight(Double.parseDouble(directEvidenceWeightTextField.getText()));
        parameters.set_witnessEvidenceWeight(Double.parseDouble(witnessEvidenceWeightTextField.getText()));
        parameters.set_pathLengthPenalty(Double.parseDouble(pathLengthPenaltyTextField.getText()));
        parameters.set_selectionThreshold(Double.parseDouble(selectionThresholdTextField.getText()));
        parameters.set_windowSize(((Integer) windowSizeSpinner.getValue()).intValue());
        return parameters;
    }

    @Override
    public void set_TRMParameters(TRMParameters trmParameters) {
        BayesTrust_Parameters parameters = (BayesTrust_Parameters) trmParameters;
        priorAlphaTextField.setText(String.valueOf(parameters.get_priorAlpha()));
        priorBetaTextField.setText(String.valueOf(parameters.get_priorBeta()));
        directEvidenceWeightTextField.setText(String.valueOf(parameters.get_directEvidenceWeight()));
        witnessEvidenceWeightTextField.setText(String.valueOf(parameters.get_witnessEvidenceWeight()));
        pathLengthPenaltyTextField.setText(String.valueOf(parameters.get_pathLengthPenalty()));
        selectionThresholdTextField.setText(String.valueOf(parameters.get_selectionThreshold()));
        windowSizeSpinner.setValue(Integer.valueOf(parameters.get_windowSize()));
    }

    @Override
    public void setEnabled(boolean enabled) {
        priorAlphaLabel.setEnabled(enabled);
        priorAlphaTextField.setEnabled(enabled);
        priorBetaLabel.setEnabled(enabled);
        priorBetaTextField.setEnabled(enabled);
        directEvidenceWeightLabel.setEnabled(enabled);
        directEvidenceWeightTextField.setEnabled(enabled);
        witnessEvidenceWeightLabel.setEnabled(enabled);
        witnessEvidenceWeightTextField.setEnabled(enabled);
        pathLengthPenaltyLabel.setEnabled(enabled);
        pathLengthPenaltyTextField.setEnabled(enabled);
        selectionThresholdLabel.setEnabled(enabled);
        selectionThresholdTextField.setEnabled(enabled);
        windowSizeLabel.setEnabled(enabled);
        windowSizeSpinner.setEnabled(enabled);
    }
}
