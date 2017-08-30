package de.htwberlin.f4.ai.ma.node.fingerprint;

import android.telephony.SignalStrength;

import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.htwberlin.f4.ai.ma.node.fingerprint.SignalInformation;
import de.htwberlin.f4.ai.ma.node.fingerprint.SignalStrengthInformation;

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

            for (SignalStrengthInformation ssi : si.getSignalStrengthInfoList()) {
                counter++;
                value += ssi.signalStrength;
            }
            value = value / counter;

            SignalStrengthInformation signalStrengthInformation = new SignalStrengthInformation(si.getSignalStrengthInfoList().)

            List<SignalStrengthInformation> SsiList = new ArrayList<>();
            SignalStrengthInformation ssi = new SignalStrengthInformation(ssi.macAddress, value);
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

            List<SignalStrengthInformation> SsiList = new ArrayList<>();
            SignalStrengthInformation ssi = new SignalStrengthInformation(s, value);
            SsiList.add(ssi);
            SignalInformation signalInformation = new SignalInformation("", SsiList);
            signalInformationList.add(signalInformation);
        }
        return signalInformationList;
    }
}



