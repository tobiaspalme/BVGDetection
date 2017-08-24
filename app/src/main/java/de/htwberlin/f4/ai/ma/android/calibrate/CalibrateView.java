package de.htwberlin.f4.ai.ma.android.calibrate;

import android.content.Context;

/**
 * Created by benni on 18.07.2017.
 */

public interface CalibrateView {

    void setController(CalibrateController controller);
    void updateStepCount(int stepCount);
    void loadCalibrateStep(int setupStep);
    void updateAverageStepdistance(float distance);
    void updateAverageStepperiod(int period);
    void updateAzimuth(int azimuth);
    Context getContext();
}
