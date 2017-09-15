package de.htwberlin.f4.ai.ma.fingerprint;

import java.util.List;

/**
 * Created by Johann Winter
 *
 * A fingerprint consists of a WiFi-Name (SSID) and a list of SignalInformations.
 */

class FingerprintImpl implements Fingerprint{

    private String wifiName;
    private List<SignalInformation> signalInformationList;


    FingerprintImpl(String wifiName, List<SignalInformation> signalInformationList) {
        this.wifiName = wifiName;
        this.signalInformationList = signalInformationList;
    }

    /**
     * Getter for the WiFi name
     * @return the WiFi name
     */
    public String getSSID() {
        return this.wifiName;
    }


    /**
     * Getter for the list of SignalInformations
     * @return the list of SignalInformations
     */
    public List<SignalInformation> getSignalInformationList() {
        return this.signalInformationList;
    }


    //public void setWifiName(String wifiName) {
   //     this.wifiName = wifiName;
   // }


}
