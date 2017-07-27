package de.htwberlin.f4.ai.ma.prototype_temp.location_result;

/**
 * Created by Johann Winter
 */

public interface LocationResult {

    String getSettings();
    void setSettings(String settings);

    String getMeasuredTime();
    void setMeasuredTime(String measuredTime);

    String getSelectedNode();
    void setSelectedNode(String selectedNode);

    String getMeasuredNode();
    void setMeasuredNode(String measuredNode);
}
