package es.ants.felixgm.trmsim_wsn.gui.support;

import javax.swing.JFormattedTextField;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.DocumentFilter;
import javax.swing.text.NumberFormatter;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.NumberFormat;

public final class NumericInputBindingHelper {
    private NumericInputBindingHelper() {
    }

    public static void bindSliderAndField(JSlider slider, JTextField field) {
        field.setEditable(true);
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new NumericDocumentFilter(4));
        field.setText(String.valueOf(slider.getValue()));

        slider.addChangeListener(evt -> field.setText(String.valueOf(slider.getValue())));
        field.addActionListener(evt -> applyFieldValue(slider, field));
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                applyFieldValue(slider, field);
            }
        });
    }

    public static void configureIntegerSpinner(JSpinner spinner) {
        if (!(spinner.getModel() instanceof SpinnerNumberModel)) {
            return;
        }
        SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, "#");
        spinner.setEditor(editor);

        NumberFormatter formatter = (NumberFormatter) editor.getTextField().getFormatter();
        formatter.setValueClass(Integer.class);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        formatter.setMinimum(model.getMinimum());
        formatter.setMaximum(model.getMaximum());

        JFormattedTextField textField = editor.getTextField();
        textField.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
        textField.setFormatterFactory(new DefaultFormatterFactory(formatter));
        textField.setColumns(6);
    }

    private static void applyFieldValue(JSlider slider, JTextField field) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            field.setText(String.valueOf(slider.getValue()));
            return;
        }
        try {
            int value = Integer.parseInt(text);
            int normalizedValue = Math.max(slider.getMinimum(), Math.min(slider.getMaximum(), value));
            slider.setValue(normalizedValue);
            field.setText(String.valueOf(normalizedValue));
        } catch (NumberFormatException ex) {
            field.setText(String.valueOf(slider.getValue()));
        }
    }

    private static final class NumericDocumentFilter extends DocumentFilter {
        private final int maxLength;

        private NumericDocumentFilter(int maxLength) {
            this.maxLength = maxLength;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            replace(fb, offset, 0, string, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            String replacement = (text == null) ? "" : text;
            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String candidate = current.substring(0, offset) + replacement + current.substring(offset + length);
            if (candidate.length() > maxLength) {
                return;
            }
            if (!candidate.isEmpty() && !candidate.matches("\\d+")) {
                return;
            }
            fb.replace(offset, length, text, attrs);
        }
    }
}
