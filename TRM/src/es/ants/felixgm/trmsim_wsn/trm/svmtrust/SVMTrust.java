package es.ants.felixgm.trmsim_wsn.trm.svmtrust;

import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Sensor;
import es.ants.felixgm.trmsim_wsn.network.Service;
import es.ants.felixgm.trmsim_wsn.outcomes.EnergyConsumptionOutcome;
import es.ants.felixgm.trmsim_wsn.outcomes.Outcome;
import es.ants.felixgm.trmsim_wsn.satisfaction.SatisfactionInterval;
import es.ants.felixgm.trmsim_wsn.search.IsServerSearchCondition;
import es.ants.felixgm.trmsim_wsn.trm.GatheredInformation;
import es.ants.felixgm.trmsim_wsn.trm.TRModel_WSN;

import java.util.Collection;
import java.util.Vector;

/**
 * Lightweight online linear SVM trust model using local and witness evidence features.
 */
public class SVMTrust extends TRModel_WSN {
    private static final double MIN_SATISFACTION = 0.0;
    private static final double MAX_SATISFACTION = 1.0;

    private final double[] weights = new double[] {0.9, 0.6, 0.3, 0.8};
    private double bias = -0.2;

    public SVMTrust(SVMTrust_Parameters parameters) {
        super(parameters);
    }

    public static String get_name() {
        return "SVMTrust";
    }

    @Override
    public GatheredInformation gatherInformation(Sensor client, Service service) {
        Collection<Vector<Sensor>> pathsToServers = client.findSensors(new IsServerSearchCondition(service));
        return new GatheredInformation(pathsToServers);
    }

    @Override
    public synchronized Vector<Sensor> scoreAndRanking(Sensor client, GatheredInformation gi) {
        SVMTrust_Parameters parameters = (SVMTrust_Parameters) trmParameters;
        double bestScore = Double.NEGATIVE_INFINITY;
        Vector<Sensor> bestPath = null;

        for (Vector<Sensor> pathToServer : gi.getPathsToServers()) {
            if ((pathToServer == null) || pathToServer.isEmpty() || !pathToServer.lastElement().isActive()) {
                continue;
            }

            double[] features = extractFeatures((SVMTrust_Sensor) client, (SVMTrust_Sensor) pathToServer.lastElement(), pathToServer.size());
            double score = margin(features);

            if (score >= bestScore) {
                bestScore = score;
                bestPath = pathToServer;
            }

            for (int i = 0; i < pathToServer.size() - 1; i++) {
                pathToServer.get(i).addTransmittedDistance((long) pathToServer.get(i).distance(pathToServer.get(i + 1)));
            }
        }

        if (bestPath == null) {
            return new Vector<Sensor>();
        }
        return bestPath;
    }

    @Override
    public synchronized Outcome performTransaction(Vector<Sensor> path, Service service) {
        if ((path == null) || path.isEmpty() || !path.lastElement().isActive()) {
            return null;
        }

        SVMTrust_Sensor client = (SVMTrust_Sensor) path.firstElement();
        SVMTrust_Sensor server = (SVMTrust_Sensor) path.lastElement();
        double[] features = extractFeatures(client, server, path.size());
        Service receivedService = server.serve(service, path);
        boolean satisfied = (receivedService != null);

        client.recordDirectExperience(server, satisfied);
        server.recordWitnessExperience(client, satisfied);
        updateModel(features, satisfied ? 1.0 : -1.0);

        return new EnergyConsumptionOutcome(
                new SatisfactionInterval(MIN_SATISFACTION, MAX_SATISFACTION, satisfied ? MAX_SATISFACTION : MIN_SATISFACTION),
                path.size());
    }

    @Override
    public Outcome reward(Vector<Sensor> path, Outcome outcome) {
        return outcome;
    }

    @Override
    public Outcome punish(Vector<Sensor> path, Outcome outcome) {
        return outcome;
    }

    @Override
    public Network generateRandomNetwork(
            int numSensors,
            double probClients,
            double rangeFactor,
            Collection<Double> probServices,
            Collection<Double> probGoodness,
            Collection<Service> services) {
        return new SVMTrust_Network(numSensors, probClients, rangeFactor, probServices, probGoodness, services);
    }

    @Override
    public Network loadCurrentNetwork(String fileName) throws Exception {
        return new SVMTrust_Network(fileName);
    }

    private double[] extractFeatures(SVMTrust_Sensor client, SVMTrust_Sensor server, int pathSize) {
        SVMTrust_Parameters parameters = (SVMTrust_Parameters) trmParameters;
        SVMTrust_Sensor.Evidence directEvidence = client.getDirectEvidence(server);
        SVMTrust_Sensor.Evidence witnessEvidence = server.getWitnessEvidenceExcluding(client);

        double directTotal = directEvidence.getSuccesses() + directEvidence.getFailures();
        double witnessTotal = witnessEvidence.getSuccesses() + witnessEvidence.getFailures();
        double directRate = directTotal > 0.0 ? directEvidence.getSuccesses() / directTotal : 0.5;
        double witnessRate = witnessTotal > 0.0 ? witnessEvidence.getSuccesses() / witnessTotal : 0.5;
        double confidence = Math.min(1.0,
                ((directTotal * parameters.get_directEvidenceWeight()) + (witnessTotal * parameters.get_witnessEvidenceWeight()))
                        / Math.max(1.0, parameters.get_evidenceNormalizationFactor()));
        double hopScore = 1.0 / (1.0 + (Math.max(0, pathSize - 1) * parameters.get_pathLengthPenalty()));

        return new double[] {directRate, witnessRate, confidence, hopScore};
    }

    private double margin(double[] features) {
        double score = bias;
        for (int i = 0; i < features.length; i++) {
            score += weights[i] * features[i];
        }
        return score;
    }

    private void updateModel(double[] features, double label) {
        SVMTrust_Parameters parameters = (SVMTrust_Parameters) trmParameters;
        double lr = parameters.get_learningRate();
        double lambda = parameters.get_regularization();
        double currentMargin = label * margin(features);

        for (int i = 0; i < weights.length; i++) {
            weights[i] = weights[i] * (1.0 - (lr * lambda));
            if (currentMargin < 1.0) {
                weights[i] += lr * label * features[i];
            }
        }
        if (currentMargin < 1.0) {
            bias += lr * label;
        }
    }
}
