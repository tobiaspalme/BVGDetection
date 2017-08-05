package de.htwberlin.f4.ai.ba.coordinates.measurement;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModel;

/**
 * Created by benni on 05.08.2017.
 */

public interface StepDirectionDetect {

    StepDirection getLastStepDirection(SensorDataModel dataModel);
}
