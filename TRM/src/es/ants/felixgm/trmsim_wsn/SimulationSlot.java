package es.ants.felixgm.trmsim_wsn;

/**
 * Logical slot for simulation execution/results separation.
 * PRIMARY keeps current single-run behavior; SECONDARY is used by dual mode.
 */
public enum SimulationSlot {
    PRIMARY,
    SECONDARY
}

