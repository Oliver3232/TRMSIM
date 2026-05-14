package es.ants.felixgm.trmsim_wsn.gui.export;

import es.ants.felixgm.trmsim_wsn.outcomes.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class SimulationChartExporter {

    private static final int WIDTH = 1200;
    private static final int CHART_H = 240;
    private static final int ML = 88;
    private static final int MR = 36;
    private static final int MT = 46;
    private static final int MB = 44;

    private static final Color BG_CHART  = new Color(250, 251, 252);
    private static final Color GRID_CLR  = new Color(215, 220, 225);
    private static final Color AXIS_CLR  = new Color(70,  75,  80);
    private static final Color TEXT_DARK = new Color(30,  35,  40);

    private static final Color C_BLUE   = new Color(66,  133, 244);
    private static final Color C_RED    = new Color(220,  53,  69);
    private static final Color C_GREEN  = new Color(52,  168,  83);
    private static final Color C_ORANGE = new Color(251, 152,   0);
    private static final Color C_PURPLE = new Color(103,  58, 183);

    private static final String[] FUZZY_TERMS  = {"Very High", "High", "Medium", "Low", "Very Low"};
    private static final Color[]  FUZZY_COLORS = {
        new Color(34, 139, 34),
        new Color(100, 200, 80),
        new Color(255, 200,  0),
        new Color(255, 120,  0),
        new Color(200,  40, 40)
    };

    // -------------------------------------------------------------------------
    // Public entry point
    // -------------------------------------------------------------------------

    public static String exportCharts(Component parent, List<Outcome> outcomes, ExportRequest request) {
        try {
            if (outcomes.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "No simulation data available for export",
                        "Export Failed", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            String filename = request.resolveFilePath("charts", ".png");
            ImageIO.write(renderAll(outcomes), "png", new File(filename));
            return filename;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, "Error during chart export: " + ex.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Layout
    // -------------------------------------------------------------------------

    private static BufferedImage renderAll(List<Outcome> outcomes) {
        boolean hasEnergy = hasEnergyData(outcomes);
        boolean hasFuzzy  = hasFuzzyData(outcomes);
        boolean hasBasic  = hasBasicData(outcomes);

        int n = 0;
        if (hasBasic)  n += 4;
        if (hasEnergy) n += 1;
        if (hasFuzzy)  n += 1;

        int sectionH = MT + CHART_H + MB;
        int totalH   = 64 + n * sectionH;

        BufferedImage img = new BufferedImage(WIDTH, totalH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = makeG(img);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, totalH);

        String modelName = resolveModelName(outcomes);
        String heading   = "TRMSim-WSN — Simulation Charts";
        if (!modelName.isEmpty()) heading += "  |  " + modelName;

        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.setColor(TEXT_DARK);
        g.drawString(heading, ML, 40);

        int y = 64;

        if (hasBasic) {
            double[] sat  = extractSatisfactions(outcomes);
            double[] path = extractPathLengths(outcomes);

            double satMin  = adaptiveMin(sat,  0.0);
            double satMax  = adaptiveMax(sat,  1.0);
            double pathMin = adaptiveMin(path, 0.0);
            double pathMax = adaptiveMax(path, maxOf(path, 1.0));

            y = drawBandChart(g, y, sat,
                    "Satisfaction over Simulation Runs  (bold = rolling mean,  band = ±1σ)",
                    "Satisfaction", satMin, satMax, C_BLUE);

            y = drawBandChart(g, y, path,
                    "Path Length over Simulation Runs  (bold = rolling mean,  band = ±1σ)",
                    "Path Length", pathMin, pathMax, C_GREEN);

            y = drawHistogram(g, y, sat,
                    "Satisfaction Distribution (Histogram)", "Satisfaction",
                    0.0, 1.0, 10, C_BLUE);

            double maxPath = maxOf(path, 1.0);
            y = drawScatter(g, y, path, sat, maxPath, satMin, satMax);
        }

        if (hasEnergy) {
            y = drawEnergyBar(g, y, outcomes);
        }

        if (hasFuzzy) {
            drawFuzzyStackedBar(g, y, outcomes);
        }

        g.dispose();
        return img;
    }

    // -------------------------------------------------------------------------
    // 1 & 2. Band chart  (replaces old line + convergence charts)
    //   small n (≤60): line + dots
    //   large n  (>60): shaded ±1σ band + bold rolling mean + faint raw line
    // -------------------------------------------------------------------------

    private static int drawBandChart(Graphics2D g, int yBase, double[] values,
                                     String title, String yLabel,
                                     double yMin, double yMax, Color color) {
        int px = ML, py = yBase + MT, pw = WIDTH - ML - MR, ph = CHART_H;
        drawChartBackground(g, px, py, pw, ph, title, yLabel, yMin, yMax, 5);

        if (values.length == 0) return yBase + MT + CHART_H + MB;

        int n = values.length;
        int[] xPts = xPositions(px, pw, n);

        // overall average dashed line — label on the LEFT to stay clear of the legend
        double overallAvg = avg(values);
        int avgY = yPos(py, ph, overallAvg, yMin, yMax);
        g.setColor(new Color(160, 160, 160));
        g.setStroke(dashed(1.2f));
        g.drawLine(px, avgY, px + pw, avgY);
        g.setFont(new Font("SansSerif", Font.ITALIC, 10));
        g.setColor(new Color(110, 110, 110));
        String avgLbl = "avg=" + fmt(overallAvg);
        // position: just to the right of the Y-axis, above the line if near bottom else below
        int avgLblY = (avgY > py + ph - 16) ? avgY - 4 : avgY + 12;
        g.drawString(avgLbl, px + 6, avgLblY);

        if (n <= 60) {
            // Small dataset — classic line + dots
            int[] y = yPositions(py, ph, values, yMin, yMax);
            g.setColor(color);
            g.setStroke(solid(2.0f));
            for (int i = 0; i < n - 1; i++) g.drawLine(xPts[i], y[i], xPts[i + 1], y[i + 1]);
            g.setStroke(solid(1.5f));
            for (int i = 0; i < n; i++) {
                g.setColor(Color.WHITE);
                g.fillOval(xPts[i] - 4, y[i] - 4, 8, 8);
                g.setColor(color);
                g.drawOval(xPts[i] - 4, y[i] - 4, 8, 8);
            }
        } else {
            // Large dataset — band chart
            int window = Math.max(5, n / 25);
            double[] rollMean = rollingAvg(values, window);
            double[] rollStd  = rollingStd(values, rollMean, window);

            double[] upper = new double[n];
            double[] lower = new double[n];
            for (int i = 0; i < n; i++) {
                upper[i] = Math.min(yMax, rollMean[i] + rollStd[i]);
                lower[i] = Math.max(yMin, rollMean[i] - rollStd[i]);
            }

            // filled ±1σ band
            int[] upperY = yPositions(py, ph, upper, yMin, yMax);
            int[] lowerY = yPositions(py, ph, lower, yMin, yMax);
            int[] polyX  = new int[2 * n];
            int[] polyY  = new int[2 * n];
            for (int i = 0; i < n; i++) {
                polyX[i]           = xPts[i];      polyY[i]           = upperY[i];
                polyX[2*n-1-i]     = xPts[i];      polyY[2*n-1-i]     = lowerY[i];
            }
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 45));
            g.fillPolygon(polyX, polyY, 2 * n);

            // raw data — very faint thin line, no dots
            int[] rawY = yPositions(py, ph, values, yMin, yMax);
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
            g.setStroke(solid(0.8f));
            for (int i = 0; i < n - 1; i++) g.drawLine(xPts[i], rawY[i], xPts[i + 1], rawY[i + 1]);

            // rolling mean — bold solid
            int[] meanY = yPositions(py, ph, rollMean, yMin, yMax);
            g.setColor(color);
            g.setStroke(solid(2.5f));
            for (int i = 0; i < n - 1; i++) g.drawLine(xPts[i], meanY[i], xPts[i + 1], meanY[i + 1]);

            // legend — bottom-right so it never collides with the avg label or band
            int lx = px + pw - 220, ly = py + ph - 36;
            // semi-transparent white background
            g.setColor(new Color(255, 255, 255, 180));
            g.fillRoundRect(lx - 4, ly - 2, 218, 32, 6, 6);
            g.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 110));
            g.fillRect(lx, ly + 2, 14, 10);
            g.setColor(color);
            g.fillRect(lx, ly + 20, 14, 3);
            g.setColor(AXIS_CLR);
            g.drawString("±1σ band  (window=" + window + ")", lx + 18, ly + 11);
            g.drawString("Rolling mean", lx + 18, ly + 23);
        }

        drawXAxisLabels(g, px, py, ph, pw, xPts, n, "Simulation #");
        return yBase + MT + CHART_H + MB;
    }

    // -------------------------------------------------------------------------
    // 3. Histogram
    // -------------------------------------------------------------------------

    private static int drawHistogram(Graphics2D g, int yBase, double[] values,
                                     String title, String xLabel,
                                     double vMin, double vMax, int buckets, Color color) {
        int[] counts = new int[buckets];
        for (double v : values) {
            int b = (int) ((v - vMin) / (vMax - vMin) * buckets);
            counts[Math.min(b, buckets - 1)]++;
        }
        int maxCount = 0;
        for (int c : counts) maxCount = Math.max(maxCount, c);
        if (maxCount == 0) return yBase + MT + CHART_H + MB;

        int px = ML, py = yBase + MT, pw = WIDTH - ML - MR, ph = CHART_H;

        drawChartBackground(g, px, py, pw, ph, title, "Count", 0, maxCount, 5);

        int barW = pw / buckets;
        int pad  = Math.max(2, barW / 8);

        for (int i = 0; i < buckets; i++) {
            int barH = (int) ((double) counts[i] / maxCount * ph);
            int bx = px + i * barW + pad;
            int by = py + ph - barH;

            g.setColor(color);
            g.fillRect(bx, by, barW - 2 * pad, barH);
            g.setColor(color.darker());
            g.setStroke(solid(1.0f));
            g.drawRect(bx, by, barW - 2 * pad, barH);

            if (counts[i] > 0) {
                g.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g.setColor(TEXT_DARK);
                String lbl = String.valueOf(counts[i]);
                int lw = g.getFontMetrics().stringWidth(lbl);
                g.drawString(lbl, bx + (barW - 2 * pad) / 2 - lw / 2, by - 3);
            }

            g.setFont(new Font("SansSerif", Font.PLAIN, 9));
            g.setColor(AXIS_CLR);
            double lo = vMin + i * (vMax - vMin) / buckets;
            String bl = fmt(lo);
            g.drawString(bl, bx, py + ph + 14);
        }

        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.setColor(AXIS_CLR);
        g.drawString(xLabel, px + pw / 2 - g.getFontMetrics().stringWidth(xLabel) / 2, py + ph + 32);
        return yBase + MT + CHART_H + MB;
    }

    // -------------------------------------------------------------------------
    // 4. Scatter – Satisfaction vs Path Length
    // -------------------------------------------------------------------------

    private static int drawScatter(Graphics2D g, int yBase,
                                   double[] pathLengths, double[] satisfactions,
                                   double xMax, double yMin, double yMax) {
        int n = Math.min(pathLengths.length, satisfactions.length);
        if (n == 0) return yBase + MT + CHART_H + MB;

        int px = ML, py = yBase + MT, pw = WIDTH - ML - MR, ph = CHART_H;

        drawChartBackground(g, px, py, pw, ph,
                "Scatter — Satisfaction vs Path Length", "Satisfaction",
                yMin, yMax, 5);

        // X grid + labels
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        for (int i = 0; i <= 5; i++) {
            double val = xMax * i / 5;
            int gx = px + (int) (val / xMax * pw);
            g.setColor(GRID_CLR);
            g.setStroke(solid(1.0f));
            g.drawLine(gx, py, gx, py + ph);
            g.setColor(AXIS_CLR);
            String lbl = fmt(val);
            g.drawString(lbl, gx - g.getFontMetrics().stringWidth(lbl) / 2, py + ph + 14);
        }
        String xLbl = "Path Length";
        g.drawString(xLbl, px + pw / 2 - g.getFontMetrics().stringWidth(xLbl) / 2, py + ph + 32);

        // dots
        for (int i = 0; i < n; i++) {
            int dx = px + (xMax == 0 ? pw / 2 : (int) (pathLengths[i] / xMax * pw));
            int dy = yPos(py, ph, satisfactions[i], yMin, yMax);
            dx = Math.max(px + 2, Math.min(px + pw - 2, dx));
            dy = Math.max(py + 2, Math.min(py + ph - 2, dy));

            g.setColor(new Color(C_BLUE.getRed(), C_BLUE.getGreen(), C_BLUE.getBlue(), 160));
            g.fillOval(dx - 5, dy - 5, 10, 10);
            g.setColor(C_BLUE.darker());
            g.setStroke(solid(1.0f));
            g.drawOval(dx - 5, dy - 5, 10, 10);
        }

        return yBase + MT + CHART_H + MB;
    }

    // -------------------------------------------------------------------------
    // 5. Energy bar chart
    // -------------------------------------------------------------------------

    private static int drawEnergyBar(Graphics2D g, int yBase, List<Outcome> outcomes) {
        double cl = 0, mal = 0, ben = 0, rel = 0, avg = 0, pre = 0, pow = 0;
        int cnt = 0;
        for (Outcome o : outcomes) {
            if (!(o instanceof EnergyConsumptionOutcome)) continue;
            EnergyConsumptionOutcome e = (EnergyConsumptionOutcome) o;
            cl  += e.get_clientEnergyConsumption();
            mal += e.get_maliciousServerEnergyConsumption();
            ben += e.get_benevolentServerEnergyConsumption();
            rel += e.get_relayServerEnergyConsumption();
            avg += e.get_avgSensorEnergyConsumption();
            if (o instanceof EigenTrustEnergyConsumptionOutcome)
                pre += ((EigenTrustEnergyConsumptionOutcome) o).get_preTrustedPeerEnergyConsumption();
            if (o instanceof PowerTrustEnergyConsumptionOutcome)
                pow += ((PowerTrustEnergyConsumptionOutcome) o).get_powerNodeEnergyConsumption();
            cnt++;
        }
        if (cnt == 0) return yBase + MT + CHART_H + MB;

        List<String> lbls = new ArrayList<>();
        List<Double> vals = new ArrayList<>();
        List<Color>  cols = new ArrayList<>();
        addBar(lbls, vals, cols, "Client",     cl  / cnt, C_BLUE);
        addBar(lbls, vals, cols, "Malicious",  mal / cnt, C_RED);
        addBar(lbls, vals, cols, "Benevolent", ben / cnt, C_GREEN);
        addBar(lbls, vals, cols, "Relay",      rel / cnt, C_ORANGE);
        addBar(lbls, vals, cols, "Avg Sensor", avg / cnt, C_PURPLE);
        if (pre > 0) addBar(lbls, vals, cols, "Pre-Trusted", pre / cnt, new Color(0, 188, 212));
        if (pow > 0) addBar(lbls, vals, cols, "Power Node",  pow / cnt, new Color(255, 87, 34));

        double maxVal = 0;
        for (double v : vals) maxVal = Math.max(maxVal, v);
        if (maxVal == 0) return yBase + MT + CHART_H + MB;

        int n  = vals.size();
        int px = ML, py = yBase + MT, pw = WIDTH - ML - MR, ph = CHART_H;

        drawChartBackground(g, px, py, pw, ph,
                "Average Energy Consumption by Node Type", "Energy",
                0, maxVal, 5);

        int barGroupW = pw / n;
        int pad = Math.max(4, barGroupW / 6);
        int barW = barGroupW - 2 * pad;

        for (int i = 0; i < n; i++) {
            int barH = (int) (vals.get(i) / maxVal * ph);
            int bx = px + i * barGroupW + pad;
            int by = py + ph - barH;

            g.setColor(cols.get(i));
            g.fillRect(bx, by, barW, barH);
            g.setColor(cols.get(i).darker());
            g.setStroke(solid(1.0f));
            g.drawRect(bx, by, barW, barH);

            g.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g.setColor(TEXT_DARK);
            String vl = fmtEnergy(vals.get(i));
            int vw = g.getFontMetrics().stringWidth(vl);
            g.drawString(vl, bx + barW / 2 - vw / 2, by - 3);

            g.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g.setColor(AXIS_CLR);
            int lw = g.getFontMetrics().stringWidth(lbls.get(i));
            g.drawString(lbls.get(i), bx + barW / 2 - lw / 2, py + ph + 15);
        }

        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.drawString("Node Type", px + pw / 2 - g.getFontMetrics().stringWidth("Node Type") / 2, py + ph + 32);
        return yBase + MT + CHART_H + MB;
    }

    // -------------------------------------------------------------------------
    // 6. Fuzzy stacked bar (LFTM)
    // -------------------------------------------------------------------------

    private static void drawFuzzyStackedBar(Graphics2D g, int yBase, List<Outcome> outcomes) {
        List<FuzzyOutcome> fuzzy = new ArrayList<>();
        for (Outcome o : outcomes) if (o instanceof FuzzyOutcome) fuzzy.add((FuzzyOutcome) o);
        if (fuzzy.isEmpty()) return;

        int n  = fuzzy.size();
        int px = ML, py = yBase + MT, pw = WIDTH - ML - MR, ph = CHART_H;

        g.setColor(BG_CHART);
        g.fillRect(px, py, pw, ph);
        g.setColor(TEXT_DARK);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("Fuzzy Satisfaction Distribution per Simulation Run (% of clients)", px, yBase + MT - 12);

        FontMetrics fm = g.getFontMetrics(new Font("SansSerif", Font.PLAIN, 11));
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        for (int i = 0; i <= 5; i++) {
            int val  = i * 20;
            int gridY = py + ph - (int) ((double) val / 100 * ph);
            g.setColor(GRID_CLR);
            g.setStroke(solid(1.0f));
            g.drawLine(px, gridY, px + pw, gridY);
            g.setColor(AXIS_CLR);
            String lbl = val + "%";
            g.drawString(lbl, px - fm.stringWidth(lbl) - 6, gridY + 4);
        }
        drawRotatedLabel(g, "Clients (%)", px - 60, py + ph / 2);

        g.setColor(AXIS_CLR);
        g.setStroke(solid(1.5f));
        g.drawRect(px, py, pw, ph);

        int barGroupW = pw / n;
        int pad = Math.max(2, barGroupW / 8);
        int barW = barGroupW - 2 * pad;

        for (int i = 0; i < n; i++) {
            FuzzyOutcome fo = fuzzy.get(i);
            int bx = px + i * barGroupW + pad;
            int stackY = py + ph;

            for (int t = 0; t < FUZZY_TERMS.length; t++) {
                double pct = fo.getSatisfactionPercentage(FUZZY_TERMS[t]);
                int segH = (int) (pct * ph);
                if (segH == 0) continue;
                stackY -= segH;
                g.setColor(FUZZY_COLORS[t]);
                g.fillRect(bx, stackY, barW, segH);
                g.setColor(FUZZY_COLORS[t].darker());
                g.setStroke(solid(0.5f));
                g.drawRect(bx, stackY, barW, segH);

                if (segH > 14) {
                    g.setFont(new Font("SansSerif", Font.PLAIN, 9));
                    g.setColor(Color.WHITE);
                    String pctLbl = String.format("%.0f%%", pct * 100);
                    int lw = g.getFontMetrics().stringWidth(pctLbl);
                    if (lw < barW - 2) g.drawString(pctLbl, bx + barW / 2 - lw / 2, stackY + segH / 2 + 4);
                }
            }

            g.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g.setColor(AXIS_CLR);
            String xl = String.valueOf(i + 1);
            g.drawString(xl, bx + barW / 2 - g.getFontMetrics().stringWidth(xl) / 2, py + ph + 15);
        }

        g.drawString("Simulation #", px + pw / 2 - g.getFontMetrics().stringWidth("Simulation #") / 2, py + ph + 32);

        int lx = px + pw - 160, ly = py + 10;
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        for (int t = 0; t < FUZZY_TERMS.length; t++) {
            g.setColor(FUZZY_COLORS[t]);
            g.fillRect(lx, ly + t * 18, 12, 12);
            g.setColor(AXIS_CLR);
            g.drawString(FUZZY_TERMS[t], lx + 16, ly + t * 18 + 11);
        }
    }

    // -------------------------------------------------------------------------
    // Shared drawing helpers
    // -------------------------------------------------------------------------

    private static void drawChartBackground(Graphics2D g, int px, int py, int pw, int ph,
                                            String title, String yLabel,
                                            double yMin, double yMax, int gridLines) {
        g.setColor(BG_CHART);
        g.fillRect(px, py, pw, ph);

        g.setColor(TEXT_DARK);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString(title, px, py - 12);

        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        FontMetrics fm = g.getFontMetrics();
        for (int i = 0; i <= gridLines; i++) {
            double val  = yMin + (yMax - yMin) * i / gridLines;
            int    gridY = py + ph - i * ph / gridLines;
            g.setColor(GRID_CLR);
            g.setStroke(solid(1.0f));
            g.drawLine(px, gridY, px + pw, gridY);
            g.setColor(AXIS_CLR);
            String lbl = fmtAxis(val);
            g.drawString(lbl, px - fm.stringWidth(lbl) - 6, gridY + 4);
        }

        g.setColor(AXIS_CLR);
        g.setStroke(solid(1.5f));
        g.drawRect(px, py, pw, ph);

        drawRotatedLabel(g, yLabel, px - 60, py + ph / 2);
    }

    private static void drawXAxisLabels(Graphics2D g, int px, int py, int ph, int pw,
                                        int[] xPos, int n, String label) {
        Font f = new Font("SansSerif", Font.PLAIN, 11);
        g.setFont(f);
        g.setColor(AXIS_CLR);
        FontMetrics fm = g.getFontMetrics(f);

        // widest possible label + 6 px padding on each side
        int maxLblW = fm.stringWidth(String.valueOf(n)) + 12;
        double pxPerPt = n > 1 ? (double) pw / (n - 1) : pw;
        int step = Math.max(1, (int) Math.ceil(maxLblW / pxPerPt));

        int lastRightEdge = Integer.MIN_VALUE;
        for (int i = 0; i < n; i += step) {
            String lbl = String.valueOf(i + 1);
            int lw  = fm.stringWidth(lbl);
            int lx  = xPos[i] - lw / 2;
            if (lx > lastRightEdge) {
                g.drawString(lbl, lx, py + ph + 15);
                lastRightEdge = lx + lw + 6;
            }
        }
        // always attempt to draw the last value
        if (n > 1) {
            String lbl = String.valueOf(n);
            int lw = fm.stringWidth(lbl);
            int lx = xPos[n - 1] - lw / 2;
            if (lx > lastRightEdge) {
                g.drawString(lbl, lx, py + ph + 15);
            }
        }

        g.drawString(label, px + pw / 2 - fm.stringWidth(label) / 2, py + ph + 32);
    }

    private static void drawRotatedLabel(Graphics2D g, String text, int cx, int cy) {
        Graphics2D gr = (Graphics2D) g.create();
        gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gr.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gr.setColor(new Color(80, 85, 90));
        gr.rotate(-Math.PI / 2, cx, cy);
        gr.drawString(text, cx - gr.getFontMetrics().stringWidth(text) / 2, cy + 4);
        gr.dispose();
    }

    // -------------------------------------------------------------------------
    // Data helpers
    // -------------------------------------------------------------------------

    private static String resolveModelName(List<Outcome> outcomes) {
        Set<String> names = new LinkedHashSet<>();
        for (Outcome o : outcomes) {
            String n = o.getModelName();
            if (n != null && !n.isEmpty()) names.add(n);
        }
        return String.join(" / ", names);
    }

    private static double[] extractSatisfactions(List<Outcome> outcomes) {
        double[] r = new double[outcomes.size()];
        for (int i = 0; i < outcomes.size(); i++) {
            Outcome o = outcomes.get(i);
            r[i] = (o instanceof BasicOutcome)
                    ? ((BasicOutcome) o).get_avgSatisfaction()
                    : (o.get_satisfaction().isSatisfied() ? 1.0 : 0.0);
        }
        return r;
    }

    private static double[] extractPathLengths(List<Outcome> outcomes) {
        double[] r = new double[outcomes.size()];
        for (int i = 0; i < outcomes.size(); i++) {
            Outcome o = outcomes.get(i);
            if (o instanceof BasicOutcome) r[i] = ((BasicOutcome) o).get_avgPathLength();
        }
        return r;
    }

    private static double[] rollingAvg(double[] vals, int window) {
        double[] result = new double[vals.length];
        for (int i = 0; i < vals.length; i++) {
            int lo = Math.max(0, i - window + 1);
            double sum = 0;
            for (int j = lo; j <= i; j++) sum += vals[j];
            result[i] = sum / (i - lo + 1);
        }
        return result;
    }

    private static double[] rollingStd(double[] vals, double[] rollMean, int window) {
        double[] result = new double[vals.length];
        for (int i = 0; i < vals.length; i++) {
            int lo  = Math.max(0, i - window + 1);
            int cnt = i - lo + 1;
            double sumSq = 0;
            for (int j = lo; j <= i; j++) {
                double d = vals[j] - rollMean[i];
                sumSq += d * d;
            }
            result[i] = cnt > 1 ? Math.sqrt(sumSq / cnt) : 0;
        }
        return result;
    }

    private static boolean hasEnergyData(List<Outcome> outcomes) {
        for (Outcome o : outcomes) if (o instanceof EnergyConsumptionOutcome) return true;
        return false;
    }

    private static boolean hasFuzzyData(List<Outcome> outcomes) {
        for (Outcome o : outcomes) if (o instanceof FuzzyOutcome) return true;
        return false;
    }

    private static boolean hasBasicData(List<Outcome> outcomes) {
        for (Outcome o : outcomes) if (o instanceof BasicOutcome) return true;
        return false;
    }

    private static void addBar(List<String> lbls, List<Double> vals, List<Color> cols,
                               String lbl, double val, Color col) {
        lbls.add(lbl); vals.add(val); cols.add(col);
    }

    // -------------------------------------------------------------------------
    // Adaptive axis range
    // -------------------------------------------------------------------------

    private static double adaptiveMin(double[] vals, double absoluteMin) {
        if (vals.length == 0) return absoluteMin;
        double min = vals[0];
        for (double v : vals) min = Math.min(min, v);
        double padding = Math.max(0.01, (1.0 - min) * 0.1);
        return Math.max(absoluteMin, roundDown(min - padding, 3));
    }

    private static double adaptiveMax(double[] vals, double absoluteMax) {
        if (vals.length == 0) return absoluteMax;
        double max = vals[0];
        for (double v : vals) max = Math.max(max, v);
        double padding = Math.max(0.01, (max - 0.0) * 0.05);
        return Math.min(absoluteMax, roundUp(max + padding, 3));
    }

    private static double roundDown(double v, int decimals) {
        double factor = Math.pow(10, decimals);
        return Math.floor(v * factor) / factor;
    }

    private static double roundUp(double v, int decimals) {
        double factor = Math.pow(10, decimals);
        return Math.ceil(v * factor) / factor;
    }

    // -------------------------------------------------------------------------
    // Geometry helpers
    // -------------------------------------------------------------------------

    private static int[] xPositions(int px, int pw, int n) {
        int[] x = new int[n];
        for (int i = 0; i < n; i++)
            x[i] = px + (n == 1 ? pw / 2 : i * pw / (n - 1));
        return x;
    }

    private static int[] yPositions(int py, int ph, double[] vals, double yMin, double yMax) {
        int[] y = new int[vals.length];
        for (int i = 0; i < vals.length; i++) y[i] = yPos(py, ph, vals[i], yMin, yMax);
        return y;
    }

    private static int yPos(int py, int ph, double val, double yMin, double yMax) {
        double clamped = Math.max(yMin, Math.min(yMax, val));
        return py + ph - (int) ((clamped - yMin) / (yMax - yMin) * ph);
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    private static Graphics2D makeG(BufferedImage img) {
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);
        return g;
    }

    private static BasicStroke solid(float w)  { return new BasicStroke(w); }

    private static BasicStroke dashed(float w) {
        return new BasicStroke(w, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[]{7, 5}, 0);
    }

    private static double avg(double[] v) {
        if (v.length == 0) return 0;
        double s = 0;
        for (double x : v) s += x;
        return s / v.length;
    }

    private static double maxOf(double[] v, double min) {
        double m = min;
        for (double x : v) m = Math.max(m, x);
        return m;
    }

    private static String fmt(double v) {
        if (Math.abs(v) >= 100) return String.format("%.0f", v);
        if (Math.abs(v) >= 10)  return String.format("%.1f", v);
        if (Math.abs(v) >= 1)   return String.format("%.2f", v);
        return String.format("%.3f", v);
    }

    private static String fmtAxis(double v) {
        if (Math.abs(v) >= 1000) return String.format("%.0f", v);
        if (Math.abs(v) >= 100)  return String.format("%.0f", v);
        if (Math.abs(v) >= 10)   return String.format("%.1f", v);
        if (Math.abs(v) >= 1)    return String.format("%.2f", v);
        return String.format("%.3f", v);
    }

    private static String fmtEnergy(double v) {
        if (v == 0) return "0";
        if (Math.abs(v) < 0.001 || Math.abs(v) > 999999) return String.format("%.2e", v);
        if (Math.abs(v) < 1.0)  return String.format("%.4f", v);
        if (Math.abs(v) < 100)  return String.format("%.2f", v);
        return String.format("%.0f", v);
    }
}
