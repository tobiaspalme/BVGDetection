package de.htwberlin.f4.ai.ma.indoor_graph;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;

/**
 * Created by Johann Winter
 */

class EdgeImplementation {

    Node fromNode;
    Node toNode;
    boolean accessibly;


    public Node getFromNode(){
        return this.fromNode;
    }
    public void setFromNode(Node fromNode){
        this.fromNode = fromNode;
    }

    public Node getToNode(){
        return this.toNode;
    }
    public void setToNode(Node toNode){
        this.toNode = toNode;
    }

    public boolean getAccessibly(){
        return this.accessibly;
    };
    public void setAccessibly(boolean accessibly){
        this.accessibly = accessibly;
    }

}
