package es.ants.felixgm.trmsim_wsn.gui.support;


import es.ants.felixgm.trmsim_wsn.gui.networkpanels.NetworkPanel;
import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Service;

public final class NetworkRenderSupport {
    public static final int LARGE_SCALE_NETWORK_THRESHOLD = 750;

    public static final class RenderState {
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

        public double getRadioRange() {
            return radioRange;
        }

        public boolean isShowRanges() {
            return showRanges;
        }

        public boolean isShowLinks() {
            return showLinks;
        }

        public boolean isShowIds() {
            return showIds;
        }

        public boolean isShowGrid() {
            return showGrid;
        }
    }

    private NetworkRenderSupport() {
    }

    public static RenderState createState(double radioRange, boolean showRanges, boolean showLinks, boolean showIds, boolean showGrid) {
        return new RenderState(radioRange, showRanges, showLinks, showIds, showGrid);
    }

    public static boolean isLargeScaleNetwork(Network network) {
        return network != null
                && network.get_sensors() != null
                && network.get_sensors().size() >= LARGE_SCALE_NETWORK_THRESHOLD;
    }

    public static RenderState effectiveState(Network network, RenderState requestedState) {
        if (!isLargeScaleNetwork(network)) {
            return requestedState;
        }
        return new RenderState(
                requestedState.getRadioRange(),
                false,
                false,
                false,
                false);
    }

    public static boolean isReducedForLargeScale(Network network, RenderState requestedState, RenderState effectiveState) {
        return isLargeScaleNetwork(network)
                && ((requestedState.isShowRanges() != effectiveState.isShowRanges())
                || (requestedState.isShowLinks() != effectiveState.isShowLinks())
                || (requestedState.isShowIds() != effectiveState.isShowIds())
                || (requestedState.isShowGrid() != effectiveState.isShowGrid()));
    }

    public static void renderNetwork(NetworkPanel targetPanel, Network network, Service requiredService, RenderState renderState) throws Exception {
        RenderState effectiveState = effectiveState(network, renderState);
        targetPanel.paintNetwork(
                network,
                requiredService,
                effectiveState.getRadioRange(),
                effectiveState.isShowRanges(),
                effectiveState.isShowLinks(),
                effectiveState.isShowIds(),
                effectiveState.isShowGrid());
    }
}
