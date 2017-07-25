package de.htwberlin.f4.ai.ma.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;
import de.htwberlin.f4.ai.ma.fingerprint_generator.node.NodeFactory;


/**
 * Created by Johann Winter
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "indoor_data.db";
    private static final int DATABASE_VERSION = 1;

    private static final String NODES_TABLE = "nodes";
    private static final String EDGES_TABLE = "edges";

    private static final String NODE_ID = "id";
    private static final String NODE_DESCRIPTION = "description";
    private static final String NODE_SIGNALINFORMATIONLIST = "signalinformationlist";
    private static final String NODE_COORDINATES = "coordinates";
    private static final String NODE_PICTURE_PATH = "picture_path";

    private static final String EDGE_ID = "id";
    private static final String EDGE_NODE_A = "nodeA";
    private static final String EDGE_NODE_B = "nodeB";
    private static final String EDGE_ACCESSIBLY = "accessibly";


    private NodeFactory nodeFactory;


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createDbQuery = "CREATE TABLE " + NODES_TABLE + " (" +
                NODE_ID + " TEXT PRIMARY KEY," +
                NODE_DESCRIPTION + " TEXT," +
                NODE_SIGNALINFORMATIONLIST + " TEXT," +
                NODE_COORDINATES + " TEXT," +
                NODE_PICTURE_PATH + " TEXT)";

        db.execSQL(createDbQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {}


    public void insertNode(Node node) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id", node.getId());
        values.put("description", node.getDescription());
        //values.put("signalinformationlist", node.getSignalInformation().toString()); TODO: serialize signalinformationlist?
        values.put("coordinates", node.getCoordinates());
        values.put("picture_path", node.getPicturePath());

        database.insert(NODES_TABLE, null, values);

        Log.d("DB: insert_node:id:", node.getId());

        database.close();
    }


    public void updateNode(Node node, String oldNodeId) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("id", node.getId());
        contentValues.put("description",node.getDescription());
        // TODO contentValues.put("signalstrengthinformationlist", node.getSignalInformation());
        contentValues.put("coordinates", node.getCoordinates());
        contentValues.put("picture_path", node.getPicturePath());

        Log.d("DB: update_node: id:", node.getId());

        database.update(NODES_TABLE, contentValues, NODE_ID + "='" + oldNodeId + "'", null);
    }


    public ArrayList<Node> getAllNodes() {
        ArrayList<Node> allNodes = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + NODES_TABLE;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Node node = nodeFactory.getInstance();
                node.setId(cursor.getString(0));
                node.setDescription(cursor.getString(1));

                Log.d("DB: get_all_nodes", cursor.getString(0));

                //node.setSignalInformationList(); TODO: serialize signalinformationlist?
                node.setCoordinates(cursor.getString(3));
                node.setPicturePath(cursor.getString(4));
                allNodes.add(node);
            } while (cursor.moveToNext());
        }
        return allNodes;
    }


    public Node getNode(String nodeName) {
        SQLiteDatabase database = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + NODES_TABLE + " WHERE " + NODE_ID + " ='"+ nodeName +"'";
        Node node = nodeFactory.getInstance();

        Log.d("DB: select_node", nodeName);

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            node.setId(cursor.getString(0));
            node.setDescription(cursor.getString(1));
            //node.setSignalInformationList(); TODO: serialize signalinformationlist?
            node.setCoordinates(cursor.getString(3));
            node.setPicturePath(cursor.getString(4));
        }
        return node;
    }


    public void deleteNode(Node node) {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + NODES_TABLE + " WHERE " + NODE_ID + " ='"+ node.getId() +"'";

        Log.d("DB: delete_node", node.getId());

        database.execSQL(deleteQuery);
    }

}