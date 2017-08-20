package de.htwberlin.f4.ai.ba.coordinates.android.measure.edges;

import de.htwberlin.f4.ai.ma.node.Node;

/**
 * Created by benni on 13.08.2017.
 */

public interface EdgeDetailsController {

    void setView(EdgeDetailsView view);
    void setNodes(String startNodeId, String targetNodeId);

    void onDeleteClicked();
    void onSaveClicked();

    void onHandycapChanged(boolean handycapFriendly);
    void onEdgeInfoChanged(String info);

    void onResume();
}
