package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.stepdirection;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.Sensor;

/**
 * Created by benni on 05.08.2017.
 */

public interface StepDirectionDetect {

    StepDirection getLastStepDirection();
    Sensor getSensor();
}
