package de.htwberlin.f4.ai.ma.WifiScanner;

import android.net.wifi.WifiManager;

import java.util.List;

/**
 * Created by Johann Winter
 *
 * The WifiScanner scans for available WiFi networks und returns a list of them.
 * It needs to be passed a WifiManager object and a boolean. If the boolean "onlyNetworksWithTwoOrMoreAPs"
 * is set true, the WifiScanner counts the occurrences of the SSIDs and returns a list which contains
 * only the SSIDs which have two or more BSSIDs (MAC-Addresses, Accesspoints).
 */

public interface WifiScanner {

    List<String> getAvailableNetworks(WifiManager wifiManager, boolean onlyNetworksWithTwoOrMoreAPs);
}
