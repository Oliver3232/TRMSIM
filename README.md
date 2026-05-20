# TRMSim-WSN — Modified Simulator

This is a **modified version** of [TRMSim-WSN](https://github.com/LefKok/TRMSIM) (Trust and Reputation Models Simulator for Wireless Sensor Networks), extended as part of a bachelor's thesis at [your university].

Modified version repository: https://github.com/Oliver3232/TRMSIM

---

## Original project

TRMSim-WSN was created by:

- **Félix Gómez Mármol**
- **Gregorio Martínez Pérez**
- **Antonio Bernárdez**

Original source: https://github.com/LefKok/TRMSIM

The original simulator is distributed under the GNU Lesser General Public License v3 or later, with additional attribution requirements. See [LICENSE](LICENSE) and [NOTICE.md](NOTICE.md) for full details.

---

## What was changed

This version adds the following on top of the original TRMSim-WSN:

- **Two new trust models**: BayesTrust (Bayesian inference) and SVMTrust (SVM classification via LIBSVM)
- **Dual simulation mode**: run and compare two trust model configurations side by side
- **Headless batch runner**: run scenarios from the command line without a GUI, export results as CSV/TSV
- **Scalability benchmark runner**: automated multi-model comparison across network sizes
- **Predefined scenarios**: drone-network scenarios and large-scale scenarios bundled as `.properties` files
- **Export pipeline**: export simulation outcomes, energy consumption, and node-level metrics to CSV, TSV, and PNG
- **Refactored GUI**: modular main-window architecture (controllers, sections, hosts); FlatLaf modern look-and-feel; fullscreen graph mode; embedded node inspector
- **Energy consumption tracking**: per-sensor energy accounting across all trust models

A full list of all added, modified, moved, and deleted source files is in [MODIFICATIONS.md](MODIFICATIONS.md).

---

## Requirements

- Java 17 or later (JDK)
- Maven 3.6 or later
- Internet connection for the first build (Maven downloads dependencies)

The project uses the following libraries (see [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md)):

| Library | Version | License |
|---------|---------|---------|
| FlatLaf | 3.5.2 | Apache-2.0 |
| OpenJFX (javafx-controls, javafx-swing) | 21.0.5 | GPL-2.0 with Classpath Exception |
| LIBSVM | 3.35 | BSD 3-Clause |

---

## Building and running

### 1. Clone the repository

```bash
git clone https://github.com/Oliver3232/TRMSIM.git
cd TRMSIM
git checkout development
```

> **Note:** The `master` branch contains the original unmodified codebase (kept for reference). All new features and fixes are on the `development` branch — always switch to it after cloning.

### 2. Build with Maven

Maven automatically downloads all dependencies from Maven Central on the first build.

```bash
cd TRM
mvn clean package -q
```

This produces two JARs in `TRM/target/`:
- `TRM-0.0.1-SNAPSHOT.jar` — thin JAR (requires classpath)
- `TRM-0.0.1-SNAPSHOT-jar-with-dependencies.jar` — fat JAR with all dependencies bundled

### 3. Run the simulator (GUI)

Run from the `TRMSIM/` root directory:

```bash
java -jar TRM/target/TRM-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

Or if you are already inside the `TRM/` directory:

```bash
cd ..
java -jar TRM/target/TRM-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

On **macOS** add `-XstartOnFirstThread`:

```bash
java -XstartOnFirstThread -jar TRM/target/TRM-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

### 4. Run from your IDE

**IntelliJ IDEA:**

1. `File → Open` → select the **`TRMSIM/`** root directory (not `TRM/pom.xml`) → open it as a plain directory project
2. In the Project panel, right-click `TRM/pom.xml` → **Add as Maven Project**
3. IntelliJ imports all dependencies automatically

Opening the root directory ensures IntelliJ places `.idea/` in `TRMSIM/` where it is covered by `.gitignore`. Opening `TRM/pom.xml` directly would create `.idea/` inside `TRM/` instead.

**Eclipse:**

`File → Import → Existing Maven Projects` → select the `TRM/` directory

**Main class:** `es.ants.felixgm.trmsim_wsn.gui.TRMSim_WSN`

> **Note for OpenJFX users**: if you run directly from the IDE without the fat JAR, you may need to add VM options for the JavaFX module path. IntelliJ IDEA with Maven resolves this automatically when you use the Maven-generated classpath.

### 5. Headless batch run (no GUI)

```bash
java -jar TRM/target/TRM-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
     --headless-batch-scenario large-scale-fast-peertrust PeerTrust
```

To control network sizes and execution count:

```bash
java -jar TRM/target/TRM-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
     --headless-batch-scenario large-scale-fast-peertrust PeerTrust "" 1 100
```

You can replace `PeerTrust` with any of the supported trust models, or combine multiple models separated by commas:

```bash
# Single model
java -jar TRM/target/TRM-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
     --headless-batch-scenario large-scale-fast-peertrust EigenTrust

# Multiple models compared side by side
java -jar TRM/target/TRM-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
     --headless-batch-scenario large-scale-fast-peertrust PeerTrust,EigenTrust,BTRM_WSN
```

Available trust models: `PeerTrust`, `BTRM_WSN`, `EigenTrust`, `LFTM`, `PowerTrust`, `TRIP`, `BayesTrust`, `SVMTrust`

Full syntax: `--headless-batch-scenario <scenarioId> <models> [sizesCsv] [numNetworks] [numExecutions] [outputDir]`

- `scenarioId` — name of the `.properties` file without extension (e.g. `large-scale-fast-peertrust`)
- `models` — comma-separated trust model names: `PeerTrust`, `BTRM_WSN`, `EigenTrust`, `LFTM`, `PowerTrust`, `TRIP`, `BayesTrust`, `SVMTrust`
- `sizesCsv` — comma-separated network sizes (empty = scenario default)
- `numNetworks` — number of networks per execution (default from scenario)
- `numExecutions` — number of executions (e.g. `100`)
- `outputDir` — output directory for results (default: `docs/headless-batch`). The path is relative to the directory from which you run the command. The directory is created automatically if it does not exist. For each model, the runner writes:
  - `<scenarioId>-<profile>-<model>-raw.csv` — per-execution raw data
  - `<scenarioId>-<profile>-<model>-graph.tsv` — aggregated data for plotting
  - `<scenarioId>-<profile>-<model>-summary.md` — human-readable summary
  - `<scenarioId>-<profile>-<model>-eigentrust-profile.csv` — EigenTrust only

---

## Project structure

```
TRMSIM/
├── README.md                      This file
├── LICENSE.md                     Project license (LGPL v3 + additional terms)
├── NOTICE.md                      Original author attribution
├── MODIFICATIONS.md               Full list of changes vs. original TRMSim-WSN
├── THIRD_PARTY_NOTICES.md         Third-party library licenses
├── EXPORT_GUIDE.md                Description of all export file formats and charts
├── simulation_results/            Export output directory (auto-created on first export)
└── TRM/
    ├── pom.xml                        Maven build descriptor
    └── src/
        ├── es/ants/felixgm/trmsim_wsn/
        │   └── gui/
        │       └── TRMSim_WSN.java       Main class (entry point)
        └── resources/
            ├── lgpl.txt                  GNU LGPL v3 full text
            ├── trmsim-wsn_license.txt    Original TRMSim-WSN license header
            └── scenarios/               Predefined scenario files
```

---

## Export guide

All export formats produced by the simulator are documented in [EXPORT_GUIDE.md](EXPORT_GUIDE.md).

It covers every output file (CSV, TSV, TXT, PNG), the meaning of each column, and a detailed description of each chart — including how to read the rolling-mean satisfaction curve, the path-length trend, the scatter plot, the energy bar chart, and the LFTM fuzzy distribution stacked bar.

Export runs are saved under `simulation_results/report_<timestamp>/`. Each subdirectory also contains a `simulation_params.txt` file that records the exact trust model, scenario, and parameter values used for that run.

---

## License

This project is distributed under the **GNU Lesser General Public License v3 or later**, with the additional attribution terms required by the original TRMSim-WSN license.

See [LICENSE.md](LICENSE.md) for the full license text and [NOTICE.md](NOTICE.md) for attribution requirements.
