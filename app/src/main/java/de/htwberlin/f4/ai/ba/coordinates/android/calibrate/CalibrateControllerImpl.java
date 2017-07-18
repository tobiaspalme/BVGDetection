package de.htwberlin.f4.ai.ba.coordinates.android.calibrate;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.StepCounter;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.StepCounterImpl;

/**
 * Created by benni on 18.07.2017.
 */

public class CalibrateControllerImpl implements CalibrateController {

    private CalibrateView view;
    private StepCounter stepCounter;


    @Override
    public void onStartStepSetupClick() {
        // get context so we can pass it to stepcounter
        Context context = view.getContext();
        stepCounter = new StepCounterImpl(context);
        stepCounter.start();

    }

    @Override
    public void onStopStepSetupClick() {
        if (stepCounter != null) {
            view.updateStepCount(stepCounter.getStepCount());
            stepCounter.stop();
        }

    }

    @Override
    public void setView(CalibrateView view) {
        this.view = view;
    }


}
