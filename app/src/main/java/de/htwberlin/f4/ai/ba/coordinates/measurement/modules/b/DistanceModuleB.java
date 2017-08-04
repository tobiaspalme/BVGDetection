package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.b;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a.DistanceModuleA;

/**
 * Created by benni on 04.08.2017.
 */

public class DistanceModuleB extends DistanceModuleA {

    public DistanceModuleB(SensorFactory sensorFactory, float stepLength) {
        super(sensorFactory, stepLength);
    }
}
