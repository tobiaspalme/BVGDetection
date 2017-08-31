package de.htwberlin.f4.ai.ma.node.fingerprint;

/**
 * Created by Johann Winter
 */

public interface AsyncResponse {
    void processFinish(Fingerprint fingerprint, int seconds);
}
