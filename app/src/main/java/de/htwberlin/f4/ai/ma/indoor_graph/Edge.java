package de.htwberlin.f4.ai.ma.indoor_graph;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;

/**
 * Created by Johann Winter
 */

public interface Edge {

    //int getId();
    //void setID(int id);

    String getNodeA();
    void setNodeA(String nodeA);

    String getNodeB();
    void setNodeB(String nodeB);

    boolean getAccessibly();
    void setAccessibly(boolean accessibly);

    int getExpenditure();
    void setExpenditure(int expenditure);
}
