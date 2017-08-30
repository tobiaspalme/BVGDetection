package de.htwberlin.f4.ai.ma.node.fingerprint.signalstrength;

/**
 * Created by Johann Winter
 *
 * A SignalStrength consists of a MAC-Address and a RSSI (signal strength) in dBm.
 *
 */

public interface SignalStrength {

    int getRSSI();
    String getMacAddress();

}
