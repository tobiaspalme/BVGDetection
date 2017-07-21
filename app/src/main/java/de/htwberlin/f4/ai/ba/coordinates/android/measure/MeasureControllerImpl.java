package de.htwberlin.f4.ai.ba.coordinates.android.measure;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.barometer.Barometer;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.barometer.BarometerImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.barometer.BarometerListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.compass.Compass;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.compass.CompassImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.compass.CompassListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.compass.CompassSimple;

/**
 * Created by benni on 18.07.2017.
 */

public class MeasureControllerImpl implements MeasureController {

    private MeasureView view;
    private Compass compass;
    private Compass compass2;
    private Barometer barometer;

    @Override
    public void setView(MeasureView view) {
        this.view = view;
    }

    @Override
    public void onStartClicked() {
        compass = new CompassImpl(view.getContext());
        compass.setListener(new CompassListener() {
            @Override
            public void valueChanged(Integer newValue) {
                view.updateAzimuth(newValue);
            }
        });
        compass.start();

        compass2 = new CompassSimple(view.getContext());
        compass2.setListener(new CompassListener() {
            @Override
            public void valueChanged(Integer newValue) {
                view.updateAzimuth2(newValue);
            }
        });
        compass2.start();

        barometer = new BarometerImpl(view.getContext());
        barometer.setListener(new BarometerListener() {
            @Override
            public void valueChanged(Float newValue) {
                view.updatePressure(newValue);
            }
        });
        barometer.start();
    }

    @Override
    public void onStopClicked() {
        stopSensors();
    }

    @Override
    public void onPause() {
        stopSensors();
    }

    private void stopSensors() {
        if (compass != null) {
            compass.stop();
        }
        if (compass2 != null) {
            compass2.stop();
        }
        if (barometer != null) {
            barometer.stop();
        }

    }
}
