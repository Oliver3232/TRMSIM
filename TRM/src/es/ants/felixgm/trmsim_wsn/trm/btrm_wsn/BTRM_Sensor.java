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
 *
 *
 * Additional Terms of this License
 * --------------------------------
 *
 * 1. It is Required the preservation of specified reasonable legal notices
 * and author attributions in that material and in the Appropriate Legal
 * Notices displayed by works containing it.
 *
 * 2. It is limited the use for publicity purposes of names of licensors or
 * authors of the material.
 *
 * 3. It is Required indemnification of licensors and authors of that material
 * by anyone who conveys the material (or modified versions of it) with
 * contractual assumptions of liability to the recipient, for any liability
 * that these contractual assumptions directly impose on those licensors
 * and authors.
 *
 * 4. It is Prohibited misrepresentation of the origin of that material, and it is
 * required that modified versions of such material be marked in reasonable
 * ways as different from the original version.
 *
 * 5. It is Declined to grant rights under trademark law for use of some trade
 * names, trademarks, or service marks.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program (lgpl.txt).  If not, see <http://www.gnu.org/licenses/>
 */


package es.ants.felixgm.trmsim_wsn.trm.btrm_wsn;

import es.ants.felixgm.trmsim_wsn.network.Link;
import es.ants.felixgm.trmsim_wsn.network.Sensor;
import java.util.ArrayList;

/**
 * <p>This class models a Sensor implementing BTRM-WSN</p>
 * @author <a href="http://ants.dif.um.es/~felixgm/en" target="_blank">F&eacute;lix G&oacute;mez M&aacute;rmol</a>, <a href="http://webs.um.es/gregorio" target="_blank">Gregorio Mart&iacute;nez P&eacute;rez</a> and Antonio Bern&aacute;rdez
 * @version 0.2
 * @since 0.2
 */
public class BTRM_Sensor extends Sensor {
    /** Number of servers composing the network this sensor belongs to */
    // CHANGED: Instance variable, removed static
    protected int numServers = 0;

    // NEW: Local parameters
    protected BTRM_WSN_Parameters parameters;

    /**
     * This constructor creates a new Sensor implementing BTRM-WSN
     */
    public BTRM_Sensor () {
        super();
    }

    /**
     * This constructor creates a new Sensor implementing BTRM-WSN
     * @param id Identifier of the new sensor
     * @param x X coordinate of the new sensor
     * @param y Y coordinate of the new sensor
     */
    public BTRM_Sensor(int id, double x, double y) {
        super(id,x,y);
    }

    // NEW: Setters for Network to call
    public void setParameters(BTRM_WSN_Parameters p) { this.parameters = p; }
    public void setNumServers(int n) { this.numServers = n; }

    // Helper to reset links with correct parameters
    public void resetLinks() {
        if (links != null) {
            double initialPheromone = (parameters != null) ? parameters.get_initialPheromone() : 0.5;
            for (Link link : links) {
                if (link instanceof BTRM_Link) {
                    ((BTRM_Link) link).setInitialPheromoneAndReset(initialPheromone);
                }
            }
        }
    }

    @Override
    public void addLink(Sensor node) {
        if (!isNeighbor(node))
        {
            if (links == null)
                links = new ArrayList<Link>();

            // FIX: Pass initial pheromone if parameters exist, else default
            double initPheromone = (parameters != null) ? parameters.get_initialPheromone() : 0.5;
            BTRM_Link link = new BTRM_Link(this,(BTRM_Sensor)node, initPheromone);
            links.add(link);
        }
    }

    /**
     * Returns the number of servers of the network
     * @return Number of servers
     */
    // CHANGED: return instance variable
    public int get_numServers() { return numServers; }

    /**
     * Sets the number of servers
     * @param numServers Number of servers
     */
    // CHANGED: Removed static
    public void setNumServersStatic(int numServers){ this.numServers = numServers; }

    /**
     * Gets the pheromone trace with a given neighbor
     * @param sensor One of this sensor's neighbors
     * @return The pheromone trace with the given neighbor sensor
     */
    synchronized public double getPheromone(Sensor sensor) {
        if (sensor.isActive())
            for (Link link : links)
                if (link.get_destination().equals(sensor)) {
                    transmittedDistance += this.distance(sensor);
                    try {
                        if (collusion && (get_goodness(requiredService) < 0.5) &&
                                (sensor.get_numServices() > 0)) {
                            if (sensor.get_goodness(requiredService) < 0.5)
                                return BTRM_Link.MAX_PHEROMONE;
                            else
                                return BTRM_Link.MIN_PHEROMONE;
                        } else
                            return ((BTRM_Link)link).get_pheromone();
                    } catch(Exception ex) {}
                }

        return 0.0;
    }

    /**
     * Gets the heuristic value with a given neighbor
     * @param sensor One of this sensor's neighbors
     * @return The heuristic value with the given neighbor sensor
     */
    synchronized public double getHeuristic(Sensor sensor) {
        if (sensor.isActive())
            for (Link link : links)
                if (link.get_destination().equals(sensor)) {
                    transmittedDistance += this.distance(sensor);
                    return ((BTRM_Link)link).get_heuristic();
                }

        return 0.0;
    }

    /**
     * Sets the pheromone trace value of the link connecting to a certain neighbor
     * @param sensor Neighbor whose pheromone trace is to be modified
     * @param value New pheromone value
     */
    synchronized public void setPheromone(Sensor sensor, double value) {
        for (Link link : links)
            if (link.get_destination().equals(sensor)) {
                transmittedDistance += this.distance(sensor);
                ((BTRM_Link)link).set_pheromone(value);
            }
    }

    /**
     * Sets the heuristic trace value of the link connecting to a certain neighbor
     * @param sensor Neighbor whose heuristic trace is to be modified
     * @param value New heuristic value
     */
    synchronized public void setHeuristic(Sensor sensor, double value) {
        for (Link link : links)
            if (link.get_destination().equals(sensor)) {
                transmittedDistance += this.distance(sensor);
                ((BTRM_Link)link).set_heuristic(value);
            }
    }

    @Override
    public void reset() {
        for (Link link : links) {
            ((BTRM_Link)link).reset();
        }
    }

    @Override
    public String toString() {
        String s = id+" ("+xPosition+","+yPosition+") ->";

        for (Link link : links)
            s += " ("+link.get_destination().id()
                    +","+((int)(getPheromone(link.get_destination())*100))/100.0
                    +","+((int)(getHeuristic(link.get_destination())*100))/100.0
                    +") |";
        return s;
    }
}