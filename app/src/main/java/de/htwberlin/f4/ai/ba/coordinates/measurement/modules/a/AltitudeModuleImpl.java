package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.AltitudeModule;

/**
 * Created by benni on 28.07.2017.
 */

public class AltitudeModuleImpl implements AltitudeModule {

    private SensorDataModel dataModel;

    public AltitudeModuleImpl(SensorDataModel dataModel) {
        this.dataModel = dataModel;
    }

    @Override
    public float getAltitude() {
        return 0;
    }
}
