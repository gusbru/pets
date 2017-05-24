package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

import static com.example.android.pets.data.PetContract.PetEntry;

/**
 * This class extends SQLiteOpenHelper to use SQL database
 * to store pet information
 *
 * Created by gusbru on 5/24/17.
 */

public class PetDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shelter.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " +
            PetEntry.TABLE_NAME + " (" +
            PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PetEntry.COLUMN_NAME + " TEXT NOT NULL, " +
            PetEntry.COLUMN_BREED + " TEXT, " +
            PetEntry.COLUMN_GENDER + " INTEGER NOT NULL, " +
            PetEntry.COLUMN_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";

    private static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + PetEntry.TABLE_NAME + ";";

    // constructor
    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
