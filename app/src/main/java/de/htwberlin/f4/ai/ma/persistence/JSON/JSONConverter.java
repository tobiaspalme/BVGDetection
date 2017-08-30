package de.htwberlin.f4.ai.ma.persistence.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.htwberlin.f4.ai.ma.node.fingerprint.SignalInformation;
import de.htwberlin.f4.ai.ma.node.fingerprint.SignalStrengthInformation;

/**
 * Created by Johann Winter
 */

public class JSONConverter {

    /**
     * Convert a List<SignalInformation> to JSON-String (for database storing).
     * @param signalInformationList a list of SignalInformations
     * @return JSON-String containing the SignalInformations
     */
    public String convertSignalInfoListToJSON(List<SignalInformation> signalInformationList) {

        JSONObject jsonObject = new JSONObject();
        JSONArray signalJsonArray = new JSONArray();

        if (signalInformationList != null) {
            try {
                for (int i = 0; i < signalInformationList.size(); i++) {

                    JSONObject signalJsonObject = new JSONObject();
                    JSONArray signalStrengthJsonArray = new JSONArray();

                    for (int j = 0; j < signalInformationList.get(i).getSignalStrengthInfoList().size(); j++) {

                        JSONObject signalStrenghtObject = new JSONObject();
                        signalStrenghtObject.put("macAddress", signalInformationList.get(i).getSignalStrengthInfoList().get(j).macAddress);
                        signalStrenghtObject.put("strength", signalInformationList.get(i).getSignalStrengthInfoList().get(j).signalStrength);
                        signalStrengthJsonArray.put(signalStrenghtObject);
                    }
                    signalJsonObject.put("timestamp", signalInformationList.get(i).getTimestamp());
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
     * Convert JSON-String to a List<SignalInformation>
     * @param jsonString the JSON-String from the database
     * @return the list of SignalInformations
     */
    public List<SignalInformation> convertJsonToSignalInfoList(String jsonString) {

        List<SignalInformation> signalInformationList = new ArrayList<>();

        try {
            JSONObject jsonObj = new JSONObject(jsonString);

            if (jsonObj.has("signalInformation")) {
                JSONArray signalJsonArray = jsonObj.getJSONArray("signalInformation");

                for (int j = 0; j < signalJsonArray.length(); j++) {

                    JSONObject signalJsonObject = signalJsonArray.getJSONObject(j);
                    String timestamp = signalJsonObject.getString("timestamp");

                    JSONArray signalStrengthJsonArray = signalJsonObject.getJSONArray("signalStrength");
                    List<SignalStrengthInformation> signalStrenghtList = new ArrayList<>();

                    for (int k = 0; k < signalStrengthJsonArray.length(); k++) {
                        JSONObject signalStrenghtObject = signalStrengthJsonArray.getJSONObject(k);
                        String macAdress = signalStrenghtObject.getString("macAddress");
                        int signalStrenght = signalStrenghtObject.getInt("strength");
                        SignalStrengthInformation signal = new SignalStrengthInformation(macAdress, signalStrenght);
                        signalStrenghtList.add(signal);
                    }
                    SignalInformation signalInformation = new SignalInformation(timestamp, signalStrenghtList);
                    signalInformationList.add(signalInformation);
                }
            }
        } catch (JSONException e) { e.printStackTrace(); }
        return signalInformationList;
    }
}
