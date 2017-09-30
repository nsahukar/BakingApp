package com.nsahukar.android.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nsahukar.android.bakingapp.data.BakingAppContract.RecipesEntry;

/**
 * Created by Nikhil on 10/08/17.
 */

public class BakingAppDbHelper extends SQLiteOpenHelper {

    // Database name
    private static final String DATABASE_NAME = "bakingapp.db";

    // Databse version
    private static final int DATABASE_VERSION = 1;

    public BakingAppDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // Database methods

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // SQLite statement that will create recipes table
        final String SQL_CREATE_RECIPES_TABLE =

                "CREATE TABLE " + RecipesEntry.TABLE_NAME + " (" +

                        RecipesEntry._ID +                      " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        RecipesEntry.COLUMN_NAME +              " TEXT NOT NULL, " +
                        RecipesEntry.COLUMN_SERVINGS +          " INTEGER NOT NULL, " +
                        RecipesEntry.COLUMN_IMAGE +             " TEXT NOT NULL, " +
                        RecipesEntry.COLUMN_INGREDIENTS +       " TEXT NOT NULL, " +
                        RecipesEntry.COLUMN_STEPS +             " TEXT NOT NULL " +

                 ");";

        // Execute the sql statement with the execSQL method of our SQLite database object
        sqLiteDatabase.execSQL(SQL_CREATE_RECIPES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}
