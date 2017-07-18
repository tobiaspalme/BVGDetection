package de.htwberlin.f4.ai.ma.fingerprint_generator.fingerprint;

/**
 * Created by Johann Winter
 */

public class FingerprintFactory {

    public static Fingerprint getFingerprint() {
        return new FingerprintImplementation();
    }
}

