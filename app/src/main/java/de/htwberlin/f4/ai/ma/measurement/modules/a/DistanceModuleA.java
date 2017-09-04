package de.htwberlin.f4.ai.ma.measurement.modules.a;

import android.content.Context;

import de.htwberlin.f4.ai.ma.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ma.android.sensors.SensorDataModelImpl;
import de.htwberlin.f4.ai.ma.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ma.android.sensors.SensorFactoryImpl;
import de.htwberlin.f4.ai.ma.measurement.modules.DistanceModule;

/**
 * Simply calculate distance by using the previously calibrated
 * step length
 */

public class DistanceModuleA implements DistanceModule {

    protected float stepLength;
    protected Context context;

    private static final float STAIRHIGH = 0.17f;

    public DistanceModuleA(Context context, float stepLength) {
        this.context = context;
        this.stepLength = stepLength;
    }

    @Override
    public float getDistance(boolean stairs) {
        float distance = stepLength;

        if (stairs) {
            distance = distance - (2 * STAIRHIGH);
        }
        
        return distance;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
