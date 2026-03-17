package es.ants.felixgm.trmsim_wsn.gui.parameterpanels.lftm;

final class LFTMPanelComponentInitializer {
    private LFTMPanelComponentInitializer() {
    }

    static void initialize(LFTM_ParametersPanel panel) {
        
                panel.lftm_ParametersPanelTabbedPane = new javax.swing.JTabbedPane();
                panel.btrm_ParametersPanel = new javax.swing.JPanel();
                panel.phiLabel = new javax.swing.JLabel();
                panel.phiSlider = new javax.swing.JSlider();
                panel.rhoLabel = new javax.swing.JLabel();
                panel.rhoSlider = new javax.swing.JSlider();
                panel.q0Label = new javax.swing.JLabel();
                panel.q0Slider = new javax.swing.JSlider();
                panel.numAntsLabel = new javax.swing.JLabel();
                panel.numAntsSlider = new javax.swing.JSlider();
                panel.numIterationsLabel = new javax.swing.JLabel();
                panel.numIterationsSlider = new javax.swing.JSlider();
                panel.numIterationsTextField = new javax.swing.JTextField();
                panel.numAntsTextField = new javax.swing.JTextField();
                panel.q0TextField = new javax.swing.JTextField();
                panel.rhoTextField = new javax.swing.JTextField();
                panel.phiTextField = new javax.swing.JTextField();
                panel.alphaLabel = new javax.swing.JLabel();
                panel.alphaSlider = new javax.swing.JSlider();
                panel.alphaTextField = new javax.swing.JTextField();
                panel.betaLabel = new javax.swing.JLabel();
                panel.betaSlider = new javax.swing.JSlider();
                panel.betaTextField = new javax.swing.JTextField();
                panel.initialPheromoneLabel = new javax.swing.JLabel();
                panel.initialPheromoneSlider = new javax.swing.JSlider();
                panel.initialPheromoneTextField = new javax.swing.JTextField();
                panel.punishmentThresholdLabel = new javax.swing.JLabel();
                panel.punishmentThresholdSlider = new javax.swing.JSlider();
                panel.punishmentThresholdTextField = new javax.swing.JTextField();
                panel.pathLengthFactorLabel = new javax.swing.JLabel();
                panel.pathLengthFactorSlider = new javax.swing.JSlider();
                panel.pathLengthFactorTextField = new javax.swing.JTextField();
                panel.transitionThresholdLabel = new javax.swing.JLabel();
                panel.transitionThresholdSlider = new javax.swing.JSlider();
                panel.transitionThresholdTextField = new javax.swing.JTextField();
                panel.linguisticTermsPanel = new javax.swing.JPanel();
                panel.veryHighLabel = new javax.swing.JLabel();
                panel.highLabel = new javax.swing.JLabel();
                panel.mediumLabel = new javax.swing.JLabel();
                panel.lowLabel = new javax.swing.JLabel();
                panel.veryLowLabel = new javax.swing.JLabel();
                panel.veryHighMembershipFunctionComboBox = new javax.swing.JComboBox();
                panel.membershipFunctionLabel = new javax.swing.JLabel();
                panel.highMembershipFunctionComboBox = new javax.swing.JComboBox();
                panel.mediumMembershipFunctionComboBox = new javax.swing.JComboBox();
                panel.lowMembershipFunctionComboBox = new javax.swing.JComboBox();
                panel.veryLowMembershipFunctionComboBox = new javax.swing.JComboBox();
                panel.veryHighAParameterTextField = new javax.swing.JTextField();
                panel.veryHighBParameterTextField = new javax.swing.JTextField();
                panel.veryHighCParameterTextField = new javax.swing.JTextField();
                panel.veryHighDParameterTextField = new javax.swing.JTextField();
                panel.aParameterLabel = new javax.swing.JLabel();
                panel.bParameterLabel = new javax.swing.JLabel();
                panel.cParameterLabel = new javax.swing.JLabel();
                panel.dParameterLabel = new javax.swing.JLabel();
                panel.highAParameterTextField = new javax.swing.JTextField();
                panel.highBParameterTextField = new javax.swing.JTextField();
                panel.highCParameterTextField = new javax.swing.JTextField();
                panel.highDParameterTextField = new javax.swing.JTextField();
                panel.mediumDParameterTextField = new javax.swing.JTextField();
                panel.mediumCParameterTextField = new javax.swing.JTextField();
                panel.mediumBParameterTextField = new javax.swing.JTextField();
                panel.mediumAParameterTextField = new javax.swing.JTextField();
                panel.lowAParameterTextField = new javax.swing.JTextField();
                panel.lowCParameterTextField = new javax.swing.JTextField();
                panel.lowDParameterTextField = new javax.swing.JTextField();
                panel.lowBParameterTextField = new javax.swing.JTextField();
                panel.veryLowDParameterTextField = new javax.swing.JTextField();
                panel.veryLowCParameterTextField = new javax.swing.JTextField();
                panel.veryLowBParameterTextField = new javax.swing.JTextField();
                panel.veryLowAParameterTextField = new javax.swing.JTextField();
                panel.universeMINLabel = new javax.swing.JLabel();
                panel.universeMAXLabel = new javax.swing.JLabel();
                panel.universeMINTextField = new javax.swing.JTextField();
                panel.universeMAXTextField = new javax.swing.JTextField();
                panel.linguisticTermsPanelContainer = new javax.swing.JPanel();
        
                panel.setPreferredSize(new java.awt.Dimension(450, 500));
        
                panel.lftm_ParametersPanelTabbedPane.setPreferredSize(new java.awt.Dimension(450, 500));
        
                panel.phiLabel.setText("phi");
                panel.phiLabel.setEnabled(false);
        
                panel.phiSlider.setMaximum(10000);
                panel.phiSlider.setValue(1784);
                panel.phiSlider.setEnabled(false);
                panel.phiSlider.setPreferredSize(new java.awt.Dimension(140, 25));
                panel.phiSlider.addChangeListener(new javax.swing.event.ChangeListener() {
                    public void stateChanged(javax.swing.event.ChangeEvent evt) {
                        panel.phiSliderStateChanged(evt);
                    }
                });
        
                panel.rhoLabel.setText("rho");
                panel.rhoLabel.setEnabled(false);
        
                panel.rhoSlider.setMaximum(10000);
                panel.rhoSlider.setValue(3044);
                panel.rhoSlider.setEnabled(false);
                panel.rhoSlider.setPreferredSize(new java.awt.Dimension(140, 25));
                panel.rhoSlider.addChangeListener(new javax.swing.event.ChangeListener() {
                    public void stateChanged(javax.swing.event.ChangeEvent evt) {
                        panel.rhoSliderStateChanged(evt);
                    }
                });
        
                panel.q0Label.setText("q0");
                panel.q0Label.setEnabled(false);
        
                panel.q0Slider.setMaximum(10000);
                panel.q0Slider.setValue(8331);
                panel.q0Slider.setEnabled(false);
                panel.q0Slider.setPreferredSize(new java.awt.Dimension(140, 25));
                panel.q0Slider.addChangeListener(new javax.swing.event.ChangeListener() {
                    public void stateChanged(javax.swing.event.ChangeEvent evt) {
                        panel.q0SliderStateChanged(evt);
                    }
                });
        
                panel.numAntsLabel.setText("Num ants");
                panel.numAntsLabel.setEnabled(false);
        
                panel.numAntsSlider.setMaximum(10000);
                panel.numAntsSlider.setValue(3973);
                panel.numAntsSlider.setEnabled(false);
                panel.numAntsSlider.setPreferredSize(new java.awt.Dimension(140, 25));
                panel.numAntsSlider.addChangeListener(new javax.swing.event.ChangeListener() {
                    public void stateChanged(javax.swing.event.ChangeEvent evt) {
                        panel.numAntsSliderStateChanged(evt);
                    }
                });
        
                panel.numIterationsLabel.setText("Num iterations");
                panel.numIterationsLabel.setEnabled(false);
        
                panel.numIterationsSlider.setMaximum(10000);
                panel.numIterationsSlider.setValue(5404);
                panel.numIterationsSlider.setEnabled(false);
                panel.numIterationsSlider.setPreferredSize(new java.awt.Dimension(140, 25));
                panel.numIterationsSlider.addChangeListener(new javax.swing.event.ChangeListener() {
                    public void stateChanged(javax.swing.event.ChangeEvent evt) {
                        panel.numIterationsSliderStateChanged(evt);
                    }
                });
        
                panel.numIterationsTextField.setEditable(false);
                panel.numIterationsTextField.setText("0.5404");
                panel.numIterationsTextField.setPreferredSize(new java.awt.Dimension(45, 25));
        
                panel.numAntsTextField.setEditable(false);
                panel.numAntsTextField.setText("0.3973");
                panel.numAntsTextField.setPreferredSize(new java.awt.Dimension(45, 25));
        
                panel.q0TextField.setEditable(false);
                panel.q0TextField.setText("0.8331");
                panel.q0TextField.setPreferredSize(new java.awt.Dimension(45, 25));
        
                panel.rhoTextField.setEditable(false);
                panel.rhoTextField.setText("0.3044");
                panel.rhoTextField.setPreferredSize(new java.awt.Dimension(45, 25));
        
                panel.phiTextField.setEditable(false);
                panel.phiTextField.setText("0.1784");
                panel.phiTextField.setPreferredSize(new java.awt.Dimension(45, 25));
        
                panel.alphaLabel.setText("alpha");
                panel.alphaLabel.setEnabled(false);
        
                panel.alphaSlider.setMaximum(10000);
                panel.alphaSlider.setValue(10000);
                panel.alphaSlider.setEnabled(false);
                panel.alphaSlider.setPreferredSize(new java.awt.Dimension(140, 25));
                panel.alphaSlider.addChangeListener(new javax.swing.event.ChangeListener() {
                    public void stateChanged(javax.swing.event.ChangeEvent evt) {
                        panel.alphaSliderStateChanged(evt);
                    }
                });
        
                panel.alphaTextField.setEditable(false);
                panel.alphaTextField.setText("1.0");
                panel.alphaTextField.setPreferredSize(new java.awt.Dimension(45, 25));
        
                panel.betaLabel.setText("beta");
                panel.betaLabel.setEnabled(false);
        
                panel.betaSlider.setMaximum(10000);
                panel.betaSlider.setValue(10000);
                panel.betaSlider.setEnabled(false);
                panel.betaSlider.setPreferredSize(new java.awt.Dimension(140, 25));
                panel.betaSlider.addChangeListener(new javax.swing.event.ChangeListener() {
                    public void stateChanged(javax.swing.event.ChangeEvent evt) {
                        panel.betaSliderStateChanged(evt);
                    }
                });
        
                panel.betaTextField.setEditable(false);
                panel.betaTextField.setText("1.0");
                panel.betaTextField.setPreferredSize(new java.awt.Dimension(45, 25));
        
                panel.initialPheromoneLabel.setText("Initial pheromone");
                panel.initialPheromoneLabel.setEnabled(false);
        
                panel.initialPheromoneSlider.setMaximum(10000);
                panel.initialPheromoneSlider.setValue(4928);
                panel.initialPheromoneSlider.setEnabled(false);
                panel.initialPheromoneSlider.setPreferredSize(new java.awt.Dimension(140, 25));
                panel.initialPheromoneSlider.addChangeListener(new javax.swing.event.ChangeListener() {
                    public void stateChanged(javax.swing.event.ChangeEvent evt) {
                        panel.initialPheromoneSliderStateChanged(evt);
                    }
                });
        
                panel.initialPheromoneTextField.setEditable(false);
                panel.initialPheromoneTextField.setText("0.4928");
                panel.initialPheromoneTextField.setPreferredSize(new java.awt.Dimension(45, 25));
        
                panel.punishmentThresholdLabel.setText("Punishment threshold");
                panel.punishmentThresholdLabel.setEnabled(false);
        
                panel.punishmentThresholdSlider.setMaximum(10000);
                panel.punishmentThresholdSlider.setValue(6806);
                panel.punishmentThresholdSlider.setEnabled(false);
                panel.punishmentThresholdSlider.setPreferredSize(new java.awt.Dimension(140, 25));
                panel.punishmentThresholdSlider.addChangeListener(new javax.swing.event.ChangeListener() {
                    public void stateChanged(javax.swing.event.ChangeEvent evt) {
                        panel.punishmentThresholdSliderStateChanged(evt);
                    }
                });
        
                panel.punishmentThresholdTextField.setEditable(false);
                panel.punishmentThresholdTextField.setText("0.6806");
                panel.punishmentThresholdTextField.setPreferredSize(new java.awt.Dimension(45, 25));
        
                panel.pathLengthFactorLabel.setText("Path length factor");
                panel.pathLengthFactorLabel.setEnabled(false);
        
                panel.pathLengthFactorSlider.setMaximum(10000);
                panel.pathLengthFactorSlider.setValue(5651);
                panel.pathLengthFactorSlider.setEnabled(false);
                panel.pathLengthFactorSlider.setPreferredSize(new java.awt.Dimension(140, 25));
                panel.pathLengthFactorSlider.addChangeListener(new javax.swing.event.ChangeListener() {
                    public void stateChanged(javax.swing.event.ChangeEvent evt) {
                        panel.pathLengthFactorSliderStateChanged(evt);
                    }
                });
        
                panel.pathLengthFactorTextField.setEditable(false);
                panel.pathLengthFactorTextField.setText("0.5651");
                panel.pathLengthFactorTextField.setPreferredSize(new java.awt.Dimension(45, 25));
        
                panel.transitionThresholdLabel.setText("Transition threshold");
                panel.transitionThresholdLabel.setEnabled(false);
        
                panel.transitionThresholdSlider.setMaximum(10000);
                panel.transitionThresholdSlider.setValue(4972);
                panel.transitionThresholdSlider.setEnabled(false);
                panel.transitionThresholdSlider.setPreferredSize(new java.awt.Dimension(140, 25));
                panel.transitionThresholdSlider.addChangeListener(new javax.swing.event.ChangeListener() {
                    public void stateChanged(javax.swing.event.ChangeEvent evt) {
                        panel.transitionThresholdSliderStateChanged(evt);
                    }
                });
        
                panel.transitionThresholdTextField.setEditable(false);
                panel.transitionThresholdTextField.setText("0.4972");
                panel.transitionThresholdTextField.setPreferredSize(new java.awt.Dimension(45, 25));
        
                javax.swing.GroupLayout btrm_ParametersPanelLayout = new javax.swing.GroupLayout(panel.btrm_ParametersPanel);
                panel.btrm_ParametersPanel.setLayout(btrm_ParametersPanelLayout);
                btrm_ParametersPanelLayout.setHorizontalGroup(
                    btrm_ParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(btrm_ParametersPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(btrm_ParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panel.numIterationsLabel)
                            .addGroup(btrm_ParametersPanelLayout.createSequentialGroup()
                                .addComponent(panel.numIterationsSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel.numIterationsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(panel.numAntsLabel)
                            .addGroup(btrm_ParametersPanelLayout.createSequentialGroup()
                                .addComponent(panel.numAntsSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel.numAntsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(btrm_ParametersPanelLayout.createSequentialGroup()
                                .addGroup(btrm_ParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(panel.phiSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(panel.rhoLabel)
                                    .addComponent(panel.rhoSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(btrm_ParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(panel.rhoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(panel.phiTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(panel.phiLabel)
                            .addGroup(btrm_ParametersPanelLayout.createSequentialGroup()
                                .addComponent(panel.q0Slider, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel.q0TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(panel.q0Label, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(btrm_ParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(btrm_ParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(btrm_ParametersPanelLayout.createSequentialGroup()
                                    .addComponent(panel.betaLabel)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btrm_ParametersPanelLayout.createSequentialGroup()
                                    .addGroup(btrm_ParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(panel.initialPheromoneLabel)
                                        .addComponent(panel.pathLengthFactorLabel)
                                        .addComponent(panel.punishmentThresholdSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(panel.transitionThresholdLabel)
                                        .addComponent(panel.pathLengthFactorSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(panel.betaSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(panel.alphaSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(panel.initialPheromoneSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(panel.transitionThresholdSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(panel.punishmentThresholdLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGap(12, 12, 12)
                                    .addGroup(btrm_ParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(panel.alphaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(panel.pathLengthFactorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(panel.punishmentThresholdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(panel.betaTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(panel.transitionThresholdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(panel.initialPheromoneTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(panel.alphaLabel))
                        .addGap(192, 192, 192))
                );
                btrm_ParametersPanelLayout.setVerticalGroup(
                    btrm_ParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(btrm_ParametersPanelLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(btrm_ParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(btrm_ParametersPanelLayout.createSequentialGroup()
                                .addGroup(btrm_ParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(btrm_ParametersPanelLayout.createSequentialGroup()
                                        .addComponent(panel.phiLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(panel.phiSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(btrm_ParametersPanelLayout.createSequentialGroup()
                                        .addGap(23, 23, 23)
                                        .addComponent(panel.phiTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(6, 6, 6)
                                .addComponent(panel.rhoLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(btrm_ParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(panel.rhoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(panel.rhoSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel.q0Label)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(btrm_ParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(panel.q0TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(panel.q0Slider, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel.numAntsLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(btrm_ParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(panel.numAntsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(panel.numAntsSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel.numIterationsLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(btrm_ParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(panel.numIterationsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(panel.numIterationsSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(btrm_ParametersPanelLayout.createSequentialGroup()
                                .addComponent(panel.alphaLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(btrm_ParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(panel.alphaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(panel.alphaSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel.betaLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(btrm_ParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(panel.betaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(panel.betaSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel.initialPheromoneLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(btrm_ParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(panel.initialPheromoneTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(panel.initialPheromoneSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel.punishmentThresholdLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(btrm_ParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(panel.punishmentThresholdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(panel.punishmentThresholdSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel.pathLengthFactorLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(btrm_ParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(panel.pathLengthFactorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(panel.pathLengthFactorSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel.transitionThresholdLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(btrm_ParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(panel.transitionThresholdSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(panel.transitionThresholdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(57, 57, 57))
                );
        
                panel.lftm_ParametersPanelTabbedPane.addTab("BTRM", panel.btrm_ParametersPanel);
        
                panel.veryHighLabel.setText("Very High");
                panel.veryHighLabel.setEnabled(false);
        
                panel.highLabel.setText("High");
                panel.highLabel.setEnabled(false);
        
                panel.mediumLabel.setText("Medium");
                panel.mediumLabel.setEnabled(false);
        
                panel.lowLabel.setText("Low");
                panel.lowLabel.setEnabled(false);
        
                panel.veryLowLabel.setText("Very Low");
                panel.veryLowLabel.setEnabled(false);
        
                panel.veryHighMembershipFunctionComboBox.setModel(panel.membershipFunctionsComboBoxModels.get(0));
                panel.veryHighMembershipFunctionComboBox.setEnabled(false);
                panel.veryHighMembershipFunctionComboBox.addItemListener(new java.awt.event.ItemListener() {
                    public void itemStateChanged(java.awt.event.ItemEvent evt) {
                        panel.veryHighMembershipFunctionComboBoxItemStateChanged(evt);
                    }
                });
        
                panel.membershipFunctionLabel.setText("Membership function");
                panel.membershipFunctionLabel.setEnabled(false);
        
                panel.highMembershipFunctionComboBox.setModel(panel.membershipFunctionsComboBoxModels.get(1));
                panel.highMembershipFunctionComboBox.setEnabled(false);
                panel.highMembershipFunctionComboBox.addItemListener(new java.awt.event.ItemListener() {
                    public void itemStateChanged(java.awt.event.ItemEvent evt) {
                        panel.highMembershipFunctionComboBoxItemStateChanged(evt);
                    }
                });
        
                panel.mediumMembershipFunctionComboBox.setModel(panel.membershipFunctionsComboBoxModels.get(2));
                panel.mediumMembershipFunctionComboBox.setEnabled(false);
                panel.mediumMembershipFunctionComboBox.addItemListener(new java.awt.event.ItemListener() {
                    public void itemStateChanged(java.awt.event.ItemEvent evt) {
                        panel.mediumMembershipFunctionComboBoxItemStateChanged(evt);
                    }
                });
        
                panel.lowMembershipFunctionComboBox.setModel(panel.membershipFunctionsComboBoxModels.get(3));
                panel.lowMembershipFunctionComboBox.setEnabled(false);
                panel.lowMembershipFunctionComboBox.addItemListener(new java.awt.event.ItemListener() {
                    public void itemStateChanged(java.awt.event.ItemEvent evt) {
                        panel.lowMembershipFunctionComboBoxItemStateChanged(evt);
                    }
                });
        
                panel.veryLowMembershipFunctionComboBox.setModel(panel.membershipFunctionsComboBoxModels.get(4));
                panel.veryLowMembershipFunctionComboBox.setEnabled(false);
                panel.veryLowMembershipFunctionComboBox.addItemListener(new java.awt.event.ItemListener() {
                    public void itemStateChanged(java.awt.event.ItemEvent evt) {
                        panel.veryLowMembershipFunctionComboBoxItemStateChanged(evt);
                    }
                });
        
                panel.veryHighAParameterTextField.setEnabled(false);
                panel.veryHighAParameterTextField.setInputVerifier(panel.doubleInputVerifier);
        
                panel.veryHighBParameterTextField.setEnabled(false);
                panel.veryHighBParameterTextField.setInputVerifier(panel.doubleInputVerifier);
        
                panel.veryHighCParameterTextField.setEnabled(false);
                panel.veryHighCParameterTextField.setInputVerifier(panel.doubleInputVerifier);
        
                panel.veryHighDParameterTextField.setEnabled(false);
                panel.veryHighDParameterTextField.setInputVerifier(panel.doubleInputVerifier);
        
                panel.aParameterLabel.setText("a");
                panel.aParameterLabel.setEnabled(false);
        
                panel.bParameterLabel.setText("b");
                panel.bParameterLabel.setEnabled(false);
        
                panel.cParameterLabel.setText("c");
                panel.cParameterLabel.setEnabled(false);
        
                panel.dParameterLabel.setText("d");
                panel.dParameterLabel.setEnabled(false);
        
                panel.highAParameterTextField.setEnabled(false);
                panel.highAParameterTextField.setInputVerifier(panel.doubleInputVerifier);
        
                panel.highBParameterTextField.setEnabled(false);
                panel.highBParameterTextField.setInputVerifier(panel.doubleInputVerifier);
        
                panel.highCParameterTextField.setEnabled(false);
                panel.highCParameterTextField.setInputVerifier(panel.doubleInputVerifier);
        
                panel.highDParameterTextField.setEnabled(false);
                panel.highDParameterTextField.setInputVerifier(panel.doubleInputVerifier);
        
                panel.mediumDParameterTextField.setEnabled(false);
                panel.mediumDParameterTextField.setInputVerifier(panel.doubleInputVerifier);
        
                panel.mediumCParameterTextField.setEnabled(false);
                panel.mediumCParameterTextField.setInputVerifier(panel.doubleInputVerifier);
        
                panel.mediumBParameterTextField.setEnabled(false);
                panel.mediumBParameterTextField.setInputVerifier(panel.doubleInputVerifier);
        
                panel.mediumAParameterTextField.setEnabled(false);
                panel.mediumAParameterTextField.setInputVerifier(panel.doubleInputVerifier);
        
                panel.lowAParameterTextField.setEnabled(false);
                panel.lowAParameterTextField.setInputVerifier(panel.doubleInputVerifier);
        
                panel.lowCParameterTextField.setEnabled(false);
                panel.lowCParameterTextField.setInputVerifier(panel.doubleInputVerifier);
        
                panel.lowDParameterTextField.setEnabled(false);
                panel.lowDParameterTextField.setInputVerifier(panel.doubleInputVerifier);
        
                panel.lowBParameterTextField.setEnabled(false);
                panel.lowBParameterTextField.setInputVerifier(panel.doubleInputVerifier);
        
                panel.veryLowDParameterTextField.setEnabled(false);
                panel.veryLowDParameterTextField.setInputVerifier(panel.doubleInputVerifier);
        
                panel.veryLowCParameterTextField.setEnabled(false);
                panel.veryLowCParameterTextField.setInputVerifier(panel.doubleInputVerifier);
        
                panel.veryLowBParameterTextField.setEnabled(false);
                panel.veryLowBParameterTextField.setInputVerifier(panel.doubleInputVerifier);
        
                panel.veryLowAParameterTextField.setEnabled(false);
                panel.veryLowAParameterTextField.setInputVerifier(panel.doubleInputVerifier);
        
                panel.universeMINLabel.setText("Universe MIN");
                panel.universeMINLabel.setEnabled(false);
        
                panel.universeMAXLabel.setText("Universe MAX");
                panel.universeMAXLabel.setEnabled(false);
        
                panel.universeMINTextField.setText("0.0");
                panel.universeMINTextField.setEnabled(false);
        
                panel.universeMAXTextField.setText("1.0");
                panel.universeMAXTextField.setEnabled(false);
        
                panel.linguisticTermsPanelContainer.add(panel.fuzzySetPanelAux,null);
                panel.linguisticTermsPanelContainer.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        
                javax.swing.GroupLayout linguisticTermsPanelContainerLayout = new javax.swing.GroupLayout(panel.linguisticTermsPanelContainer);
                panel.linguisticTermsPanelContainer.setLayout(linguisticTermsPanelContainerLayout);
                linguisticTermsPanelContainerLayout.setHorizontalGroup(
                    linguisticTermsPanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGap(0, 267, Short.MAX_VALUE)
                );
                linguisticTermsPanelContainerLayout.setVerticalGroup(
                    linguisticTermsPanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGap(0, 0, Short.MAX_VALUE)
                );
        
                javax.swing.GroupLayout linguisticTermsPanelLayout = new javax.swing.GroupLayout(panel.linguisticTermsPanel);
                panel.linguisticTermsPanel.setLayout(linguisticTermsPanelLayout);
                linguisticTermsPanelLayout.setHorizontalGroup(
                    linguisticTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(linguisticTermsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(linguisticTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(linguisticTermsPanelLayout.createSequentialGroup()
                                .addGroup(linguisticTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(panel.veryLowLabel)
                                    .addComponent(panel.lowLabel)
                                    .addComponent(panel.mediumLabel)
                                    .addComponent(panel.highLabel)
                                    .addComponent(panel.veryHighLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(linguisticTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(linguisticTermsPanelLayout.createSequentialGroup()
                                        .addGroup(linguisticTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(linguisticTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(panel.highMembershipFunctionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(panel.membershipFunctionLabel)
                                                .addComponent(panel.veryHighMembershipFunctionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(panel.lowMembershipFunctionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(panel.mediumMembershipFunctionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(linguisticTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(linguisticTermsPanelLayout.createSequentialGroup()
                                                .addComponent(panel.lowAParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(panel.lowBParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(panel.lowCParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(panel.lowDParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(linguisticTermsPanelLayout.createSequentialGroup()
                                                .addComponent(panel.mediumAParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(panel.mediumBParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(panel.mediumCParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(panel.mediumDParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(linguisticTermsPanelLayout.createSequentialGroup()
                                                .addGroup(linguisticTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(panel.veryHighAParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(panel.aParameterLabel))
                                                .addGap(18, 18, 18)
                                                .addGroup(linguisticTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(panel.veryHighBParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(panel.bParameterLabel))
                                                .addGap(18, 18, 18)
                                                .addGroup(linguisticTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(panel.veryHighCParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(panel.cParameterLabel))
                                                .addGap(18, 18, 18)
                                                .addGroup(linguisticTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(panel.dParameterLabel)
                                                    .addComponent(panel.veryHighDParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addGroup(linguisticTermsPanelLayout.createSequentialGroup()
                                                .addComponent(panel.highAParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(panel.highBParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(panel.highCParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(panel.highDParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(linguisticTermsPanelLayout.createSequentialGroup()
                                        .addComponent(panel.veryLowMembershipFunctionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(panel.veryLowAParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(panel.veryLowBParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(panel.veryLowCParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(panel.veryLowDParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(linguisticTermsPanelLayout.createSequentialGroup()
                                .addGroup(linguisticTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, linguisticTermsPanelLayout.createSequentialGroup()
                                        .addComponent(panel.universeMINLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(panel.universeMINTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, linguisticTermsPanelLayout.createSequentialGroup()
                                        .addComponent(panel.universeMAXLabel)
                                        .addGap(18, 18, 18)
                                        .addComponent(panel.universeMAXTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addComponent(panel.linguisticTermsPanelContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                );
                linguisticTermsPanelLayout.setVerticalGroup(
                    linguisticTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(linguisticTermsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(linguisticTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(panel.membershipFunctionLabel)
                            .addComponent(panel.aParameterLabel)
                            .addComponent(panel.bParameterLabel)
                            .addComponent(panel.cParameterLabel)
                            .addComponent(panel.dParameterLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(linguisticTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(panel.veryHighMembershipFunctionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel.veryHighLabel)
                            .addComponent(panel.veryHighAParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel.veryHighBParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel.veryHighCParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel.veryHighDParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(linguisticTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(panel.highMembershipFunctionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel.highLabel)
                            .addComponent(panel.highAParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel.highBParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel.highCParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel.highDParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(linguisticTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(panel.mediumMembershipFunctionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel.mediumLabel)
                            .addComponent(panel.mediumAParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel.mediumBParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel.mediumCParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel.mediumDParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(linguisticTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(panel.lowMembershipFunctionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel.lowLabel)
                            .addComponent(panel.lowAParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel.lowBParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel.lowCParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel.lowDParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(13, 13, 13)
                        .addGroup(linguisticTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(panel.veryLowMembershipFunctionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel.veryLowLabel)
                            .addComponent(panel.veryLowAParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel.veryLowBParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel.veryLowCParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel.veryLowDParameterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(linguisticTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(linguisticTermsPanelLayout.createSequentialGroup()
                                .addGroup(linguisticTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(panel.universeMINLabel)
                                    .addComponent(panel.universeMINTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(linguisticTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(panel.universeMAXLabel)
                                    .addComponent(panel.universeMAXTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 131, Short.MAX_VALUE))
                            .addComponent(panel.linguisticTermsPanelContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
                );
        
                panel.lftm_ParametersPanelTabbedPane.addTab("Linguistic terms", panel.linguisticTermsPanel);
        
                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(panel);
                panel.setLayout(layout);
                layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel.lftm_ParametersPanelTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                );
                layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel.lftm_ParametersPanelTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)
                );
        
                panel.lftm_ParametersPanelTabbedPane.getAccessibleContext().setAccessibleName("BTRM");
    }
}
