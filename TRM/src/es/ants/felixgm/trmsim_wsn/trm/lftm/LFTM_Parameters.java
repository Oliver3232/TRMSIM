/**
 *  "TRMSim-WSN, Trust and Reputation Models Simulator for Wireless
 * Sensor Networks" is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version always keeping
 * the additional terms specified in this license.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 *
 * Additional Terms of this License
 * --------------------------------
 *
 * 1. It is Required the preservation of specified reasonable legal notices
 *   and author attributions in that material and in the Appropriate Legal
 *   Notices displayed by works containing it.
 *
 * 2. It is limited the use for publicity purposes of names of licensors or
 *   authors of the material.
 *
 * 3. It is Required indemnification of licensors and authors of that material
 *   by anyone who conveys the material (or modified versions of it) with
 *   contractual assumptions of liability to the recipient, for any liability
 *   that these contractual assumptions directly impose on those licensors
 *   and authors.
 *
 * 4. It is Prohibited misrepresentation of the origin of that material, and it is
 *   required that modified versions of such material be marked in reasonable
 *   ways as different from the original version.
 *
 * 5. It is Declined to grant rights under trademark law for use of some trade
 *   names, trademarks, or service marks.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program (lgpl.txt).  If not, see <http://www.gnu.org/licenses/>
*/

package es.ants.felixgm.trmsim_wsn.trm.lftm;

import es.ants.felixgm.trmsim_wsn.trm.TRMParameters;
import es.ants.felixgm.trmsim_wsn.trm.libs.fuzzylib.FuzzyRule;
import es.ants.felixgm.trmsim_wsn.trm.libs.fuzzylib.FuzzyRuleSet;
import es.ants.felixgm.trmsim_wsn.trm.libs.fuzzylib.LinguisticTerm;
import es.ants.felixgm.trmsim_wsn.trm.libs.fuzzylib.Variable;
import java.util.Collection;
import java.util.Vector;

/**
 * <p>This class represents the set of parameters' values of {@link LFTM}.</p>
 * <p>A LFTM parameters file has the following structure:</p>
 * <pre>
 *    ####################################
 *    # LFTM parameters file
 *    ####################################
 *    phi=0.01
 *    rho=0.87
 *    q0=0.45
 *    numAnts=0.35
 *    numIterations=0.59
 *    alpha=1.0
 *    beta=1.0
 *    initialPheromone=0.85
 *    pathLengthFactor=0.71
 *    transitionThreshold=0.66
 *    punishmentThreshold=0.48
 *    U_MIN=0.0
 *    U_MAX=1.0
 *    VH_A=0.7
 *    VH_B=0.9
 *    VH_C=1.0
 *    VH_D=1.0
 *    H_A=0.55
 *    H_B=0.7
 *    H_C=0.8
 *    H_D=0.9
 *    M_A=0.3
 *    M_B=0.45
 *    M_C=0.55
 *    M_D=0.7
 *    L_A=0.1
 *    L_B=0.2
 *    L_C=0.3
 *    L_D=0.45
 *    VL_A=0.0
 *    VL_B=0.0
 *    VL_C=0.1
 *    VL_D=0.3
 * </pre>
 * This file can be downloaded
 * <a href="http://ants.dif.um.es/~felixgm/research/trmsim-wsn/resources/LFTMparameters.txt" target=_blank">here</a>.
 * But if any of the parameters can not be successfully extracted from the file, they are set
 * to a default value.
 *
 * @author <a href="http://ants.dif.um.es/~felixgm/en" target="_blank">F&eacute;lix G&oacute;mez M&aacute;rmol</a>, <a href="http://webs.um.es/gregorio" target="_blank">Gregorio Mart&iacute;nez P&eacute;rez</a>
 * @version 0.4
 * @since 0.4
 */
public class LFTM_Parameters extends TRMParameters {
    /** Default parameters file name */
    public static final String defaultParametersFileName = "trmodels/lftm/LFTMparameters.txt";

    private double phi;
    private double rho;
    private double q0;
    private double numAnts;
    private double numIterations;
    private double alpha;
    private double beta;
    private double initialPheromone;
    private double pathLengthFactor;
    private double transitionThreshold;
    private double punishmentThreshold;

    private static double U_MIN;
    private static double U_MAX;
    private static Collection<LinguisticTerm> linguisticTerms;

    /**
     * Creates a new instance of LFTM_Parameters setting them to their default values
     */
    public LFTM_Parameters() {
        super();
        parametersFileHeader = LFTMScalarParametersSupport.buildHeader();
        LFTMScalarParametersSupport.applyDefaults(this);
        linguisticTerms = getDefaultLinguisticTerms();
    }

    /**
     * Creates a new instance of LFTM_Parameters from a given parameters file name
     * @param fileName LFTM parameters file name
     * @throws java.lang.Exception If any parameter can not be successfully retrieved
     */
    public LFTM_Parameters(String fileName) throws Exception {
        super(fileName);
        parametersFileHeader = LFTMScalarParametersSupport.buildHeader();
        LFTMScalarParametersSupport.load(this, this::getDoubleParameter);
        linguisticTerms  = loadLinguisticTerms();
    }

    /**
     * Returns phi parameter value
     * @return phi parameter value
     */
    public double get_phi() { return phi; }

    /**
     * Returns rho parameter value
     * @return rho parameter value
     */
    public double get_rho() { return rho; }

    /**
     * Returns q0 parameter value
     * @return q0 parameter value
     */
    public double get_q0() { return q0; }

    /**
     * Returns alpha parameter value
     * @return alpha parameter value
     */
    public double get_alpha() { return alpha; }

    /**
     * Returns beta parameter value
     * @return beta parameter value
     */
    public double get_beta() { return beta; }

    /**
     * Returns numAnts parameter value
     * @return numAnts parameter value
     */
    public double get_numAnts() { return numAnts; }

    /**
     * Returns numIterations parameter value
     * @return numIterations parameter value
     */
    public double get_numIterations() { return numIterations; }

    /**
     * Returns initialPheromone parameter value
     * @return initialPheromone parameter value
     */
    public double get_initialPheromone() { return initialPheromone; }

    /**
     * Returns pathLengthFactor parameter value
     * @return pathLengthFactor parameter value
     */
    public double get_pathLengthFactor() { return pathLengthFactor; }

    /**
     * Returns transitionThreshold parameter value
     * @return transitionThreshold parameter value
     */
    public double get_transitionThreshold() { return transitionThreshold; }

    /**
     * Returns punishmentThreshold parameter value
     * @return punishmentThreshold parameter value
     */
    public double get_punishmentThreshold() { return punishmentThreshold; }

    /**
     * Returns U_MIN parameter value
     * @return U_MIN parameter value
     */
    public static double get_U_MIN() { return U_MIN; }

    /**
     * Returns U_MAX parameter value
     * @return U_MAX parameter value
     */
    public static double get_U_MAX() { return U_MAX; }

    /**
     * Returns linguisticTerms parameter value
     * @return linguisticTerms parameter value
     */
    public static Collection<LinguisticTerm> get_linguisticTerms() { return linguisticTerms; }


    /**
     * Sets a new phi parameter value
     * @param phi New phi parameter value
     */
    public void set_phi(double phi) {
        this.phi = phi;
        setDoubleParameter("phi",phi);
    }

    /**
     * Sets a new rho parameter value
     * @param rho New rho parameter value
     */
    public void set_rho(double rho) {
        this.rho = rho;
        setDoubleParameter("rho",rho);
    }

    /**
     * Sets a new q0 parameter value
     * @param q0 New q0 parameter value
     */
    public void set_q0(double q0) {
        this.q0 = q0;
        setDoubleParameter("q0",q0);
    }

    /**
     * Sets a new alpha parameter value
     * @param alpha New alpha parameter value
     */
    public void set_alpha(double alpha) {
        this.alpha = alpha;
        setDoubleParameter("alpha",alpha);
    }

    /**
     * Sets a new beta parameter value
     * @param beta New beta parameter value
     */
    public void set_beta(double beta) {
        this.beta = beta;
        setDoubleParameter("beta",beta);
    }

    /**
     * Sets a new numAnts parameter value
     * @param numAnts New numAnts parameter value
     */
    public void set_numAnts(double numAnts) {
        this.numAnts = numAnts;
        setDoubleParameter("numAnts",numAnts);
    }

    /**
     * Sets a new numIterations parameter value
     * @param numIterations New numIterations parameter value
     */
    public void set_numIterations(double numIterations) {
        this.numIterations = numIterations;
        setDoubleParameter("numIterations",numIterations);
    }

    /**
     * Sets a new initialPheromone parameter value
     * @param initialPheromone New initialPheromone parameter value
     */
    public void set_initialPheromone(double initialPheromone) {
        this.initialPheromone = initialPheromone;
        setDoubleParameter("initialPheromone",initialPheromone);
    }

    /**
     * Sets a new pathLengthFactor parameter value
     * @param pathLengthFactor New pathLengthFactor parameter value
     */
    public void set_pathLengthFactor(double pathLengthFactor) {
        this.pathLengthFactor = pathLengthFactor;
        setDoubleParameter("pathLengthFactor",pathLengthFactor);
    }

    /**
     * Sets a new transitionThreshold parameter value
     * @param transitionThreshold New transitionThreshold parameter value
     */
    public void set_transitionThreshold(double transitionThreshold) {
        this.transitionThreshold = transitionThreshold;
        setDoubleParameter("transitionThreshold",transitionThreshold);
    }

    /**
     * Sets a new punishmentThreshold parameter value
     * @param punishmentThreshold New punishmentThreshold parameter value
     */
    public void set_punishmentThreshold(double punishmentThreshold) {
        this.punishmentThreshold = punishmentThreshold;
        setDoubleParameter("punishmentThreshold",punishmentThreshold);
    }

    /**
     * Sets a new U_MIN parameter value
     * @param _U_MIN New U_MIN parameter value
     */
    public void set_U_MIN(double _U_MIN) {
        U_MIN = _U_MIN;
        setDoubleParameter("U_MIN",U_MIN);
    }

    /**
     * Sets a new U_MAX parameter value
     * @param _U_MAX New U_MAX parameter value
     */
    public void set_U_MAX(double _U_MAX) { 
        U_MAX = _U_MAX;
        setDoubleParameter("U_MAX",U_MAX);
    }

    /**
     * Sets a new linguisticTerms parameter value
     * @param _linguisticTerms New linguisticTerms parameter value
     */
    public void set_linguisticTerms(Collection<LinguisticTerm> _linguisticTerms) {
        linguisticTerms = _linguisticTerms;
        LFTMLinguisticTermsSupport.write(linguisticTerms, this::setDoubleParameter);
    }

    /**
     * This method loads the linguisticTerm parameter from the parameters file
     * @return The linguisticTerm parameter loaded from the parameters file
     */
    private Collection<LinguisticTerm> loadLinguisticTerms() {
        return LFTMLinguisticTermsSupport.load(this::getDoubleParameter, U_MIN, U_MAX);
    }

    /**
     * This method returns a default set of linguistic terms
     * @return A default set of linguistic terms
     */
    private static Collection<LinguisticTerm> getDefaultLinguisticTerms() {
        if (linguisticTerms == null) {
            linguisticTerms = LFTMLinguisticTermsSupport.createDefaults(U_MIN, U_MAX);
        }
        return linguisticTerms;
    }

    /**
     * This method returns the set of fuzzy rules corresponding to the comparison
     * of those service attributes which improve when they increase (for example, the quality)
     * @param service1Property Service 1's property or attribute (quality...)
     * @param service2Property Service 2's property or attribute (quality...)
     * @param servicesPropertiesComparisson Output fuzzy set that will contain the actual
     * comparison between the two given service properties or attributes
     * @return The set of fuzzy rules corresponding to the comparison of those service
     * attributes which improve when they increase (for example, the price)
     */
    public static FuzzyRuleSet getFRSServicesAttributesPositive(Variable service1Property,
            Variable service2Property, Variable servicesPropertiesComparisson) {
        Vector<LinguisticTerm> sortedLT = (Vector<LinguisticTerm>)linguisticTerms;
        return LFTMFuzzyRuleSupport.buildRuleSet(
                service1Property,
                service2Property,
                servicesPropertiesComparisson,
                sortedLT,
                (i, j) -> {
                    if (i == j) return "Medium";
                    if ((j > i) && (j < i + 2)) return "Low";
                    if ((j > i) && (j >= i + 2)) return "Very Low";
                    if ((j < i) && (j > i - 2)) return "High";
                    return "Very High";
                });
    }

    /**
     * This method returns the set of fuzzy rules corresponding to the comparison
     * of those service attributes which improve when they decrease (for example, the price)
     * @param service1Property Service 1's property or attribute (price, delivery time...)
     * @param service2Property Service 2's property or attribute (price, delivery time...)
     * @param servicesPropertiesComparisson Output fuzzy set that will contain the actual
     * comparison between the two given service properties or attributes
     * @return The set of fuzzy rules corresponding to the comparison of those service
     * attributes which improve when they decrease (for example, the price)
     */
    public static FuzzyRuleSet getFRSServicesAttributesNegative(Variable service1Property,
            Variable service2Property, Variable servicesPropertiesComparisson) {
        Vector<LinguisticTerm> sortedLT = (Vector<LinguisticTerm>)linguisticTerms;
        return LFTMFuzzyRuleSupport.buildRuleSet(
                service1Property,
                service2Property,
                servicesPropertiesComparisson,
                sortedLT,
                (i, j) -> {
                    if (i == j) return "Medium";
                    if ((j > i) && (j < i + 2)) return "High";
                    if ((j > i) && (j >= i + 2)) return "Very High";
                    if ((j < i) && (j > i - 2)) return "Low";
                    return "Very Low";
                });
    }

    /**
     * This method returns the set of fuzzy rules which will determine the value of
     * the attribute of the service provided by a server, for those service attributes
     * which improve when they increase (for example, the quality)
     * @param serverGoodness Goodness of the server providing the service
     * @param offeredServiceProperty Property or attribute of the service offered by the server
     * @param givenServiceProperty Output fuzzy set that will contain the value of the
     * property or attribute of the service actually provided by the server
     * @return The set of fuzzy rules which will determine the value of
     * the attribute of the service provided by a server, for those service attributes
     * which improve when they increase (for example, the quality)
     */
    public static FuzzyRuleSet getFRSServerGoodnessPositive(Variable serverGoodness,
            Variable offeredServiceProperty, Variable givenServiceProperty) {
        Vector<LinguisticTerm> sortedLT = (Vector<LinguisticTerm>)linguisticTerms;
        return LFTMFuzzyRuleSupport.buildRuleSet(
                serverGoodness,
                offeredServiceProperty,
                givenServiceProperty,
                sortedLT,
                (i, j) -> {
                    if (j < 3 - i) return "Very Low";
                    if ((j >= 3 - i) && (j < 4 - i)) return "Low";
                    if (j == 4 - i) return "Medium";
                    if ((j > 4 - i) && (j < 4 - i + 2)) return "High";
                    return "Very High";
                });
    }

    /**
     * This method returns the set of fuzzy rules which will determine the value of
     * the attribute of the service provided by a server, for those service attributes
     * which improve when they decrease (for example, the price)
     * @param serverGoodness Goodness of the server providing the service
     * @param offeredServiceProperty Property or attribute of the service offered by the server
     * @param givenServiceProperty Output fuzzy set that will contain the value of the
     * property or attribute of the service actually provided by the server
     * @return The set of fuzzy rules which will determine the value of
     * the attribute of the service provided by a server, for those service attributes
     * which improve when they decrease (for example, the price)
     */
    public static FuzzyRuleSet getFRSServerGoodnessNegative(Variable serverGoodness,
            Variable offeredServiceProperty, Variable givenServiceProperty) {
        Vector<LinguisticTerm> sortedLT = (Vector<LinguisticTerm>)linguisticTerms;
        return LFTMFuzzyRuleSupport.buildRuleSet(
                serverGoodness,
                offeredServiceProperty,
                givenServiceProperty,
                sortedLT,
                (i, j) -> {
                    if (i == j) return "Medium";
                    if ((j > i) && (j < i + 2)) return "High";
                    if ((j > i) && (j >= i + 2)) return "Very High";
                    if ((j < i) && (j > i - 2)) return "Low";
                    return "Very Low";
                });
    }

    /**
     * This method returns the set of fuzzy rules which will determine the satisfaction
     * of a client with the actually received service
     * @param clientConformity Client's conformity
     * @param servicesComparison Comparison between the requested service (the one
     * initially offered by the server) and the actually received one
     * @param clientSatisfaction Output fuzzy set that will contain the satisfaction
     * of a client with the actually received service
     * @return The set of fuzzy rules which determine the satisfaction
     * of a client with the actually received service
     */
    public static FuzzyRuleSet getFRSClientSatisfaction(Variable clientConformity, Variable servicesComparison,
            Variable clientSatisfaction) {
        Vector<LinguisticTerm> sortedLT = (Vector<LinguisticTerm>)linguisticTerms;
        return LFTMFuzzyRuleSupport.buildRuleSet(
                clientConformity,
                servicesComparison,
                clientSatisfaction,
                sortedLT,
                (i, j) -> {
                    if (j < 3 - i) return "Very Low";
                    if ((j >= 3 - i) && (j < 4 - i)) return "Low";
                    if (j == 4 - i) return "Medium";
                    if ((j > 4 - i) && (j < 4 - i + 2)) return "High";
                    return "Very High";
                });
    }

    /**
     * This method returns the set of fuzzy rules which will determine the
     * punishment or reward degree to be applied over the selected service provider
     * @param clientGoodness Client's goodness
     * @param clientSatisfaction Satisfaction of a client with the actually received service
     * @param punishmentReward Output fuzzy set that will contain the punishment
     * or reward degree to be applied over the selected service provider
     * @return The set of fuzzy rules which will determine the
     * punishment or reward degree to be applied over the selected service provider
     */
    public static FuzzyRuleSet getFRSPunishmentReward(Variable clientGoodness, Variable clientSatisfaction,
            Variable punishmentReward) {
        Vector<LinguisticTerm> sortedLT = (Vector<LinguisticTerm>)linguisticTerms;
        return LFTMFuzzyRuleSupport.buildRuleSet(
                clientGoodness,
                clientSatisfaction,
                punishmentReward,
                sortedLT,
                (i, j) -> {
                    if (j < 3 - i) return "Very High";
                    if ((j >= 3 - i) && (j < 4 - i)) return "High";
                    if (j == 4 - i) return "Medium";
                    if ((j > 4 - i) && (j < 4 - i + 2)) return "Low";
                    return "Very Low";
                });
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(parametersFileHeader);
        LFTMScalarParametersSupport.appendTo(builder, this);
        LFTMLinguisticTermsSupport.appendTo(builder, linguisticTerms);
        return builder.toString();
    }
}
