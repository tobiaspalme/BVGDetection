package de.htwberlin.f4.ai.ma.node.fingerprint;

import android.net.wifi.WifiManager;

/**
 * Created by Johann Winter
 *
 * This Interface makes it possible to create a FingerprintImpl of a specific SSID (wifiName).
 * The seconds parameter of the getFingerprintImpl method specifies the measuring time.
 * The WifiManager parameter has to be passed to get access to the WiFi module of the device.
 */

public interface FingerprintGenerator {

    FingerprintImpl getFingerprint(final String wifiName, final int seconds, final WifiManager wifiManager);

}
