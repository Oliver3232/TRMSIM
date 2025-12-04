package es.ants.felixgm.trmsim_wsn.outcomes;

public class NodeMetric {
    private int id;
    private String type;
    private double consumedEnergy;
    private double transmittedDistance;

    // --- NOVÉ POLIA ---
    private double x;
    private double y;
    private int neighborsCount;
    private double goodness; // -1 ak neponúka službu

    public NodeMetric(int id, String type, double consumedEnergy, double transmittedDistance,
                      double x, double y, int neighborsCount, double goodness) {
        this.id = id;
        this.type = type;
        this.consumedEnergy = consumedEnergy;
        this.transmittedDistance = transmittedDistance;
        this.x = x;
        this.y = y;
        this.neighborsCount = neighborsCount;
        this.goodness = goodness;
    }

    public int getId() { return id; }
    public String getType() { return type; }
    public double getConsumedEnergy() { return consumedEnergy; }
    public double getTransmittedDistance() { return transmittedDistance; }

    // --- NOVÉ GETTERY ---
    public double getX() { return x; }
    public double getY() { return y; }
    public int getNeighborsCount() { return neighborsCount; }
    public double getGoodness() { return goodness; }

    @Override
    public String toString() {
        return String.format("Node %d [%s]: Energy=%.4f, Neigh=%d", id, type, consumedEnergy, neighborsCount);
    }
}