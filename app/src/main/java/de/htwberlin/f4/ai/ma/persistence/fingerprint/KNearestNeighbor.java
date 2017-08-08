package de.htwberlin.f4.ai.ma.persistence.fingerprint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Johann Winter
 */


public class KNearestNeighbor {

    private int knnValue;

    public KNearestNeighbor(int knnValue){
        this.knnValue = knnValue;
    }


    /**
     * start knn
     * @param distanceNames names of sorted list
     * @return actually poi
     */
    public String calculateKnn(List<String> distanceNames) {
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

            // TODO!! rausreichen der percent
            //setPercentage(percent);
            return mostRepeated.getKey();
        } else {
            return null;
        }
    }
}