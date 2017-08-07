package de.htwberlin.f4.ai.ma.edge;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;

/**
 * Created by Johann Winter
 */

public class EdgeImplementation implements Edge{

    //private int id;
    private Node nodeA;
    private Node nodeB;
    private boolean accessibly;
    private int expenditure;

    //public EdgeImplementation(int id, String nodeA, String nodeB, boolean accessibly, int expenditure) {
    public EdgeImplementation(Node nodeA, Node nodeB, boolean accessibly, int expenditure) {
        //this.id = id;
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        this.accessibly = accessibly;
        this.expenditure = expenditure;
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


    //public EdgeImplementation() {}


   /* @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }
*/

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
        return this.expenditure;
    }

    @Override
    public void setWeight(int expenditure) {
        this.expenditure = expenditure;
    }


}
