package es.ants.felixgm.trmsim_wsn.gui.networkpanels;

import es.ants.felixgm.trmsim_wsn.network.Network;
import es.ants.felixgm.trmsim_wsn.network.Sensor;
import es.ants.felixgm.trmsim_wsn.network.Service;
import java.awt.BorderLayout;
import java.awt.Color;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.animation.AnimationTimer;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.TextAlignment;
import javafx.geometry.VPos;

/**
 * JavaFX-based network renderer embedded in Swing via JFXPanel.
 * Keeps the same behavior as NetworkPanel but uses a modern FX canvas pipeline.
 */
public class JavaFXNetworkPanel extends NetworkPanel {
    private enum VisualTheme {
        FUTURISTIC, CLASSIC, WIREFRAME
    }

    private final JFXPanel fxPanel;
    private Canvas canvas;
    private final AtomicBoolean renderQueued = new AtomicBoolean(false);
    private volatile long frameNanos = 0L;
    private AnimationTimer animator;
    private volatile double viewScale = 1.0;
    private volatile double viewOffsetX = 0.0;
    private volatile double viewOffsetY = 0.0;
    private double dragStartX;
    private double dragStartY;
    private volatile boolean enable3DNavigation = false;
    private volatile VisualTheme visualTheme = VisualTheme.FUTURISTIC;
    private volatile double cameraYaw = 0.35;
    private volatile double cameraPitch = 0.25;
    private volatile double cameraDistance = 3.3;

    public JavaFXNetworkPanel() {
        super();
        Logger.getLogger("com.sun.javafx.application.PlatformImpl").setLevel(Level.SEVERE);
        fxPanel = new JFXPanel();
        removeAll();
        setLayout(new BorderLayout());
        add(fxPanel, BorderLayout.CENTER);
        initFxScene();
    }

    private void initFxScene() {
        Platform.runLater(() -> {
            canvas = new Canvas(640, 420);
            StackPane root = new StackPane(canvas);
            root.setStyle("-fx-background-color: #f3f7ff;");
            Scene scene = new Scene(root);
            fxPanel.setScene(scene);
            installInteractions();
            animator = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    frameNanos = now;
                    requestFxRender();
                }
            };
            animator.start();
            requestFxRender();
        });
    }

    private void installInteractions() {
        canvas.setOnScroll(event -> {
            if (enable3DNavigation && event.isAltDown()) {
                double delta = event.getDeltaY() > 0 ? -0.14 : 0.14;
                cameraDistance = clamp(cameraDistance + delta, 2.0, 6.0);
                requestFxRender();
                event.consume();
                return;
            }

            double zoomFactor = event.getDeltaY() > 0 ? 1.08 : 0.92;
            double oldScale = viewScale;
            double newScale = clamp(oldScale * zoomFactor, 0.45, 3.5);
            if (Math.abs(newScale - oldScale) >= 0.0001) {
                double mouseX = event.getX();
                double mouseY = event.getY();
                double baseX = (mouseX - viewOffsetX) / oldScale;
                double baseY = (mouseY - viewOffsetY) / oldScale;
                viewScale = newScale;
                viewOffsetX = mouseX - baseX * newScale;
                viewOffsetY = mouseY - baseY * newScale;
            }
            requestFxRender();
            event.consume();
        });

        canvas.setOnMousePressed(event -> {
            if ((event.getButton() == MouseButton.PRIMARY) || (event.getButton() == MouseButton.SECONDARY)) {
                dragStartX = event.getX();
                dragStartY = event.getY();
            }
        });

        canvas.setOnMouseDragged(event -> {
            double dx = event.getX() - dragStartX;
            double dy = event.getY() - dragStartY;
            dragStartX = event.getX();
            dragStartY = event.getY();

            if (enable3DNavigation && event.isSecondaryButtonDown()) {
                cameraYaw = cameraYaw + (dx * 0.006);
                cameraPitch = clamp(cameraPitch - (dy * 0.006), -1.2, 1.2);
                requestFxRender();
                return;
            }

            if (!event.isPrimaryButtonDown()) {
                return;
            }
            viewOffsetX += dx;
            viewOffsetY += dy;
            requestFxRender();
        });

        canvas.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                viewScale = 1.0;
                viewOffsetX = 0.0;
                viewOffsetY = 0.0;
                cameraYaw = 0.35;
                cameraPitch = 0.25;
                cameraDistance = 3.3;
                requestFxRender();
            }
        });
    }

    @Override
    protected void paintComponent(java.awt.Graphics graphics) {
        super.paintComponent(graphics);
        requestFxRender();
    }

    @Override
    public void paintNetwork(Network network, Service requiredService,
                             double radioRange, boolean showRanges, boolean showLinks,
                             boolean showIds, boolean showGrid) {
        this.network = network;
        this.requiredService = requiredService;
        this.radioRange = radioRange;
        this.showRanges = showRanges;
        this.showLinks = showLinks;
        this.showIds = showIds;
        this.showGrid = showGrid;
        requestFxRender();
    }

    public void setVisualTheme(String themeName) {
        if (themeName == null) {
            return;
        }
        if ("Classic".equalsIgnoreCase(themeName)) {
            visualTheme = VisualTheme.CLASSIC;
        } else if ("Wireframe".equalsIgnoreCase(themeName)) {
            visualTheme = VisualTheme.WIREFRAME;
        } else {
            visualTheme = VisualTheme.FUTURISTIC;
        }
        requestFxRender();
    }

    public void set3DNavigationEnabled(boolean enabled) {
        enable3DNavigation = enabled;
        requestFxRender();
    }

    public void applyCameraPreset(String presetName) {
        if (presetName == null) {
            return;
        }
        if ("Top".equalsIgnoreCase(presetName)) {
            cameraYaw = 0.0;
            cameraPitch = 1.12;
            cameraDistance = 3.55;
        } else if ("Front".equalsIgnoreCase(presetName)) {
            cameraYaw = 0.0;
            cameraPitch = 0.02;
            cameraDistance = 3.2;
        } else {
            cameraYaw = 0.35;
            cameraPitch = 0.25;
            cameraDistance = 3.3;
        }
        requestFxRender();
    }

    private void requestFxRender() {
        if (canvas == null) {
            return;
        }
        if (!renderQueued.compareAndSet(false, true)) {
            return;
        }

        Platform.runLater(() -> {
            try {
                if (canvas == null) {
                    return;
                }
                try {
                    renderSnapshot();
                } catch (Exception ex) {
                    GraphicsContext gc = canvas.getGraphicsContext2D();
                    gc.setFill(javafx.scene.paint.Color.rgb(32, 45, 68, 1.0));
                    gc.fillRect(0, 0, Math.max(10, canvas.getWidth()), Math.max(10, canvas.getHeight()));
                    gc.setFill(javafx.scene.paint.Color.rgb(235, 245, 255, 0.95));
                    gc.setFont(Font.font("SansSerif", 12));
                    gc.fillText("Graph renderer recovered from an internal update race.", 12, 24);
                }
            } finally {
                renderQueued.set(false);
            }
        });
    }

    private void renderSnapshot() {
        double w = Math.max(10, fxPanel.getWidth());
        double h = Math.max(10, fxPanel.getHeight());
        if (canvas.getWidth() != w) {
            canvas.setWidth(w);
        }
        if (canvas.getHeight() != h) {
            canvas.setHeight(h);
        }

        Network currentNetwork = this.network;
        Service currentService = this.requiredService;
        double currentRadio = this.radioRange;
        boolean currentShowRanges = this.showRanges;
        boolean currentShowLinks = this.showLinks;
        boolean currentShowIds = this.showIds;
        boolean currentShowGrid = this.showGrid;
        VisualTheme currentTheme = this.visualTheme;
        boolean use3D = this.enable3DNavigation;
        double pulse = 0.5 + 0.5 * Math.sin(frameNanos * 0.0000000027);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        paintBackground(gc, w, h, pulse, currentTheme);
        gc.setFontSmoothingType(FontSmoothingType.LCD);

        drawHudFrame(gc, w, h, currentTheme);
        drawAxes(gc, w, h);
        if (currentShowGrid) {
            drawGrid(gc, w, h);
        }

        if (currentNetwork == null) {
            return;
        }

        Collection<Sensor> clientsSnapshot = copySensors(currentNetwork.get_clients());
        for (Sensor client : clientsSnapshot) {
            Color color = client.isActive() ? clientColor : idleClientColor;
            color = adjustSpecialColor(client, color);
            drawSensor(gc, client, color, currentRadio, currentShowRanges, currentShowLinks, currentShowIds, w, h, pulse, currentTheme, use3D);
        }

        if (currentService != null) {
            Collection<Sensor> serversSnapshot = copySensors(currentNetwork.get_servers());
            for (Sensor server : serversSnapshot) {
                Color sensorColor = relayServerColor;
                if (!server.isActive()) {
                    sensorColor = idleServerColor;
                } else if (server.offersService(currentService)) {
                    try {
                        sensorColor = (server.get_goodness(currentService) >= 0.5) ? benevolentServerColor : maliciousServerColor;
                    } catch (Exception ignored) {
                        sensorColor = relayServerColor;
                    }
                }
                sensorColor = adjustSpecialColor(server, sensorColor);
                drawSensor(gc, server, sensorColor, currentRadio, currentShowRanges, currentShowLinks, currentShowIds, w, h, pulse, currentTheme, use3D);
            }
        }

        if (use3D) {
            draw3DHint(gc, w, h, currentTheme);
        }
    }

    private void drawSensor(GraphicsContext gc, Sensor sensor, Color sensorColor,
                            double currentRadio, boolean currentShowRanges, boolean currentShowLinks, boolean currentShowIds,
                            double w, double h, double pulse, VisualTheme theme, boolean use3D) {
        ProjectedPoint sourcePoint = projectPoint(sensor, w, h, use3D);
        double x = sourcePoint.x;
        double y = sourcePoint.y;
        double nodeRadius = getNodeRadius(w, h) * sourcePoint.depthScale;
        double radio = currentRadio * Math.sqrt(Math.pow(w * (1.0 - 2 * axesMargin), 2.0) + Math.pow(h * (1.0 - 2 * axesMargin), 2.0));

        if (currentShowRanges && radio > 0.0) {
            gc.setStroke((theme == VisualTheme.CLASSIC)
                    ? javafx.scene.paint.Color.rgb(90, 90, 90, 0.22)
                    : javafx.scene.paint.Color.rgb(64, 145, 255, 0.24));
            gc.setLineWidth(1.0);
            double scaledRadio = radio * viewScale * sourcePoint.depthScale;
            gc.strokeOval(x - scaledRadio, y - scaledRadio, scaledRadio * 2.0, scaledRadio * 2.0);
        }

        if (currentShowLinks && sensor.isActive()) {
            if (theme == VisualTheme.FUTURISTIC) {
                gc.setStroke(javafx.scene.paint.Color.rgb(60, 210, 240, 0.18));
                gc.setLineWidth(2.2);
            } else if (theme == VisualTheme.WIREFRAME) {
                gc.setStroke(javafx.scene.paint.Color.rgb(100, 255, 255, 0.35));
                gc.setLineWidth(1.3);
            } else {
                gc.setStroke(javafx.scene.paint.Color.rgb(130, 130, 130, 0.28));
                gc.setLineWidth(1.4);
            }
            Collection<Sensor> neighborsSnapshotPrimary = copySensors(sensor.getNeighbors());
            for (Sensor neighbor : neighborsSnapshotPrimary) {
                if (!neighbor.isActive()) {
                    continue;
                }
                ProjectedPoint p1 = projectPoint(neighbor, w, h, use3D);
                double x1 = p1.x;
                double y1 = p1.y;
                gc.strokeLine(x, y, x1, y1);
                if (theme == VisualTheme.FUTURISTIC) {
                    drawDataTrail(gc, sensor.id(), neighbor.id(), x, y, x1, y1, pulse);
                }
            }
            if (theme == VisualTheme.CLASSIC) {
                gc.setStroke(javafx.scene.paint.Color.rgb(70, 70, 70, 0.75));
            } else {
                gc.setStroke(javafx.scene.paint.Color.rgb(90, 240, 255, 0.72));
            }
            gc.setLineWidth(theme == VisualTheme.WIREFRAME ? 0.95 : 1.05);
            Collection<Sensor> neighborsSnapshotSecondary = copySensors(sensor.getNeighbors());
            for (Sensor neighbor : neighborsSnapshotSecondary) {
                if (!neighbor.isActive()) {
                    continue;
                }
                ProjectedPoint p1 = projectPoint(neighbor, w, h, use3D);
                double x1 = p1.x;
                double y1 = p1.y;
                gc.strokeLine(x, y, x1, y1);
                drawArrow(gc, x, y, x1, y1);
            }
        }

        if (theme == VisualTheme.FUTURISTIC) {
            double halo = nodeRadius + 5.0 + pulse * 2.5;
            gc.setFill(javafx.scene.paint.Color.rgb(70, 210, 255, sensor.isActive() ? 0.15 : 0.08));
            gc.fillOval(x - halo, y - halo, halo * 2.0, halo * 2.0);

            javafx.scene.paint.Color core = toFx(sensorColor, 0.95);
            javafx.scene.paint.Color edge = toFx(sensorColor.darker(), 0.95);
            gc.setFill(new RadialGradient(
                    0, 0, x - nodeRadius * 0.25, y - nodeRadius * 0.25, nodeRadius * 1.15,
                    false, CycleMethod.NO_CYCLE,
                    new Stop(0.0, core.brighter()),
                    new Stop(0.7, core),
                    new Stop(1.0, edge)
            ));
            gc.fillOval(x - nodeRadius, y - nodeRadius, nodeRadius * 2.0, nodeRadius * 2.0);
            gc.setStroke(javafx.scene.paint.Color.rgb(210, 248, 255, 0.8));
            gc.setLineWidth(1.15);
            gc.strokeOval(x - nodeRadius, y - nodeRadius, nodeRadius * 2.0, nodeRadius * 2.0);
        } else if (theme == VisualTheme.WIREFRAME) {
            gc.setStroke(javafx.scene.paint.Color.rgb(160, 245, 255, 0.92));
            gc.setLineWidth(1.2);
            gc.strokeOval(x - nodeRadius, y - nodeRadius, nodeRadius * 2.0, nodeRadius * 2.0);
        } else {
            gc.setFill(toFx(sensorColor, 0.93));
            gc.fillOval(x - nodeRadius, y - nodeRadius, nodeRadius * 2.0, nodeRadius * 2.0);
            gc.setStroke(javafx.scene.paint.Color.rgb(45, 45, 45, 0.85));
            gc.setLineWidth(1.0);
            gc.strokeOval(x - nodeRadius, y - nodeRadius, nodeRadius * 2.0, nodeRadius * 2.0);
        }

        if (currentShowIds) {
            if (theme == VisualTheme.CLASSIC) {
                gc.setFill(javafx.scene.paint.Color.rgb(30, 30, 30, 0.96));
                gc.setFont(Font.font("SansSerif", 11));
            } else {
                gc.setFill(javafx.scene.paint.Color.rgb(220, 250, 255, 0.96));
                gc.setFont(Font.font("Consolas", 11));
            }
            gc.fillText(String.valueOf(sensor.id()), x + nodeRadius + 2.0, y - nodeRadius - 1.0);
        }
    }

    private void drawArrow(GraphicsContext gc, double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double len = Math.sqrt(dx * dx + dy * dy);
        if (len < 1.0) {
            return;
        }
        double ux = dx / len;
        double uy = dy / len;
        double size = 7.0;
        double px = -uy;
        double py = ux;
        double tipX = x2;
        double tipY = y2;
        double leftX = tipX - ux * size + px * (size * 0.6);
        double leftY = tipY - uy * size + py * (size * 0.6);
        double rightX = tipX - ux * size - px * (size * 0.6);
        double rightY = tipY - uy * size - py * (size * 0.6);
        gc.fillPolygon(new double[]{tipX, leftX, rightX}, new double[]{tipY, leftY, rightY}, 3);
    }

    private void drawDataTrail(GraphicsContext gc, int fromId, int toId, double x1, double y1, double x2, double y2, double pulse) {
        double phase = ((fromId * 31 + toId * 17) % 1000) / 1000.0;
        double t = ((frameNanos * 0.00000000022) + phase) % 1.0;
        double px = x1 + (x2 - x1) * t;
        double py = y1 + (y2 - y1) * t;
        double radius = 1.4 + pulse * 1.2;
        gc.setFill(javafx.scene.paint.Color.rgb(178, 252, 255, 0.95));
        gc.fillOval(px - radius, py - radius, radius * 2.0, radius * 2.0);
    }

    private void drawGrid(GraphicsContext gc, double w, double h) {
        if (visualTheme == VisualTheme.CLASSIC) {
            gc.setStroke(javafx.scene.paint.Color.rgb(185, 185, 185, 0.55));
            gc.setLineWidth(0.8);
        } else {
            gc.setStroke(javafx.scene.paint.Color.rgb(120, 190, 230, 0.25));
            gc.setLineWidth(0.75);
        }
        for (int i = 1; i <= numTicks; i++) {
            double y = h * (1.0 - axesMargin) - h * (1.0 - 2 * axesMargin) * (i / (double) numTicks);
            gc.strokeLine(w * axesMargin, y, w * (1.0 - axesMargin), y);
            double x = w * axesMargin + w * (1.0 - 2 * axesMargin) * (i / (double) numTicks);
            gc.strokeLine(x, h * (1.0 - axesMargin), x, h * axesMargin);
        }
    }

    private void drawAxes(GraphicsContext gc, double w, double h) {
        if (visualTheme == VisualTheme.CLASSIC) {
            gc.setStroke(javafx.scene.paint.Color.rgb(40, 40, 40, 0.88));
            gc.setFont(Font.font("SansSerif", 11));
        } else {
            gc.setStroke(javafx.scene.paint.Color.rgb(132, 220, 255, 0.88));
            gc.setFont(Font.font("Consolas", 11));
        }
        gc.setLineWidth(1.2);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        double x0 = w * axesMargin;
        double y0 = h * (1.0 - axesMargin);
        double xMax = w * (1.0 - axesMargin);
        double yMin = h * axesMargin;

        gc.strokeLine(x0, y0, xMax, y0);
        for (int i = 0; i <= numTicks; i++) {
            double x = x0 + w * (1.0 - 2 * axesMargin) * (i / (double) numTicks);
            gc.strokeLine(x, y0 - 4, x, y0 + 4);
            int label = (int) ((xOrigin + xAxisLength) * (i / (double) numTicks));
            if (visualTheme == VisualTheme.CLASSIC) {
                gc.setFill(javafx.scene.paint.Color.rgb(30, 30, 30, 0.95));
            } else {
                gc.setFill(javafx.scene.paint.Color.rgb(170, 238, 255, 0.95));
            }
            gc.fillText(String.valueOf(label), x, h - 8);
        }

        gc.strokeLine(x0, y0, x0, yMin);
        gc.setTextAlign(TextAlignment.LEFT);
        for (int i = 0; i <= numTicks; i++) {
            double y = y0 - h * (1.0 - 2 * axesMargin) * (i / (double) numTicks);
            gc.strokeLine(x0 - 4, y, x0 + 4, y);
            int label = (int) ((xOrigin + xAxisLength) * (i / (double) numTicks));
            gc.fillText(String.valueOf(label), 4, y);
        }
    }

    private void paintBackground(GraphicsContext gc, double w, double h, double pulse, VisualTheme theme) {
        if (theme == VisualTheme.CLASSIC) {
            gc.setFill(javafx.scene.paint.Color.rgb(248, 248, 248, 1.0));
            gc.fillRect(0, 0, w, h);
            return;
        }

        if (theme == VisualTheme.WIREFRAME) {
            gc.setFill(new LinearGradient(
                    0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0.0, javafx.scene.paint.Color.rgb(8, 11, 19, 1.0)),
                    new Stop(1.0, javafx.scene.paint.Color.rgb(15, 25, 40, 1.0))
            ));
            gc.fillRect(0, 0, w, h);
            gc.setStroke(javafx.scene.paint.Color.rgb(90, 180, 220, 0.12));
            gc.setLineWidth(1.0);
            for (int i = 0; i < 24; i++) {
                double y = (h / 24.0) * i;
                gc.strokeLine(0, y, w, y);
            }
            return;
        }

        gc.setFill(new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, javafx.scene.paint.Color.rgb(7, 20, 44, 1.0)),
                new Stop(0.45, javafx.scene.paint.Color.rgb(8, 36, 72, 1.0)),
                new Stop(1.0, javafx.scene.paint.Color.rgb(10, 60, 96, 1.0))
        ));
        gc.fillRect(0, 0, w, h);

        gc.setStroke(javafx.scene.paint.Color.rgb(60, 220, 250, 0.05 + pulse * 0.04));
        gc.setLineWidth(1.0);
        for (int i = 0; i < 18; i++) {
            double y = (h / 18.0) * i;
            gc.strokeLine(0, y, w, y);
        }
    }

    private void drawHudFrame(GraphicsContext gc, double w, double h, VisualTheme theme) {
        if (theme == VisualTheme.CLASSIC) {
            return;
        }

        gc.setStroke(javafx.scene.paint.Color.rgb(88, 224, 255, 0.45));
        gc.setLineWidth(1.0);
        gc.strokeRect(2, 2, w - 4, h - 4);

        gc.setStroke(javafx.scene.paint.Color.rgb(88, 224, 255, 0.75));
        gc.setLineWidth(2.0);
        double c = 16.0;
        gc.strokeLine(2, 2, 2 + c, 2);
        gc.strokeLine(2, 2, 2, 2 + c);
        gc.strokeLine(w - 2, 2, w - 2 - c, 2);
        gc.strokeLine(w - 2, 2, w - 2, 2 + c);
        gc.strokeLine(2, h - 2, 2 + c, h - 2);
        gc.strokeLine(2, h - 2, 2, h - 2 - c);
        gc.strokeLine(w - 2, h - 2, w - 2 - c, h - 2);
        gc.strokeLine(w - 2, h - 2, w - 2, h - 2 - c);
    }

    private double getNodeRadius(double w, double h) {
        double base = Math.min(w, h);
        return Math.max(4.0, Math.min(9.0, base / 80.0));
    }

    private ProjectedPoint projectPoint(Sensor sensor, double w, double h, boolean use3D) {
        double baseX = w * axesMargin + (sensor.getX() / Network.get_maxDistance()) * w * (1.0 - 2 * axesMargin);
        double baseY = h * (1.0 - axesMargin) - (sensor.getY() / Network.get_maxDistance()) * h * (1.0 - 2 * axesMargin);

        if (!use3D) {
            return new ProjectedPoint(
                    baseX * viewScale + viewOffsetX,
                    baseY * viewScale + viewOffsetY,
                    1.0
            );
        }

        double nx = ((sensor.getX() / Network.get_maxDistance()) * 2.0) - 1.0;
        double ny = ((sensor.getY() / Network.get_maxDistance()) * 2.0) - 1.0;
        double nz = (((sensor.id() * 37L) % 100) / 100.0) - 0.5;
        if (sensor.isActive()) {
            nz += 0.12;
        }

        double cosy = Math.cos(cameraYaw);
        double siny = Math.sin(cameraYaw);
        double cosp = Math.cos(cameraPitch);
        double sinp = Math.sin(cameraPitch);

        double rx = cosy * nx + siny * nz;
        double rz = -siny * nx + cosy * nz;
        double ry = cosp * ny - sinp * rz;
        double rz2 = sinp * ny + cosp * rz;

        double perspective = cameraDistance / (cameraDistance - rz2);
        perspective = clamp(perspective, 0.55, 1.75);
        double scale = Math.min(w, h) * 0.46 * viewScale;
        double sx = (w * 0.5) + (rx * scale * perspective) + viewOffsetX;
        double sy = (h * 0.5) - (ry * scale * perspective) + viewOffsetY;
        return new ProjectedPoint(sx, sy, perspective);
    }

    private javafx.scene.paint.Color toFx(Color color, double alpha) {
        return javafx.scene.paint.Color.rgb(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    private void draw3DHint(GraphicsContext gc, double w, double h, VisualTheme theme) {
        if (theme == VisualTheme.CLASSIC) {
            gc.setFill(javafx.scene.paint.Color.rgb(50, 50, 50, 0.82));
            gc.setFont(Font.font("SansSerif", 11));
        } else {
            gc.setFill(javafx.scene.paint.Color.rgb(182, 248, 255, 0.88));
            gc.setFont(Font.font("Consolas", 11));
        }
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("3D: RMB rotate, LMB pan, wheel zoom, ALT+wheel depth, dblclick reset", 12, h - 16);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private Color adjustSpecialColor(Sensor sensor, Color baseColor) {
        if (hasTrueBoolean(sensor, "isRSU")) {
            return Color.MAGENTA;
        }
        if (hasTrueBoolean(sensor, "isPowerNode") || hasTrueBoolean(sensor, "isPreTrustedPeer")) {
            return sensor.isActive() ? Color.MAGENTA : idleClientColor;
        }
        return baseColor;
    }

    private boolean hasTrueBoolean(Sensor sensor, String methodName) {
        try {
            Method method = sensor.getClass().getMethod(methodName);
            Object value = method.invoke(sensor);
            return value instanceof Boolean && ((Boolean) value);
        } catch (Exception ignored) {
            return false;
        }
    }

    private Collection<Sensor> copySensors(Collection<Sensor> sensors) {
        if (sensors == null || sensors.isEmpty()) {
            return new ArrayList<Sensor>(0);
        }
        return new ArrayList<Sensor>(sensors);
    }

    private static final class ProjectedPoint {
        private final double x;
        private final double y;
        private final double depthScale;

        private ProjectedPoint(double x, double y, double depthScale) {
            this.x = x;
            this.y = y;
            this.depthScale = depthScale;
        }
    }
}
