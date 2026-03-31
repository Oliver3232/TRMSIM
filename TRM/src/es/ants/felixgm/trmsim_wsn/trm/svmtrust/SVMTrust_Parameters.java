package es.ants.felixgm.trmsim_wsn.trm.svmtrust;

import es.ants.felixgm.trmsim_wsn.trm.TRMParameters;

/**
 * Parameters for the lightweight online SVM trust model.
 */
public class SVMTrust_Parameters extends TRMParameters {
    public static final String defaultParametersFileName = "trmodels/svmtrust/SVMTrustparameters.txt";

    private double learningRate;
    private double regularization;
    private double directEvidenceWeight;
    private double witnessEvidenceWeight;
    private double evidenceNormalizationFactor;
    private double pathLengthPenalty;
    private double selectionThreshold;
    private int windowSize;

    public SVMTrust_Parameters() {
        super();
        parametersFileHeader = "####################################\n";
        parametersFileHeader += "# SVMTrust parameters file\n";
        parametersFileHeader += "# " + (new java.util.Date()) + "\n";
        parametersFileHeader += "####################################\n";

        set_learningRate(0.2);
        set_regularization(0.02);
        set_directEvidenceWeight(1.5);
        set_witnessEvidenceWeight(0.7);
        set_evidenceNormalizationFactor(20.0);
        set_pathLengthPenalty(0.12);
        set_selectionThreshold(0.0);
        set_windowSize(20);
    }

    public SVMTrust_Parameters(String fileName) throws Exception {
        super(fileName);
        parametersFileHeader = "####################################\n";
        parametersFileHeader += "# SVMTrust parameters file\n";
        parametersFileHeader += "# " + (new java.util.Date()) + "\n";
        parametersFileHeader += "####################################\n";

        learningRate = getDoubleParameter("learningRate");
        regularization = getDoubleParameter("regularization");
        directEvidenceWeight = getDoubleParameter("directEvidenceWeight");
        witnessEvidenceWeight = getDoubleParameter("witnessEvidenceWeight");
        evidenceNormalizationFactor = getDoubleParameter("evidenceNormalizationFactor");
        pathLengthPenalty = getDoubleParameter("pathLengthPenalty");
        selectionThreshold = getDoubleParameter("selectionThreshold");
        windowSize = getIntegerParameter("windowSize");
    }

    public double get_learningRate() { return learningRate; }
    public void set_learningRate(double learningRate) {
        this.learningRate = learningRate;
        setDoubleParameter("learningRate", learningRate);
    }
    public double get_regularization() { return regularization; }
    public void set_regularization(double regularization) {
        this.regularization = regularization;
        setDoubleParameter("regularization", regularization);
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
    public int get_windowSize() { return windowSize; }
    public void set_windowSize(int windowSize) {
        this.windowSize = windowSize;
        setIntegerParameter("windowSize", windowSize);
    }

    @Override
    public String toString() {
        String s = parametersFileHeader;
        s += "learningRate=" + learningRate + "\n";
        s += "regularization=" + regularization + "\n";
        s += "directEvidenceWeight=" + directEvidenceWeight + "\n";
        s += "witnessEvidenceWeight=" + witnessEvidenceWeight + "\n";
        s += "evidenceNormalizationFactor=" + evidenceNormalizationFactor + "\n";
        s += "pathLengthPenalty=" + pathLengthPenalty + "\n";
        s += "selectionThreshold=" + selectionThreshold + "\n";
        s += "windowSize=" + windowSize + "\n";
        return s;
    }
}
