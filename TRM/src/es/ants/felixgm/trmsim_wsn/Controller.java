package es.ants.felixgm.trmsim_wsn;

import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Service;
import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.TRMParametersPanel;
import es.ants.felixgm.trmsim_wsn.network.Sensor;
import es.ants.felixgm.trmsim_wsn.trm.TRModel_WSN;
import es.ants.felixgm.trmsim_wsn.trm.TRMParameters;
import es.ants.felixgm.trmsim_wsn.gui.SimulationResultRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Observer;
import java.util.Observable;
import java.util.Collection;
import java.util.ArrayList;
import java.util.LinkedList;

public class Controller implements Observer {
    private Network currentNetwork;
    private String parametersFile;
    private TRModel_WSN trmodel_wsn;
    private Service requiredService;
    private long delay;
    private Thread simulationThread;
    private Simulation simulation;
    private SimulationResultRepository resultRepository;

    public Controller() throws Exception {
        requiredService = new Service("My service");
        delay = 0;
        resultRepository = new SimulationResultRepository();
    }

    public SimulationResultRepository getResultRepository() {
        return resultRepository;
    }

    public Network createNewNetwork(int minNumSensors, int maxNumSensors,
                                    double probClients, double probRelay, double probMalicious,
                                    double radioRange, boolean dynamic, boolean oscillating,
                                    boolean collusion) {
        int numSensors = (int)(minNumSensors + Math.random()*Math.abs(maxNumSensors-minNumSensors));
        ArrayList<Double> probServices = new ArrayList<Double>();
        ArrayList<Double> probGoodness = new ArrayList<Double>();
        ArrayList<Service> services = new ArrayList<Service>();

        services.add(new Service("Relay"));
        services.add(requiredService);

        probServices.add(1.0);
        probGoodness.add(1.0);
        probServices.add(1.0-probRelay);
        probGoodness.add(1.0-probMalicious);

        currentNetwork = trmodel_wsn.generateRandomNetwork(numSensors, probClients, radioRange, probServices, probGoodness, services);

        currentNetwork.set_collusion(collusion);
        currentNetwork.set_dynamic(dynamic);
        currentNetwork.set_TRModel(trmodel_wsn);

        return currentNetwork;
    }

    public Network setNewNeighborsNetwork(double newRange) {
        if (currentNetwork != null) {
            currentNetwork.setNewNeighbors(newRange);
            return currentNetwork;
        }
        return null;
    }

    public void resetCurrentNetwork() {
        if (currentNetwork != null)
            currentNetwork.reset();
    }

    public void update(Observable observable, Object arg) {
        if (arg instanceof Network)
            currentNetwork = (Network) arg;
    }

    public void runSimulations(Observer observer, int minNumSensors, int maxNumSensors,
                               double probClients, double probRelay, double probMalicious,
                               double radioRange, boolean dynamic, boolean oscillating,
                               boolean collusion, int numNetworks, int numExecutions) {

        if (currentNetwork != null) currentNetwork.set_runningSimulation(true);

        Collection<Observer> observers = new LinkedList<Observer>();
        observers.add(observer);
        observers.add(this);

        // OPRAVA: Pridaný parameter 'this' (Controller) na prvé miesto
        simulation = new Simulation(this, observers, requiredService, minNumSensors, maxNumSensors,
                probClients, probRelay, probMalicious, radioRange,
                dynamic, oscillating, collusion,
                numNetworks, numExecutions);

        simulationThread = new Thread(simulation);
        simulationThread.start();
    }

    public void stopSimulations() {
        if (simulationThread != null)
            simulation.stop();
        if (currentNetwork != null) currentNetwork.set_runningSimulation(false);
    }

    public void runTRM_WSN(Observer observer, boolean dynamic, boolean oscillating,
                           boolean collusion, int numExecutions) {
        Collection<Observer> observers = new LinkedList<Observer>();
        observers.add(observer);
        observers.add(this);

        if (currentNetwork != null) {
            currentNetwork.set_runningSimulation(true);
            currentNetwork.set_dynamic(dynamic);
            currentNetwork.set_collusion(collusion);
        }

        // OPRAVA: Pridaný parameter 'this' (Controller) na prvé miesto
        simulation = new Simulation(this, observers, requiredService,
                dynamic, oscillating, collusion,
                numExecutions, currentNetwork);

        simulationThread = new Thread(simulation);
        simulationThread.start();
    }

    public void saveCurrentNetwork(String fileName) throws Exception {
        currentNetwork.writeToXMLFile(fileName);
    }

    public Network loadCurrentNetwork(String fileName) throws Exception {
        currentNetwork = trmodel_wsn.loadCurrentNetwork(fileName);
        currentNetwork.set_TRModel(trmodel_wsn);
        return currentNetwork;
    }

    public void sleep() {
        try { Thread.sleep(delay); } catch (Exception ex) { ex.printStackTrace(); }
    }

    public Network get_currentNetwork() { return currentNetwork; }
    public Service get_requiredService() { return requiredService; }
    public String get_parametersFile() { return parametersFile; }
    public TRMParameters get_TRMParameters() { return trmodel_wsn.get_TRMParameters(); }

    public boolean isSimulationRunning() {
        return (currentNetwork != null && simulationThread != null && simulationThread.isAlive());
    }

    public void set_TRMParameters(TRMParametersPanel trmParametersPanel) {
        trmodel_wsn.set_TRMParameters(trmParametersPanel.get_TRMParameters());
        if (currentNetwork != null) currentNetwork.set_TRModel(trmodel_wsn);
    }

    public TRMParameters set_TRMParameters(String parametersFile) throws Exception {
        try {
            Class[] set_TRMParametersParametersTypes = {Class.forName("java.lang.String")};
            Object[] set_TRMParametersParametersValues = {parametersFile};
            trmodel_wsn.set_TRMParameters((TRMParameters)Class.forName(trmodel_wsn.getClass().getName()+"_Parameters").getConstructor(set_TRMParametersParametersTypes).newInstance(set_TRMParametersParametersValues));
        } catch (Exception ex) {
            trmodel_wsn.set_TRMParameters((TRMParameters) Class.forName(trmodel_wsn.getClass().getName()+"_Parameters").newInstance());
        }
        if (currentNetwork != null) currentNetwork.set_TRModel(trmodel_wsn);
        return trmodel_wsn.get_TRMParameters();
    }

    public void set_TRModel_WSN(String trmodel_wsn) throws Exception {
        TRMParameters trm_parameters = null;
        String packageName = "es.ants.felixgm.trmsim_wsn.trm."+trmodel_wsn.toLowerCase()+".";

        parametersFile = (String)Class.forName(packageName+trmodel_wsn+"_Parameters").getDeclaredField("defaultParametersFileName").get(null);
        try {
            Class[] trmParametersConstructorParametersTypes = {Class.forName("java.lang.String")};
            Object[] trmParametersConstructorParametersValues = {parametersFile};
            trm_parameters = (TRMParameters) Class.forName(packageName+trmodel_wsn+"_Parameters").getConstructor(trmParametersConstructorParametersTypes).newInstance(trmParametersConstructorParametersValues);
        } catch (java.lang.Exception ex) {
            trm_parameters = (TRMParameters) Class.forName(packageName+trmodel_wsn+"_Parameters").newInstance();
        }
        Class[] trmodel_wsnConstructorParametersTypes = {Class.forName(packageName+trmodel_wsn+"_Parameters")};
        Object[] trmodel_wsnConstructorParametersValues = {trm_parameters};
        this.trmodel_wsn = (TRModel_WSN) Class.forName(packageName+trmodel_wsn).getConstructor(trmodel_wsnConstructorParametersTypes).newInstance(trmodel_wsnConstructorParametersValues);

        if (currentNetwork != null) currentNetwork.set_TRModel(this.trmodel_wsn);
    }

    public void saveParametersFileContent(String filePath, String newContent) throws Exception {
        FileWriter fileWriter = new FileWriter(filePath);
        fileWriter.write(newContent);
        fileWriter.flush();
        fileWriter.close();
    }

    public String get_ParametersFileContent() throws Exception {
        String defaultParametersFileContent = "";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(parametersFile));
        String newLine = null;
        while ((newLine = bufferedReader.readLine()) != null)
            defaultParametersFileContent += newLine+"\n";
        bufferedReader.close();
        return defaultParametersFileContent;
    }

    public String get_DefaultParametersFileContent(String trmodel_wsn) throws Exception {
        String defaultParametersFileContent = "";
        String packageName = "es.ants.felixgm.trmsim_wsn.trm."+trmodel_wsn.toLowerCase()+".";
        try {
            String defaultParametersFilePath = (String)Class.forName(packageName+trmodel_wsn+"_Parameters").getDeclaredField("defaultParametersFileName").get(null);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream(defaultParametersFilePath)));
            String newLine = null;
            while ((newLine = bufferedReader.readLine()) != null)
                defaultParametersFileContent += newLine+"\n";
            bufferedReader.close();
        } catch (Exception ex) {
            TRMParameters trm_parameters = (TRMParameters) Class.forName(packageName+trmodel_wsn+"_Parameters").newInstance();
            defaultParametersFileContent = trm_parameters.toString();
        }
        return defaultParametersFileContent;
    }

    public void set_delay(long delay) { this.delay = delay; }
    public void set_parametersFile(String parametersFile) { this.parametersFile = parametersFile; }

    public Sensor getSensorAtCoordinate(double x, double y) {
        if (currentNetwork == null) return null;
        int error = 2;
        for (Sensor sensor : currentNetwork.get_sensors()) {
            if (((x <= (sensor.getX()+error)) && (x >= (sensor.getX()-error))) && ((y <= (sensor.getY()+error)) && (y >= (sensor.getY()-error))))
                return sensor;
        }
        return null;
    }

    public Sensor getSensor(int id) {
        if (currentNetwork == null) return null;
        return currentNetwork.getSensor(id);
    }
}