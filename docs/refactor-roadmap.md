# TRMSIM Refactor Roadmap

## Goal

Refactor the simulator so it can support:

- isolated simulation execution outside the GUI
- predefined scenarios such as drone profiles
- parallel multi-model simulation runs
- larger-scale networks with 1000+ nodes
- future ML-based trust models such as Bayes and SVM

## Current architectural constraints

- `TRMSim_WSN` mixes UI, orchestration, and simulation setup.
- `Controller` is a singleton with mutable runtime state.
- `Sensor` relies on static mutable state shared across the whole process.
- `Simulation` mixes orchestration, threading, progress notifications, and execution.
- Old `Observable`/`Observer` events are weakly typed.
- Reflection for model setup is duplicated between GUI and controller code.

## Refactor phases

### Phase 1: Application-layer foundation

Status: in progress

- Introduce typed configuration objects for network generation and simulation execution.
- Add a small application service facade between GUI and controller code.
- Keep behavior compatible while reducing direct GUI coupling to low-level methods.

### Phase 2: Runtime context extraction

- Introduce `SimulationContext`.
- Move runtime flags and current trust model out of static global state.
- Start reducing dependence on `Sensor` static fields.

### Phase 3: Trust model registry

- Add a model registry/factory instead of scattered reflection.
- Centralize model metadata, parameter creation, and instantiation.
- Prepare extension points for future Bayes and SVM models.

### Phase 4: Simulation engine split

- Split orchestration from execution internals.
- Replace `Observable`/`Observer` with typed events.
- Isolate outcome aggregation and progress publishing.

### Phase 5: Scale support

- Replace per-client thread creation with `ExecutorService`.
- Add bounded concurrency and optional lightweight rendering mode.
- Reduce GUI refresh frequency for large networks.

### Phase 6: Scenario system

- Introduce `ScenarioDefinition` and `ScenarioLoader`.
- Support random, XML, and predefined scenario sources.
- Implement the first domain scenario: drone profile.

### Phase 7: Parallel multi-model execution

- Add a batch runner for isolated model comparisons.
- Run multiple trust models over equivalent scenarios in parallel.
- Store results using a model-agnostic result structure.

### Phase 8: ML-based trust models

- Add feature extraction for trust decisions.
- Implement a simple Bayes-based trust model.
- Implement an SVM-based trust model after the Bayes path is stable.

### Phase 9: Documentation and diagrams

- Generate class diagram for the original simulator from `master`.
- Generate class diagram for the refactored simulator.
- Document extension points for adding new trust models and scenarios.

## Recommended branch strategy

- `combined_export_interface`: backup/reference branch
- `refactoring`: stable base branch for refactor work
- feature branches from `refactoring`, for example:
  - `refactor/core-foundation`
  - `refactor/runtime-context`
  - `refactor/trust-model-registry`
  - `feature/drone-scenario`
  - `feature/parallel-simulations`
  - `feature/bayes-trust-model`
  - `feature/svm-trust-model`

## Immediate next step

Implement `SimulationContext` and start removing static runtime state from `Sensor`.
