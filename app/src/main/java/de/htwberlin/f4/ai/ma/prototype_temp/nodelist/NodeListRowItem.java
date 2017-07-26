package de.htwberlin.f4.ai.ma.prototype_temp.nodelist;

/**
 * Created by Johann Winter
 */

class NodeListRowItem {

    private String nodeID;
    private String nodeDescription;
    private String picturePath;

    public NodeListRowItem (String nodeID, String nodeDescription, String picturePath) {
        this.nodeDescription = nodeDescription;
        this.nodeID = nodeID;
        this.picturePath = picturePath;
    }

    public String getNodeID() {
        return this.nodeID;
    }
    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }

    public String getNodeDescription() {
        return this.nodeDescription;
    }
    public void setNodeDescription(String nodeDescription) {
        this.nodeDescription = nodeDescription;
    }

    public String getPicturePath() {
        return this.picturePath;
    }
    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

}
