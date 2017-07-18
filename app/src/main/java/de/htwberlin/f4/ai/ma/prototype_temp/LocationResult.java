package de.htwberlin.f4.ai.ma.prototype_temp;

/**
 * a class for the location result with important information
 */
public class LocationResult {
    public String settings;
    public String measuredTime;
    public String poi;
    public String measuredPoi;

    public LocationResult(String settings, String measuredTime, String poi, String measuredPoi) {
        this.settings = settings;
        this.measuredTime = measuredTime;
        this.poi = poi;
        this.measuredPoi = measuredPoi;
    }
}
