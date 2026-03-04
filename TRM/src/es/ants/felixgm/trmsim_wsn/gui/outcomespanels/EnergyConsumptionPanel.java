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

import es.ants.felixgm.trmsim_wsn.outcomes.EnergyConsumptionOutcome;
import es.ants.felixgm.trmsim_wsn.outcomes.Outcome;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>This class represents the generic panel used to plot the energy
 * consumption of every Trust and Reputation Model</p>
 * @author <a href="http://ants.dif.um.es/~felixgm/en" target="_blank">F&eacute;lix G&oacute;mez M&aacute;rmol</a>, <a href="http://webs.um.es/gregorio" target="_blank">Gregorio Mart&iacute;nez P&eacute;rez</a>
 * @version 0.4
 * @since 0.4
 */
public class EnergyConsumptionPanel extends OutcomesPanel {
    /** Color used to plot the clients energy consumption */
    protected Color clientEnergyColor = new Color(255, 167, 38);
    /** Color used to plot the malicious servers energy consumption */
    protected Color maliciousServerEnergyColor = new Color(239, 83, 80);
    /** Color used to plot the benevolent servers energy consumption */
    protected Color benevolentServerEnergyColor = new Color(102, 187, 106);
    /** Color used to plot the relay servers energy consumption */
    protected Color relayServerEnergyColor = new Color(66, 165, 245);

    /** 'alpha' parameter in the energy consumption formula: E=C+d^alpha */
    protected long alpha = 4;
    /** 'C' parameter in the energy consumption formula: E=C+d^alpha */
    protected long constant = 1000000000;
    /** Upper padding so 100% and top bars stay visible inside panel bounds */
    protected double topAxisMargin = 0.12;
    /** Grid color */
    protected Color gridColor = new Color(206, 218, 236);
    /** Axis label color */
    protected Color labelColor = new Color(64, 80, 102);

    /**
     * Class EnergyConsumptionPanel constructor
     * @param outcomes Outcomes to be plotted in this outcomes panel
     */
    public EnergyConsumptionPanel(Collection<Outcome> outcomes) {
        super(outcomes);
        yAxisMargin = 0.82;
        initComponents();
    }

    /**
     * Class EnergyConsumptionPanel constructor
     */
    public EnergyConsumptionPanel() {
        super("Energy Consumption");
        yAxisMargin = 0.82;
        initComponents();
    }

    @Override
    protected void drawAxes(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics;
        ChartArea chart = buildChartArea();
        ChartArea barChart = buildBarChartArea(chart);
        ChartArea pieChart = buildPieChartArea(chart);
        String[] labels = getCategoryLabels();
        Color[] colors = getCategoryColors();

        g2.setColor(labelColor);
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2.drawString(getHeaderTitle(), chart.left, barChart.top - 18);

        drawEnergyGuideYAxis(g2, barChart);
        g2.setColor(new Color(84, 102, 132));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(barChart.left, barChart.top, barChart.left, barChart.bottom);
        g2.drawLine(barChart.left, barChart.bottom, barChart.right, barChart.bottom);
        g2.drawLine(pieChart.left, barChart.top, pieChart.left, barChart.bottom);

        int barWidth = Math.max(20, barChart.width / Math.max(8, labels.length * 2));
        int slot = barChart.width / labels.length;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(labelColor);
        for (int i = 0; i < labels.length; i++) {
            int barX = barChart.left + i * slot + (slot - barWidth) / 2;
            g2.drawString(labels[i], barX - 2, barChart.bottom + 16);
        }

        int legendY = barChart.top - 10;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        int[] itemWidths = new int[labels.length];
        int totalLegendWidth = 0;
        for (int i = 0; i < labels.length; i++) {
            itemWidths[i] = 26 + g2.getFontMetrics().stringWidth(labels[i]) + 16;
            totalLegendWidth += itemWidths[i];
        }
        int legendX = barChart.left + Math.max(0, (barChart.width - totalLegendWidth) / 2);
        for (int i = 0; i < labels.length; i++) {
            drawLegend(g2, legendX, legendY, colors[i], labels[i]);
            legendX += itemWidths[i];
        }

        g2.setColor(labelColor);
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString("Distribution pie", pieChart.left + 8, pieChart.top - 6);
    }

    @Override
    protected void plotOutcomes(Collection<Outcome> outcomes, Graphics graphics) {
        this.outcomes = outcomes;

        clearPanel(graphics);
        drawAxes(graphics);

        if ((outcomes == null)  || (outcomes.size() == 0))
            return;

        Graphics2D g2 = (Graphics2D) graphics;
        ChartArea chart = buildChartArea();
        ChartArea barChart = buildBarChartArea(chart);
        ChartArea pieChart = buildPieChartArea(chart);
        Outcome outcome = Outcome.computeOutcomes(outcomes);
        double[] values = extractCategoryValues(outcome);
        double max = 0.0;
        for (double v : values) {
            if (v > max) max = v;
        }
        if (max <= 0.0) max = 1.0;
        double[] normalized = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            normalized[i] = values[i] / max;
        }
        double[] pieShares = buildPieShares(values);

        double avg = getAverageSensorEnergy(outcome);
        avg = 4*constant + Math.pow(avg,alpha);
        double power = Math.ceil(Math.log10(avg));
        avg = ((int)(avg/Math.pow(10, power-2)))/10.0;
        g2.setColor(labelColor);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.drawString("Avg sensor energy: " + avg + "*10^" + (power - 1), pieChart.left + 8, chart.top - 18);

        int slot = barChart.width / normalized.length;
        int barWidth = Math.max(20, barChart.width / Math.max(8, normalized.length * 2));
        for (int i = 0; i < normalized.length; i++) {
            int barX = barChart.left + i * slot + (slot - barWidth) / 2;
            int barHeight = (int)((barChart.bottom - barChart.top) * normalized[i]);
            int barY = barChart.bottom - barHeight;
            drawEnergyBar(g2, getCategoryColors()[i], barX, barY, barWidth, barHeight);
            drawBarValue(g2, normalized[i], barX, barY);
        }

        drawPieInset(g2, pieChart, pieShares, getCategoryColors(), getCategoryLabels(), values);
    }

    protected void drawEnergyGuideYAxis(Graphics2D graphics, ChartArea chart) {
        Color previousColor = graphics.getColor();
        int[] marks = {0, 25, 50, 75, 100};
        int yZero = chart.bottom;
        int yTop = chart.top;
        int xLeft = chart.left;
        int xRight = chart.right;

        for (int mark : marks) {
            int y = yZero - (int)((yZero - yTop) * (mark / 100.0));
            graphics.setColor(new Color(215, 225, 241));
            graphics.drawLine(xLeft, y, xRight, y);
            graphics.setColor(new Color(92, 108, 128));
            graphics.drawString(mark + "%", Math.max(2, xLeft - 35), y + 4);
        }

        graphics.setColor(previousColor);
    }

    protected void drawEnergyBar(Graphics graphics, Color color, int x, int y, int width, int height) {
        if (!(graphics instanceof Graphics2D)) {
            graphics.setColor(color);
            graphics.fillRect(x, y, width, height);
            return;
        }
        Graphics2D g2 = (Graphics2D) graphics;
        int arc = Math.max(8, Math.min(14, width / 3));
        int safeHeight = Math.max(1, height);

        g2.setColor(new Color(20, 20, 20, 40));
        g2.fillRoundRect(x + 2, y + 2, width, safeHeight, arc, arc);
        g2.setColor(color);
        g2.fillRoundRect(x, y, width, safeHeight, arc, arc);
    }

    protected void drawBarValue(Graphics graphics, double normalizedValue, int x, int yTop) {
        graphics.setColor(new Color(52, 68, 88));
        graphics.setFont(new Font("SansSerif", Font.PLAIN, 11));
        String text = String.format("%.1f%%", normalizedValue * 100.0);
        graphics.drawString(text, x + 2, Math.max(44, yTop - 4));
    }

    protected void drawPieInset(Graphics2D g2, ChartArea chart, double[] shares, Color[] colors, String[] labels, double[] values) {
        int size = Math.max(96, Math.min(chart.width - 20, chart.bottom - chart.top - 20));
        int x = chart.left + Math.max(6, (chart.width - size) / 2);
        int y = chart.top + Math.max(8, (chart.bottom - chart.top - size) / 2);
        g2.setColor(new Color(255, 255, 255, 210));
        g2.fillRoundRect(x - 6, y - 6, size + 12, size + 12, 12, 12);
        int angle = 0;
        for (int i = 0; i < shares.length; i++) {
            int slice = (i == shares.length - 1) ? 360 - angle : (int)Math.round(shares[i] * 360.0);
            g2.setColor(colors[i]);
            g2.fillArc(x, y, size, size, angle, slice);
            g2.setColor(new Color(255, 255, 255, 210));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawArc(x, y, size, size, angle, slice);
            angle += slice;
        }
        g2.setColor(new Color(120, 140, 170));
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawOval(x, y, size, size);

        drawPieValues(g2, chart, labels, values, shares, colors);
    }

    protected void drawPieValues(Graphics2D g2, ChartArea chart, String[] labels, double[] values, double[] shares, Color[] colors) {
        int[] displayOrder = getPieValueDisplayOrder(labels);
        int lineHeight = 13;
        int startX = chart.left + 8;
        int startY = chart.top + 16;

        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        for (int i = 0; i < displayOrder.length; i++) {
            int index = displayOrder[i];
            int y = startY + i * lineHeight;
            g2.setColor(colors[index]);
            g2.fillRoundRect(startX, y - 8, 10, 10, 3, 3);
            g2.setColor(labelColor);
            g2.drawString(
                    labels[index] + ": " + formatEnergyValue(values[index]) + " (" + String.format("%.1f%%", shares[index] * 100.0) + ")",
                    startX + 14,
                    y
            );
        }
    }

    protected int[] getPieValueDisplayOrder(String[] labels) {
        List<Integer> order = new ArrayList<Integer>();
        addLabelIndex(labels, order, "Relay");
        addLabelIndex(labels, order, "Benevolent");
        addLabelIndex(labels, order, "Malicious");
        addLabelIndex(labels, order, "Client");
        for (int i = 0; i < labels.length; i++) {
            if (!order.contains(i)) {
                order.add(i);
            }
        }

        int[] result = new int[order.size()];
        for (int i = 0; i < order.size(); i++) {
            result[i] = order.get(i);
        }
        return result;
    }

    private void addLabelIndex(String[] labels, List<Integer> order, String name) {
        for (int i = 0; i < labels.length; i++) {
            if (labels[i].equalsIgnoreCase(name) && !order.contains(i)) {
                order.add(i);
                return;
            }
        }
    }

    protected double[] buildPieShares(double[] values) {
        double[] shares = new double[values.length];
        double total = 0.0;
        for (double value : values) {
            total += Math.max(0.0, value);
        }
        if (total <= 0.0) {
            return shares;
        }
        for (int i = 0; i < values.length; i++) {
            shares[i] = Math.max(0.0, values[i]) / total;
        }
        return shares;
    }

    protected String formatEnergyValue(double value) {
        if (Math.abs(value) < 0.0001) {
            return "0";
        }
        return String.format("%.2e", value);
    }

    protected String getHeaderTitle() {
        return "Energy Consumption Distribution";
    }

    protected String[] getCategoryLabels() {
        return new String[] {"Client", "Malicious", "Benevolent", "Relay"};
    }

    protected Color[] getCategoryColors() {
        return new Color[] {clientEnergyColor, maliciousServerEnergyColor, benevolentServerEnergyColor, relayServerEnergyColor};
    }

    protected double[] extractCategoryValues(Outcome outcome) {
        EnergyConsumptionOutcome e = (EnergyConsumptionOutcome) outcome;
        return new double[] {
                e.get_clientEnergyConsumption(),
                e.get_maliciousServerEnergyConsumption(),
                e.get_benevolentServerEnergyConsumption(),
                e.get_relayServerEnergyConsumption()
        };
    }

    protected double getAverageSensorEnergy(Outcome outcome) {
        return ((EnergyConsumptionOutcome) outcome).get_avgSensorEnergyConsumption();
    }

    protected void drawLegend(Graphics2D g2, int x, int y, Color color, String label) {
        g2.setColor(color);
        g2.fillRoundRect(x, y - 8, 14, 10, 4, 4);
        g2.setColor(labelColor);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.drawString(label, x + 20, y + 1);
    }

    protected ChartArea buildChartArea() {
        int width = getWidth();
        int height = getHeight();
        int left = Math.max(54, (int)(width * 0.10));
        int right = width - Math.max(14, (int)(width * 0.04));
        int top = Math.max(42, (int)(height * 0.15));
        int bottom = height - Math.max(48, (int)(height * 0.17));
        return new ChartArea(left, top, right, bottom);
    }

    protected ChartArea buildBarChartArea(ChartArea chart) {
        int splitX = chart.left + (int)(chart.width * 0.72);
        int right = Math.max(chart.left + 50, splitX - 8);
        return new ChartArea(chart.left, chart.top, right, chart.bottom);
    }

    protected ChartArea buildPieChartArea(ChartArea chart) {
        int splitX = chart.left + (int)(chart.width * 0.72);
        int left = Math.min(chart.right - 90, splitX + 8);
        return new ChartArea(left, chart.top + 8, chart.right, chart.bottom - 2);
    }

    protected static class ChartArea {
        protected final int left;
        protected final int top;
        protected final int right;
        protected final int bottom;
        protected final int width;

        protected ChartArea(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = Math.max(left + 12, right);
            this.bottom = Math.max(top + 12, bottom);
            this.width = this.right - this.left;
        }
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
