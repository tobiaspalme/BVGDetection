package de.htwberlin.f4.ai.ba.coordinates.android.measure;

import android.content.Context;

import java.util.List;

import de.htwberlin.f4.ai.ma.edge.Edge;
import de.htwberlin.f4.ai.ma.node.Node;

/**
 * Created by benni on 18.07.2017.
 */

public interface MeasureView {


    void updateAzimuth(float azimuth);
    void updateStepCount(int stepCount);
    void updateDistance(float distance);
    void updateCoordinates(float x, float y, float z);
    void updateStartNodeCoordinates(float x, float y, float z);
    void updateTargetNodeCoordinates(float x, float y, float z);
    void updateEdge(Edge edge);
    void enableStart();
    void disableStart();
    void enableStop();
    void disableStop();
    void enableAdd();
    void disableAdd();

    Context getContext();

}
