package de.htwberlin.f4.ai.ma.fingerprint_generator.fingerprint;

import java.util.List;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;


public interface Fingerprint {

    boolean getMovingAverage();
    void setMovingAverage(boolean average);

    int getAverageOrder();
    void setAverageOrder(int order);

    boolean getKalman();
    void setKalman(boolean average);
    int getKalmanValue();
    void setKalmanValue(int value);

    boolean getEuclideanDistance();
    void setEuclideanDistance(boolean average);

    boolean getKNN();
    void setKNN(boolean average);
    int getKNNValue();
    void setKNNValue(int value);

    void setAllNodes(List<Node> allNodes);
    //List<Node> getAllNodes();

    void setActuallyNode(List<Node> measuredNodes);
    //List<Node> getMeasuredNode();

    String getCalculatedPOI();

    double getPercentage();
    void setPercentage(double percentage);
}
