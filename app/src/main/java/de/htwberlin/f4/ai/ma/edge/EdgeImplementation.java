package de.htwberlin.f4.ai.ma.edge;

import java.util.ArrayList;
import java.util.List;

import de.htwberlin.f4.ai.ma.node.Node;

/**
 * Created by Johann Winter
 */

public class EdgeImplementation implements Edge{

    //private int id;
    private Node nodeA;
    private Node nodeB;
    private boolean accessibly;
    private int weight;
    private List<String> stepCoordList;


    public EdgeImplementation(Node nodeA, Node nodeB, boolean accessibly, int weight) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        this.accessibly = accessibly;
        this.weight = weight;
        this.stepCoordList = new ArrayList<>();
    }


    public EdgeImplementation(Node nodeA, Node nodeB, boolean accessibly, List<String> stepCoordList, int weight) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        this.accessibly = accessibly;
        this.weight = weight;
        this.stepCoordList = stepCoordList;
    }

    /*
    // TODO temporary, until expenditure is implemented
    public EdgeImplementation(int id, String nodeA, String nodeB, boolean accessibly) {
        this.id = id;
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        this.accessibly = accessibly;
        this.expenditure = expenditure;
    }*/


    @Override
    public Node getNodeA() {
        return this.nodeA;
    }

    @Override
    public void setNodeA(Node nodeA) {
        this.nodeA = nodeA;
    }

    @Override
    public Node getNodeB() {
        return this.nodeB;
    }

    @Override
    public void setNodeB(Node nodeB) {
        this.nodeB = nodeB;
    }

    @Override
    public boolean getAccessibly() {
        return this.accessibly;
    }

    @Override
    public void setAccessibly(boolean accessibly) {
        this.accessibly = accessibly;
    }

    @Override
    public int getWeight() {
        return this.weight;
    }

    @Override
    public void setWeight(int weight) {
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


}
