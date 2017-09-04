package de.htwberlin.f4.ai.ma.fingerprint;

import java.util.List;

import de.htwberlin.f4.ai.ma.fingerprint.accesspointsample.AccessPointSample;

/**
 * Created by Johann Winter
 *
 * A SignalInformation consists of a timestamp and a list of AccessPointSamples at that time.
 */


public class SignalInformation {

    private String timestamp;
    private List<AccessPointSample> accessPointSampleList;


    public SignalInformation(String timestamp, List<AccessPointSample> accessPointSamples) {
        this.timestamp = timestamp;
        this.accessPointSampleList = accessPointSamples;
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
    public List<AccessPointSample> getAccessPointSampleList() {
        return accessPointSampleList;
    }


}