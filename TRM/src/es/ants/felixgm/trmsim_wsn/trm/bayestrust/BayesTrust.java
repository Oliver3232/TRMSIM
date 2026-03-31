package es.ants.felixgm.trmsim_wsn.trm.bayestrust;

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
 * Bayesian trust model using direct and witness evidence with Beta-Bernoulli updating.
 */
public class BayesTrust extends TRModel_WSN {
    private static final double MIN_SATISFACTION = 0.0;
    private static final double MAX_SATISFACTION = 1.0;

    public BayesTrust(BayesTrust_Parameters parameters) {
        super(parameters);
    }

    public static String get_name() {
        return "BayesTrust";
    }

    @Override
    public GatheredInformation gatherInformation(Sensor client, Service service) {
        Collection<Vector<Sensor>> pathsToServers = client.findSensors(new IsServerSearchCondition(service));
        return new GatheredInformation(pathsToServers);
    }

    @Override
    public synchronized Vector<Sensor> scoreAndRanking(Sensor client, GatheredInformation gi) {
        BayesTrust_Parameters parameters = (BayesTrust_Parameters) trmParameters;
        double bestScore = Double.NEGATIVE_INFINITY;
        Vector<Sensor> bestPath = null;

        for (Vector<Sensor> pathToServer : gi.getPathsToServers()) {
            if ((pathToServer == null) || pathToServer.isEmpty() || !pathToServer.lastElement().isActive()) {
                continue;
            }

            BayesTrust_Sensor bayesClient = (BayesTrust_Sensor) client;
            BayesTrust_Sensor bayesServer = (BayesTrust_Sensor) pathToServer.lastElement();
            BayesTrust_Sensor.Evidence directEvidence = bayesClient.getDirectEvidence(bayesServer);
            BayesTrust_Sensor.Evidence witnessEvidence = bayesServer.getWitnessEvidenceExcluding(bayesClient);

            double weightedSuccesses =
                    (directEvidence.getSuccesses() * parameters.get_directEvidenceWeight()) +
                    (witnessEvidence.getSuccesses() * parameters.get_witnessEvidenceWeight());
            double weightedFailures =
                    (directEvidence.getFailures() * parameters.get_directEvidenceWeight()) +
                    (witnessEvidence.getFailures() * parameters.get_witnessEvidenceWeight());

            double posterior = (parameters.get_priorAlpha() + weightedSuccesses)
                    / (parameters.get_priorAlpha() + parameters.get_priorBeta() + weightedSuccesses + weightedFailures);
            int hops = Math.max(1, pathToServer.size() - 1);
            double hopPenalty = 1.0 / (1.0 + (parameters.get_pathLengthPenalty() * Math.max(0, hops - 1)));
            double score = posterior * hopPenalty;

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
        Outcome outcome = null;
        if ((path == null) || path.isEmpty() || !path.lastElement().isActive()) {
            return outcome;
        }

        BayesTrust_Sensor client = (BayesTrust_Sensor) path.firstElement();
        BayesTrust_Sensor server = (BayesTrust_Sensor) path.lastElement();
        Service receivedService = server.serve(service, path);
        boolean satisfied = (receivedService != null);
        outcome = new EnergyConsumptionOutcome(
                new SatisfactionInterval(MIN_SATISFACTION, MAX_SATISFACTION, satisfied ? MAX_SATISFACTION : MIN_SATISFACTION),
                path.size());

        client.recordDirectExperience(server, satisfied);
        server.recordWitnessExperience(client, satisfied);
        return outcome;
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
        return new BayesTrust_Network(numSensors, probClients, rangeFactor, probServices, probGoodness, services);
    }

    @Override
    public Network loadCurrentNetwork(String fileName) throws Exception {
        return new BayesTrust_Network(fileName);
    }
}
