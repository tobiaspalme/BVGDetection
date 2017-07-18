package de.htwberlin.f4.ai.ma.indoor_graph;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;

/**
 * Created by Johann Winter
 */

public interface Edge {

    Node getFromNode();
    void setFromNode(Node fromNode);

    Node getToNode();
    void setToNode(Node toNode);

    boolean getAccessibly();
    void setAccessibly(boolean accessibly);
}
