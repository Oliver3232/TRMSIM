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

package es.ants.felixgm.trmsim_wsn.gui.outcomespanels;

import es.ants.felixgm.trmsim_wsn.outcomes.EigenTrustEnergyConsumptionOutcome;
import es.ants.felixgm.trmsim_wsn.outcomes.Outcome;
import java.awt.Color;
import java.util.Collection;

/**
 * <p>This class represents the generic panel used to plot the energy
 * consumption of EigenTrust Model</p>
 * @author <a href="http://ants.dif.um.es/~felixgm/en" target="_blank">F&eacute;lix G&oacute;mez M&aacute;rmol</a>, <a href="http://webs.um.es/gregorio" target="_blank">Gregorio Mart&iacute;nez P&eacute;rez</a>
 * @version 0.4
 * @since 0.4
 */
public class EigenTrustEnergyConsumptionPanel extends EnergyConsumptionPanel {

    /** Color used to plot the pre-trusted peers energy consumption */
    protected Color preTrustedPeerEnergyColor = new Color(171, 71, 188);

    /**
     * Class EigenTrustEnergyConsumptionPanel constructor
     * @param outcomes Outcomes to be plotted in this outcomes panel
     */
    public EigenTrustEnergyConsumptionPanel(Collection<Outcome> outcomes) {
        super(outcomes);
    }

    /**
     * Class EigenTrustEnergyConsumptionPanel constructor
     */
    public EigenTrustEnergyConsumptionPanel() {
        super();
    }

    @Override
    protected String getHeaderTitle() {
        return "EigenTrust Energy Consumption";
    }

    @Override
    protected String[] getCategoryLabels() {
        return new String[] {"Pre-Trusted", "Malicious", "Benevolent", "Relay"};
    }

    @Override
    protected Color[] getCategoryColors() {
        return new Color[] {preTrustedPeerEnergyColor, maliciousServerEnergyColor, benevolentServerEnergyColor, relayServerEnergyColor};
    }

    @Override
    protected double[] extractCategoryValues(Outcome outcome) {
        EigenTrustEnergyConsumptionOutcome e = (EigenTrustEnergyConsumptionOutcome) outcome;
        return new double[] {
                e.get_preTrustedPeerEnergyConsumption(),
                e.get_maliciousServerEnergyConsumption(),
                e.get_benevolentServerEnergyConsumption(),
                e.get_relayServerEnergyConsumption()
        };
    }

    @Override
    protected double getAverageSensorEnergy(Outcome outcome) {
        return ((EigenTrustEnergyConsumptionOutcome) outcome).get_avgSensorEnergyConsumption();
    }
}
