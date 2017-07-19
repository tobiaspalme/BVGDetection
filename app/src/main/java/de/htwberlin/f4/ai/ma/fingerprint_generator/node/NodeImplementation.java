package de.htwberlin.f4.ai.ma.fingerprint_generator.node;

import android.media.Image;

import java.util.List;

/**
 * Created by Johann Winter
 */

class NodeImplementation implements Node {

    String id;
    //float xValue;
    //float yValue;
    float zValue;
    List<SignalInformation> signalInformationList;
    String coordinates;
    //Image picture;
    String picturePath;

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
    public String getPicturePath() {
        return this.picturePath;
    }

    @Override
    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    /*
    @Override
    public Image getPicture() {
        return this.picture;
    }

    @Override
    public void setPicture(Image picture) {
        this.picture = picture;
    }
    */

    @Override
    public List<SignalInformation> getSignalInformation() {
        return this.signalInformationList;
    }



    public NodeImplementation(String id, float zValue, List<SignalInformation> signalInformationList) {
        this.id = id;
        //this.xValue = xValue;
        //this.yValue = yValue;
        this.zValue = zValue;
        this.signalInformationList = signalInformationList;
    }

    public NodeImplementation(){}
}