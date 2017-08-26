package de.htwberlin.f4.ai.ma.measurement.modules.stepdirection;

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
