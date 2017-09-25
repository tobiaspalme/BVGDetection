package de.htwberlin.f4.ai.ma.indoorroutefinder.node;

import de.htwberlin.f4.ai.ma.indoorroutefinder.fingerprint.Fingerprint;

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

    Fingerprint getFingerprint();

    String getCoordinates();
    void setCoordinates(String coordinates);

    String getPicturePath();

    String getAdditionalInfo();
    void setAdditionalInfo(String additionalInfo);

}

