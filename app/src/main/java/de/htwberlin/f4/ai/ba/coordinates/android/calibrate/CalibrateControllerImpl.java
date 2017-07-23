package de.htwberlin.f4.ai.ba.coordinates.android.calibrate;

import android.widget.Toast;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactoryImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.Sensor;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurement;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurementFactory;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurementListener;

/**
 * Created by benni on 18.07.2017.
 */

public class CalibrateControllerImpl implements CalibrateController {

    private CalibrateView view;
    private IndoorMeasurement indoorMeasurement;

    @Override
    public void onStartStepSetupClick() {

        SensorFactory sensorFactory = new SensorFactoryImpl(view.getContext());
        indoorMeasurement = IndoorMeasurementFactory.getIndoorMeasurement(sensorFactory);
        indoorMeasurement.setListener(new IndoorMeasurementListener() {
            @Override
            public void valueChanged(float[] values, SensorType sensorType) {
                if (sensorType == SensorType.STEPCOUNTER) {
                    view.updateStepCount((int) values[0]);
                }
            }
        });
        indoorMeasurement.start(SensorType.STEPCOUNTER);
    }

    @Override
    public void onStopStepSetupClick() {
        if (indoorMeasurement != null) {
            indoorMeasurement.stop();
        }
    }

    @Override
    public void onSaveClicked() {
        Toast toast = Toast.makeText(view.getContext(), "save", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onStepIncreaseClicked() {
        Toast toast = Toast.makeText(view.getContext(), "step inc", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onStepDecreaseClicked() {
        Toast toast = Toast.makeText(view.getContext(), "step dec", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onPause() {
        if (indoorMeasurement != null) {
            indoorMeasurement.stop();
        }
    }

    @Override
    public void setView(CalibrateView view) {
        this.view = view;
    }


}
