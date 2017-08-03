package de.htwberlin.f4.ai.ma.fingerprint_generator.fingerprint;

/**
 * Created by Johann Winter
 */

public final class FingerprintFactory {

    private static Fingerprint instance;

    public static Fingerprint getInstance() {
        if (instance == null) {
            instance = new FingerprintImplementation();
        }
        return instance;
    }
}

