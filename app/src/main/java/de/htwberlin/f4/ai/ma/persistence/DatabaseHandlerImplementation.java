package de.htwberlin.f4.ai.ma.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import de.htwberlin.f4.ai.ma.edge.EdgeImplementation;
import de.htwberlin.f4.ai.ma.node.Fingerprint;
import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.node.NodeFactory;
import de.htwberlin.f4.ai.ma.edge.Edge;
import de.htwberlin.f4.ai.ma.node.SignalInformation;
import de.htwberlin.f4.ai.ma.node.SignalStrengthInformation;
import de.htwberlin.f4.ai.ma.location.LocationResult;
import de.htwberlin.f4.ai.ma.location.LocationResultImplementation;
import de.htwberlin.f4.ai.ma.persistence.JSON.JSONConverter;
import de.htwberlin.f4.ai.ma.persistence.calculations.EuclideanDistance;
import de.htwberlin.f4.ai.ma.persistence.calculations.KNearestNeighbor;
import de.htwberlin.f4.ai.ma.persistence.calculations.KalmanFilter;
import de.htwberlin.f4.ai.ma.persistence.calculations.MeasuredNode;
import de.htwberlin.f4.ai.ma.persistence.calculations.MovingAverage;
import de.htwberlin.f4.ai.ma.persistence.calculations.RestructedNode;


/**
 * Created by Johann Winter
 */

public class DatabaseHandlerImplementation extends SQLiteOpenHelper implements DatabaseHandler {

    private static final String DATABASE_NAME = "indoor_data.db";
    private static final int DATABASE_VERSION = 1;

    private static final String NODES_TABLE = "nodes";
    private static final String RESULTS_TABLE = "results";
    private static final String EDGES_TABLE = "edges";


    private static final String NODE_ID = "id";
    private static final String NODE_DESCRIPTION = "description";
    private static final String NODE_SIGNALINFORMATIONLIST = "signalinformationlist";
    private static final String NODE_COORDINATES = "coordinates";
    private static final String NODE_PICTURE_PATH = "picture_path";
    private static final String NODE_ADDITIONAL_INFO = "additional_info";

    private static final String RESULT_ID = "id";
    private static final String RESULT_SETTINGS = "settings";
    private static final String RESULT_MEASURED_TIME = "measuredtime";
    private static final String RESULT_SELECTED_NODE = "selectednode";
    private static final String RESULT_MEASURED_NODE = "measurednode";

    private static final String EDGE_ID = "id";
    private static final String EDGE_NODE_A = "nodeA";
    private static final String EDGE_NODE_B = "nodeB";
    private static final String EDGE_ACCESSIBLY = "accessibly";
    private static final String EDGE_STEPLIST = "steplist";
    private static final String EDGE_WEIGHT = "weight";
    private static final String EDGE_ADDITIONAL_INFO = "additional_info";


    private NodeFactory nodeFactory;
    private JSONConverter jsonConverter = new JSONConverter();
    private Context context;



    private int averageOrder;
    private int knnValue;
    private int kalmanValue;
    private double percentage;

    private SharedPreferences sharedPreferences;

    //TODO temp:
    private List<Node> measuredNode;


    public DatabaseHandlerImplementation(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createNodeTableQuery = "CREATE TABLE " + NODES_TABLE + " (" +
                NODE_ID + " TEXT PRIMARY KEY," +
                NODE_DESCRIPTION + " TEXT," +
                NODE_SIGNALINFORMATIONLIST + " TEXT," +
                NODE_COORDINATES + " TEXT," +
                NODE_PICTURE_PATH + " TEXT," +
                NODE_ADDITIONAL_INFO + " TEXT)";

        String createResultTableQuery = "CREATE TABLE " + RESULTS_TABLE + " (" +
                RESULT_ID + " INTEGER PRIMARY KEY," +
                RESULT_SETTINGS + " TEXT," +
                RESULT_MEASURED_TIME + " TEXT," +
                RESULT_SELECTED_NODE + " TEXT," +
                RESULT_MEASURED_NODE + " TEXT)";

        String createEdgeTableQuery = "CREATE TABLE " + EDGES_TABLE + " (" +
                EDGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                EDGE_NODE_A + " TEXT," +
                EDGE_NODE_B + " TEXT," +
                EDGE_ACCESSIBLY + " TEXT," +
                EDGE_STEPLIST + " TEXT," +
                EDGE_WEIGHT + " INTEGER," +
                EDGE_ADDITIONAL_INFO + " TEXT);";


        db.execSQL(createNodeTableQuery);
        db.execSQL(createResultTableQuery);
        db.execSQL(createEdgeTableQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }


    //----------------- N O D E S ------------------------------------------------------------------------------------------

    // Insert
    public void insertNode(Node node) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(NODE_ID, node.getId());
        values.put(NODE_DESCRIPTION, node.getDescription());
        values.put(NODE_SIGNALINFORMATIONLIST, jsonConverter.convertSignalInfoToJSON(node.getFingerprint().getSignalInformationList()));
        values.put(NODE_COORDINATES, node.getCoordinates());
        values.put(NODE_PICTURE_PATH, node.getPicturePath());
        values.put(NODE_ADDITIONAL_INFO, node.getAdditionalInfo());

        database.insert(NODES_TABLE, null, values);

        Log.d("DB: insert_node:id:", node.getId());

        database.close();
    }

    // Update Nodes
    public void updateNode(Node node, String oldNodeId) {

        // Update Edges which contain the updated Node
        for (Edge e : getAllEdges()) {
            if (e.getNodeA().getId().equals(oldNodeId)) {
                updateEdge(e, EDGE_NODE_A, node.getId());
            } else if (e.getNodeB().getId().equals(oldNodeId)) {
                updateEdge(e, EDGE_NODE_B, node.getId());
            }
        }

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(NODE_ID, node.getId());
        contentValues.put(NODE_DESCRIPTION, node.getDescription());
        contentValues.put(NODE_COORDINATES, node.getCoordinates());
        contentValues.put(NODE_PICTURE_PATH, node.getPicturePath());
        contentValues.put(NODE_ADDITIONAL_INFO, node.getAdditionalInfo());

        Log.d("DB: update_node: id:", node.getId());

        database.update(NODES_TABLE, contentValues, NODE_ID + "='" + oldNodeId + "'", null);

        database.close();
    }


    // Get all Nodes
    public ArrayList<Node> getAllNodes() {
        ArrayList<Node> allNodes = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + NODES_TABLE;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Node node = nodeFactory.createInstance(cursor.getString(0), cursor.getString(1), new Fingerprint(jsonConverter.convertJsonToSignalInfo(cursor.getString(2))), cursor.getString(3), cursor.getString(4), cursor.getString(5));
                Log.d("DB: get_all_nodes", cursor.getString(0));

                allNodes.add(node);
            } while (cursor.moveToNext());
        }
        database.close();
        return allNodes;
    }


    // Get single Node
    public Node getNode(String nodeID) {
        SQLiteDatabase database = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + NODES_TABLE + " WHERE " + NODE_ID + " ='" + nodeID + "'";
        Node node = null;
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            node = nodeFactory.createInstance(cursor.getString(0), cursor.getString(1), new Fingerprint(jsonConverter.convertJsonToSignalInfo(cursor.getString(2))), cursor.getString(3), cursor.getString(4), cursor.getString(5));
            Log.d("DB: select_node", nodeID);
        }
        database.close();
        return node;
    }


    // Return true if Node with this nodeID (name) already exists.
    public boolean checkIfNodeExists(String nodeID) {
        if (getNode(nodeID) != null) {
            return true;
        }
        else { return false; }
    }


    // Delete Node
    public void deleteNode(Node node) {
        SQLiteDatabase database = this.getWritableDatabase();

        String deleteNodeQuery = "DELETE FROM " + NODES_TABLE + " WHERE " + NODE_ID + " ='" + node.getId() + "'";

        String deleteBelongingEdgeQuery = "DELETE FROM " + EDGES_TABLE + " WHERE " + EDGE_NODE_A + " ='" + node.getId() + "' OR " + EDGE_NODE_B + " ='" + node.getId() + "'";

        Log.d("DB: delete_node", node.getId());

        database.execSQL(deleteNodeQuery);
        database.execSQL(deleteBelongingEdgeQuery);
    }


    //----------------- R E S U L T S ------------------------------------------------------------------------------------------

    // Insert LocationResult
    public void insertLocationResult(LocationResult locationResult) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(RESULT_ID, locationResult.getId());
        values.put(RESULT_SETTINGS, locationResult.getSettings());
        values.put(RESULT_MEASURED_TIME, locationResult.getMeasuredTime());
        values.put(RESULT_SELECTED_NODE, locationResult.getSelectedNode());
        values.put(RESULT_MEASURED_NODE, locationResult.getMeasuredNode());

        database.insert(RESULTS_TABLE, null, values);

        Log.d("DB: insert_result", "###########");

        database.close();
    }


    // Get all LocationResults
    public ArrayList<LocationResultImplementation> getAllLocationResults() {
        ArrayList<LocationResultImplementation> allResults = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + RESULTS_TABLE;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                LocationResultImplementation locationResultImplementation = new LocationResultImplementation();

                Log.d("DB: get_all_locations", "###########");

                locationResultImplementation.setId(Integer.valueOf(cursor.getString(0)));
                locationResultImplementation.setSettings(cursor.getString(1));
                locationResultImplementation.setMeasuredTime(cursor.getString(2));
                locationResultImplementation.setSelectedNode(cursor.getString(3));
                locationResultImplementation.setMeasuredNode(cursor.getString(4));

                allResults.add(locationResultImplementation);
            } while (cursor.moveToNext());
        }

        database.close();
        return allResults;
    }


    // Delete LocatonResult
    public void deleteLocationResult(LocationResult locationResult) {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + RESULTS_TABLE + " WHERE " + RESULT_ID + " ='" + locationResult.getId() + "'";

        Log.d("DB: delete_LOCRESULT", "" + locationResult.getSelectedNode() + " id: " + locationResult.getId());

        database.execSQL(deleteQuery);
        database.close();
    }


    //----------- E D G E S -------------------------------------------------------------------------------------

    // Insert Edge
    public void insertEdge(Edge edge) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //values.put("id", edge.getId());
        values.put(EDGE_NODE_A, edge.getNodeA().getId());
        values.put(EDGE_NODE_B, edge.getNodeB().getId());
        values.put(EDGE_ACCESSIBLY, edge.getAccessibly());


        StringBuilder stepListSb = new StringBuilder();
        for (String string : edge.getStepCoordsList())
        {
            stepListSb.append(string);
            stepListSb.append("\t");
        }

        values.put(EDGE_STEPLIST, stepListSb.toString());
        values.put(EDGE_WEIGHT, edge.getWeight());
        values.put(EDGE_ADDITIONAL_INFO, edge.getAdditionalInfo());

        database.insert(EDGES_TABLE, null, values);

        Log.d("DB: insert_EDGE", edge.getNodeA().getId() + " " + edge.getNodeB().getId());

        database.close();
    }


    // Update Edges
    public void updateEdge(Edge edge, String key, String value) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if (key.equals(EDGE_NODE_A)) {
            contentValues.put(EDGE_NODE_A, value);
            contentValues.put(EDGE_NODE_B, edge.getNodeB().getId());

        } else if (key.equals(EDGE_NODE_B)) {
            contentValues.put(EDGE_NODE_B, value);
            contentValues.put(EDGE_NODE_A, edge.getNodeA().getId());
        }

        contentValues.put("accessibly", edge.getAccessibly());

        StringBuilder stepListSb = new StringBuilder();
        for (String string : edge.getStepCoordsList())
        {
            stepListSb.append(string);
            stepListSb.append("\t");
        }

        contentValues.put(EDGE_STEPLIST, stepListSb.toString());
        contentValues.put(EDGE_WEIGHT, edge.getWeight());
        contentValues.put(EDGE_ADDITIONAL_INFO, edge.getAdditionalInfo());

        database.update(EDGES_TABLE, contentValues, EDGE_NODE_A + "='" + edge.getNodeA().getId() + "' AND " + EDGE_NODE_B + "='" + edge.getNodeB().getId() + "'", null);

        database.close();
    }


    // Get all Edges
    public ArrayList<Edge> getAllEdges() {
        ArrayList<Edge> allEdges = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + EDGES_TABLE;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                boolean accessibly = false;

                if (cursor.getInt(3) == 1) {
                    accessibly = true;
                }

                Node nodeA = getNode(cursor.getString(1));
                Node nodeB = getNode(cursor.getString(2));

                String stepListString = cursor.getString(4);
                List<String> stepList = new ArrayList<>(Arrays.asList(stepListString.split("\t")));

                //Edge edge = new EdgeImplementation(Integer.valueOf(cursor.getString(0)), cursor.getString(1), cursor.getString(2), accessibly, cursor.getInt(4));
                Edge edge = new EdgeImplementation(nodeA, nodeB, accessibly, stepList, cursor.getInt(5), cursor.getString(6));

                /*
                // TODO Cast entfernen
                edge.setID(Integer.valueOf(cursor.getString(0)));
                edge.setNodeA(cursor.getString(1));
                edge.setNodeB(cursor.getString(2));
                edge.setWeight(cursor.getInt(4));*/

                allEdges.add(edge);

            } while (cursor.moveToNext());
            database.close();
        }
        return allEdges;
    }


    // Check if Edge already exists
    public boolean checkIfEdgeExists(Edge edge) {
        String selectQuery = "SELECT * FROM " + EDGES_TABLE + " WHERE " + EDGE_NODE_A + " ='" + edge.getNodeA().getId() + "' AND " + EDGE_NODE_B + " ='" + edge.getNodeB().getId() + "' " +
                " OR " + EDGE_NODE_A + " ='" + edge.getNodeB().getId() + "' AND " + EDGE_NODE_B + " ='" + edge.getNodeA().getId() + "' ";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            return true;
        } while (cursor.moveToNext());

        database.close();
        return false;
    }


    // Delete Edge
    public void deleteEdge(Edge edge) {
        SQLiteDatabase database = this.getWritableDatabase();
        //String deleteQuery = "DELETE FROM " + EDGES_TABLE + " WHERE " + EDGE_ID + " ='" + edge.getId() + "'";
        String deleteQuery = "DELETE FROM " + EDGES_TABLE + " WHERE " + EDGE_NODE_A + " ='" + edge.getNodeA().getId() + "' AND "+ EDGE_NODE_B + " ='" + edge.getNodeB().getId() + "'"
                + " OR " + EDGE_NODE_A + " ='" + edge.getNodeB().getId() + "' AND " + EDGE_NODE_B + " ='" + edge.getNodeA().getId() + "' ";


//        Log.d("DB: delete_EDGE", "" + edge.getId());
        Log.d("DB: delete_EDGE", "" + edge.getNodeA().getId() + " " + edge.getNodeB().getId());


        database.execSQL(deleteQuery);
        database.close();
    }


    /*TODO
    //-------------I M P O R T ------------------------------------------------------------------

    private String DB_FILEPATH = context.getFilesDir().getPath() + "/databases/indoor_data.db";

    /**
     * Copies the database file at the specified location over the current
     * internal application database.
     */
    /*
    public boolean importDatabase(String dbPath) throws IOException {

        // Close the SQLiteOpenHelper so it will commit the created empty database to internal storage
        close();

        File newDb = new File(dbPath);
        File oldDb = new File(DB_FILEPATH);
        if (newDb.exists()) {
            FileUtils.copyFile(new FileInputStream(newDb), new FileOutputStream(oldDb));
            // Access the copied database so SQLiteHelper will cache it and mark
            // it as created.
            getWritableDatabase().close();
            return true;
        }
        return false;
    }*/


    //------------------- E X P O R T ------------------------------------------------------------

    public void exportDatabase() {
        try {

            File exportFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/IndoorPositioning/Exported");
            if (!exportFolder.exists()) {
                exportFolder.mkdirs();
            }

            if (exportFolder.canWrite()) {
                String currentDBPath = context.getDatabasePath("indoor_data.db").getPath();

                String exportFilename = "indoor_data.db";
                File currentDB = new File(currentDBPath);
                File backupDB = new File(exportFolder, exportFilename);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch(Exception e) {e.printStackTrace();}
    }




    //------------------- F I N D   N O D E   F O R   P O S I T I O N ------------------------------------------------------------

    public String calculateNodeId(Node node) {

        measuredNode = new ArrayList<>();
        measuredNode.add(node);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean movingAverage = sharedPreferences.getBoolean("pref_movingAverage", true);
        boolean kalmanFilter = sharedPreferences.getBoolean("pref_kalman", false);
        boolean euclideanDistance = sharedPreferences.getBoolean("pref_euclideanDistance", false);
        boolean knnAlgorithm = sharedPreferences.getBoolean("pref_knnAlgorithm", true);

        int movingAverageOrder = Integer.parseInt(sharedPreferences.getString("pref_movivngAverageOrder", "3"));
        int knnValue = Integer.parseInt(sharedPreferences.getString("pref_knnNeighbours", "3"));
        int kalmanValue = Integer.parseInt(sharedPreferences.getString("pref_kalmanValue","2"));

        String poi = null;

        List<RestructedNode> restructedNodeList = calculateNewNodeDateSet(getAllNodes());
        List<RestructedNode> calculatedNodeList = new ArrayList<>();

        if (!restructedNodeList.isEmpty()) {
            if (movingAverage) {
                MovingAverage movingAverageClass = new MovingAverage();
                calculatedNodeList = movingAverageClass.calculation(restructedNodeList, averageOrder);
            } else if (kalmanFilter) {
                KalmanFilter kalmanFilterClass = new KalmanFilter(kalmanValue);
                calculatedNodeList = kalmanFilterClass.calculationKalman(restructedNodeList);
            }

            if (euclideanDistance) {
                List<MeasuredNode> actuallyNode = getActuallyNode(measuredNode);
                if (actuallyNode.size() == 0) {
                    return null;
                }
                EuclideanDistance euclideanDistanceClass = new EuclideanDistance();
                List<String> distanceNames = euclideanDistanceClass.calculateDistance(calculatedNodeList, actuallyNode);
                if (knnAlgorithm) {
                    KNearestNeighbor KnnClass = new KNearestNeighbor(knnValue);
                    poi = KnnClass.calculateKnn(distanceNames);
                } else {
                    poi = distanceNames.get(0);
                }
            }

            return poi;
        } else {
            return null;
        }
    }


    /**
     * rewrite actually node to type measured node
     * @param nodeList list of nodes
     * @return list of measured node
     */
    private List<MeasuredNode> getActuallyNode(List<Node> nodeList) {
        List<MeasuredNode> measuredNodeList = new ArrayList<>();

        for (int i = 0; i < nodeList.size(); i++) {
            List<SignalInformation> signalInformation = nodeList.get(i).getFingerprint().getSignalInformationList();
            for (SignalInformation sigInfo : signalInformation)
                for (SignalStrengthInformation ssi : sigInfo.getSignalStrengthInfoList()) {
                    String macAdress = ssi.macAdress;
                    int signalStrenght = ssi.signalStrength;
                    MeasuredNode measuredNode = new MeasuredNode(macAdress, signalStrenght);
                    measuredNodeList.add(measuredNode);
                }
        }

        return measuredNodeList;
    }


    /**
     * rewrite nodelist to restrucetd nodes and delete weak mac addresses
     * @param allExistingNodes list of all nodes
     * @return restructed node list
     */
    private List<RestructedNode> calculateNewNodeDateSet(List<Node> allExistingNodes) {
        List<String> macAddresses;
        int count = 0;

        List<RestructedNode> restructedNodes = new ArrayList<>();
        Multimap<String, Double> multiMap = null;

        for (Node node : allExistingNodes) {
            count = node.getFingerprint().getSignalInformationList().size();
            double minValue = (((double) 1 / (double) 3) * (double) count);
            macAddresses = getMacAddresses(node);
            multiMap = getMultiMap(node, macAddresses);

            //delete weak addresses
            for (String checkMacAdress : macAddresses) {
                int countValue = 0;

                for (Double signalValue : multiMap.get(checkMacAdress)) {
                    if (signalValue != null) {
                        countValue++;
                    }
                }
                if (countValue <= minValue) {
                    multiMap.removeAll(checkMacAdress);
                }
            }
            //fill restructed Nodes
            RestructedNode restructedNode = new RestructedNode(node.getId(), multiMap);
            restructedNodes.add(restructedNode);
        }
        return restructedNodes;
    }


    /**
     * create a multimap with mac address and values
     * @param node
     * @param macAdresses
     * @return multimap with mac address and vales
     */
    private Multimap<String, Double> getMultiMap(Node node, List<String> macAdresses) {
        Multimap<String, Double> multiMap = ArrayListMultimap.create();
        for (SignalInformation signalInfo : node.getFingerprint().getSignalInformationList()) {
            HashSet<String> actuallyMacAdresses = new HashSet<>();
            for (SignalStrengthInformation ssi : signalInfo.getSignalStrengthInfoList()) {
                multiMap.put(ssi.macAdress, (double) ssi.signalStrength);
                actuallyMacAdresses.add(ssi.macAdress);
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
     * get all mac addresses
     * @param node
     * @return list of unique mac addresses
     */
    private List<String> getMacAddresses(Node node) {
        HashSet<String> macAdresses = new HashSet<String>();
        for (SignalInformation sigInfo : node.getFingerprint().getSignalInformationList()) {
            for (SignalStrengthInformation ssi : sigInfo.getSignalStrengthInfoList()) {
                macAdresses.add(ssi.macAdress);
            }
        }
        List<String> uniqueList = new ArrayList<String>(macAdresses);
        return uniqueList;
    }


}

