package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.data.PetContract.PetEntry;

/**
 * Created by gusbru on 6/6/17.
 */

public class PetCursorAdapter extends CursorAdapter {

    // constructor
    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * The newView method inflate a new view and return it
     *
     * @param context
     * @param cursor
     * @param parent
     * @return
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_pet, parent, false);
    }

    /**
     * The bindView method is used to bind all data to a view
     * such as setting the text on a TextView
     *
     * @param view
     * @param context
     * @param cursor
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // find the fields to populate
        TextView petName = (TextView) view.findViewById(R.id.pet_name);
        TextView petBreed = (TextView) view.findViewById(R.id.pet_breed);

        // get the info from the cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow(PetEntry.COLUMN_NAME));
        String breed = cursor.getString(cursor.getColumnIndexOrThrow(PetEntry.COLUMN_BREED));

        // populate the fields
        petName.setText(name);
        petBreed.setText(breed);

    }
}
