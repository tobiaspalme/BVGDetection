package de.htwberlin.f4.ai.ba.coordinates.android.measure;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.barometer.Barometer;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.barometer.BarometerImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.barometer.BarometerListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.compass.Compass;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.compass.CompassImpl;

/**
 * Created by benni on 18.07.2017.
 */

public class MeasureControllerImpl implements MeasureController {

    private MeasureView view;
    private Compass compass;
    private Barometer barometer;

    @Override
    public void setView(MeasureView view) {
        this.view = view;
    }

    @Override
    public void onStartClicked() {
        compass = new CompassImpl(view.getContext());
        compass.start();

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
        if (barometer != null) {
            barometer.stop();
        }
    }
}
