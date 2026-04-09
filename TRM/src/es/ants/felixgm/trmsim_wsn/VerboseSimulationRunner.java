package es.ants.felixgm.trmsim_wsn;

import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Sensor;
import es.ants.felixgm.trmsim_wsn.network.Service;
import es.ants.felixgm.trmsim_wsn.outcomes.Outcome;
import es.ants.felixgm.trmsim_wsn.trm.TRModel_WSN;
import es.ants.felixgm.trmsim_wsn.trm.TrustModelBundle;
import es.ants.felixgm.trmsim_wsn.trm.TrustModelFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Command-line oriented execution utility kept outside the GUI class.
 */
public final class VerboseSimulationRunner {
    private VerboseSimulationRunner() {
    }

    private static void executeClients(Network network) throws InterruptedException {
        Thread[] clients = new Thread[network.get_numClients()];
        int index = 0;
        for (Sensor client : network.get_clients()) {
            clients[index++] = new Thread(client);
        }
        for (Thread clientThread : clients) {
            clientThread.start();
        }
        for (Thread clientThread : clients) {
            clientThread.join();
        }
    }

    public static void runVerbose(
            String[] trustModelNames,
            Service requiredService,
            int numNetworks,
            int numExecutions,
            int minNumSensors,
            int maxNumSensors,
            double probClients,
            double probRelay,
            double probMalicious,
            boolean dynamic,
            boolean oscillating,
            boolean collusion,
            String currentVersion) {
        System.out.println("Executing TRMSim-WSN " + currentVersion + " in verbose mode... [" + (new java.util.Date()) + "]\n");
        try {
            Collection<Outcome> outcomes;
            int avgLinksPerSensor = 5;

            for (String trustModelName : trustModelNames) {
                String outcomesFileName = trustModelName + "_outcomes";
                for (int numSensors = minNumSensors; numSensors <= maxNumSensors; numSensors += 100) {
                    String currentOutcomesFileName = outcomesFileName + "_" + numSensors + ".txt";
                    outcomes = new ArrayList<Outcome>();
                    double radioRange = Math.sqrt(avgLinksPerSensor / (2.0 * Math.PI * numSensors));
                    for (double currentProbMalicious = probMalicious; currentProbMalicious < 0.9001; currentProbMalicious += 0.1) {
                        System.out.println("Running " + trustModelName + "; Ns = " + numSensors + "; %Mal = " + currentProbMalicious);
                        System.out.flush();

                        Outcome outcome = runTrustModel(
                                trustModelName,
                                requiredService,
                                numNetworks,
                                numExecutions,
                                minNumSensors,
                                maxNumSensors,
                                probClients,
                                probRelay,
                                currentProbMalicious,
                                radioRange,
                                dynamic,
                                oscillating,
                                collusion);

                        if (outcome != null) {
                            outcomes.add(outcome);
                        }

                        Outcome.writeToFile(outcomes, currentOutcomesFileName);
                    }
                }
                System.out.println();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Outcome runTrustModel(
            String trustModelName,
            Service requiredService,
            int numNetworks,
            int numExecutions,
            int minNumSensors,
            int maxNumSensors,
            double probClients,
            double probRelay,
            double probMalicious,
            double radioRange,
            boolean dynamic,
            boolean oscillating,
            boolean collusion) throws Exception {
        TrustModelBundle trustModelBundle = TrustModelFactory.create(trustModelName);
        TRModel_WSN trustModel = trustModelBundle.getModel();
        Network network = null;
        Collection<Outcome> globalOutcomes = new ArrayList<Outcome>();
        Sensor.set_TRModel_WSN(trustModel);

        for (int net = 0; net < numNetworks; net++) {
            if ((network == null) || (numNetworks != 1)) {
                int numSensors = (int) (minNumSensors + Math.random() * Math.abs(maxNumSensors - minNumSensors));
                ArrayList<Double> probServices = new ArrayList<Double>();
                ArrayList<Double> probGoodness = new ArrayList<Double>();
                ArrayList<Service> services = new ArrayList<Service>();

                services.add(new Service("Relay"));
                services.add(requiredService);

                probServices.add(1.0);
                probGoodness.add(1.0);
                probServices.add(1.0 - probRelay);
                probGoodness.add(1.0 - probMalicious);

                network = trustModel.generateRandomNetwork(numSensors, probClients, radioRange, probServices, probGoodness, services);
                network.set_collusion(collusion);
                network.set_dynamic(dynamic);
	            }

                trustModel.resetModelState();

	            for (Sensor client : network.get_clients()) {
	                client.set_requiredService(requiredService);
	            }

            Collection<Outcome> outcomes = new ArrayList<Outcome>();
            int executions = 0;
            if ((net % 5) == 0) {
                System.out.println("\tnet = " + net);
                System.out.flush();
            }
            for (; executions < numExecutions; executions++) {
                executeClients(network);

                for (Sensor client : network.get_clients()) {
                    if (client.get_outcome() != null) {
                        outcomes.add(client.get_outcome());
                    }
                }

                if (oscillating && (executions % 20 == 0)) {
                    network.oscillate(requiredService);
                }
            }

            Outcome outcome = Outcome.computeOutcomes(outcomes, network, requiredService, executions);
            if (outcome == null) {
                if (net > 0) {
                    net--;
                }
            } else {
                globalOutcomes.add(outcome);
            }
        }

        return Outcome.computeOutcomes(globalOutcomes);
    }
}
