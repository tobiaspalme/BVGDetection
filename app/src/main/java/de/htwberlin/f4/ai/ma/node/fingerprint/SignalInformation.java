package de.htwberlin.f4.ai.ma.node.fingerprint;

import java.util.List;

/**
 * Created by Johann Winter
 *
 * A SignalInformation consists of a timestamp and a list of SignalStrengths at this time.
 */


public class SignalInformation {

    private String timestamp;
    private List<SignalStrengthInformation> signalStrengthInfoList;


    public SignalInformation(String timestamp, List<SignalStrengthInformation> signalStrengthInformationList) {
        this.timestamp = timestamp;
        this.signalStrengthInfoList = signalStrengthInformationList;
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
     * @param timestamp
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


    /**
     * Getter for the list of SignalStrengths
     * @return the list of SignalStrengths
     */
    public List<SignalStrengthInformation> getSignalStrengthInfoList() {
        return signalStrengthInfoList;
    }


    //public void setSignalStrengthInfoList(List<SignalStrengthInformation> signalStrengthInfoList) {
    //    this.signalStrengthInfoList = signalStrengthInfoList;
    //}
}