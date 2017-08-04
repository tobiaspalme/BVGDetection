package de.htwberlin.f4.ai.ma.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;
import de.htwberlin.f4.ai.ma.fingerprint_generator.node.NodeFactory;
import de.htwberlin.f4.ai.ma.edge.Edge;
import de.htwberlin.f4.ai.ma.edge.EdgeImplementation;
import de.htwberlin.f4.ai.ma.location_result.LocationResult;
import de.htwberlin.f4.ai.ma.location_result.LocationResultImplementation;


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
    private static final String EDGE_EXPENDITURE = "expenditure";


    private NodeFactory nodeFactory;

    private JSONConverter jsonConverter = new JSONConverter();

    private Context context;


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
                EDGE_EXPENDITURE + " INTEGER);";


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

        values.put("id", node.getId());
        values.put("description", node.getDescription());
        values.put("signalinformationlist", jsonConverter.convertSignalInfoToJSON(node.getSignalInformation()));
        values.put("coordinates", node.getCoordinates());
        values.put("picture_path", node.getPicturePath());
        values.put("additional_info", node.getAdditionalInfo());

        database.insert(NODES_TABLE, null, values);

        Log.d("DB: insert_node:id:", node.getId());

        database.close();
    }

    // Update
    public void updateNode(Node node, String oldNodeId) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("id", node.getId());
        contentValues.put("description", node.getDescription());
        // TODO? Oder unnötig?               contentValues.put("signalstrengthinformationlist", node.getSignalInformation());
        contentValues.put("coordinates", node.getCoordinates());
        contentValues.put("picture_path", node.getPicturePath());
        contentValues.put("additional_info", node.getAdditionalInfo());

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
                Node node = nodeFactory.createInstance(cursor.getString(0), 0, cursor.getString(1), jsonConverter.convertJsonToSignalInfo(cursor.getString(2)), cursor.getString(3), cursor.getString(4), cursor.getString(5));
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
            node = nodeFactory.createInstance(cursor.getString(0), 0, cursor.getString(1), jsonConverter.convertJsonToSignalInfo(cursor.getString(2)), cursor.getString(3), cursor.getString(4), cursor.getString(5));
            Log.d("DB: select_node", nodeID);
        }
        database.close();
        return node;
    }


    // Delete Node
    public void deleteNode(Node node) {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + NODES_TABLE + " WHERE " + NODE_ID + " ='" + node.getId() + "'";

        Log.d("DB: delete_node", node.getId());

        database.execSQL(deleteQuery);
    }


    //----------------- R E S U L T S ------------------------------------------------------------------------------------------

    // Insert
    public void insertLocationResult(LocationResult locationResult) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id", locationResult.getId());
        values.put("settings", locationResult.getSettings());
        values.put("measuredtime", locationResult.getMeasuredTime());
        values.put("selectednode", locationResult.getSelectedNode());
        values.put("measurednode", locationResult.getMeasuredNode());

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

        Log.d("DB: delete_LOCRESULT", "" + locationResult.getId());
        System.out.println("REM LOCRES: " + locationResult.getSelectedNode());

        database.execSQL(deleteQuery);
        database.close();
    }


    //----------- E D G E S -------------------------------------------------------------------------------------

    // Insert
    public void insertEdge(Edge edge) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //values.put("id", edge.getId());
        values.put("nodeA", edge.getNodeA());
        values.put("nodeB", edge.getNodeB());
        values.put("accessibly", edge.getAccessibly());
        values.put("expenditure", edge.getExpenditure());

        database.insert(EDGES_TABLE, null, values);

        Log.d("DB: insert_EDGE", edge.getNodeA() + " " + edge.getNodeB());

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

                boolean accessibly;
                if (cursor.getString(3).equals("true")) {
                    accessibly = true;
                } else {
                    accessibly = false;
                }

                //Edge edge = new EdgeImplementation(Integer.valueOf(cursor.getString(0)), cursor.getString(1), cursor.getString(2), accessibly, cursor.getInt(4));
                Edge edge = new EdgeImplementation(cursor.getString(1), cursor.getString(2), accessibly, cursor.getInt(4));

                /*
                // TODO Cast entfernen
                edge.setID(Integer.valueOf(cursor.getString(0)));
                edge.setNodeA(cursor.getString(1));
                edge.setNodeB(cursor.getString(2));
                edge.setExpenditure(cursor.getInt(4));*/

                allEdges.add(edge);
            } while (cursor.moveToNext());
        }
        database.close();
        return allEdges;
    }


    // Check if Edge already exists
    public boolean checkIfEdgeExists(Edge edge) {
        String selectQuery = "SELECT * FROM " + EDGES_TABLE + " WHERE " + EDGE_NODE_A + " ='" + edge.getNodeA() + "' AND " + EDGE_NODE_B + " ='" + edge.getNodeB() + "'";

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
        String deleteQuery = "DELETE FROM " + EDGES_TABLE + " WHERE " + EDGE_NODE_A + " ='" + edge.getNodeA() + "' AND "+ EDGE_NODE_B + " ='" + edge.getNodeB() + "'";


//        Log.d("DB: delete_EDGE", "" + edge.getId());
        Log.d("DB: delete_EDGE", "" + edge.getNodeA() + " " + edge.getNodeB());


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


}

