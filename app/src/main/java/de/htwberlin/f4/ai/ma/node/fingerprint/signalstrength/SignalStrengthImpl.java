package de.htwberlin.f4.ai.ma.node.fingerprint.signalstrength;

/**
 * Created by Johann Winter
 *
 * A SignalStrengthImpl consists of a MAC-Address and a signal strength (RSSI) in dBm
 */

public class SignalStrengthImpl implements SignalStrength {

    private String macAddress;
    private int RSSI;

    public SignalStrengthImpl(String macAddress, int RSSI) {
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