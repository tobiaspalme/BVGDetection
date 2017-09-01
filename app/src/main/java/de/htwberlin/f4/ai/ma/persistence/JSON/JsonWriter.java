package de.htwberlin.f4.ai.ma.persistence.JSON;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class JsonWriter {
    private Context context;

    /*
    public JsonWriter(Context context) {
        this.context = context;
    }*/


    /**
     * Write a JSON object to the JSON file on the device storage
     * @param node the node to save
     */
    public void writeJSON(de.htwberlin.f4.ai.ma.node.Node node) {
//        String jsonString = loadJSONFromAsset(context);
        String jsonString = loadJSONFromAsset();
        String nodeId = node.getId();

        boolean idIsContained = false;

        if (jsonString != null) {
            try {
                int index = 0;
                JSONObject jsonObj = new JSONObject(jsonString);
                JSONArray jsonNode = jsonObj.getJSONArray("Node");
                for (int i = 0; i < jsonNode.length(); i++) {
                    JSONObject jsonObjectNode = jsonNode.getJSONObject(i);
                    if(jsonObjectNode.length()>0){
                        String id = jsonObjectNode.getString("id");
                        if (id.equals(nodeId)) {
                            index = i;
                            idIsContained = true;
                        }
                    }
                }
                if (idIsContained) {
                    JSONObject newJsonObject = jsonNode.getJSONObject(index);

                    if (newJsonObject.has("signalInformation")) {
                        JSONArray jsonArray = newJsonObject.getJSONArray("signalInformation");
                        JSONArray jsonArrayAdd = makeJsonNode(newJsonObject, node).getJSONArray("signalInformation");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonArrayAdd.put(jsonArray.getJSONObject(i));
                        }
                        newJsonObject.put("signalInformation", jsonArrayAdd);
                    }
                    save(jsonObj);
                } else {
                    JSONObject jsonObjectNode = new JSONObject();
                    jsonNode.put(makeJsonNode(jsonObjectNode, node));

                    save(jsonObj);
                }
            } catch (final JSONException e) {
                Log.e("JSON", "Json parsing error: " + e.getMessage());
            }
        }
    }

    /**
     * Create a new JSON object containing all information from node to save
     * @param jsonObjectNode the old JSON object
     * @param node the node to save
     * @return the new JSON object
     */
    private JSONObject makeJsonNode(JSONObject jsonObjectNode, de.htwberlin.f4.ai.ma.node.Node node) {
        try {
            jsonObjectNode.put("id", node.getId());
            jsonObjectNode.put("description", node.getDescription());
            jsonObjectNode.put("coordinates", node.getCoordinates());
            jsonObjectNode.put("picturePath", node.getPicturePath());
            jsonObjectNode.put("additionalInfo", node.getAdditionalInfo());

            //if (node.getFingerprint().getSignalInformationList() != null) {
            if (node.getFingerprint() != null) {
                JSONArray signalJsonArray = new JSONArray();
                for (int i = 0; i < node.getFingerprint().getSignalInformationList().size(); i++) {

                    JSONObject signalJsonObject = new JSONObject();
                    JSONArray signalStrengthJsonArray = new JSONArray();

                    for (int j = 0; j < node.getFingerprint().getSignalInformationList().get(i).getAccessPointSampleList().size(); j++) {
                        JSONObject signalStrenghtObject = new JSONObject();
                        signalStrenghtObject.put("macAddress", node.getFingerprint().getSignalInformationList().get(i).getAccessPointSampleList().get(j).getMacAddress());
                        signalStrenghtObject.put("strength", node.getFingerprint().getSignalInformationList().get(i).getAccessPointSampleList().get(j).getRSSI());
                        signalStrengthJsonArray.put(signalStrenghtObject);
                    }
                    signalJsonObject.put("timestamp", node.getFingerprint().getSignalInformationList().get(i).getTimestamp());
                    signalJsonObject.put("signalStrength", signalStrengthJsonArray);
                    signalJsonArray.put(signalJsonObject);
                }
                jsonObjectNode.put("signalInformation", signalJsonArray);
            }
        } catch (final JSONException e) {
            Log.e("JSON", "parsing Error");
        }
        return jsonObjectNode;

    }

    /**
     * Save the new jsonSting to file
     * @param jsonObject the new json String
     */
    public void save(JSONObject jsonObject){
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/IndoorPositioning/JSON");
        dir.mkdirs();
        File file = new File(dir, "jsonFile.txt");
        FileOutputStream outputStream;

        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(jsonObject.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * If file exists, load .txt file from Files folder and return its content as a JSON String.
     * If no file exist create empty JSON String
     //* @param context
     * @return json String
     */
    //private String loadJSONFromAsset(Context context) {
    private String loadJSONFromAsset() {
        String json = null;
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File (sdCard.getAbsolutePath() + "/IndoorPositioning/JSON");
            dir.mkdirs();
            File file = new File(dir, "jsonFile.txt");
            if (file.exists()) {
                FileInputStream is = new FileInputStream(file);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
            } else {
                json = "{Node: []}";
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
