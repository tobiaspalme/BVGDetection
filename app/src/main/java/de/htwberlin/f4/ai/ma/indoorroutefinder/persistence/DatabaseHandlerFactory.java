package de.htwberlin.f4.ai.ma.indoorroutefinder.persistence;

import android.content.Context;

/**
 * Factory for creating and retrieving a DatabaseHandler singleton object.
 *
 * Created by Johann Winter
 */

public class DatabaseHandlerFactory {

    private static DatabaseHandler databaseHandler;

    public static DatabaseHandler getInstance(Context context) {
        if (databaseHandler == null) {
            databaseHandler = new DatabaseHandlerImpl(context);
        }

        return databaseHandler;
    }
}
