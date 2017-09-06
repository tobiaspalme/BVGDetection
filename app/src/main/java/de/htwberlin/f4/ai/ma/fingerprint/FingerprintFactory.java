package de.htwberlin.f4.ai.ma.fingerprint;

import java.util.List;

/**
 * Created by Johann Winter
 */

public class FingerprintFactory {

    public static Fingerprint createInstance(String wifiName, List<SignalInformation> signalInformationList) {
        return new FingerprintImpl(wifiName, signalInformationList);
    }
}
