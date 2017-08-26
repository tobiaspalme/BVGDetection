package de.htwberlin.f4.ai.ma.android.measure.edges;

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
