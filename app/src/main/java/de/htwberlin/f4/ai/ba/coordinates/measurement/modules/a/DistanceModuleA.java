package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a;

import android.content.Context;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModelImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactoryImpl;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.DistanceModule;

/**
 * Simply calculate distance by using the previously calibrated
 * step length
 */

public class DistanceModuleA implements DistanceModule {

    protected SensorDataModel dataModel;
    protected SensorFactory sensorFactory;
    protected float stepLength;
    protected Context context;

    public DistanceModuleA(Context context, float stepLength) {
        this.context = context;
        dataModel = new SensorDataModelImpl();
        sensorFactory = new SensorFactoryImpl(context);
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
