package es.ants.felixgm.trmsim_wsn.gui.trustmodel;


import es.ants.felixgm.trmsim_wsn.Controller;
import es.ants.felixgm.trmsim_wsn.gui.graph.SimulationGraphWorkspace;
import es.ants.felixgm.trmsim_wsn.gui.layout.MiniLegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.legendpanels.LegendPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.OutcomesPanel;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.TRMParametersPanel;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.TRMParametersPanelFactory;

import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public final class TrustModelSelectionHelper {
    public interface UiCallbacks {
        void setTrmParametersPanel(TRMParametersPanel parametersPanel);
        void applyCurrentParameters() throws Exception;
        void setClientsProbabilityControlsEnabled(boolean enabled);
        void setCurrentNetworkPanel(NetworkPanel networkPanel);
        void attachNetworkPanel(NetworkPanel networkPanel);
        void applyVisualizationControls(NetworkPanel networkPanel);
        void refreshInspectorLegendPanel();
        void clearNetworkPanel(NetworkPanel networkPanel);
        void clearNodeInspector();
    }

    public static final class SelectionResult {
        private final LegendPanel legendPanel;
        private final NetworkPanel networkPanel;
        private final Collection<OutcomesPanel> outcomesPanels;

        SelectionResult(LegendPanel legendPanel, NetworkPanel networkPanel, Collection<OutcomesPanel> outcomesPanels) {
            this.legendPanel = legendPanel;
            this.networkPanel = networkPanel;
            this.outcomesPanels = outcomesPanels;
        }

        public LegendPanel getLegendPanel() {
            return legendPanel;
        }

        public NetworkPanel getNetworkPanel() {
            return networkPanel;
        }

        public Collection<OutcomesPanel> getOutcomesPanels() {
            return outcomesPanels;
        }
    }

    private TrustModelSelectionHelper() {
    }

    public static void updateTrustModelMenuSelection(JMenu trustModelMenu, String trustModelName) {
        for (int i = 0; i < trustModelMenu.getItemCount(); i++) {
            JMenuItem trustModelMenuItem = trustModelMenu.getItem(i);
            trustModelMenuItem.setIcon(null);
            if (trustModelMenuItem.getText().equals(trustModelName)) {
                trustModelMenuItem.setIcon(new ImageIcon(ClassLoader.getSystemResource("resources/images/tick.gif")));
            }
        }
    }

    public static void configureSelectedTrustModel(
            Controller controller,
            String trustModelName,
            JTextField parametersFileTextField,
            JTextArea parametersFileContentTextArea,
            UiCallbacks callbacks) throws Exception {
        controller.set_TRModel_WSN(trustModelName);
        controller.clearCurrentNetwork();
        es.ants.felixgm.trmsim_wsn.gui.export.SimulationResultRepository.getInstance().clearRepository();

        TRMParametersPanel trmParametersPanel = TRMParametersPanelFactory.create(trustModelName);
        trmParametersPanel.set_TRMParameters(controller.get_TRMParameters());
        callbacks.setTrmParametersPanel(trmParametersPanel);
        callbacks.applyCurrentParameters();

        String defaultParametersFileName = controller.get_parametersFile();
        defaultParametersFileName = defaultParametersFileName.substring(defaultParametersFileName.lastIndexOf(File.separator) + 1);
        parametersFileTextField.setText(defaultParametersFileName);
        parametersFileContentTextArea.setText(controller.get_DefaultParametersFileContent(trustModelName));
    }

    public static SelectionResult rebuildTrustModelUi(
            String trustModelName,
            javax.swing.JPanel legendPanelContainer,
            javax.swing.JPanel networkPanelContainer,
            JTabbedPane outcomesTabbedPane,
            SimulationGraphWorkspace graphWorkspace,
            UiCallbacks callbacks) {
        legendPanelContainer.removeAll();
        legendPanelContainer.setLayout(new BorderLayout());
        networkPanelContainer.removeAll();
        outcomesTabbedPane.removeAll();

        TrustModelUiFactory.Descriptor uiDescriptor = TrustModelUiFactory.get(trustModelName);
        callbacks.setClientsProbabilityControlsEnabled(uiDescriptor.isClientsPercentageEnabled());
        LegendPanel legendPanel = uiDescriptor.createLegendPanel();
        NetworkPanel networkPanel = uiDescriptor.createNetworkPanel();
        Collection<OutcomesPanel> outcomesPanels = new ArrayList<OutcomesPanel>(uiDescriptor.createOutcomesPanels());

        legendPanelContainer.add(legendPanel, BorderLayout.CENTER);
        if (graphWorkspace != null) {
            graphWorkspace.setFullscreenLegendItems(createLegendItems(legendPanel));
        }
        legendPanel.setBackground(Color.white);
        Dimension legendSize = computeLegendPreferredSize(legendPanel);
        legendPanel.setPreferredSize(legendSize);
        legendPanel.setMinimumSize(legendSize);
        legendPanel.setSize(legendSize);
        legendPanel.plotLegend();
        legendPanelContainer.setPreferredSize(legendSize);
        legendPanelContainer.setMinimumSize(legendSize);
        legendPanelContainer.setBorder(BorderFactory.createEmptyBorder());
        legendPanelContainer.revalidate();
        legendPanelContainer.repaint();
        scheduleLegendRefresh(legendPanelContainer, legendPanel);

        callbacks.setCurrentNetworkPanel(networkPanel);
        callbacks.attachNetworkPanel(networkPanel);
        callbacks.applyVisualizationControls(networkPanel);
        networkPanelContainer.revalidate();
        networkPanelContainer.repaint();
        callbacks.clearNetworkPanel(networkPanel);

        for (OutcomesPanel outcomesPanel : outcomesPanels) {
            outcomesTabbedPane.addTab(outcomesPanel.getLabel(), outcomesPanel);
            outcomesPanel.setSize(outcomesTabbedPane.getSize());
            outcomesPanel.setOutcomes(null);
            outcomesPanel.clearPanel();
            outcomesPanel.drawAxes();
        }
        outcomesTabbedPane.revalidate();
        outcomesTabbedPane.repaint();

        return new SelectionResult(legendPanel, networkPanel, outcomesPanels);
    }

    private static java.util.List<MiniLegendPanel.Item> createLegendItems(LegendPanel legendPanel) {
        java.util.List<MiniLegendPanel.Item> items = new ArrayList<MiniLegendPanel.Item>();
        if (legendPanel == null) {
            return items;
        }
        for (LegendPanel.LegendItem legendItem : legendPanel.getLegendItems()) {
            items.add(new MiniLegendPanel.Item(legendItem.getLabel(), legendItem.getColor()));
        }
        return items;
    }

    public static Dimension computeLegendPreferredSize(LegendPanel legendPanel) {
        int itemCount = (legendPanel == null) ? 5 : Math.max(legendPanel.getLegendItems().size(), 5);
        int height = Math.max(98, 18 + (itemCount * 18));
        return new Dimension(120, height);
    }

    private static void scheduleLegendRefresh(final JPanel legendPanelContainer, final LegendPanel legendPanel) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Dimension legendSize = legendPanelContainer.getSize();
                if ((legendSize.width <= 0) || (legendSize.height <= 0)) {
                    legendSize = computeLegendPreferredSize(legendPanel);
                }
                legendPanel.setSize(legendSize);
                legendPanel.setPreferredSize(legendSize);
                legendPanel.revalidate();
                legendPanel.repaint();
                legendPanelContainer.revalidate();
                legendPanelContainer.repaint();
            }
        });
    }

    public static void resetUiAfterTrustModelSwitch(
            String trustModelName,
            javax.swing.AbstractButton resetWSNButton,
            JMenuItem resetWSNmenuItem,
            javax.swing.AbstractButton runTRMButton,
            JMenuItem runTRMmenuItem,
            javax.swing.AbstractButton saveWSNButton,
            JMenuItem saveWSNmenuItem,
            javax.swing.JPanel sensorPropertiesPanel,
            JTextArea messagesTextArea,
            UiCallbacks callbacks) {
        resetWSNButton.setEnabled(false);
        runTRMButton.setEnabled(false);
        saveWSNButton.setEnabled(false);
        resetWSNmenuItem.setEnabled(false);
        runTRMmenuItem.setEnabled(false);
        saveWSNmenuItem.setEnabled(false);
        sensorPropertiesPanel.setVisible(false);
        callbacks.clearNodeInspector();
        messagesTextArea.setText("Model changed to " + trustModelName + ". Network state cleared. Create or load a new WSN.\n");
    }
}
