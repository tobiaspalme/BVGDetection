package de.htwberlin.f4.ai.ma.indoorroutefinder.measurement.modules;

/**
 * OrientationModule Interface
 *
 * used to get the users heading direction
 *
 * Author: Benjamin Kneer
 */

public interface OrientationModule{

    // get the azimuth, pitch, roll
    float[] getOrientation();

    // start module
    void start();

    // stop module
    void stop();
}
