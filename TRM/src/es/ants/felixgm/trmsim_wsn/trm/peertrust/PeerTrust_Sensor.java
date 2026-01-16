/**
 * "TRMSim-WSN, Trust and Reputation Models Simulator for Wireless
 * Sensor Networks" is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * ... (License header kept intact) ...
 */

package es.ants.felixgm.trmsim_wsn.trm.peertrust;


import es.ants.felixgm.trmsim_wsn.outcomes.Outcome;
import es.ants.felixgm.trmsim_wsn.network.Sensor;
import es.ants.felixgm.trmsim_wsn.network.Service;
import java.util.Collection;
import java.util.LinkedList;

/**
 * <p>This class models a Sensor implementing PeerTrust</p>
 * ... (Javadoc kept intact) ...
 */
public class PeerTrust_Sensor extends Sensor {
    /** Window size for storing transactions outcomes */
    // CHANGED: Removed static
    protected int windowSize;
    /** Collection of Transactions this sensor has had */
    protected Collection<Transaction> transactions;

    /**
     * This constructor creates a new Sensor implementing PeerTrust
     */
    public PeerTrust_Sensor () {
        super();
        transactions = new LinkedList<Transaction>();
    }

    /**
     * This constructor creates a new Sensor implementing PeerTrust
     * ... (Javadoc params kept intact) ...
     */
    public PeerTrust_Sensor(int id, double x, double y) {
        super(id,x,y);
        transactions = new LinkedList<Transaction>();
    }

    /**
     * This method adds a new Transaction to the collection of transactions of this sensor
     * ... (Javadoc params kept intact) ...
     */
    public synchronized void addNewTransaction(PeerTrust_Sensor client, PeerTrust_Sensor server, Outcome outcome){
        // CHANGED: Use instance variable 'windowSize'
        if ((transactions.size() != 0) && (transactions.size() >= windowSize))
            ((LinkedList<Transaction>)transactions).removeLast();

        ((LinkedList<Transaction>)transactions).addFirst(new Transaction(client, server,outcome));
    }

    /**
     * This method gets the collection of Transactions this sensor has had
     * @return The collection of Transactions this sensor has had
     */
    public synchronized Collection<Transaction> getTransactions(){
        return (LinkedList<Transaction>)transactions;
    }

    /**
     * Returns the number of Transactions this sensor has had
     * @return The number of Transactions this sensor has had
     */
    public synchronized int getNumTransactions() {
        return transactions.size();
    }

    @Override
    public void reset() {
        transactions = new LinkedList<Transaction>();
    }

    /**
     * Indicates if ther is a collusion or not
     * @return true, if there is a collusion, false otherwise
     */
    // CHANGED: Removed static method, use inherited instance variable 'this.collusion'
    // public static boolean collusion() { return collusion; }

    /**
     * Returns the service requested by the client
     * @return The service requested by the client
     */
    public Service get_requiredService() { return requiredService; }

    /**
     * Sets the window size for storing transactions outcomes
     * @param windowSize New window size for storing transactions outcomes
     */
    // CHANGED: Removed static, now an instance method
    public void setWindowSize(int windowSize) { this.windowSize = windowSize; }
}