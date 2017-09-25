package de.htwberlin.f4.ai.ma.indoorroutefinder.persistence.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.htwberlin.f4.ai.ma.indoorroutefinder.fingerprint.SignalSample;
import de.htwberlin.f4.ai.ma.indoorroutefinder.fingerprint.accesspoint_information.AccessPointInformation;
import de.htwberlin.f4.ai.ma.indoorroutefinder.fingerprint.accesspoint_information.AccessPointInformationFactory;

/**
 * Created by Johann Winter
 */

public class JSONConverter {

    /**
     * Convert a List<SignalSample> to JSON-String (for database storing).
     * @param signalSampleList a list of SignalInformations
     * @return JSON-String containing the SignalInformations
     */
    public String convertSignalInfoListToJSON(List<SignalSample> signalSampleList) {

        JSONObject jsonObject = new JSONObject();
        JSONArray signalJsonArray = new JSONArray();

        if (signalSampleList != null) {
            try {
                for (int i = 0; i < signalSampleList.size(); i++) {

                    JSONObject signalJsonObject = new JSONObject();
                    JSONArray signalStrengthJsonArray = new JSONArray();

                    for (int j = 0; j < signalSampleList.get(i).getAccessPointInformationList().size(); j++) {

                        JSONObject signalStrenghtObject = new JSONObject();
                        signalStrenghtObject.put("macAddress", signalSampleList.get(i).getAccessPointInformationList().get(j).getMacAddress());
                        signalStrenghtObject.put("strength", signalSampleList.get(i).getAccessPointInformationList().get(j).getRssi());
                        //signalStrenghtObject.put("strength", signalSampleList.get(i).getAccessPointInformationList().get(j).getMilliwatt());
                        signalStrengthJsonArray.put(signalStrenghtObject);
                    }
                    signalJsonObject.put("timestamp", signalSampleList.get(i).getTimestamp());
                    signalJsonObject.put("signalStrength", signalStrengthJsonArray);
                    signalJsonArray.put(signalJsonObject);
                }
                jsonObject.put("signalInformation", signalJsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject.toString();
    }


    /**
     * Convert JSON-String to a List<SignalSample>
     * @param jsonString the JSON-String from the database
     * @return the list of SignalInformations
     */
    public List<SignalSample> convertJsonToSignalInfoList(String jsonString) {

        List<SignalSample> signalSampleList = new ArrayList<>();

        try {
            JSONObject jsonObj = new JSONObject(jsonString);

            if (jsonObj.has("signalInformation")) {
                JSONArray signalJsonArray = jsonObj.getJSONArray("signalInformation");

                for (int j = 0; j < signalJsonArray.length(); j++) {

                    JSONObject signalJsonObject = signalJsonArray.getJSONObject(j);
                    String timestamp = signalJsonObject.getString("timestamp");

                    JSONArray signalStrengthJsonArray = signalJsonObject.getJSONArray("signalStrength");
                    List<AccessPointInformation> accessPointInformations = new ArrayList<>();

                    for (int k = 0; k < signalStrengthJsonArray.length(); k++) {
                        JSONObject signalStrenghtObject = signalStrengthJsonArray.getJSONObject(k);
                        String macAdress = signalStrenghtObject.getString("macAddress");
                        int signalStrength = signalStrenghtObject.getInt("strength");
                        AccessPointInformation accessPointInformation = AccessPointInformationFactory.createInstance(macAdress, signalStrength);
                        accessPointInformations.add(accessPointInformation);
                    }
                    SignalSample signalSample = new SignalSample(timestamp, accessPointInformations);
                    signalSampleList.add(signalSample);
                }
            }
        } catch (JSONException e) { e.printStackTrace(); }
        return signalSampleList;
    }
}
