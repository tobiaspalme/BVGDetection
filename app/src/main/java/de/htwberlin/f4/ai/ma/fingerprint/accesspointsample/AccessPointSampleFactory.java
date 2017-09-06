package de.htwberlin.f4.ai.ma.fingerprint.accesspointsample;

/**
 * Created by Johann Winter
 */

public class AccessPointSampleFactory {

    public static AccessPointSample createInstance(String macAddress, int RSSI) {
        return new AccessPointSampleImpl(macAddress, RSSI);
    }
}
