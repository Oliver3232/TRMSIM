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
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Color;
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
        java.util.List<MiniLegendPanel.Item> createLegendItems();
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
        networkPanelContainer.removeAll();
        outcomesTabbedPane.removeAll();

        TrustModelUiFactory.Descriptor uiDescriptor = TrustModelUiFactory.get(trustModelName);
        callbacks.setClientsProbabilityControlsEnabled(uiDescriptor.isClientsPercentageEnabled());
        LegendPanel legendPanel = uiDescriptor.createLegendPanel();
        NetworkPanel networkPanel = uiDescriptor.createNetworkPanel();
        Collection<OutcomesPanel> outcomesPanels = new ArrayList<OutcomesPanel>(uiDescriptor.createOutcomesPanels());

        legendPanelContainer.add(legendPanel, null);
        if (graphWorkspace != null) {
            graphWorkspace.setFullscreenLegendItems(callbacks.createLegendItems());
        }
        callbacks.refreshInspectorLegendPanel();
        legendPanel.setBackground(Color.white);
        legendPanel.setSize(legendPanelContainer.getSize());
        legendPanel.plotLegend();
        legendPanelContainer.setPreferredSize(new Dimension(100, 64));

        callbacks.setCurrentNetworkPanel(networkPanel);
        callbacks.attachNetworkPanel(networkPanel);
        callbacks.applyVisualizationControls(networkPanel);
        networkPanelContainer.revalidate();
        networkPanelContainer.repaint();
        callbacks.clearNetworkPanel(networkPanel);

        int visibleCharts = 0;
        for (OutcomesPanel outcomesPanel : outcomesPanels) {
            if (visibleCharts >= 3) {
                break;
            }
            outcomesTabbedPane.addTab(outcomesPanel.getLabel(), outcomesPanel);
            outcomesPanel.setSize(outcomesTabbedPane.getSize());
            visibleCharts++;
            outcomesPanel.setOutcomes(null);
            outcomesPanel.clearPanel();
            outcomesPanel.drawAxes();
        }
        outcomesTabbedPane.revalidate();
        outcomesTabbedPane.repaint();

        return new SelectionResult(legendPanel, networkPanel, outcomesPanels);
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
