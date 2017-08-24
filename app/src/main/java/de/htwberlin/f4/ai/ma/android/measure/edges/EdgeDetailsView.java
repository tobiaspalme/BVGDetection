package de.htwberlin.f4.ai.ma.android.measure.edges;

import android.content.Context;

import de.htwberlin.f4.ai.ma.edge.Edge;
import de.htwberlin.f4.ai.ma.node.Node;

/**
 * Created by benni on 13.08.2017.
 */

public interface EdgeDetailsView {

    Context getContext();
    void updateStartNodeInfo(Node node);
    void updateTargetNodeInfo(Node node);
    void updateEdgeInfo(Edge edge);
    void finish();
}
