package de.htwberlin.f4.ai.ma.node;

import de.htwberlin.f4.ai.ma.node.fingerprint.Fingerprint;
import de.htwberlin.f4.ai.ma.node.fingerprint.FingerprintImpl;

/**
 * Created by Johann Winter
 *
 * This Interface is used to manage nodes ("Orte").
 * It contains methods for getting and setting the Node's ID and coordinates.
 * It also contains methods for getting the Node's description
 * and picturePath (path to the Node's picture on the device storage).
 * The methods "getAdditionalInfo" and "setAdditionalInfo" are for
 * the field "additionalInfo", which is a placeholder for future purposes.
 */

public interface Node {

    String getId();
    void setId(String id);

    String getDescription();
    //void setDescription(String description);

    Fingerprint getFingerprintImpl();
    //void setFingerprint(FingerprintImpl fingerprint);

    String getCoordinates();
    void setCoordinates(String coordinates);

    String getPicturePath();
    //void setPicturePath(String picturePath);

    String getAdditionalInfo();
    void setAdditionalInfo(String additionalInfo);

}

