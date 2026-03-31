package es.ants.felixgm.trmsim_wsn.trm.bayestrust;

import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Sensor;
import es.ants.felixgm.trmsim_wsn.network.Service;

import java.util.Collection;

/**
 * Network composed of BayesTrust sensors.
 */
public class BayesTrust_Network extends Network {
    public BayesTrust_Network(
            int numSensors,
            double probClients,
            double rangeFactor,
            Collection<Double> probServices,
            Collection<Double> probGoodness,
            Collection<Service> services) {
        super(numSensors, probClients, rangeFactor, probServices, probGoodness, services);
        reset();
    }

    public BayesTrust_Network(String xmlFilePath) throws Exception {
        super(xmlFilePath);
        reset();
    }

    @Override
    public Sensor newSensor() {
        return new BayesTrust_Sensor();
    }

    @Override
    public Sensor newSensor(int id, double x, double y) {
        return new BayesTrust_Sensor(id, x, y);
    }
}
