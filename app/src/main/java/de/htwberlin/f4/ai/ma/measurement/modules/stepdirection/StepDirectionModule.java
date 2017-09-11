package de.htwberlin.f4.ai.ma.measurement.modules.stepdirection;

import de.htwberlin.f4.ai.ma.android.sensors.Sensor;

/**
 * StepDirectionModule Interface
 *
 * used to detect the last step direction
 *
 * Author: Benjamin Kneer
 */

public interface StepDirectionModule {

    // return the last detected step direction
    StepDirection getLastStepDirection();

    // return the used sensor
    Sensor getSensor();
}
