package de.htwberlin.f4.ai.ma.location.locationresult;

/**
 * Created by Johann Winter
 */

public class LocationResultFactory {

    public static LocationResult createInstance() {
        return new LocationResultImpl();
    }

    public static LocationResult createInstance(int id, String settings, String measuredTime, String measuredNode, double percentage) {
        return new LocationResultImpl(id, settings, measuredTime, measuredNode, percentage);
    }

}
