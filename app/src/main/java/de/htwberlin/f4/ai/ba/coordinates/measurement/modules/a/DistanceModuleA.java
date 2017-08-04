package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModelImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.DistanceModule;

/**
 * Simply calculate distance by using the previously calibrated
 * step length
 */

public class DistanceModuleA implements DistanceModule {

    private SensorDataModel dataModel;
    private SensorFactory sensorFactory;
    private float stepLength;

    public DistanceModuleA(SensorFactory sensorFactory, float stepLength) {
        dataModel = new SensorDataModelImpl();
        this.sensorFactory = sensorFactory;
        this.stepLength = stepLength;
    }

    @Override
    public float getDistance() {
        return stepLength;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
