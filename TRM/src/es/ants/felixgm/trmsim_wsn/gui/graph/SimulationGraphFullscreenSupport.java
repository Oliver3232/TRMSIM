package es.ants.felixgm.trmsim_wsn.gui.graph;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import java.awt.Color;
import java.util.function.Consumer;

final class
SimulationGraphFullscreenSupport {
    private SimulationGraphFullscreenSupport() {
    }

    static JPopupMenu buildToolbarPopup(
            JComboBox<String> visualThemeComboBox,
            JComboBox<String> cameraPresetComboBox,
            JCheckBox enable3DNavigationCheckBox,
            Runnable applyVisualizationControls,
            Runnable closeFullscreenWindow) {
        JPopupMenu popup = new JPopupMenu("Graph Controls");

        JMenu themeMenu = new JMenu("Theme");
        ButtonGroup themeGroup = new ButtonGroup();
        addThemeItem(themeMenu, themeGroup, "Futuristic", visualThemeComboBox, applyVisualizationControls);
        addThemeItem(themeMenu, themeGroup, "Classic", visualThemeComboBox, applyVisualizationControls);
        addThemeItem(themeMenu, themeGroup, "Wireframe", visualThemeComboBox, applyVisualizationControls);
        popup.add(themeMenu);

        JMenu presetMenu = new JMenu("3D View");
        presetMenu.setEnabled(enable3DNavigationCheckBox.isSelected());
        ButtonGroup presetGroup = new ButtonGroup();
        addPresetItem(presetMenu, presetGroup, "Isometric", cameraPresetComboBox, enable3DNavigationCheckBox, applyVisualizationControls);
        addPresetItem(presetMenu, presetGroup, "Top", cameraPresetComboBox, enable3DNavigationCheckBox, applyVisualizationControls);
        addPresetItem(presetMenu, presetGroup, "Front", cameraPresetComboBox, enable3DNavigationCheckBox, applyVisualizationControls);
        popup.add(presetMenu);

        JCheckBoxMenuItem enable3DItem = new JCheckBoxMenuItem("Enable 3D navigation");
        enable3DItem.setSelected(enable3DNavigationCheckBox.isSelected());
        enable3DItem.addActionListener(e -> {
            enable3DNavigationCheckBox.setSelected(enable3DItem.isSelected());
            presetMenu.setEnabled(enable3DItem.isSelected());
            applyVisualizationControls.run();
        });
        popup.add(enable3DItem);
        popup.addSeparator();

        JMenuItem closeItem = new JMenuItem("Close Fullscreen");
        closeItem.addActionListener(e -> closeFullscreenWindow.run());
        popup.add(closeItem);

        return popup;
    }

    static JCheckBox createFullscreenToggle(String label, boolean selected, Consumer<Boolean> consumer) {
        JCheckBox checkBox = new JCheckBox(label);
        checkBox.setOpaque(false);
        checkBox.setForeground(new Color(220, 245, 255));
        checkBox.setAlignmentX(0.0f);
        checkBox.setSelected(selected);
        checkBox.addActionListener(e -> consumer.accept(checkBox.isSelected()));
        return checkBox;
    }

    private static void addThemeItem(
            JMenu menu,
            ButtonGroup group,
            String themeName,
            JComboBox<String> visualThemeComboBox,
            Runnable applyVisualizationControls) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(themeName);
        item.setSelected(themeName.equals(visualThemeComboBox.getSelectedItem()));
        item.addActionListener(e -> {
            visualThemeComboBox.setSelectedItem(themeName);
            applyVisualizationControls.run();
        });
        group.add(item);
        menu.add(item);
    }

    private static void addPresetItem(
            JMenu menu,
            ButtonGroup group,
            String presetName,
            JComboBox<String> cameraPresetComboBox,
            JCheckBox enable3DNavigationCheckBox,
            Runnable applyVisualizationControls) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(presetName);
        item.setSelected(presetName.equals(cameraPresetComboBox.getSelectedItem()));
        item.setEnabled(enable3DNavigationCheckBox.isSelected());
        item.addActionListener(e -> {
            cameraPresetComboBox.setSelectedItem(presetName);
            applyVisualizationControls.run();
        });
        group.add(item);
        menu.add(item);
    }
}
