package es.ants.felixgm.trmsim_wsn.gui;

import es.ants.felixgm.trmsim_wsn.Controller;

import es.ants.felixgm.trmsim_wsn.gui.legendpanels.EigenTrustLegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.legendpanels.LegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.legendpanels.PowerTrustLegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.legendpanels.TRIPLegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.EigenTrustNetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.PowerTrustNetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.TRIPNetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.*;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.TRMParametersPanel;

import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Sensor;
import es.ants.felixgm.trmsim_wsn.network.Service;

import es.ants.felixgm.trmsim_wsn.outcomes.Outcome;

import es.ants.felixgm.trmsim_wsn.trm.TRModel_WSN;
import es.ants.felixgm.trmsim_wsn.trm.btrm_wsn.BTRM_WSN;
import es.ants.felixgm.trmsim_wsn.trm.eigentrust.EigenTrust;
import es.ants.felixgm.trmsim_wsn.trm.lftm.LFTM;
import es.ants.felixgm.trmsim_wsn.trm.peertrust.PeerTrust;
import es.ants.felixgm.trmsim_wsn.trm.powertrust.PowerTrust;
import es.ants.felixgm.trmsim_wsn.trm.templatetrm.TemplateTRM;
import es.ants.felixgm.trmsim_wsn.trm.trip.TRIP;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.util.*;
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Font;
import javax.swing.border.EmptyBorder;
import com.formdev.flatlaf.FlatIntelliJLaf;

public class TRMSim_WSN extends javax.swing.JPanel implements Observer {

    public static final String CURRENT_VERSION = "0.6-Parallel";
    protected Controller C;

    public TRMSim_WSN() {
        try {
            C = new Controller();
            initComponents();
            setupModernLayout();
            initializeTRModels();

            // OPRAVA: Tu už nevoláme TRModelComboBoxItemStateChanged(null),
            // pretože okno ešte nie je viditeľné a grafika by bola null.
            // Volanie presunieme do metódy startUp().

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Táto metóda sa zavolá až keď je okno viditeľné.
     * Nastaví defaultný model (BTRM) a vykreslí prázdne grafy.
     */
    public void startUp() {
        // Nastavíme BTRM ako predvolený model
        TRModelComboBox.setSelectedItem(BTRM_WSN.get_name());

        // Spustíme logiku zmeny modelu (načítanie parametrov, vytvorenie panelov)
        TRModelComboBoxItemStateChanged(null);
    }

    public static void main(String args[]) {
        try {
            try { UIManager.setLookAndFeel(new FlatIntelliJLaf()); } catch(Exception ex) {}

            Object[] options = {"1 Simulácia", "2 Simulácie"};
            int n = JOptionPane.showOptionDialog(null,
                    "Vitajte v TRMSim-WSN.\nKoľko nezávislých simulácií si želáte spustiť?",
                    "Konfigurácia spustenia",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);

            JFrame frame = new JFrame("TRMSim-WSN " + CURRENT_VERSION);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()*0.9),
                    (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()*0.9));
            frame.setLocationRelativeTo(null);
            frame.setLayout(new BorderLayout());

            if (n == 0) {
                TRMSim_WSN simulation1 = new TRMSim_WSN();
                frame.add(simulation1, BorderLayout.CENTER);

                // OPRAVA: Najprv zobrazíme okno, AŽ POTOM inicializujeme grafiku
                frame.setVisible(true);
                simulation1.startUp();

            } else {
                JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
                splitPane.setResizeWeight(0.5);

                TRMSim_WSN simulation1 = new TRMSim_WSN();
                TRMSim_WSN simulation2 = new TRMSim_WSN();

                simulation1.setBorder(BorderFactory.createTitledBorder("Simulácia A"));
                simulation2.setBorder(BorderFactory.createTitledBorder("Simulácia B"));

                splitPane.setLeftComponent(simulation1);
                splitPane.setRightComponent(simulation2);

                frame.add(splitPane, BorderLayout.CENTER);

                // OPRAVA: Najprv zobrazíme okno, AŽ POTOM inicializujeme grafiku
                frame.setVisible(true);
                simulation1.startUp();
                simulation2.startUp();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initializeTRModels() {
        Vector<String> trmodels = new Vector<String>();
        trmodels.add(BTRM_WSN.get_name());
        trmodels.add(EigenTrust.get_name());
        trmodels.add(PeerTrust.get_name());
        trmodels.add(PowerTrust.get_name());
        trmodels.add(LFTM.get_name());
        trmodels.add(TRIP.get_name());
        trmodels.add(TemplateTRM.get_name());
        TRModelComboBox.setModel(new javax.swing.DefaultComboBoxModel(trmodels));

        TRModelMenu.removeAll();
        for (final String trmodel : trmodels) {
            JMenuItem trmodelMenuItem = new JMenuItem(trmodel);
            trmodelMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    TRModelComboBox.setSelectedItem(trmodel);
                    TRModelComboBoxItemStateChanged(null);
                }
            });
            TRModelMenu.add(trmodelMenuItem);
        }
    }

    // --- Zvyšok triedy je bez zmeny, len pre istotu prikladám setupOutcomesPanels s drawAxes ---

    private void TRModelComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        try {
            String trModelName = (String)TRModelComboBox.getSelectedItem();
            C.set_TRModel_WSN(trModelName);
            String packageName = "es.ants.felixgm.trmsim_wsn.";
            TRMParametersPanel trmParametersPanel = (TRMParametersPanel)Class.forName(packageName+"gui.parameterpanels."+trModelName+"_ParametersPanel").newInstance();
            trmParametersPanel.set_TRMParameters(C.get_TRMParameters());
            TRMParametersScrollPane.setViewportView(trmParametersPanel);
            setupOutcomesPanels(trModelName);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void setupOutcomesPanels(String trModelName) {
        legendPanelContainer.removeAll();
        networkPanelContainer.removeAll();
        outcomesTabbedPane.removeAll();
        outcomesPanels = new ArrayList<OutcomesPanel>();

        if (trModelName.equals(BTRM_WSN.get_name())) {
            percentageClientsLabel.setEnabled(true);
            percentageClientsSlider.setEnabled(true);
            percentageClientsTextField.setEnabled(true);

            legendPanel = new LegendPanel();
            networkPanel = new NetworkPanel();

            outcomesPanels.add(new AccuracyPanel());
            outcomesPanels.add(new PathLengthPanel());
            outcomesPanels.add(new EnergyConsumptionPanel());

        } else if (trModelName.equals(EigenTrust.get_name())) {
            percentageClientsLabel.setEnabled(false);
            percentageClientsSlider.setEnabled(false);
            percentageClientsTextField.setEnabled(false);
            legendPanel = new EigenTrustLegendPanel();
            networkPanel = new EigenTrustNetworkPanel();
            outcomesPanels.add(new AccuracyPanel());
            outcomesPanels.add(new PathLengthPanel());
            outcomesPanels.add(new EigenTrustEnergyConsumptionPanel());
        } else if (trModelName.equals(PeerTrust.get_name())) {
            percentageClientsLabel.setEnabled(true);
            percentageClientsSlider.setEnabled(true);
            percentageClientsTextField.setEnabled(true);
            legendPanel = new LegendPanel();
            networkPanel = new NetworkPanel();
            outcomesPanels.add(new AccuracyPanel());
            outcomesPanels.add(new PathLengthPanel());
            outcomesPanels.add(new EnergyConsumptionPanel());
        } else if (trModelName.equals(PowerTrust.get_name())){
            percentageClientsLabel.setEnabled(true);
            percentageClientsSlider.setEnabled(true);
            percentageClientsTextField.setEnabled(true);
            legendPanel = new PowerTrustLegendPanel();
            networkPanel = new PowerTrustNetworkPanel();
            outcomesPanels.add(new AccuracyPanel());
            outcomesPanels.add(new PathLengthPanel());
            outcomesPanels.add(new PowerTrustEnergyConsumptionPanel());
        } else if (trModelName.equals(LFTM.get_name())){
            percentageClientsLabel.setEnabled(true);
            percentageClientsSlider.setEnabled(true);
            percentageClientsTextField.setEnabled(true);
            legendPanel = new LegendPanel();
            networkPanel = new NetworkPanel();
            outcomesPanels.add(new AccuracyPanel());
            outcomesPanels.add(new PathLengthPanel());
            outcomesPanels.add(new EnergyConsumptionPanel());
            outcomesPanels.add(new LFTM_SatisfactionPanel());
        } else if (trModelName.equals(TRIP.get_name())){
            percentageClientsLabel.setEnabled(false);
            percentageClientsSlider.setEnabled(false);
            percentageClientsTextField.setEnabled(false);
            legendPanel = new TRIPLegendPanel();
            networkPanel = new TRIPNetworkPanel();
            outcomesPanels.add(new AccuracyPanel());
            outcomesPanels.add(new PathLengthPanel());
            outcomesPanels.add(new EnergyConsumptionPanel());
        } else {
            legendPanel = new LegendPanel();
            networkPanel = new NetworkPanel();
            outcomesPanels.add(new AccuracyPanel());
            outcomesPanels.add(new PathLengthPanel());
            outcomesPanels.add(new EnergyConsumptionPanel());
        }

        legendPanelContainer.add(legendPanel);
        networkPanelContainer.add(networkPanel);
        networkPanel.setBackground(Color.white);

        // Bezpečné vykreslenie
        SwingUtilities.invokeLater(() -> {
            if (legendPanel.isDisplayable()) legendPanel.plotLegend();
            for (OutcomesPanel p : outcomesPanels) {
                if (p.isDisplayable()) {
                    p.clearPanel();
                    p.drawAxes(); // Toto vykreslí prázdne grafy na začiatku
                }
            }
        });

        for (OutcomesPanel p : outcomesPanels) outcomesTabbedPane.addTab(p.getLabel(), p);

        this.validate();
        this.repaint();
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        parametersSourceButtonGroup = new javax.swing.ButtonGroup();

        legendPanelContainer = new javax.swing.JPanel();
        legendLabel = new javax.swing.JLabel();

        newWSNButton = new JButton("New WSN");
        newWSNButton.addActionListener(evt -> newWSNButtonActionPerformed(evt));
        resetWSNButton = new JButton("Reset WSN");
        resetWSNButton.setEnabled(false);
        resetWSNButton.addActionListener(evt -> resetWSNButtonActionPerformed(evt));
        runTRMButton = new JButton("Run T&R Model");
        runTRMButton.setEnabled(false);
        runTRMButton.addActionListener(evt -> runTRMButtonActionPerformed(evt));
        stopTRMButton = new JButton("Stop T&R Model");
        stopTRMButton.setEnabled(false);
        stopTRMButton.addActionListener(evt -> stopTRMButtonActionPerformed(evt));
        loadWSNButton = new JButton("Load WSN");
        loadWSNButton.addActionListener(evt -> loadWSNButtonActionPerformed(evt));
        saveWSNButton = new JButton("Save WSN");
        saveWSNButton.setEnabled(false);
        saveWSNButton.addActionListener(evt -> saveWSNButtonActionPerformed(evt));
        stopSimulationsButton = new JButton("Stop Simulations");
        stopSimulationsButton.setEnabled(false);
        stopSimulationsButton.addActionListener(evt -> stopSimulationsButtonActionPerformed(evt));
        runSimulationsButton = new JButton("Run Simulations");
        runSimulationsButton.addActionListener(evt -> runSimulationsButtonActionPerformed(evt));
        exportDataButton = new JButton("Export Data");
        exportDataButton.addActionListener(evt -> exportDataButtonActionPerformed(evt));

        numExecutionsLabel = new JLabel("Num executions");
        numExecutionsSpinner = new JSpinner(new SpinnerNumberModel(100,1,Integer.MAX_VALUE,1));
        numNetworksLabel = new JLabel("Num networks");
        numNetworksSpinner = new JSpinner(new SpinnerNumberModel(100,1,Integer.MAX_VALUE,1));
        minNumSensorsLabel = new JLabel("Min Num Sensors");
        minNumSensorsSpinner = new JSpinner(new SpinnerNumberModel(50,1,Integer.MAX_VALUE,1));
        minNumSensorsSpinner.addChangeListener(evt -> minNumSensorsSpinnerStateChanged(evt));
        maxNumSensorsLabel = new JLabel("Max Num Sensors");
        maxNumSensorsSpinner = new JSpinner(new SpinnerNumberModel(50,1,Integer.MAX_VALUE,1));
        maxNumSensorsSpinner.addChangeListener(evt -> maxNumSensorsSpinnerStateChanged(evt));

        percentageClientsLabel = new JLabel("% Clients");
        percentageClientsSlider = new JSlider();
        percentageClientsSlider.setValue(15);
        percentageClientsTextField = new JTextField("15");
        percentageClientsSlider.addChangeListener(evt -> percentageClientsSliderStateChanged(evt));
        percentageRelayServersLabel = new JLabel("% Relay Servers");
        percentageRelayServersSlider = new JSlider();
        percentageRelayServersSlider.setValue(5);
        percentageRelayServersTextField = new JTextField("5");
        percentageRelayServersSlider.addChangeListener(evt -> percentageRelayServersSliderStateChanged(evt));
        percentageMaliciousServersLabel = new JLabel("% Malicious Servers");
        percentageMaliciousServersSlider = new JSlider();
        percentageMaliciousServersSlider.setValue(70);
        percentageMaliciousServersTextField = new JTextField("70");
        percentageMaliciousServersSlider.addChangeListener(evt -> percentageMaliciousServersSliderStateChanged(evt));
        radioRangeLabel = new JLabel("Radio Range");
        radioRangeSlider = new JSlider();
        radioRangeSlider.setValue(12);
        radioRangeTextField = new JTextField("12");
        radioRangeSlider.addChangeListener(evt -> radioRangeSliderStateChanged(evt));
        delayLabel = new JLabel("Delay");
        delaySlider = new JSlider();
        delaySlider.setValue(0);
        delayTextField = new JTextField("0");
        delaySlider.addChangeListener(evt -> delaySliderStateChanged(evt));

        TRModelLabel = new JLabel("Trust & Reputation Model");
        TRModelComboBox = new JComboBox();
        TRModelComboBox.addItemListener(evt -> TRModelComboBoxItemStateChanged(evt));

        showIdsCheckBox = new JCheckBox("Show ids");
        showIdsCheckBox.addItemListener(evt -> showIdsCheckBoxItemStateChanged(evt));
        showLinksCheckBox = new JCheckBox("Show links");
        showLinksCheckBox.setSelected(true);
        showLinksCheckBox.addItemListener(evt -> showLinksCheckBoxItemStateChanged(evt));
        showRangesCheckBox = new JCheckBox("Show ranges");
        showRangesCheckBox.addItemListener(evt -> showRangesCheckBoxItemStateChanged(evt));
        showGridCheckBox = new JCheckBox("Show grid");
        showGridCheckBox.addItemListener(evt -> showGridCheckBoxItemStateChanged(evt));

        collusionCheckBox = new JCheckBox("Collusion");
        oscillatingWSNsCheckBox = new JCheckBox("Oscillating WSNs");
        dynamicWSNsCheckBox = new JCheckBox("Dynamic WSNs");

        sensorPropertiesPanel = new JPanel();
        sensorIdLabel = new JLabel("Sensor Id");
        sensorIdTextField = new JTextField();
        xCoordinateLabel = new JLabel("X");
        xCoordinateTextField = new JTextField();
        yCoordinateLabel = new JLabel("Y");
        yCoordinateTextField = new JTextField();
        neighborsLabel = new JLabel("Neighbor(s)");
        neighborsList = new JList();
        neighborsScrollPane = new JScrollPane(neighborsList);
        neighborsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                neighborsListMouseClicked(evt);
            }
        });

        networkPanelContainer = new JPanel(new BorderLayout());
        networkPanelContainer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                networkPanelContainerMouseClicked(evt);
            }
        });

        outcomesPanelsPanel = new JPanel();
        outcomesTabbedPane = new JTabbedPane();
        messagePanel = new JPanel();
        messagesTextArea = new JTextArea();
        messagesScrollPane = new JScrollPane(messagesTextArea);

        parametersPanel = new JPanel();
        parametersFileRadioButton = new JRadioButton("File", true);
        customizedParametersRadioButton = new JRadioButton("Customized");
        parametersSourceButtonGroup.add(parametersFileRadioButton);
        parametersSourceButtonGroup.add(customizedParametersRadioButton);
        parametersFileRadioButton.addItemListener(evt -> parametersFileRadioButtonItemStateChanged(evt));

        parametersFileTextField = new JTextField("BTRM-WSNparameters.txt");
        browseButton = new JButton("Browse");
        browseButton.addActionListener(evt -> browseButtonActionPerformed(evt));
        applyParametersChangesButton = new JButton("Apply changes");
        applyParametersChangesButton.addActionListener(evt -> applyParametersChangesButtonActionPerformed(evt));
        saveParametersFileContentButton = new JButton("Save file content");
        saveParametersFileContentButton.addActionListener(evt -> saveParametersFileContentButtonActionPerformed(evt));
        parametersFileContentTextArea = new JTextArea();
        TRMParametersScrollPane = new JScrollPane();

        menuBar = new JMenuBar();
        wsnMenu = new JMenu("WSN");
        simulationsMenu = new JMenu("Simulations");
        parametersMenu = new JMenu("Parameters");
        TRModelMenu = new JMenu("T&R Model");
        helpMenu = new JMenu("Help");

        menuBar.add(wsnMenu);
        menuBar.add(simulationsMenu);
        menuBar.add(parametersMenu);
        menuBar.add(TRModelMenu);
        menuBar.add(helpMenu);
    }

    private void setupModernLayout() {
        this.setLayout(new BorderLayout());

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(menuBar, BorderLayout.NORTH);

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.add(newWSNButton);
        toolbar.add(loadWSNButton);
        toolbar.add(saveWSNButton);
        toolbar.addSeparator();
        toolbar.add(resetWSNButton);
        toolbar.addSeparator();
        toolbar.add(runTRMButton);
        toolbar.add(stopTRMButton);
        toolbar.addSeparator();
        toolbar.add(runSimulationsButton);
        toolbar.add(stopSimulationsButton);
        toolbar.addSeparator();
        toolbar.add(exportDataButton);
        topContainer.add(toolbar, BorderLayout.CENTER);

        this.add(topContainer, BorderLayout.NORTH);

        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(320, 0));
        JTabbedPane settingsTabs = new JTabbedPane();

        JPanel topologyPanel = new JPanel();
        topologyPanel.setLayout(new BoxLayout(topologyPanel, BoxLayout.Y_AXIS));
        topologyPanel.add(wrapConfig(minNumSensorsLabel, minNumSensorsSpinner));
        topologyPanel.add(wrapConfig(maxNumSensorsLabel, maxNumSensorsSpinner));
        topologyPanel.add(wrapConfig(radioRangeLabel, radioRangeSlider));
        topologyPanel.add(radioRangeTextField);

        JPanel scenarioPanel = new JPanel();
        scenarioPanel.setLayout(new BoxLayout(scenarioPanel, BoxLayout.Y_AXIS));
        scenarioPanel.add(wrapConfig(percentageClientsLabel, percentageClientsSlider));
        scenarioPanel.add(wrapConfig(percentageMaliciousServersLabel, percentageMaliciousServersSlider));
        scenarioPanel.add(wrapConfig(percentageRelayServersLabel, percentageRelayServersSlider));
        scenarioPanel.add(wrapConfig(delayLabel, delaySlider));

        JPanel simSettingsPanel = new JPanel();
        simSettingsPanel.setLayout(new BoxLayout(simSettingsPanel, BoxLayout.Y_AXIS));
        simSettingsPanel.add(wrapConfig(numExecutionsLabel, numExecutionsSpinner));
        simSettingsPanel.add(wrapConfig(numNetworksLabel, numNetworksSpinner));
        JPanel checkPanel = new JPanel(new GridLayout(0, 1));
        checkPanel.setBorder(BorderFactory.createTitledBorder("Threats"));
        checkPanel.add(collusionCheckBox);
        checkPanel.add(oscillatingWSNsCheckBox);
        checkPanel.add(dynamicWSNsCheckBox);
        simSettingsPanel.add(checkPanel);

        JPanel viewPanel = new JPanel(new GridLayout(0, 1));
        viewPanel.add(showIdsCheckBox);
        viewPanel.add(showLinksCheckBox);
        viewPanel.add(showRangesCheckBox);
        viewPanel.add(showGridCheckBox);

        settingsTabs.addTab("Topology", topologyPanel);
        settingsTabs.addTab("Scenario", scenarioPanel);
        settingsTabs.addTab("Sim Settings", simSettingsPanel);
        settingsTabs.addTab("View", viewPanel);

        sidebar.add(settingsTabs, BorderLayout.CENTER);

        JPanel modelPanel = new JPanel(new BorderLayout());
        modelPanel.setBorder(new EmptyBorder(5,5,5,5));
        modelPanel.add(TRModelLabel, BorderLayout.NORTH);
        modelPanel.add(TRModelComboBox, BorderLayout.CENTER);
        sidebar.add(modelPanel, BorderLayout.SOUTH);

        this.add(sidebar, BorderLayout.WEST);

        JSplitPane centerSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        centerSplit.setResizeWeight(0.6);

        JPanel mapContainer = new JPanel(new BorderLayout());
        mapContainer.add(networkPanelContainer, BorderLayout.CENTER);

        JPanel legendWrapper = new JPanel(new BorderLayout());
        legendWrapper.setPreferredSize(new Dimension(150, 0));
        legendWrapper.add(legendPanelContainer, BorderLayout.CENTER);
        mapContainer.add(legendWrapper, BorderLayout.EAST);

        centerSplit.setTopComponent(mapContainer);

        JTabbedPane bottomTabs = new JTabbedPane();
        outcomesPanelsPanel.setLayout(new BorderLayout());
        outcomesPanelsPanel.add(outcomesTabbedPane, BorderLayout.CENTER);

        messagePanel.setLayout(new BorderLayout());
        messagePanel.add(messagesScrollPane, BorderLayout.CENTER);

        parametersPanel.setLayout(new BoxLayout(parametersPanel, BoxLayout.Y_AXIS));
        JPanel pSettings = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        pSettings.add(parametersFileRadioButton);
        pSettings.add(parametersFileTextField);
        pSettings.add(browseButton);
        pSettings.add(customizedParametersRadioButton);
        pSettings.add(applyParametersChangesButton);
        pSettings.add(saveParametersFileContentButton);
        parametersPanel.add(pSettings);

        JSplitPane paramSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        paramSplit.setLeftComponent(TRMParametersScrollPane);
        paramSplit.setRightComponent(new JScrollPane(parametersFileContentTextArea));
        parametersPanel.add(paramSplit);

        bottomTabs.addTab("Charts", outcomesPanelsPanel);
        bottomTabs.addTab("Log", messagePanel);
        bottomTabs.addTab("Parameters", parametersPanel);

        centerSplit.setBottomComponent(bottomTabs);
        this.add(centerSplit, BorderLayout.CENTER);
    }

    private JPanel wrapConfig(JLabel label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(label, BorderLayout.WEST);
        p.add(comp, BorderLayout.CENTER);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        return p;
    }

    private void newWSNButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            int minNumSensors = (Integer)minNumSensorsSpinner.getValue();
            int maxNumSensors = (Integer)maxNumSensorsSpinner.getValue();
            double probClients = percentageClientsSlider.getValue()/(double)percentageClientsSlider.getMaximum();
            double probRelay = percentageRelayServersSlider.getValue()/(double)percentageRelayServersSlider.getMaximum();
            double probMalicious = percentageMaliciousServersSlider.getValue()/(double)percentageMaliciousServersSlider.getMaximum();
            double radioRange = radioRangeSlider.getValue()/(double)radioRangeSlider.getMaximum();
            boolean dynamic = dynamicWSNsCheckBox.isSelected();
            boolean oscillating = oscillatingWSNsCheckBox.isSelected();
            boolean collusion = collusionCheckBox.isSelected();
            C.set_delay(1000*delaySlider.getValue()/delaySlider.getMaximum());
            Network network = C.createNewNetwork(minNumSensors,maxNumSensors, probClients,probRelay,probMalicious,radioRange, dynamic, oscillating, collusion);
            if (network != null) {
                paintNetwork(network, C.get_requiredService());
                updateButtonStates(true);
                messagesTextArea.append("New WSN created\n");
                clearCharts();
            }
        } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, ex.getMessage()); }
    }

    private void runSimulationsButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            C.getResultRepository().clearRepository();
            int numExecutions = (Integer)numExecutionsSpinner.getValue();
            int numNetworks = (Integer)numNetworksSpinner.getValue();
            int minNumSensors = (Integer)minNumSensorsSpinner.getValue();
            int maxNumSensors = (Integer)maxNumSensorsSpinner.getValue();
            double probClients = percentageClientsSlider.getValue()/(double)percentageClientsSlider.getMaximum();
            double probRelay = percentageRelayServersSlider.getValue()/(double)percentageRelayServersSlider.getMaximum();
            double probMalicious = percentageMaliciousServersSlider.getValue()/(double)percentageMaliciousServersSlider.getMaximum();
            double radioRange = radioRangeSlider.getValue()/(double)radioRangeSlider.getMaximum();
            boolean dynamic = dynamicWSNsCheckBox.isSelected();
            boolean oscillating = oscillatingWSNsCheckBox.isSelected();
            boolean collusion = collusionCheckBox.isSelected();
            C.set_delay(1000*delaySlider.getValue()/delaySlider.getMaximum());
            messagesTextArea.append("Starting simulations...\n");
            simulationComponentsEnabling(true);
            stopSimulationsButton.setEnabled(true);
            clearCharts();
            C.runSimulations(this,minNumSensors,maxNumSensors, probClients,probRelay,probMalicious,radioRange, dynamic,oscillating,collusion,numNetworks,numExecutions);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void stopSimulationsButtonActionPerformed(java.awt.event.ActionEvent evt) {
        C.stopSimulations();
        simulationComponentsEnabling(false);
        stopSimulationsButton.setEnabled(false);
    }

    private void runTRMButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            C.getResultRepository().clearRepository();
            int numExecutions = (Integer)numExecutionsSpinner.getValue();
            boolean dynamic = dynamicWSNsCheckBox.isSelected();
            boolean oscillating = oscillatingWSNsCheckBox.isSelected();
            boolean collusion = collusionCheckBox.isSelected();
            C.set_delay(1000*delaySlider.getValue()/delaySlider.getMaximum());
            simulationComponentsEnabling(true);
            stopTRMButton.setEnabled(true);
            clearCharts();
            C.runTRM_WSN(this, dynamic, oscillating, collusion, numExecutions);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void stopTRMButtonActionPerformed(java.awt.event.ActionEvent evt) {
        C.stopSimulations();
        simulationComponentsEnabling(false);
        stopTRMButton.setEnabled(false);
    }

    private void exportDataButtonActionPerformed(java.awt.event.ActionEvent evt) {
        C.getResultRepository().exportToCSV(this);
    }

    private void updateButtonStates(boolean wsnLoaded) {
        resetWSNButton.setEnabled(wsnLoaded);
        runTRMButton.setEnabled(wsnLoaded);
        saveWSNButton.setEnabled(wsnLoaded);
    }

    private void simulationComponentsEnabling(boolean running) {
        boolean enable = !running;
        runTRMButton.setEnabled(enable);
        runSimulationsButton.setEnabled(enable);
        newWSNButton.setEnabled(enable);
    }

    private void clearCharts() {
        for (OutcomesPanel p : outcomesPanels) {
            p.clearPanel();
            p.drawAxes();
        }
    }

    public void update(Observable observable, Object arg) {
        try {
            if (arg instanceof Network)
                paintNetwork((Network)arg,C.get_requiredService());
            else if (arg instanceof Collection) {
                Collection<Outcome> outcomes = (Collection<Outcome>) arg;
                C.getResultRepository().addAllOutcomes(outcomes);
                for (OutcomesPanel outcomesPanel : outcomesPanels) {
                    if (outcomesPanel.isShowing()) outcomesPanel.plotOutcomes(outcomes);
                    else outcomesPanel.setOutcomes(outcomes);
                }
            } else if (arg instanceof String) {
                String msg = ((String)arg).replaceFirst("selected TRM",(String)TRModelComboBox.getSelectedItem());
                messagesTextArea.insert(msg, 0);
                if (msg.startsWith("Finishing")) {
                    simulationComponentsEnabling(false);
                    stopTRMButton.setEnabled(false);
                    stopSimulationsButton.setEnabled(false);
                    messagesTextArea.insert("Simulation completed.\n", 0);
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    protected void paintNetwork(Network network, Service requiredService) throws Exception {
        double radioRange = radioRangeSlider.getValue()/(double)radioRangeSlider.getMaximum();
        if (networkPanel.isShowing())
            networkPanel.paintNetwork(network, requiredService, radioRange,
                    showRangesCheckBox.isSelected(), showLinksCheckBox.isSelected(),
                    showIdsCheckBox.isSelected(), showGridCheckBox.isSelected());
        C.sleep();
    }
    private javax.swing.JButton exportDataButton;
    private javax.swing.JScrollPane TRMParametersScrollPane;
    private javax.swing.JComboBox TRModelComboBox;
    private javax.swing.JLabel TRModelLabel;
    private javax.swing.JMenu TRModelMenu;
    private javax.swing.JButton applyParametersChangesButton;
    private javax.swing.JButton browseButton;
    private javax.swing.JCheckBox collusionCheckBox;
    private javax.swing.JRadioButton customizedParametersRadioButton;
    private javax.swing.JLabel delayLabel;
    private javax.swing.JSlider delaySlider;
    private javax.swing.JTextField delayTextField;
    private javax.swing.JCheckBox dynamicWSNsCheckBox;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JLabel legendLabel;
    private javax.swing.JPanel legendPanelContainer;
    private javax.swing.JButton loadWSNButton;
    private javax.swing.JLabel maxNumSensorsLabel;
    private javax.swing.JSpinner maxNumSensorsSpinner;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JPanel messagePanel;
    private javax.swing.JScrollPane messagesScrollPane;
    private javax.swing.JTextArea messagesTextArea;
    private javax.swing.JLabel minNumSensorsLabel;
    private javax.swing.JSpinner minNumSensorsSpinner;
    private javax.swing.JLabel neighborsLabel;
    private javax.swing.JList neighborsList;
    private javax.swing.JScrollPane neighborsScrollPane;
    private javax.swing.JPanel networkPanelContainer;
    private javax.swing.JButton newWSNButton;
    private javax.swing.JLabel numExecutionsLabel;
    private javax.swing.JSpinner numExecutionsSpinner;
    private javax.swing.JLabel numNetworksLabel;
    private javax.swing.JSpinner numNetworksSpinner;
    private javax.swing.JCheckBox oscillatingWSNsCheckBox;
    private javax.swing.JPanel outcomesPanelsPanel;
    private javax.swing.JTabbedPane outcomesTabbedPane;
    private javax.swing.JTextArea parametersFileContentTextArea;
    private javax.swing.JRadioButton parametersFileRadioButton;
    private javax.swing.JTextField parametersFileTextField;
    private javax.swing.JMenu parametersMenu;
    private javax.swing.JPanel parametersPanel;
    private javax.swing.ButtonGroup parametersSourceButtonGroup;
    private javax.swing.JLabel percentageClientsLabel;
    private javax.swing.JSlider percentageClientsSlider;
    private javax.swing.JTextField percentageClientsTextField;
    private javax.swing.JLabel percentageMaliciousServersLabel;
    private javax.swing.JSlider percentageMaliciousServersSlider;
    private javax.swing.JTextField percentageMaliciousServersTextField;
    private javax.swing.JLabel percentageRelayServersLabel;
    private javax.swing.JSlider percentageRelayServersSlider;
    private javax.swing.JTextField percentageRelayServersTextField;
    private javax.swing.JLabel radioRangeLabel;
    private javax.swing.JSlider radioRangeSlider;
    private javax.swing.JTextField radioRangeTextField;
    private javax.swing.JButton resetWSNButton;
    private javax.swing.JButton runSimulationsButton;
    private javax.swing.JButton runTRMButton;
    private javax.swing.JButton saveParametersFileContentButton;
    private javax.swing.JButton saveWSNButton;
    private javax.swing.JLabel sensorIdLabel;
    private javax.swing.JTextField sensorIdTextField;
    private javax.swing.JPanel sensorPropertiesPanel;
    private javax.swing.JCheckBox showGridCheckBox;
    private javax.swing.JCheckBox showIdsCheckBox;
    private javax.swing.JCheckBox showLinksCheckBox;
    private javax.swing.JCheckBox showRangesCheckBox;
    private javax.swing.JMenu simulationsMenu;
    private javax.swing.JButton stopSimulationsButton;
    private javax.swing.JButton stopTRMButton;
    private javax.swing.JMenu wsnMenu;
    private javax.swing.JLabel xCoordinateLabel;
    private javax.swing.JTextField xCoordinateTextField;
    private javax.swing.JLabel yCoordinateLabel;
    private javax.swing.JTextField yCoordinateTextField;

    private NetworkPanel networkPanel;
    private LegendPanel legendPanel;
    private Collection<OutcomesPanel> outcomesPanels;

    private void loadWSNButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            JFileChooser fileChooser = new JFileChooser("./wsn");
            fileChooser.setDialogTitle("Load WSN");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter(){
                public boolean accept(java.io.File f) { return (f.isDirectory() || f.getName().toLowerCase().endsWith(".xml")); }
                public String getDescription() { return "XML Files"; }
            });
            fileChooser.showOpenDialog(this);
            Network network = null;
            if (fileChooser.getSelectedFile() != null) {
                network = C.loadCurrentNetwork(fileChooser.getSelectedFile().getCanonicalPath());
                if (network != null) {
                    JOptionPane.showMessageDialog(this,"WSN loaded successfully","Info",JOptionPane.INFORMATION_MESSAGE);
                    paintNetwork(network,C.get_requiredService());
                    updateButtonStates(true);
                }
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); ex.printStackTrace(); }
    }

    private void saveWSNButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            JFileChooser fileChooser = new JFileChooser("./wsn");
            fileChooser.setDialogTitle("Save WSN");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter(){
                public boolean accept(java.io.File f) { return (f.isDirectory() || f.getName().toLowerCase().endsWith(".xml")); }
                public String getDescription() { return "XML Files"; }
            });
            fileChooser.showSaveDialog(this);
            if (fileChooser.getSelectedFile() != null){
                C.saveCurrentNetwork(fileChooser.getSelectedFile().getCanonicalPath());
                JOptionPane.showMessageDialog(this,"WSN saved successfully","Info",JOptionPane.INFORMATION_MESSAGE);
                paintNetwork(C.get_currentNetwork(),C.get_requiredService());
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
    }

    private void resetWSNButtonActionPerformed(java.awt.event.ActionEvent evt) { C.resetCurrentNetwork(); }
    private void parametersFileRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {}
    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {}
    private void applyParametersChangesButtonActionPerformed(java.awt.event.ActionEvent evt) {}
    private void saveParametersFileContentButtonActionPerformed(java.awt.event.ActionEvent evt) {}
    private void showIdsCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) { try{paintNetwork(C.get_currentNetwork(),C.get_requiredService());}catch(Exception e){} }
    private void showLinksCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) { try{paintNetwork(C.get_currentNetwork(),C.get_requiredService());}catch(Exception e){} }
    private void showRangesCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) { try{paintNetwork(C.get_currentNetwork(),C.get_requiredService());}catch(Exception e){} }
    private void showGridCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) { try{paintNetwork(C.get_currentNetwork(),C.get_requiredService());}catch(Exception e){} }
    private void networkPanelContainerMouseClicked(java.awt.event.MouseEvent evt) {
        if (!C.isSimulationRunning()) {
            Point coordinate = networkPanel.getCoordinateAtPosition(evt.getX(), evt.getY());
            Sensor sensor = C.getSensorAtCoordinate(coordinate.getX(), coordinate.getY());
            if (sensor != null) {
                sensorIdTextField.setText(String.valueOf(sensor.id()));
            }
        }
    }
    private void neighborsListMouseClicked(java.awt.event.MouseEvent evt) {}
    private void minNumSensorsSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {}
    private void maxNumSensorsSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {}
    private void percentageClientsSliderStateChanged(javax.swing.event.ChangeEvent evt) { percentageClientsTextField.setText(String.valueOf(percentageClientsSlider.getValue())); }
    private void percentageRelayServersSliderStateChanged(javax.swing.event.ChangeEvent evt) { percentageRelayServersTextField.setText(String.valueOf(percentageRelayServersSlider.getValue())); }
    private void percentageMaliciousServersSliderStateChanged(javax.swing.event.ChangeEvent evt) { percentageMaliciousServersTextField.setText(String.valueOf(percentageMaliciousServersSlider.getValue())); }
    private void radioRangeSliderStateChanged(javax.swing.event.ChangeEvent evt) { radioRangeTextField.setText(String.valueOf(radioRangeSlider.getValue())); }
    private void delaySliderStateChanged(javax.swing.event.ChangeEvent evt) {
        delayTextField.setText(String.valueOf(delaySlider.getValue()));
        C.set_delay(1000*delaySlider.getValue()/delaySlider.getMaximum());
    }
}