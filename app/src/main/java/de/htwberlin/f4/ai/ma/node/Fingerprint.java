package de.htwberlin.f4.ai.ma.node;

import java.util.List;

/**
 * Created by Johann Winter
 */

public class Fingerprint {

    private List<SignalInformation> signalInformationList;


    public Fingerprint(List<SignalInformation> signalInformationList) {
        this.signalInformationList = signalInformationList;
    }


    public List<SignalInformation> getSignalInformationList() {
        return this.signalInformationList;
    }

    public void setSignalInformationList(List<SignalInformation> signalInformationList) {
        this.signalInformationList = signalInformationList;
    }


    /*
    private String timestamp;
    private List<SignalStrengthInformation> signalStrengthInformationList;


    public Fingerprint(String timestamp, List<SignalStrengthInformation> signalStrengthInformationList) {
        this.timestamp = timestamp;
        this.signalStrengthInformationList = signalStrengthInformationList;
    }


    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<SignalStrengthInformation> getSignalStrengthInformationList() {
        return signalStrengthInformationList;
    }

    public void setSignalStrengthInfoList(List<SignalStrengthInformation> signalStrengthInformationList) {
        this.signalStrengthInformationList = signalStrengthInformationList;
    } */
}
