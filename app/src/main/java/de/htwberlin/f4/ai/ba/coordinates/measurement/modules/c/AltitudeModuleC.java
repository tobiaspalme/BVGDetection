package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.c;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a.AltitudeModuleA;

/**
 * Created by benni on 10.08.2017.
 */

public class AltitudeModuleC extends AltitudeModuleA {

    public AltitudeModuleC(SensorFactory sensorFactory, float airPressure) {
        super(sensorFactory, airPressure);
    }
}
