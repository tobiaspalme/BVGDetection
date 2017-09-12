package de.htwberlin.f4.ai.ma.android.record;

/**
 * RecordController Interface
 *
 * used for recording sensor values
 *
 * Author: Benjamin Kneer
 */

public interface RecordController {

    // set the responsible RecordView
    void setView(RecordView view);

    // triggered by clicked on start button
    void onStartClicked();

    // triggered by clicking on stop button
    void onStopClicked();

    // triggered by activity onPause()
    void onPause();

    // triggered by changing the save period
    void onSavePeriodChanged(int value);
}
