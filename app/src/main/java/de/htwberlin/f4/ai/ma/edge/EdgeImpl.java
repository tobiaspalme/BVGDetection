package de.htwberlin.f4.ai.ma.edge;

import java.util.ArrayList;
import java.util.List;

import de.htwberlin.f4.ai.ma.node.Node;

/**
 * Created by Johann Winter
 */

public class EdgeImpl implements Edge{

    private Node nodeA;
    private Node nodeB;
    private boolean accessible;
    private float weight;
    private List<String> stepCoordList;
    private String additionalInfo;


    // Constructor for an Edge without given stepCoordList
    public EdgeImpl(Node nodeA, Node nodeB, boolean accessible, float weight) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        this.accessible = accessible;
        this.weight = weight;
        this.stepCoordList = new ArrayList<>();
    }

    // Constructor for an Edge with given stepCoordList
    public EdgeImpl(Node nodeA, Node nodeB, boolean accessible, List<String> stepCoordList, float weight, String additionalInfo) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        this.accessible = accessible;
        this.weight = weight;
        this.stepCoordList = stepCoordList;
        this.additionalInfo = additionalInfo;
    }


    @Override
    public Node getNodeA() {
        return this.nodeA;
    }

    //@Override
    //public void setNodeA(Node nodeA) {
    //    this.nodeA = nodeA;
    //}

    @Override
    public Node getNodeB() {
        return this.nodeB;
    }

    //@Override
    //public void setNodeB(Node nodeB) {
    //    this.nodeB = nodeB;
    //}

    @Override
    public boolean getAccessibility() {
        return this.accessible;
    }

    @Override
    public void setAccessibility(boolean accessibly) {
        this.accessible = accessibly;
    }

    @Override
    public float getWeight() {
        return this.weight;
    }

    @Override
    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public void insertStepCoords(String coords) {
        this.stepCoordList.add(coords);
    }

    @Override
    public List<String> getStepCoordsList() {
        return this.stepCoordList;
    }


    @Override
    public String getAdditionalInfo() {
        return this.additionalInfo;
    }

    @Override
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }


}
