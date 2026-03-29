package es.ants.felixgm.trmsim_wsn.trm.lftm;

final class LFTMScalarParametersSupport {
    interface DoubleParameterReader {
        double read(String key);
    }

    private LFTMScalarParametersSupport() {
    }

    static String buildHeader() {
        return "####################################\n"
                + "# LFTM parameters file\n"
                + "# " + (new java.util.Date()) + "\n"
                + "####################################\n";
    }

    static void applyDefaults(LFTM_Parameters parameters) {
        parameters.set_phi(0.1);
        parameters.set_rho(0.2);
        parameters.set_q0(0.98);
        parameters.set_alpha(1.0);
        parameters.set_beta(1.0);
        parameters.set_numAnts(0.5);
        parameters.set_numIterations(0.5);
        parameters.set_initialPheromone(0.5);
        parameters.set_pathLengthFactor(0.5);
        parameters.set_transitionThreshold(0.5);
        parameters.set_punishmentThreshold(0.5);
        parameters.set_U_MIN(0.0);
        parameters.set_U_MAX(1.0);
    }

    static void load(LFTM_Parameters parameters, DoubleParameterReader reader) {
        parameters.set_phi(reader.read("phi"));
        parameters.set_rho(reader.read("rho"));
        parameters.set_q0(reader.read("q0"));
        parameters.set_alpha(reader.read("alpha"));
        parameters.set_beta(reader.read("beta"));
        parameters.set_numAnts(reader.read("numAnts"));
        parameters.set_numIterations(reader.read("numIterations"));
        parameters.set_initialPheromone(reader.read("initialPheromone"));
        parameters.set_pathLengthFactor(reader.read("pathLengthFactor"));
        parameters.set_transitionThreshold(reader.read("transitionThreshold"));
        parameters.set_punishmentThreshold(reader.read("punishmentThreshold"));
        parameters.set_U_MIN(reader.read("U_MIN"));
        parameters.set_U_MAX(reader.read("U_MAX"));
    }

    static void appendTo(StringBuilder builder, LFTM_Parameters parameters) {
        builder.append("phi=").append(parameters.get_phi()).append('\n');
        builder.append("rho=").append(parameters.get_rho()).append('\n');
        builder.append("q0=").append(parameters.get_q0()).append('\n');
        builder.append("numAnts=").append(parameters.get_numAnts()).append('\n');
        builder.append("numIterations=").append(parameters.get_numIterations()).append('\n');
        builder.append("alpha=").append(parameters.get_alpha()).append('\n');
        builder.append("beta=").append(parameters.get_beta()).append('\n');
        builder.append("initialPheromone=").append(parameters.get_initialPheromone()).append('\n');
        builder.append("pathLengthFactor=").append(parameters.get_pathLengthFactor()).append('\n');
        builder.append("transitionThreshold=").append(parameters.get_transitionThreshold()).append('\n');
        builder.append("punishmentThreshold=").append(parameters.get_punishmentThreshold()).append('\n');
        builder.append("U_MIN=").append(LFTM_Parameters.get_U_MIN()).append('\n');
        builder.append("U_MAX=").append(LFTM_Parameters.get_U_MAX()).append('\n');
    }
}
