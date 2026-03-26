package es.ants.felixgm.trmsim_wsn.gui.networkpanels;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;

import javax.swing.Timer;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.logging.Level;

final class JavaFXNetworkPanelSupport {
    private final JavaFXNetworkPanel owner;

    JavaFXNetworkPanelSupport(JavaFXNetworkPanel owner) {
        this.owner = owner;
    }

    void configureFxRuntimeLifecycle() {
        if (JavaFXNetworkPanel.FX_RUNTIME_CONFIGURED.compareAndSet(false, true)) {
            try {
                Platform.setImplicitExit(false);
                JavaFXNetworkPanel.LOGGER.info("JavaFX runtime configured: implicitExit=false");
            } catch (Exception ex) {
                JavaFXNetworkPanel.LOGGER.log(Level.WARNING, "Unable to configure JavaFX implicitExit", ex);
            }
        }
    }

    void scheduleInitHealthCheck() {
        Timer initCheck = new Timer(900, e -> {
            if (owner.fxPanel.isShowing() && !owner.sceneAttached.get()) {
                JavaFXNetworkPanel.LOGGER.warning("FX panel #" + owner.panelId + " scene not attached after 900ms; retrying init");
                initFxScene();
            }
        });
        initCheck.setRepeats(false);
        initCheck.start();
    }

    void installSwingDebugHooks() {
        owner.fxPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                JavaFXNetworkPanel.LOGGER.info("FX panel #" + owner.panelId + " componentShown size=" + owner.fxPanel.getWidth() + "x" + owner.fxPanel.getHeight());
            }

            @Override
            public void componentResized(ComponentEvent e) {
                JavaFXNetworkPanel.LOGGER.info("FX panel #" + owner.panelId + " componentResized size=" + owner.fxPanel.getWidth() + "x" + owner.fxPanel.getHeight());
                owner.requestFxRender();
            }
        });
        owner.fxPanel.addHierarchyListener(new HierarchyListener() {
            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                if ((e.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) != 0) {
                    String parentName = (owner.fxPanel.getParent() == null) ? "null" : owner.fxPanel.getParent().getClass().getSimpleName();
                    JavaFXNetworkPanel.LOGGER.info("FX panel #" + owner.panelId + " parentChanged parent=" + parentName);
                }
                if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                    boolean showing = owner.fxPanel.isShowing();
                    JavaFXNetworkPanel.LOGGER.info("FX panel #" + owner.panelId + " showingChanged showing=" + showing);
                    if (showing) {
                        startAnimator();
                        owner.requestFxRender();
                    } else {
                        stopAnimator();
                    }
                }
            }
        });
    }

    void initFxScene() {
        Platform.runLater(() -> {
            try {
                owner.canvas = new Canvas(640, 420);
                StackPane root = new StackPane(owner.canvas);
                root.setStyle("-fx-background-color: #f3f7ff;");
                Scene scene = new Scene(root);
                owner.fxPanel.setScene(scene);
                owner.sceneAttached.set(true);
                JavaFXNetworkPanel.LOGGER.info("FX panel #" + owner.panelId + " scene attached on FX thread. size=" + owner.fxPanel.getWidth() + "x" + owner.fxPanel.getHeight() + ", showing=" + owner.fxPanel.isShowing());
                owner.installInteractions();
                owner.animator = new AnimationTimer() {
                    @Override
                    public void handle(long now) {
                        owner.frameNanos = now;
                        owner.requestFxRender();
                    }
                };
                if (owner.fxPanel.isShowing()) {
                    startAnimator();
                }
                owner.requestFxRender();
            } catch (Throwable t) {
                owner.sceneAttached.set(false);
                JavaFXNetworkPanel.LOGGER.log(Level.SEVERE, "FX panel #" + owner.panelId + " initFxScene failed", t);
            }
        });
    }

    void startAnimator() {
        Platform.runLater(() -> {
            if ((owner.animator == null) || owner.animatorRunning.get()) {
                return;
            }
            owner.animator.start();
            owner.animatorRunning.set(true);
            JavaFXNetworkPanel.LOGGER.info("FX panel #" + owner.panelId + " animator started");
        });
    }

    void stopAnimator() {
        Platform.runLater(() -> {
            if ((owner.animator == null) || !owner.animatorRunning.get()) {
                return;
            }
            owner.animator.stop();
            owner.animatorRunning.set(false);
            JavaFXNetworkPanel.LOGGER.info("FX panel #" + owner.panelId + " animator stopped");
        });
    }
}
