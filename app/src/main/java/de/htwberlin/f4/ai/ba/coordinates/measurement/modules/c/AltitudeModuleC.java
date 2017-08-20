package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.c;

import android.content.Context;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a.AltitudeModuleA;

/**
 * Created by benni on 10.08.2017.
 */

public class AltitudeModuleC extends AltitudeModuleA {

    public AltitudeModuleC(Context context, float airPressure) {
        super(context, airPressure);
    }
}
