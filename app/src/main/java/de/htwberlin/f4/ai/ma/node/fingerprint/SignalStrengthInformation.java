package de.htwberlin.f4.ai.ma.node.fingerprint;

/**
 * Created by Johann Winter
 */

public class SignalStrengthInformation {

    public String macAddress;
    public int signalStrength;

    public SignalStrengthInformation(String macAddress, int signalStrength) {
        this.macAddress = macAddress;
        this.signalStrength = signalStrength;
    }
}