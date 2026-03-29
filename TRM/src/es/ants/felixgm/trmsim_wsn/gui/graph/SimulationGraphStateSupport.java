package es.ants.felixgm.trmsim_wsn.gui.graph;

import es.ants.felixgm.trmsim_wsn.gui.networkpanels.JavaFXNetworkPanel;
import es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextArea;

final class SimulationGraphStateSupport {
    private SimulationGraphStateSupport() {
    }

    static void initializeControls(
            JComboBox<String> visualThemeComboBox,
            JCheckBox enable3DNavigationCheckBox,
            JComboBox<String> cameraPresetComboBox,
            JButton fullscreenGraphButton,
            Runnable applyVisualizationControls,
            Runnable toggleFullscreenGraphWindow) {
        visualThemeComboBox.setModel(new javax.swing.DefaultComboBoxModel<String>(
                new String[]{"Futuristic", "Classic", "Wireframe"}));
        visualThemeComboBox.setSelectedItem("Futuristic");
        visualThemeComboBox.addActionListener(e -> applyVisualizationControls.run());

        enable3DNavigationCheckBox.setSelected(false);
        enable3DNavigationCheckBox.setOpaque(false);
        enable3DNavigationCheckBox.addActionListener(e -> applyVisualizationControls.run());

        cameraPresetComboBox.setModel(new javax.swing.DefaultComboBoxModel<String>(
                new String[]{"Isometric", "Top", "Front"}));
        cameraPresetComboBox.setSelectedItem("Isometric");
        cameraPresetComboBox.addActionListener(e -> applyVisualizationControls.run());
        cameraPresetComboBox.setEnabled(enable3DNavigationCheckBox.isSelected());

        fullscreenGraphButton.setText("Open Fullscreen");
        fullscreenGraphButton.addActionListener(e -> toggleFullscreenGraphWindow.run());
    }

    static void applySelectedNodeSummary(
            String title,
            String body,
            JLabel fullscreenInspectorTitleLabel,
            JTextArea fullscreenInspectorTextArea) {
        if (fullscreenInspectorTitleLabel != null) {
            fullscreenInspectorTitleLabel.setText(title);
        }
        if (fullscreenInspectorTextArea != null) {
            fullscreenInspectorTextArea.setText(body);
            fullscreenInspectorTextArea.setCaretPosition(0);
        }
    }

    static void applySimulationControlsState(
            String stateLabel,
            String runLabel,
            String pauseResumeLabel,
            boolean canRun,
            boolean canPauseResume,
            boolean canStop,
            JLabel fullscreenSimulationStateLabel,
            JButton fullscreenPauseResumeButton,
            JButton fullscreenStopButton) {
        if (fullscreenSimulationStateLabel != null) {
            fullscreenSimulationStateLabel.setText(stateLabel);
        }
        if (fullscreenPauseResumeButton != null) {
            fullscreenPauseResumeButton.setText(canRun ? runLabel : pauseResumeLabel);
            fullscreenPauseResumeButton.setEnabled(canRun || canPauseResume);
        }
        if (fullscreenStopButton != null) {
            fullscreenStopButton.setEnabled(canStop);
        }
    }

    static void applyDisplayControlsState(
            boolean showIds,
            boolean showLinks,
            boolean showRanges,
            boolean showGrid,
            int delayValue,
            int delayMin,
            int delayMax,
            JCheckBox fullscreenShowIdsCheckBox,
            JCheckBox fullscreenShowLinksCheckBox,
            JCheckBox fullscreenShowRangesCheckBox,
            JCheckBox fullscreenShowGridCheckBox,
            JSlider fullscreenDelaySlider) {
        if (fullscreenShowIdsCheckBox != null) {
            fullscreenShowIdsCheckBox.setSelected(showIds);
        }
        if (fullscreenShowLinksCheckBox != null) {
            fullscreenShowLinksCheckBox.setSelected(showLinks);
        }
        if (fullscreenShowRangesCheckBox != null) {
            fullscreenShowRangesCheckBox.setSelected(showRanges);
        }
        if (fullscreenShowGridCheckBox != null) {
            fullscreenShowGridCheckBox.setSelected(showGrid);
        }
        if (fullscreenDelaySlider != null) {
            fullscreenDelaySlider.setMinimum(delayMin);
            fullscreenDelaySlider.setMaximum(delayMax);
            fullscreenDelaySlider.setValue(delayValue);
        }
    }

    static void applyFullscreenInteractionLockState(
            boolean fullscreenInteractionLocked,
            boolean currentCanPauseResume,
            boolean currentCanStop,
            JCheckBox fullscreenEnable3DCheckBox,
            JComboBox<String> fullscreenThemeComboBox,
            JComboBox<String> fullscreenPresetComboBox,
            JButton fullscreenPauseResumeButton,
            JButton fullscreenStopButton,
            JCheckBox fullscreenShowIdsCheckBox,
            JCheckBox fullscreenShowLinksCheckBox,
            JCheckBox fullscreenShowRangesCheckBox,
            JCheckBox fullscreenShowGridCheckBox,
            JSlider fullscreenDelaySlider,
            JCheckBox fullscreenPinDrawerCheckBox,
            JButton fullscreenCloseButton) {
        boolean enabled = !fullscreenInteractionLocked;
        if (fullscreenThemeComboBox != null) {
            fullscreenThemeComboBox.setEnabled(enabled);
        }
        if (fullscreenEnable3DCheckBox != null) {
            fullscreenEnable3DCheckBox.setEnabled(enabled);
        }
        if (fullscreenPresetComboBox != null) {
            boolean presetEnabled = enabled
                    && fullscreenEnable3DCheckBox != null
                    && fullscreenEnable3DCheckBox.isSelected();
            fullscreenPresetComboBox.setEnabled(presetEnabled);
        }
        if (fullscreenPauseResumeButton != null) {
            fullscreenPauseResumeButton.setEnabled(enabled && currentCanPauseResume);
        }
        if (fullscreenStopButton != null) {
            fullscreenStopButton.setEnabled(enabled && currentCanStop);
        }
        if (fullscreenShowIdsCheckBox != null) {
            fullscreenShowIdsCheckBox.setEnabled(enabled);
        }
        if (fullscreenShowLinksCheckBox != null) {
            fullscreenShowLinksCheckBox.setEnabled(enabled);
        }
        if (fullscreenShowRangesCheckBox != null) {
            fullscreenShowRangesCheckBox.setEnabled(enabled);
        }
        if (fullscreenShowGridCheckBox != null) {
            fullscreenShowGridCheckBox.setEnabled(enabled);
        }
        if (fullscreenDelaySlider != null) {
            fullscreenDelaySlider.setEnabled(enabled);
        }
        if (fullscreenPinDrawerCheckBox != null) {
            fullscreenPinDrawerCheckBox.setEnabled(enabled);
        }
        if (fullscreenCloseButton != null) {
            fullscreenCloseButton.setEnabled(true);
        }
    }

    static NetworkPanel applyVisualizationControlsToPanels(
            NetworkPanel mainPanel,
            NetworkPanel currentMainNetworkPanel,
            JavaFXNetworkPanel fullscreenNetworkPanel,
            JCheckBox enable3DNavigationCheckBox,
            JComboBox<String> visualThemeComboBox,
            JComboBox<String> cameraPresetComboBox) {
        NetworkPanel resolvedMainPanel = mainPanel != null ? mainPanel : currentMainNetworkPanel;
        if (resolvedMainPanel instanceof JavaFXNetworkPanel) {
            applyVisualizationControls((JavaFXNetworkPanel) resolvedMainPanel, enable3DNavigationCheckBox, visualThemeComboBox, cameraPresetComboBox);
        }
        if (fullscreenNetworkPanel != null) {
            applyVisualizationControls(fullscreenNetworkPanel, enable3DNavigationCheckBox, visualThemeComboBox, cameraPresetComboBox);
        }
        return resolvedMainPanel;
    }

    static void applyVisualizationControls(
            JavaFXNetworkPanel panel,
            JCheckBox enable3DNavigationCheckBox,
            JComboBox<String> visualThemeComboBox,
            JComboBox<String> cameraPresetComboBox) {
        boolean enable3D = enable3DNavigationCheckBox.isSelected();
        cameraPresetComboBox.setEnabled(enable3D);
        panel.setVisualTheme((String) visualThemeComboBox.getSelectedItem());
        panel.set3DNavigationEnabled(enable3D);
        if (enable3D) {
            panel.applyCameraPreset((String) cameraPresetComboBox.getSelectedItem());
        }
    }
}
