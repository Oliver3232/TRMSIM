package es.ants.felixgm.trmsim_wsn.app.support;

import es.ants.felixgm.trmsim_wsn.trm.TRMParameters;
import es.ants.felixgm.trmsim_wsn.trm.TrustModelRegistry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class ControllerParametersIO {
    private ControllerParametersIO() {
    }

    public static void saveParametersFileContent(String filePath, String newContent) throws Exception {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(newContent);
            fileWriter.flush();
        }
    }

    public static String getParametersFileContent(String configuredPath) throws Exception {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openParametersStream(configuredPath)))) {
            return readContent(bufferedReader);
        }
    }

    public static String getDefaultParametersFileContent(String trustModelName) throws Exception {
        TrustModelRegistry.Descriptor descriptor = TrustModelRegistry.get(trustModelName);

        try {
            String defaultParametersFilePath = descriptor.getDefaultParametersFile();
            InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(defaultParametersFilePath);
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream))) {
                return readContent(bufferedReader);
            }
        } catch (Exception ex) {
            TRMParameters parameters = descriptor.createDefaultParameters();
            return parameters.toString();
        }
    }

    private static InputStream openParametersStream(String configuredPath) throws Exception {
        InputStream resourceStream = ClassLoader.getSystemClassLoader().getResourceAsStream(configuredPath);
        if (resourceStream != null) {
            return resourceStream;
        }

        File directFile = new File(configuredPath);
        if (directFile.isFile()) {
            return new FileInputStream(directFile);
        }

        File trmRelativeFile = new File("TRM", configuredPath);
        if (trmRelativeFile.isFile()) {
            return new FileInputStream(trmRelativeFile);
        }

        throw new java.io.FileNotFoundException(configuredPath);
    }

    private static String readContent(BufferedReader bufferedReader) throws Exception {
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            content.append(line).append('\n');
        }
        return content.toString();
    }
}
