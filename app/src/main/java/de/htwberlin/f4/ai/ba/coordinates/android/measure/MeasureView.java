package de.htwberlin.f4.ai.ba.coordinates.android.measure;

import android.content.Context;

/**
 * Created by benni on 18.07.2017.
 */

public interface MeasureView {

    void setController(MeasureController controller);
    void updatePressure(float pressure);
    void updateAzimuth(int azimuth);
    void updateStepCount(int stepCount);
    void updateDistance(float distance);
    void updateCoordinates(float[] coordinates);
    void updateHeight(float height);
    void insertStep();
    Context getContext();

}
