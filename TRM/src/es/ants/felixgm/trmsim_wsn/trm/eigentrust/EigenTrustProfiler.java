package es.ants.felixgm.trmsim_wsn.trm.eigentrust;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

/**
 * Lightweight opt-in profiler for EigenTrust benchmark runs.
 */
public final class EigenTrustProfiler {
    private static boolean enabled = false;
    private static ProfileSnapshot current = null;

    private EigenTrustProfiler() {
    }

    public static synchronized void beginRun(String scenarioId, int numSensors, int numNetworks, int numExecutions) {
        enabled = true;
        current = new ProfileSnapshot(scenarioId, numSensors, numNetworks, numExecutions);
    }

    public static synchronized ProfileSnapshot endRun() {
        ProfileSnapshot snapshot = current;
        enabled = false;
        current = null;
        return snapshot;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static synchronized void recordGather(long durationNanos) {
        if (current != null) {
            current.gatherCalls++;
            current.gatherTimeNanos += durationNanos;
        }
    }

    public static synchronized void recordMatrixFill(long durationNanos) {
        if (current != null) {
            current.matrixFillCalls++;
            current.matrixFillTimeNanos += durationNanos;
        }
    }

    public static synchronized void recordScore(long durationNanos, int iterations) {
        if (current != null) {
            current.scoreCalls++;
            current.scoreTimeNanos += durationNanos;
            current.powerIterations += iterations;
        }
    }

    public static synchronized void recordProviderSelection(long durationNanos, int pathChecks) {
        if (current != null) {
            current.providerSelectionCalls++;
            current.providerSelectionTimeNanos += durationNanos;
            current.providerPathChecks += pathChecks;
        }
    }

    public static synchronized void recordTransaction(long durationNanos) {
        if (current != null) {
            current.transactionCalls++;
            current.transactionTimeNanos += durationNanos;
        }
    }

    public static synchronized void appendCsv(ProfileSnapshot snapshot, File outputFile) throws Exception {
        if (snapshot == null) {
            return;
        }
        File parent = outputFile.getParentFile();
        if ((parent != null) && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Unable to create profile output directory: " + parent.getAbsolutePath());
        }
        boolean writeHeader = !outputFile.isFile() || (outputFile.length() == 0L);
        FileWriter writer = new FileWriter(outputFile, true);
        try {
            if (writeHeader) {
                writer.write("recorded_at,scenario_id,num_sensors,num_networks,num_executions,gather_calls,gather_ms,matrix_fill_calls,matrix_fill_ms,score_calls,score_ms,power_iterations,provider_selection_calls,provider_selection_ms,provider_path_checks,transaction_calls,transaction_ms\n");
            }
            writer.write(csv(new Date().toString()) + ","
                    + csv(snapshot.scenarioId) + ","
                    + snapshot.numSensors + ","
                    + snapshot.numNetworks + ","
                    + snapshot.numExecutions + ","
                    + snapshot.gatherCalls + ","
                    + nanosToMillis(snapshot.gatherTimeNanos) + ","
                    + snapshot.matrixFillCalls + ","
                    + nanosToMillis(snapshot.matrixFillTimeNanos) + ","
                    + snapshot.scoreCalls + ","
                    + nanosToMillis(snapshot.scoreTimeNanos) + ","
                    + snapshot.powerIterations + ","
                    + snapshot.providerSelectionCalls + ","
                    + nanosToMillis(snapshot.providerSelectionTimeNanos) + ","
                    + snapshot.providerPathChecks + ","
                    + snapshot.transactionCalls + ","
                    + nanosToMillis(snapshot.transactionTimeNanos) + "\n");
        } finally {
            writer.close();
        }
    }

    private static long nanosToMillis(long nanos) {
        return nanos / 1_000_000L;
    }

    private static String csv(String value) {
        String normalized = (value == null) ? "" : value;
        return "\"" + normalized.replace("\"", "\"\"") + "\"";
    }

    public static final class ProfileSnapshot {
        private final String scenarioId;
        private final int numSensors;
        private final int numNetworks;
        private final int numExecutions;
        private long gatherCalls;
        private long gatherTimeNanos;
        private long matrixFillCalls;
        private long matrixFillTimeNanos;
        private long scoreCalls;
        private long scoreTimeNanos;
        private long powerIterations;
        private long providerSelectionCalls;
        private long providerSelectionTimeNanos;
        private long providerPathChecks;
        private long transactionCalls;
        private long transactionTimeNanos;

        private ProfileSnapshot(String scenarioId, int numSensors, int numNetworks, int numExecutions) {
            this.scenarioId = scenarioId;
            this.numSensors = numSensors;
            this.numNetworks = numNetworks;
            this.numExecutions = numExecutions;
        }
    }
}
