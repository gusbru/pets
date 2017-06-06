package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.pets.data.PetContract.PetEntry;

/**
 * This class implements the Content Provider, an interface
 * to communicate the Content Resolver with the Database
 *
 * Created by gusbru on 6/5/17.
 */

public class PetProvider extends ContentProvider {

    private static final String LOG_TAG = PetProvider.class.getSimpleName();

    // codes for Uri match
    private static final int PETS = 100;
    private static final int PETS_ID = 101;

    // Uri matcher
    private static final UriMatcher sUriMathcer = new UriMatcher(UriMatcher.NO_MATCH);

    // create the petDbHelper object to connect with the database
    private PetDbHelper mPetDbHelper;


    /**
     * Initialization
     *
     * @return
     */
    @Override
    public boolean onCreate() {
        // initialize the object petDbHelper
        mPetDbHelper = new PetDbHelper(getContext());

        return true;
    }

    /**
     * Uri Matcher
     *
     */
    static {
        sUriMathcer.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        sUriMathcer.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PETS_ID);
    }


    /**
     *********** QUERY ***********
     *
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        // read the database
        SQLiteDatabase database = mPetDbHelper.getReadableDatabase();

        // Declare the Cursor to be returned in the end
        Cursor cursor;

        // Run the Uri Matcher
        final int matcher = sUriMathcer.match(uri);

        // Select between PETS or PETS_ID case
        switch (matcher) {
            case PETS:
                cursor = database.query(
                        PetEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            case PETS_ID:
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(
                        PetEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        return cursor;
    }

    /**
     *********** GET TYPE ***********
     *
     * @param uri
     * @return
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     *********** INSERT ***********
     *
     * @param uri
     * @param values
     * @return
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMathcer.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Helper method to add Pet to the database.
     * This method is called inside the INSERT method
     *
     * @param uri
     * @param values
     * @return
     */
    private Uri insertPet(Uri uri, ContentValues values) {
        // sanity check the values
        String name = values.getAsString(PetEntry.COLUMN_NAME);
        Integer gender = values.getAsInteger(PetEntry.COLUMN_GENDER);
        Integer weight = values.getAsInteger(PetEntry.COLUMN_WEIGHT);

        // check name
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        // check gender
        if (gender == null || !isValidGender(gender)) {
            throw new IllegalArgumentException("Pet requires valid gender");
        }

        // check weight
        if ((weight != null) && (weight < 0)) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }



        // access the database in write mode
        SQLiteDatabase database = mPetDbHelper.getWritableDatabase();

        // insert the pet to database
        long id = database.insert(PetEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Check if the entered gender is valid
     * UNKNOWN = 0
     * MALE = 1
     * FEMALE = 2
     * @param gender
     * @return
     */
    private boolean isValidGender(int gender) {
        return gender == PetEntry.GENDER_UNKNOWN || gender == PetEntry.GENDER_MALE || gender == PetEntry.GENDER_FEMALE;
    }

    /**
     *********** DELETE ***********
     *
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    /**
     *********** UPDATE ***********
     *
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }



}
