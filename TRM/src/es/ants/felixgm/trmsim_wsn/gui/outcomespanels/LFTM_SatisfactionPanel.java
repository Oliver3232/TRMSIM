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

import es.ants.felixgm.trmsim_wsn.outcomes.FuzzyOutcome;
import es.ants.felixgm.trmsim_wsn.outcomes.Outcome;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Collection;

/**
 * <p>This class represents the generic panel used to plot the satisfaction
 * of the clients using the LFTM model</p>
 * @author <a href="http://ants.dif.um.es/~felixgm/en" target="_blank">F&eacute;lix G&oacute;mez M&aacute;rmol</a>, <a href="http://webs.um.es/gregorio" target="_blank">Gregorio Mart&iacute;nez P&eacute;rez</a>
 * @version 0.4
 * @since 0.4
 */
public class LFTM_SatisfactionPanel extends OutcomesPanel {

    protected Color veryHighSatisfactionColor = new Color(56, 142, 60);
    protected Color highSatisfactionColor = new Color(255, 167, 38);
    protected Color mediumSatisfactionColor = new Color(66, 165, 245);
    protected Color lowSatisfactionColor = new Color(255, 112, 67);
    protected Color veryLowSatisfactionColor = new Color(84, 110, 122);
    /** Upper padding so top values remain visible */
    protected double topAxisMargin = 0.08;

    /** Creates new form LFTM_SatisfactionPanel */
    public LFTM_SatisfactionPanel(Collection<Outcome> outcomes) {
        super(outcomes);
        initComponents();
    }

    public LFTM_SatisfactionPanel() {
        super("Satisfaction");
        initComponents();
    }

    protected void drawAxes(Graphics graphics) {
        int height = this.getHeight();
        int width = this.getWidth();

        graphics.setColor(new Color(64, 80, 102));
        graphics.setFont(new Font("SansSerif", Font.BOLD, 14));
        graphics.drawString("Satisfaction Distribution", (int)(width*xAxisMargin), 16);
        graphics.setFont(new Font("SansSerif", Font.PLAIN, 12));
        graphics.drawString("Y: Satisfaction [%]", (int)(width*xAxisMargin), 32);

        graphics.drawLine(0, (int)(height*yAxisMargin), width, (int)(height*yAxisMargin));
        graphics.drawLine((int)(width*xAxisMargin), (int)(height*topAxisMargin), (int)(width*xAxisMargin), height);

        graphics.drawString("Very High", (int)((xAxisMargin+(1-xAxisMargin)*(2.0/18.0))*width), (int)(0.5*height*(yAxisMargin+1))+5);
        graphics.drawString("High", (int)((xAxisMargin+(1-xAxisMargin)*(5.5/18.0))*width), (int)(0.5*height*(yAxisMargin+1))+5);
        graphics.drawString("Medium", (int)((xAxisMargin+(1-xAxisMargin)*(8.0/18.0))*width), (int)(0.5*height*(yAxisMargin+1))+5);
        graphics.drawString("Low", (int)((xAxisMargin+(1-xAxisMargin)*(11.5/18.0))*width), (int)(0.5*height*(yAxisMargin+1))+5);
        graphics.drawString("Very Low", (int)((xAxisMargin+(1-xAxisMargin)*(14.0/18.0))*width), (int)(0.5*height*(yAxisMargin+1))+5);
        graphics.drawString("X: Satisfaction class", (int)(width*0.38), height - 8);
    }

    protected void plotOutcomes(Collection<Outcome> outcomes, Graphics graphics) {
        this.outcomes = outcomes;

        int height = this.getHeight();
        int width = this.getWidth();

        clearPanel(graphics);
        drawAxes(graphics);

        if ((outcomes == null)  || (outcomes.size() == 0))
            return;

        Outcome outcome = Outcome.computeOutcomes(outcomes);

        double veryHighSatisfaction = ((FuzzyOutcome)outcome).getSatisfactionPercentage("Very High");
        double highSatisfaction = ((FuzzyOutcome)outcome).getSatisfactionPercentage("High");
        double mediumSatisfaction = ((FuzzyOutcome)outcome).getSatisfactionPercentage("Medium");
        double lowSatisfaction = ((FuzzyOutcome)outcome).getSatisfactionPercentage("Low");
        double veryLowSatisfaction = ((FuzzyOutcome)outcome).getSatisfactionPercentage("Very Low");
        int yZero = (int)(height*yAxisMargin);
        int yTop = (int)(height*topAxisMargin);
        int drawableHeight = Math.max(1, yZero-yTop);
        Graphics2D g2 = (graphics instanceof Graphics2D) ? (Graphics2D) graphics : null;

        drawRoundedSatisfactionBar(graphics, veryHighSatisfactionColor,
                (int)((xAxisMargin+(1-xAxisMargin)*(2.0/18.0))*width),
                yZero-(int)(drawableHeight*veryHighSatisfaction),
                (int)((1-xAxisMargin)*(2.0/18.0)*width),
                (int)(drawableHeight*veryHighSatisfaction));

        drawRoundedSatisfactionBar(graphics, highSatisfactionColor,
                (int)((xAxisMargin+(1-xAxisMargin)*(5.0/18.0))*width),
                yZero-(int)(drawableHeight*highSatisfaction),
                (int)((1-xAxisMargin)*(2.0/18.0)*width),
                (int)(drawableHeight*highSatisfaction));

        drawRoundedSatisfactionBar(graphics, mediumSatisfactionColor,
                (int)((xAxisMargin+(1-xAxisMargin)*(8.0/18.0))*width),
                yZero-(int)(drawableHeight*mediumSatisfaction),
                (int)((1-xAxisMargin)*(2.0/18.0)*width),
                (int)(drawableHeight*mediumSatisfaction));

        drawRoundedSatisfactionBar(graphics, lowSatisfactionColor,
                (int)((xAxisMargin+(1-xAxisMargin)*(11.0/18.0))*width),
                yZero-(int)(drawableHeight*lowSatisfaction),
                (int)((1-xAxisMargin)*(2.0/18.0)*width),
                (int)(drawableHeight*lowSatisfaction));

        drawRoundedSatisfactionBar(graphics, veryLowSatisfactionColor,
                (int)((xAxisMargin+(1-xAxisMargin)*(14.0/18.0))*width),
                yZero-(int)(drawableHeight*veryLowSatisfaction),
                (int)((1-xAxisMargin)*(2.0/18.0)*width),
                (int)(drawableHeight*veryLowSatisfaction));

        graphics.setColor(axesColor);
        graphics.drawString(((int)(veryHighSatisfaction*10000.0))/100.0+" %", (int)((xAxisMargin+(1-xAxisMargin)*(2.0/18.0))*width), yZero-(int)(drawableHeight*veryHighSatisfaction));
        graphics.drawString(((int)(highSatisfaction*10000.0))/100.0+" %", (int)((xAxisMargin+(1-xAxisMargin)*(5.0/18.0))*width), yZero-(int)(drawableHeight*highSatisfaction));
        graphics.drawString(((int)(mediumSatisfaction*10000.0))/100.0+" %", (int)((xAxisMargin+(1-xAxisMargin)*(8.0/18.0))*width), yZero-(int)(drawableHeight*mediumSatisfaction));
        graphics.drawString(((int)(lowSatisfaction*10000.0))/100.0+" %", (int)((xAxisMargin+(1-xAxisMargin)*(11.0/18.0))*width), yZero-(int)(drawableHeight*lowSatisfaction));
        graphics.drawString(((int)(veryLowSatisfaction*10000.0))/100.0+" %", (int)((xAxisMargin+(1-xAxisMargin)*(14.0/18.0))*width), yZero-(int)(drawableHeight*veryLowSatisfaction));
        if (g2 != null) {
            g2.setStroke(new BasicStroke(1.0f));
        }
    }

    private void drawRoundedSatisfactionBar(Graphics graphics, Color color, int x, int y, int width, int height) {
        if (!(graphics instanceof Graphics2D)) {
            graphics.setColor(color);
            graphics.fillRect(x, y, width, height);
            return;
        }
        Graphics2D g2 = (Graphics2D) graphics;
        int arc = Math.max(8, Math.min(14, width / 3));
        int safeHeight = Math.max(1, height);

        g2.setColor(new Color(20, 20, 20, 38));
        g2.fillRoundRect(x + 2, y + 2, width, safeHeight, arc, arc);
        g2.setColor(color);
        g2.fillRoundRect(x, y, width, safeHeight, arc, arc);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
