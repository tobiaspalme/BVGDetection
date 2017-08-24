package de.htwberlin.f4.ai.ma.android.calibrate;

/**
 * Created by benni on 18.07.2017.
 */

public interface CalibrateController {

    void setView(CalibrateView view);
    void onStartStepSetupClick();
    void onStopStepSetupClick();
    void onNextClicked(int currentStep);
    void onBackClicked(int currentStep);
    void onStepIncreaseClicked();
    void onStepDecreaseClicked();
    void onDistanceChange(float distance);
    void onPause();
}
