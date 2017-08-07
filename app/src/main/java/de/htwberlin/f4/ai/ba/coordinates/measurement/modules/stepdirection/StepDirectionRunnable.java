package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.stepdirection;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.sql.Timestamp;

import de.htwberlin.f4.ai.ba.coordinates.android.CoordinatesActivity;

/**
 * Created by benni on 07.08.2017.
 */

public class StepDirectionRunnable implements Runnable {

    private StepDirectionDetectListener listener;
    private StepDirectionDetect stepDirectionDetect;


    public StepDirectionRunnable(StepDirectionDetect stepDirectionDetect) {
        this.stepDirectionDetect = stepDirectionDetect;
    }

    @Override
    public void run() {
        StepDirection direction = stepDirectionDetect.getLastStepDirection();

        if (listener != null) {
            listener.onDirectionDetect(direction);
        }
    }

    public void setListener(StepDirectionDetectListener listener) {
        this.listener = listener;
    }

}
