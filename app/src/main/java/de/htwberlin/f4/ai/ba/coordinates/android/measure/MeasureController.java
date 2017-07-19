package de.htwberlin.f4.ai.ba.coordinates.android.measure;



/**
 * Created by benni on 18.07.2017.
 */

public interface MeasureController {

    void setView(MeasureView view);
    void onStartClicked();
    void onStopClicked();
    void onPause();

}
