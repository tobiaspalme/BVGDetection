package de.htwberlin.f4.ai.ma.fingerprint_generator.node;

/**
 * Created by Johann Winter
 */

// TODO: Public in package private ändern wenn Package vollständig
public class SignalStrengthInformation {

    public String macAdress;
    public int signalStrength;

    public SignalStrengthInformation(String macAdress, int signalStrength) {
        this.macAdress = macAdress;
        this.signalStrength = signalStrength;
    }
}