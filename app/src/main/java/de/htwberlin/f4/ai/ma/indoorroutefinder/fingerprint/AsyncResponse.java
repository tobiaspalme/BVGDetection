package de.htwberlin.f4.ai.ma.indoorroutefinder.fingerprint;

/**
 * Created by Johann Winter
 */

public interface AsyncResponse {

    /**
     * Controls the asynchronous response to the activity which started
     * the fingerprinting thread when fingerprinting finished (or got aborted)
     * @param fingerprint the fingerprint, in case of an abort it is null
     * @param seconds the duration of the measurement
     */
    void processFinish(Fingerprint fingerprint, int seconds);
}
