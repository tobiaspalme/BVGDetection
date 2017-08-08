package de.htwberlin.f4.ai.ma.persistence.fingerprint;

/**
 * Created by Johann Winter
 */

public class MeasuredNode {

    String macAdress;
    int signalStrength;

    // TODO: evtl. private?
    public MeasuredNode(String macAdress, int signalStrength) {
        this.macAdress = macAdress;
        this.signalStrength = signalStrength;
    }
}