package de.htwberlin.f4.ai.ma.node.fingerprint;

import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.htwberlin.f4.ai.ma.node.fingerprint.signalstrength.SignalStrength;
import de.htwberlin.f4.ai.ma.node.fingerprint.signalstrength.SignalStrengthImpl;

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

            for (SignalStrengthImpl ssi : si.getSignalStrengthList()) {
                counter++;
                value += ssi.signalStrength;
            }
            value = value / counter;

            SignalStrengthImpl signalStrengthInformation = new SignalStrengthImpl(si.getSignalStrengthList().)

            List<SignalStrengthImpl> SsiList = new ArrayList<>();
            SignalStrengthImpl ssi = new SignalStrengthImpl(ssi.macAddress, value);
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

            List<SignalStrength> SsiList = new ArrayList<>();
            SignalStrength ssi = new SignalStrengthImpl(s, value);
            SsiList.add(ssi);
            SignalInformation signalInformation = new SignalInformation("", SsiList);
            signalInformationList.add(signalInformation);
        }
        return signalInformationList;
    }
}



