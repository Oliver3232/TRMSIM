/**
 * "TRMSim-WSN, Trust and Reputation Models Simulator for Wireless
 * Sensor Networks" is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * ... (License header kept intact) ...
 */

package es.ants.felixgm.trmsim_wsn.trm.peertrust;

import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Sensor;
import es.ants.felixgm.trmsim_wsn.network.Service;
import java.util.Collection;

/**
 * <p>This class models a network composed by sensors implementing PeerTrust</p>
 * ... (Javadoc kept intact) ...
 */
public class PeerTrust_Network extends Network {

    // NEW: Storing parameters locally
    private PeerTrust_Parameters parameters;

    /**
     * This constructor creates a new random PeerTrust Network using the given parameters
     * ... (Javadoc params kept intact) ...
     * @param parameters PeerTrust specific parameters [NEW]
     */
    public PeerTrust_Network(
            int numSensors,
            double probClients,
            double rangeFactor,
            Collection<Double> probServices,
            Collection<Double> probGoodness,
            Collection<Service> services,
            PeerTrust_Parameters parameters) { // Added parameters argument
        super(numSensors, probClients, rangeFactor, probServices, probGoodness, services);
        this.parameters = parameters;
        initializeSensors(); // Initialize sensors with parameters
        reset();
    }

    /**
     * This method loads a network from a XML file and creates the specific
     * corresponding PeerTrust Network
     * ... (Javadoc params kept intact) ...
     */
    public PeerTrust_Network(String xmlFilePath, PeerTrust_Parameters parameters) throws Exception {
        super(xmlFilePath);
        this.parameters = parameters;
        initializeSensors(); // Initialize sensors with parameters
        reset();
    }

    // Helper to push parameters to sensors (replacing static setup)
    private void initializeSensors() {
        int windowSize = parameters.get_windowSize();
        for (Sensor s : sensors) {
            if (s instanceof PeerTrust_Sensor) {
                ((PeerTrust_Sensor) s).setWindowSize(windowSize);
            }
        }
    }

    @Override
    public void reset() {
        // REMOVED: PeerTrust_Sensor.set_windowSize(...); -> Replaced by initializeSensors()
        initializeSensors();
        super.reset();
    }

    @Override
    public Sensor newSensor(){
        return new PeerTrust_Sensor();
    }

    @Override
    public Sensor newSensor(int id, double x, double y) {
        return new PeerTrust_Sensor(id,x,y);
    }
}