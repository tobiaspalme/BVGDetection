package de.htwberlin.f4.ai.ma.prototype_temp;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.SignalInformation;
import de.htwberlin.f4.ai.ma.fingerprint_generator.node.SignalStrengthInformation;

public class JsonReader {

    /**
     * load and read a .txt file from Files Folder
     * @param context
     * @return return the content as json string
     */
    public String loadJSONFromAsset(Context context) {
        String json = null;
        //TODO Exception abfangen
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/Files");
        dir.mkdirs();
        File file = new File(dir, "jsonFile.txt");
        //File file = new File(Environment.getExternalStorageDirectory(), "/Files/jsonFile.txt");
        try {
            FileInputStream is = new FileInputStream(file);
            //InputStream is = context.getAssets().open("ergebnisse.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    /**
     * deserialize jsonString to Node Object and Save all Objects in a list
     * @param context
     * @return list of Nodes
     */
    public List<de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node> initializeNodeFromJson(Context context) {
        String jsonString = loadJSONFromAsset(context);
        List<de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node> allNodes = new ArrayList<>();

        if (jsonString != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonString);

                // Getting JSON Array node
                JSONArray jsonNode = jsonObj.getJSONArray("Node");

                for (int i = 0; i < jsonNode.length(); i++) {

                    List<SignalInformation> signalInformationList = new ArrayList<>();

                    JSONObject jsonObjectNode = jsonNode.getJSONObject(i);
                    String id = jsonObjectNode.getString("id");
                    float z = jsonObjectNode.getLong("zValue");

                    JSONArray signalJsonArray = jsonObjectNode.getJSONArray("signalInformation");
                    for (int j = 0; j < signalJsonArray.length(); j++) {
                        JSONObject signalJsonObject = signalJsonArray.getJSONObject(j);
                        String timestamp = signalJsonObject.getString("timestamp");

                        JSONArray signalStrengthJsonArray = signalJsonObject.getJSONArray("signalStrength");
                        List<SignalStrengthInformation> signalStrenghtList = new ArrayList<>();

                        for (int k = 0; k < signalStrengthJsonArray.length(); k++) {
                            JSONObject signalStrenghtObject = signalStrengthJsonArray.getJSONObject(k);
                            String macAdress = signalStrenghtObject.getString("macAdress");
                            int signalStrenght = signalStrenghtObject.getInt("strength");
                            SignalStrengthInformation signal = new SignalStrengthInformation(macAdress, signalStrenght);
                            signalStrenghtList.add(signal);
                        }

                        SignalInformation signalInformation = new SignalInformation(timestamp, signalStrenghtList);
                        signalInformationList.add(signalInformation);
                    }

                    de.htwberlin.f4.ai.ma.prototype_temp.Node node = new Node(id, z, signalInformationList);
                    allNodes.add(node);
                }


            } catch (final JSONException e) {
                Log.e("JSON", "Json parsing error: " + e.getMessage());
            }

        }
        return allNodes;
    }

}
