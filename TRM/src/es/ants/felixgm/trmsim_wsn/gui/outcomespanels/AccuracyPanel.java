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

import es.ants.felixgm.trmsim_wsn.outcomes.BasicOutcome;
import es.ants.felixgm.trmsim_wsn.outcomes.Outcome;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>This class represents the generic panel used to plot the accuracy of
 * every Trust and Reputation Model, measured as the percentage of benevolent
 * servers suggested and selected by the current Trust and Reputation Model</p>
 * @author <a href="http://ants.dif.um.es/~felixgm/en" target="_blank">F&eacute;lix G&oacute;mez M&aacute;rmol</a>, <a href="http://webs.um.es/gregorio" target="_blank">Gregorio Mart&iacute;nez P&eacute;rez</a>
 * @version 0.5
 * @since 0.4
 */
public class AccuracyPanel extends OutcomesPanel {
    /** Color used to plot the current accuracy of each outcome */
    protected Color currentValueColor = new Color(0, 150, 136);
    /** Color used to plot the average accuracy of all the outcomes */
    protected Color averageValueColor = new Color(255, 112, 67);
    /** Grid color */
    protected Color gridColor = new Color(206, 218, 236);
    /** Axis label color */
    protected Color labelColor = new Color(64, 80, 102);
    /** Cached points for hover tooltips */
    private List<HoverPoint> hoverPoints = new ArrayList<HoverPoint>();

    /**
     * Class AccuracyPanel constructor
     * @param outcomes Outcomes to be plotted in this outcomes panel
     */
    public AccuracyPanel(Collection<Outcome> outcomes) {
        super(outcomes);
        initComponents();
    }

    /**
     * Class AccuracyPanel constructor
     */
    public AccuracyPanel() {
        super("Accuracy");
        initComponents();
    }

    @Override
    protected void drawAxes(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics;
        ChartArea chart = buildChartArea();

        g2.setColor(labelColor);
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2.drawString("Accuracy Over Recent Simulations", chart.left, chart.top - 18);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.drawString("Y: Accuracy [% of benevolent selections]", chart.left, chart.top - 4);

        g2.setColor(gridColor);
        g2.setStroke(new BasicStroke(1f));
        for (int tick = 0; tick <= 5; tick++) {
            int y = chart.bottom - (int) ((chart.bottom - chart.top) * (tick / 5.0));
            g2.drawLine(chart.left, y, chart.right, y);
            g2.setColor(labelColor);
            g2.drawString(String.valueOf(tick * 20), Math.max(2, chart.left - 30), y + 4);
            g2.setColor(gridColor);
        }

        g2.setColor(new Color(84, 102, 132));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(chart.left, chart.top, chart.left, chart.bottom);
        g2.drawLine(chart.left, chart.bottom, chart.right, chart.bottom);

        g2.setColor(labelColor);
        g2.drawString("X: Recent simulation index", chart.left + (chart.width / 2) - 70, chart.bottom + 28);

        int legendX = chart.right - 220;
        int legendY = chart.top - 18;
        drawLegend(g2, legendX, legendY, currentValueColor, "Current");
        drawLegend(g2, legendX + 108, legendY, averageValueColor, "Running avg");
    }

    @Override
    protected void plotOutcomes(Collection<Outcome> outcomes, Graphics graphics) {
        this.outcomes = outcomes;

        clearPanel(graphics);
        drawAxes(graphics);

        if (!(graphics instanceof Graphics2D) || outcomes == null || outcomes.isEmpty())
            return;

        Graphics2D g2 = (Graphics2D) graphics;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        List<Double> currentSeries = new ArrayList<Double>();
        List<Double> averageSeries = new ArrayList<Double>();
        double cumulative = 0.0;
        int count = 0;
        int start = Math.max(0, outcomes.size() - windowsSize);
        for (Outcome outcome : outcomes) {
            count++;
            double current = ((BasicOutcome) outcome).get_avgSatisfaction();
            cumulative += current;
            if ((count - 1) >= start) {
                currentSeries.add(bound01(current));
                averageSeries.add(bound01(cumulative / count));
            }
        }

        ChartArea chart = buildChartArea();
        hoverPoints = new ArrayList<HoverPoint>();
        drawSmoothedSeries(g2, currentSeries, chart, currentValueColor, 2.8f, "Current");
        drawSmoothedSeries(g2, averageSeries, chart, averageValueColor, 2.8f, "Running avg");

        if (!currentSeries.isEmpty()) {
            double current = currentSeries.get(currentSeries.size() - 1) * 100.0;
            double avg = averageSeries.get(averageSeries.size() - 1) * 100.0;
            g2.setColor(labelColor);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.drawString(String.format("Current: %.2f%%", current), chart.left, chart.bottom + 46);
            g2.drawString(String.format("Running avg: %.2f%%", avg), chart.left + 170, chart.bottom + 46);
        }
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        if (hoverPoints == null || hoverPoints.isEmpty() || event == null) {
            return super.getToolTipText(event);
        }
        int radius = 7;
        for (HoverPoint point : hoverPoints) {
            if (Math.abs(event.getX() - point.x) <= radius && Math.abs(event.getY() - point.y) <= radius) {
                return point.label;
            }
        }
        return super.getToolTipText(event);
    }

    private void drawSmoothedSeries(Graphics2D g2, List<Double> values, ChartArea chart, Color color, float strokeWidth, String seriesLabel) {
        if (values == null || values.isEmpty()) {
            return;
        }

        if (values.size() == 1) {
            double y = mapY(values.get(0), chart);
            fillAreaUnderSeries(g2, new double[] { chart.left }, new double[] { y }, chart, color);
            g2.setColor(color);
            g2.fill(new Ellipse2D.Double(chart.left - 3, y - 3, 6, 6));
            hoverPoints.add(new HoverPoint(chart.left, (int)Math.round(y), String.format("%s: %.2f%%", seriesLabel, values.get(0) * 100.0)));
            return;
        }

        double stepX = chart.width / (double) (values.size() - 1);
        double[] xs = new double[values.size()];
        double[] ys = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            xs[i] = chart.left + (stepX * i);
            ys[i] = mapY(values.get(i), chart);
        }

        fillAreaUnderSeries(g2, xs, ys, chart, color);

        Path2D.Double path = new Path2D.Double();
        path.moveTo(xs[0], ys[0]);
        for (int i = 1; i < xs.length - 1; i++) {
            double cx = xs[i];
            double cy = ys[i];
            double nx = (xs[i] + xs[i + 1]) / 2.0;
            double ny = (ys[i] + ys[i + 1]) / 2.0;
            path.quadTo(cx, cy, nx, ny);
        }
        path.quadTo(xs[xs.length - 2], ys[ys.length - 2], xs[xs.length - 1], ys[ys.length - 1]);

        g2.setColor(color);
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(path);

        for (int i = 0; i < xs.length; i++) {
            g2.fill(new Ellipse2D.Double(xs[i] - 2.5, ys[i] - 2.5, 5, 5));
            hoverPoints.add(new HoverPoint((int)Math.round(xs[i]), (int)Math.round(ys[i]),
                    String.format("%s #%d: %.2f%%", seriesLabel, i + 1, values.get(i) * 100.0)));
        }
    }

    private void fillAreaUnderSeries(Graphics2D g2, double[] xs, double[] ys, ChartArea chart, Color color) {
        if (xs.length == 0 || ys.length == 0) {
            return;
        }
        Path2D.Double area = new Path2D.Double();
        area.moveTo(xs[0], chart.bottom);
        for (int i = 0; i < xs.length; i++) {
            area.lineTo(xs[i], ys[i]);
        }
        area.lineTo(xs[xs.length - 1], chart.bottom);
        area.closePath();
        Color fillColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 32);
        g2.setColor(fillColor);
        g2.fill(area);
    }

    private double mapY(double normalized, ChartArea chart) {
        double bounded = bound01(normalized);
        return chart.bottom - (chart.height * bounded);
    }

    private double bound01(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    private void drawLegend(Graphics2D g2, int x, int y, Color color, String label) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x, y, x + 20, y);
        g2.setColor(labelColor);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.drawString(label, x + 26, y + 4);
    }

    private ChartArea buildChartArea() {
        int width = getWidth();
        int height = getHeight();
        int left = Math.max(50, (int) (width * 0.10));
        int right = width - Math.max(14, (int) (width * 0.04));
        int top = Math.max(42, (int) (height * 0.15));
        int bottom = height - Math.max(54, (int) (height * 0.20));
        return new ChartArea(left, top, right, bottom);
    }

    private static class HoverPoint {
        private final int x;
        private final int y;
        private final String label;

        private HoverPoint(int x, int y, String label) {
            this.x = x;
            this.y = y;
            this.label = label;
        }
    }

    private static class ChartArea {
        private final int left;
        private final int top;
        private final int right;
        private final int bottom;
        private final int width;
        private final int height;

        private ChartArea(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = Math.max(left + 10, right);
            this.bottom = Math.max(top + 10, bottom);
            this.width = this.right - this.left;
            this.height = this.bottom - this.top;
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
