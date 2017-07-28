package de.htwberlin.f4.ai.ma.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;
import de.htwberlin.f4.ai.ma.fingerprint_generator.node.NodeFactory;
import de.htwberlin.f4.ai.ma.indoor_graph.Edge;
import de.htwberlin.f4.ai.ma.indoor_graph.EdgeImplementation;
import de.htwberlin.f4.ai.ma.prototype_temp.location_result.LocationResult;
import de.htwberlin.f4.ai.ma.prototype_temp.location_result.LocationResultImpl;

/**
 * Created by Johann Winter
 */

public class DatabaseHandler extends SQLiteOpenHelper {

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

    private static final String RESULT_ID = "id";
    private static final String RESULT_SETTINGS = "settings";
    private static final String RESULT_MEASURED_TIME = "measuredtime";
    private static final String RESULT_SELECTED_NODE = "selectednode";
    private static final String RESULT_MEASURED_NODE = "measurednode";

    private static final String EDGE_ID = "id";
    private static final String EDGE_NODE_A = "nodeA";
    private static final String EDGE_NODE_B = "nodeB";
    private static final String EDGE_ACCESSIBLY = "accessibly";


    private NodeFactory nodeFactory;
    private JSONConverter jsonConverter = new JSONConverter();


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createNodeTableQuery = "CREATE TABLE " + NODES_TABLE + " (" +
                NODE_ID + " TEXT PRIMARY KEY," +
                NODE_DESCRIPTION + " TEXT," +
                NODE_SIGNALINFORMATIONLIST + " TEXT," +
                NODE_COORDINATES + " TEXT," +
                NODE_PICTURE_PATH + " TEXT)";

        String createResultTableQuery = "CREATE TABLE " + RESULTS_TABLE + " (" +
                RESULT_ID + " INTEGER PRIMARY KEY," +
                RESULT_SETTINGS + " TEXT," +
                RESULT_MEASURED_TIME + " TEXT," +
                RESULT_SELECTED_NODE + " TEXT," +
                RESULT_MEASURED_NODE + " TEXT)";

        String createEdgeTableQuery = "CREATE TABLE " + EDGES_TABLE + " (" +
                EDGE_ID + " INTEGER PRIMARY KEY," +
                EDGE_NODE_A + " TEXT," +
                EDGE_NODE_B + " TEXT," +
                EDGE_ACCESSIBLY + " TEXT)";

        db.execSQL(createNodeTableQuery);
        db.execSQL(createResultTableQuery);
        db.execSQL(createEdgeTableQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {}



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

        database.insert(NODES_TABLE, null, values);

        Log.d("DB: insert_node:id:", node.getId());

        database.close();
    }

    // Update
    public void updateNode(Node node, String oldNodeId) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("id", node.getId());
        contentValues.put("description",node.getDescription());
        // TODO? Oder unnötig?               contentValues.put("signalstrengthinformationlist", node.getSignalInformation());
        contentValues.put("coordinates", node.getCoordinates());
        contentValues.put("picture_path", node.getPicturePath());

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
                Node node = nodeFactory.getInstance();

                Log.d("DB: get_all_nodes", cursor.getString(0));

                node.setId(cursor.getString(0));
                node.setDescription(cursor.getString(1));
                node.setSignalInformationList(jsonConverter.convertJsonToSignalInfo(cursor.getString(2)));
                node.setCoordinates(cursor.getString(3));
                node.setPicturePath(cursor.getString(4));
                allNodes.add(node);
            } while (cursor.moveToNext());
        }
        database.close();
        return allNodes;
    }


    // Get single Node
    public Node getNode(String nodeName) {
        SQLiteDatabase database = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + NODES_TABLE + " WHERE " + NODE_ID + " ='"+ nodeName +"'";
        Node node = nodeFactory.getInstance();

        Log.d("DB: select_node", nodeName);

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            node.setId(cursor.getString(0));
            node.setDescription(cursor.getString(1));
            node.setSignalInformationList(jsonConverter.convertJsonToSignalInfo(cursor.getString(2)));
            node.setCoordinates(cursor.getString(3));
            node.setPicturePath(cursor.getString(4));
        }
        database.close();
        return node;
    }


    // Delete Node
    public void deleteNode(Node node) {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + NODES_TABLE + " WHERE " + NODE_ID + " ='"+ node.getId() +"'";

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
    public ArrayList<LocationResultImpl> getAllLocationResults() {
        ArrayList<LocationResultImpl> allResults = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + RESULTS_TABLE;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                LocationResultImpl locationResultImpl = new LocationResultImpl();

                Log.d("DB: get_all_locations", "###########");

                locationResultImpl.setId(Integer.valueOf(cursor.getString(0)));
                locationResultImpl.setSettings(cursor.getString(1));
                locationResultImpl.setMeasuredTime(cursor.getString(2));
                locationResultImpl.setSelectedNode(cursor.getString(3));
                locationResultImpl.setMeasuredNode(cursor.getString(4));

                allResults.add(locationResultImpl);
            } while (cursor.moveToNext());
        }

        database.close();
        return allResults;
    }


    // Delete LocatonResult
    public void deleteLocationResult(LocationResult locationResult) {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + RESULTS_TABLE + " WHERE " + RESULT_ID + " ='"+ locationResult.getId() +"'";

        Log.d("DB: delete_LOCRESULT", "" + locationResult.getId());
        System.out.println("REM LOCRES: " + locationResult.getSelectedNode());

        database.execSQL(deleteQuery);
    }




    //----------- E D G E S -------------------------------------------------------------------------------------

    // Insert
    public void insertEdge(Edge edge) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id", edge.getId());
        values.put("nodeA", edge.getNodeA());
        values.put("nodeB", edge.getNodeB());
        values.put("accessibly", edge.getAccessibly());

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
                Edge edge = new EdgeImplementation();

                // TODO Cast entfernen
                edge.setID(Integer.valueOf(cursor.getString(0)));
                edge.setNodeA(cursor.getString(1));
                edge.setNodeB(cursor.getString(2));

                if (cursor.getString(3).equals("true")) {
                    edge.setAccessibly(true);
                } else if (cursor.getString(3).equals("false")) {
                    edge.setAccessibly(false);
                }

                allEdges.add(edge);
            } while (cursor.moveToNext());
        }

        database.close();
        return allEdges;
    }


    // Delete Edge
    public void deleteEdge(Edge edge) {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + EDGES_TABLE + " WHERE " + EDGE_ID + " ='"+ edge.getId() +"'";

        Log.d("DB: delete_EDGE", "" + edge.getId());

        database.execSQL(deleteQuery);
    }

}

