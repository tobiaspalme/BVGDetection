package de.htwberlin.f4.ai.ma.indoorroutefinder.fingerprint;

import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.htwberlin.f4.ai.ma.indoorroutefinder.fingerprint.accesspoint_information.AccessPointInformation;
import de.htwberlin.f4.ai.ma.indoorroutefinder.fingerprint.accesspoint_information.AccessPointInformationFactory;

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
    static List<SignalSample> calculateAverageSignal(Multimap<String, Integer> multiMap) {

        Set<String> BSSIDs = multiMap.keySet();

        final List<SignalSample> signalSampleList = new ArrayList<>();

        /*
        for (SignalSample si : signalInformations) {
            int value = 0;
            int counter = 0;

            for (AccessPointSampleImpl ssi : si.getAccessPointInformationList()) {
                counter++;
                value += ssi.signalStrength;
            }
            value = value / counter;

            AccessPointSampleImpl signalStrengthInformation = new AccessPointSampleImpl(si.getAccessPointInformationList().)

            List<AccessPointSampleImpl> SsiList = new ArrayList<>();
            AccessPointSampleImpl ssi = new AccessPointSampleImpl(ssi.macAddress, value);
            SsiList.add(ssi);
            SignalSample signalInformation = new SignalSample("", SsiList);
            signalSampleList.add(signalInformation);
        }*/



        for (String s : BSSIDs) {
            int value = 0;
            int counter = 0;

            for (int test : multiMap.get(s)) {
                counter++;
                value += test;
            }
            value = value / counter;

            List<AccessPointInformation> accessPointInformations = new ArrayList<>();
            AccessPointInformation accessPointInformation = AccessPointInformationFactory.createInstance(s, value);
            accessPointInformations.add(accessPointInformation);
            SignalSample signalSample = new SignalSample("", accessPointInformations);
            signalSampleList.add(signalSample);
        }
        return signalSampleList;
    }
}



