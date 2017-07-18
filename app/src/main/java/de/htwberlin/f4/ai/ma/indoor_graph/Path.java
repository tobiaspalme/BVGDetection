package de.htwberlin.f4.ai.ma.indoor_graph;

import java.util.ArrayList;
import java.util.List;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;

/**
 * Created by Johann Winter
 */

class Path {

    private List<Node> nodeList;

    // Never ever change that object -> comment
    // List zurückgeben
    List<Node> getNodeList(){
        return this.nodeList;
    }
    //void setNodeList(ArrayList<Node> nodes){
     //   this.nodeList = nodes;
    //};
}
