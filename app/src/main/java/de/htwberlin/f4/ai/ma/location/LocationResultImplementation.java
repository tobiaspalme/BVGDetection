package de.htwberlin.f4.ai.ma.location;

/**
 * a class for the location result with important information
 */
public class LocationResultImplementation implements LocationResult{

    private int id;
    private String settings;
    private String measuredTime;
    private String measuredNode;
    private double percentage;

    LocationResultImplementation(int id, String settings, String measuredTime, String measuredNode, double percentage) {
        this.id = id;
        this.settings = settings;
        this.measuredTime = measuredTime;
        this.measuredNode = measuredNode;
        this.percentage = percentage;
    }

    public LocationResultImplementation() {}

    public int getId() { return this.id; }
    public void setId(int id) {
        this.id = id;
    }

    public String getSettings() { return this.settings; }
    public void setSettings(String settings) { this.settings = settings; }

    public String getMeasuredTime() { return this.measuredTime; }
    public void setMeasuredTime(String measuredTime) {this.measuredTime = measuredTime; }

    public String getMeasuredNode() { return this.measuredNode; }
    public void setMeasuredNode(String measuredNode) { this.measuredNode = measuredNode; }

    @Override
    public double getPercentage() {
        return this.percentage;
    }

    @Override
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
