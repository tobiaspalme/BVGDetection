package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModelImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.OrientationModule;

/**
 * Created by benni on 28.07.2017.
 */

public class OrientationModuleImpl implements OrientationModule {

    private SensorDataModel dataModel;
    private SensorFactory sensorFactory;
    private float orientation;

    public OrientationModuleImpl(SensorFactory sensorFactory) {
        dataModel = new SensorDataModelImpl();
        this.sensorFactory = sensorFactory;
    }

    @Override
    public float getOrientation() {
        return orientation;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
