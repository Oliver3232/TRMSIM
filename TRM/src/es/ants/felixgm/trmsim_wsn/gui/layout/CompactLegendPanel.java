package es.ants.felixgm.trmsim_wsn.gui.layout;


import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class CompactLegendPanel extends JPanel {
    private List<MiniLegendPanel.Item> items = new ArrayList<MiniLegendPanel.Item>();

    public CompactLegendPanel() {
        setOpaque(false);
        setLayout(new WrapLayout(WrapLayout.LEFT, 6, 6));
    }

    public void setItems(List<MiniLegendPanel.Item> items) {
        this.items = new ArrayList<MiniLegendPanel.Item>(items);
        rebuild();
    }

    private void rebuild() {
        removeAll();
        for (MiniLegendPanel.Item item : items) {
            add(createChip(item));
        }
        revalidate();
        repaint();
    }

    private JPanel createChip(MiniLegendPanel.Item item) {
        JPanel chip = new JPanel(new WrapLayout(WrapLayout.LEFT, 4, 0));
        chip.setOpaque(true);
        chip.setBackground(new Color(247, 250, 255));
        chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 236), 1, true),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));

        JLabel swatch = new JLabel();
        swatch.setOpaque(true);
        swatch.setBackground(item.color);
        swatch.setPreferredSize(new Dimension(10, 10));
        swatch.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 220), 1, true));

        JLabel text = new JLabel(item.label);
        text.setForeground(new Color(42, 56, 79));

        chip.add(swatch);
        chip.add(Box.createHorizontalStrut(2));
        chip.add(text);
        return chip;
    }
}
