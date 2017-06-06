package com.example.android.pets.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * This class define the contract to be used to create a SQL
 * database to store the pets
 *
 * Created by gusbru on 5/23/17.
 */

public final class PetContract {

    // Content Provider
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";

    // URI with the location of the database
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PETS = "pets";


    public static final class PetEntry implements BaseColumns {

        // table name
        public static final String TABLE_NAME = "pets";


        // Full URI to the database
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);

        // columns name
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_BREED = "breed";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_WEIGHT = "weight";

        // constant to define the gender
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;

    }
}
