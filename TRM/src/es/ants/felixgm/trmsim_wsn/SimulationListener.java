package es.ants.felixgm.trmsim_wsn;

import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.outcomes.Outcome;

import java.util.Collection;

/**
 * Typed listener for simulation progress and results.
 */
public interface SimulationListener {
    void onNetworkUpdated(Network network);

    void onOutcomesUpdated(Collection<Outcome> outcomes);

    void onMessage(String message);

    void onError(Exception exception);
}
