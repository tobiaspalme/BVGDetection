package de.htwberlin.f4.ai.ma.location.locationresult;

/**
 * Created by Johann Winter
 *
 * This interface handles the management of LocationResults.
 * LocationResults are created when the user locates his position.
 *
 * There are getters and setters for the ID, settings used to measure the location,
 * time of the measurement, the ID of the resulting Node and the percentage of how probable
 * the found result node is.
 */

public interface LocationResult {

    int getId();
    void setId(int id);

    String getSettings();
    void setSettings(String settings);

    String getMeasuredTime();
    void setMeasuredTime(String measuredTime);

    String getMeasuredNode();
    void setMeasuredNode(String measuredNode);

    double getPercentage();
    void setPercentage(double percentage);
}
