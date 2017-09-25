package de.htwberlin.f4.ai.ma.indoorroutefinder.fingerprint;

/**
 * Created by Johann Winter
 */

public interface AsyncResponse {

    void processFinish(Fingerprint fingerprint, int seconds);
}
