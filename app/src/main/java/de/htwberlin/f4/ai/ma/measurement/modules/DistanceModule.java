package de.htwberlin.f4.ai.ma.measurement.modules;

/**
 * DistanceModule Interface
 *
 * used to get the distance traveled
 *
 * Author: Benjamin Kneer
 */

public interface DistanceModule{

    // get distance
    float getDistance(boolean stairs);

    // start module
    void start();

    // stop module
    void stop();
}
