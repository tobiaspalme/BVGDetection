package de.htwberlin.f4.ai.ma.fingerprint_generator.fingerprint;

/**
 * Created by Johann Winter
 */

class MeasuredNode {

    String macAdress;
    int signalStrength;

    // TODO: evtl. private?
    MeasuredNode(String macAdress, int signalStrength) {
        this.macAdress = macAdress;
        this.signalStrength = signalStrength;
    }
}