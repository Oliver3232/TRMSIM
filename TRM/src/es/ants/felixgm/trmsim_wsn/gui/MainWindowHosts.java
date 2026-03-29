package es.ants.felixgm.trmsim_wsn.gui;

import es.ants.felixgm.trmsim_wsn.SimulationSlot;
import es.ants.felixgm.trmsim_wsn.gui.events.SimulationEventHelper;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowConfigurationController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowInitializationController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowNetworkOverlayController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowNodeInspectorController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowParametersController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowSimulationControlsController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.controllers.MainWindowUiStateController;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.hosts.MainWindowConfigurationHostFactory;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.hosts.MainWindowInitializationHostFactory;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.hosts.MainWindowNodeInspectorHostFactory;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.hosts.MainWindowOverlayHostFactory;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.hosts.MainWindowParametersHostFactory;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.hosts.MainWindowSimulationControlsHostFactory;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.hosts.MainWindowSimulationEventHostFactory;
import es.ants.felixgm.trmsim_wsn.gui.mainwindow.hosts.MainWindowUiStateHostFactory;

public final class MainWindowHosts {
    private MainWindowHosts() {
    }

    static MainWindowInitializationController.Host initialization(TRMSim_WSN window) {
        return MainWindowInitializationHostFactory.create(new MainWindowContext(window));
    }

    static MainWindowNodeInspectorController.Host nodeInspector(TRMSim_WSN window) {
        return MainWindowNodeInspectorHostFactory.create(new MainWindowContext(window));
    }

    static MainWindowSimulationControlsController.Host simulationControls(TRMSim_WSN window) {
        return MainWindowSimulationControlsHostFactory.create(new MainWindowContext(window));
    }

    public static MainWindowParametersController.Host parameters(TRMSim_WSN window) {
        return MainWindowParametersHostFactory.create(new MainWindowContext(window));
    }

    public static MainWindowConfigurationController.Host configuration(TRMSim_WSN window) {
        return MainWindowConfigurationHostFactory.create(new MainWindowContext(window));
    }

    static MainWindowUiStateController.Host uiState(TRMSim_WSN window) {
        return MainWindowUiStateHostFactory.create(new MainWindowContext(window));
    }

    static MainWindowNetworkOverlayController.Host overlay(TRMSim_WSN window) {
        return MainWindowOverlayHostFactory.create(new MainWindowContext(window));
    }

    static SimulationEventHelper.EventHost simulationEvents(TRMSim_WSN window) {
        return MainWindowSimulationEventHostFactory.create(new MainWindowContext(window));
    }

    static SimulationEventHelper.EventHost simulationEvents(TRMSim_WSN window, SimulationSlot slot) {
        return MainWindowSimulationEventHostFactory.create(new MainWindowContext(window), slot);
    }
}
