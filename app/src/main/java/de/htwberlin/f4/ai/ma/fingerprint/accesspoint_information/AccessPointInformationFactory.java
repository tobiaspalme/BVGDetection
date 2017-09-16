package de.htwberlin.f4.ai.ma.fingerprint.accesspoint_information;

/**
 * Created by Johann Winter
 */

public class AccessPointInformationFactory {

    public static AccessPointInformation createInstance(String macAddress, int RSSI) {
        return new AccessPointInformationImpl(macAddress, RSSI);
    }
}
