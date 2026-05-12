# TRMSim-WSN — Modified Simulator

This is a **modified version** of [TRMSim-WSN](https://github.com/LefKok/TRMSIM) (Trust and Reputation Models Simulator for Wireless Sensor Networks), extended as part of a bachelor's thesis at [your university].

Modified version repository: https://github.com/Oliver3232/TRMSIM

---

## Original project

TRMSim-WSN was created by:

- **Félix Gómez Mármol** — [ants.dif.um.es/~felixgm](http://ants.dif.um.es/~felixgm/en)
- **Gregorio Martínez Pérez** — [webs.um.es/gregorio](http://webs.um.es/gregorio)
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
```

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

```bash
java -jar TRM/target/TRM-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

On **macOS** add `-XstartOnFirstThread`:

```bash
java -XstartOnFirstThread -jar TRM/target/TRM-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

### 4. Run from your IDE

Import the project as a **Maven project** in IntelliJ IDEA or Eclipse. The IDE will automatically import all dependencies defined in `pom.xml`.

- IntelliJ IDEA: `File → Open` → select `TRM/pom.xml` → `Open as Project`
- Eclipse: `File → Import → Existing Maven Projects` → select the `TRM/` directory

Main class: `es.ants.felixgm.trmsim_wsn.gui.TRMSim_WSN`

> **Note for OpenJFX users**: if you run directly from the IDE without the fat JAR, you may need to add VM options for the JavaFX module path. IntelliJ IDEA with Maven resolves this automatically when you use the Maven-generated classpath.

### 5. Headless batch run (no GUI)

```bash
java -cp TRM/target/TRM-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
     es.ants.felixgm.trmsim_wsn.ClientExecutionSupport \
     --scenario TRM/src/resources/scenarios/large-scale-fast-peertrust.properties \
     --runs 100
```

---

## Project structure

```
TRMSIM/
├── README.md                      This file
├── LICENSE.md                     Project license (LGPL v3 + additional terms)
├── NOTICE.md                      Original author attribution
├── MODIFICATIONS.md               Full list of changes vs. original TRMSim-WSN
├── THIRD_PARTY_NOTICES.md         Third-party library licenses
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

## License

This project is distributed under the **GNU Lesser General Public License v3 or later**, with the additional attribution terms required by the original TRMSim-WSN license.

See [LICENSE.md](LICENSE.md) for the full license text and [NOTICE.md](NOTICE.md) for attribution requirements.
