package de.htwberlin.f4.ai.ma.fingerprint_generator.node;

import android.media.Image;

import java.util.List;

/**
 * Created by Johann Winter
 */

class NodeImplementation implements Node {

    private String id;
    //float xValue;
    //float yValue;
    private String description;
    private float zValue;
    private List<SignalInformation> signalInformationList;
    private String coordinates;
    private String picturePath;
    private String additionalInfo;


    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public String getAdditionalInfo() {
        return this.additionalInfo;
    }

    @Override
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }


    @Override
    public List<SignalInformation> getSignalInformation() {
        return this.signalInformationList;
    }



    NodeImplementation(String id, float zValue, String description, List<SignalInformation> signalInformationList, String coordinates, String picturePath, String additionalInfo) {
        this.id = id;
        //this.xValue = xValue;
        //this.yValue = yValue;
        this.zValue = zValue;
        this.description = description;
        this.coordinates = coordinates;
        this.picturePath = picturePath;
        this.signalInformationList = signalInformationList;
        this.additionalInfo = additionalInfo;
    }

}