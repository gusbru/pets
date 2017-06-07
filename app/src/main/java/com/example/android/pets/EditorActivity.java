/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.PetContract.PetEntry;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = EditorActivity.class.getSimpleName();

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = 0;

    // Uri for the selected pet
    Uri uriCurrentPet;

    // Loader ID
    private static final int PET_LOADER = 1;

    // Check if any field was changed
    private boolean mPetHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mPetHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        uriCurrentPet = intent.getData();

        // check if it is a new pet or update pet
        if (uriCurrentPet == null) {
            // Insert New Pet
            setTitle(getString(R.string.editor_activity_title_new_pet));
        } else {
            // Update Existing Pet
            setTitle(getString(R.string.editor_activity_title_edit_pet));

            // Initialize the Cursor Loader
            getLoaderManager().initLoader(PET_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        // Attach the listener to track changes
        mNameEditText.setOnTouchListener(mTouchListener);
        mBreedEditText.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = PetEntry.GENDER_UNKNOWN; // Unknown
            }
        });
    }

    /**
     * Get information from form and insert it into database
     *
     */
    private void savePet() {

        // get the info from the filled form
        String petName = mNameEditText.getText().toString().trim();
        String petBreed = mBreedEditText.getText().toString().trim();
        String stringWeight = mWeightEditText.getText().toString().trim();
        Integer petWeight;
        // Check the weight
        if (stringWeight.isEmpty()) {
            petWeight = 0;
        } else {
            petWeight = Integer.parseInt(stringWeight);
        }

        // check if all fields are empty
        if (petName.isEmpty() && petBreed.isEmpty() && stringWeight.isEmpty() && petWeight == 0) {
            Toast.makeText(this, "No new pet added", Toast.LENGTH_SHORT).show();
            return;
        }

        // put the value at ContentValue
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_NAME, petName);
        values.put(PetEntry.COLUMN_BREED, petBreed);
        values.put(PetEntry.COLUMN_GENDER, mGender);
        values.put(PetEntry.COLUMN_WEIGHT, petWeight);


        if (uriCurrentPet == null) {
            // ***** New pet *****

            // add the pet to database
            Uri uri = getContentResolver().insert(PetEntry.CONTENT_URI, values);

            // Display a Toast message with the status
            if (uri== null) {
                Toast.makeText(this, "Error saving pet", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Pet saved", Toast.LENGTH_SHORT).show();
            }
        } else {
            // ***** Update existing pet *****

            int nUpdates = getContentResolver().update(uriCurrentPet, values, null, null);

            if (nUpdates == 0) {
                Toast.makeText(this, "Pet Information NOT Updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Pet Update", Toast.LENGTH_SHORT).show();
            }


        }





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // insert the pet to database
                savePet();
                // close the windows with the forms
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                // if no changes, continue back to parent activity
                if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // if there are unsaved changes
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked "Discard Button"
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };

                // Show Dialog
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * Controls the unsaved changes warning dialog
     *
     */
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "Keep Editing"
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public void onBackPressed() {
        // nothing was changed
        if (!mPetHasChanged) {
            super.onBackPressed();
        }

        // something was changed
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "Discard Button"
                finish();
            }
        };

        // Show dialog
        showUnsavedChangesDialog(discardButtonClickListener);

    }

    /**
     *
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_NAME,
                PetEntry.COLUMN_BREED,
                PetEntry.COLUMN_GENDER,
                PetEntry.COLUMN_WEIGHT
        };

        return new CursorLoader(
                this,
                uriCurrentPet,
                projection,
                null,
                null,
                null);
    }

    /**
     *
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        String name = data.getString(data.getColumnIndexOrThrow(PetEntry.COLUMN_NAME));
        String breed = data.getString(data.getColumnIndexOrThrow(PetEntry.COLUMN_BREED));
        Integer gender = data.getInt(data.getColumnIndexOrThrow(PetEntry.COLUMN_GENDER));
        String weight = data.getString(data.getColumnIndexOrThrow(PetEntry.COLUMN_WEIGHT));
        mNameEditText.setText(name);
        mBreedEditText.setText(breed);
        mWeightEditText.setText(weight);
        mGenderSpinner.setSelection(gender);
    }

    /**
     *
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.clearComposingText();
        mBreedEditText.clearComposingText();
        mWeightEditText.clearComposingText();
        mGenderSpinner.setSelection(0);
    }
}