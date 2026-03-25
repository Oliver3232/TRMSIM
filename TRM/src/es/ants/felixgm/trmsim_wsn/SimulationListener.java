package es.ants.felixgm.trmsim_wsn;

import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.outcomes.Outcome;

import java.util.Collection;

/**
 * Typed listener for simulation progress and results.
 */
public interface SimulationListener {
    void onNetworkUpdated(Network network);

    default void onNetworkUpdated(SimulationSlot slot, Network network) {
        onNetworkUpdated(network);
    }

    void onOutcomesUpdated(Collection<Outcome> outcomes);

    default void onOutcomesUpdated(SimulationSlot slot, Collection<Outcome> outcomes) {
        onOutcomesUpdated(outcomes);
    }

    void onMessage(String message);

    default void onMessage(SimulationSlot slot, String message) {
        onMessage(message);
    }

    void onError(Exception exception);

    default void onError(SimulationSlot slot, Exception exception) {
        onError(exception);
    }
}
