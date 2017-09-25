package de.htwberlin.f4.ai.ma.indoorroutefinder.android.calibrate;

import android.content.Context;

/**
 * CalibrateView Interface
 *
 * Used for Steplength and Stepperiod calibration
 *
 * Author: Benjamin Kneer
 */

public interface CalibrateView {

    // set the responsible controller
    void setController(CalibrateController controller);

    // update the step count
    void updateStepCount(int stepCount);

    // load a specific calibration step
    void loadCalibrateStep(int setupStep);

    // update the average stepdistance
    void updateAverageStepdistance(float distance);

    // update the average stepperiod
    void updateAverageStepperiod(int period);

    // update the azimut
    void updateAzimuth(int azimuth);

    // get the view's context
    Context getContext();
}
