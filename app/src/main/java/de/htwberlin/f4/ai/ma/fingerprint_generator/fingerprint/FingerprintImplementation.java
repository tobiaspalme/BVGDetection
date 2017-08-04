package de.htwberlin.f4.ai.ma.fingerprint_generator.fingerprint;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;
import de.htwberlin.f4.ai.ma.fingerprint_generator.node.SignalInformation;
import de.htwberlin.f4.ai.ma.fingerprint_generator.node.SignalStrengthInformation;

/**
 * Created by Johann Winter
 */


// TODO: vorher: private static
class FingerprintImplementation implements Fingerprint {
    private boolean average;
    private boolean kalman;
    private boolean euclideanDistance;
    private boolean knn;

    private int averageOrder;
    private int knnValue;
    private int kalmanValue;
    private double percentage;

    private List<Node> allExistingNodes;
    private List<Node> measuredNode;

    @Override
    public void setMovingAverage(boolean average) {
        this.average = average;
    }

    @Override
    public boolean getMovingAverage() {
        return this.average;
    }

    @Override
    public void setAverageOrder(int order) {
        this.averageOrder = order;
    }

    @Override
    public int getAverageOrder() {
        return this.averageOrder;
    }

    @Override
    public void setKalman(boolean kalman) {
        this.kalman = kalman;
    }

    @Override
    public boolean getKalman() {
        return this.kalman;
    }

    @Override
    public void setKalmanValue(int value) {
        this.kalmanValue = value;
    }

    @Override
    public int getKalmanValue() {
        return this.kalmanValue;
    }

    @Override
    public void setEuclideanDistance(boolean euclideanDistance) {
        this.euclideanDistance = euclideanDistance;
    }

    @Override
    public boolean getEuclideanDistance() {
        return this.euclideanDistance;
    }

    @Override
    public void setKNN(boolean knn) {
        this.knn = knn;
    }

    @Override
    public boolean getKNN() {
        return this.knn;
    }

    @Override
    public void setKNNValue(int value) {
        this.knnValue = value;
    }

    @Override
    public int getKNNValue() {
        return this.knnValue;
    }

    @Override
    public void setAllNodes(List<Node> allNodes) {
        this.allExistingNodes = allNodes;
    }


    // @Override
    public void setActuallyNode(List<Node> measuredNode) {
        this.measuredNode = measuredNode;
    }

    @Override
    public void setPercentage(double percentage){
        this.percentage = percentage;
    }

    @Override
    public double getPercentage(){
        return this.percentage;
    }

    /**
     * check settings and start calculation
     * @return calculated Poi
     */
    @Override
    public String getCalculatedNode() {
        String poi = null;

        List<RestructedNode> restructedNodeList = calculateNewNodeDateSet(allExistingNodes);
        List<RestructedNode> calculatedNodeList = new ArrayList<>();
        if (allExistingNodes != null) {
            if (average) {
                MovingAverage movingAverageClass = new MovingAverage();
                calculatedNodeList = movingAverageClass.calculation(restructedNodeList, averageOrder);
            } else if (kalman) {
                KalmanFilter kalmanFilterClass = new KalmanFilter();
                calculatedNodeList = kalmanFilterClass.calculationKalman(restructedNodeList);
            }

            if (euclideanDistance) {
                List<MeasuredNode> actuallyNode = getActuallyNode(measuredNode);
                if (actuallyNode.size() == 0) {
                    return null;
                }
                EuclideanDistance euclideanDistanceClass = new EuclideanDistance();
                List<String> distanceNames = euclideanDistanceClass.calculateDistance(calculatedNodeList, actuallyNode);
                if (knn) {
                    KNearestNeighbor KnnClass = new KNearestNeighbor();
                    poi = KnnClass.calculateKnn(distanceNames);
                } else {
                    poi = distanceNames.get(0);
                }
            }

            return poi;
        } else {
            return null;
        }
    }


    /**
     * rewrite actually node to type measured node
     * @param nodeList list of nodes
     * @return list of measured node
     */
    private List<MeasuredNode> getActuallyNode(List<Node> nodeList) {
        List<MeasuredNode> measuredNodeList = new ArrayList<>();

        for (int i = 0; i < nodeList.size(); i++) {
            List<SignalInformation> signalInformation = nodeList.get(i).getSignalInformation();
            for (SignalInformation test : signalInformation)
                for (SignalStrengthInformation juhu : test.signalStrengthInformationList) {
                    String macAdress = juhu.macAdress;
                    int signalStrenght = juhu.signalStrength;
                    MeasuredNode measuredNode = new MeasuredNode(macAdress, signalStrenght);
                    measuredNodeList.add(measuredNode);
                }
        }

        return measuredNodeList;
    }

    /**
     * rewrite nodelist to restrucetd nodes and delete weak mac addresses
     * @param allExistingNodes list of all nodes
     * @return restructed node list
     */
    private List<RestructedNode> calculateNewNodeDateSet(List<Node> allExistingNodes) {
        List<String> macAddresses = new ArrayList<>();
        int count = 0;

        List<RestructedNode> restructedNodes = new ArrayList<>();
        Multimap<String, Double> multiMap = null;

        for (Node node : allExistingNodes) {
            count = node.getSignalInformation().size();
            double minValue = (((double) 1 / (double) 3) * (double) count);
            macAddresses = getMacAddresses(node);
            multiMap = getMultiMap(node, macAddresses);

            //delete weak addresses
            for (String checkMacAdress : macAddresses) {
                int countValue = 0;

                for (Double signalValue : multiMap.get(checkMacAdress)) {
                    if (signalValue != null) {
                        countValue++;
                    }
                }
                if (countValue <= minValue) {
                    multiMap.removeAll(checkMacAdress);
                }

            }

            //fill restructed Nodes
            RestructedNode restructedNode = new RestructedNode(node.getId(), multiMap);
            restructedNodes.add(restructedNode);

        }

        return restructedNodes;
    }

    /**
     * get all mac addresses
     * @param node
     * @return list of unique mac addresses
     */
    private List<String> getMacAddresses(Node node) {
        HashSet<String> macAdresses = new HashSet<String>();
        for (SignalInformation signal : node.getSignalInformation()) {
            for (SignalStrengthInformation signalStrength : signal.signalStrengthInformationList) {
                macAdresses.add(signalStrength.macAdress);
            }
        }
        List<String> uniqueList = new ArrayList<String>(macAdresses);
        return uniqueList;
    }

    /**
     * create a multimap with mac address and values
     * @param node
     * @param macAdresses
     * @return multimap with mac address and vales
     */
    private Multimap<String, Double> getMultiMap(Node node, List<String> macAdresses) {
        Multimap<String, Double> multiMap = ArrayListMultimap.create();
        for (SignalInformation signal : node.getSignalInformation()) {
            HashSet<String> actuallyMacAdresses = new HashSet<String>();
            for (SignalStrengthInformation signalStrength : signal.signalStrengthInformationList) {
                multiMap.put(signalStrength.macAdress, (double) signalStrength.signalStrength);
                actuallyMacAdresses.add(signalStrength.macAdress);
            }
            for (String checkMacAdress : macAdresses) {
                if (!actuallyMacAdresses.contains(checkMacAdress)) {
                    multiMap.put(checkMacAdress, null);
                }
            }
        }
        return multiMap;
    }


    private class KNearestNeighbor {
        /**
         * start knn
         * @param distanceNames names of sorted list
         * @return actually poi
         */
        private String calculateKnn(List<String> distanceNames) {
            int knnValue = getKNNValue();
            Map<String, Integer> stringsCount = new HashMap<String, Integer>();

            if (distanceNames.size() >= knnValue && knnValue != 0) {
                for (int i = 0; i < 4*knnValue; i++) {
                    if (distanceNames.get(i).length() > 0) {
                        //String distanceNameString = distanceNames.get(i).toLowerCase();
                        Integer count = stringsCount.get(distanceNames.get(i));
                        if (count == null) count = new Integer(0);
                        count++;
                        stringsCount.put(distanceNames.get(i), count);
                    }
                }
            }
            Map.Entry<String, Integer> mostRepeated = null;
            for (Map.Entry<String, Integer> e : stringsCount.entrySet()) {
                if (mostRepeated == null || mostRepeated.getValue() < e.getValue())
                    mostRepeated = e;
            }
            if (mostRepeated != null) {
                double percent = ((double)mostRepeated.getValue() / ((double)4 * (double)knnValue))*(double)100;
                setPercentage(percent);
                return mostRepeated.getKey();
            } else {
                return null;
            }
        }
    }


    /**
     * start kalman filter
     */
    private class KalmanFilter {

        private List<RestructedNode> calculationKalman(List<RestructedNode> restructedNodeList) {

            List<RestructedNode> calculatedNodes = new ArrayList<>();
            Multimap<String, Double> calculadetMultiMap = null;

            for (int i = 0; i < restructedNodeList.size(); i++) {
                RestructedNode Node = restructedNodeList.get(i);
                calculadetMultiMap = ArrayListMultimap.create();

                double average;
                double Xk, Pk, Kk, Pkt, Xkt, value, deviation;

//                    int counterTest = 0;
//                    int tempAverageTest = 0;
//                    double averageTest;
//                    double XkTest, PkTest, KkTest, PktTest, XktTest, valueTest, deviationTest;
//                    Integer [] ValuesTest = {-52,-53,-51,-50, -51, -50,  -54, -52, -51, -49, -50, -49,  -47, -53, -52, -53, -49,  -54, -51, -53, -53, -52, -53, -50, -53, -50, -48, -50, -51,  -49, -51,-52,  -53};
//                    ArrayList<Double> calculatedValuesTest = new ArrayList<>();
//                    for (Integer Signal : ValuesTest) {
//                        if (Signal != null) {
//                            counterTest++;
//                            tempAverageTest += Signal;
//                        }
//                    }
//                    averageTest = (double) tempAverageTest / (double) counterTest;
//
//                    XkTest = averageTest;
//                    PkTest = 2;
//
//                    deviationTest = calculateDeviation(ValuesTest, averageTest, counterTest);
//                    //deviationTest = 0.1;
//
//                    for (int j = 0; j < ValuesTest.length; j++) {
//
//                        if (ValuesTest[j] != null) {
//                            valueTest = ValuesTest[j];
//                        } else {
//                            valueTest = averageTest;
//                        }
//
//                        KkTest = (PkTest) / (PkTest + deviationTest);
//                        PktTest = ((double) 1 - KkTest) * PkTest;
//                        PkTest = PktTest;
//                        XktTest = XkTest + KkTest * (valueTest - XkTest);
//                        XkTest = XktTest;
//
//                        calculatedValuesTest.add(j, XktTest);
//                    }

                for (String Key : Node.restructedSignals.keySet()) {
                    int counter = 0;
                    int tempAverage = 0;

                    Double[] Values = Node.restructedSignals.get(Key).toArray(new Double[0]);

                    for (Double Signal : Values) {
                        if (Signal != null) {
                            counter++;
                            tempAverage += Signal;
                        }
                    }
                    average = (double) tempAverage / (double) counter;

                    Xk = average;
                    Pk = getKalmanValue();

                    deviation = calculateDeviation(Values,average,counter);

                    for (int j = 0; j < Values.length; j++) {

                        if (Values[j] != null) {
                            value = Values[j];
                        } else {
                            value = average;
                        }

                        Kk = (Pk) / (Pk + deviation);
                        Pkt = ((double) 1 - Kk) * Pk;
                        Pk = Pkt;
                        Xkt = Xk + Kk * (value - Xk);
                        Xk = Xkt;

                        calculadetMultiMap.put(Key, Xkt);
                    }
                }
                calculatedNodes.add(new RestructedNode(Node.id, calculadetMultiMap));
            }

            return calculatedNodes;
        }

        private double calculateDeviation(Double[] values, double average, double count) {
            int x = 0;
            for (int i = 0; i < values.length; i++) {
                if (values[i] != null) {
                    x += Math.pow((values[i] - average), 2);
                } else {
                    x += Math.pow((average - average), 2);
                }
            }

            double temp = ((double) 1 / ((double) count - 1)) * x;
            return Math.sqrt(temp);
        }
    }
}

