package es.ants.felixgm.trmsim_wsn.trm.svmtrust;

import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Sensor;
import es.ants.felixgm.trmsim_wsn.network.Service;

import java.util.Collection;

/**
 * Network composed of SVMTrust sensors.
 */
public class SVMTrust_Network extends Network {
    public SVMTrust_Network(
            int numSensors,
            double probClients,
            double rangeFactor,
            Collection<Double> probServices,
            Collection<Double> probGoodness,
            Collection<Service> services) {
        super(numSensors, probClients, rangeFactor, probServices, probGoodness, services);
        reset();
    }

    public SVMTrust_Network(String xmlFilePath) throws Exception {
        super(xmlFilePath);
        reset();
    }

    @Override
    public Sensor newSensor() {
        return new SVMTrust_Sensor();
    }

    @Override
    public Sensor newSensor(int id, double x, double y) {
        return new SVMTrust_Sensor(id, x, y);
    }
}
