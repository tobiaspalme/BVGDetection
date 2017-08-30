package de.htwberlin.f4.ai.ma.node.fingerprint;

/**
 * Created by Johann Winter
 *
 * A SignalStrengthInformation consists of a MAC-Address and a signal strength in dBm
 */

public class SignalStrengthInformation {

    public String macAddress;
    public int signalStrength;

    public SignalStrengthInformation(String macAddress, int signalStrength) {
        this.macAddress = macAddress;
        this.signalStrength = signalStrength;
    }
}