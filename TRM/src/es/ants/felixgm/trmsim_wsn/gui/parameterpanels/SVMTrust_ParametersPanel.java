package es.ants.felixgm.trmsim_wsn.gui.parameterpanels;

import es.ants.felixgm.trmsim_wsn.trm.TRMParameters;
import es.ants.felixgm.trmsim_wsn.trm.svmtrust.SVMTrust_Parameters;

/**
 * Parameters panel for SVMTrust.
 */
public class SVMTrust_ParametersPanel extends TRMParametersPanel {
    private javax.swing.JLabel learningRateLabel;
    private javax.swing.JTextField learningRateTextField;
    private javax.swing.JLabel regularizationLabel;
    private javax.swing.JTextField regularizationTextField;
    private javax.swing.JLabel directWeightLabel;
    private javax.swing.JTextField directWeightTextField;
    private javax.swing.JLabel witnessWeightLabel;
    private javax.swing.JTextField witnessWeightTextField;
    private javax.swing.JLabel normalizationLabel;
    private javax.swing.JTextField normalizationTextField;
    private javax.swing.JLabel pathPenaltyLabel;
    private javax.swing.JTextField pathPenaltyTextField;
    private javax.swing.JLabel thresholdLabel;
    private javax.swing.JTextField thresholdTextField;
    private javax.swing.JLabel windowSizeLabel;
    private javax.swing.JSpinner windowSizeSpinner;

    public SVMTrust_ParametersPanel() {
        initComponents();
    }

    private void initComponents() {
        learningRateLabel = new javax.swing.JLabel("Learning rate");
        learningRateTextField = new javax.swing.JTextField("0.2");
        regularizationLabel = new javax.swing.JLabel("Regularization");
        regularizationTextField = new javax.swing.JTextField("0.02");
        directWeightLabel = new javax.swing.JLabel("Direct evidence weight");
        directWeightTextField = new javax.swing.JTextField("1.5");
        witnessWeightLabel = new javax.swing.JLabel("Witness evidence weight");
        witnessWeightTextField = new javax.swing.JTextField("0.7");
        normalizationLabel = new javax.swing.JLabel("Evidence normalization");
        normalizationTextField = new javax.swing.JTextField("20.0");
        pathPenaltyLabel = new javax.swing.JLabel("Path length penalty");
        pathPenaltyTextField = new javax.swing.JTextField("0.12");
        thresholdLabel = new javax.swing.JLabel("Selection threshold");
        thresholdTextField = new javax.swing.JTextField("0.0");
        windowSizeLabel = new javax.swing.JLabel("Witness window size");
        windowSizeSpinner = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(20, 1, Integer.MAX_VALUE, 1));

        java.awt.Dimension fieldSize = new java.awt.Dimension(80, 25);
        learningRateTextField.setPreferredSize(fieldSize);
        regularizationTextField.setPreferredSize(fieldSize);
        directWeightTextField.setPreferredSize(fieldSize);
        witnessWeightTextField.setPreferredSize(fieldSize);
        normalizationTextField.setPreferredSize(fieldSize);
        pathPenaltyTextField.setPreferredSize(fieldSize);
        thresholdTextField.setPreferredSize(fieldSize);
        windowSizeSpinner.setPreferredSize(new java.awt.Dimension(120, 25));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup().addComponent(learningRateLabel).addComponent(learningRateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup().addComponent(regularizationLabel).addComponent(regularizationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup().addComponent(directWeightLabel).addComponent(directWeightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup().addComponent(witnessWeightLabel).addComponent(witnessWeightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup().addComponent(normalizationLabel).addComponent(normalizationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup().addComponent(pathPenaltyLabel).addComponent(pathPenaltyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup().addComponent(thresholdLabel).addComponent(thresholdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup().addComponent(windowSizeLabel).addComponent(windowSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER).addComponent(learningRateLabel).addComponent(learningRateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER).addComponent(regularizationLabel).addComponent(regularizationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER).addComponent(directWeightLabel).addComponent(directWeightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER).addComponent(witnessWeightLabel).addComponent(witnessWeightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER).addComponent(normalizationLabel).addComponent(normalizationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER).addComponent(pathPenaltyLabel).addComponent(pathPenaltyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER).addComponent(thresholdLabel).addComponent(thresholdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER).addComponent(windowSizeLabel).addComponent(windowSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)));
    }

    @Override
    public TRMParameters get_TRMParameters() {
        SVMTrust_Parameters parameters = new SVMTrust_Parameters();
        parameters.set_learningRate(Double.parseDouble(learningRateTextField.getText()));
        parameters.set_regularization(Double.parseDouble(regularizationTextField.getText()));
        parameters.set_directEvidenceWeight(Double.parseDouble(directWeightTextField.getText()));
        parameters.set_witnessEvidenceWeight(Double.parseDouble(witnessWeightTextField.getText()));
        parameters.set_evidenceNormalizationFactor(Double.parseDouble(normalizationTextField.getText()));
        parameters.set_pathLengthPenalty(Double.parseDouble(pathPenaltyTextField.getText()));
        parameters.set_selectionThreshold(Double.parseDouble(thresholdTextField.getText()));
        parameters.set_windowSize(((Integer) windowSizeSpinner.getValue()).intValue());
        return parameters;
    }

    @Override
    public void set_TRMParameters(TRMParameters trmParameters) {
        SVMTrust_Parameters parameters = (SVMTrust_Parameters) trmParameters;
        learningRateTextField.setText(String.valueOf(parameters.get_learningRate()));
        regularizationTextField.setText(String.valueOf(parameters.get_regularization()));
        directWeightTextField.setText(String.valueOf(parameters.get_directEvidenceWeight()));
        witnessWeightTextField.setText(String.valueOf(parameters.get_witnessEvidenceWeight()));
        normalizationTextField.setText(String.valueOf(parameters.get_evidenceNormalizationFactor()));
        pathPenaltyTextField.setText(String.valueOf(parameters.get_pathLengthPenalty()));
        thresholdTextField.setText(String.valueOf(parameters.get_selectionThreshold()));
        windowSizeSpinner.setValue(Integer.valueOf(parameters.get_windowSize()));
    }

    @Override
    public void setEnabled(boolean enabled) {
        learningRateLabel.setEnabled(enabled);
        learningRateTextField.setEnabled(enabled);
        regularizationLabel.setEnabled(enabled);
        regularizationTextField.setEnabled(enabled);
        directWeightLabel.setEnabled(enabled);
        directWeightTextField.setEnabled(enabled);
        witnessWeightLabel.setEnabled(enabled);
        witnessWeightTextField.setEnabled(enabled);
        normalizationLabel.setEnabled(enabled);
        normalizationTextField.setEnabled(enabled);
        pathPenaltyLabel.setEnabled(enabled);
        pathPenaltyTextField.setEnabled(enabled);
        thresholdLabel.setEnabled(enabled);
        thresholdTextField.setEnabled(enabled);
        windowSizeLabel.setEnabled(enabled);
        windowSizeSpinner.setEnabled(enabled);
    }
}
