package es.ants.felixgm.trmsim_wsn.gui;

final class MainWindowAssemblyController {
    private MainWindowAssemblyController() {
    }

    static void configure(TRMSim_WSN window) {
        MainWindowComponentInitializer.initialize(window);

        MainWindowFrameSetup.configure(window);

        window.exportDataButton = new javax.swing.JButton();
        MainWindowControlsSection.configure(
                window,
                window.controlsScrollPane,
                window.controlsPanel,
                window.legendPanelContainer,
                window.legendLabel,
                window.buttonsControlPanel,
                window.newWSNButton,
                window.resetWSNButton,
                window.runTRMButton,
                window.stopTRMButton,
                window.loadWSNButton,
                window.saveWSNButton,
                window.stopSimulationsButton,
                window.runSimulationsButton,
                window.exportDataButton,
                window.spinnersControlPanel,
                window.numExecutionsLabel,
                window.numExecutionsSpinner,
                window.numNetworksLabel,
                window.numNetworksSpinner,
                window.minNumSensorsLabel,
                window.minNumSensorsSpinner,
                window.maxNumSensorsLabel,
                window.maxNumSensorsSpinner,
                window.slidersControlsPanel,
                window.percentageClientsLabel,
                window.percentageClientsSlider,
                window.percentageClientsTextField,
                window.percentageRelayServersLabel,
                window.percentageRelayServersSlider,
                window.percentageRelayServersTextField,
                window.percentageMaliciousServersLabel,
                window.percentageMaliciousServersSlider,
                window.percentageMaliciousServersTextField,
                window.radioRangeLabel,
                window.radioRangeSlider,
                window.radioRangeTextField,
                window.delayLabel,
                window.delaySlider,
                window.delayTextField,
                window.TRModelLabel,
                window.TRModelComboBox,
                window.displayControlsPanel,
                window.showIdsCheckBox,
                window.showLinksCheckBox,
                window.showRangesCheckBox,
                window.showGridCheckBox,
                window.threatsControlsPanel,
                window.collusionCheckBox,
                window.oscillatingWSNsCheckBox,
                window.dynamicWSNsCheckBox,
                window.legendPanel);

        MainWindowCenterSection.configure(
                window,
                window.upperSplitPane,
                window.networkAndSensorPropertiesContainerPanel,
                window.networkPanelContainer,
                window.networkPanel,
                window.sensorPropertiesPanel,
                window.sensorIdLabel,
                window.sensorIdTextField,
                window.xCoordinateLabel,
                window.xCoordinateTextField,
                window.yCoordinateLabel,
                window.yCoordinateTextField,
                window.radioRangePropertyLabel,
                window.radioRangeSpinner,
                window.neighborsLabel,
                window.neighborsScrollPane,
                window.neighborsList,
                window.sensorTypeLabel,
                window.sensorTypeComboBox,
                window.applyChangesButton,
                window.hideSensorPropertiesPanelButton);

        MainWindowSimulationTabSection.configure(
                window,
                window.tabbedPane,
                window.simulationsPanel,
                window.simulationsSplitPane,
                window.upperPanel,
                window.upperSplitPane,
                window.controlsScrollPane,
                window.bottomPanel,
                window.outcomesPanelsPanel,
                window.outcomesTabbedPane,
                window.messagePanel,
                window.messagesScrollPane,
                window.messagesTextArea);

        MainWindowParametersTabSection.configure(
                window,
                window.tabbedPane,
                window.parametersPanel,
                window.parametersSettingsPanel,
                window.separator1,
                window.separator2,
                window.parametersFileTextField,
                window.parametersFileLabel,
                window.customizedParametersRadioButton,
                window.parametersFileRadioButton,
                window.parametersSourceLabel,
                window.applyParametersChangesButton,
                window.saveParametersFileContentButton,
                window.browseButton,
                window.parametersSourceButtonGroup,
                window.separator3,
                window.bottomParametersContainerPanel,
                window.TRMParametersScrollPane,
                window.TRM_ParametersPanelAux,
                window.parametersFileContentScrollPane,
                window.parametersFileContentTextArea,
                window.separator4);

        window.getContentPane().add(window.tabbedPane);

        window.exportDataMenuItem = new javax.swing.JMenuItem();
        MainWindowMenuSetupController.install(
                window,
                window.menuBar,
                window.wsnMenu,
                window.newWSNmenuItem,
                window.resetWSNmenuItem,
                window.loadWSNmenuItem,
                window.saveWSNmenuItem,
                window.simulationsMenu,
                window.runTRMmenuItem,
                window.stopTRMmenuItem,
                window.runSimulationsMenuItem,
                window.stopSimulationsMenuItem,
                window.exportDataMenuItem,
                window.parametersMenu,
                window.loadParametersMenuItem,
                window.saveParametersMenuItem,
                window.applyParametersChangesMenuItem,
                window.TRModelMenu,
                window.helpMenu,
                window.helpMenuItem,
                window.aboutTRMSim_WSNmenuItem);

        window.getAccessibleContext().setAccessibleParent(window);
        window.pack();
    }
}
