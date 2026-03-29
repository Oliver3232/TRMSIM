package es.ants.felixgm.trmsim_wsn.scenario;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

/**
 * Small dialog helper used to select one bundled predefined scenario.
 */
public final class ScenarioSelectionHelper {
    private ScenarioSelectionHelper() {
    }

    public static ScenarioDefinition chooseScenario(Component owner) throws Exception {
        List<ScenarioDefinition> scenarios = PredefinedScenarioLoader.loadBundledScenarios();
        if (scenarios.isEmpty()) {
            JOptionPane.showMessageDialog(owner, "No predefined scenarios are bundled with this build.", "Scenario Selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        JComboBox<ScenarioDefinition> comboBox = new JComboBox<ScenarioDefinition>(scenarios.toArray(new ScenarioDefinition[0]));
        JTextArea descriptionArea = new JTextArea(5, 32);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setOpaque(false);
        updateDescription(descriptionArea, (ScenarioDefinition) comboBox.getSelectedItem());

        comboBox.addActionListener(evt -> updateDescription(descriptionArea, (ScenarioDefinition) comboBox.getSelectedItem()));
        comboBox.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                updateDescription(descriptionArea, (ScenarioDefinition) comboBox.getSelectedItem());
            }

            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.add(new JLabel("Scenario:"), BorderLayout.NORTH);
        panel.add(comboBox, BorderLayout.CENTER);
        panel.add(descriptionArea, BorderLayout.SOUTH);

        int choice = JOptionPane.showConfirmDialog(
                owner,
                panel,
                "Load Predefined Scenario",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return null;
        }
        return (ScenarioDefinition) comboBox.getSelectedItem();
    }

    private static void updateDescription(JTextArea descriptionArea, ScenarioDefinition scenario) {
        if (scenario == null) {
            descriptionArea.setText("");
            return;
        }
        descriptionArea.setText(scenario.getDescription());
        descriptionArea.setCaretPosition(0);
    }
}
