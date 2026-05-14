package es.ants.felixgm.trmsim_wsn.gui.export;

import es.ants.felixgm.trmsim_wsn.SimulationSlot;
import es.ants.felixgm.trmsim_wsn.gui.MainWindowContext;
import es.ants.felixgm.trmsim_wsn.gui.TRMSim_WSN;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Immutable snapshot of all parameters active at the moment an export is triggered.
 * Saved automatically as simulation_params.txt in every export directory.
 */
public final class SimulationSnapshot {

    private final String timestamp;
    private final String trustModelName;      // single mode: one name; dual mode: "Primary / Secondary"
    private final String scenarioName;        // null = custom configuration
    private final String scenarioDescription;
    private final int    minSensors;
    private final int    maxSensors;
    private final int    percentClients;
    private final int    percentRelay;
    private final int    percentMalicious;
    private final String radioRange;
    private final int    numNetworks;
    private final int    numExecutions;
    private final boolean dynamic;
    private final boolean oscillating;
    private final boolean collusion;
    private final String trmParameters;       // model-specific .properties text

    private SimulationSnapshot(Builder b) {
        this.timestamp          = b.timestamp;
        this.trustModelName     = b.trustModelName;
        this.scenarioName       = b.scenarioName;
        this.scenarioDescription= b.scenarioDescription;
        this.minSensors         = b.minSensors;
        this.maxSensors         = b.maxSensors;
        this.percentClients     = b.percentClients;
        this.percentRelay       = b.percentRelay;
        this.percentMalicious   = b.percentMalicious;
        this.radioRange         = b.radioRange;
        this.numNetworks        = b.numNetworks;
        this.numExecutions      = b.numExecutions;
        this.dynamic            = b.dynamic;
        this.oscillating        = b.oscillating;
        this.collusion          = b.collusion;
        this.trmParameters      = b.trmParameters;
    }

    // -------------------------------------------------------------------------
    // Factories
    // -------------------------------------------------------------------------

    public static SimulationSnapshot fromContext(MainWindowContext ctx) {
        return buildBase(ctx, ctx.getSelectedTrustModelName());
    }

    public static SimulationSnapshot fromDualContext(TRMSim_WSN window, String primaryModel, String secondaryModel) {
        MainWindowContext ctx = new MainWindowContext(window);
        String combined = label(primaryModel) + " (A)  /  " + label(secondaryModel) + " (B)";
        return buildBase(ctx, combined);
    }

    private static SimulationSnapshot buildBase(MainWindowContext ctx, String modelName) {
        Builder b = new Builder();
        b.timestamp     = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        b.trustModelName = modelName;

        Object stored = ctx.getActiveScenarioLabel().getClientProperty("scenarioName");
        b.scenarioName        = (stored instanceof String) ? (String) stored : null;
        b.scenarioDescription = ctx.getActiveScenarioDescriptionTextArea().getText();

        b.minSensors      = spinnerInt(ctx.getMinNumSensorsSpinner());
        b.maxSensors      = spinnerInt(ctx.getMaxNumSensorsSpinner());
        b.percentClients  = ctx.getPercentageClientsSlider().getValue();
        b.percentRelay    = ctx.getPercentageRelayServersSlider().getValue();
        b.percentMalicious= ctx.getPercentageMaliciousServersSlider().getValue();
        b.radioRange      = ctx.getRadioRangeTextField().getText().trim();
        b.numNetworks     = spinnerInt(ctx.getNumNetworksSpinner());
        b.numExecutions   = spinnerInt(ctx.getNumExecutionsSpinner());
        b.dynamic         = ctx.getDynamicCheckBox().isSelected();
        b.oscillating     = ctx.getOscillatingCheckBox().isSelected();
        b.collusion       = ctx.getCollusionCheckBox().isSelected();

        try {
            b.trmParameters = ctx.getController().get_TRMParameters().toString();
        } catch (Exception e) {
            b.trmParameters = "";
        }

        return new SimulationSnapshot(b);
    }

    // -------------------------------------------------------------------------
    // Save
    // -------------------------------------------------------------------------

    public void saveToDirectory(File dir) {
        File out = new File(dir, "simulation_params.txt");
        try (PrintWriter w = new PrintWriter(new OutputStreamWriter(new FileOutputStream(out), "UTF-8"))) {
            line(w, "=== TRMSim-WSN  Simulation Parameters ===");
            field(w, "Generated",  timestamp);
            w.println();

            line(w, "--- General ---");
            field(w, "Trust Model",  blank(trustModelName) ? "(none)" : trustModelName);
            String sname = blank(scenarioName) ? "Custom Configuration" : scenarioName;
            field(w, "Scenario",    sname);
            if (!blank(scenarioDescription)) {
                field(w, "Description", wrap(scenarioDescription.trim()));
            }

            boolean isDrone = !blank(scenarioName)
                    && scenarioName.toLowerCase().contains("drone");
            if (isDrone) {
                w.println();
                line(w, "--- Drone Profile ---");
                field(w, "Profile", scenarioName);
                if (!blank(scenarioDescription)) {
                    field(w, "Details", wrap(scenarioDescription.trim()));
                }
            }

            w.println();
            line(w, "--- Network Configuration ---");
            field(w, "Sensors",    minSensors == maxSensors
                    ? String.valueOf(minSensors)
                    : minSensors + " – " + maxSensors);
            field(w, "Clients",    percentClients  + "%");
            field(w, "Relay",      percentRelay    + "%");
            field(w, "Malicious",  percentMalicious + "%");
            field(w, "Radio Range", blank(radioRange) ? "?" : radioRange);
            field(w, "Dynamic",    dynamic    ? "yes" : "no");
            field(w, "Oscillating",oscillating ? "yes" : "no");
            field(w, "Collusion",  collusion  ? "yes" : "no");

            w.println();
            line(w, "--- Simulation Configuration ---");
            field(w, "Networks",   String.valueOf(numNetworks));
            field(w, "Executions", String.valueOf(numExecutions));

            if (!blank(trmParameters)) {
                w.println();
                line(w, "--- Trust Model Parameters ---");
                w.println(trmParameters.trim());
            }

            w.println();
            line(w, "==========================================");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static void line(PrintWriter w, String s)              { w.println(s); }
    private static void field(PrintWriter w, String k, String v)   { w.printf("%-14s: %s%n", k, v); }
    private static boolean blank(String s)                         { return s == null || s.trim().isEmpty(); }
    private static String  label(String s)                         { return blank(s) ? "?" : s; }
    private static String  wrap(String s)                          { return s.replace("\n", "\n                 "); }

    private static int spinnerInt(javax.swing.JSpinner s) {
        Object v = s.getValue();
        return v instanceof Number ? ((Number) v).intValue() : 0;
    }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    private static final class Builder {
        String  timestamp, trustModelName, scenarioName, scenarioDescription, radioRange, trmParameters;
        int     minSensors, maxSensors, percentClients, percentRelay, percentMalicious, numNetworks, numExecutions;
        boolean dynamic, oscillating, collusion;
    }
}
