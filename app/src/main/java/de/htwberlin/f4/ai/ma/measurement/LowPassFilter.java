package de.htwberlin.f4.ai.ma.measurement;

/**
 * Lowpass filter to smooth out noise from sensors.
 * Source: Professional Android Sensor Programming pp. 108
 */

public class LowPassFilter {

    public static float filter(float lastVal, float currentVal, float weightingVal) {
        return lastVal * (1.0f - weightingVal) + currentVal * weightingVal;
    }
}
