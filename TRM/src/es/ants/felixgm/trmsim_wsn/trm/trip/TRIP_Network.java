package es.ants.felixgm.trmsim_wsn.trm.trip;

import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Sensor;
import es.ants.felixgm.trmsim_wsn.network.Service;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This class models a network composed by sensors implementing TRIP</p>
 */
public class TRIP_Network extends Network {

    private TRIP_Parameters tripParameters;

    public TRIP_Network(
            int numSensors,
            double probClients,
            double rangeFactor,
            Collection<Double> probServices,
            Collection<Double> probGoodness,
            Collection<Service> services,
            TRIP_Parameters parameters) { // Pridaný parameter

        // 1. Super vytvorí senzory, ale s tripParameters = null (lebo ešte nie je priradené)
        super(numSensors, probClients, rangeFactor, probServices, probGoodness, services);

        // 2. Uložíme parametre
        this.tripParameters = parameters;

        // 3. OPRAVA: Dodatočne nastavíme parametre všetkým už vytvoreným senzorom
        for (Sensor s : sensors) {
            if (s instanceof TRIP_Sensor) {
                ((TRIP_Sensor) s).setParameters(parameters);
            }
        }

        // Zvyšok logiky pre nastavenie služieb...
        servers.removeAll(servers);
        clients.removeAll(clients);
        // Poznámka: Iterujeme cez existujúce senzory, nevytvárame nové
        for (Sensor sensor : sensors) {
            servers.add(sensor);
            clients.add(sensor);
            Iterator<Double> itProbServices = probServices.iterator();
            Iterator<Double> itProbGoodness = probGoodness.iterator();
            for (Service service : services) {
                if (itProbServices.hasNext() && Math.random() <= itProbServices.next().doubleValue()) {
                    double goodness = 0.0;
                    if (itProbGoodness.hasNext() && Math.random() <= itProbGoodness.next().doubleValue())
                        goodness = 1.0;

                    sensor.addService(service, goodness);

                    if (!this.services.contains(service))
                        this.services.add(service);
                }
            }
        }
        reset();
        setNewNeighbors(rangeFactor);
    }

    public TRIP_Network(String xmlFilePath, TRIP_Parameters parameters) throws Exception {
        super(xmlFilePath); // Vytvorí senzory s null parametrami
        this.tripParameters = parameters;

        // OPRAVA: Dodatočne nastavíme parametre
        for (Sensor s : sensors) {
            if (s instanceof TRIP_Sensor) {
                ((TRIP_Sensor) s).setParameters(parameters);
            }
        }

        for (Sensor client : clients) {
            client.addService(new Service("Relay"), 1.0);
            servers.add(client);
        }
        for (Sensor server : servers)
            if (!clients.contains(server))
                clients.add(server);
        reset();
    }

    @Override
    public void reset() {
        super.reset();
        for (Sensor sensor : sensors)
            if (((TRIP_Sensor)sensor).isRSU())
                return;

        // Použitie lokálnej premennej
        double rsuPercentage = (tripParameters != null) ? tripParameters.get_rsuPercentage() : 0.1;
        int numRSUs = (int)(sensors.size() * rsuPercentage);

        if ((rsuPercentage > 0) && (numRSUs == 0))
            numRSUs = 1;

        int safetyCounter = 0; // Ochrana proti nekonečnému cyklu
        while (numRSUs > 0 && safetyCounter < sensors.size() * 2) {
            int selectedServerIdx = (int)(Math.random() * servers.size());
            Sensor s = ((List<Sensor>)servers).get(selectedServerIdx);

            if (s instanceof TRIP_Sensor && !((TRIP_Sensor)s).isRSU()) {
                ((TRIP_Sensor)s).setRSU(true);
                numRSUs--;
            }
            safetyCounter++;
        }
    }

    @Override
    public void setNewNeighbors(double newRange) {
        double rangeThreshold = newRange*Math.sqrt(2.0)*maxDistance;
        double rsuRangeThreshold = 2.0*rangeThreshold;

        for (Sensor sensor : sensors) {
            sensor.removeAllNeighbors();
            for (Sensor sensor2 : sensors) {
                boolean isRSU1 = (sensor instanceof TRIP_Sensor) && ((TRIP_Sensor)sensor).isRSU();
                boolean isRSU2 = (sensor2 instanceof TRIP_Sensor) && ((TRIP_Sensor)sensor2).isRSU();

                if (isRSU1 || isRSU2) {
                    if ((!sensor.equals(sensor2)) && (sensor.distance(sensor2) < rsuRangeThreshold)) {
                        sensor.addLink(sensor2);
                        sensor2.addLink(sensor);
                    }
                } else {
                    if ((!sensor.equals(sensor2)) && (sensor.distance(sensor2) < rangeThreshold)) {
                        sensor.addLink(sensor2);
                        sensor2.addLink(sensor);
                    }
                }
            }
        }
    }

    @Override
    public Sensor newSensor(){
        return new TRIP_Sensor(this.tripParameters);
    }

    @Override
    public Sensor newSensor(int id, double x, double y) {
        return new TRIP_Sensor(id, x, y, this.tripParameters);
    }
}