package de.htwberlin.f4.ai.ma.fingerprint_generator.node;

import android.media.Image;

import java.util.List;

public interface Node {
    String getId();
    void setId(String id);

    List<SignalInformation> getSignalInformation();
    void setSignalInformationList(List<SignalInformation> signalInformationList);

    String getCoordinates();
    void setCoordinates(String coordinates);

    Image getPicture();
    void setPicture(Image picture);

}

// Kommentare