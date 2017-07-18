package de.htwberlin.f4.ai.ba.coordinates.android.measure;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.Compass;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.CompassImpl;

/**
 * Created by benni on 18.07.2017.
 */

public class MeasureControllerImpl implements MeasureController {

    MeasureView view;

    @Override
    public void setView(MeasureView view) {
        this.view = view;
    }

    @Override
    public void onStartClicked() {
        Compass compass = new CompassImpl(view.getContext());
        compass.start();
    }

    @Override
    public void onStopClicked() {

    }
}
