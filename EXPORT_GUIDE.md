# TRMSim-WSN — Export Guide

This directory contains all simulation exports produced by TRMSim-WSN.
Each export run creates a timestamped subdirectory (e.g. `report_20260514_112507/`) with the files you selected, plus an auto-generated `simulation_params.txt` that records the exact settings used for that run.

---

## simulation_params.txt

Saved automatically alongside every export. Contains:

| Section | Fields |
|---------|--------|
| **General** | Trust model name, scenario name, description |
| **Drone Profile** | *(only when a `drone-*` scenario is active)* Profile name and description |
| **Network Configuration** | Min/max sensors, % clients, % relay, % malicious, radio range, dynamic/oscillating/collusion flags |
| **Simulation Configuration** | Number of networks per execution, number of executions |
| **Trust Model Parameters** | Full model-specific parameter set (window size, alpha, beta, etc.) |

---

## Export formats

### Simple CSV — `simulation_results.csv`

One row per aggregated simulation outcome (one outcome = one full execution over all networks).

| Column | Type | Description |
|--------|------|-------------|
| `index` | int | Sequential run number (0-based) |
| `satisfied` | 0 / 1 | Whether the client's service request was satisfied overall |
| `avgSatisfaction` | float | Average satisfaction score across all client interactions (0–1) |
| `avgPathLength` | float | Average number of hops from client to server across all requests |

> **Note:** This is the original compact format. For full metrics use *Detailed CSV*.

---

### Detailed CSV — `simulation_results_detailed.csv`

One row per aggregated outcome with all available metrics.

| Column | Type | Description |
|--------|------|-------------|
| `SimulationID` | int | Sequential run number (1-based) |
| `Timestamp` | datetime | When this outcome was recorded |
| `ModelType` | string | Trust model name (e.g. `PeerTrust`, `EigenTrust`) |
| `Satisfaction` | true/false | Whether the overall service request was satisfied |
| `AvgSatisfaction` | float 0–1 | Mean satisfaction across all client–server interactions |
| `AvgPathLength` | float | Mean hop count from client to the chosen server |
| `ClientEnergy` | float | Total energy consumed by client nodes |
| `MaliciousServerEnergy` | float | Total energy consumed by malicious server nodes |
| `BenevolentServerEnergy` | float | Total energy consumed by benevolent (honest) server nodes |
| `RelayServerEnergy` | float | Total energy consumed by relay nodes |
| `AvgSensorEnergy` | float | Average energy consumed per sensor node |
| `PreTrustedPeerEnergy` | float | Energy of pre-trusted peers *(EigenTrust only, otherwise 0)* |
| `PowerNodeEnergy` | float | Energy of power nodes *(PowerTrust only, otherwise 0)* |
| `VeryHighCount` | int | Number of clients with *Very High* fuzzy satisfaction *(LFTM only)* |
| `HighCount` | int | Number of clients with *High* fuzzy satisfaction |
| `MediumCount` | int | Number of clients with *Medium* fuzzy satisfaction |
| `LowCount` | int | Number of clients with *Low* fuzzy satisfaction |
| `VeryLowCount` | int | Number of clients with *Very Low* fuzzy satisfaction |
| `VeryHighPercentage` | float 0–1 | Fraction of clients in the *Very High* category |
| `HighPercentage` | float 0–1 | Fraction of clients in the *High* category |
| `MediumPercentage` | float 0–1 | Fraction of clients in the *Medium* category |
| `LowPercentage` | float 0–1 | Fraction of clients in the *Low* category |
| `VeryLowPercentage` | float 0–1 | Fraction of clients in the *Very Low* category |

---

### Formatted Text Report — `simulation_report.txt`

Human-readable report in sections.

**Summary block**
- Total number of simulation runs
- Overall satisfaction rate (%)
- Mean satisfaction score, mean path length, mean accuracy
- Count of runs per trust model

**Per-run detail block** (one per simulation run)
- Timestamp, model type, satisfied/not
- Mean satisfaction and path length
- Energy breakdown by node type (if available)
- Fuzzy distribution counts and percentages (LFTM only)
- Derived metrics: accuracy (%), efficiency, overall rating (EXCELLENT / GOOD / FAIR / POOR / UNSATISFACTORY)

---

### Formatted TSV — `simulation_report.tsv`

Excel-friendly tab-separated file. Same 23 columns as *Detailed CSV* but with tab delimiters and human-readable column headers. Opens directly in Excel / LibreOffice Calc without import configuration.

Appended summary rows at the end:
- `Total_Simulations`, `Satisfied_Count`, `Satisfaction_Rate`
- Model distribution (one row per model with run count)

---

### Energy Consumption Summary CSV — `energy_consumption.csv`

One row per simulation run. Energy values only, no satisfaction data.

| Column | Description |
|--------|-------------|
| `ModelType` | Trust model name |
| `ClientEnergy` | Energy consumed by all client nodes |
| `MaliciousServerEnergy` | Energy consumed by malicious server nodes |
| `BenevolentServerEnergy` | Energy consumed by honest server nodes |
| `RelayServerEnergy` | Energy consumed by relay nodes |
| `AvgSensorEnergy` | Average energy per sensor |
| `PreTrustedPeerEnergy` | EigenTrust pre-trusted peers *(0 for other models)* |
| `PowerNodeEnergy` | PowerTrust power nodes *(0 for other models)* |

All values are raw energy units (6 decimal places).

---

### Energy Consumption Text — `energy_report.txt`

Narrative energy report with:
- **Overall averages** per node type across all runs
- **Value range** for client energy (min / max / standard deviation)
- **Model distribution** (run count per model)
- **Per-run detail table** with all node-type energies

---

### Node Data CSV — `node_data.csv`

One row per node per simulation run. Use this for per-sensor analysis.

| Column | Description |
|--------|-------------|
| `SimulationID` | Run number |
| `NodeID` | Sensor identifier |
| `NodeType` | `CLIENT`, `BENEVOLENT_SERVER`, `MALICIOUS_SERVER`, `RELAY` |
| `EnergyConsumed` | Energy this node used during the run |
| `TransmittedDistance` | Total distance over which this node transmitted |
| `X` | X coordinate in the network plane (0–1) |
| `Y` | Y coordinate in the network plane (0–1) |
| `NeighborsCount` | Number of direct radio-range neighbours |
| `Goodness` | Computed goodness/trust value for this node (0–1) |

---

### Node Energy CSV — `node_energy.csv`

Compact version of *Node Data CSV* with energy data only.

| Column | Description |
|--------|-------------|
| `SimulationID` | Run number |
| `NodeID` | Sensor identifier |
| `NodeType` | Node role |
| `EnergyConsumed` | Energy consumed during this run |

---

### Node Data Text / Node Energy Text

Human-readable table versions of the two node-level CSVs above, formatted for direct reading in a terminal or text editor.

---

### Charts PNG — `charts.png`

Single image containing all charts for this export. See the next section for a description of each chart.

---

### Energy Consumption Graph PNG — `Energy Consumption.png`

Exported directly from the live in-app energy graph panel. Shows energy over simulation time as rendered by the application.

---

## Charts — detailed description

### 1 · Satisfaction over Simulation Runs

**What it shows:** How the average client satisfaction score evolved across all simulation runs.

| Visual element | Meaning |
|----------------|---------|
| Faint thin line | Raw satisfaction value for every individual run |
| Bold solid line | Rolling mean — smoothed trend (window size shown in legend, ≈ n/25 runs) |
| Shaded band | ±1 standard deviation around the rolling mean — the wider the band, the less consistent the model |
| Dashed grey line | Overall mean across all runs (value shown on left axis) |

**Y axis:** Satisfaction score (0 = completely unsatisfied, 1 = fully satisfied). Range adapts to the actual data spread — if all values are between 0.95 and 1.0, the axis zooms in there.

**X axis:** Sequential simulation run index (1 = first run, N = last run).

**How to read it:** A flat bold line with a narrow band = the model is stable. A declining trend = the model degrades over time (e.g. as malicious nodes learn to evade detection). High variance (wide band) = results depend heavily on the random network topology.

---

### 2 · Path Length over Simulation Runs

Same visual structure as the satisfaction chart above.

| Visual element | Meaning |
|----------------|---------|
| Faint thin line | Raw mean hop count per run |
| Bold solid line | Rolling mean of hop count |
| Shaded band | ±1σ — variability in routing efficiency |
| Dashed grey line | Overall mean path length |

**Y axis:** Average number of hops from client to chosen server. A path length of 1 means the client contacted a direct neighbour. Higher values mean the model routes through more intermediate relay nodes.

**How to read it:** Short path lengths are generally better (less energy, lower latency). A correlation between longer paths and lower satisfaction (visible when comparing this chart with chart 1) indicates that long routing chains reduce service quality.

---

### 3 · Satisfaction Distribution (Histogram)

**What it shows:** How often each satisfaction level occurred across all runs.

**X axis:** Satisfaction value divided into 10 equal buckets (0.0–0.1, 0.1–0.2, …, 0.9–1.0).

**Y axis:** Number of simulation runs that fell into each bucket.

**How to read it:** A model that always achieves high satisfaction will have a single tall bar on the right (near 1.0) and empty bars elsewhere. A mixed or unreliable model will have bars spread across the axis. A bimodal distribution (bars at both extremes) suggests the model either works or fails completely, with little middle ground.

---

### 4 · Scatter — Satisfaction vs Path Length

**What it shows:** The relationship between routing path length and the resulting satisfaction for each individual simulation run.

**X axis:** Average path length (hop count) for that run.

**Y axis:** Average satisfaction score for that run.

**How to read it:** Points in the top-left corner (short path, high satisfaction) represent ideal runs. A clear downward trend from left to right would mean longer paths cause lower satisfaction. Scattered points with no pattern indicate path length does not drive satisfaction in this model. Outlier points (long path but high satisfaction, or short path but low satisfaction) are worth investigating.

---

### 5 · Average Energy Consumption by Node Type

*(Present only when energy data is available — models: BTRM-WSN, EigenTrust, LFTM, PowerTrust)*

**What it shows:** Mean energy consumed per node role, averaged across all simulation runs.

| Bar | Node role |
|-----|-----------|
| **Client** | Sensor nodes that initiate service requests and run trust evaluation |
| **Malicious** | Server nodes that provide false or degraded service |
| **Benevolent** | Honest server nodes that provide correct service |
| **Relay** | Nodes that forward messages but do not serve requests directly |
| **Avg Sensor** | Mean across all sensor nodes regardless of role |
| **Pre-Trusted** | *(EigenTrust only)* Pre-trusted seed peers used to bootstrap trust |
| **Power Node** | *(PowerTrust only)* High-authority nodes with elevated trust weight |

**Y axis:** Raw energy units (model-dependent scale). Values are comparable only within a single trust model, not across models.

**How to read it:** Client energy is typically the highest because clients both transmit requests and run the trust computation. A very high malicious-server bar relative to benevolent-server can indicate the attacker is making many failed attempts. Large relay energy suggests the network topology forces many multi-hop routes.

---

### 6 · Fuzzy Satisfaction Distribution per Run

*(Present only for LFTM — Linguistic Fuzzy Trust Model)*

**What it shows:** For each simulation run, what fraction of clients fell into each fuzzy satisfaction category.

| Colour | Category | Meaning |
|--------|----------|---------|
| Dark green | Very High | Client is highly satisfied with the service |
| Light green | High | Client is mostly satisfied |
| Yellow | Medium | Client is moderately satisfied |
| Orange | Low | Client received poor service |
| Red | Very Low | Client received very poor or no service |

**X axis:** Simulation run index.

**Y axis:** Percentage of clients (0–100 %).

**How to read it:** A bar that is almost entirely dark green across all runs = the model reliably directs clients to trustworthy servers. Growing red/orange segments over time = the model is being deceived more as the simulation progresses. This chart is specific to LFTM because it uses a fuzzy linguistic satisfaction scale instead of a numeric score.

---

## Node types reference

| Type | Role in simulation |
|------|--------------------|
| **Client** | Initiates service requests; evaluates trust and selects a server |
| **Benevolent Server** | Provides correct service; contributes positively to trust |
| **Malicious Server** | Provides false or degraded service; attacks the trust system |
| **Relay** | Forwards messages between non-neighbouring nodes; does not serve requests |
| **Pre-Trusted Peer** | *(EigenTrust)* Seed node whose trust score is fixed at 1.0 to bootstrap the computation |
| **Power Node** | *(PowerTrust)* Node with elevated authority used to weight trust scores |

---

## Trust models reference

| Model | Full name | Notes |
|-------|-----------|-------|
| `PeerTrust` | PeerTrust | Feedback-based; uses transaction history and context factors |
| `BTRM_WSN` | Biologically-inspired TRM for WSN | Ant-colony inspired routing |
| `EigenTrust` | EigenTrust | Eigenvector-based global reputation; requires pre-trusted peers |
| `LFTM` | Linguistic Fuzzy Trust Model | Uses fuzzy logic; produces linguistic satisfaction categories |
| `PowerTrust` | PowerTrust | Uses power-law node distribution; requires power nodes |
| `TRIP` | TRIP | Threshold-based reputation |
