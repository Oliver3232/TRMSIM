package es.ants.felixgm.trmsim_wsn.gui.parameterpanels.lftm;

import es.ants.felixgm.trmsim_wsn.trm.libs.fuzzylib.FuzzySetPanel;

import javax.swing.DefaultComboBoxModel;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import java.util.Vector;

final class LFTMPanelSupport {
    private LFTMPanelSupport() {
    }

    static Vector<DefaultComboBoxModel> createMembershipFunctionModels(int size) {
        Vector<DefaultComboBoxModel> models = new Vector<DefaultComboBoxModel>();
        for (int i = 0; i < size; i++) {
            DefaultComboBoxModel model = new DefaultComboBoxModel();
            model.addElement("Trapezoidal");
            model.addElement("Triangular");
            models.add(model);
        }
        return models;
    }

    static InputVerifier createDoubleInputVerifier() {
        return new InputVerifier() {
            public boolean verify(JComponent input) {
                try {
                    JTextField textField = (JTextField) input;
                    Double.parseDouble(textField.getText());
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            }
        };
    }

    static FuzzySetPanel createEmptyFuzzySetPanel() {
        return new FuzzySetPanel();
    }
}
