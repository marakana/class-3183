
package com.marakana.android.yamba;

import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.database.Cursor;

public class TimelineActivity extends ListActivity implements LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 7;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // return a cursor loader
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        ((SimpleCursorAdapter) getListAdapter()).swapCursor(c);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        ((SimpleCursorAdapter) getListAdapter()).swapCursor(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SimpleCursorAdapter adapter = null;
        // create a simple cursor adapter
        // setListAdapter(adapter);

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }
}
