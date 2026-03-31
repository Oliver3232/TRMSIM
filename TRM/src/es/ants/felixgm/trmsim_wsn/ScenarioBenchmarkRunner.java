package es.ants.felixgm.trmsim_wsn;

import es.ants.felixgm.trmsim_wsn.app.BatchSimulationConfig;
import es.ants.felixgm.trmsim_wsn.app.NetworkGenerationConfig;
import es.ants.felixgm.trmsim_wsn.network.Service;
import es.ants.felixgm.trmsim_wsn.outcomes.Outcome;
import es.ants.felixgm.trmsim_wsn.scenario.PredefinedScenarioLoader;
import es.ants.felixgm.trmsim_wsn.scenario.ScenarioDefinition;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Small non-GUI benchmark entry point for predefined scenarios.
 */
public final class ScenarioBenchmarkRunner {
    private static final File REPORT_FILE = new File("docs/scalability-benchmark-report.csv");

    private ScenarioBenchmarkRunner() {
    }

    public static int run(String[] args, String currentVersion) {
        if (args.length < 3) {
            printUsage();
            return 1;
        }

        String scenarioId = args[1];
        String trustModelName = args[2];
        String sizesArg = args.length >= 4 ? args[3] : "";
        int networksOverride = args.length >= 5 ? parsePositiveInt(args[4], "numNetworks") : -1;
        int executionsOverride = args.length >= 6 ? parsePositiveInt(args[5], "numExecutions") : -1;

        try {
            ScenarioDefinition scenario = PredefinedScenarioLoader.loadScenario(scenarioId);
            List<Integer> sizes = parseSizes(sizesArg, scenario.getNetworkGenerationConfig().getMaxNumSensors());
            BatchSimulationConfig batchConfig = scenario.getBatchSimulationConfig();
            int numNetworks = networksOverride > 0 ? networksOverride : batchConfig.getNumNetworks();
            int numExecutions = executionsOverride > 0 ? executionsOverride : batchConfig.getNumExecutions();

            System.out.println("Executing TRMSim-WSN " + currentVersion + " benchmark mode");
            System.out.println("Scenario: " + scenario.getDisplayName() + " [" + scenario.getId() + "]");
            System.out.println("Model: " + trustModelName);
            System.out.println("Batch: numNetworks=" + numNetworks + ", numExecutions=" + numExecutions);
            System.out.println("Sizes: " + sizes);
            System.out.println();

            for (int numSensors : sizes) {
                runSingleBenchmark(scenario, trustModelName, numSensors, numNetworks, numExecutions, currentVersion);
            }
            return 0;
        } catch (Exception ex) {
            System.err.println("Benchmark failed: " + ex.getMessage());
            ex.printStackTrace();
            return 2;
        }
    }

    private static void runSingleBenchmark(
            ScenarioDefinition scenario,
            String trustModelName,
            int numSensors,
            int numNetworks,
            int numExecutions,
            String currentVersion) throws Exception {
        NetworkGenerationConfig baseConfig = scenario.getNetworkGenerationConfig();
        long startedAtNanos = System.nanoTime();
        Outcome outcome = VerboseSimulationRunner.runTrustModel(
                trustModelName,
                new Service("My service"),
                numNetworks,
                numExecutions,
                numSensors,
                numSensors,
                baseConfig.getProbClients(),
                baseConfig.getProbRelay(),
                baseConfig.getProbMalicious(),
                baseConfig.getRadioRange(),
                baseConfig.isDynamic(),
                baseConfig.isOscillating(),
                baseConfig.isCollusion());
        long elapsedMillis = (System.nanoTime() - startedAtNanos) / 1_000_000L;
        String outcomeName = (outcome == null ? "null" : outcome.getClass().getSimpleName());

        System.out.println("size=" + numSensors
                + " durationMs=" + elapsedMillis
                + " outcome=" + outcomeName);
        appendReportLine(scenario, trustModelName, numSensors, numNetworks, numExecutions, elapsedMillis, outcomeName, currentVersion);
    }

    private static List<Integer> parseSizes(String rawSizes, int defaultSize) {
        List<Integer> sizes = new ArrayList<Integer>();
        if (rawSizes == null || rawSizes.trim().isEmpty()) {
            sizes.add(Integer.valueOf(defaultSize));
            return sizes;
        }

        for (String part : rawSizes.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                sizes.add(Integer.valueOf(parsePositiveInt(trimmed, "size")));
            }
        }

        if (sizes.isEmpty()) {
            sizes.add(Integer.valueOf(defaultSize));
        }
        return sizes;
    }

    private static int parsePositiveInt(String value, String label) {
        try {
            int parsed = Integer.parseInt(value.trim());
            if (parsed <= 0) {
                throw new IllegalArgumentException(label + " must be > 0");
            }
            return parsed;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid " + label + ": " + value, ex);
        }
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  --benchmark-scenario <scenarioId> <trustModelName> [sizesCsv] [numNetworks] [numExecutions]");
        System.out.println("Example:");
        System.out.println("  --benchmark-scenario large-scale-fast-peertrust PeerTrust 250,500,1000 1 3");
    }

    private static void appendReportLine(
            ScenarioDefinition scenario,
            String trustModelName,
            int numSensors,
            int numNetworks,
            int numExecutions,
            long durationMs,
            String outcomeName,
            String currentVersion) throws Exception {
        File parent = REPORT_FILE.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Unable to create report directory: " + parent.getAbsolutePath());
        }
        boolean writeHeader = !REPORT_FILE.isFile() || REPORT_FILE.length() == 0L;
        FileWriter writer = new FileWriter(REPORT_FILE, true);
        try {
            if (writeHeader) {
                writer.write("recorded_at,app_version,scenario_id,scenario_name,trust_model,num_sensors,num_networks,num_executions,duration_ms,outcome\n");
            }
            writer.write(csv(new Date().toString()) + ","
                    + csv(currentVersion) + ","
                    + csv(scenario.getId()) + ","
                    + csv(scenario.getDisplayName()) + ","
                    + csv(trustModelName) + ","
                    + numSensors + ","
                    + numNetworks + ","
                    + numExecutions + ","
                    + durationMs + ","
                    + csv(outcomeName) + "\n");
        } finally {
            writer.close();
        }
    }

    private static String csv(String value) {
        String normalized = (value == null) ? "" : value;
        return "\"" + normalized.replace("\"", "\"\"") + "\"";
    }
}
