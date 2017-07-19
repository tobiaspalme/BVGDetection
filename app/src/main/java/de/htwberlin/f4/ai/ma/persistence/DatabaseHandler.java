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
        String createDB = "CREATE TABLE " + NODES_TABLE + " (" +
                NODE_ID + " TEXT PRIMARY KEY," +
                NODE_SIGNALINFORMATIONLIST + " TEXT," +
                NODE_COORDINATES + " TEXT," +
                NODE_PICTURE_PATH + " TEXT)";

        db.execSQL(createDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {}

    /*
    public void insertNode(SQLiteDatabase db, Node node) {
        String query = "INSERT INTO " + NODES_TABLE + " ("
                + NODE_ID + ", "
                + NODE_SIGNALINFORMATIONLIST + ", "
                + NODE_COORDINATES + ", "
                + NODE_PICTURE_PATH + ") VALUES " + "('"
                + node.getId() + "','"
                + node.getSignalInformation() + "','"
                + node.getCoordinates() + "','"
                + node.getPicture() + "')" ;

        System.out.println("##### DB insert query: " + query);
        try {
            db.execSQL(query);
        } catch (SQLiteException sqliteException) { Log.d("DB Error" , "Insert Error"); }
    }
    */

    public void insertNode(Node node) {

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id", node.getId());
        //values.put("signalinformationlist", node.getSignalInformation().toString()); TODO: serialize signalinformationlist?
        values.put("coordinates", node.getCoordinates());
        values.put("picture_path", node.getPicturePath());

        database.insert(NODES_TABLE, null, values);
        database.close();
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

                Log.d("GETALLNODES:ID", cursor.getString(0));

                //node.setSignalInformationList(); TODO: serialize signalinformationlist?
                node.setCoordinates(cursor.getString(2));
                node.setPicturePath(cursor.getString(3));
                allNodes.add(node);
            } while (cursor.moveToNext());
        }
        return allNodes;
    }


}