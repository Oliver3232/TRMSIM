package es.ants.felixgm.trmsim_wsn.network;

import es.ants.felixgm.trmsim_wsn.search.ISearchCondition;
import es.ants.felixgm.trmsim_wsn.search.IsServerSearchCondition;
import es.ants.felixgm.trmsim_wsn.trm.GatheredInformation;
import es.ants.felixgm.trmsim_wsn.trm.TRModel_WSN;
import es.ants.felixgm.trmsim_wsn.outcomes.Outcome;
import es.ants.felixgm.trmsim_wsn.search.IsSensorSearchCondition;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public abstract class Sensor implements Runnable {
    // Inštančné premenné pre nezávislosť simulácií
    public boolean collusion = false;
    public boolean dynamic = false;
    public boolean runningSimulation = false;
    public static double _maxDistance = 0;

    // Každý senzor má vlastný model
    public TRModel_WSN trmmodelWSN;

    public static int idCount = 1;
    public int id;
    public double xPosition;
    public double yPosition;
    public Collection<Link> links;
    public HashMap<Service,Double> servicesGoodness;
    public Service requiredService;
    public boolean activeState;
    public int numRequests;
    public static final int numRequestsThreshold = 20;
    public static final long sleepingTimeoutMilis = 1000;
    public Outcome outcome;
    public long transmittedDistance;
    public Timer numRequestsTimer;
    public Timer sleepTimer;
    public Timer sleepTimerAux;

    public Sensor() {
        this(idCount++,Math.random()*100.0,Math.random()*100.0);
    }

    public Sensor(int id, double x, double y) {
        this.id = id;
        links = new ArrayList<Link>();
        xPosition = x;
        yPosition = y;
        activeState = true;
        transmittedDistance = 0;

        servicesGoodness = new HashMap<Service,Double>();
        numRequests = 0;
        numRequestsTimer = null;
        sleepTimer = null;
        sleepTimerAux = null;
        sleepIfInactive(sleepingTimeoutMilis/2+((int)(Math.random()*(sleepingTimeoutMilis/2))));
    }

    public void run() {
        if (trmmodelWSN == null) return;

        if (reachesQualifiedService(requiredService)) {
            GatheredInformation gi = trmmodelWSN.gatherInformation(this, requiredService);
            Vector<Sensor> path = trmmodelWSN.scoreAndRanking(this,gi);
            outcome = trmmodelWSN.performTransaction(path,requiredService);
            if (outcome != null) {
                if (outcome.get_satisfaction().isSatisfied())
                    outcome = trmmodelWSN.reward(path,outcome);
                else
                    outcome = trmmodelWSN.punish(path,outcome);
            }
        } else
            outcome = null;
    }

    public Service serve(Service service, Vector<Sensor> path) {
        Service givenService = service.clone();
        try {
            if (get_goodness(service) < 0.5)
                givenService = null;
        } catch (Exception ex) {
            givenService = null;
        }

        for (int i = 0; i < path.size()-1; i++)
            transmittedDistance += path.get(i).distance(path.get(i+1));

        numRequests++;
        if (numRequests == numRequestsThreshold) {
            numRequests = 0;
            if (dynamic && runningSimulation) {
                activeState = false;
                numRequestsTimer = new Timer();
                numRequestsTimer.schedule(new TimerTask(){
                    @Override
                    public void run() {
                        activeState = true;
                        numRequestsTimer.cancel();
                    }
                },sleepingTimeoutMilis);
            }
        }
        return givenService;
    }

    private void sleepIfInactive(final long time) {
        if (dynamic && runningSimulation) {
            sleepTimer = new Timer();
            sleepTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (numRequests < numRequestsThreshold / 2) {
                        activeState = false;
                        sleepTimerAux = new Timer();
                        sleepTimerAux.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                activeState = true;
                                sleepTimerAux.cancel();
                            }
                        }, time /2);
                    }
                    sleepTimer.cancel();
                }
            }, time, time);
        }
    }

    // Settery pre inštančné premenné
    public void setCollusion(boolean coll) { this.collusion = coll; }
    public void setDynamic(boolean dyn) { this.dynamic = dyn; }
    public void setRunningSimulation(boolean run) { this.runningSimulation = run; }
    public static void setMaxDistance(double maxDist) { _maxDistance = maxDist; }
    public void setTRModel(TRModel_WSN model) { this.trmmodelWSN = model; }
    public TRModel_WSN getTRModel() { return this.trmmodelWSN; }

    public abstract void reset();

    public void cancelAllTimers() {
        activeState = true;
        if(numRequestsTimer != null){ numRequestsTimer.cancel(); numRequestsTimer.purge(); }
        if(sleepTimer != null){ sleepTimer.cancel(); sleepTimer.purge(); }
        if(sleepTimerAux != null){ sleepTimerAux.cancel(); sleepTimerAux.purge(); }
    }

    public boolean reachesQualifiedService(Service service) {
        Collection<Vector<Sensor>> pathsToServers = findSensors(new IsServerSearchCondition(service,IsServerSearchCondition.BENEVOLENT_SERVER));
        return ((pathsToServers != null) && (pathsToServers.size() > 0));
    }

    public Collection<Vector<Sensor>> findSensors(ISearchCondition searchCondition) {
        Collection<Vector<Sensor>> out = new LinkedList<Vector<Sensor>>();
        Collection<Sensor> Q = new ArrayList<Sensor>();
        Collection<Sensor> visitedNodes = new ArrayList<Sensor>();
        Hashtable<Sensor,Double> distanceFromSource = new Hashtable<Sensor,Double>();
        Hashtable<Sensor,Sensor> previousNode = new Hashtable<Sensor,Sensor>();

        distanceFromSource.put(this, 0.0);
        previousNode.put(this, this);
        Q.add(this);

        while (!Q.isEmpty()) {
            double minD = Double.POSITIVE_INFINITY;
            Sensor closestNode = null;
            for (Sensor sensor : Q)
                if (distanceFromSource.get(sensor) < minD) {
                    minD = distanceFromSource.get(sensor);
                    closestNode = sensor;
                }

            Q.remove(closestNode);
            visitedNodes.add(closestNode);
            for (Sensor sensor : closestNode.getNeighbors()) {
                if ((!Q.contains(sensor)) && (!visitedNodes.contains(sensor))){
                    distanceFromSource.put(sensor, Double.POSITIVE_INFINITY);
                    Q.add(sensor);
                }

                double alternative = distanceFromSource.get(closestNode) + closestNode.distance(sensor);
                if (alternative < distanceFromSource.get(sensor)) {
                    distanceFromSource.put(sensor, alternative);
                    previousNode.put(sensor, closestNode);
                }
            }
        }

        for (Sensor sensor : previousNode.keySet()) {
            if ((sensor.id() != id) && sensor.isActive() && (searchCondition.sensorAcomplishesCondition(sensor))) {
                boolean intermediateInactiveSensor = false;
                LinkedList<Sensor> path1 = new LinkedList<Sensor>();
                path1.addFirst(sensor);
                Sensor prev = sensor;
                while (prev != this){
                    prev = previousNode.get(prev);
                    if (!prev.isActive())
                        intermediateInactiveSensor = true;
                    path1.addFirst(prev);
                }
                Vector<Sensor> path2 = new Vector<Sensor>();
                for (Sensor s1 : path1)
                    path2.add(s1);

                if (!intermediateInactiveSensor)
                    out.add(path2);
            }
        }
        return out;
    }

    public synchronized Collection<Sensor> getNeighbors() {
        Collection<Sensor> neighbors = new ArrayList<Sensor>();
        for (Link link : links) {
            neighbors.add(link.get_destination());
            transmittedDistance += distance(link.get_destination());
        }
        return neighbors;
    }

    public void removeAllNeighbors() { links = new ArrayList<Link>(); }

    public boolean isNeighbor(Sensor sensor) {
        if (links != null)
            for (Link link : links) {
                if (link.get_destination().equals(sensor))
                    return true;
            }
        return false;
    }

    public void addLink(Sensor sensor) {
        if (!isNeighbor(sensor)) {
            if (links == null)
                links = new ArrayList<Link>();
            Link link = new Link(this,sensor);
            links.add(link);
        }
    }

    public void removeLink(Sensor sensor) {
        java.util.Iterator<Link> linkIt = links.iterator();
        while (linkIt.hasNext())
        {
            Link link = linkIt.next();
            if (link.get_destination().equals(sensor))
                linkIt.remove();
        }
    }

    public double distance(Sensor sensor) {
        return Math.sqrt(Math.pow(xPosition-sensor.getX(),2)+Math.pow(yPosition-sensor.getY(),2));
    }

    // --- OPRAVA: Pridaná overloading metóda pre Service ---
    public boolean offersService(Service service) {
        return offersService(service.id());
    }

    public boolean offersService(String service) {
        return (getService(service) != null);
    }

    public double get_goodness(Service service) throws Exception {
        if (!offersService(service.id())) throw new Exception("Server "+id+" doesn't offer service "+service.id());
        return servicesGoodness.get(getService(service.id())).doubleValue();
    }

    // --- OPRAVA: Odstránené new Double() ---
    public void set_goodness(Service service, double goodness) throws Exception {
        if (!offersService(service.id())) throw new Exception("Server "+id+" doesn't offer service "+service.id());
        servicesGoodness.put(service, Double.valueOf(goodness)); // Použitie valueOf namiesto new Double
    }

    public void addService(Service service, double goodness) {
        servicesGoodness.put(service, Double.valueOf(goodness));
    }

    public void removeService(Service service) {
        servicesGoodness.remove(service);
        if (requiredService != null && requiredService.equals(service))
            requiredService = null;
    }

    public void set_requiredService(Service requiredService) { this.requiredService = requiredService; }
    public Service get_requiredService() { return requiredService; }
    public int id() { return id; }
    public double getX() { return xPosition; }
    public double getY() { return yPosition; }
    public boolean isActive() { return activeState;}
    public void addTransmittedDistance(long distance) { transmittedDistance += distance; }
    public static void resetId() { idCount = 1; }
    public boolean equals(Sensor node) { return (id == node.id()); }

    public int get_numServices() { if (servicesGoodness == null) return 0; return servicesGoodness.keySet().size(); }
    public Collection<Service> get_services() { if (servicesGoodness == null) return null; return servicesGoodness.keySet(); }

    public Service getService(String service) {
        if (servicesGoodness != null) {
            java.util.Iterator<Service> serviceIt = servicesGoodness.keySet().iterator();
            while (serviceIt.hasNext()) {
                Service service1 = serviceIt.next();
                if (service1.id().equalsIgnoreCase(service)) return service1;
            }
        }
        return null;
    }

    public long get_transmittedDistance() { return transmittedDistance; }
    public Outcome get_outcome() { return outcome; }
    public void setActiveState(boolean active_state) { this.activeState = active_state; }

    public int distanceInHops(Sensor sensor) {
        int distanceInHops = Integer.MAX_VALUE;
        Collection<Vector<Sensor>> pathsToSensor = findSensors(new IsSensorSearchCondition(sensor.id()));
        if (pathsToSensor != null)
            for (Vector<Sensor> pathToSensor : pathsToSensor)
                if (pathToSensor.size() < distanceInHops)
                    distanceInHops = pathToSensor.size();
        return distanceInHops;
    }

    @Override
    public String toString() {
        String s = id+" ("+((int)(xPosition*100))/100.0+","+((int)(yPosition*100))/100.0+") ->";
        for (Link link : links) s += " "+link.get_destination().id();
        return s;
    }
}