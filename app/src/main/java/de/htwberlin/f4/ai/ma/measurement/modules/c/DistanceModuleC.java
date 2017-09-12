package de.htwberlin.f4.ai.ma.measurement.modules.c;

import android.content.Context;

import de.htwberlin.f4.ai.ma.measurement.modules.a.DistanceModuleA;

/**
 * DistanceModuleC Class which implements the DistanceModule Interface
 *
 * Simply calculate distance by using the previously calibrated
 * step length. Change distance if stair toggle is active
 *
 * Author: Benjamin Kneer
 */

public class DistanceModuleC extends DistanceModuleA {

    public DistanceModuleC(Context context, float stepLength) {
        super(context, stepLength);
    }
}
