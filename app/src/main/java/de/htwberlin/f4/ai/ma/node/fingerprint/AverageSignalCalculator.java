package de.htwberlin.f4.ai.ma.node.fingerprint;

import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.htwberlin.f4.ai.ma.node.fingerprint.accesspointsample.AccessPointSample;
import de.htwberlin.f4.ai.ma.node.fingerprint.accesspointsample.AccessPointSampleImpl;

/**
 * Created by Johann Winter
 */


public class AverageSignalCalculator {

    // TODO doku
    /**
     * Make average values from the capture.
    // * @param signalInformations a list of BSSIDs and signal strengths
     * @return the calculated nodeID
     */
    public static List<SignalInformation> calculateAverageSignal(Multimap<String, Integer> multiMap) {

        Set<String> bssid = multiMap.keySet();




        final List<SignalInformation> signalInformationList = new ArrayList<>();

        /*

        for (SignalInformation si : signalInformations) {
            int value = 0;
            int counter = 0;

            for (AccessPointSampleImpl ssi : si.getAccessPointSampleList()) {
                counter++;
                value += ssi.signalStrength;
            }
            value = value / counter;

            AccessPointSampleImpl signalStrengthInformation = new AccessPointSampleImpl(si.getAccessPointSampleList().)

            List<AccessPointSampleImpl> SsiList = new ArrayList<>();
            AccessPointSampleImpl ssi = new AccessPointSampleImpl(ssi.macAddress, value);
            SsiList.add(ssi);
            SignalInformation signalInformation = new SignalInformation("", SsiList);
            signalInformationList.add(signalInformation);
        }*/



        for (String s : bssid) {
            int value = 0;
            int counter = 0;

            for (int test : multiMap.get(s)) {
                counter++;
                value += test;
            }
            value = value / counter;

            List<AccessPointSample> SsiList = new ArrayList<>();
            AccessPointSample ssi = new AccessPointSampleImpl(s, value);
            SsiList.add(ssi);
            SignalInformation signalInformation = new SignalInformation("", SsiList);
            signalInformationList.add(signalInformation);
        }
        return signalInformationList;
    }
}



