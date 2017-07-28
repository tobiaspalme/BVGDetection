package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.AltitudeModule;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.DistanceModule;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.OrientationModule;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.PositionModule;

/**
 * PositionModule for IndoorMeasurementType.VARIANT_A
 */

public class PositionModuleImpl implements PositionModule {

    private SensorDataModel dataModel;
    private AltitudeModule altitudeModule;
    private DistanceModule distanceModule;
    private OrientationModule orientationModule;

    public PositionModuleImpl(SensorDataModel dataModel) {
        this.dataModel = dataModel;
        altitudeModule = new AltitudeModuleImpl(dataModel);
        distanceModule = new DistanceModuleImpl(dataModel);
        orientationModule = new OrientationModuleImpl(dataModel);
    }

    @Override
    public void getPosition() {

    }
}
