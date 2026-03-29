package es.ants.felixgm.trmsim_wsn.scenario;

import es.ants.felixgm.trmsim_wsn.app.BatchSimulationConfig;
import es.ants.felixgm.trmsim_wsn.app.NetworkGenerationConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Loads bundled predefined scenarios from classpath resources.
 */
public final class PredefinedScenarioLoader {
    private static final String INDEX_RESOURCE = "resources/scenarios/index.properties";
    private static final File FILESYSTEM_SCENARIOS_DIR = new File("TRM/src/resources/scenarios");

    private PredefinedScenarioLoader() {
    }

    public static List<ScenarioDefinition> loadBundledScenarios() throws IOException {
        Map<String, ScenarioDefinition> scenariosById = new LinkedHashMap<String, ScenarioDefinition>();
        Properties index = loadProperties(INDEX_RESOURCE);
        String idsValue = index.getProperty("scenario.ids", "").trim();
        if (!idsValue.isEmpty()) {
            for (String id : idsValue.split(",")) {
                String trimmedId = id.trim();
                if (!trimmedId.isEmpty()) {
                    ScenarioDefinition scenario = loadScenario(trimmedId);
                    scenariosById.put(scenario.getId(), scenario);
                }
            }
        }
        for (ScenarioDefinition scenario : loadFilesystemScenarios()) {
            if (!scenariosById.containsKey(scenario.getId())) {
                scenariosById.put(scenario.getId(), scenario);
            }
        }
        if (scenariosById.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<ScenarioDefinition>(scenariosById.values());
    }

    public static ScenarioDefinition loadScenario(String scenarioId) throws IOException {
        Properties properties = loadProperties("resources/scenarios/" + scenarioId + ".properties");
        return buildScenarioDefinition(properties);
    }

    public static ScenarioDefinition buildScenarioDefinition(Properties properties) throws IOException {
        NetworkGenerationConfig networkGenerationConfig = new NetworkGenerationConfig(
                getInt(properties, "network.minNumSensors"),
                getInt(properties, "network.maxNumSensors"),
                getDouble(properties, "network.probClients"),
                getDouble(properties, "network.probRelay"),
                getDouble(properties, "network.probMalicious"),
                getDouble(properties, "network.radioRange"),
                getBoolean(properties, "network.dynamic"),
                getBoolean(properties, "network.oscillating"),
                getBoolean(properties, "network.collusion"));

        BatchSimulationConfig batchSimulationConfig = new BatchSimulationConfig(
                networkGenerationConfig,
                getInt(properties, "simulation.numNetworks"),
                getInt(properties, "simulation.numExecutions"));

        return new ScenarioDefinition(
                required(properties, "id"),
                required(properties, "displayName"),
                required(properties, "description"),
                networkGenerationConfig,
                batchSimulationConfig,
                properties.getProperty("recommendedTrustModel", "").trim());
    }

    public static ScenarioDefinition customScenarioPlaceholder() {
        NetworkGenerationConfig networkGenerationConfig = new NetworkGenerationConfig(0, 0, 0.0, 0.0, 0.0, 0.0, false, false, false);
        BatchSimulationConfig batchSimulationConfig = new BatchSimulationConfig(networkGenerationConfig, 0, 0);
        return new ScenarioDefinition(
                "custom",
                "Custom Configuration",
                "Settings were adjusted manually or a stored WSN was loaded directly. The current workspace is not bound to a bundled predefined scenario.",
                networkGenerationConfig,
                batchSimulationConfig,
                "");
    }

    static File filesystemScenariosDirectory() {
        return FILESYSTEM_SCENARIOS_DIR;
    }

    private static List<ScenarioDefinition> loadFilesystemScenarios() throws IOException {
        if (!FILESYSTEM_SCENARIOS_DIR.isDirectory()) {
            return Collections.emptyList();
        }
        File[] files = FILESYSTEM_SCENARIOS_DIR.listFiles(pathname ->
                pathname.isFile()
                        && pathname.getName().toLowerCase().endsWith(".properties")
                        && !"index.properties".equalsIgnoreCase(pathname.getName()));
        if (files == null || files.length == 0) {
            return Collections.emptyList();
        }
        List<ScenarioDefinition> scenarios = new ArrayList<ScenarioDefinition>();
        for (File file : files) {
            Properties properties = new Properties();
            FileInputStream inputStream = new FileInputStream(file);
            try {
                properties.load(inputStream);
            } finally {
                inputStream.close();
            }
            scenarios.add(buildScenarioDefinition(properties));
        }
        return scenarios;
    }

    private static Properties loadProperties(String resourcePath) throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IOException("Scenario resource not found: " + resourcePath);
        }

        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } finally {
            inputStream.close();
        }
        return properties;
    }

    private static String required(Properties properties, String key) throws IOException {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IOException("Missing scenario property: " + key);
        }
        return value.trim();
    }

    private static int getInt(Properties properties, String key) throws IOException {
        try {
            return Integer.parseInt(required(properties, key));
        } catch (NumberFormatException ex) {
            throw new IOException("Invalid integer for scenario property: " + key, ex);
        }
    }

    private static double getDouble(Properties properties, String key) throws IOException {
        try {
            return Double.parseDouble(required(properties, key));
        } catch (NumberFormatException ex) {
            throw new IOException("Invalid decimal for scenario property: " + key, ex);
        }
    }

    private static boolean getBoolean(Properties properties, String key) throws IOException {
        return Boolean.parseBoolean(required(properties, key));
    }
}
