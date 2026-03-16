package es.ants.felixgm.trmsim_wsn.gui;

import es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel;
import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Service;

final class NetworkRenderSupport {
    static final class RenderState {
        private final double radioRange;
        private final boolean showRanges;
        private final boolean showLinks;
        private final boolean showIds;
        private final boolean showGrid;

        RenderState(double radioRange, boolean showRanges, boolean showLinks, boolean showIds, boolean showGrid) {
            this.radioRange = radioRange;
            this.showRanges = showRanges;
            this.showLinks = showLinks;
            this.showIds = showIds;
            this.showGrid = showGrid;
        }

        double getRadioRange() {
            return radioRange;
        }

        boolean isShowRanges() {
            return showRanges;
        }

        boolean isShowLinks() {
            return showLinks;
        }

        boolean isShowIds() {
            return showIds;
        }

        boolean isShowGrid() {
            return showGrid;
        }
    }

    private NetworkRenderSupport() {
    }

    static RenderState createState(double radioRange, boolean showRanges, boolean showLinks, boolean showIds, boolean showGrid) {
        return new RenderState(radioRange, showRanges, showLinks, showIds, showGrid);
    }

    static void renderNetwork(NetworkPanel targetPanel, Network network, Service requiredService, RenderState renderState) throws Exception {
        targetPanel.paintNetwork(
                network,
                requiredService,
                renderState.getRadioRange(),
                renderState.isShowRanges(),
                renderState.isShowLinks(),
                renderState.isShowIds(),
                renderState.isShowGrid());
    }
}
