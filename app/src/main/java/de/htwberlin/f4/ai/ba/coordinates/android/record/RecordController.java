package de.htwberlin.f4.ai.ba.coordinates.android.record;

/**
 * Created by benni on 22.07.2017.
 */

public interface RecordController {

    void setView(RecordView view);
    void onStartClicked();
    void onStopClicked();
    void onPause();
    void onSavePeriodChanged(int value);
}
