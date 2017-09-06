package de.htwberlin.f4.ai.ma.fingerprint;

import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.htwberlin.f4.ai.ma.fingerprint.accesspointsample.AccessPointSample;
import de.htwberlin.f4.ai.ma.fingerprint.accesspointsample.AccessPointSampleFactory;

/**
 * Created by Johann Winter
 */


class AverageSignalCalculator {

    // TODO doku
    /**
     * Make average values from the capture.
    // * @param signalInformations a list of BSSIDs and signal strengths
     * @return the calculated nodeID
     */
    static List<SignalInformation> calculateAverageSignal(Multimap<String, Integer> multiMap) {

        Set<String> BSSIDs = multiMap.keySet();

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



        for (String s : BSSIDs) {
            int value = 0;
            int counter = 0;

            for (int test : multiMap.get(s)) {
                counter++;
                value += test;
            }
            value = value / counter;

            List<AccessPointSample> accessPointSamples = new ArrayList<>();
            AccessPointSample accessPointSample = AccessPointSampleFactory.createInstance(s, value);
            accessPointSamples.add(accessPointSample);
            SignalInformation signalInformation = new SignalInformation("", accessPointSamples);
            signalInformationList.add(signalInformation);
        }
        return signalInformationList;
    }
}



