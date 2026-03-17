package es.ants.felixgm.trmsim_wsn.gui.export;

import es.ants.felixgm.trmsim_wsn.gui.outcomespanels.OutcomesPanel;
import java.awt.Component;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class GraphImageExporter {
    public static void exportCurrentGraph(Component parentFrame, OutcomesPanel outcomesPanel, String graphTitle) {
        if (outcomesPanel == null) {
            JOptionPane.showMessageDialog(parentFrame,
                    "No graph is available to export.",
                    "Export Failed",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            JFileChooser fileChooser = new JFileChooser("./simulation_results");
            fileChooser.setDialogTitle("Export Current Graph as PNG");
            fileChooser.setSelectedFile(new File(sanitizeFileName(graphTitle) + ".png"));
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".png");
                }

                public String getDescription() {
                    return "PNG Images (*.png)";
                }
            });

            if (fileChooser.showSaveDialog(parentFrame) == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filename.toLowerCase().endsWith(".png")) {
                    filename += ".png";
                }

                int width = outcomesPanel.getWidth();
                int height = outcomesPanel.getHeight();
                if (width <= 0 || height <= 0) {
                    Dimension preferred = outcomesPanel.getPreferredSize();
                    width = Math.max(preferred.width, 800);
                    height = Math.max(preferred.height, 400);
                }

                int scale = 2;
                int renderedWidth = width * scale;
                int renderedHeight = height * scale;

                BufferedImage chartImage = new BufferedImage(renderedWidth, renderedHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D chartGraphics = chartImage.createGraphics();
                chartGraphics.scale(scale, scale);
                chartGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                chartGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                outcomesPanel.paint(chartGraphics);
                chartGraphics.dispose();

                int topMargin = 72;
                int bottomMargin = 24;
                int leftMargin = 70;
                int rightMargin = 24;
                BufferedImage image = new BufferedImage(
                        renderedWidth + leftMargin + rightMargin,
                        renderedHeight + topMargin + bottomMargin,
                        BufferedImage.TYPE_INT_ARGB
                );
                Graphics2D graphics = image.createGraphics();
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                graphics.setColor(Color.WHITE);
                graphics.fillRect(0, 0, image.getWidth(), image.getHeight());

                graphics.setColor(new Color(30, 30, 30));
                graphics.setFont(new Font("SansSerif", Font.BOLD, 22));
                graphics.drawString(graphTitle, leftMargin, 32);

                graphics.setColor(new Color(90, 90, 90));
                graphics.setFont(new Font("SansSerif", Font.PLAIN, 13));
                graphics.drawString(
                        "Exported at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        leftMargin,
                        52
                );

                int chartX = leftMargin;
                int chartY = topMargin;
                graphics.drawImage(chartImage, chartX, chartY, null);

                graphics.setColor(new Color(120, 120, 120));
                graphics.drawRect(chartX, chartY, renderedWidth, renderedHeight);
                graphics.dispose();

                ImageIO.write(image, "png", new File(filename));
                JOptionPane.showMessageDialog(parentFrame,
                        "Graph successfully exported to: " + filename,
                        "Export Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame,
                    "Error during graph export: " + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private static String sanitizeFileName(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "outcomes_graph";
        }
        return value.replaceAll("[\\\\/:*?\"<>|\\s]+", "_").toLowerCase();
    }

}
