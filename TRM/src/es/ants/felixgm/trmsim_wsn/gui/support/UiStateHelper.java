package es.ants.felixgm.trmsim_wsn.gui.support;


import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import java.awt.Component;

public final class UiStateHelper {
    private UiStateHelper() {
    }

    public static void syncSliderValue(JSlider slider, JTextField textField) {
        textField.setText(String.valueOf(slider.getValue()));
    }

    public static void alignMinMaxSpinners(javax.swing.JSpinner minSpinner, javax.swing.JSpinner maxSpinner, boolean minChanged) {
        int minValue = (Integer) minSpinner.getValue();
        int maxValue = (Integer) maxSpinner.getValue();
        if (minValue > maxValue) {
            if (minChanged) {
                maxSpinner.setValue(minValue);
            } else {
                minSpinner.setValue(maxValue);
            }
        }
    }

    public static void setComponentsEnabled(boolean enabled, Component... components) {
        for (Component component : components) {
            if (component != null) {
                component.setEnabled(enabled);
            }
        }
    }

    public static void setClientsProbabilityControlsEnabled(boolean enabled, Component label, Component slider, Component textField) {
        label.setEnabled(enabled);
        slider.setEnabled(enabled);
        textField.setEnabled(enabled);
    }

    public static void updateParametersSourceView(
            boolean fileSource,
            JComponent parametersFileLabel,
            JComponent parametersFileTextField,
            JComponent browseButton,
            JComponent saveParametersFileContentButton,
            JComponent trmParametersScrollPane,
            JComponent parametersFileContentScrollPane,
            JSplitPane bottomParametersSplitPane) {
        parametersFileLabel.setVisible(fileSource);
        parametersFileTextField.setVisible(fileSource);
        browseButton.setVisible(fileSource);
        saveParametersFileContentButton.setVisible(fileSource);
        trmParametersScrollPane.setVisible(!fileSource);
        parametersFileContentScrollPane.setVisible(fileSource);
        bottomParametersSplitPane.setDividerLocation(fileSource ? 0.0 : 1.0);
    }
}
