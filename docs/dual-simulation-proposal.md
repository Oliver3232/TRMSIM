# Dual Simulation Proposal

## Goal

Add a dual-simulation mode on top of the current `refactoring` branch without repeating the large unrelated UI and model changes from the old `dual_simulations` branch.

The target behavior is:

- single mode keeps current behavior
- dual mode shows two simulation workspaces side by side
- each workspace can use a different trust model and its own network
- the network views remain visible during execution
- secondary panels can be hidden, shown, or moved to dialogs
- export works for one or both simulations

## What Should Be Duplicated

These parts must be isolated per simulation slot:

- current network
- selected trust model
- parameters file / parameter panel state
- single-run execution state
- batch execution state
- simulation results repository
- messages console
- outcomes panels
- node selection / inspector state
- graph workspace state

This means dual mode cannot be built only as two `NetworkPanel`s on top of the current singleton-driven runtime.

## What Should Stay Shared

These parts should stay application-wide:

- app mode switch: single / dual
- top-level frame
- export entry point
- global layout shell
- optional shared visualization presets if desired later

The mode switch should be visible at all times. It should not be hidden inside drawers.

## Important Correction

Your intuition about duplicating `New WSN`, `Load WSN`, `Save WSN`, `Reset WSN`, `Run T&R Model`, `Stop T&R Model`, and trust-model selection per simulation is correct.

Those controls belong to the simulation slot, not to the whole application, because in dual mode each side must be independently configurable and executable.

`Export` and `single/dual mode switch` should stay shared.

## Recommended Runtime Architecture

Introduce an explicit simulation slot runtime instead of extending the current singleton-style controller state.

### New Core Concepts

- `SimulationSlot`: already exists and should become real runtime identity
- `SimulationWorkspaceState`
  - slot id
  - network
  - selected trust model name
  - trust model instance
  - parameters source state
  - single/batch execution state
  - selected node id
  - results repository
  - message log
- `DualSimulationSession`
  - owns `PRIMARY` and `SECONDARY` workspace state
  - exposes helpers like `get(slot)`
- `SimulationResultsStore`
  - per-slot results instead of one global singleton repository

### Minimal Refactor Required First

Before changing the GUI heavily, move runtime ownership away from the current global hotspots:

- `Controller`
- `Sensor` static runtime state
- global `SimulationResultRepository`
- event helpers that assume a single active workspace

The first implementation does not need a perfect architecture, but each running simulation must know which slot it belongs to and where its outputs go.

## GUI Proposal

### 1. Shell Layout

Use a mode-aware shell:

- single mode
  - current main network area
  - current controls layout
- dual mode
  - center split pane: left workspace / right workspace
  - persistent top or center toolbar for shared actions

Recommended shared toolbar:

- mode switch: `Single | Dual`
- export
- optional "link settings" toggle later

### 2. Per-Slot Workspace

Each side in dual mode should be a `SimulationWorkspacePanel`.

Each workspace contains:

- always-visible network panel
- slim slot header with slot label and selected model
- collapsible slot drawer for slot-specific setup
- collapsible bottom area for messages and outcomes

### 3. Slot Drawer

The drawer should be slot-specific and collapsible, not permanently visible.

Contains:

- trust model selector
- `New WSN`
- `Load WSN`
- `Save WSN`
- `Reset WSN`
- `Run T&R Model`
- `Stop T&R Model`
- simulation settings
- parameter source / parameter editor

Behavior:

- editable before run
- disabled while the slot is running, except for actions that are already safe
- hidden by default in dual mode after simulation starts
- re-openable on hover button or explicit toggle

Do not hide it only on hover. Prefer an explicit pin/toggle behavior:

- collapsed
- temporarily expanded
- pinned open

Hover-only controls are too fragile for long thesis demos.

### 4. Outcomes and Console

Keep the network always visible. Everything else can become secondary UI.

Recommended dual-mode workspace layout:

- center: network panel
- bottom collapsible strip:
  - `Outcomes`
  - `Messages`
  - `Inspector`

The strip can have tabs:

- `Outcomes`
- `Messages`
- `Inspector`

This is better than making every secondary component a separate floating drawer.

### 5. Dialog Windows

For larger inspection:

- outcomes can open in a dialog
- messages can open in a dialog
- parameter editor can open in a dialog
- export dialog can open as one shared dialog with slot tabs or slot columns

This gives visibility without sacrificing network space.

## Export Proposal

Keep one shared `Export` button in the app shell.

In dual mode the export dialog should offer:

- `Primary simulation`
- `Secondary simulation`
- `Both`

Recommended dialog structure:

- target selector at top
- export format checklist below
- when `Both` is selected, output names are prefixed with slot id or model name

Do not force the user to export only one side at a time.

## Settings Behavior

### Simulation Settings

These should be treated as pre-run settings and locked during execution:

- network generation settings
- trust model selection
- parameters source switching
- load/save/new/reset actions

This matches your intuition and avoids inconsistent mid-run mutations.

### Runtime Display Controls

These may remain adjustable during execution:

- show ids
- show links
- show ranges
- show grid
- view-only graph controls

### Trust Model Parameters During Run

For the first dual-mode version, treat them as locked while running.

Even if some models technically tolerate live changes, allowing it in dual mode adds ambiguity:

- which execution step sees the update
- whether the current run or only the next run should use it
- whether both slots are linked

For thesis scope, "parameters are editable only when that slot is idle" is cleaner.

## Implementation Strategy

### Phase 1: Runtime Split

- introduce per-slot workspace state
- move results storage from singleton to per-slot repository
- make simulation events slot-aware
- make controller/service operations accept target slot or workspace state

### Phase 2: Dual GUI Skeleton

- add app mode switch
- add split dual layout with two network panels
- create slot drawers
- wire duplicated per-slot controls

### Phase 3: Results and Export

- add per-slot outcomes panels and messages
- update export dialog for one/both slot selection
- add clear slot labeling in exported files

### Phase 4: Polish

- collapse/pin behavior
- dialog-based expanded views
- state persistence when switching single/dual mode

## Scope Guardrails

Avoid these mistakes from the old branch:

- rewriting unrelated refactor pieces
- reverting current package/controller/export architecture
- mixing dual-mode work with scenario work
- changing trust model internals unless runtime isolation requires it
- redesigning every screen at once

## Practical Recommendation

Implement dual mode in the smallest valid path:

1. slot-aware runtime
2. two visible network panels
3. per-slot setup drawers
4. per-slot outcomes/messages
5. shared export dialog for one/both slots

Only after that should optional extras be added:

- linked settings
- floating dialogs
- richer comparison overlays
