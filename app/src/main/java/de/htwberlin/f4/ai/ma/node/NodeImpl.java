package de.htwberlin.f4.ai.ma.node;

import de.htwberlin.f4.ai.ma.node.fingerprint.Fingerprint;
import de.htwberlin.f4.ai.ma.node.fingerprint.FingerprintImpl;

/**
 * Created by Johann Winter
 */

class NodeImpl implements Node {

    private String id;
    private String description;
    private Fingerprint fingerprint;
    private String coordinates;
    private String picturePath;
    private String additionalInfo;


    NodeImpl(String id, String description, Fingerprint fingerprint, String coordinates, String picturePath, String additionalInfo) {
        this.id = id;
        this.description = description;
        this.coordinates = coordinates;
        this.picturePath = picturePath;
        this.fingerprint = fingerprint;
        this.additionalInfo = additionalInfo;
    }

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

    //@Override
    // public void setDescription(String description) {
    //    this.description = description;
    //}

    public Fingerprint getFingerprintImpl() {
        return this.fingerprint;
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

    //@Override
    //public void setPicturePath(String picturePath) {
    //    this.picturePath = picturePath;
    //}

    @Override
    public String getAdditionalInfo() {
        return this.additionalInfo;
    }

    @Override
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }



}