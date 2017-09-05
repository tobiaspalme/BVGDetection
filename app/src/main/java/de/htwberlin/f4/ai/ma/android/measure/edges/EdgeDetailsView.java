package de.htwberlin.f4.ai.ma.android.measure.edges;

import android.content.Context;

import de.htwberlin.f4.ai.ma.edge.Edge;
import de.htwberlin.f4.ai.ma.node.Node;

/**
 * EdgeDetailsView Interface
 *
 * Used for managing Edge details
 *
 * Author: Benjamin Kneer
 */

public interface EdgeDetailsView {

    // get the view's context
    Context getContext();

    // update start node
    void updateStartNodeInfo(Node node);

    // update target node
    void updateTargetNodeInfo(Node node);

    // update edge
    void updateEdgeInfo(Edge edge);

    // finish the view
    void finish();
}
