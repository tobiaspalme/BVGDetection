package de.htwberlin.f4.ai.ma.edge;

import java.util.List;

import de.htwberlin.f4.ai.ma.node.Node;

/**
 * Created by Johann Winter
 */

public interface Edge {

    Node getNodeA();
    void setNodeA(Node nodeA);

    Node getNodeB();
    void setNodeB(Node nodeB);

    boolean getAccessibility();
    void setAccessibility(boolean accessable);

    float getWeight();
    void setWeight(float weight);

    void insertStepCoords(String coords);
    List<String> getStepCoordsList();

    String getAdditionalInfo();
    void setAdditionalInfo(String additionalInfo);
}
