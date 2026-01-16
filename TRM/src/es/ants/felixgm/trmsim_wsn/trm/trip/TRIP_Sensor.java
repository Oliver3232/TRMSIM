package es.ants.felixgm.trmsim_wsn.trm.trip;

import es.ants.felixgm.trmsim_wsn.network.Sensor;
import es.ants.felixgm.trmsim_wsn.trm.libs.fuzzylib.LinguisticTerm;
import java.util.Hashtable;
import java.util.Vector;

/**
 * <p>This class models a Sensor implementing TRIP</p>
 * @author <a href="http://ants.dif.um.es/~felixgm/en" target="_blank">F&eacute;lix G&oacute;mez M&aacute;rmol</a>, <a href="http://webs.um.es/gregorio" target="_blank">Gregorio Mart&iacute;nez P&eacute;rez</a>
 * @version 0.5
 * @since 0.5
 */
public class TRIP_Sensor extends Sensor {

    private double alpha;
    private double beta;
    private double gamma;

    private Hashtable<TRIP_Sensor,Double> weights;
    private Hashtable<TRIP_Sensor,Double> trustValues;
    private Hashtable<TRIP_Sensor,Vector<Hashtable<TRIP_Sensor,Double>>> recommenders;

    private Vector<LinguisticTerm> trustLevels;
    /** Indicates if this sensor is a Road Side Unit (true) or not (false), i.e., it is a vehicle */
    protected boolean isRSU;

    // NEW: Local parameters
    private TRIP_Parameters parameters;

    private static final double MIN_WEIGHT_VALUE = 0.00001;
    private static final double MAX_WEIGHT_VALUE = 0.99999;

    /**
     * This constructor creates a new Sensor implementing TRIP
     */
    public TRIP_Sensor() {
        super();
    }

    /**
     * This constructor creates a new Sensor implementing TRIP
     * @param id Identifier of the new sensor
     * @param x X initial coordinate of the new sensor
     * @param y Y initial coordinate of the new sensor
     */
    public TRIP_Sensor(int id, double x, double y) {
        super(id,x,y);
    }

    // --- NEW CONSTRUCTORS FOR PARALLEL SIMULATION ---

    public TRIP_Sensor(TRIP_Parameters parameters) {
        super();
        this.parameters = parameters;
    }

    public TRIP_Sensor(int id, double x, double y, TRIP_Parameters parameters) {
        super(id, x, y);
        this.parameters = parameters;
    }

    // --- NEW SETTER ---
    public void setParameters(TRIP_Parameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public void reset() {
        // MODIFIED: Use local 'parameters' instead of global static call
        // Fallback to default if parameters are null (should not happen if initialized correctly)
        TRIP_Parameters params = (this.parameters != null) ? this.parameters : (TRIP_Parameters)trmmodelWSN.get_TRMParameters();

        alpha = params.get_initialAlpha();
        beta = params.get_initialBeta();
        gamma = params.get_initialGamma();

        weights = new Hashtable<TRIP_Sensor,Double>();
        trustValues = new Hashtable<TRIP_Sensor,Double>();
        recommenders = new Hashtable<TRIP_Sensor,Vector<Hashtable<TRIP_Sensor,Double>>>();
        trustLevels = new Vector<LinguisticTerm>();

        trustLevels.add(params.get_notTrustFuzzySet());
        trustLevels.add(params.get_moreOrLessTrustFuzzySet());
        trustLevels.add(params.get_trustFuzzySet());
    }

    public void addRecommender(TRIP_Sensor recommendee, TRIP_Sensor recommender, double recommendation) {
        if (!recommenders.containsKey(recommendee))
            recommenders.put(recommendee, new Vector<Hashtable<TRIP_Sensor,Double>>());
        recommenders.get(recommendee).add(new Hashtable<TRIP_Sensor,Double>());
        recommenders.get(recommendee).lastElement().put(recommender, recommendation);
    }

    public Vector<Hashtable<TRIP_Sensor,Double>> getRecommenders(TRIP_Sensor recommendee) {
        return recommenders.get(recommendee);
    }

    public void resetRecommenders(TRIP_Sensor recommendee) {
        recommenders.remove(recommendee);
    }

    public LinguisticTerm get_trustLevel(double trustValue) {
        double trustLevelProbability[] = new double[trustLevels.size()];
        double membershipSum = 0.0;

        int i = 0;
        for (LinguisticTerm trustLevel : trustLevels) {
            trustLevelProbability[i] = trustLevel.membership(trustValue);
            membershipSum += trustLevelProbability[i];
            i++;
        }

        if (membershipSum != 0.0)
            for (i = 0; i < trustLevelProbability.length; i++)
                trustLevelProbability[i] = trustLevelProbability[i]/membershipSum;

        int index = getTrustLevelIndex(trustLevelProbability);

        return trustLevels.get(index);
    }

    private int getTrustLevelIndex(double trustLevelProbability[]) {
        int index = 0;
        double r = Math.random();
        double accumulator = 0.0;

        for (int i = 0; i < trustLevelProbability.length; i++) {
            if ((accumulator <= r) && (r < accumulator+trustLevelProbability[i])) {
                index = i;
                break;
            }
            accumulator += trustLevelProbability[i];
        }

        return index;
    }

    public double get_alpha() { return alpha; }
    public double get_beta() { return beta; }
    public double get_gamma() { return gamma; }

    public double get_weight(TRIP_Sensor tripSensor) {
        if (weights.containsKey(tripSensor))
            return weights.get(tripSensor);
        return Double.NaN;
    }

    public double get_trustValue(TRIP_Sensor tripSensor) {
        if (!trustValues.containsKey(tripSensor))
            set_trustValue(tripSensor, 0.5);
        return trustValues.get(tripSensor);
    }

    public double get_recommendation(TRIP_Sensor tripSensor) {
        try {
            if (this.isRSU)
                return tripSensor.get_goodness(requiredService);
            else {
                if ((collusion) && (get_goodness(requiredService) < 0.5))
                    return (tripSensor.get_goodness(requiredService) >= 0.5) ? 0.0 : 1.0;
                else
                    return get_trustValue(tripSensor);
            }
        } catch (Exception ex) {
            //return Double.NaN;
        }
        return Double.NaN;
    }

    public void set_alpha(double alpha) { this.alpha = alpha; }
    public void set_beta(double beta) { this.beta = beta; }
    public void set_gamma(double gamma) { this.gamma = gamma; }

    public synchronized void set_weight(TRIP_Sensor tripSensor, double weight) {
        if (weight < MIN_WEIGHT_VALUE)
            weight = MIN_WEIGHT_VALUE;
        if (weight > MAX_WEIGHT_VALUE)
            weight = MAX_WEIGHT_VALUE;

        if (!weights.containsKey(tripSensor)) {
            if (weights.isEmpty()) {
                weights.put(tripSensor, MAX_WEIGHT_VALUE);
            } else {
                for (TRIP_Sensor _tripSensor : ((Hashtable<TRIP_Sensor,Double>)weights.clone()).keySet()) {
                    if (!tripSensor.equals(_tripSensor)) {
                        double normalizedWeight = weights.get(_tripSensor)*(1.0-weight);
                        if (normalizedWeight < MIN_WEIGHT_VALUE)
                            normalizedWeight = MIN_WEIGHT_VALUE;
                        if (normalizedWeight > MAX_WEIGHT_VALUE)
                            normalizedWeight = MAX_WEIGHT_VALUE;
                        weights.put(_tripSensor, normalizedWeight);
                    }
                }
                weights.put(tripSensor, weight);
            }
        } else {
            if (weights.size() > 1) {
                double oldWeight = weights.get(tripSensor);
                for (TRIP_Sensor _tripSensor : weights.keySet()) {
                    double normalizedWeight = weights.get(_tripSensor)*((1.0-weight)/(1-oldWeight));
                    if (normalizedWeight < MIN_WEIGHT_VALUE)
                        normalizedWeight = MIN_WEIGHT_VALUE;
                    if (normalizedWeight > MAX_WEIGHT_VALUE)
                        normalizedWeight = MAX_WEIGHT_VALUE;
                    weights.put(_tripSensor, normalizedWeight);
                }
                weights.put(tripSensor, weight);
            }
        }
        double weightsSum = 0.0;
        for (TRIP_Sensor _tripSensor : ((Hashtable<TRIP_Sensor,Double>)weights.clone()).keySet())
            weightsSum += weights.get(_tripSensor);
        for (TRIP_Sensor _tripSensor : ((Hashtable<TRIP_Sensor,Double>)weights.clone()).keySet())
            weights.put(_tripSensor, weights.get(_tripSensor)/weightsSum);
    }

    public void set_trustValue(TRIP_Sensor tripSensor, double trustValue) {
        trustValues.put(tripSensor, trustValue);
    }

    /**
     * This method indicates whether this node is a RSU or not
     * @return true if this node is a RSU, false otherwise
     */
    public boolean isRSU() { return isRSU; }

    /**
     * This method sets this node as a RSU or normal node
     * @param isRSU If true, this node is set as RSU; otherwise, it is set as a normal node
     */
    public void setRSU(boolean isRSU) { this.isRSU = isRSU; }
}