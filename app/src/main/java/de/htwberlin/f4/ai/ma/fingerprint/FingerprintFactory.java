package de.htwberlin.f4.ai.ma.fingerprint;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FingerprintFactory {

    public static Fingerprint getFingerprint() {
        return new FingerprintImplementation();
    }

    private static class FingerprintImplementation implements Fingerprint {
        private boolean average;
        private boolean kalman;
        private boolean euclideanDistance;
        private boolean knn;

        int averageOrder;
        int knnValue;
        int kalmanValue;
        double percentage;

        List<Node> allExistingNodes;
        List<Node> measuredNode;

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
        public boolean getEuclidienDistance() {
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


        @Override
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
        public String getCalculatedPOI() {
            String poi = null;

            List<RestructedNode> restructedNodeList = calculateNewNodeDateSet(allExistingNodes);
            List<RestructedNode> calculatedNodeList = new ArrayList<>();
            if (allExistingNodes != null) {
                if (average) {
                    MovingAverage movingAverageClass = new MovingAverage();
                    calculatedNodeList = movingAverageClass.calculation(restructedNodeList, averageOrder);
                } else if (kalman) {
                    KalmanFilter kalmanFilterClass = new KalmanFilter();
                    calculatedNodeList = kalmanFilterClass.calculationKalmann(restructedNodeList);
                }

                if (euclideanDistance) {
                    List<MeasuredNode> actuallyNode = getActuallyNode(measuredNode);
                    if (actuallyNode.size() == 0) {
                        return null;
                    }
                    EuclideanDistance euclideanDistanceClass = new EuclideanDistance();
                    List<String> distanceNames = euclideanDistanceClass.calculateDistance(calculatedNodeList, actuallyNode);
                    if (knn) {
                        KNN KnnClass = new KNN();
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
                List<Node.SignalInformation> signalInformation = nodeList.get(i).getSignalInformation();
                for (Node.SignalInformation test : signalInformation)
                    for (Node.SignalStrengthInformation juhu : test.signalStrengthInformationList) {
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
                RestructedNode restructedNode = new RestructedNode(node.getId().toString(), multiMap);
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
            for (Node.SignalInformation signal : node.getSignalInformation()) {
                for (Node.SignalStrengthInformation signalStrength : signal.signalStrengthInformationList) {
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
            for (Node.SignalInformation signal : node.getSignalInformation()) {
                HashSet<String> actuallyMacAdresses = new HashSet<String>();
                for (Node.SignalStrengthInformation signalStrength : signal.signalStrengthInformationList) {
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


        private class RestructedNode {
            String id;
            Multimap<String, Double> restructedSignals;

            private RestructedNode(String id, Multimap<String, Double> restructedSignals) {
                this.id = id;
                this.restructedSignals = restructedSignals;
            }
        }

        private class MeasuredNode {
            String macAdress;
            int signalStrenght;

            private MeasuredNode(String macAdress, int signalStrenght) {
                this.macAdress = macAdress;
                this.signalStrenght = signalStrenght;
            }
        }

        /**
         * start moving average depending on the order three ot five
         */
        private class MovingAverage {
            private List<RestructedNode> calculation(List<RestructedNode> restructedNodeList, int order) {
                List<RestructedNode> calculatedNodes = new ArrayList<>();
                Multimap<String, Double> calculadetMultiMap = null;

                for (int i = 0; i < restructedNodeList.size(); i++) {
                    RestructedNode Node = restructedNodeList.get(i);
                    calculadetMultiMap = ArrayListMultimap.create();

                    double average;

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

                        for (int j = 0; j < Values.length; j++) {

                            if (order == 3) {
                                if (j >= 2) {
                                    double t1, t2, t3;
                                    if (Values[j - 2] != null) {
                                        t1 = Values[j - 2];
                                    } else {
                                        t1 = average;
                                    }
                                    if (Values[j - 1] != null) {
                                        t2 = Values[j - 1];
                                    } else {
                                        t2 = average;
                                    }
                                    if (Values[j] != null) {
                                        t3 = Values[j];
                                    } else {
                                        t3 = average;
                                    }

                                    double movingAverageValue = ((double) 1 / (double) 3) * (t1 + t2 + t3);
                                    calculadetMultiMap.put(Key, movingAverageValue);
                                }

                            } else if (order == 5) {
                                if (j >= 4) {
                                    double t1, t2, t3, t4, t5;
                                    if (Values[j - 4] != null) {
                                        t1 = Values[j - 4];
                                    } else {
                                        t1 = average;
                                    }
                                    if (Values[j - 3] != null) {
                                        t2 = Values[j - 3];
                                    } else {
                                        t2 = average;
                                    }
                                    if (Values[j - 2] != null) {
                                        t3 = Values[j - 2];
                                    } else {
                                        t3 = average;
                                    }
                                    if (Values[j - 1] != null) {
                                        t4 = Values[j - 1];
                                    } else {
                                        t4 = average;
                                    }
                                    if (Values[j] != null) {
                                        t5 = Values[j];
                                    } else {
                                        t5 = average;
                                    }

                                    double movingAverageValue = ((double) 1 / (double) 5) * (t1 + t2 + t3 + t4 + t5);
                                    calculadetMultiMap.put(Key, movingAverageValue);
                                }

                            }

                        }

                    }
                    calculatedNodes.add(new RestructedNode(Node.id, calculadetMultiMap));
                }

                return calculatedNodes;
            }
        }

        /**
         * start kalman filter
         */
        private class KalmanFilter {
            private List<RestructedNode> calculationKalmann(List<RestructedNode> restructedNodeList) {

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

        /**
         * calculate all euclidean distances
         */
        private class EuclideanDistance {
            private List<String> calculateDistance(List<RestructedNode> restructedNodes, List<MeasuredNode> measuredNodeList) {
                List<String> distanceName = new ArrayList<>();
                List<DistanceClass> distanceClassList = new ArrayList<>();

                for (int i = 0; i < restructedNodes.size(); i++) {

                    List<Collection<Double>> matchingSignalStrengths = new ArrayList<>();
                    List<Integer> measuredSignalStrength = new ArrayList<>();
                    for (int j = 0; j < measuredNodeList.size(); j++) {
                        Boolean contains = restructedNodes.get(i).restructedSignals.containsKey(measuredNodeList.get(j).macAdress);
                        if (contains) {
                            matchingSignalStrengths.add(restructedNodes.get(i).restructedSignals.get(measuredNodeList.get(j).macAdress));
                            measuredSignalStrength.add(measuredNodeList.get(j).signalStrenght);
                        }
                    }

                    if (matchingSignalStrengths.size() >= 2) {
                        List<Iterator<Double>> signalIterators = new ArrayList<>();

                        for (int j = 0; j < matchingSignalStrengths.size(); j++) {
                            signalIterators.add(matchingSignalStrengths.get(j).iterator());
                        }

                        while (signalIterators.get(0).hasNext()) {
                            double distance = 0d;

                            for (int j = 0; j < matchingSignalStrengths.size(); j++) {
                                Double restructedSignalStregnth = signalIterators.get(j).next();
                                distance += Math.pow(measuredSignalStrength.get(j) - restructedSignalStregnth, 2);
                            }

                            distance /= (double) matchingSignalStrengths.size();
                            distance = Math.sqrt(distance);

                            DistanceClass distanceClass = new DistanceClass();
                            distanceClass.name = restructedNodes.get(i).id;
                            distanceClass.distance = distance;
                            distanceClassList.add(distanceClass);
                        }
                    }
                }

                //List<DistanceClass> distanceList2 = doSelectionSort(distanceClassList);
                //List<String> distanceList2 = doSelectionSort(distanceClassList);

                return selectionsort(distanceClassList);
            }

            /**
             * sort euclidean distances
             * @param distanceList
             * @return sorted list
             */
            private List<String> selectionsort(List<DistanceClass> distanceList) {
                for (int i = 0; i < distanceList.size() - 1; i++) {
                    for (int j = i + 1; j < distanceList.size(); j++) {
                        if (distanceList.get(i).distance > distanceList.get(j).distance) {
                            DistanceClass temp = distanceList.get(i);
                            distanceList.set(i, distanceList.get(j));
                            distanceList.set(j, temp);
                        }
                    }
                }

                List<String> distanceNameList = new ArrayList<>();
                for (DistanceClass poi : distanceList) {
                    distanceNameList.add(poi.name);
                }

                return distanceNameList;
            }


            private class DistanceClass {
                String name;
                double distance;
            }
        }

        private class KNN {
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

    }
}

