package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.pets.CatalogActivity;

/**
 * This class implements the Content Provider, an interface
 * to communicate the Content Resolver with the Database
 *
 * Created by gusbru on 6/5/17.
 */

public class PetProvider extends ContentProvider {

    // codes for Uri match
    private static final int PETS = 100;
    private static final int PETS_ID = 101;

    // Uri matcher
    private static final UriMatcher sUriMathcer = new UriMatcher(UriMatcher.NO_MATCH);

    // create the petDbHelper object to connect with the database
    private PetDbHelper petDbHelper;


    @Override
    public boolean onCreate() {
        // initialize the object petDbHelper
        petDbHelper = new PetDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    static {
        sUriMathcer.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        sUriMathcer.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PETS_ID);
    }

}
