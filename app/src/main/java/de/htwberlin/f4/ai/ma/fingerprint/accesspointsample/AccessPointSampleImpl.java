package de.htwberlin.f4.ai.ma.fingerprint.accesspointsample;

/**
 * Created by Johann Winter
 *
 * A AccessPointSampleImpl consists of a MAC-Address and a signal strength (RSSI) in dBm
 */

public class AccessPointSampleImpl implements AccessPointSample {

    private String macAddress;
    private int RSSI;

    public AccessPointSampleImpl(String macAddress, int RSSI) {
        this.macAddress = macAddress;
        this.RSSI = RSSI;
    }


    public int getRSSI() {
        return this.RSSI;
    }

    public String getMacAddress() {
        return this.macAddress;
    }

}