package de.htwberlin.f4.ai.ba.coordinates.android.calibrate;

import android.content.Context;

/**
 * Created by benni on 18.07.2017.
 */

public interface CalibrateView {

    void setController(CalibrateController controller);
    void updateStepCount(int stepCount);
    Context getContext();
}
