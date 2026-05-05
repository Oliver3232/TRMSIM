package es.ants.felixgm.trmsim_wsn.trm.svmtrust;

import es.ants.felixgm.trmsim_wsn.trm.TRMParameters;

/**
 * Parameters for the lightweight online SVM trust model.
 */
public class SVMTrust_Parameters extends TRMParameters {
    public static final String defaultParametersFileName = "trmodels/svmtrust/SVMTrustparameters.txt";

    private double directEvidenceWeight;
    private double witnessEvidenceWeight;
    private double evidenceNormalizationFactor;
    private double pathLengthPenalty;
    private double selectionThreshold;
    private double svmCost;
    private int minTrainingExamples;
    private int retrainInterval;
    private int maxTrainingExamples;
    private int windowSize;

    public SVMTrust_Parameters() {
        super();
        parametersFileHeader = "####################################\n";
        parametersFileHeader += "# SVMTrust parameters file\n";
        parametersFileHeader += "# " + (new java.util.Date()) + "\n";
        parametersFileHeader += "####################################\n";

        set_directEvidenceWeight(1.5);
        set_witnessEvidenceWeight(0.7);
        set_evidenceNormalizationFactor(20.0);
        set_pathLengthPenalty(0.12);
        set_selectionThreshold(0.0);
        set_svmCost(1.0);
        set_minTrainingExamples(8);
        set_retrainInterval(4);
        set_maxTrainingExamples(500);
        set_windowSize(20);
    }

    public SVMTrust_Parameters(String fileName) throws Exception {
        super(fileName);
        parametersFileHeader = "####################################\n";
        parametersFileHeader += "# SVMTrust parameters file\n";
        parametersFileHeader += "# " + (new java.util.Date()) + "\n";
        parametersFileHeader += "####################################\n";

        directEvidenceWeight = getDoubleParameter("directEvidenceWeight");
        witnessEvidenceWeight = getDoubleParameter("witnessEvidenceWeight");
        evidenceNormalizationFactor = getDoubleParameter("evidenceNormalizationFactor");
        pathLengthPenalty = getDoubleParameter("pathLengthPenalty");
        selectionThreshold = getDoubleParameter("selectionThreshold");
        svmCost = getOptionalDoubleParameter("svmCost", 1.0);
        minTrainingExamples = getOptionalIntegerParameter("minTrainingExamples", 8);
        retrainInterval = getOptionalIntegerParameter("retrainInterval", 4);
        maxTrainingExamples = getOptionalIntegerParameter("maxTrainingExamples", 500);
        windowSize = getIntegerParameter("windowSize");
    }

    public double get_directEvidenceWeight() { return directEvidenceWeight; }
    public void set_directEvidenceWeight(double directEvidenceWeight) {
        this.directEvidenceWeight = directEvidenceWeight;
        setDoubleParameter("directEvidenceWeight", directEvidenceWeight);
    }
    public double get_witnessEvidenceWeight() { return witnessEvidenceWeight; }
    public void set_witnessEvidenceWeight(double witnessEvidenceWeight) {
        this.witnessEvidenceWeight = witnessEvidenceWeight;
        setDoubleParameter("witnessEvidenceWeight", witnessEvidenceWeight);
    }
    public double get_evidenceNormalizationFactor() { return evidenceNormalizationFactor; }
    public void set_evidenceNormalizationFactor(double evidenceNormalizationFactor) {
        this.evidenceNormalizationFactor = evidenceNormalizationFactor;
        setDoubleParameter("evidenceNormalizationFactor", evidenceNormalizationFactor);
    }
    public double get_pathLengthPenalty() { return pathLengthPenalty; }
    public void set_pathLengthPenalty(double pathLengthPenalty) {
        this.pathLengthPenalty = pathLengthPenalty;
        setDoubleParameter("pathLengthPenalty", pathLengthPenalty);
    }
    public double get_selectionThreshold() { return selectionThreshold; }
    public void set_selectionThreshold(double selectionThreshold) {
        this.selectionThreshold = selectionThreshold;
        setDoubleParameter("selectionThreshold", selectionThreshold);
    }
    public double get_svmCost() { return svmCost; }
    public void set_svmCost(double svmCost) {
        this.svmCost = svmCost;
        setDoubleParameter("svmCost", svmCost);
    }
    public int get_minTrainingExamples() { return minTrainingExamples; }
    public void set_minTrainingExamples(int minTrainingExamples) {
        this.minTrainingExamples = Math.max(2, minTrainingExamples);
        setIntegerParameter("minTrainingExamples", this.minTrainingExamples);
    }
    public int get_retrainInterval() { return retrainInterval; }
    public void set_retrainInterval(int retrainInterval) {
        this.retrainInterval = Math.max(1, retrainInterval);
        setIntegerParameter("retrainInterval", this.retrainInterval);
    }
    public int get_maxTrainingExamples() { return maxTrainingExamples; }
    public void set_maxTrainingExamples(int maxTrainingExamples) {
        this.maxTrainingExamples = Math.max(2, maxTrainingExamples);
        setIntegerParameter("maxTrainingExamples", this.maxTrainingExamples);
    }
    public int get_windowSize() { return windowSize; }
    public void set_windowSize(int windowSize) {
        this.windowSize = windowSize;
        setIntegerParameter("windowSize", windowSize);
    }

    private double getOptionalDoubleParameter(String parameterName, double defaultValue) {
        String value = parameters.getProperty(parameterName);
        if (value == null) {
            setDoubleParameter(parameterName, defaultValue);
            return defaultValue;
        }
        return Double.parseDouble(value);
    }

    private int getOptionalIntegerParameter(String parameterName, int defaultValue) {
        String value = parameters.getProperty(parameterName);
        if (value == null) {
            setIntegerParameter(parameterName, defaultValue);
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    @Override
    public String toString() {
        String s = parametersFileHeader;
        s += "directEvidenceWeight=" + directEvidenceWeight + "\n";
        s += "witnessEvidenceWeight=" + witnessEvidenceWeight + "\n";
        s += "evidenceNormalizationFactor=" + evidenceNormalizationFactor + "\n";
        s += "pathLengthPenalty=" + pathLengthPenalty + "\n";
        s += "selectionThreshold=" + selectionThreshold + "\n";
        s += "svmCost=" + svmCost + "\n";
        s += "minTrainingExamples=" + minTrainingExamples + "\n";
        s += "retrainInterval=" + retrainInterval + "\n";
        s += "maxTrainingExamples=" + maxTrainingExamples + "\n";
        s += "windowSize=" + windowSize + "\n";
        return s;
    }
}
