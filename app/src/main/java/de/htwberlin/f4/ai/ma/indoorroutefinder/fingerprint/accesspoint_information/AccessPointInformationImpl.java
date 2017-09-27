package de.htwberlin.f4.ai.ma.indoorroutefinder.fingerprint.accesspoint_information;

/**
 * Created by Johann Winter
 *
 */

class AccessPointInformationImpl implements AccessPointInformation {

    private String macAddress;
    private int rssi;

    AccessPointInformationImpl(String macAddress, int rssi) {
        this.macAddress = macAddress;
        this.rssi = rssi;
    }


    public int getRssi() {
        return this.rssi;
    }

    public String getMacAddress() {
        return this.macAddress;
    }

    // Convert the rssi (dBm) to milliwatt
    public double getMilliwatt() {
        return Math.pow(10, this.getRssi()/10);
    }

}