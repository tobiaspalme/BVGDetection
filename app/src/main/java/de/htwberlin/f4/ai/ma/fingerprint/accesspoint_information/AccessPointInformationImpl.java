package de.htwberlin.f4.ai.ma.fingerprint.accesspoint_information;

/**
 * Created by Johann Winter
 *
 * A AccessPointInformationImpl consists of a MAC-Address and a signal strength (rssi) in dBm.
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