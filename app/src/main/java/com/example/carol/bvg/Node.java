package com.example.carol.bvg;

import java.util.ArrayList;
import java.util.List;

/**
 * the node class implements the node from package
 */
public class Node implements de.htwberlin.f4.ai.ma.fingerprint.Node {
    String id;
    //float xValue;
    //float yValue;
    float zValue;
    List<de.htwberlin.f4.ai.ma.fingerprint.Node.SignalInformation> signalInformationList;

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setSignalInformationList(List<de.htwberlin.f4.ai.ma.fingerprint.Node.SignalInformation> signalInformationList) {
        this.signalInformationList = signalInformationList;
    }

    @Override
    public List<de.htwberlin.f4.ai.ma.fingerprint.Node.SignalInformation> getSignalInformation() {
        return this.signalInformationList;
    }



    public Node(String id, float zValue, List<de.htwberlin.f4.ai.ma.fingerprint.Node.SignalInformation> signalInformationList) {
        this.id = id;
        //this.xValue = xValue;
        //this.yValue = yValue;
        this.zValue = zValue;
        this.signalInformationList = signalInformationList;
    }

    public Node(){

    }
}


