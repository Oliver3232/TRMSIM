# TRMSIM

Trust and Reputation Models Simulator for Wireless Sensor Networks.

## Requirements

- Java 17+
- Maven 3.6+

## Build and run

```bash
cd TRM
mvn clean package -q
java -jar target/TRM-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

On **macOS** add `-XstartOnFirstThread`:

```bash
java -XstartOnFirstThread -jar target/TRM-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

Main class: `es.ants.felixgm.trmsim_wsn.gui.TRMSim_WSN`
