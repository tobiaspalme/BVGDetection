package de.htwberlin.f4.ai.ma.fingerprint;

import java.util.List;

/**
 * Created by Johann Winter
 *
 * A fingerprint consists of a WiFi-Name (ssid) and a list of SignalInformations.
 */

class FingerprintImpl implements Fingerprint{

    private String ssid;
    private List<SignalSample> signalSampleList;


    FingerprintImpl(String ssid, List<SignalSample> signalSampleList) {
        this.ssid = ssid;
        this.signalSampleList = signalSampleList;
    }

    /**
     * Getter for the WiFi name
     * @return the WiFi name
     */
    public String getSsid() {
        return this.ssid;
    }


    /**
     * Getter for the list of SignalInformations
     * @return the list of SignalInformations
     */
    public List<SignalSample> getSignalSampleList() {
        return this.signalSampleList;
    }


    //public void setWifiName(String ssid) {
   //     this.ssid = ssid;
   // }


}
