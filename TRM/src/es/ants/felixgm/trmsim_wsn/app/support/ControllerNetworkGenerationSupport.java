package es.ants.felixgm.trmsim_wsn.app.support;

import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Service;
import es.ants.felixgm.trmsim_wsn.trm.TRModel_WSN;

import java.util.ArrayList;

public final class ControllerNetworkGenerationSupport {
    private ControllerNetworkGenerationSupport() {
    }

    public static Network createRandomNetwork(
            TRModel_WSN trustModel,
            Service requiredService,
            int minNumSensors,
            int maxNumSensors,
            double probClients,
            double probRelay,
            double probMalicious,
            double radioRange,
            boolean dynamic,
            boolean collusion) {
        int numSensors = (int) (minNumSensors + Math.random() * Math.abs(maxNumSensors - minNumSensors));
        ArrayList<Service> services = buildNetworkServices(requiredService);
        ArrayList<Double> probServices = buildServiceProbabilities(probRelay);
        ArrayList<Double> probGoodness = buildGoodnessProbabilities(probMalicious);

        Network network = trustModel.generateRandomNetwork(
                numSensors,
                probClients,
                radioRange,
                probServices,
                probGoodness,
                services);

        network.set_collusion(collusion);
        network.set_dynamic(dynamic);
        return network;
    }

    private static ArrayList<Service> buildNetworkServices(Service requiredService) {
        ArrayList<Service> services = new ArrayList<Service>();
        services.add(new Service("Relay"));
        services.add(requiredService);
        return services;
    }

    private static ArrayList<Double> buildServiceProbabilities(double probRelay) {
        ArrayList<Double> probabilities = new ArrayList<Double>();
        probabilities.add(1.0);
        probabilities.add(1.0 - probRelay);
        return probabilities;
    }

    private static ArrayList<Double> buildGoodnessProbabilities(double probMalicious) {
        ArrayList<Double> probabilities = new ArrayList<Double>();
        probabilities.add(1.0);
        probabilities.add(1.0 - probMalicious);
        return probabilities;
    }
}
