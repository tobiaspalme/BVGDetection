package de.htwberlin.f4.ai.ma.android.measure;

import android.content.Context;

import de.htwberlin.f4.ai.ma.edge.Edge;
import de.htwberlin.f4.ai.ma.node.Node;

/**
 * MeasureView Interface
 *
 * View for showing Measuring details, nodes..
 *
 * Author: Benjamin Kneer
 */

public interface MeasureView {

    // update the azimut angle view
    void updateAzimuth(float azimuth);

    // update the stepcount view
    void updateStepCount(int stepCount);

    // update the distance view
    void updateDistance(float distance);

    // update the current position view
    void updateCoordinates(float x, float y, float z);

    // update the startnode coordinates view
    void updateStartNodeCoordinates(float x, float y, float z);

    // update the targetnode coordinates view
    void updateTargetNodeCoordinates(float x, float y, float z);

    // update edge informations
    void updateEdge(Edge edge);

    // enable start button
    void enableStart();

    // disable start button
    void disableStart();

    // disable stop button
    void disableStop();

    // disable add step button
    void disableAdd();

    // set the start node, used when node is retrieved from wifi fingerprint or qr
    void setStartNode(Node node);

    // get the view's context
    Context getContext();
}
