package de.htwberlin.f4.ai.ma.fingerprint_generator.node;

import java.util.List;

/**
 * Created by Johann Winter
 */


// TODO: Public in package private ändern wenn Package vollständig

public class SignalInformation {

    public String timestamp;
    public List<SignalStrengthInformation> signalStrengthInformationList;

    public SignalInformation(String timestamp, List<SignalStrengthInformation> signalStrengthInformationList) {
        this.timestamp = timestamp;
        this.signalStrengthInformationList = signalStrengthInformationList;
    }
}