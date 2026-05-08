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
 * SVM trust model backed by LIBSVM, using local and witness evidence features.
 */
public class SVMTrust extends TRModel_WSN {
    private static final double MIN_SATISFACTION = 0.0;
    private static final double MAX_SATISFACTION = 1.0;
    private static final int FEATURE_COUNT = 5;

    private final LibSvmTrustClassifier classifier = new LibSvmTrustClassifier();
    private int coldStartSelectionCursor;

    public SVMTrust(SVMTrust_Parameters parameters) {
        super(parameters);
        resetModelState();
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
        Vector<Vector<Sensor>> candidatePaths = new Vector<Vector<Sensor>>();

        for (Vector<Sensor> pathToServer : gi.getPathsToServers()) {
            if ((pathToServer == null) || pathToServer.isEmpty() || !pathToServer.lastElement().isActive()) {
                continue;
            }
            candidatePaths.add(pathToServer);

            for (int i = 0; i < pathToServer.size() - 1; i++) {
                pathToServer.get(i).addTransmittedDistance((long) pathToServer.get(i).distance(pathToServer.get(i + 1)));
            }
        }

        if (candidatePaths.isEmpty()) {
            return new Vector<Sensor>();
        }

        if (!classifier.isTrained()) {
            return selectColdStartPath(candidatePaths);
        }

        double bestScore = Double.NEGATIVE_INFINITY;
        Vector<Sensor> bestPath = null;
        for (Vector<Sensor> pathToServer : candidatePaths) {
            double[] features = extractFeatures((SVMTrust_Sensor) client, (SVMTrust_Sensor) pathToServer.lastElement(), pathToServer.size());
            double score = classifier.score(features);

            if (score >= bestScore) {
                bestScore = score;
                bestPath = pathToServer;
            }
        }

        if ((bestPath == null) || (bestScore < parameters.get_selectionThreshold())) {
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
        classifier.addExample(features, satisfied, (SVMTrust_Parameters) trmParameters);

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

    @Override
    public synchronized void resetModelState() {
        classifier.reset();
        coldStartSelectionCursor = 0;
    }

    public synchronized boolean isLibSvmModelTrained() {
        return classifier.isTrained();
    }

    public synchronized int getLibSvmTrainingExampleCount() {
        return classifier.getTrainingExampleCount();
    }

    public synchronized int getLibSvmTrustedExampleCount() {
        return classifier.getTrustedExampleCount();
    }

    public synchronized int getLibSvmUntrustedExampleCount() {
        return classifier.getUntrustedExampleCount();
    }

    public synchronized int getLibSvmTrainingCount() {
        return classifier.getTrainingCount();
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
        double directBalance = toSignedRate(directRate);
        double witnessBalance = toSignedRate(witnessRate);
        double agreement = 1.0 - Math.abs(directRate - witnessRate);
        double hopScore = 1.0 / (1.0 + (Math.max(0, pathSize - 1) * parameters.get_pathLengthPenalty()));

        return new double[] {directBalance, witnessBalance, confidence, hopScore, agreement};
    }

    private Vector<Sensor> selectColdStartPath(Vector<Vector<Sensor>> candidatePaths) {
        Vector<Sensor> selectedPath = candidatePaths.get(coldStartSelectionCursor % candidatePaths.size());
        coldStartSelectionCursor++;
        return selectedPath;
    }

    private double toSignedRate(double probability) {
        return (2.0 * probability) - 1.0;
    }
}
