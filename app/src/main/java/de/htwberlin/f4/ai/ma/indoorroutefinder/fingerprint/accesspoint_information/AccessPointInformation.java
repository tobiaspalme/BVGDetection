package de.htwberlin.f4.ai.ma.indoorroutefinder.fingerprint.accesspoint_information;

/**
 * Created by Johann Winter
 *
 * A AccessPointInformation consists of a MAC-Address and a RSSI (signal strength) in dBm.
 */

public interface AccessPointInformation {

    String getMacAddress();
    int getRssi();
    double getMilliwatt();

}
