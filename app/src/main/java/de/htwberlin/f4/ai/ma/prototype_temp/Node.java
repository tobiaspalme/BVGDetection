package de.htwberlin.f4.ai.ma.prototype_temp;

import android.media.Image;

import java.util.List;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.SignalInformation;

/**
 * the node class implements the node from package
 */
public class Node implements de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node {
    String id;
    //float xValue;
    //float yValue;
    float zValue;
    List<SignalInformation> signalInformationList;
    String coordinates;
    Image picture;

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setSignalInformationList(List<SignalInformation> signalInformationList) {
        this.signalInformationList = signalInformationList;
    }

    @Override
    public String getCoordinates() {
        return this.coordinates;
    }

    @Override
    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public Image getPicture() {
        return this.picture;
    }

    @Override
    public void setPicture(Image picture) {
        this.picture = picture;
    }

    @Override
    public List<SignalInformation> getSignalInformation() {
        return this.signalInformationList;
    }



    public Node(String id, float zValue, List<SignalInformation> signalInformationList) {
        this.id = id;
        //this.xValue = xValue;
        //this.yValue = yValue;
        this.zValue = zValue;
        this.signalInformationList = signalInformationList;
    }

    public Node(){}
}


