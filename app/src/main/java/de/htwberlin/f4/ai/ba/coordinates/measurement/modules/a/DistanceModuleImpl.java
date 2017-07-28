package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.DistanceModule;

/**
 * Created by benni on 28.07.2017.
 */

public class DistanceModuleImpl implements DistanceModule {

    private SensorDataModel dataModel;

    public DistanceModuleImpl(SensorDataModel dataModel) {
        this.dataModel = dataModel;
    }

    @Override
    public float getDistance() {
        return 0;
    }
}
