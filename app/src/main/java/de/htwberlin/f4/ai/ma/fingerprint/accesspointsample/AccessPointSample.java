package de.htwberlin.f4.ai.ma.fingerprint.accesspointsample;

/**
 * Created by Johann Winter
 *
 * A AccessPointSample consists of a MAC-Address and a RSSI (signal strength) in dBm.
 */

public interface AccessPointSample {

    int getRSSI();
    String getMacAddress();

}
