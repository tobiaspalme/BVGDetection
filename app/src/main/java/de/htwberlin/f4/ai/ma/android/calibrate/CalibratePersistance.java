package de.htwberlin.f4.ai.ma.android.calibrate;

import de.htwberlin.f4.ai.ma.android.measure.CalibrationData;

/**
 * Created by benni on 30.07.2017.
 */

public interface CalibratePersistance {

    CalibrationData load();
    void save(CalibrationData calibrationData);
}
