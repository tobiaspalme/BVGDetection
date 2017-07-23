package de.htwberlin.f4.ai.ba.coordinates.android.measure;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactoryImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.barometer.Barometer;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurement;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurementFactory;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurementListener;

/**
 * Created by benni on 18.07.2017.
 */

public class MeasureControllerImpl implements MeasureController {

    private MeasureView view;
    private IndoorMeasurement indoorMeasurement;

    @Override
    public void setView(MeasureView view) {
        this.view = view;
    }

    @Override
    public void onStartClicked() {

        SensorFactory sensorFactory = new SensorFactoryImpl(view.getContext());
        indoorMeasurement = IndoorMeasurementFactory.getIndoorMeasurement(sensorFactory);
        indoorMeasurement.setListener(new IndoorMeasurementListener() {
            @Override
            public void valueChanged(float[] values, SensorType sensorType) {
                if (sensorType == SensorType.COMPASS_FUSION) {
                    view.updateAzimuth((int) values[0]);
                } else if (sensorType == SensorType.COMPASS_SIMPLE) {
                    view.updateAzimuth2((int) values[0]);
                } else if (sensorType == SensorType.BAROMETER) {
                    view.updatePressure(values[0]);
                }
            }
        });
        indoorMeasurement.start(SensorType.COMPASS_FUSION,
                                SensorType.COMPASS_SIMPLE,
                                SensorType.BAROMETER);
    }

    @Override
    public void onStopClicked() {
        if (indoorMeasurement != null) {
            indoorMeasurement.stop();
        }
    }

    @Override
    public void onPause() {
        if (indoorMeasurement != null) {
            indoorMeasurement.stop();
        }
    }

}
