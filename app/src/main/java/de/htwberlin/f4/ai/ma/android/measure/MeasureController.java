package de.htwberlin.f4.ai.ma.android.measure;


import de.htwberlin.f4.ai.ma.node.Node;

/**
 * Created by benni on 18.07.2017.
 */

public interface MeasureController {

    void setView(MeasureView view);
    void onStartClicked();
    void onStopClicked();
    void onAddClicked();
    void onPause();
    void onResume();
    void onStartNodeSelected(Node node);
    void onTargetNodeSelected(Node node);
    void onEdgeDetailsClicked();
    void onStartNodeImageClicked();
    void onTargetNodeImageClicked();
    void onLocateWifiClicked();
    void onLocateQrClicked();
    void onQrResult(String qr);
    void onNullpointCheckedStartNode(boolean checked);
}
