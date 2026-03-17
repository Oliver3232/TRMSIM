package es.ants.felixgm.trmsim_wsn.gui.parameterpanels.lftm;

import es.ants.felixgm.trmsim_wsn.trm.lftm.LFTM_Parameters;

final class LFTMPanelParametersMapper {
    private LFTMPanelParametersMapper() {
    }

    static void copyPanelToParameters(LFTM_ParametersPanel panel, LFTM_Parameters parameters) {
        parameters.set_phi(panel.get_phi());
        parameters.set_rho(panel.get_rho());
        parameters.set_q0(panel.get_q0());
        parameters.set_numAnts(panel.get_numAnts());
        parameters.set_numIterations(panel.get_numIterations());
        parameters.set_alpha(panel.get_alpha());
        parameters.set_beta(panel.get_beta());
        parameters.set_initialPheromone(panel.get_initialPheromone());
        parameters.set_transitionThreshold(panel.get_transitionThreshold());
        parameters.set_pathLengthFactor(panel.get_pathLengthFactor());
        parameters.set_punishmentThreshold(panel.get_punishmentThreshold());
        parameters.set_U_MIN(panel.get_U_MIN());
        parameters.set_U_MAX(panel.get_U_MAX());
        parameters.set_linguisticTerms(panel.getLinguisticTerms());
    }

    static void copyParametersToPanel(LFTM_Parameters parameters, LFTM_ParametersPanel panel) {
        panel.set_phi(parameters.get_phi());
        panel.set_rho(parameters.get_rho());
        panel.set_q0(parameters.get_q0());
        panel.set_numAnts(parameters.get_numAnts());
        panel.set_numIterations(parameters.get_numIterations());
        panel.set_alpha(parameters.get_alpha());
        panel.set_beta(parameters.get_beta());
        panel.set_initialPheromone(parameters.get_initialPheromone());
        panel.set_transitionThreshold(parameters.get_transitionThreshold());
        panel.set_pathLengthFactor(parameters.get_pathLengthFactor());
        panel.set_punishmentThreshold(parameters.get_punishmentThreshold());
        panel.set_U_MIN(LFTM_Parameters.get_U_MIN());
        panel.set_U_MAX(LFTM_Parameters.get_U_MAX());
        panel.set_LinguisticTerms(LFTM_Parameters.get_linguisticTerms());
    }
}
