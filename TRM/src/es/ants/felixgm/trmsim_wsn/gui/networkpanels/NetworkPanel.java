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

package es.ants.felixgm.trmsim_wsn.gui.networkpanels;

import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Sensor;
import es.ants.felixgm.trmsim_wsn.network.Service;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

/**
 * <p>This class implements a panel for plotting a network</p>
 * @author <a href="http://ants.dif.um.es/~felixgm/en" target="_blank">F&eacute;lix G&oacute;mez M&aacute;rmol</a>, <a href="http://webs.um.es/gregorio" target="_blank">Gregorio Mart&iacute;nez P&eacute;rez</a>
 * @version 0.5
 * @since 0.4
 */
public class NetworkPanel extends javax.swing.JPanel {
    private static final Stroke AXIS_STROKE = new BasicStroke(1.2f);
    private static final Stroke GRID_STROKE = new BasicStroke(0.8f);
    private static final Stroke LINK_STROKE = new BasicStroke(1.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final Stroke RANGE_STROKE = new BasicStroke(1.0f);
    private static final Color RANGE_COLOR = new Color(90, 130, 210, 65);
    private static final Color NODE_STROKE_COLOR = new Color(32, 42, 52, 170);
    private static final Font AXIS_FONT = new Font("SansSerif", Font.PLAIN, 11);

    protected double axesMargin = 0.06;
    protected double xOrigin = 0.0;
    protected double yOrigin = 0.0;
    protected double xAxisLength = 100.0;
    protected double yAxisLength = 100.0;
    protected int numTicks = 10;

    /** Background color */
    protected Color backgroundColor = Color.WHITE;
    /** Clients color */
    protected Color clientColor = Color.ORANGE;
    /** Benevolent servers color */
    protected Color benevolentServerColor = Color.GREEN;
    /** Malicious servers color */
    protected Color maliciousServerColor = Color.RED;
    /** Relay servers color */
    protected Color relayServerColor = Color.BLUE;
    /** Idle clients color */
    protected Color idleClientColor = Color.GRAY;
    /** Idle servers color */
    protected Color idleServerColor = Color.DARK_GRAY;
    /** Links color */
    protected Color linksColor = Color.GRAY;
    /** Color used to plot the axes  */
    protected Color axesColor = Color.black;
    /** Color used to plot the grid  */
    protected Color gridColor = Color.LIGHT_GRAY;

    /** Network to be plotted */
    protected Network network;
    /** Service requested by the clients of the network */
    protected Service requiredService;
    /** Sensors radio range */
    protected double radioRange;
    /** Indicates whether to plot sensors radio ranges or not */
    protected boolean showRanges;
    /** Indicates whether to plot links between sensors or not */
    protected boolean showLinks;
    /** Indicates whether to plot sensors identifiers or not */
    protected boolean showIds;
    /** Indicates whether to plot a grid or not */
    protected boolean showGrid;

    /** Creates new form NetworkPanel */
    public NetworkPanel() {
        initComponents();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        try {
            paintNetwork(network, requiredService, radioRange, showRanges, showLinks, showIds, showGrid, graphics);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method plots a Wireless Sensor Network
     * @param network Wireless Sensor Network to be plotted
     * @param requiredService Service requested by the clients (needed in order to paint and
     * distinguish benevolent and malicious servers)
     * @param radioRange Sensors radio range
     * @param showRanges Indicates whether to plot sensors radio ranges or not
     * @param showLinks Indicates whether to plot links between sensors or not
     * @param showIds Indicates whether to plot sensors identifiers or not
     * @param showGrid Indicates whether to plot a grid or not
     * @throws Exception If any error occurs while plotting a WSN
     */
    public void paintNetwork(Network network, Service requiredService,
            double radioRange, boolean showRanges, boolean showLinks,
            boolean showIds, boolean showGrid) throws Exception {
        this.network = network;
        this.requiredService = requiredService;
        this.radioRange = radioRange;
        this.showRanges = showRanges;
        this.showLinks = showLinks;
        this.showIds = showIds;
        this.showGrid = showGrid;
        repaint();
    }

    /**
     * This method plots a wireless sensor
     * @param sensor Wireless sensor to be plotted
     * @param color Color of the sensor to be plotted
     * @param graphics Graphic object where to plot the sensor
     */
    protected void paintSensor(Sensor sensor, Color color, Graphics graphics) {
        if (graphics == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) graphics;
        int height = this.getHeight();
        int width = this.getWidth();
        int radio = (int)(radioRange*Math.sqrt(Math.pow(width*(1.0-2*axesMargin), 2.0)+Math.pow(height*(1.0-2*axesMargin), 2.0)));

        int x = mapX(sensor.getX());
        int y = mapY(sensor.getY());
        int nodeRadius = getNodeRadius();

        g2.setColor(color);
        g2.fillOval(x-nodeRadius, y-nodeRadius, nodeRadius*2, nodeRadius*2);
        g2.setColor(NODE_STROKE_COLOR);
        g2.drawOval(x-nodeRadius, y-nodeRadius, nodeRadius*2, nodeRadius*2);

        if ((showRanges) && (radio > 0))
            drawRange(g2, x, y, radio);
        if ((showLinks) && (sensor.isActive())) {
            Stroke oldStroke = g2.getStroke();
            g2.setStroke(LINK_STROKE);
            g2.setColor(new Color(linksColor.getRed(), linksColor.getGreen(), linksColor.getBlue(), 145));
            for (Sensor neighbor : sensor.getNeighbors())
                if (neighbor.isActive()) {
                    int x1 = mapX(neighbor.getX());
                    int y1 = mapY(neighbor.getY());
                    g2.drawLine(x, y, x1, y1);
                    drawArrow(g2, x, y, x1, y1);
                }
            g2.setStroke(oldStroke);
        }
        if (showIds) {
            g2.setColor(new Color(38, 48, 64));
            g2.drawString(String.valueOf(sensor.id()), x + nodeRadius + 1, y - nodeRadius - 1);
        }
    }

    /** this method plots arrows Lefteris
     * 
     * 
     */
    private final int ARR_SIZE = 8;

    void drawArrow(Graphics g1, int x1, int y1, int x2, int y2) {
        Graphics2D g = (Graphics2D) g1.create();

        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx*dx + dy*dy);
        AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
        at.concatenate(AffineTransform.getRotateInstance(angle));
        g.transform(at);

        // Draw horizontal arrow starting in (0, 0)
        g.drawLine(0, 0, len, 0);
        g.fillPolygon(new int[] {len, len-ARR_SIZE, len-ARR_SIZE, len},
                      new int[] {0, -ARR_SIZE, ARR_SIZE, 0}, 4);
        g.dispose();
    }
    
    
    /**
     * This method plots a Wireless Sensor Network
     * @param network Wireless Sensor Network to be plotted
     * @param requiredService Service requested by the clients (needed in order to paint and
     * distinguish benevolent and malicious servers)
     * @param radioRange Sensors radio range
     * @param showRanges Indicates whether to plot sensors radio ranges or not
     * @param showLinks Indicates whether to plot links between sensors or not
     * @param showIds Indicates whether to plot sensors identifiers or not
     * @param showGrid Indicates whether to plot a grid or not
     * @param graphics Graphic object where to plot the wireless sensor network
     * @throws Exception If any error occurs while plotting a WSN
     */
    protected void paintNetwork(Network network, Service requiredService,
            double radioRange, boolean showRanges, boolean showLinks,
            boolean showIds, boolean showGrid, Graphics graphics) throws Exception {
        if (graphics == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int height = this.getHeight();
        int width = this.getWidth();
        Color sensorColor;

        g2.setColor(backgroundColor);
        g2.fillRect(0, 0, width, height);

        drawAxes(g2);
        if (showGrid)
            drawGrid(g2);

        if (network != null) {
            for (Sensor client : network.get_clients()) {
                if (client.isActive())
                    sensorColor = clientColor;
                else
                    sensorColor = idleClientColor;

                paintSensor(client,sensorColor,g2);
            }
            
            if (requiredService != null)
                for (Sensor server : network.get_servers()) {
                    sensorColor = relayServerColor;
                    if (!server.isActive())
                        sensorColor = idleServerColor;
                    else if (server.offersService(requiredService)) {
                        if (server.get_goodness(requiredService) >= 0.5)
                            sensorColor = benevolentServerColor;
                        else
                            sensorColor = maliciousServerColor;
                    }

                    paintSensor(server,sensorColor,g2);
                }
        }

        g2.dispose();
    }
    
    protected void drawGrid(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics;
        int height = this.getHeight();
        int width = this.getWidth();

        Stroke oldStroke = g2.getStroke();
        g2.setStroke(GRID_STROKE);
        g2.setColor(new Color(gridColor.getRed(), gridColor.getGreen(), gridColor.getBlue(), 165));

        for (int i = 1; i <= numTicks; i++) {
            //horizontal grid
            g2.drawLine((int)(width*axesMargin),(int)(height*(1.0-axesMargin)-height*(1.0-2*axesMargin)*(i/(double)numTicks)),(int)(width*(1.0-axesMargin)),(int)(height*(1.0-axesMargin)-height*(1.0-2*axesMargin)*(i/(double)numTicks)));
            //vertical grid
            g2.drawLine((int)(width*axesMargin+width*(1.0-2*axesMargin)*(i/(double)numTicks)),(int)(height*(1.0-axesMargin)),(int)(width*axesMargin+width*(1.0-2*axesMargin)*(i/(double)numTicks)),(int)(height*axesMargin));
        }
        g2.setStroke(oldStroke);
    }
    
    protected void drawAxes(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics;
        int height = this.getHeight();
        int width = this.getWidth();

        Stroke oldStroke = g2.getStroke();
        Font oldFont = g2.getFont();
        g2.setStroke(AXIS_STROKE);
        g2.setFont(AXIS_FONT);
        g2.setColor(new Color(axesColor.getRed(), axesColor.getGreen(), axesColor.getBlue(), 210));
        FontMetrics fm = g2.getFontMetrics();
        
        //X axis
        g2.drawLine((int)(width*axesMargin), (int)(height*(1-axesMargin)), (int)(width*(1.0-axesMargin)), (int)(height*(1.0-axesMargin)));
        for (int i = 0; i <= numTicks; i++) {
            int x = (int)(width*axesMargin+width*(1.0-2*axesMargin)*(i/(double)numTicks));
            g2.drawLine(x, (int)(height*(1.0-axesMargin))+4, x, (int)(height*(1.0-axesMargin))-4);
            String label = String.valueOf((int)((xOrigin+xAxisLength)*(i/(double)numTicks)));
            g2.drawString(label, x - (fm.stringWidth(label) / 2), (int)(height)-6);
        }
        
        //Y axis
        g2.drawLine((int)(width*axesMargin), (int)(height*(1-axesMargin)), (int)(width*axesMargin), (int)(height*axesMargin));
        for (int i = 0; i <= numTicks; i++) {
            int y = (int)(height*(1.0-axesMargin)-height*(1.0-2*axesMargin)*(i/(double)numTicks));
            g2.drawLine((int)(width*axesMargin)-4, y, (int)(width*axesMargin)+4, y);
            String label = String.valueOf((int)((xOrigin+xAxisLength)*(i/(double)numTicks)));
            g2.drawString(label, 3, y + (fm.getAscent() / 2) - 1);
        }

        g2.setStroke(oldStroke);
        g2.setFont(oldFont);
    }

    private int mapX(double x) {
        int width = this.getWidth();
        return (int)(width*axesMargin+(x/Network.get_maxDistance())*width*(1.0-2*axesMargin));
    }

    private int mapY(double y) {
        int height = this.getHeight();
        return (int)(height*(1-axesMargin)-(y/Network.get_maxDistance())*height*(1.0-2*axesMargin));
    }

    private int getNodeRadius() {
        int base = Math.min(this.getWidth(), this.getHeight());
        return Math.max(4, Math.min(9, base / 80));
    }

    private void drawRange(Graphics2D g2, int x, int y, int radio) {
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(RANGE_STROKE);
        g2.setColor(RANGE_COLOR);
        g2.drawArc(x-radio, y-radio, radio*2, radio*2, 0, 360);
        g2.setStroke(oldStroke);
    }
    
    public Point getCoordinateAtPosition(int x, int y) {
        int height = this.getHeight();
        int width = this.getWidth();

        int X = (int)Math.round((x - getBounds().getX() - width*axesMargin)/((width*(1.0-2*axesMargin))/(xAxisLength-xOrigin)));
        int Y = (int)Math.round((height*(1.0-axesMargin) -(y - getBounds().getY()))/((height*(1.0-2*axesMargin))/(yAxisLength-yOrigin)));

        return new Point(X,Y);
    }
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 398, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 298, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
