package de.htwberlin.f4.ai.ma.wifi_scanner;

/**
 * Created by user Johann Winter
 */

public class WifiScannerFactory {

    public static WifiScanner createInstance() {
        WifiScannerImpl wifiScannerImpl = new WifiScannerImpl();
        return wifiScannerImpl;
    }
}