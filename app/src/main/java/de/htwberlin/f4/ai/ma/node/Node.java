package de.htwberlin.f4.ai.ma.node;


// TODO: Interface beschreiben


import de.htwberlin.f4.ai.ma.node.fingerprint.Fingerprint;

/**
 * Created by Johann Winter
 *
 *
 * This Interface is used to manage nodes ("Orte").
 *
 * The methods "getId" and "setId" are for managing the node's name.
 *
 * The methods "getAdditionalInfo" and "setAdditionalInfo" are for
 * the field "additionalInfo", which is a placeholder for future purposes.
 *
 */

public interface Node {

    String getId();
    void setId(String id);

    String getDescription();
    void setDescription(String description);

    Fingerprint getFingerprint();
    //void setFingerprint(Fingerprint fingerprint);

    String getCoordinates();
    void setCoordinates(String coordinates);

    String getPicturePath();
    void setPicturePath(String picturePath);

    String getAdditionalInfo();
    void setAdditionalInfo(String additionalInfo);

}

