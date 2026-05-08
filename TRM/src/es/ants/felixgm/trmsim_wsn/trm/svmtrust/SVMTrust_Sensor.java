package es.ants.felixgm.trmsim_wsn.trm.svmtrust;

import es.ants.felixgm.trmsim_wsn.network.Sensor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Sensor state for SVMTrust.
 */
public class SVMTrust_Sensor extends Sensor {
    static final class Evidence {
        private double successes;
        private double failures;

        void record(boolean success) {
            if (success) {
                successes++;
            } else {
                failures++;
            }
        }

        void merge(Evidence other) {
            successes += other.successes;
            failures += other.failures;
        }

        double getSuccesses() { return successes; }
        double getFailures() { return failures; }
    }

    private static final class WitnessTransaction {
        private final SVMTrust_Sensor client;
        private final boolean success;

        private WitnessTransaction(SVMTrust_Sensor client, boolean success) {
            this.client = client;
            this.success = success;
        }
    }

    private Map<Integer, Evidence> directEvidenceByServerId;
    private LinkedList<WitnessTransaction> witnessTransactions;

    public SVMTrust_Sensor() {
        super();
        reset();
    }

    public SVMTrust_Sensor(int id, double x, double y) {
        super(id, x, y);
        reset();
    }

    @Override
    public void reset() {
        directEvidenceByServerId = new HashMap<Integer, Evidence>();
        witnessTransactions = new LinkedList<WitnessTransaction>();
    }

    public synchronized void recordDirectExperience(SVMTrust_Sensor server, boolean success) {
        Evidence evidence = directEvidenceByServerId.get(server.id());
        if (evidence == null) {
            evidence = new Evidence();
            directEvidenceByServerId.put(server.id(), evidence);
        }
        evidence.record(success);
    }

    public synchronized Evidence getDirectEvidence(SVMTrust_Sensor server) {
        Evidence stored = directEvidenceByServerId.get(server.id());
        Evidence snapshot = new Evidence();
        if (stored != null) {
            snapshot.merge(stored);
        }
        return snapshot;
    }

    public synchronized void recordWitnessExperience(SVMTrust_Sensor client, boolean success) {
        witnessTransactions.addFirst(new WitnessTransaction(client, success));
        trimWitnessTransactions();
    }

    public synchronized Evidence getWitnessEvidenceExcluding(SVMTrust_Sensor excludedClient) {
        Evidence summary = new Evidence();
        for (WitnessTransaction witnessTransaction : witnessTransactions) {
            if ((excludedClient != null) && witnessTransaction.client.equals(excludedClient)) {
                continue;
            }
            summary.record(witnessTransaction.success);
        }
        return summary;
    }

    private void trimWitnessTransactions() {
        int maxWindowSize = ((SVMTrust_Parameters) trustModel().get_TRMParameters()).get_windowSize();
        while (witnessTransactions.size() > maxWindowSize) {
            witnessTransactions.removeLast();
        }
    }
}
