package de.htwberlin.f4.ai.ma.location.location_calculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.htwberlin.f4.ai.ma.fingerprint.SignalSample;
import de.htwberlin.f4.ai.ma.fingerprint.accesspoint_information.AccessPointInformation;
import de.htwberlin.f4.ai.ma.fingerprint.accesspoint_information.AccessPointInformationFactory;
import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.fingerprint.Fingerprint;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerFactory;
import de.htwberlin.f4.ai.ma.location.calculations.EuclideanDistance;
import de.htwberlin.f4.ai.ma.location.calculations.FoundNode;
import de.htwberlin.f4.ai.ma.location.calculations.KNearestNeighbor;
import de.htwberlin.f4.ai.ma.location.calculations.KalmanFilter;
import de.htwberlin.f4.ai.ma.location.calculations.MovingAverage;
import de.htwberlin.f4.ai.ma.location.calculations.RestructedNode;

/**
 * Created by Johann Winter
 */

class LocationCalculatorImpl implements LocationCalculator {

    Context context;
    private DatabaseHandler databaseHandler;
    private SharedPreferences sharedPreferences;

    LocationCalculatorImpl(Context context) {
        this.context = context;
        databaseHandler = DatabaseHandlerFactory.getInstance(context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Calculate a Node for a Fingerprint
     * @param fingerprint the input FingerprintImpl to be compared with all existent Nodes to get the position
     * @return the ID (name) of the resulting Node
     */
    public FoundNode calculateNodeId(Fingerprint fingerprint) {

        List<SignalSample> signalSampleList = fingerprint.getSignalSampleList();

        boolean movingAverage = sharedPreferences.getBoolean("pref_movingAverage", true);
        boolean kalmanFilter = sharedPreferences.getBoolean("pref_kalman", true);
        boolean euclideanDistance = sharedPreferences.getBoolean("pref_euclideanDistance", true);
        boolean knnAlgorithm = sharedPreferences.getBoolean("pref_knnAlgorithm", true);

        int movingAverageOrder = Integer.parseInt(sharedPreferences.getString("pref_movivngAverageOrder", "3"));
        int knnValue = Integer.parseInt(sharedPreferences.getString("pref_knnNeighbours", "3"));
        int kalmanValue = Integer.parseInt(sharedPreferences.getString("pref_kalmanValue","2"));

        FoundNode foundNode = null;


        // Load all nodes which have a fingerprint
        List<Node> nodesWithFingerprint = new ArrayList<>();
        for (Node n : databaseHandler.getAllNodes()) {
            if (n.getFingerprint() != null) {
                nodesWithFingerprint.add(n);
            }
        }

        List<RestructedNode> restructedNodeList = calculateNewNodeDateset(nodesWithFingerprint);
        List<RestructedNode> calculatedNodeList = new ArrayList<>();

        if (!restructedNodeList.isEmpty()) {
            if (movingAverage) {
                calculatedNodeList = MovingAverage.calculate(restructedNodeList, movingAverageOrder);

            } else if (kalmanFilter) {
                calculatedNodeList = KalmanFilter.calculateCalman(kalmanValue, restructedNodeList);
            }

            if (euclideanDistance) {
                List<AccessPointInformation> accessPointInformations = getSignalStrengths(signalSampleList);

                if (accessPointInformations.size() == 0) {
                    return null;
                }
                List<String> distanceNames = EuclideanDistance.calculateDistance(calculatedNodeList, accessPointInformations);
                if (knnAlgorithm) {
                    foundNode = KNearestNeighbor.calculateKnn(knnValue, distanceNames);

                } else if (!distanceNames.isEmpty()) {
                    //TODO hier 100%? ...... 100.0/distanceNames.size()
                    foundNode = new FoundNode(distanceNames.get(0), 100.0);
                }
            }
            return foundNode;
        } else {
            return null;
        }
    }


    /**
     * Get a list of AccessPointSamples by passing a list of SignalSample (unwrap).
     * @param signalSampleList a list of SignalInformations
     * @return a list of SignalStrengthInformations
     */
    public List<AccessPointInformation> getSignalStrengths(List<SignalSample> signalSampleList) {
        List<AccessPointInformation> accessPointInformations = new ArrayList<>();

        for (SignalSample sigInfo : signalSampleList) {
            for (AccessPointInformation accessPointInformation : sigInfo.getAccessPointInformationList()) {
                String macAdress = accessPointInformation.getMacAddress();
                int signalStrength = accessPointInformation.getRssi();
                //double signalStrength = accessPointInformation.getMilliwatt();
                AccessPointInformation aps = AccessPointInformationFactory.createInstance(macAdress, signalStrength);
                accessPointInformations.add(aps);
            }
        }
        return accessPointInformations;
    }




    /**
     * Rewrite the nodelist to restrucetd Nodes and delete weak MAC addresses
     * @param allNodes list of all nodes
     * @return restructed Node list
     */
    public List<RestructedNode> calculateNewNodeDateset(List<Node> allNodes) {
        List<String> macAddresses;
        int count = 0;

        List<RestructedNode> restructedNodes = new ArrayList<>();
        Multimap<String, Double> multiMap = null;

        for (Node node : allNodes) {
            count = node.getFingerprint().getSignalSampleList().size();
            double minValue = (((double) 1 / (double) 3) * (double) count);
            macAddresses = getMacAddresses(node);
            multiMap = getMultiMap(node, macAddresses);

            //delete weak addresses
            for (String macAddress : macAddresses) {
                int countValue = 0;

                for (Double signalValue : multiMap.get(macAddress)) {
                    if (signalValue != null) {
                        countValue++;
                    }
                }
                if (countValue <= minValue) {
                    multiMap.removeAll(macAddress);
                    //Log.d("LocationCalculatorImpl", "calculateNewNodeDataset,   remove MAC: " + macAddress);
                }
            }
            //fill restructed Nodes
            RestructedNode restructedNode = new RestructedNode(node.getId(), multiMap);
            restructedNodes.add(restructedNode);
        }
        return restructedNodes;
    }


    /**
     * Create a multimap with MAC address and signal strength values
     * @param node input Node
     * @param macAdresses list of MAC addresses
     * @return multimap with mac address and signal strengths
     */
    public Multimap<String, Double> getMultiMap(Node node, List<String> macAdresses) {
        Multimap<String, Double> multiMap = ArrayListMultimap.create();
        for (SignalSample signalInfo : node.getFingerprint().getSignalSampleList()) {
            HashSet<String> actuallyMacAdresses = new HashSet<>();
            for (AccessPointInformation accessPointInformation : signalInfo.getAccessPointInformationList()) {
                multiMap.put(accessPointInformation.getMacAddress(), (double) accessPointInformation.getRssi());
                //multiMap.put(accessPointInformation.getMacAddress(), (double) accessPointInformation.getMilliwatt());
                actuallyMacAdresses.add(accessPointInformation.getMacAddress());
            }
            for (String checkMacAdress : macAdresses) {
                if (!actuallyMacAdresses.contains(checkMacAdress)) {
                    multiMap.put(checkMacAdress, null);
                }
            }
        }
        return multiMap;
    }


    /**
     * Get all mac addresses of a specific Node
     * @param node the Node
     * @return list of unique MAC addresses
     */
    public List<String> getMacAddresses(Node node) {
        HashSet<String> macAdresses = new HashSet<String>();
        for (SignalSample sigInfo : node.getFingerprint().getSignalSampleList()) {
            for (AccessPointInformation accessPointInformation : sigInfo.getAccessPointInformationList()) {
                macAdresses.add(accessPointInformation.getMacAddress());
            }
        }
        return new ArrayList<>(macAdresses);
    }

}
