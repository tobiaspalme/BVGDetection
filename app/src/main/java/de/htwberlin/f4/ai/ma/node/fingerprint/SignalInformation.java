package de.htwberlin.f4.ai.ma.node.fingerprint;

import java.util.List;

import de.htwberlin.f4.ai.ma.node.fingerprint.signalstrength.SignalStrength;

/**
 * Created by Johann Winter
 *
 * A SignalInformation consists of a timestamp and a list of SignalStrengths at this time.
 */


public class SignalInformation {

    private String timestamp;
    private List<SignalStrength> signalStrengthList;


    public SignalInformation(String timestamp, List<SignalStrength> signalStrengths) {
        this.timestamp = timestamp;
        this.signalStrengthList = signalStrengths;
    }

    /**
     * Getter for the timestamp
     * @return the timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Setter for the timestamp
     * @param timestamp the timestamp
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


    /**
     * Getter for the list of SignalStrengths
     * @return the list of SignalStrengths
     */
    public List<SignalStrength> getSignalStrengthList() {
        return signalStrengthList;
    }


}