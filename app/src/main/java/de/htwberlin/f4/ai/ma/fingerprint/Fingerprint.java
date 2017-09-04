package de.htwberlin.f4.ai.ma.fingerprint;

import java.util.List;

/**
 * Created by Johann Winter
 *
 * This interface is for fingerprints.
 * A fingerprint contains a String for the name of the measured WiFi
 * and a list of SignalInformation which contain each a timestamp and a list
 * of measured MAC addresses (with the same SSID) in combination with a signal strength value.
 */

public interface Fingerprint {

    String getWifiName();
    List<SignalInformation> getSignalInformationList();
}
