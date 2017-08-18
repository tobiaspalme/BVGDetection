package de.htwberlin.f4.ai.ba.coordinates.android.measure;


import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurementType;
import de.htwberlin.f4.ai.ma.node.Node;

/**
 * Created by benni on 18.07.2017.
 */

public interface MeasureController {

    void setView(MeasureView view);
    void onStartClicked();
    void onStopClicked();
    void onAddClicked();
    //void onMeasurementTypeSelected(IndoorMeasurementType type);
    void onPause();
    void onResume();
    void onStartNodeSelected(Node node);
    void onTargetNodeSelected(Node node);
    void onEdgeDetailsClicked();

    void onTestClicked();
}
