package de.htwberlin.f4.ai.ma.indoorroutefinder.fingerprint;

import java.util.List;

/**
 * Created by Johann Winter
 *
 * This interface is for fingerprints.
 * A fingerprint contains a String for the name of the measured WiFi
 * and a list of SignalSample which contain each a timestamp and a list
 * of measured MAC addresses (with the same SSID) in combination with a signal strength value.
 * If the fingerprint is captured without a SSID-filter, getSsid() will return null.
 */

public interface Fingerprint {

    String getSsid();
    List<SignalSample> getSignalSampleList();
}
