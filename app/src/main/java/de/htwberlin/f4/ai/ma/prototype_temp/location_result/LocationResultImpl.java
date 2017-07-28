package de.htwberlin.f4.ai.ma.prototype_temp.location_result;

/**
 * a class for the location result with important information
 */
public class LocationResultImpl implements LocationResult{

    private int id;
    private String settings;
    private String measuredTime;
    private String selectedNode;
    private String measuredNode;

    public LocationResultImpl(int id, String settings, String measuredTime, String selectedNode, String measuredNode) {
        this.id = id;
        this.settings = settings;
        this.measuredTime = measuredTime;
        this.selectedNode = selectedNode;
        this.measuredNode = measuredNode;
    }

    public LocationResultImpl() {};

    public int getId() { return this.id; }
    public void setId(int id) {
        this.id = id;
    }

    public String getSettings() { return this.settings; }
    public void setSettings(String settings) { this.settings = settings; }

    public String getMeasuredTime() { return this.measuredTime; }
    public void setMeasuredTime(String measuredTime) {this.measuredTime = measuredTime; }

    public String getSelectedNode() { return this.selectedNode; }
    public void setSelectedNode(String selectedNode) { this.selectedNode = selectedNode; }

    public String getMeasuredNode() { return this.measuredNode; }
    public void setMeasuredNode(String measuredNode) { this.measuredNode = measuredNode; }
}
