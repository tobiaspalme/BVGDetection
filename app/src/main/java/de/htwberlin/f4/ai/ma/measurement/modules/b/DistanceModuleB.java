package de.htwberlin.f4.ai.ma.measurement.modules.b;

import android.content.Context;

import de.htwberlin.f4.ai.ma.measurement.modules.a.DistanceModuleA;

/**
 * DistanceModuleB Class which implements the DistanceModule Interface
 *
 * Simply calculate distance by using the previously calibrated
 * step length. Change distance if stair toggle is active
 *
 * Author: Benjamin Kneer
 */

public class DistanceModuleB extends DistanceModuleA {

    public DistanceModuleB(Context context, float stepLength) {
        super(context, stepLength);
    }
}
