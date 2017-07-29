package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.DistanceModule;

/**
 * Simply calculate distance by using the previously calibrated
 * step length
 */

public class DistanceModuleImpl implements DistanceModule {

    private SensorDataModel dataModel;
    private float stepLength;

    public DistanceModuleImpl(SensorDataModel dataModel, float stepLength) {
        this.dataModel = dataModel;
        this.stepLength = stepLength;
    }

    @Override
    public float getDistance() {
        return stepLength;
    }
}
