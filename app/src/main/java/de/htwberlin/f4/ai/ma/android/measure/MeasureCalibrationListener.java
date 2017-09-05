package de.htwberlin.f4.ai.ma.android.measure;

/**
 * MeasureCalibrationListener Interface
 *
 * Used to inform about calculated average airpressure
 *
 * Author: Benjamin Kneer
 */

public interface MeasureCalibrationListener {

    // calculation finished
    void onFinish(float airPressure);
}
