package es.ants.felixgm.trmsim_wsn.network;

import es.ants.felixgm.trmsim_wsn.search.ISearchCondition;
import es.ants.felixgm.trmsim_wsn.search.IsClientSearchCondition;
import es.ants.felixgm.trmsim_wsn.search.IsServerSearchCondition;
import es.ants.felixgm.trmsim_wsn.trm.TRModel_WSN;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

public abstract class Network {
    protected static final double maxDistance = 100.0;
    protected Collection<Sensor> clients;
    protected Collection<Sensor> servers;
    protected Collection<Sensor> sensors;
    protected Collection<Service> services;

    protected TRModel_WSN currentModel;
    protected boolean collusion = false;
    protected boolean dynamic = false;
    protected boolean runningSimulation = false;

    public Network(Collection<Sensor> clients, Collection<Sensor> servers, Collection<Service> services) {
        this.clients = clients;
        this.servers = servers;
        sensors = new ArrayList<Sensor>();
        if (clients != null) for (Sensor client : clients) { initSensor(client); sensors.add(client); }
        if (servers != null) for (Sensor server : servers) { initSensor(server); sensors.add(server); }
        this.services = services;
    }

    public Network(int numSensors, double probClients, double rangeFactor,
                   Collection<Double> probServices, Collection<Double> probGoodness, Collection<Service> services) {
        clients = new ArrayList<Sensor>();
        servers = new ArrayList<Sensor>();
        sensors = new ArrayList<Sensor>();
        this.services = new ArrayList<Service>();

        Sensor.resetId();

        for (int i = 0; i < numSensors; i++) {
            if (Math.random() <= probClients) {
                Sensor client = newSensor();
                initSensor(client);
                clients.add(client);
                sensors.add(client);
            } else {
                Sensor server = newSensor();
                initSensor(server);
                servers.add(server);
                sensors.add(server);

                Iterator<Double> itProbServices = probServices.iterator();
                Iterator<Double> itProbGoodness = probGoodness.iterator();
                for (Service service : services)
                    if (Math.random() <= itProbServices.next().doubleValue()) {
                        if (Math.random() <= itProbGoodness.next().doubleValue())
                            server.addService(service, 1.0);
                        else
                            server.addService(service, 0.0);

                        if (!this.services.contains(service))
                            this.services.add(service);
                    }
            }
        }

        if (clients.size() == 0) {
            Sensor client = newSensor();
            initSensor(client);
            clients.add(client);
            sensors.add(client);
        }

        double rangeThreshold = rangeFactor*Math.sqrt(2.0)*maxDistance;
        for (Sensor server : servers) {
            for (Sensor client : clients)
                if (server.distance(client) < rangeThreshold) {
                    client.addLink(server);
                    server.addLink(client);
                }
            for (Sensor server2 : servers)
                if ((!server.equals(server2)) && (server.distance(server2) < rangeThreshold)) {
                    server.addLink(server2);
                    server2.addLink(server);
                }
        }
        for (Sensor client1 : clients) {
            for (Sensor client2 : clients)
                if ((!client1.equals(client2)) && (client1.distance(client2) < rangeThreshold)) {
                    client1.addLink(client2);
                    client2.addLink(client1);
                }
            for (Sensor server : servers)
                if (server.distance(client1) < rangeThreshold) {
                    client1.addLink(server);
                    server.addLink(client1);
                }
        }
    }

    public Network(String xmlFilePath) throws Exception {
        if (!xmlFilePath.endsWith(".xml"))
            throw new Exception("Only XML files are accepted");
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(new File(xmlFilePath));
        doc.getDocumentElement().normalize();
        org.w3c.dom.Node root = doc.getDocumentElement();
        NodeList nodes = root.getChildNodes();

        clients = new ArrayList<Sensor>();
        servers = new ArrayList<Sensor>();
        sensors = new ArrayList<Sensor>();
        services = new ArrayList<Service>();
        Sensor.resetId();

        for (int i = 1; i < nodes.getLength(); i++) {
            org.w3c.dom.Node node = nodes.item(i);
            if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                NamedNodeMap attributes = node.getAttributes();
                int id = Integer.parseInt(attributes.getNamedItem("id").getNodeValue());
                double x = Double.parseDouble(attributes.getNamedItem("x").getNodeValue());
                double y = Double.parseDouble(attributes.getNamedItem("y").getNodeValue());
                if (node.getNodeName().equals("client")) {
                    Sensor client = newSensor(id, x, y);
                    initSensor(client);
                    if (!clients.contains(client)) {
                        clients.add(client);
                        sensors.add(client);
                    }
                } else if ((node.getNodeName().equals("server")) && (attributes != null)) {
                    Sensor server = newSensor(id,x,y);
                    initSensor(server);
                    if (!servers.contains(server)) {
                        servers.add(server);
                        sensors.add(server);
                    }
                    NodeList nodeChildren = node.getChildNodes();
                    for (int j = 0; j < nodeChildren.getLength(); j++)
                        if (nodeChildren.item(j).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                            if (nodeChildren.item(j).getNodeName().equals("service")) {
                                double goodness = Double.parseDouble(nodeChildren.item(j).getAttributes().getNamedItem("goodness").getNodeValue());
                                String idService = nodeChildren.item(j).getAttributes().getNamedItem("id").getNodeValue();
                                Service service = new Service(idService);
                                if (!services.contains(service)) services.add(service);
                                server.addService(service, goodness);
                            }
                        }
                }
            }
        }

        // Druhý prechod pre linky
        for (int i = 1; i < nodes.getLength(); i++) {
            org.w3c.dom.Node node = nodes.item(i);
            if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                NamedNodeMap attributes = node.getAttributes();
                if ((node.getNodeName().equals("client")) && (attributes != null)) {
                    int id = Integer.parseInt(attributes.getNamedItem("id").getNodeValue());
                    Sensor client = getSensor(id);
                    NodeList clientChildren = node.getChildNodes();
                    for (int j = 0; j < clientChildren.getLength(); j++)
                        if (clientChildren.item(j).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE)
                            if (clientChildren.item(j).getNodeName().equals("server")) {
                                int idServer = Integer.parseInt(clientChildren.item(j).getAttributes().getNamedItem("id").getNodeValue());
                                Sensor server = getSensor(idServer);
                                if (client != null && server != null) client.addLink(server);
                            } else if (clientChildren.item(j).getNodeName().equals("client")) {
                                int idClient = Integer.parseInt(clientChildren.item(j).getAttributes().getNamedItem("id").getNodeValue());
                                Sensor client_aux = getSensor(idClient);
                                if (client != null && client_aux != null) client.addLink(client_aux);
                            }
                } else if ((node.getNodeName().equals("server")) && (attributes != null)) {
                    int id = Integer.parseInt(attributes.getNamedItem("id").getNodeValue());
                    Sensor server = getSensor(id);
                    NodeList nodeChildren = node.getChildNodes();
                    for (int j = 0; j < nodeChildren.getLength(); j++)
                        if (nodeChildren.item(j).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                            if (nodeChildren.item(j).getNodeName().equals("server")) {
                                int idServer = Integer.parseInt(nodeChildren.item(j).getAttributes().getNamedItem("id").getNodeValue());
                                Sensor neighbour = getSensor(idServer);
                                if (server != null && neighbour != null) server.addLink(neighbour);
                            } else if (nodeChildren.item(j).getNodeName().equals("client")) {
                                int idClient = Integer.parseInt(nodeChildren.item(j).getAttributes().getNamedItem("id").getNodeValue());
                                Sensor client = getSensor(idClient);
                                if (server != null && client != null) server.addLink(client);
                            }
                        }
                }
            }
        }
    }

    protected void initSensor(Sensor s) {
        s.setMaxDistance(maxDistance);
        s.setDynamic(this.dynamic);
        s.setCollusion(this.collusion);
        s.setRunningSimulation(this.runningSimulation);
        if (this.currentModel != null) {
            s.setTRModel(this.currentModel);
        }
    }

    public void set_TRModel(TRModel_WSN model) {
        this.currentModel = model;
        for (Sensor s : sensors) s.setTRModel(model);
    }

    public void set_collusion(boolean collusion) {
        this.collusion = collusion;
        for (Sensor s : sensors) s.setCollusion(collusion);
    }

    public void set_dynamic(boolean dynamic) {
        this.dynamic = dynamic;
        for (Sensor s : sensors) s.setDynamic(dynamic);
    }

    public void set_runningSimulation(boolean running) {
        this.runningSimulation = running;
        for (Sensor s : sensors) s.setRunningSimulation(running);
    }

    public abstract Sensor newSensor();
    public abstract Sensor newSensor(int id, double x, double y);

    public void oscillate(Service service) {
        try {
            int numBenevolentServers = 0;
            for (Sensor server : servers)
                // Tu už to prejde, lebo Sensor má metódu offersService(Service)
                if ((server.offersService(service)) && (server.get_goodness(service) >= 0.5)) {
                    numBenevolentServers++;
                    server.set_goodness(service,0.0);
                }

            double prob = ((double)numBenevolentServers/servers.size());
            while (numBenevolentServers > 0)
                for (Sensor server : servers)
                    if ((Math.random() < prob) && (server.offersService(service)) && (server.get_goodness(service)< 0.5)) {
                        server.set_goodness(service,1.0);
                        numBenevolentServers--;
                        if (numBenevolentServers == 0)
                            break;
                    }
        } catch(Exception ex){ ex.printStackTrace(); }
    }

    public void reset() {
        for (Sensor sensor : sensors)
            sensor.reset();
    }

    public void setNewNeighbors(double newRange) {
        double rangeThreshold = newRange*Math.sqrt(2.0)*maxDistance;
        for (Sensor sensor : sensors) {
            sensor.removeAllNeighbors();
            for (Sensor sensor2 : sensors)
                if ((!sensor.equals(sensor2)) && (sensor.distance(sensor2) < rangeThreshold))
                {
                    sensor.addLink(sensor2);
                    sensor2.addLink(sensor);
                }
        }
    }

    public void writeToXMLFile(String fileName) throws Exception {
        try {
            if (!fileName.endsWith(".xml")) fileName += ".xml";
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            out.write("<wsn>\n");
            for (Sensor client : clients) {
                out.write("\t<client id=\""+client.id()+"\" x=\""+(((int)(client.getX()*100))/100.0)+"\" y=\""+(((int)(client.getY()*100))/100.0)+"\">\n");
                for (Sensor neighbor : client.getNeighbors())
                    if (neighbor.get_numServices() == 0) out.write("\t\t<client id=\""+neighbor.id()+"\"/>\n");
                    else out.write("\t\t<server id=\""+neighbor.id()+"\"/>\n");
                out.write("\t</client>\n");
            }
            for (Sensor server : servers) {
                out.write("\t<server id=\""+server.id()+"\" x=\""+(((int)(server.getX()*100))/100.0)+"\" y=\""+(((int)(server.getY()*100))/100.0)+"\">\n");
                for (Service service : server.get_services())
                    out.write("\t\t<service id=\""+service.id()+"\" goodness=\""+server.get_goodness(service)+"\"/>\n");
                for (Sensor neighbor : server.getNeighbors())
                    if (neighbor.get_numServices() == 0) out.write("\t\t<client id=\""+neighbor.id()+"\"/>\n");
                    else out.write("\t\t<server id=\""+neighbor.id()+"\"/>\n");
                out.write("\t</server>\n");
            }
            out.write("</wsn>\n");
            out.flush();
            out.close();
        } catch (Exception ex) { throw new Exception("writeToXMLFile: "+ex); }
    }

    @Override
    public String toString() {
        String s = "";
        for (Sensor client : clients) s += "C "+client+"\n";
        for (Sensor server : servers) s += "S "+server+"\n";
        return s;
    }

    public long get_sensorsTransmittedDistance(ISearchCondition searchCondition, Service requiredService) {
        long sensorsTransmittedDistance = 0;
        long numSensors = 0;
        for (Sensor sensor : sensors)
            if (searchCondition.sensorAcomplishesCondition(sensor) && reachesQualifiedService(sensor,requiredService)) {
                sensorsTransmittedDistance += sensor.get_transmittedDistance();
                numSensors++;
            }
        if (numSensors != 0) return sensorsTransmittedDistance/numSensors;
        return 0;
    }

    protected boolean reachesQualifiedService(Sensor sensor, Service requiredService) {
        boolean reachableClient = false;
        boolean reachableBenevolentServer = false;
        if (sensor.isActive())
            try {
                reachableClient = (sensor.get_numServices() == 0);
                reachableBenevolentServer = ((sensor.get_numServices() > 1) && (sensor.get_goodness(requiredService) > 0.5));
                if (!reachableClient) {
                    Collection<Vector<Sensor>> pathsToClients = sensor.findSensors(new IsClientSearchCondition());
                    reachableClient = ((pathsToClients != null) && (pathsToClients.size() > 0));
                }
                if (!reachableBenevolentServer) {
                    Collection<Vector<Sensor>> pathsToServers = sensor.findSensors(new IsServerSearchCondition(requiredService,IsServerSearchCondition.BENEVOLENT_SERVER));
                    reachableBenevolentServer = ((pathsToServers != null) && (pathsToServers.size() > 0));
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        return (reachableClient && reachableBenevolentServer);
    }

    public Sensor getSensor(int id) {
        if ((sensors == null) || (sensors.isEmpty())) return null;
        for (Sensor sensor : sensors) if (sensor.id() == id) return sensor;
        return null;
    }

    public Collection<Sensor> get_clients() { return clients; }
    public Collection<Sensor> get_servers() { return servers; }
    public Collection<Sensor> get_sensors() { return sensors; }
    public Collection<Service> get_services() { return services; }
    public int get_numSensors() { return sensors.size(); }
    public int get_numClients() { return clients.size(); }
    public int get_numServers() { return servers.size(); }
    public static double get_maxDistance() { return maxDistance; }
}