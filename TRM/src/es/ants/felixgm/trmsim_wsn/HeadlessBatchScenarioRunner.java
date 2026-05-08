package es.ants.felixgm.trmsim_wsn;

import es.ants.felixgm.trmsim_wsn.app.BatchSimulationConfig;
import es.ants.felixgm.trmsim_wsn.app.NetworkGenerationConfig;
import es.ants.felixgm.trmsim_wsn.network.Service;
import es.ants.felixgm.trmsim_wsn.outcomes.Outcome;
import es.ants.felixgm.trmsim_wsn.scenario.PredefinedScenarioLoader;
import es.ants.felixgm.trmsim_wsn.scenario.ScenarioDefinition;
import es.ants.felixgm.trmsim_wsn.trm.eigentrust.EigenTrust;
import es.ants.felixgm.trmsim_wsn.trm.eigentrust.EigenTrustProfiler;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Fully headless batch benchmark/export runner for thesis experiments.
 */
public final class HeadlessBatchScenarioRunner {
    private HeadlessBatchScenarioRunner() {
    }

    public static int run(String[] args, String currentVersion) {
        if (args.length < 3) {
            printUsage();
            return 1;
        }

        String scenarioId = args[1];
        String modelsCsv = args[2];
        String sizesArg = args.length >= 4 ? args[3] : "";
        int networksOverride = args.length >= 5 ? parsePositiveInt(args[4], "numNetworks") : -1;
        int executionsOverride = args.length >= 6 ? parsePositiveInt(args[5], "numExecutions") : -1;
        String outputDirArg = args.length >= 7 ? args[6] : "docs/headless-batch";

        try {
            ScenarioDefinition scenario = PredefinedScenarioLoader.loadScenario(scenarioId);
            List<String> trustModels = parseModels(modelsCsv);
            List<Integer> sizes = parseSizes(sizesArg, scenario.getNetworkGenerationConfig().getMaxNumSensors());
            BatchSimulationConfig batchConfig = scenario.getBatchSimulationConfig();
            int numNetworks = networksOverride > 0 ? networksOverride : batchConfig.getNumNetworks();
            int numExecutions = executionsOverride > 0 ? executionsOverride : batchConfig.getNumExecutions();
            File outputDir = new File(outputDirArg);

            if (!outputDir.exists() && !outputDir.mkdirs()) {
                throw new IllegalStateException("Unable to create output directory: " + outputDir.getAbsolutePath());
            }

            String profileLabel = numNetworks + "x" + numExecutions;
            String modelLabel = toModelLabel(trustModels);
            File rawCsv = new File(outputDir, scenarioId + "-" + profileLabel + "-" + modelLabel + "-raw.csv");
            File graphTsv = new File(outputDir, scenarioId + "-" + profileLabel + "-" + modelLabel + "-graph.tsv");
            File summaryMd = new File(outputDir, scenarioId + "-" + profileLabel + "-" + modelLabel + "-summary.md");
            File eigenProfileCsv = new File(outputDir, scenarioId + "-" + profileLabel + "-" + modelLabel + "-eigentrust-profile.csv");

            resetFile(rawCsv);
            resetFile(graphTsv);
            resetFile(summaryMd);
            resetFile(eigenProfileCsv);
            append(rawCsv, "recorded_at,app_version,scenario_id,scenario_name,trust_model,num_sensors,num_networks,num_executions,duration_ms,outcome\n");
            append(graphTsv, "model\tnodes\tduration_ms\tprofile\tscenario_id\n");

            List<BatchRecord> records = new ArrayList<BatchRecord>();

            System.out.println("Executing TRMSim-WSN " + currentVersion + " headless batch mode");
            System.out.println("Scenario: " + scenario.getDisplayName() + " [" + scenario.getId() + "]");
            System.out.println("Models: " + trustModels);
            System.out.println("Batch: numNetworks=" + numNetworks + ", numExecutions=" + numExecutions);
            System.out.println("Sizes: " + sizes);
            System.out.println("Output: " + outputDir.getAbsolutePath());
            System.out.println();

            for (String trustModelName : trustModels) {
                for (Integer numSensors : sizes) {
                    BatchRecord record = runSingleBatch(
                            scenario,
                            trustModelName,
                            numSensors.intValue(),
                            numNetworks,
                            numExecutions,
                            currentVersion,
                            eigenProfileCsv);
                    records.add(record);
                    append(rawCsv, record.toCsv());
                    append(graphTsv, record.toTsv(profileLabel));
                }
            }

            writeSummary(summaryMd, scenario, trustModels, sizes, numNetworks, numExecutions, records);
            return 0;
        } catch (Exception ex) {
            System.err.println("Headless batch failed: " + ex.getMessage());
            ex.printStackTrace();
            return 2;
        }
    }

    private static BatchRecord runSingleBatch(
            ScenarioDefinition scenario,
            String trustModelName,
            int numSensors,
            int numNetworks,
            int numExecutions,
            String currentVersion,
            File eigenProfileCsv) throws Exception {
        NetworkGenerationConfig baseConfig = scenario.getNetworkGenerationConfig();
        boolean profileEigenTrust = EigenTrust.get_name().equals(trustModelName);
        if (profileEigenTrust) {
            EigenTrustProfiler.beginRun(scenario.getId(), numSensors, numNetworks, numExecutions);
        }
        long startedAtNanos = System.nanoTime();
        try {
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

            System.out.println("model=" + trustModelName
                    + " size=" + numSensors
                    + " durationMs=" + elapsedMillis
                    + " outcome=" + outcomeName);
            return new BatchRecord(
                    new Date().toString(),
                    currentVersion,
                    scenario.getId(),
                    scenario.getDisplayName(),
                    trustModelName,
                    numSensors,
                    numNetworks,
                    numExecutions,
                    elapsedMillis,
                    outcomeName);
        } finally {
            if (profileEigenTrust) {
                EigenTrustProfiler.appendCsv(EigenTrustProfiler.endRun(), eigenProfileCsv);
            }
        }
    }

    private static void writeSummary(
            File outputFile,
            ScenarioDefinition scenario,
            List<String> trustModels,
            List<Integer> sizes,
            int numNetworks,
            int numExecutions,
            List<BatchRecord> records) throws Exception {
        FileWriter writer = new FileWriter(outputFile, true);
        try {
            writer.write("# Headless Batch Summary\n\n");
            writer.write("Scenario: `" + scenario.getId() + "`\n\n");
            writer.write("Profile: `" + numNetworks + "x" + numExecutions + "`\n\n");
            writer.write("Models: `" + trustModels + "`\n\n");
            writer.write("| Model | Nodes | Duration |\n");
            writer.write("| --- | ---: | ---: |\n");
            for (String trustModelName : trustModels) {
                for (Integer size : sizes) {
                    BatchRecord record = findRecord(records, trustModelName, size.intValue());
                    if (record != null) {
                        writer.write("| " + trustModelName + " | `" + size + "` | `" + record.durationMs + " ms` |\n");
                    }
                }
            }
        } finally {
            writer.close();
        }
    }

    private static BatchRecord findRecord(List<BatchRecord> records, String trustModelName, int size) {
        for (BatchRecord record : records) {
            if (record.trustModelName.equals(trustModelName) && (record.numSensors == size)) {
                return record;
            }
        }
        return null;
    }

    private static void append(File outputFile, String text) throws Exception {
        FileWriter writer = new FileWriter(outputFile, true);
        try {
            writer.write(text);
        } finally {
            writer.close();
        }
    }

    private static void resetFile(File outputFile) throws Exception {
        File parent = outputFile.getParentFile();
        if ((parent != null) && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Unable to create output directory: " + parent.getAbsolutePath());
        }
        FileWriter writer = new FileWriter(outputFile, false);
        try {
            writer.write("");
        } finally {
            writer.close();
        }
    }

    private static List<String> parseModels(String rawModels) {
        List<String> models = new ArrayList<String>();
        for (String part : rawModels.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                models.add(trimmed);
            }
        }
        if (models.isEmpty()) {
            throw new IllegalArgumentException("At least one trust model must be provided");
        }
        return models;
    }

    private static String toModelLabel(List<String> trustModels) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < trustModels.size(); i++) {
            if (i > 0) {
                builder.append("-");
            }
            String model = trustModels.get(i);
            for (int j = 0; j < model.length(); j++) {
                char ch = model.charAt(j);
                if (Character.isLetterOrDigit(ch)) {
                    builder.append(Character.toLowerCase(ch));
                } else {
                    builder.append('_');
                }
            }
        }
        return builder.toString();
    }

    private static List<Integer> parseSizes(String rawSizes, int defaultSize) {
        List<Integer> sizes = new ArrayList<Integer>();
        if ((rawSizes == null) || rawSizes.trim().isEmpty()) {
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
        System.out.println("  --headless-batch-scenario <scenarioId> <modelsCsv> [sizesCsv] [numNetworks] [numExecutions] [outputDir]");
        System.out.println("Example:");
        System.out.println("  --headless-batch-scenario large-scale-fast-peertrust PeerTrust,BTRM_WSN,EigenTrust 250,500 1 3 docs/headless-batch");
    }

    private static String csv(String value) {
        String normalized = (value == null) ? "" : value;
        return "\"" + normalized.replace("\"", "\"\"") + "\"";
    }

    private static final class BatchRecord {
        private final String recordedAt;
        private final String currentVersion;
        private final String scenarioId;
        private final String scenarioName;
        private final String trustModelName;
        private final int numSensors;
        private final int numNetworks;
        private final int numExecutions;
        private final long durationMs;
        private final String outcomeName;

        private BatchRecord(
                String recordedAt,
                String currentVersion,
                String scenarioId,
                String scenarioName,
                String trustModelName,
                int numSensors,
                int numNetworks,
                int numExecutions,
                long durationMs,
                String outcomeName) {
            this.recordedAt = recordedAt;
            this.currentVersion = currentVersion;
            this.scenarioId = scenarioId;
            this.scenarioName = scenarioName;
            this.trustModelName = trustModelName;
            this.numSensors = numSensors;
            this.numNetworks = numNetworks;
            this.numExecutions = numExecutions;
            this.durationMs = durationMs;
            this.outcomeName = outcomeName;
        }

        private String toCsv() {
            return csv(recordedAt) + ","
                    + csv(currentVersion) + ","
                    + csv(scenarioId) + ","
                    + csv(scenarioName) + ","
                    + csv(trustModelName) + ","
                    + numSensors + ","
                    + numNetworks + ","
                    + numExecutions + ","
                    + durationMs + ","
                    + csv(outcomeName) + "\n";
        }

        private String toTsv(String profileLabel) {
            return trustModelName + "\t"
                    + numSensors + "\t"
                    + durationMs + "\t"
                    + profileLabel + "\t"
                    + scenarioId + "\n";
        }
    }
}
