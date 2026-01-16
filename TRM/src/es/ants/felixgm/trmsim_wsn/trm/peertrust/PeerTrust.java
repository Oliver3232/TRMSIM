/**
 * "TRMSim-WSN, Trust and Reputation Models Simulator for Wireless
 * Sensor Networks" is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version always keeping
 * the additional terms specified in this license.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * * ... (License header kept intact) ...
 */

package es.ants.felixgm.trmsim_wsn.trm.peertrust;

import es.ants.felixgm.trmsim_wsn.outcomes.EnergyConsumptionOutcome;
import es.ants.felixgm.trmsim_wsn.search.IsServerSearchCondition;
import es.ants.felixgm.trmsim_wsn.trm.GatheredInformation;
import java.util.Vector;

import es.ants.felixgm.trmsim_wsn.outcomes.Outcome;
import es.ants.felixgm.trmsim_wsn.satisfaction.SatisfactionInterval;
import es.ants.felixgm.trmsim_wsn.network.Sensor;
import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Service;

import es.ants.felixgm.trmsim_wsn.trm.TRModel_WSN;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Hashtable;

/**
 * <p>This class models PeerTrust ... (Javadoc kept intact) ...
 */
public class PeerTrust extends TRModel_WSN {
    /** Minimum satisfaction value: {@value} */
    protected final double MIN_SATISFACTION = 0.0;
    /** Maximum satisfaction value: {@value} */
    protected final double MAX_SATISFACTION = 1.0;

    /**
     * Class PeerTrust constructor
     * @param peerTrust_parameters Parameters needed for the algorithm, as described <a href="#PeerTrustparameters">before</a>
     */
    public PeerTrust(PeerTrust_Parameters peerTrust_parameters) {
        super(peerTrust_parameters);
    }

    /**
     * Returns the name of this model, i.e., "PeerTrust"
     * @return The name of this model, i.e., "PeerTrust"
     */
    public static String get_name() { return "PeerTrust"; }

    @Override
    public GatheredInformation gatherInformation(Sensor client, Service service) {
        Collection<Vector<Sensor>> pathsToServers = client.findSensors(new IsServerSearchCondition(service));
        return new GatheredInformation(pathsToServers);
    }

    @Override
    public synchronized Vector<Sensor> scoreAndRanking(Sensor client, GatheredInformation gi) {
        double MaxTPSM = Double.NEGATIVE_INFINITY;
        double MaxNow = 0.0;
        Vector<Sensor> mostTrustworthyPath = new Vector<Sensor>();
        for (Vector<Sensor> pathToServer : gi.getPathsToServers())
            if (pathToServer.lastElement().isActive()) {
                MaxNow = computeTPSM((PeerTrust_Sensor)pathToServer.lastElement(), (PeerTrust_Sensor)client);
                if (MaxNow >= MaxTPSM) {
                    MaxTPSM = MaxNow;
                    mostTrustworthyPath = pathToServer;
                }
                for (int i = 0; i < pathToServer.size()-1; i++)
                    pathToServer.get(i).addTransmittedDistance((long)pathToServer.get(i).distance(pathToServer.get(i+1)));
            }
        return mostTrustworthyPath;
    }

    @Override
    public synchronized Outcome performTransaction(Vector<Sensor> path, Service service) {
        Outcome outcome = null;
        if ((path == null) || (path.size() == 0) || (!path.lastElement().isActive()))
            return outcome;

        PeerTrust_Sensor client = (PeerTrust_Sensor)path.firstElement();
        PeerTrust_Sensor server = (PeerTrust_Sensor)path.lastElement();
        Service receivedService = server.serve(service,path);
        if (receivedService == null)
            outcome = new EnergyConsumptionOutcome(new SatisfactionInterval(MIN_SATISFACTION,MAX_SATISFACTION,MIN_SATISFACTION),path.size());
        else
            outcome = new EnergyConsumptionOutcome(new SatisfactionInterval(MIN_SATISFACTION,MAX_SATISFACTION,MAX_SATISFACTION),path.size());

        client.addNewTransaction(client, server, null);
        server.addNewTransaction(client, server, outcome);

        return outcome;
    }

    /**
     * PeerTrust does not implement this method, since it does not apply any specific reward step
     * @param path
     * @param outcome
     */
    @Override
    public Outcome reward(Vector<Sensor> path, Outcome outcome) {
        return outcome;
    }

    /**
     * PeerTrust does not implement this method, since it does not apply any specific punishment step
     * @param path
     * @param outcome
     */
    @Override
    public Outcome punish(Vector<Sensor> path, Outcome outcome) {
        return outcome;
    }

    /**
     * This method computes the trust that a given client places in a given
     * server using the Trust Peer Similarity Metric ... (Javadoc kept intact) ...
     */
    private synchronized double computeTPSM(PeerTrust_Sensor server, PeerTrust_Sensor client) {
        double satisfaction = 0.0;
        double similaritiesSum = 0.0;
        double result = 0.0;
        Hashtable<Sensor,Double> similarity = new Hashtable<Sensor,Double>();

        if (server.getNumTransactions() == 0)
            return 1.0;

        for (Transaction transaction : server.getTransactions()) {
            similarity.put((PeerTrust_Sensor)transaction.getClient(), sim((PeerTrust_Sensor)transaction.getClient(),client));
            similaritiesSum += similarity.get(transaction.getClient());
        }

        if (similaritiesSum != 0.0)
            for (Transaction transaction : server.getTransactions()) {
                if (client.collusion) { // Changed from PeerTrust_Sensor.collusion() to instance variable
                    try {
                        if (server.get_goodness(client.get_requiredService()) < 0.5)
                            satisfaction = MAX_SATISFACTION-Math.random()*((MAX_SATISFACTION-MIN_SATISFACTION)/2);
                        else
                            satisfaction = MIN_SATISFACTION+Math.random()*((MAX_SATISFACTION-MIN_SATISFACTION)/2);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else
                    satisfaction = ((SatisfactionInterval)transaction.getSatisfaction()).getSatisfactionValue();
                result += satisfaction * (similarity.get(transaction.getClient()) / similaritiesSum);
            }

        return result;
    }

    /**
     * This method computes the similarity between two given clients ... (Javadoc kept intact) ...
     */
    private double sim(PeerTrust_Sensor client1, PeerTrust_Sensor client2) {
        double denominator1 = 1.0;
        double denominator2 = 1.0;
        double satisfaction1 = 0.0;
        double satisfaction2 = 0.0;
        double member1 = 0.0;
        double member2 = 0.0;
        double similarity = 0.0;

        Collection<PeerTrust_Sensor> IJS = computeIJS(client1,client2);
        client1.addTransmittedDistance((long)client1.distance(client2));

        if (IJS.size() == 0)
            return 0.0;

        for (PeerTrust_Sensor server : IJS) {
            denominator1 = 0.0;
            denominator2 = 0.0;
            satisfaction1 = 0.0;
            satisfaction2 = 0.0;
            member1 = 0.0;
            member2 = 0.0;
            for (Transaction transaction : server.getTransactions()) {
                if (transaction.getClient().equals(client1)) {
                    if (client1.collusion) { // Changed to instance variable
                        try {
                            if (server.get_goodness(client1.get_requiredService()) < 0.5)
                                satisfaction1 = MAX_SATISFACTION;
                            else
                                satisfaction1 = MIN_SATISFACTION;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else
                        satisfaction1 += ((SatisfactionInterval)transaction.getSatisfaction()).getSatisfactionValue();
                    denominator1++;
                }
                if (transaction.getClient().equals(client2)) {
                    if (client2.collusion) { // Changed to instance variable
                        try {
                            if (server.get_goodness(client2.get_requiredService()) < 0.5)
                                satisfaction2 = MAX_SATISFACTION;
                            else
                                satisfaction2 = MIN_SATISFACTION;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else
                        satisfaction2 += ((SatisfactionInterval)transaction.getSatisfaction()).getSatisfactionValue();
                    denominator2++;
                }
            }
            member1 = satisfaction1 / denominator1;
            member2 = satisfaction2 / denominator2;
            similarity += (member1-member2)*(member1-member2);
        }

        similarity = similarity / IJS.size();
        return (1.0 - Math.sqrt(similarity));
    }

    /*
     * This method obtains the set IJS of servers that have interacted with both
     * the two given sensors in the past ... (Javadoc kept intact) ...
     */
    private synchronized Collection<PeerTrust_Sensor> computeIJS(PeerTrust_Sensor client1, PeerTrust_Sensor client2){
        Collection<PeerTrust_Sensor> collectionIJS = new LinkedList<PeerTrust_Sensor>();
        Collection<Transaction> sensor1Transactions = client1.getTransactions();
        Collection<Transaction> sensor2Transactions = client2.getTransactions();
        client1.addTransmittedDistance((long)client1.distance(client2));

        for (Transaction sensor1Transaction : sensor1Transactions)
            for (Transaction sensor2Transaction : sensor2Transactions)
                if ((sensor1Transaction.getServer().equals(sensor2Transaction.getServer())) &&
                        (!collectionIJS.contains((PeerTrust_Sensor)sensor1Transaction.getServer())))
                    collectionIJS.add((PeerTrust_Sensor)sensor1Transaction.getServer());
        return collectionIJS;
    }

    @Override
    public Network generateRandomNetwork(
            int numSensors,
            double probClients,
            double rangeFactor,
            Collection<Double> probServices,
            Collection<Double> probGoodness,
            Collection<Service> services) {
        // FIX: Passing parameters to the network constructor
        return new PeerTrust_Network(numSensors,probClients,rangeFactor,probServices,probGoodness,services, (PeerTrust_Parameters) get_TRMParameters());
    }

    @Override
    public Network loadCurrentNetwork(String fileName) throws Exception {
        // FIX: Passing parameters to the network constructor
        return new PeerTrust_Network(fileName, (PeerTrust_Parameters) get_TRMParameters());
    }
}