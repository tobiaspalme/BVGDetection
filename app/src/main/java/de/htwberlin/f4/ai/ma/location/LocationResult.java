package de.htwberlin.f4.ai.ma.location;

/**
 * Created by Johann Winter
 */

public interface LocationResult {

    int getId();
    void setId(int id);

    String getSettings();
    void setSettings(String settings);

    String getMeasuredTime();
    void setMeasuredTime(String measuredTime);

    String getSelectedNode();
    void setSelectedNode(String selectedNode);

    String getMeasuredNode();
    void setMeasuredNode(String measuredNode);
}
