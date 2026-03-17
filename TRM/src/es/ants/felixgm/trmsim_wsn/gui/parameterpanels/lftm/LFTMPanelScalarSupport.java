package es.ants.felixgm.trmsim_wsn.gui.parameterpanels.lftm;

import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JSlider;
import javax.swing.JTextField;

final class LFTMPanelScalarSupport {
    private LFTMPanelScalarSupport() {
    }

    static void syncSliderText(JSlider slider, JTextField textField) {
        textField.setText(String.valueOf(slider.getValue() / (double) slider.getMaximum()));
    }

    static double readDouble(JTextField textField) {
        return Double.parseDouble(textField.getText());
    }

    static void applySliderValue(JSlider slider, double value) {
        slider.setValue((int) ((slider.getMaximum() - slider.getMinimum()) * value));
    }

    static void applyTextValue(JTextField textField, double value) {
        textField.setText(String.valueOf(value));
    }

    static void updateTrapezoidalVisibility(JComboBox comboBox, JTextField dField) {
        dField.setVisible("Trapezoidal".equals(comboBox.getSelectedItem()));
    }

    static void setEnabled(boolean enabled, JComponent... components) {
        for (JComponent component : components) {
            component.setEnabled(enabled);
        }
    }
}
