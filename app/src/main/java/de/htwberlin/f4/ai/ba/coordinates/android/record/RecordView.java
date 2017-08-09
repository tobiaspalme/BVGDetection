package de.htwberlin.f4.ai.ba.coordinates.android.record;

import android.content.Context;
import android.support.annotation.IntegerRes;

/**
 * Created by benni on 22.07.2017.
 */

public interface RecordView {

    void updateAcceleration(float[] values);
    void updateAccelerationLinear(float[] values);
    void updateGravity(float[] values);
    void updateGyroscope(float[] values);
    void updateGyroscopeUncalibrated(float[] values);
    void updateMagneticField(float[] values);
    void updateCompassFusion(float value);
    void updateCompassSimple(float value);
    void updatePressure(float value);
    Context getContext();
}
