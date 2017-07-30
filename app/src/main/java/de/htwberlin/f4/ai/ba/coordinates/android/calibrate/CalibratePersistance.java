package de.htwberlin.f4.ai.ba.coordinates.android.calibrate;

/**
 * Created by benni on 30.07.2017.
 */

public interface CalibratePersistance {

    boolean load();
    void save(float stepLength, int stepPeriod);
    float getStepLength();
    int getStepPeriod();
}
