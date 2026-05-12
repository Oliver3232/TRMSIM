# Modifications to TRMSim-WSN

This file lists all Java source files added, modified, moved, or deleted relative to the
original TRMSim-WSN repository (https://github.com/LefKok/TRMSIM, commit used as base).

All paths below are relative to `TRM/src/es/ants/felixgm/trmsim_wsn/`.

Author of modifications: Oliver Olšavský, bachelor's thesis 2025/2026.

---

## New Java source files

### Core simulation layer
| File | Description |
|------|-------------|
| `ClientExecutionSupport` | Headless CLI entry-point support |
| `HeadlessBatchScenarioRunner` | Runs batch scenarios without GUI |
| `ScenarioBenchmarkRunner` | Benchmarks scenarios across trust models |
| `SimulationContext` | Shared immutable context passed through a simulation run |
| `SimulationListener` | Observer interface for simulation lifecycle events |
| `SimulationSlot` | Holds one simulation instance in dual-mode |
| `SimulationWorkspaceState` | Mutable workspace state for dual-mode |
| `VerboseSimulationRunner` | Simulation runner that prints step-level logs |

### Application service layer (`app/`)
| File | Description |
|------|-------------|
| `app/BatchSimulationConfig` | Configuration record for batch runs |
| `app/NetworkGenerationConfig` | Configuration record for network generation |
| `app/SimulationApplicationService` | Orchestrates simulation lifecycle from the app layer |
| `app/SimulationConfig` | General simulation configuration record |
| `app/support/ControllerNetworkGenerationSupport` | Network generation helpers extracted from Controller |
| `app/support/ControllerParametersIO` | Parameters load/save helpers extracted from Controller |

### GUI — main window (`gui/`)
| File | Description |
|------|-------------|
| `gui/AppMode` | Enum distinguishing single vs. dual simulation modes |
| `gui/DualModeCoordinator` | Coordinates the two simulation slots in dual mode |
| `gui/DualModeParametersSupport` | Parameters synchronisation for dual mode |
| `gui/DualModeScenarioSupport` | Scenario load/save for dual mode |
| `gui/DualModeSessionSupport` | Session management for dual mode |
| `gui/DualModeWorkspaceSupport` | Workspace panel management for dual mode |
| `gui/DualSettingsPanel` | Settings panel shown in dual simulation view |
| `gui/MainWindowAssemblyController` | Assembles all main-window sub-controllers on startup |
| `gui/MainWindowComponentInitializer` | Initializes Swing components of the main window |
| `gui/MainWindowContext` | Shared context object passed between main-window controllers |
| `gui/MainWindowEmbeddedInspectorController` | Controls the embedded node inspector panel |
| `gui/MainWindowHosts` | Factory for host interfaces consumed by sub-controllers |
| `gui/MainWindowRuntimeSupport` | Runtime helpers used during an active simulation |

### GUI — dual simulation panel (`gui/dual/`)
| File | Description |
|------|-------------|
| `gui/dual/DualSimulationShellPanel` | Outer shell panel that hosts both simulation workspaces |
| `gui/dual/DualSimulationWorkspacePanel` | Single workspace panel inside dual view |
| `gui/dual/DualWorkspaceLiveDrawer` | Live network graph drawer for one dual-mode workspace |

### GUI — events (`gui/events/`)
| File | Description |
|------|-------------|
| `gui/events/SimulationEventHelper` | Dispatches and routes simulation lifecycle events to the GUI |

### GUI — export (`gui/export/`)
| File | Description |
|------|-------------|
| `gui/export/DualSimulationExportHelper` | Coordinates export for both slots in dual mode |
| `gui/export/EnergyConsumptionExporter` | Exports energy consumption results to CSV |
| `gui/export/EnergyConsumptionTextExporter` | Exports energy consumption results to plain text |
| `gui/export/ExportRequest` | Value object carrying an export request |
| `gui/export/FormattedTSVExporter` | Exports simulation outcomes as TSV |
| `gui/export/FormattedTextExporter` | Exports simulation outcomes as formatted plain text |
| `gui/export/GraphImageExporter` | Exports the network graph as a PNG image |
| `gui/export/NodeLevelExporter` | Exports per-node metric data |
| `gui/export/SimulationExportHelper` | High-level export orchestrator for a single simulation |
| `gui/export/SimulationResultRepository` | Holds accumulated results ready for export |

### GUI — graph workspace (`gui/graph/`)
| File | Description |
|------|-------------|
| `gui/graph/SimulationGraphFullscreenSupport` | Manages fullscreen mode for the network graph |
| `gui/graph/SimulationGraphStateSupport` | Tracks graph display state (zoom, selection, etc.) |
| `gui/graph/SimulationGraphWorkspace` | Main graph canvas and interaction surface |

### GUI — layout (`gui/layout/`)
| File | Description |
|------|-------------|
| `gui/layout/CompactLegendPanel` | Compact legend shown beside the graph |
| `gui/layout/MiniLegendPanel` | Minimal legend overlay for fullscreen mode |
| `gui/layout/ModernLayoutInstaller` | Installs FlatLaf-based modern look-and-feel |
| `gui/layout/WrapLayout` | Flow layout that wraps children to new rows |

### GUI — main window sub-controllers (`gui/mainwindow/`)
| File | Description |
|------|-------------|
| `gui/mainwindow/bootstrap/MainWindowFrameSetup` | Sets JFrame properties on startup |
| `gui/mainwindow/controllers/MainWindowActionController` | Handles toolbar and menu actions |
| `gui/mainwindow/controllers/MainWindowConfigurationController` | Manages configuration panel interactions |
| `gui/mainwindow/controllers/MainWindowGraphStripController` | Controls the graph strip at the bottom |
| `gui/mainwindow/controllers/MainWindowInitializationController` | Runs startup initialization sequence |
| `gui/mainwindow/controllers/MainWindowMenuSetupController` | Builds and wires the menu bar |
| `gui/mainwindow/controllers/MainWindowNetworkOverlayController` | Manages the network overlay panel |
| `gui/mainwindow/controllers/MainWindowNodeInspectorController` | Controls node detail inspector |
| `gui/mainwindow/controllers/MainWindowParametersController` | Handles trust model parameter panels |
| `gui/mainwindow/controllers/MainWindowRenderController` | Triggers repaints and graph updates |
| `gui/mainwindow/controllers/MainWindowSimulationController` | Drives the simulation run/stop/reset cycle |
| `gui/mainwindow/controllers/MainWindowSimulationControlsController` | Manages simulation control buttons |
| `gui/mainwindow/controllers/MainWindowTrustModelController` | Handles trust model selection and switching |
| `gui/mainwindow/controllers/MainWindowUiStateController` | Enables/disables UI elements based on state |
| `gui/mainwindow/hosts/MainWindowConfigurationHostFactory` | Factory for configuration host interface |
| `gui/mainwindow/hosts/MainWindowInitializationHostFactory` | Factory for initialization host interface |
| `gui/mainwindow/hosts/MainWindowNodeInspectorHostFactory` | Factory for node inspector host interface |
| `gui/mainwindow/hosts/MainWindowOverlayHostFactory` | Factory for overlay host interface |
| `gui/mainwindow/hosts/MainWindowParametersHostFactory` | Factory for parameters host interface |
| `gui/mainwindow/hosts/MainWindowSimulationControlsHostFactory` | Factory for simulation controls host |
| `gui/mainwindow/hosts/MainWindowSimulationEventHostFactory` | Factory for simulation event host |
| `gui/mainwindow/hosts/MainWindowUiStateHostFactory` | Factory for UI state host interface |
| `gui/mainwindow/sections/MainWindowBottomSection` | Bottom section layout and wiring |
| `gui/mainwindow/sections/MainWindowCenterSection` | Center section (graph + inspector) |
| `gui/mainwindow/sections/MainWindowControlsSection` | Controls strip section |
| `gui/mainwindow/sections/MainWindowParametersSection` | Parameters side panel section |
| `gui/mainwindow/sections/MainWindowParametersTabSection` | Tab container for parameter panels |
| `gui/mainwindow/sections/MainWindowSensorPropertiesSection` | Sensor properties section |
| `gui/mainwindow/sections/MainWindowSimulationTabSection` | Tab container for simulation panels |

### GUI — network helpers (`gui/network/`)
| File | Description |
|------|-------------|
| `gui/network/NetworkFileHelper` | Load/save helpers for network topology files |

### GUI — parameter panels (`gui/parameterpanels/`)
| File | Description |
|------|-------------|
| `gui/parameterpanels/BayesTrust_ParametersPanel` | Parameter panel for BayesTrust model |
| `gui/parameterpanels/SVMTrust_ParametersPanel` | Parameter panel for SVMTrust model |
| `gui/parameterpanels/TRMParametersPanelFactory` | Factory that instantiates the correct panel per trust model |
| `gui/parameterpanels/lftm/LFTMPanelComponentInitializer` | Initializes LFTM panel Swing components |
| `gui/parameterpanels/lftm/LFTMPanelLinguisticTermsSupport` | Linguistic term logic extracted from LFTM panel |
| `gui/parameterpanels/lftm/LFTMPanelParametersMapper` | Maps LFTM panel fields to parameter objects |
| `gui/parameterpanels/lftm/LFTMPanelScalarSupport` | Scalar parameter support for LFTM panel |
| `gui/parameterpanels/lftm/LFTMPanelSupport` | Common LFTM panel utilities |
| `gui/parameterpanels/lftm/LFTM_ParametersPanel` | Moved here from `gui/parameterpanels/` (see Moved section) |

### GUI — support helpers (`gui/support/`)
| File | Description |
|------|-------------|
| `gui/support/MessageConsoleHelper` | Writes messages to the console panel |
| `gui/support/NetworkRenderSupport` | Helpers for rendering the sensor network |
| `gui/support/NodeInspectorHelper` | Populates the node inspector with sensor data |
| `gui/support/NumericInputBindingHelper` | Binds numeric text fields with validation |
| `gui/support/ParametersSourceHelper` | Resolves the active parameters source |
| `gui/support/SimulationControlsHelper` | Enables/disables simulation control buttons |
| `gui/support/SimulationUiHelper` | UI helpers for active simulation state |
| `gui/support/UiStateHelper` | General UI enable/disable state management |

### GUI — trust model UI (`gui/trustmodel/`)
| File | Description |
|------|-------------|
| `gui/trustmodel/TrustModelSelectionHelper` | Handles trust model combo-box selection |
| `gui/trustmodel/TrustModelUiFactory` | Builds trust model specific UI fragments |

### Outcomes (`outcomes/`)
| File | Description |
|------|-------------|
| `outcomes/NodeMetric` | Per-node metric record used by node-level exporters |

### Scenario management (`scenario/`)
| File | Description |
|------|-------------|
| `scenario/PredefinedScenarioLoader` | Loads bundled predefined scenarios from resources |
| `scenario/ScenarioDefinition` | Value object describing a predefined scenario |
| `scenario/ScenarioFileHelper` | Load/save helpers for scenario `.properties` files |
| `scenario/ScenarioSelectionHelper` | Drives the scenario selection dialog |
| `scenario/ScenarioUiBindingHelper` | Binds scenario data into the parameter panels |

### Trust model infrastructure (`trm/`)
| File | Description |
|------|-------------|
| `trm/TrustModelBundle` | Groups a trust model with its network and parameters |
| `trm/TrustModelFactory` | Creates trust model instances by name |
| `trm/TrustModelRegistry` | Registry of all available trust models |

### BayesTrust — new trust model (`trm/bayestrust/`)
| File | Description |
|------|-------------|
| `trm/bayestrust/BayesTrust` | Bayesian trust model implementation |
| `trm/bayestrust/BayesTrust_Network` | Network class for BayesTrust |
| `trm/bayestrust/BayesTrust_Parameters` | Parameters class for BayesTrust |
| `trm/bayestrust/BayesTrust_Sensor` | Sensor class for BayesTrust |

### EigenTrust extensions (`trm/eigentrust/`)
| File | Description |
|------|-------------|
| `trm/eigentrust/EigenTrustProfiler` | Collects per-step profiling data for EigenTrust |

### LFTM extracted helpers (`trm/lftm/`)
| File | Description |
|------|-------------|
| `trm/lftm/LFTMFuzzyRuleSupport` | Fuzzy rule evaluation logic extracted from LFTM |
| `trm/lftm/LFTMLinguisticTermsSupport` | Linguistic term parsing extracted from LFTM |
| `trm/lftm/LFTMScalarParametersSupport` | Scalar parameter handling extracted from LFTM |

### SVMTrust — new trust model (`trm/svmtrust/`)
| File | Description |
|------|-------------|
| `trm/svmtrust/LibSvmTrustClassifier` | Wraps LIBSVM for trust classification |
| `trm/svmtrust/SVMTrust` | SVM-based trust model implementation |
| `trm/svmtrust/SVMTrust_Network` | Network class for SVMTrust |
| `trm/svmtrust/SVMTrust_Parameters` | Parameters class for SVMTrust |
| `trm/svmtrust/SVMTrust_Sensor` | Sensor class for SVMTrust |

---

## Modified Java source files

### Core
| File | Nature of change |
|------|-----------------|
| `Controller` | Extracted network generation and parameter I/O into `app/support/`; added dual-mode coordination |
| `Simulation` | Added `SimulationListener` notification; refactored step loop |
| `network/Sensor` | Added energy tracking fields used by `EnergyConsumptionOutcome` |
| `outcomes/EnergyConsumptionOutcome` | Extended to produce per-node metrics |
| `outcomes/Outcome` | Added node-level data access methods |
| `trm/TRMParameters` | Added fields shared by new trust models |
| `trm/TRModel_WSN` | Wired new trust models through the model registry |

### GUI
| File | Nature of change |
|------|-----------------|
| `gui/TRMSim_WSN` | Replaced monolithic setup with `MainWindowAssemblyController` |
| `gui/legendpanels/LegendPanel` | Adapted to new layout system |
| `gui/outcomespanels/LFTM_SatisfactionPanel` | Adapted for dual-mode and new export pipeline |
| `gui/outcomespanels/OutcomesPanel` | Adapted for dual-mode and new export pipeline |
| `gui/outcomespanels/PathLengthPanel` | Adapted for dual-mode |
| `gui/outcomespanels/PowerTrustEnergyConsumptionPanel` | Adapted for node-level export |
| `gui/parameterpanels/BTRM_WSN_ParametersPanel` | Minor refactor for `TRMParametersPanelFactory` |

### Trust models
| File | Nature of change |
|------|-----------------|
| `trm/btrm_wsn/BTRM_Ant` | Minor adaptation for `SimulationContext` |
| `trm/btrm_wsn/BTRM_Sensor` | Added energy accounting |
| `trm/btrm_wsn/BTRM_WSN` | Wired into `TrustModelRegistry` |
| `trm/eigentrust/EigenTrust` | Wired into `TrustModelRegistry`; added profiler hook |
| `trm/eigentrust/EigenTrust_Network` | Minor refactor |
| `trm/eigentrust/EigenTrust_Sensor` | Added energy accounting |
| `trm/eigentrust/GatheredInformationEigenTrust` | Minor refactor |
| `trm/lftm/LFTM` | Extracted fuzzy/linguistic logic into helper classes |
| `trm/lftm/LFTM_Ant` | Minor adaptation |
| `trm/lftm/LFTM_Parameters` | Extended for scalar/linguistic parameter split |
| `trm/lftm/LFTM_Sensor` | Added energy accounting |
| `trm/peertrust/PeerTrust_Sensor` | Added energy accounting |
| `trm/powertrust/PowerTrust` | Wired into `TrustModelRegistry` |
| `trm/powertrust/PowerTrust_Network` | Minor refactor |
| `trm/powertrust/PowerTrust_Sensor` | Added energy accounting |
| `trm/templatetrm/Application_struct` | Minor refactor |
| `trm/templatetrm/Followee_struct` | Minor refactor |
| `trm/templatetrm/TemplateTRM_Sensor` | Minor refactor |
| `trm/trip/TRIP` | Wired into `TrustModelRegistry` |
| `trm/trip/TRIP_Sensor` | Added energy accounting |

---

## Moved / Renamed files

| Original location | New location | Note |
|-------------------|--------------|------|
| `gui/AboutWindow.java` | `gui/windows/AboutWindow.java` | Moved into `windows` sub-package |
| `gui/HelpWindow.java` | `gui/windows/HelpWindow.java` | Moved into `windows` sub-package |
| `gui/SplashScreen.java` | `gui/windows/SplashScreen.java` | Moved into `windows` sub-package |
| `gui/parameterpanels/LFTM_ParametersPanel.java` | `gui/parameterpanels/lftm/LFTM_ParametersPanel.java` | Moved into dedicated `lftm` sub-package |

---

## Deleted files (original TRMSim-WSN files not present in this version)

| File | Reason |
|------|--------|
| `gui/LegendPanel.java` | Replaced by `gui/legendpanels/LegendPanel.java` |
| `gui/NetworkPanel.java` | Functionality absorbed into the refactored main-window controller modules |
| `gui/parameterpanels/LFTM_ParametersPanel.java` | Moved to `gui/parameterpanels/lftm/` (see above) |

---

## New non-Java resources

| Path | Description |
|------|-------------|
| `TRM/src/resources/scenarios/index.properties` | Index of all bundled predefined scenarios |
| `TRM/src/resources/scenarios/custom-btrm-wsn.properties` | Predefined BTRM-WSN scenario |
| `TRM/src/resources/scenarios/drone-*.properties` (7 files) | Predefined drone-network scenarios |
| `TRM/src/resources/scenarios/large-scale-fast-peertrust.properties` | Large-scale PeerTrust scenario |
| `TRM/src/resources/scenarios/olkov.properties` | Custom scenario used for experiments |
| `TRM/src/trmodels/bayestrust/BayesTrustparameters.txt` | Default parameters for BayesTrust |
| `TRM/src/trmodels/svmtrust/SVMTrustparameters.txt` | Default parameters for SVMTrust |
