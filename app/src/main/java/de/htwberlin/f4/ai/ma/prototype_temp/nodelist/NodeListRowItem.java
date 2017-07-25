package de.htwberlin.f4.ai.ma.prototype_temp.nodelist;

/**
 * Created by Johann Winter
 */

class NodeListRowItem {

    private String nodeID;
    private String picturePath;

    public NodeListRowItem (String nodeID, String picturePath) {
        this.nodeID = nodeID;
        this.picturePath = picturePath;
    }

    public String getNodeID() {
        return this.nodeID;
    }
    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }

    public String getPicturePath() {
        return this.picturePath;
    }
    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

}
