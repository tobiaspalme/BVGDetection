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
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import de.htwberlin.f4.ai.ma.edge.EdgeImpl;
import de.htwberlin.f4.ai.ma.node.fingerprint.Fingerprint;
import de.htwberlin.f4.ai.ma.node.fingerprint.FingerprintImpl;
import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.node.NodeFactory;
import de.htwberlin.f4.ai.ma.edge.Edge;
import de.htwberlin.f4.ai.ma.node.fingerprint.SignalInformation;
import de.htwberlin.f4.ai.ma.node.fingerprint.signalstrength.SignalStrength;
import de.htwberlin.f4.ai.ma.node.fingerprint.signalstrength.SignalStrengthImpl;
import de.htwberlin.f4.ai.ma.location.LocationResult;
import de.htwberlin.f4.ai.ma.location.LocationResultImpl;
import de.htwberlin.f4.ai.ma.persistence.JSON.JSONConverter;
import de.htwberlin.f4.ai.ma.persistence.calculations.EuclideanDistance;
import de.htwberlin.f4.ai.ma.persistence.calculations.FoundNode;
import de.htwberlin.f4.ai.ma.persistence.calculations.KNearestNeighbor;
import de.htwberlin.f4.ai.ma.persistence.calculations.KalmanFilter;
import de.htwberlin.f4.ai.ma.persistence.calculations.MovingAverage;
import de.htwberlin.f4.ai.ma.persistence.calculations.RestructedNode;


/**
 * Created by Johann Winter
 */

class DatabaseHandlerImpl extends SQLiteOpenHelper implements DatabaseHandler {


    // Static variables

    private static final String DATABASE_NAME = "indoor_data.db";
    private static final int DATABASE_VERSION = 1;

    private static final String NODES_TABLE = "nodes";
    private static final String EDGES_TABLE = "edges";
    private static final String RESULTS_TABLE = "results";

    private static final String NODE_ID = "id";
    private static final String NODE_DESCRIPTION = "description";
    private static final String NODE_WIFI_NAME = "wifi_name";
    private static final String NODE_SIGNALINFORMATIONLIST = "signalinformationlist";
    private static final String NODE_COORDINATES = "coordinates";
    private static final String NODE_PICTURE_PATH = "picture_path";
    private static final String NODE_ADDITIONAL_INFO = "additional_info";

    private static final String EDGE_ID = "id";
    private static final String EDGE_NODE_A = "nodeA";
    private static final String EDGE_NODE_B = "nodeB";
    private static final String EDGE_ACCESSIBILITY = "accessibility";
    private static final String EDGE_STEPLIST = "steplist";
    private static final String EDGE_WEIGHT = "weight";
    private static final String EDGE_ADDITIONAL_INFO = "additional_info";

    private static final String RESULT_ID = "id";
    private static final String RESULT_SETTINGS = "settings";
    private static final String RESULT_MEASURED_TIME = "measuredtime";
    private static final String RESULT_MEASURED_NODE = "measurednode";
    private static final String RESULT_PERCENTAGE = "percentage";

    private JSONConverter jsonConverter = new JSONConverter();
    private Context context;


    // Constructor
    DatabaseHandlerImpl(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        // Nodes table
        String createNodeTableQuery = "CREATE TABLE " + NODES_TABLE + " (" +
                NODE_ID + " TEXT PRIMARY KEY," +
                NODE_DESCRIPTION + " TEXT," +
                NODE_WIFI_NAME + " TEXT, " +
                NODE_SIGNALINFORMATIONLIST + " TEXT," +
                NODE_COORDINATES + " TEXT," +
                NODE_PICTURE_PATH + " TEXT," +
                NODE_ADDITIONAL_INFO + " TEXT)";

        // Edges table
        String createEdgeTableQuery = "CREATE TABLE " + EDGES_TABLE + " (" +
                EDGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                EDGE_NODE_A + " TEXT," +
                EDGE_NODE_B + " TEXT," +
                EDGE_ACCESSIBILITY + " TEXT," +
                EDGE_STEPLIST + " TEXT," +
                EDGE_WEIGHT + " REAL," +
                EDGE_ADDITIONAL_INFO + " TEXT);";

        // LocationResults table
        String createResultTableQuery = "CREATE TABLE " + RESULTS_TABLE + " (" +
                RESULT_ID + " INTEGER PRIMARY KEY," +
                RESULT_SETTINGS + " TEXT," +
                RESULT_MEASURED_TIME + " TEXT," +
                RESULT_MEASURED_NODE + " TEXT," +
                RESULT_PERCENTAGE + " REAL)";

        db.execSQL(createNodeTableQuery);
        db.execSQL(createEdgeTableQuery);
        db.execSQL(createResultTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) { }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }



    //----------------- N O D E S ------------------------------------------------------------------------------------------

    /**
     * Insert a new Node
     * @param node the node to insert
     */
    public void insertNode(Node node) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(NODE_ID, node.getId());
        values.put(NODE_DESCRIPTION, node.getDescription());

        // If the Node has a fingerprint
        if (node.getFingerprintImpl() != null) {
            values.put(NODE_WIFI_NAME, node.getFingerprintImpl().getWifiName());
            values.put(NODE_SIGNALINFORMATIONLIST, jsonConverter.convertSignalInfoListToJSON(node.getFingerprintImpl().getSignalInformationList()));
        }

        values.put(NODE_COORDINATES, node.getCoordinates());
        values.put(NODE_PICTURE_PATH, node.getPicturePath());
        values.put(NODE_ADDITIONAL_INFO, node.getAdditionalInfo());

        database.insert(NODES_TABLE, null, values);

        Log.d("DB: insert_node:id:", node.getId());

        database.close();
    }

    /**
     * Update a Node
     * @param node the new Node
     * @param oldNodeId the original nodeID (name) which will be changed
     */
    public void updateNode(Node node, String oldNodeId) {

        // At first, update Edges which contain the updated Node
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
        contentValues.put(NODE_ADDITIONAL_INFO, node.getAdditionalInfo());

        // If the Node has a fingerprint
        if (node.getFingerprintImpl() != null) {
            contentValues.put(NODE_WIFI_NAME, node.getFingerprintImpl().getWifiName());
            contentValues.put(NODE_SIGNALINFORMATIONLIST, jsonConverter.convertSignalInfoListToJSON(node.getFingerprintImpl().getSignalInformationList()));
        }
//TODO
        /*
        if (getNode(oldNodeId).getPicturePath() != null) {
            String sdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            String newFilePath = "/IndoorPositioning/Pictures/" + node.getId() + "_" + realTimestamp + ".jpg";

            File oldImageFile = new File(node.getPicturePath());
            File newImageFile = new File(newFilePath);

            FileUtilities.copyFile(new FileInputStream(newDb), new FileOutputStream(oldDb));

        }
*/

        contentValues.put(NODE_PICTURE_PATH, node.getPicturePath());

        Log.d("DB: update_node: id:", node.getId());

        database.update(NODES_TABLE, contentValues, NODE_ID + "='" + oldNodeId + "'", null);

        database.close();
    }


    /**
     * Get a List of all Nodes
     * @return a list of all Nodes
     */
    public ArrayList<Node> getAllNodes() {
        ArrayList<Node> allNodes = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + NODES_TABLE;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Fingerprint fingerprint;

                // Check if fingerprintImpl exists, else create null object for fingerprintImpl
                if (cursor.getString(3) == null) {
                    fingerprint = null;
                } else {
                    fingerprint = new FingerprintImpl(cursor.getString(2), jsonConverter.convertJsonToSignalInfoList(cursor.getString(3)));
                }

                Node node = NodeFactory.createInstance(cursor.getString(0), cursor.getString(1), fingerprint, cursor.getString(4), cursor.getString(5), cursor.getString(6));
                Log.d("DB: get_all_nodes", cursor.getString(0));

                allNodes.add(node);
            } while (cursor.moveToNext());
        }
        database.close();
        return allNodes;
    }


    /**
     * Get a single Node
     * @param nodeID the name of the Node
     */
    public Node getNode(String nodeID) {
        SQLiteDatabase database = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + NODES_TABLE + " WHERE " + NODE_ID + " ='" + nodeID + "'";
        Node node = null;
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            Fingerprint fingerprint;

            // Check if fingerprintImpl exists, else create null object for fingerprintImpl
            if (cursor.getString(3) == null) {
                fingerprint = null;
            } else {
                fingerprint = new FingerprintImpl(cursor.getString(2), jsonConverter.convertJsonToSignalInfoList(cursor.getString(3)));
            }

            node = NodeFactory.createInstance(cursor.getString(0), cursor.getString(1), fingerprint, cursor.getString(4), cursor.getString(5), cursor.getString(6));
            Log.d("DB: select_node", nodeID);
        }
        database.close();
        return node;
    }


    /**
     * Check if a node already exists
     * @param nodeID the name of the Node
     * @return boolean, if Node exists
     */
    public boolean checkIfNodeExists(String nodeID) {
        if (getNode(nodeID) != null) {
            return true;
        }
        else { return false; }
    }


    /**
     * Delete a single Node
     * @param node the Node to be deleted
     */
    public void deleteNode(Node node) {
        SQLiteDatabase database = this.getWritableDatabase();

        String deleteNodeQuery = "DELETE FROM " + NODES_TABLE + " WHERE " + NODE_ID + " ='" + node.getId() + "'";

        String deleteBelongingEdgeQuery = "DELETE FROM " + EDGES_TABLE + " WHERE " + EDGE_NODE_A + " ='" + node.getId() + "' OR " + EDGE_NODE_B + " ='" + node.getId() + "'";

        Log.d("DB: delete_node", node.getId());

        database.execSQL(deleteNodeQuery);
        database.execSQL(deleteBelongingEdgeQuery);
    }




    //----------- E D G E S -------------------------------------------------------------------------------------

    /**
     * Insert an edge
     * @param edge the edge to be inserted
     */
    public void insertEdge(Edge edge) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(EDGE_NODE_A, edge.getNodeA().getId());
        values.put(EDGE_NODE_B, edge.getNodeB().getId());
        values.put(EDGE_ACCESSIBILITY, edge.getAccessibility());


        StringBuilder stepListSb = new StringBuilder();
        for (String string : edge.getStepCoordsList()) {
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

    /**
     * Update an Edge (only for changing nodeA and nodeB attribute of the Edge).
     * @param edge the Edge to be updated
     * @param nodeToBeUpdated the Edge's nodeA or nodeB
     * @param value the ID (name) of the Node
     */
    public void updateEdge(Edge edge, String nodeToBeUpdated, String value) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if (nodeToBeUpdated.equals(EDGE_NODE_A)) {
            contentValues.put(EDGE_NODE_A, value);
            contentValues.put(EDGE_NODE_B, edge.getNodeB().getId());

        } else if (nodeToBeUpdated.equals(EDGE_NODE_B)) {
            contentValues.put(EDGE_NODE_B, value);
            contentValues.put(EDGE_NODE_A, edge.getNodeA().getId());
        }

        contentValues.put(EDGE_ACCESSIBILITY, edge.getAccessibility());

        StringBuilder stepListSb = new StringBuilder();
        for (String string : edge.getStepCoordsList()) {
            stepListSb.append(string);
            stepListSb.append("\t");
        }

        contentValues.put(EDGE_STEPLIST, stepListSb.toString());
        contentValues.put(EDGE_WEIGHT, edge.getWeight());
        contentValues.put(EDGE_ADDITIONAL_INFO, edge.getAdditionalInfo());

        database.update(EDGES_TABLE, contentValues, EDGE_NODE_A + "='" + edge.getNodeA().getId() + "' AND " + EDGE_NODE_B + "='" + edge.getNodeB().getId() + "'", null);

        database.close();
    }


    /**
     * Update an Edge (everything but Edge's nodeA and nodeB attribute)
     * @param edge the Edge to be updated
     */
    public void updateEdge(Edge edge) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(EDGE_ACCESSIBILITY, edge.getAccessibility());

        StringBuilder stepListSb = new StringBuilder();
        for (String string : edge.getStepCoordsList()) {
            stepListSb.append(string);
            stepListSb.append("\t");
        }

        contentValues.put(EDGE_STEPLIST, stepListSb.toString());
        contentValues.put(EDGE_WEIGHT, edge.getWeight());
        contentValues.put(EDGE_ADDITIONAL_INFO, edge.getAdditionalInfo());

        database.update(EDGES_TABLE, contentValues, EDGE_NODE_A + "='" + edge.getNodeA().getId() + "' AND " + EDGE_NODE_B + "='" + edge.getNodeB().getId() + "'", null);

        database.close();
    }


    /**
     * Get single Edge
     * @param nodeA the startnode of the Edge
     * @param nodeB the endnode of the Edge
     * @return the Edge
     */
    public Edge getEdge(Node nodeA, Node nodeB) {
        String selectQuery = "SELECT * FROM " + EDGES_TABLE + " WHERE " + EDGE_NODE_A + "='" + nodeA.getId() + "' AND " + EDGE_NODE_B + "='" + nodeB.getId() + "' OR " +
                EDGE_NODE_A + "='" + nodeB.getId() + "' AND " + EDGE_NODE_B + "='" + nodeA.getId() + "'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        //Edge edge;

        if (cursor.moveToFirst()) {
            boolean accessible = false;
            if (cursor.getInt(3) == 1) {
                accessible = true;
            }
            Node node1 = getNode(cursor.getString(1));
            Node node2 = getNode(cursor.getString(2));

            String stepListString = cursor.getString(4);
            List<String> stepList = new ArrayList<>(Arrays.asList(stepListString.split("\t")));

            Edge edge = new EdgeImpl(node1, node2, accessible, stepList, cursor.getFloat(5), cursor.getString(6));

            database.close();
            return edge;
        }
        return null;
    }


    /**
     * Get a list of all Edges
     * @return the list of Edges
     */
    public ArrayList<Edge> getAllEdges() {
        ArrayList<Edge> allEdges = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + EDGES_TABLE;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                boolean accessible = false;

                if (cursor.getInt(3) == 1) {
                    accessible = true;
                }

                Node nodeA = getNode(cursor.getString(1));
                Node nodeB = getNode(cursor.getString(2));

                String stepListString = cursor.getString(4);
                List<String> stepList = new ArrayList<>(Arrays.asList(stepListString.split("\t")));

                //Edge edge = new EdgeImpl(Integer.valueOf(cursor.getString(0)), cursor.getString(1), cursor.getString(2), accessibly, cursor.getInt(4));
                Edge edge = new EdgeImpl(nodeA, nodeB, accessible, stepList, cursor.getFloat(5), cursor.getString(6));

                allEdges.add(edge);

            } while (cursor.moveToNext());
            database.close();
        }
        return allEdges;
    }


    /**
     * Check if an Edge already exists
     * @param edge the Edge to be checked
     * @return boolean, if Edge exists
     */
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


    /**
     * Delete a single Edge
     * @param edge the Edge to be deleted
     */
    public void deleteEdge(Edge edge) {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + EDGES_TABLE + " WHERE " + EDGE_NODE_A + " ='" + edge.getNodeA().getId() + "' AND "+ EDGE_NODE_B + " ='" + edge.getNodeB().getId() + "'"
                + " OR " + EDGE_NODE_A + " ='" + edge.getNodeB().getId() + "' AND " + EDGE_NODE_B + " ='" + edge.getNodeA().getId() + "' ";

        Log.d("DB: delete_EDGE", "" + edge.getNodeA().getId() + " " + edge.getNodeB().getId());

        database.execSQL(deleteQuery);
        database.close();
    }



    //----------------- L O C A T I O N     R E S U L T S ------------------------------------------------------------------------------------------

    /**
     * Insert a LocationResult
     * @param locationResult the LocationResult to be inserted
     */
    public void insertLocationResult(LocationResult locationResult) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(RESULT_ID, locationResult.getId());
        values.put(RESULT_SETTINGS, locationResult.getSettings());
        values.put(RESULT_MEASURED_TIME, locationResult.getMeasuredTime());
        values.put(RESULT_MEASURED_NODE, locationResult.getMeasuredNode());
        values.put(RESULT_PERCENTAGE, locationResult.getPercentage());

        database.insert(RESULTS_TABLE, null, values);

        Log.d("DB: insert_result", "###########");

        database.close();
    }


    /**
     * Get a list of LocationResults
     * @return the list of LocationResults
     */
    public ArrayList<LocationResult> getAllLocationResults() {
        ArrayList<LocationResult> allResults = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + RESULTS_TABLE;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                LocationResult locationResult = new LocationResultImpl();

                Log.d("DB: get_all_locations", "###########");

                locationResult.setId(Integer.valueOf(cursor.getString(0)));
                locationResult.setSettings(cursor.getString(1));
                locationResult.setMeasuredTime(cursor.getString(2));
                locationResult.setMeasuredNode(cursor.getString(3));
                locationResult.setPercentage(cursor.getFloat(4));

                allResults.add(locationResult);
            } while (cursor.moveToNext());
        }

        database.close();
        return allResults;
    }


    /**
     * Delete a single LocationResult
     * @param locationResult the LocationResult to be deleted
     */
    public void deleteLocationResult(LocationResult locationResult) {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + RESULTS_TABLE + " WHERE " + RESULT_ID + " ='" + locationResult.getId() + "'";

        database.execSQL(deleteQuery);
        database.close();
    }



    //------------- I M P O R T ------------------------------------------------------------------

    /**
     * Copies the database file at the specified location over the current
     * internal application database (existing data will be overwritten!).
     *
     * @param dbPath path to the (new) database file
     * @return return-code: true means successful, false unsuccessful
     */
    public boolean importDatabase(String dbPath) throws IOException {

        //String DB_FILEPATH = context.getFilesDir().getPath() + "/databases/indoor_data.db";
        String DB_FILEPATH = context.getApplicationInfo().dataDir + "/databases/indoor_data.db";

        // Close the SQLiteOpenHelper so it will commit the created empty database to internal storage
        close();

        File newDb = new File(dbPath);
        File oldDb = new File(DB_FILEPATH);
        if (newDb.exists()) {
            FileUtilities.copyFile(new FileInputStream(newDb), new FileOutputStream(oldDb));
            // Access the copied database so SQLiteHelper will cache it and mark
            // it as created.
            getWritableDatabase().close();
            return true;
        }
        return false;
    }



    //------------------- E X P O R T ------------------------------------------------------------


    /**
     * Export the database to SDCARD location
     * @return boolean, if action was successful
     */
    public boolean exportDatabase() {
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
                    return true;
                }
            }
        } catch(Exception e) {e.printStackTrace();}
        return false;
    }




    //------------------- F I N D   N O D E   F O R   P O S I T I O N ------------------------------------------------------------


    /**
     * Calculate a Node for a FingerprintImpl
     * @param fingerprint the input FingerprintImpl to be compared with all existent Nodes to get the position
     * @return the ID (name) of the resulting Node
     */
    //public FoundNode calculateNodeId(List<SignalInformation> signalInformationList) {
    public FoundNode calculateNodeId(Fingerprint fingerprint) {

        List<SignalInformation> signalInformationList = fingerprint.getSignalInformationList();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean movingAverage = sharedPreferences.getBoolean("pref_movingAverage", true);
        boolean kalmanFilter = sharedPreferences.getBoolean("pref_kalman", true);
        boolean euclideanDistance = sharedPreferences.getBoolean("pref_euclideanDistance", true);
        boolean knnAlgorithm = sharedPreferences.getBoolean("pref_knnAlgorithm", true);

        int movingAverageOrder = Integer.parseInt(sharedPreferences.getString("pref_movivngAverageOrder", "3"));
        int knnValue = Integer.parseInt(sharedPreferences.getString("pref_knnNeighbours", "3"));
        int kalmanValue = Integer.parseInt(sharedPreferences.getString("pref_kalmanValue","2"));

        FoundNode foundNode = null;


        // Load all nodes which have a valid fingerprintImpl
        List<Node> nodesWithFingerprint = new ArrayList<>();
        for (Node n : getAllNodes()) {
            if (n.getFingerprintImpl() != null) {
            //if (!n.getFingerprintImpl().getSignalInformationList().isEmpty()) {
                nodesWithFingerprint.add(n);
            }
        }

        List<RestructedNode> restructedNodeList = calculateNewNodeDateset(nodesWithFingerprint);
        List<RestructedNode> calculatedNodeList = new ArrayList<>();

        if (!restructedNodeList.isEmpty()) {
            if (movingAverage) {
                calculatedNodeList = MovingAverage.calculate(restructedNodeList, movingAverageOrder);

            } else if (kalmanFilter) {
                calculatedNodeList = KalmanFilter.calculateCalman(kalmanValue, restructedNodeList);
            }

            if (euclideanDistance) {
                List<SignalStrength> signalStrengths = getSignalStrengths(signalInformationList);

                if (signalStrengths.size() == 0) {
                    return null;
                }
                List<String> distanceNames = EuclideanDistance.calculateDistance(calculatedNodeList, signalStrengths);
                if (knnAlgorithm) {
                    foundNode = KNearestNeighbor.calculateKnn(knnValue, distanceNames);

                } else if (!distanceNames.isEmpty()) {
                    //TODO hier 100%?
                    foundNode = new FoundNode(distanceNames.get(0), 100.0);
                }
            }
            return foundNode;
        } else {
            return null;
        }
    }


    /**
     * Get a list of SignalStrengthInformations by passing a list of SignalInformation (unwrap).
     * @param signalInformationList a list of SignalInformations
     * @return a list of SignalStrengthInformations
     */
    private List<SignalStrength> getSignalStrengths(List<SignalInformation> signalInformationList) {
        List<SignalStrength> signalStrengths = new ArrayList<>();

        for (SignalInformation sigInfo : signalInformationList) {
            for (SignalStrength ssi : sigInfo.getSignalStrengthList()) {

                Log.d("DatabaseHanderlImpl", "getSignalStrengths,  MAC: " + ssi.getMacAddress() + " Strength: " + ssi.getRSSI());

                String macAdress = ssi.getMacAddress();
                int signalStrength = ssi.getRSSI();
                SignalStrength SSI = new SignalStrengthImpl(macAdress, signalStrength);

                signalStrengths.add(SSI);
            }
        }
        return signalStrengths;
    }




    /**
     * Rewrite the nodelist to restrucetd Nodes and delete weak MAC addresses
     * @param allNodes list of all nodes
     * @return restructed Node list
     */
    private List<RestructedNode> calculateNewNodeDateset(List<Node> allNodes) {
        List<String> macAddresses;
        int count = 0;

        List<RestructedNode> restructedNodes = new ArrayList<>();
        Multimap<String, Double> multiMap = null;

        for (Node node : allNodes) {
            count = node.getFingerprintImpl().getSignalInformationList().size();
            double minValue = (((double) 1 / (double) 3) * (double) count);
            macAddresses = getMacAddresses(node);
            multiMap = getMultiMap(node, macAddresses);

            //delete weak addresses
            for (String macAddress : macAddresses) {
                int countValue = 0;

                for (Double signalValue : multiMap.get(macAddress)) {
                    if (signalValue != null) {
                        countValue++;
                    }
                }
                if (countValue <= minValue) {
                    multiMap.removeAll(macAddress);
                    Log.d("DatabaseHandlerImpl", "calculateNewNodeDataset,   remove MAC: " + macAddress);
                }
            }
            //fill restructed Nodes
            RestructedNode restructedNode = new RestructedNode(node.getId(), multiMap);
            restructedNodes.add(restructedNode);
        }
        return restructedNodes;
    }


    /**
     * Create a multimap with MAC address and signal strength values
     * @param node input Node
     * @param macAdresses list of MAC addresses
     * @return multimap with mac address and signal strengths
     */
    private Multimap<String, Double> getMultiMap(Node node, List<String> macAdresses) {
        Multimap<String, Double> multiMap = ArrayListMultimap.create();
        for (SignalInformation signalInfo : node.getFingerprintImpl().getSignalInformationList()) {
            HashSet<String> actuallyMacAdresses = new HashSet<>();
            for (SignalStrength ssi : signalInfo.getSignalStrengthList()) {
                multiMap.put(ssi.getMacAddress(), (double) ssi.getRSSI());
                actuallyMacAdresses.add(ssi.getMacAddress());
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
     * Get all mac addresses of a specific Node
     * @param node the Node
     * @return list of unique MAC addresses
     */
    private List<String> getMacAddresses(Node node) {
        HashSet<String> macAdresses = new HashSet<String>();
        for (SignalInformation sigInfo : node.getFingerprintImpl().getSignalInformationList()) {
            for (SignalStrength ssi : sigInfo.getSignalStrengthList()) {
                macAdresses.add(ssi.getMacAddress());
            }
        }
        return new ArrayList<>(macAdresses);
    }


}

