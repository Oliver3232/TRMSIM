package es.ants.felixgm.trmsim_wsn.gui.export;

import java.io.File;

public final class ExportRequest {
    private final File targetDirectory;
    private final String reportName;

    public ExportRequest(File targetDirectory, String reportName) {
        this.targetDirectory = targetDirectory;
        this.reportName = reportName;
    }

    public File getTargetDirectory() {
        return targetDirectory;
    }

    public String getReportName() {
        return reportName;
    }

    public String resolveFilePath(String fileName, String extension) {
        return new File(targetDirectory, fileName + extension).getAbsolutePath();
    }
}
