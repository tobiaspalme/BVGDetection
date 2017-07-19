package de.htwberlin.f4.ai.ma.indoor_graph;

import java.util.List;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;

/**
 * Created by Johann Winter
 */

class PathImplementation implements Path {

    List<Node> nodeList;

    @Override
    public List<Node> getNodeList() {
        return this.nodeList;
    }
}
