package de.htwberlin.f4.ai.ma.node;

import java.util.List;

/**
 * Created by Johann Winter
 */


// TODO: Public in package private ändern wenn Package vollständig

public class SignalInformation {

    private String timestamp;
    private List<SignalStrengthInformation> signalStrengthInfoList;


    public SignalInformation(String timestamp, List<SignalStrengthInformation> signalStrengthInformationList) {
        this.timestamp = timestamp;
        this.signalStrengthInfoList = signalStrengthInformationList;
    }


    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<SignalStrengthInformation> getSignalStrengthInfoList() {
        return signalStrengthInfoList;
    }

    public void setSignalStrengthInfoList(List<SignalStrengthInformation> signalStrengthInfoList) {
        this.signalStrengthInfoList = signalStrengthInfoList;
    }
}