package es.ants.felixgm.trmsim_wsn.gui.layout;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

public final class MiniLegendPanel extends JPanel {
    public static final class Item {
        public final String label;
        public final Color color;

        public Item(String label, Color color) {
            this.label = label;
            this.color = color;
        }
    }

    private List<Item> items = new ArrayList<Item>();

    public MiniLegendPanel() {
        setOpaque(false);
    }

    public void setItems(List<Item> items) {
        this.items = new ArrayList<Item>(items);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Font labelFont = getFont().deriveFont(Font.BOLD, 11f);
        g2.setFont(labelFont);
        FontMetrics metrics = g2.getFontMetrics();

        int width = getWidth();
        int x = 6;
        int y = 8;
        int rowHeight = 18;
        for (Item item : items) {
            int itemWidth = metrics.stringWidth(item.label) + 20;
            if (x + itemWidth > width - 6) {
                x = 6;
                y += rowHeight;
            }
            g2.setColor(item.color);
            g2.fillOval(x, y + 3, 8, 8);
            g2.setColor(new Color(255, 255, 255, 210));
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawOval(x, y + 3, 8, 8);
            g2.setColor(new Color(49, 63, 84));
            g2.drawString(item.label, x + 14, y + 12);
            x += itemWidth + 10;
        }
        g2.dispose();
    }
}
