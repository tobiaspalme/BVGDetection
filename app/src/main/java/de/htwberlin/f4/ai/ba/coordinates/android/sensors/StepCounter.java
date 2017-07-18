package de.htwberlin.f4.ai.ba.coordinates.android.sensors;

/**
 * Created by benni on 18.07.2017.
 */

public interface StepCounter {

    void start();
    void stop();
    int getStepCount();
}
