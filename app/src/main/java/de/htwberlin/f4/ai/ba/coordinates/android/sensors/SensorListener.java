package de.htwberlin.f4.ai.ba.coordinates.android.sensors;

/**
 * Created by benni on 19.07.2017.
 */

public interface SensorListener<T> {

    void valueChanged(T newValue);
}
