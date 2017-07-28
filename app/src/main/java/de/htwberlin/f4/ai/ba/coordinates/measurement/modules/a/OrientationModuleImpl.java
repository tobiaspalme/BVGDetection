package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.OrientationModule;

/**
 * Created by benni on 28.07.2017.
 */

public class OrientationModuleImpl implements OrientationModule {

    private SensorDataModel dataModel;

    public OrientationModuleImpl(SensorDataModel dataModel) {
        this.dataModel = dataModel;
    }

    @Override
    public float getOrientation() {
        return 0;
    }
}
