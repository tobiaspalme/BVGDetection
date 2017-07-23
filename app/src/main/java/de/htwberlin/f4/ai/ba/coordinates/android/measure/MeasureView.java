package de.htwberlin.f4.ai.ba.coordinates.android.measure;

import android.content.Context;

/**
 * Created by benni on 18.07.2017.
 */

public interface MeasureView {

    void setController(MeasureController controller);
    void updatePressure(float pressure);
    void updateAzimuth(Integer azimuth);
    void updateAzimuth2(Integer azimuth);
    Context getContext();

}
