package es.ants.felixgm.trmsim_wsn.gui;

import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Sensor;
import es.ants.felixgm.trmsim_wsn.network.Service;
import es.ants.felixgm.trmsim_wsn.outcomes.NodeMetric;
import es.ants.felixgm.trmsim_wsn.outcomes.Outcome;

import java.util.List;
import java.util.Locale;
import java.util.Vector;

final class NodeInspectorHelper {
    private NodeInspectorHelper() {
    }

    static Vector<String> buildNeighborIds(Sensor sensor) {
        Vector<String> neighborIds = new Vector<String>();
        for (Sensor neighbor : sensor.getNeighbors()) {
            neighborIds.add(String.valueOf(neighbor.id()));
        }
        return neighborIds;
    }

    static String buildNodeTitle(Sensor sensor) {
        return "Node " + sensor.id() + (sensor.isActive() ? "  ACTIVE" : "  SLEEP");
    }

    static String buildNodeDetailsText(Sensor sensor, Network currentNetwork) {
        StringBuilder details = new StringBuilder();
        appendNodeSummary(details, sensor);
        appendNeighborSummary(details, sensor);
        appendServiceSummary(details, sensor, currentNetwork);
        appendLatestMetrics(details, sensor.id());
        return details.toString();
    }

    private static void appendNodeSummary(StringBuilder details, Sensor sensor) {
        details.append("Node ID: ").append(sensor.id()).append('\n');
        details.append("Status: ").append(sensor.isActive() ? "Active" : "Sleeping").append('\n');
        details.append("Position: (")
                .append(String.format(Locale.US, "%.2f", sensor.getX()))
                .append(", ")
                .append(String.format(Locale.US, "%.2f", sensor.getY()))
                .append(")\n");
        details.append("Neighbors: ").append(sensor.getNeighbors().size()).append('\n');
        details.append("Provided services: ").append(sensor.get_numServices()).append('\n');
        details.append("Raw transmitted distance: ").append(sensor.get_transmittedDistance()).append('\n');
    }

    private static void appendNeighborSummary(StringBuilder details, Sensor sensor) {
        details.append('\n').append("Neighbor IDs: ");
        boolean first = true;
        for (Sensor neighbor : sensor.getNeighbors()) {
            if (!first) {
                details.append(", ");
            }
            details.append(neighbor.id());
            first = false;
        }
        if (first) {
            details.append("none");
        }
    }

    private static void appendServiceSummary(StringBuilder details, Sensor sensor, Network currentNetwork) {
        details.append('\n').append('\n').append("Services:\n");
        if (sensor.get_numServices() == 0) {
            details.append("- client-only node\n");
            return;
        }
        boolean hasPrintedService = false;
        if (currentNetwork != null) {
            for (Service service : currentNetwork.get_services()) {
                if (sensor.offersService(service)) {
                    hasPrintedService = true;
                    details.append("- ").append(service.id());
                    try {
                        details.append(" | goodness=")
                                .append(String.format(Locale.US, "%.4f", sensor.get_goodness(service)));
                    } catch (Exception ignored) {
                    }
                    details.append('\n');
                }
            }
        }
        if (!hasPrintedService) {
            details.append("- service data unavailable\n");
        }
    }

    private static void appendLatestMetrics(StringBuilder details, int sensorId) {
        NodeMetric latestMetric = findLatestNodeMetric(sensorId);
        details.append('\n').append("Latest exported metrics:\n");
        if (latestMetric == null) {
            details.append("- no node-level metrics available yet\n");
            return;
        }
        details.append("- Type: ").append(latestMetric.getType()).append('\n');
        details.append("- Energy / execution: ")
                .append(String.format(Locale.US, "%.6f", latestMetric.getConsumedEnergy())).append('\n');
        details.append("- Total transmitted distance: ")
                .append(String.format(Locale.US, "%.2f", latestMetric.getTransmittedDistance())).append('\n');
        details.append("- Exported coordinates: (")
                .append(String.format(Locale.US, "%.2f", latestMetric.getX())).append(", ")
                .append(String.format(Locale.US, "%.2f", latestMetric.getY())).append(")\n");
        details.append("- Exported neighbors: ").append(latestMetric.getNeighborsCount()).append('\n');
        details.append("- Goodness: ");
        if (latestMetric.getGoodness() < 0.0) {
            details.append("n/a\n");
        } else {
            details.append(String.format(Locale.US, "%.4f", latestMetric.getGoodness())).append('\n');
        }
    }

    private static NodeMetric findLatestNodeMetric(int sensorId) {
        List<Outcome> results = SimulationResultRepository.getInstance().getResults();
        for (int i = results.size() - 1; i >= 0; i--) {
            Outcome outcome = results.get(i);
            List<NodeMetric> metrics = outcome.getNodeMetrics();
            if (metrics == null) {
                continue;
            }
            for (NodeMetric metric : metrics) {
                if (metric.getId() == sensorId) {
                    return metric;
                }
            }
        }
        return null;
    }
}
