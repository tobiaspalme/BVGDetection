package de.htwberlin.f4.ai.ma.indoor_graph;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;

/**
 * Created by Johann Winter
 */

class EdgeImplementation implements Edge{

    int id;
    Node nodeA;
    Node nodeB;
    boolean accessibly;


    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

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


}
