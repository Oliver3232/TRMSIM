package es.ants.felixgm.trmsim_wsn.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

final class ModernLayoutInstaller {
    private static final Color PAGE_BG = new Color(246, 249, 255);
    private static final Color CARD_BG = new Color(255, 255, 255);
    private static final Color EDGE = new Color(206, 218, 236);
    private static final int CONFIG_LABEL_WIDTH = 235;
    private static final int CONFIG_INPUT_WIDTH = 72;

    private ModernLayoutInstaller() {}

    static void install(
            JFrame frame,
            javax.swing.JButton newWSNButton, javax.swing.JButton loadWSNButton, javax.swing.JButton saveWSNButton,
            javax.swing.JButton resetWSNButton, javax.swing.JButton runTRMButton, javax.swing.JButton stopTRMButton,
            javax.swing.JButton runSimulationsButton, javax.swing.JButton stopSimulationsButton, javax.swing.JButton exportDataButton,
            JLabel TRModelLabel, JComboBox TRModelComboBox,
            JLabel minNumSensorsLabel, JSpinner minNumSensorsSpinner, JLabel maxNumSensorsLabel, JSpinner maxNumSensorsSpinner,
            JLabel radioRangeLabel, JSlider radioRangeSlider, JTextField radioRangeTextField,
            JLabel percentageClientsLabel, JSlider percentageClientsSlider, JTextField percentageClientsTextField,
            JLabel percentageMaliciousServersLabel, JSlider percentageMaliciousServersSlider, JTextField percentageMaliciousServersTextField,
            JLabel percentageRelayServersLabel, JSlider percentageRelayServersSlider, JTextField percentageRelayServersTextField,
            JLabel delayLabel, JSlider delaySlider, JTextField delayTextField,
            JLabel numExecutionsLabel, JSpinner numExecutionsSpinner,
            JLabel numNetworksLabel, JSpinner numNetworksSpinner,
            JCheckBox collusionCheckBox, JCheckBox oscillatingWSNsCheckBox, JCheckBox dynamicWSNsCheckBox,
            JCheckBox showIdsCheckBox, JCheckBox showLinksCheckBox, JCheckBox showRangesCheckBox, JCheckBox showGridCheckBox,
            JComboBox visualThemeComboBox, JComboBox cameraPresetComboBox, JCheckBox enable3DNavigationCheckBox, javax.swing.JButton fullscreenGraphButton,
            JPanel parametersPanel, JPanel messagePanel,
            JPanel networkPanelContainer, JComponent dashboardLegendPanel,
            JPanel outcomesPanelsPanel, JTabbedPane outcomesTabbedPane
    ) {
        frame.getContentPane().removeAll();
        frame.getContentPane().setBackground(PAGE_BG);
        frame.setLayout(new BorderLayout());

        frame.add(createTopHeader(
                newWSNButton, loadWSNButton, saveWSNButton, resetWSNButton,
                runTRMButton, stopTRMButton, runSimulationsButton, stopSimulationsButton, exportDataButton,
                TRModelLabel, TRModelComboBox
        ), BorderLayout.NORTH);

        frame.add(createLeftSettingsPane(
                minNumSensorsLabel, minNumSensorsSpinner, maxNumSensorsLabel, maxNumSensorsSpinner,
                radioRangeLabel, radioRangeSlider, radioRangeTextField,
                percentageClientsLabel, percentageClientsSlider, percentageClientsTextField,
                percentageMaliciousServersLabel, percentageMaliciousServersSlider, percentageMaliciousServersTextField,
                percentageRelayServersLabel, percentageRelayServersSlider, percentageRelayServersTextField,
                delayLabel, delaySlider, delayTextField,
                numExecutionsLabel, numExecutionsSpinner,
                numNetworksLabel, numNetworksSpinner,
                collusionCheckBox, oscillatingWSNsCheckBox, dynamicWSNsCheckBox,
                showIdsCheckBox, showLinksCheckBox, showRangesCheckBox, showGridCheckBox,
                parametersPanel, messagePanel
        ), BorderLayout.WEST);

        frame.add(createMainContent(
                networkPanelContainer, dashboardLegendPanel, outcomesPanelsPanel, outcomesTabbedPane,
                delayLabel, delaySlider, delayTextField,
                showIdsCheckBox, showLinksCheckBox, showRangesCheckBox, showGridCheckBox,
                visualThemeComboBox, cameraPresetComboBox, enable3DNavigationCheckBox, fullscreenGraphButton
        ), BorderLayout.CENTER);
    }

    private static JPanel createTopHeader(
            javax.swing.JButton newWSNButton, javax.swing.JButton loadWSNButton, javax.swing.JButton saveWSNButton,
            javax.swing.JButton resetWSNButton, javax.swing.JButton runTRMButton, javax.swing.JButton stopTRMButton,
            javax.swing.JButton runSimulationsButton, javax.swing.JButton stopSimulationsButton, javax.swing.JButton exportDataButton,
            JLabel TRModelLabel, JComboBox TRModelComboBox
    ) {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, EDGE));
        header.setBackground(CARD_BG);

        JPanel actions = new JPanel(new WrapLayout(WrapLayout.CENTER, 8, 4));
        actions.setOpaque(false);
        actions.add(newWSNButton);
        actions.add(loadWSNButton);
        actions.add(saveWSNButton);
        actions.add(resetWSNButton);
        actions.add(runTRMButton);
        actions.add(stopTRMButton);
        actions.add(runSimulationsButton);
        actions.add(stopSimulationsButton);
        actions.add(exportDataButton);
        actions.setAlignmentX(0.0f);
        header.add(actions);

        JPanel modelRow = new JPanel(new BorderLayout());
        modelRow.setOpaque(false);
        modelRow.setBorder(new EmptyBorder(0, 8, 4, 8));
        JPanel modelBox = new JPanel(new BorderLayout(4, 4));
        modelBox.setOpaque(false);
        modelBox.setPreferredSize(new Dimension(220, 44));
        TRModelLabel.setHorizontalAlignment(SwingConstants.LEFT);
        modelBox.add(TRModelLabel, BorderLayout.NORTH);
        TRModelComboBox.setPreferredSize(new Dimension(170, 28));
        modelBox.add(TRModelComboBox, BorderLayout.CENTER);
        modelRow.add(modelBox, BorderLayout.EAST);
        modelRow.setAlignmentX(0.0f);
        header.add(modelRow);

        return header;
    }

    private static JPanel createLeftSettingsPane(
            JLabel minNumSensorsLabel, JSpinner minNumSensorsSpinner, JLabel maxNumSensorsLabel, JSpinner maxNumSensorsSpinner,
            JLabel radioRangeLabel, JSlider radioRangeSlider, JTextField radioRangeTextField,
            JLabel percentageClientsLabel, JSlider percentageClientsSlider, JTextField percentageClientsTextField,
            JLabel percentageMaliciousServersLabel, JSlider percentageMaliciousServersSlider, JTextField percentageMaliciousServersTextField,
            JLabel percentageRelayServersLabel, JSlider percentageRelayServersSlider, JTextField percentageRelayServersTextField,
            JLabel delayLabel, JSlider delaySlider, JTextField delayTextField,
            JLabel numExecutionsLabel, JSpinner numExecutionsSpinner,
            JLabel numNetworksLabel, JSpinner numNetworksSpinner,
            JCheckBox collusionCheckBox, JCheckBox oscillatingWSNsCheckBox, JCheckBox dynamicWSNsCheckBox,
            JCheckBox showIdsCheckBox, JCheckBox showLinksCheckBox, JCheckBox showRangesCheckBox, JCheckBox showGridCheckBox,
            JPanel parametersPanel, JPanel messagePanel
    ) {
        JPanel sidebar = createCard(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(500, 0));
        sidebar.setMinimumSize(new Dimension(430, 0));
        sidebar.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, EDGE),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JTabbedPane settingsTabs = new JTabbedPane();
        settingsTabs.addTab("Simulation Settings", createSettingsScroll(
                minNumSensorsLabel, minNumSensorsSpinner, maxNumSensorsLabel, maxNumSensorsSpinner,
                radioRangeLabel, radioRangeSlider, radioRangeTextField,
                percentageClientsLabel, percentageClientsSlider, percentageClientsTextField,
                percentageMaliciousServersLabel, percentageMaliciousServersSlider, percentageMaliciousServersTextField,
                percentageRelayServersLabel, percentageRelayServersSlider, percentageRelayServersTextField,
                delayLabel, delaySlider, delayTextField,
                numExecutionsLabel, numExecutionsSpinner,
                numNetworksLabel, numNetworksSpinner,
                collusionCheckBox, oscillatingWSNsCheckBox, dynamicWSNsCheckBox,
                showIdsCheckBox, showLinksCheckBox, showRangesCheckBox, showGridCheckBox
        ));

        parametersPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
        parametersPanel.setPreferredSize(null);
        parametersPanel.setMinimumSize(new Dimension(0, 0));
        parametersPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        JScrollPane parametersScroll = new JScrollPane(parametersPanel);
        parametersScroll.setBorder(BorderFactory.createEmptyBorder());
        parametersScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        parametersScroll.getVerticalScrollBar().setUnitIncrement(14);
        settingsTabs.addTab("Simulation Parameters", parametersScroll);
        JSplitPane leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        leftSplit.setBorder(null);
        leftSplit.setResizeWeight(0.62);
        leftSplit.setTopComponent(settingsTabs);
        messagePanel.setBorder(BorderFactory.createTitledBorder("Console Log"));
        leftSplit.setBottomComponent(messagePanel);
        sidebar.add(leftSplit, BorderLayout.CENTER);
        return sidebar;
    }

    private static JScrollPane createSettingsScroll(
            JLabel minNumSensorsLabel, JSpinner minNumSensorsSpinner, JLabel maxNumSensorsLabel, JSpinner maxNumSensorsSpinner,
            JLabel radioRangeLabel, JSlider radioRangeSlider, JTextField radioRangeTextField,
            JLabel percentageClientsLabel, JSlider percentageClientsSlider, JTextField percentageClientsTextField,
            JLabel percentageMaliciousServersLabel, JSlider percentageMaliciousServersSlider, JTextField percentageMaliciousServersTextField,
            JLabel percentageRelayServersLabel, JSlider percentageRelayServersSlider, JTextField percentageRelayServersTextField,
            JLabel delayLabel, JSlider delaySlider, JTextField delayTextField,
            JLabel numExecutionsLabel, JSpinner numExecutionsSpinner,
            JLabel numNetworksLabel, JSpinner numNetworksSpinner,
            JCheckBox collusionCheckBox, JCheckBox oscillatingWSNsCheckBox, JCheckBox dynamicWSNsCheckBox,
            JCheckBox showIdsCheckBox, JCheckBox showLinksCheckBox, JCheckBox showRangesCheckBox, JCheckBox showGridCheckBox
    ) {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(10, 10, 10, 10));

        minNumSensorsLabel.setText("Minimum number of sensors");
        maxNumSensorsLabel.setText("Maximum number of sensors");
        numExecutionsLabel.setText("Simulation execution threads");
        numNetworksLabel.setText("Generated networks per run");

        JPanel basicPanel = createSectionPanel("Simulation Basics");
        basicPanel.add(wrapConfig(minNumSensorsLabel, minNumSensorsSpinner));
        basicPanel.add(wrapConfig(maxNumSensorsLabel, maxNumSensorsSpinner));
        basicPanel.add(wrapConfig(numExecutionsLabel, numExecutionsSpinner));
        basicPanel.add(wrapConfig(numNetworksLabel, numNetworksSpinner));
        content.add(basicPanel);
        //content.add(spacer(8));

        JPanel threadsPanel = createSectionPanel("Threads & Dynamics");
        threadsPanel.add(checkGroup(collusionCheckBox, oscillatingWSNsCheckBox, dynamicWSNsCheckBox));
        content.add(threadsPanel);
        //content.add(spacer(8));

        JPanel parametersSection = createSectionPanel("Parameters");
        parametersSection.add(wrapSliderConfig(radioRangeLabel, radioRangeSlider, radioRangeTextField));
        parametersSection.add(wrapSliderConfig(percentageClientsLabel, percentageClientsSlider, percentageClientsTextField));
        parametersSection.add(wrapSliderConfig(percentageMaliciousServersLabel, percentageMaliciousServersSlider, percentageMaliciousServersTextField));
        parametersSection.add(wrapSliderConfig(percentageRelayServersLabel, percentageRelayServersSlider, percentageRelayServersTextField));
        content.add(parametersSection);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        return scrollPane;
    }

    private static JSplitPane createMainContent(
            JPanel networkPanelContainer, JComponent dashboardLegendPanel, JPanel outcomesPanelsPanel, JTabbedPane outcomesTabbedPane,
            JLabel delayLabel, JSlider delaySlider, JTextField delayTextField,
            JCheckBox showIdsCheckBox, JCheckBox showLinksCheckBox, JCheckBox showRangesCheckBox, JCheckBox showGridCheckBox,
            JComboBox visualThemeComboBox, JComboBox cameraPresetComboBox, JCheckBox enable3DNavigationCheckBox, javax.swing.JButton fullscreenGraphButton
    ) {
        JSplitPane centerSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        centerSplit.setResizeWeight(0.68);
        centerSplit.setBorder(null);
        centerSplit.setTopComponent(createMapContainer(
                networkPanelContainer, dashboardLegendPanel,
                delayLabel, delaySlider, delayTextField,
                showIdsCheckBox, showLinksCheckBox, showRangesCheckBox, showGridCheckBox,
                visualThemeComboBox, cameraPresetComboBox, enable3DNavigationCheckBox, fullscreenGraphButton
        ));
        centerSplit.setBottomComponent(createChartsContainer(outcomesPanelsPanel, outcomesTabbedPane));
        return centerSplit;
    }

    private static JPanel createMapContainer(
            JPanel networkPanelContainer, JComponent dashboardLegendPanel,
            JLabel delayLabel, JSlider delaySlider, JTextField delayTextField,
            JCheckBox showIdsCheckBox, JCheckBox showLinksCheckBox, JCheckBox showRangesCheckBox, JCheckBox showGridCheckBox,
            JComboBox visualThemeComboBox, JComboBox cameraPresetComboBox, JCheckBox enable3DNavigationCheckBox, javax.swing.JButton fullscreenGraphButton
    ) {
        JPanel mapContainer = new JPanel(new BorderLayout(10, 10));
        mapContainer.setBorder(new EmptyBorder(10, 10, 10, 10));
        mapContainer.setBackground(PAGE_BG);

        JPanel topStrip = new JPanel(new BorderLayout(10, 0));
        topStrip.setOpaque(false);

        JPanel legendCard = createCard(new BorderLayout());
        legendCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(180, 200, 226), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        JLabel legendTitle = new JLabel("Legend");
        legendTitle.setBorder(new EmptyBorder(0, 2, 0, 2));
        legendCard.add(legendTitle, BorderLayout.NORTH);
        legendCard.add(dashboardLegendPanel, BorderLayout.CENTER);
        legendCard.setPreferredSize(new Dimension(360, 112));
        legendCard.setMinimumSize(new Dimension(320, 100));

        JPanel liveControlsCard = createCard(new BorderLayout());
        liveControlsCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(180, 200, 226), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        JLabel liveTitle = new JLabel("Live Simulation Controls");
        liveTitle.setBorder(new EmptyBorder(0, 2, 8, 2));
        liveControlsCard.add(liveTitle, BorderLayout.NORTH);
        JPanel liveContent = new JPanel(new WrapLayout(WrapLayout.LEFT, 8, 4));
        liveContent.setOpaque(false);
        liveContent.add(wrapInlineDelay(delayLabel, delaySlider, delayTextField));
        liveContent.add(checkGroupInline(showIdsCheckBox, showLinksCheckBox, showRangesCheckBox, showGridCheckBox));
        liveContent.add(wrapRenderControls(visualThemeComboBox, cameraPresetComboBox, enable3DNavigationCheckBox, fullscreenGraphButton));
        liveControlsCard.add(liveContent, BorderLayout.CENTER);
        topStrip.add(liveControlsCard, BorderLayout.CENTER);
        topStrip.add(legendCard, BorderLayout.EAST);

        JPanel networkCard = createCard(new BorderLayout());
        networkCard.add(networkPanelContainer, BorderLayout.CENTER);

        mapContainer.add(topStrip, BorderLayout.NORTH);
        mapContainer.add(networkCard, BorderLayout.CENTER);
        return mapContainer;
    }

    private static JPanel createChartsContainer(JPanel outcomesPanelsPanel, JTabbedPane outcomesTabbedPane) {
        JPanel chartsCard = createCard(new BorderLayout());
        chartsCard.setBorder(new CompoundBorder(
                new LineBorder(EDGE, 1, true),
                new EmptyBorder(8, 8, 8, 8)
        ));

        outcomesPanelsPanel.setPreferredSize(null);
        outcomesPanelsPanel.setMinimumSize(new Dimension(0, 220));
        outcomesTabbedPane.setPreferredSize(null);

        chartsCard.add(outcomesPanelsPanel, BorderLayout.CENTER);
        return chartsCard;
    }

    private static JPanel createCard(BorderLayout layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(CARD_BG);
        return panel;
    }

    private static JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(0.0f);
        panel.setBorder(new CompoundBorder(
                new TitledBorder(new LineBorder(new Color(210, 220, 238), 1, true), title),
                new EmptyBorder(6, 8, 8, 8)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        return panel;
    }

    private static JPanel spacer(int height) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(1, height));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        return panel;
    }

    private static JLabel hintLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(88, 102, 122));
        label.setBorder(new EmptyBorder(2, 2, 4, 2));
        label.setAlignmentX(0.0f);
        return label;
    }

    private static JPanel checkGroup(JCheckBox... checkBoxes) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 0, 0));
        for (int i = 0; i < checkBoxes.length; i++) {
            JCheckBox checkBox = checkBoxes[i];
            checkBox.setOpaque(false);
            checkBox.setAlignmentX(0.0f);
            checkBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
            panel.add(checkBox);
            if (i < checkBoxes.length - 1) {
                panel.add(spacer(1));
            }
        }
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, checkBoxes.length * 23));
        return panel;
    }

    private static JPanel checkGroupInline(JCheckBox... checkBoxes) {
        JPanel panel = new JPanel(new WrapLayout(WrapLayout.LEFT, 6, 2));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(2, 0, 2, 0));
        for (JCheckBox checkBox : checkBoxes) {
            checkBox.setOpaque(false);
            panel.add(checkBox);
        }
        return panel;
    }

    private static JPanel wrapConfig(JLabel label, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(8, 4));
        panel.setOpaque(false);
        panel.setAlignmentX(0.0f);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        panel.setBorder(new EmptyBorder(2, 0, 0, 0));

        label.setPreferredSize(new Dimension(CONFIG_LABEL_WIDTH, 24));
        label.setMinimumSize(new Dimension(CONFIG_LABEL_WIDTH, 24));
        panel.add(label, BorderLayout.WEST);

        component.setPreferredSize(new Dimension(CONFIG_INPUT_WIDTH, 24));
        component.setMinimumSize(new Dimension(CONFIG_INPUT_WIDTH, 24));
        component.setMaximumSize(new Dimension(CONFIG_INPUT_WIDTH, 24));
        panel.add(component, BorderLayout.EAST);

        return panel;
    }

    private static JPanel wrapSliderConfig(JLabel label, JSlider slider, JTextField valueField) {
        JPanel panel = new JPanel(new BorderLayout(8, 4));
        panel.setOpaque(false);
        panel.setAlignmentX(0.0f);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        panel.setBorder(new EmptyBorder(2, 0, 2, 0));

        label.setPreferredSize(new Dimension(CONFIG_LABEL_WIDTH, 24));
        label.setMinimumSize(new Dimension(CONFIG_LABEL_WIDTH, 24));
        panel.add(label, BorderLayout.WEST);

        JPanel center = new JPanel(new BorderLayout(6, 0));
        center.setOpaque(false);
        center.add(slider, BorderLayout.CENTER);

        valueField.setEditable(false);
        valueField.setHorizontalAlignment(SwingConstants.CENTER);
        valueField.setPreferredSize(new Dimension(48, 24));
        center.add(valueField, BorderLayout.EAST);

        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private static JPanel wrapInlineDelay(JLabel label, JSlider slider, JTextField valueField) {
        JPanel panel = new JPanel(new WrapLayout(WrapLayout.LEFT, 4, 2));
        panel.setOpaque(false);
        panel.add(label);
        slider.setPreferredSize(new Dimension(132, 24));
        panel.add(slider);
        valueField.setEditable(false);
        valueField.setHorizontalAlignment(SwingConstants.CENTER);
        valueField.setPreferredSize(new Dimension(56, 24));
        panel.add(valueField);
        return panel;
    }

    private static JPanel wrapRenderControls(JComboBox visualThemeComboBox, JComboBox cameraPresetComboBox, JCheckBox enable3DNavigationCheckBox, javax.swing.JButton fullscreenGraphButton) {
        JPanel panel = new JPanel(new WrapLayout(WrapLayout.LEFT, 4, 2));
        panel.setOpaque(false);
        JLabel renderLabel = new JLabel("Render");
        panel.add(renderLabel);
        visualThemeComboBox.setPreferredSize(new Dimension(120, 24));
        panel.add(visualThemeComboBox);
        cameraPresetComboBox.setPreferredSize(new Dimension(88, 24));
        panel.add(cameraPresetComboBox);
        enable3DNavigationCheckBox.setOpaque(false);
        panel.add(enable3DNavigationCheckBox);
        fullscreenGraphButton.setMargin(new java.awt.Insets(2, 6, 2, 6));
        panel.add(fullscreenGraphButton);
        return panel;
    }
}
