package de.htwberlin.f4.ai.ma.indoorroutefinder.fingerprint;

import java.util.List;

/**
 * Created by Johann Winter
 *
 * Factory for creating fingerprints
 */

public class FingerprintFactory {

    public static Fingerprint createInstance(String wifiName, List<SignalSample> signalSampleList) {
        return new FingerprintImpl(wifiName, signalSampleList);
    }
}
