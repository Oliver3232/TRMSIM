package es.ants.felixgm.trmsim_wsn.trm.bayestrust;

import es.ants.felixgm.trmsim_wsn.trm.TRMParameters;

/**
 * Parameters for the Bayesian trust model.
 */
public class BayesTrust_Parameters extends TRMParameters {
    public static final String defaultParametersFileName = "trmodels/bayestrust/BayesTrustparameters.txt";

    private double priorAlpha;
    private double priorBeta;
    private double directEvidenceWeight;
    private double witnessEvidenceWeight;
    private double pathLengthPenalty;
    private double uncertaintyPenalty;
    private double selectionThreshold;
    private int windowSize;

    public BayesTrust_Parameters() {
        super();
        parametersFileHeader = "####################################\n";
        parametersFileHeader += "# BayesTrust parameters file\n";
        parametersFileHeader += "# " + (new java.util.Date()) + "\n";
        parametersFileHeader += "####################################\n";

        set_priorAlpha(2.0);
        set_priorBeta(1.0);
        set_directEvidenceWeight(1.5);
        set_witnessEvidenceWeight(0.75);
        set_pathLengthPenalty(0.12);
        set_uncertaintyPenalty(1.0);
        set_selectionThreshold(0.35);
        set_windowSize(20);
    }

    public BayesTrust_Parameters(String fileName) throws Exception {
        super(fileName);
        parametersFileHeader = "####################################\n";
        parametersFileHeader += "# BayesTrust parameters file\n";
        parametersFileHeader += "# " + (new java.util.Date()) + "\n";
        parametersFileHeader += "####################################\n";

        priorAlpha = getDoubleParameter("priorAlpha");
        priorBeta = getDoubleParameter("priorBeta");
        directEvidenceWeight = getDoubleParameter("directEvidenceWeight");
        witnessEvidenceWeight = getDoubleParameter("witnessEvidenceWeight");
        pathLengthPenalty = getDoubleParameter("pathLengthPenalty");
        uncertaintyPenalty = getOptionalDoubleParameter("uncertaintyPenalty", 1.0);
        selectionThreshold = getDoubleParameter("selectionThreshold");
        windowSize = getIntegerParameter("windowSize");
    }

    public double get_priorAlpha() {
        return priorAlpha;
    }

    public void set_priorAlpha(double priorAlpha) {
        this.priorAlpha = priorAlpha;
        setDoubleParameter("priorAlpha", priorAlpha);
    }

    public double get_priorBeta() {
        return priorBeta;
    }

    public void set_priorBeta(double priorBeta) {
        this.priorBeta = priorBeta;
        setDoubleParameter("priorBeta", priorBeta);
    }

    public double get_directEvidenceWeight() {
        return directEvidenceWeight;
    }

    public void set_directEvidenceWeight(double directEvidenceWeight) {
        this.directEvidenceWeight = directEvidenceWeight;
        setDoubleParameter("directEvidenceWeight", directEvidenceWeight);
    }

    public double get_witnessEvidenceWeight() {
        return witnessEvidenceWeight;
    }

    public void set_witnessEvidenceWeight(double witnessEvidenceWeight) {
        this.witnessEvidenceWeight = witnessEvidenceWeight;
        setDoubleParameter("witnessEvidenceWeight", witnessEvidenceWeight);
    }

    public double get_pathLengthPenalty() {
        return pathLengthPenalty;
    }

    public void set_pathLengthPenalty(double pathLengthPenalty) {
        this.pathLengthPenalty = pathLengthPenalty;
        setDoubleParameter("pathLengthPenalty", pathLengthPenalty);
    }

    public double get_uncertaintyPenalty() {
        return uncertaintyPenalty;
    }

    public void set_uncertaintyPenalty(double uncertaintyPenalty) {
        this.uncertaintyPenalty = uncertaintyPenalty;
        setDoubleParameter("uncertaintyPenalty", uncertaintyPenalty);
    }

    private double getOptionalDoubleParameter(String parameterName, double defaultValue) {
        String value = parameters.getProperty(parameterName);
        if (value == null) {
            setDoubleParameter(parameterName, defaultValue);
            return defaultValue;
        }
        return Double.parseDouble(value);
    }

    public double get_selectionThreshold() {
        return selectionThreshold;
    }

    public void set_selectionThreshold(double selectionThreshold) {
        this.selectionThreshold = selectionThreshold;
        setDoubleParameter("selectionThreshold", selectionThreshold);
    }

    public int get_windowSize() {
        return windowSize;
    }

    public void set_windowSize(int windowSize) {
        this.windowSize = windowSize;
        setIntegerParameter("windowSize", windowSize);
    }

    @Override
    public String toString() {
        String s = parametersFileHeader;
        s += "priorAlpha=" + priorAlpha + "\n";
        s += "priorBeta=" + priorBeta + "\n";
        s += "directEvidenceWeight=" + directEvidenceWeight + "\n";
        s += "witnessEvidenceWeight=" + witnessEvidenceWeight + "\n";
        s += "pathLengthPenalty=" + pathLengthPenalty + "\n";
        s += "uncertaintyPenalty=" + uncertaintyPenalty + "\n";
        s += "selectionThreshold=" + selectionThreshold + "\n";
        s += "windowSize=" + windowSize + "\n";
        return s;
    }
}
