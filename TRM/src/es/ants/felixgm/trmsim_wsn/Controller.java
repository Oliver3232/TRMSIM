/**
 *  "TRMSim-WSN, Trust and Reputation Models Simulator for Wireless 
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
 * 
 * 
 * Additional Terms of this License
 * --------------------------------
 * 
 * 1. It is Required the preservation of specified reasonable legal notices
 *   and author attributions in that material and in the Appropriate Legal
 *   Notices displayed by works containing it.
 * 
 * 2. It is limited the use for publicity purposes of names of licensors or
 *   authors of the material.
 * 
 * 3. It is Required indemnification of licensors and authors of that material
 *   by anyone who conveys the material (or modified versions of it) with
 *   contractual assumptions of liability to the recipient, for any liability
 *   that these contractual assumptions directly impose on those licensors
 *   and authors.
 * 
 * 4. It is Prohibited misrepresentation of the origin of that material, and it is
 *   required that modified versions of such material be marked in reasonable
 *   ways as different from the original version.
 * 
 * 5. It is Declined to grant rights under trademark law for use of some trade
 *   names, trademarks, or service marks.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program (lgpl.txt).  If not, see <http://www.gnu.org/licenses/>
*/

package es.ants.felixgm.trmsim_wsn;

import es.ants.felixgm.trmsim_wsn.app.BatchSimulationConfig;
import es.ants.felixgm.trmsim_wsn.app.NetworkGenerationConfig;
import es.ants.felixgm.trmsim_wsn.app.SimulationConfig;
import es.ants.felixgm.trmsim_wsn.app.support.ControllerNetworkGenerationSupport;
import es.ants.felixgm.trmsim_wsn.app.support.ControllerParametersIO;
import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Service;

import es.ants.felixgm.trmsim_wsn.gui.parameterpanels.TRMParametersPanel;

import es.ants.felixgm.trmsim_wsn.network.Sensor;
import es.ants.felixgm.trmsim_wsn.trm.TRModel_WSN;
import es.ants.felixgm.trmsim_wsn.trm.TRMParameters;
import es.ants.felixgm.trmsim_wsn.trm.TrustModelBundle;
import es.ants.felixgm.trmsim_wsn.trm.TrustModelFactory;
import es.ants.felixgm.trmsim_wsn.trm.TrustModelRegistry;

import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * <p>This class acts as a controller between classes representing the GUI, and classes
 * belonging to the model, as in the MVC pattern</p>
 * @author <a href="http://ants.dif.um.es/~felixgm/en" target="_blank">F&eacute;lix G&oacute;mez M&aacute;rmol</a>, <a href="http://webs.um.es/gregorio" target="_blank">Gregorio Mart&iacute;nez P&eacute;rez</a>
 * @version 0.5
 */
public class Controller {
    /** Singleton instance of Controller */
    private static Controller controlador = null;
    /** Service requested by every client in the network */
    private Service requiredService;
    /** Delay used to allow a better visualization of some simulationThread */
    private long delay;
    /** Executor controlling current simulation lifecycle */
    private final ExecutorService simulationExecutor;
    /** Per-slot runtime workspaces */
    private final Map<SimulationSlot, SimulationWorkspaceState> workspaces;
    
    /**
     * Returns the current instance of this controller
     * @return Current instance of this controller
     * @throws Exception If there is any problem loading default Trust and Reputation Model parameters
     */
    public static Controller C() throws Exception {
        if (controlador == null)
            controlador = new Controller();
        return controlador;
    }

    public static Controller createIndependent() throws Exception {
        return new Controller();
    }
    
    /**
     * Creates a new instance of Controller
     * @throws Exception If there is any problem loading default Trust and Reputation Model parameters
     */
    private Controller() throws Exception {
        requiredService = new Service("My service");
        delay = 0;
        simulationExecutor = Executors.newFixedThreadPool(SimulationSlot.values().length);
        workspaces = new EnumMap<SimulationSlot, SimulationWorkspaceState>(SimulationSlot.class);
        workspaces.put(SimulationSlot.PRIMARY,
                new SimulationWorkspaceState(SimulationSlot.PRIMARY, Sensor.getDefaultSimulationContext()));
        workspaces.put(SimulationSlot.SECONDARY,
                new SimulationWorkspaceState(SimulationSlot.SECONDARY, new SimulationContext()));
    }

    private SimulationWorkspaceState workspace(SimulationSlot slot) {
        return workspaces.get(slot);
    }

    private SimulationWorkspaceState primaryWorkspace() {
        return workspace(SimulationSlot.PRIMARY);
    }

    private void submitSimulation(SimulationSlot slot, Simulation nextSimulation) {
        SimulationWorkspaceState workspaceState = workspace(slot);
        workspaceState.setSimulation(nextSimulation);
        workspaceState.setSimulationFuture(simulationExecutor.submit(nextSimulation));
    }

    private Collection<SimulationListener> buildSimulationListeners(SimulationListener listener) {
        return buildSimulationListeners(SimulationSlot.PRIMARY, listener);
    }

    private Collection<SimulationListener> buildSimulationListeners(SimulationSlot slot, SimulationListener listener) {
        Collection<SimulationListener> listeners = new LinkedList<SimulationListener>();
        listeners.add(listener);
        listeners.add(createControllerListener(slot));
        return listeners;
    }

    private void updateCurrentTrustModel(TRModel_WSN trustModel, String defaultParametersFile) {
        updateCurrentTrustModel(SimulationSlot.PRIMARY, trustModel, defaultParametersFile);
    }

    private void updateCurrentTrustModel(SimulationSlot slot, TRModel_WSN trustModel, String defaultParametersFile) {
        SimulationWorkspaceState workspaceState = workspace(slot);
        workspaceState.setParametersFile(defaultParametersFile);
        workspaceState.setTrustModel(trustModel);
        Sensor.activateSimulationContext(workspaceState.getSimulationContext());
        try {
            Sensor.set_TRModel_WSN(trustModel);
        } finally {
            Sensor.clearActiveSimulationContext();
        }
    }

    private boolean hasCurrentNetwork() {
        return hasCurrentNetwork(SimulationSlot.PRIMARY);
    }

    private boolean hasCurrentNetwork(SimulationSlot slot) {
        return workspace(slot).getCurrentNetwork() != null;
    }
    
    /**
     * This method creates a new wireless sensor network using the specified parameters
     * @param minNumSensors Minimum number of sensors composing the WSN
     * @param maxNumSensors Maximum number of sensors composing the WSN
     * @param probClients The probability of a sensor to act as a client
     * @param probRelay The probability of a server to act just as a relay sensor (not offering the required service)
     * @param probMalicious The probability of a server offering the required service to act as a 
     * malicious server (not providing the offered service, or providong a worse or different one).
     * @param radioRange Maximum wireless range of every sensor. It determines the neighborhood of every sensor
     * @param dynamic It determines if the WSN will be dynamic (nodes sometimes switch off in order to save battery, breaking all their links)
     * @param oscillating It determines if the goodness of the servers belonging to the created WSN will change along the time
     * @param collusion It determines if the malicious servers belonging to the created WSN will form a collusion among them
     * @return The generated random Wireless Sensor Network
     */
    public Network createNewNetwork(int minNumSensors, int maxNumSensors,
                                double probClients, double probRelay, double probMalicious,
                                double radioRange, boolean dynamic, boolean oscillating, 
                                boolean collusion) {
        return createNewNetwork(SimulationSlot.PRIMARY, minNumSensors, maxNumSensors, probClients, probRelay,
                probMalicious, radioRange, dynamic, oscillating, collusion);
    }

    public Network createNewNetwork(SimulationSlot slot, int minNumSensors, int maxNumSensors,
                                double probClients, double probRelay, double probMalicious,
                                double radioRange, boolean dynamic, boolean oscillating,
                                boolean collusion) {
        SimulationWorkspaceState workspaceState = workspace(slot);
        workspaceState.getSimulationContext().setDynamic(dynamic);
        workspaceState.getSimulationContext().setCollusion(collusion);
        Network currentNetwork;
        Sensor.activateSimulationContext(workspaceState.getSimulationContext());
        try {
            Sensor.set_TRModel_WSN(workspaceState.getTrustModel());
            currentNetwork = ControllerNetworkGenerationSupport.createRandomNetwork(
                    workspaceState.getTrustModel(),
                    requiredService,
                    minNumSensors,
                    maxNumSensors,
                    probClients,
                    probRelay,
                    probMalicious,
                    radioRange,
                    dynamic,
                    collusion);
        } finally {
            Sensor.clearActiveSimulationContext();
        }
        workspaceState.setCurrentNetwork(currentNetwork);
        return currentNetwork;
    }

    /**
     * Creates a new wireless sensor network using a typed configuration object.
     * @param config Network generation configuration
     * @return The generated random Wireless Sensor Network
     */
    public Network createNewNetwork(NetworkGenerationConfig config) {
        return createNewNetwork(
                SimulationSlot.PRIMARY,
                config);
    }

    public Network createNewNetwork(SimulationSlot slot, NetworkGenerationConfig config) {
        return createNewNetwork(
                slot,
                config.getMinNumSensors(),
                config.getMaxNumSensors(),
                config.getProbClients(),
                config.getProbRelay(),
                config.getProbMalicious(),
                config.getRadioRange(),
                config.isDynamic(),
                config.isOscillating(),
                config.isCollusion());
    }
    
    /**
     * This method recalculates the neighborhood of every sensor composing the current network given a new range
     * @param newRange New wireless range applied to every sensor
     * @return The new current WSN
     */
    public Network setNewNeighborsNetwork(double newRange) {
        return setNewNeighborsNetwork(SimulationSlot.PRIMARY, newRange);
    }

    public Network setNewNeighborsNetwork(SimulationSlot slot, double newRange) {
        if (hasCurrentNetwork(slot)) {
            Network currentNetwork = workspace(slot).getCurrentNetwork();
            currentNetwork.setNewNeighbors(newRange);
            return currentNetwork;
        }
        return null;
    }

    /**
     * This method resets the current network to its initial state
     */
    public void resetCurrentNetwork() {
        resetCurrentNetwork(SimulationSlot.PRIMARY);
    }

    public void resetCurrentNetwork(SimulationSlot slot) {
        if (hasCurrentNetwork(slot)) {
            SimulationWorkspaceState workspaceState = workspace(slot);
            Sensor.activateSimulationContext(workspaceState.getSimulationContext());
            try {
                Sensor.set_TRModel_WSN(workspaceState.getTrustModel());
                workspaceState.getCurrentNetwork().reset();
            } finally {
                Sensor.clearActiveSimulationContext();
            }
        }
    }

    /**
     * This method is used to communicate the controller with the GUI
     * @param observable
     * @param arg
     */
    private SimulationListener createControllerListener(SimulationSlot slot) {
        return new SimulationListener() {
            public void onNetworkUpdated(Network network) {
                workspace(slot).setCurrentNetwork(network);
            }

            public void onOutcomesUpdated(Collection<es.ants.felixgm.trmsim_wsn.outcomes.Outcome> outcomes) {
            }

            public void onMessage(String message) {
            }

            public void onError(Exception exception) {
            }
        };
    }
    
    /**
     * This method runs a set of simulations over a number of random WSN, showing its outcomes
     * @param observer Used to communicate changes to the GUI
     * @param minNumSensors Minimum number of sensors composing every WSN
     * @param maxNumSensors Maximum number of sensors composing every WSN
     * @param probClients The probability of a node to act as a client
     * @param probRelay The probability of a server to act just as a relay node (not offering the required service)
     * @param probMalicious The probability of a server offering the required service to act as a 
     * malicious server (not providing the offered service, or providong a worse or different one).
     * @param radioRange Maximum wireless range of every sensor. It determines the neighborhood of every sensor
     * @param dynamic It determines if the WSN will be dynamic (nodes sometimes switch off in order to save battery, breaking all their links)
     * @param oscillating It determines if the goodness of the servers belonging to the created WSN will change along the time
     * @param collusion It determines if the malicious servers belonging to the created WSN will form a collusion among them
     * @param numNetworks Number of wireless sensor networks to test
     * @param numExecutions Number of service requests of every client composing each WSN
     */
    public void runSimulations(SimulationListener listener, int minNumSensors, int maxNumSensors,
                                double probClients, double probRelay, double probMalicious,
                                double radioRange, boolean dynamic, boolean oscillating, 
                                boolean collusion, int numNetworks, int numExecutions) {
        runSimulations(
                SimulationSlot.PRIMARY,
                listener,
                minNumSensors,
                maxNumSensors,
                probClients,
                probRelay,
                probMalicious,
                radioRange,
                dynamic,
                oscillating,
                collusion,
                numNetworks,
                numExecutions);
    }

    public void runSimulations(SimulationSlot slot, SimulationListener listener, int minNumSensors, int maxNumSensors,
                                double probClients, double probRelay, double probMalicious,
                                double radioRange, boolean dynamic, boolean oscillating,
                                boolean collusion, int numNetworks, int numExecutions) {
        SimulationWorkspaceState workspaceState = workspace(slot);
        Simulation simulation = new Simulation(this, slot, workspaceState.getSimulationContext(),
                buildSimulationListeners(slot, listener), requiredService, minNumSensors,maxNumSensors,
                        probClients,probRelay,probMalicious,radioRange,
                        dynamic, oscillating, collusion, 
                        numNetworks,numExecutions);
        submitSimulation(slot, simulation);
    }

    /**
     * Runs a batch of simulations using a typed configuration object.
     * @param observer Used to communicate changes to the GUI
     * @param config Batch configuration
     */
    public void runSimulations(SimulationListener listener, BatchSimulationConfig config) {
        runSimulations(SimulationSlot.PRIMARY, listener, config);
    }

    public void runSimulations(SimulationSlot slot, SimulationListener listener, BatchSimulationConfig config) {
        NetworkGenerationConfig networkConfig = config.getNetworkGenerationConfig();
        runSimulations(
                slot,
                listener,
                networkConfig.getMinNumSensors(),
                networkConfig.getMaxNumSensors(),
                networkConfig.getProbClients(),
                networkConfig.getProbRelay(),
                networkConfig.getProbMalicious(),
                networkConfig.getRadioRange(),
                networkConfig.isDynamic(),
                networkConfig.isOscillating(),
                networkConfig.isCollusion(),
                config.getNumNetworks(),
                config.getNumExecutions());
    }
    
    /**
     * This method stops the current simulation process
     */
    public void stopSimulations() {
        stopSimulations(SimulationSlot.PRIMARY);
    }

    public void stopAllSimulations() {
        for (SimulationSlot slot : SimulationSlot.values()) {
            stopSimulations(slot);
        }
    }

    public void stopSimulations(SimulationSlot slot) {
        SimulationWorkspaceState workspaceState = workspace(slot);
        if (workspaceState.getSimulationFuture() != null && workspaceState.getSimulation() != null)
            workspaceState.getSimulation().stop();
    }

    /**
     * Pauses the current simulation process
     */
    public void pauseSimulation() {
        pauseSimulation(SimulationSlot.PRIMARY);
    }

    public void pauseAllSimulations() {
        for (SimulationSlot slot : SimulationSlot.values()) {
            pauseSimulation(slot);
        }
    }

    public void pauseSimulation(SimulationSlot slot) {
        Simulation simulation = workspace(slot).getSimulation();
        if (simulation != null)
            simulation.pause();
    }

    /**
     * Resumes the current simulation process
     */
    public void resumeSimulation() {
        resumeSimulation(SimulationSlot.PRIMARY);
    }

    public void resumeAllSimulations() {
        for (SimulationSlot slot : SimulationSlot.values()) {
            resumeSimulation(slot);
        }
    }

    public void resumeSimulation(SimulationSlot slot) {
        Simulation simulation = workspace(slot).getSimulation();
        if (simulation != null)
            simulation.resume();
    }
    
    /**
     * This method runs a set of simulations over current WSN, showing its outcomes
     * @param observer Used to communicate changes to the GUI
     * @param dynamic It determines if the WSN will be dynamic (nodes sometimes switch off in order to save battery, breaking all their links)
     * @param oscillating It determines if the goodness of the servers belonging to the created WSN will change along the time
     * @param collusion It determines if the malicious servers belonging to the created WSN will form a collusion among them
     * @param numExecutions Number of service requests of every client composing current WSN
     */
    public void runTRM_WSN(SimulationListener listener, boolean dynamic, boolean oscillating, 
                            boolean collusion, int numExecutions) {
        runTRM_WSN(SimulationSlot.PRIMARY, listener, dynamic, oscillating, collusion, numExecutions);
    }

    public void runTRM_WSN(SimulationSlot slot, SimulationListener listener, boolean dynamic, boolean oscillating,
                            boolean collusion, int numExecutions) {
        SimulationWorkspaceState workspaceState = workspace(slot);
        Simulation simulation = new Simulation(this, slot, workspaceState.getSimulationContext(),
                buildSimulationListeners(slot, listener), requiredService,
                        dynamic, oscillating, collusion, 
                        numExecutions, workspaceState.getCurrentNetwork());
        submitSimulation(slot, simulation);
    }

    /**
     * Runs the current trust model over the current network using a typed configuration object.
     * @param observer Used to communicate changes to the GUI
     * @param config Simulation execution configuration
     */
    public void runTRM_WSN(SimulationListener listener, SimulationConfig config) {
        runTRM_WSN(SimulationSlot.PRIMARY, listener, config);
    }

    public void runTRM_WSN(SimulationSlot slot, SimulationListener listener, SimulationConfig config) {
        runTRM_WSN(
                slot,
                listener,
                config.isDynamic(),
                config.isOscillating(),
                config.isCollusion(),
                config.getNumExecutions());
    }
    
    /**
     * This method saves the current Wireless Sensor Network into a specified XML file 
     * @param fileName Path of the XML file where to write the current Network
     * @throws Exception If there is any problem when writing to the XML file
     */
    public void saveCurrentNetwork(String fileName) throws Exception {
        saveCurrentNetwork(SimulationSlot.PRIMARY, fileName);
    }

    public void saveCurrentNetwork(SimulationSlot slot, String fileName) throws Exception {
        workspace(slot).getCurrentNetwork().writeToXMLFile(fileName);
    }
    
    /**
     * This method loads a Wireless Sensor Network from a XML file 
     * @param fileName Path of the XML file describing the Network to load
     * @return The loaded WSN from XML file
     * @throws Exception If the given XML file hasn't the appropriate structure, or if
     * there is any problem reading the file
     */
    public Network loadCurrentNetwork(String fileName) throws Exception {
        return loadCurrentNetwork(SimulationSlot.PRIMARY, fileName);
    }

    public Network loadCurrentNetwork(SimulationSlot slot, String fileName) throws Exception {
        SimulationWorkspaceState workspaceState = workspace(slot);
        Network currentNetwork;
        Sensor.activateSimulationContext(workspaceState.getSimulationContext());
        try {
            Sensor.set_TRModel_WSN(workspaceState.getTrustModel());
            currentNetwork = workspaceState.getTrustModel().loadCurrentNetwork(fileName);
        } finally {
            Sensor.clearActiveSimulationContext();
        }
        workspaceState.setCurrentNetwork(currentNetwork);
        return currentNetwork;
    }

    /**
     * This method makes the current thread to sleep for a given amount of time
     */
    public void sleep() { 
        try {
            Thread.sleep(delay); 
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * This method returns current network
     * @return Current Network
     */
    public Network get_currentNetwork() { return get_currentNetwork(SimulationSlot.PRIMARY); }

    public Network get_currentNetwork(SimulationSlot slot) { return workspace(slot).getCurrentNetwork(); }

    /**
     * Clears the current network reference so UI starts from a clean state.
     */
    public void clearCurrentNetwork() {
        clearCurrentNetwork(SimulationSlot.PRIMARY);
    }

    public void clearCurrentNetwork(SimulationSlot slot) {
        workspace(slot).setCurrentNetwork(null);
    }

    /**
     * This method returns the service requested by every client in the network
     * @return The service requested by every client in the network
     */
    public Service get_requiredService() { return requiredService; }

    /**
     * This method returns the parameters file name for the current trust and reputation model
     * @return The parameters file name for the current trust and reputation model
     */
    public String get_parametersFile() { return get_parametersFile(SimulationSlot.PRIMARY); }

    public String get_parametersFile(SimulationSlot slot) { return workspace(slot).getParametersFile(); }

    /**
     * This method returns the parameters of the current trust and/or reputation model
     * @return The parameters of the current trust and/or reputation model
     */
    public TRMParameters get_TRMParameters() { return get_TRMParameters(SimulationSlot.PRIMARY); }

    public TRMParameters get_TRMParameters(SimulationSlot slot) { return workspace(slot).getTrustModel().get_TRMParameters(); }

    /**
     * This method returns a boolean indicating whether there is currently a simulation running or not
     * @return Boolean indicating whether there is currently a simulation running or not
     */
    public boolean isSimulationRunning() { return isSimulationRunning(SimulationSlot.PRIMARY); }

    public boolean isSimulationRunning(SimulationSlot slot) { return workspace(slot).getSimulationContext().isRunningSimulation(); }

    public boolean isAnySimulationRunning() {
        for (SimulationSlot slot : SimulationSlot.values()) {
            if (isSimulationRunning(slot)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the shared simulation runtime context.
     * @return Active simulation context
     */
    public SimulationContext getSimulationContext() { return getSimulationContext(SimulationSlot.PRIMARY); }

    public SimulationContext getSimulationContext(SimulationSlot slot) { return workspace(slot).getSimulationContext(); }

    /**
     * This method returns whether the current simulation is paused
     * @return true if the current simulation is paused
     */
    public boolean isSimulationPaused() {
        return isSimulationPaused(SimulationSlot.PRIMARY);
    }

    public boolean isSimulationPaused(SimulationSlot slot) {
        Simulation simulation = workspace(slot).getSimulation();
        return (simulation != null) && simulation.isPaused();
    }

    public boolean areAllRunningSimulationsPaused() {
        boolean hasRunningSimulation = false;
        for (SimulationSlot slot : SimulationSlot.values()) {
            if (isSimulationRunning(slot)) {
                hasRunningSimulation = true;
                if (!isSimulationPaused(slot)) {
                    return false;
                }
            }
        }
        return hasRunningSimulation;
    }
    
    /**
     * This method sets the parameters of current Trust and Reputation Model
     * through a {@link TRMParametersPanel}
     * @param trmParametersPanel Trust and Reputation Model parameters panel to get
     * parameters from
     */
    public void set_TRMParameters(TRMParametersPanel trmParametersPanel) {
        applyTrustModelParameters(SimulationSlot.PRIMARY, trmParametersPanel.get_TRMParameters());
    }

    public void set_TRMParameters(SimulationSlot slot, TRMParametersPanel trmParametersPanel) {
        applyTrustModelParameters(slot, trmParametersPanel.get_TRMParameters());
    }
    
    /**
     * This method sets the parameters of current Trust and Reputation Model through a parameters file
     * @param parametersFile File where to extract parameters from
     * @return Trust and Reputation Model Parameters
     * @throws Exception If the given file does not contain the appropriate parameters for current
     * Trust and Reputation Model
     */
    public TRMParameters set_TRMParameters(String parametersFile) throws Exception {
        return set_TRMParameters(SimulationSlot.PRIMARY, parametersFile);
    }

    public TRMParameters set_TRMParameters(SimulationSlot slot, String parametersFile) throws Exception {
        applyTrustModelParameters(slot, TrustModelFactory.instantiateParameters(workspace(slot).getTrustModel().getClass(), parametersFile));
        return workspace(slot).getTrustModel().get_TRMParameters();
    }

    private void applyTrustModelParameters(SimulationSlot slot, TRMParameters parameters) {
        SimulationWorkspaceState workspaceState = workspace(slot);
        workspaceState.getTrustModel().set_TRMParameters(parameters);
        workspaceState.getSimulationContext().setTrustModel(workspaceState.getTrustModel());
        Sensor.activateSimulationContext(workspaceState.getSimulationContext());
        try {
            Sensor.set_TRModel_WSN(workspaceState.getTrustModel());
        } finally {
            Sensor.clearActiveSimulationContext();
        }
    }

    /**
     * This method sets the current Trust and Reputation Model
     * @param trmodel_wsn Name of the desired Trust and Reputation Model
     * @throws Exception If the default parameters file does not contain the appropriate parameters 
     * for its Trust and Reputation Model
     */
    public void set_TRModel_WSN(String trmodel_wsn) throws Exception {
        set_TRModel_WSN(SimulationSlot.PRIMARY, trmodel_wsn);
    }

    public void set_TRModel_WSN(SimulationSlot slot, String trmodel_wsn) throws Exception {
        TrustModelBundle bundle = TrustModelFactory.create(trmodel_wsn);
        updateCurrentTrustModel(slot, bundle.getModel(), bundle.getDefaultParametersFile());
    }

    public String getTrustModelName(SimulationSlot slot) {
        TRModel_WSN trustModel = workspace(slot).getTrustModel();
        if (trustModel == null) {
            return null;
        }
        return TrustModelRegistry.getByModelClass(trustModel.getClass()).getModelName();
    }

    /**
     * This method saves a new content into the selected parameters file
     * @param filePath Path of the selected parameters file
     * @param newContent New content to be saved in the selected parameters file
     * @throws Exception If an error occurs while saving the new content into the selected parameters file
     */
    public void saveParametersFileContent(String filePath, String newContent) throws Exception {
        ControllerParametersIO.saveParametersFileContent(filePath, newContent);
    }

    /**
     * This method retrieves the content of the current parameters file
     * @return The content of the current parameters file
     * @throws Exception If an error occurs while retrieving the content of the current parameters file
     */
    public String get_ParametersFileContent() throws Exception {
        return get_ParametersFileContent(SimulationSlot.PRIMARY);
    }

    public String get_ParametersFileContent(SimulationSlot slot) throws Exception {
        return ControllerParametersIO.getParametersFileContent(workspace(slot).getParametersFile());
    }

    /**
     * This method retrieves the content of the default parameters file of the given trust model
     * @param trmodel_wsn Trust model whose default parameters file content is to be retrieved
     * @return The content of the default parameters file of the given trust model
     * @throws Exception If an error occurs while retrieving the content of the default parameters file of the given trust model
     */
    public String get_DefaultParametersFileContent(String trmodel_wsn) throws Exception {
        return ControllerParametersIO.getDefaultParametersFileContent(trmodel_wsn);
    }

    /**
     * This method sets the delay used to slow the simulations visualization
     * @param delay Delay in milliseconds
     */
    public void set_delay(long delay) { this.delay = delay; }

    /**
     * This method sets the parameters file name for the current trust and reputation model
     * @param parametersFile Parameters file name for the current trust and reputation model
     */
    public void set_parametersFile(String parametersFile) { set_parametersFile(SimulationSlot.PRIMARY, parametersFile); }

    public void set_parametersFile(SimulationSlot slot, String parametersFile) { workspace(slot).setParametersFile(parametersFile); }

    
    public Sensor getSensorAtCoordinate(double x, double y) {
        if (!hasCurrentNetwork())
            return null;
        int error = 2;
        for (Sensor sensor : primaryWorkspace().getCurrentNetwork().get_sensors()) {
            if (((x <= (sensor.getX()+error)) && (x >= (sensor.getX()-error)))
                    &&
                ((y <= (sensor.getY()+error)) && (y >= (sensor.getY()-error))))
                return sensor;
        }
        
        return null;
    }
    
    /**
     * This method returns the sensor with identifier id. If such sensor does not exist, it returns null
     * @param id Identifier of the sensor to be retrieved
     * @return The sensor with identifier id. If such sensor does not exist, it returns null
     */
    public Sensor getSensor(int id) {
        return getSensor(SimulationSlot.PRIMARY, id);
    }

    public Sensor getSensor(SimulationSlot slot, int id) {
        if (!hasCurrentNetwork(slot))
            return null;
        return workspace(slot).getCurrentNetwork().getSensor(id);
    }
}
