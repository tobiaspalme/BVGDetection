package de.htwberlin.f4.ai.ma.persistence.calculations;

/**
 * Created by Johann Winter
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.htwberlin.f4.ai.ma.node.fingerprint.SignalStrengthInformation;


/**
 * calculate all euclidean distances
 */
public class EuclideanDistance {

    //public static List<String> calculateDistance(List<RestructedNode> restructedNodes, List<MeasuredNode> measuredNodeList) {
    public static List<String> calculateDistance(List<RestructedNode> restructedNodes, List<SignalStrengthInformation> measuredSSIs) {
        List<String> distanceName = new ArrayList<>();
        List<DistanceClass> distanceClassList = new ArrayList<>();

        for (int i = 0; i < restructedNodes.size(); i++) {

            List<Collection<Double>> matchingSignalStrengths = new ArrayList<>();
            List<Integer> measuredSignalStrength = new ArrayList<>();
            for (int j = 0; j < measuredSSIs.size(); j++) {
                Boolean contains = restructedNodes.get(i).restructedSignals.containsKey(measuredSSIs.get(j).macAddress);
                if (contains) {
                    matchingSignalStrengths.add(restructedNodes.get(i).restructedSignals.get(measuredSSIs.get(j).macAddress));
                    measuredSignalStrength.add(measuredSSIs.get(j).signalStrength);
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

        return sortSelection(distanceClassList);
    }

    /**
     * sort euclidean distances
     * @param distanceList
     * @return sorted list
     */
    private static List<String> sortSelection(List<DistanceClass> distanceList) {
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

}