package de.htwberlin.f4.ai.ma.indoor_graph;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;

/**
 * Created by Johann Winter
 */

public interface Edge {

    int getId();
    void setID(int id);

    Node getNodeA();
    void setNodeA(Node nodeA);

    Node getNodeB();
    void setNodeB(Node nodeB);

    boolean getAccessibly();
    void setAccessibly(boolean accessibly);
}
