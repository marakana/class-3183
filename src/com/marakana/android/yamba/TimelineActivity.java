
package com.marakana.android.yamba;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;

import com.marakana.android.yamba.data.YambaContract;

public class TimelineActivity extends ListActivity implements LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 7;

    private static final String[] COLUMNS = new String[] {
        YambaContract.Timeline.Columns.ID,
        YambaContract.Timeline.Columns.USER,
        YambaContract.Timeline.Columns.TIMESTAMP,
        YambaContract.Timeline.Columns.STATUS
    };

    private static final String[] FROM = new String[COLUMNS.length -1];
    static { System.arraycopy(COLUMNS, 1, FROM, 0, FROM.length); };

    private static final int[] TO = new int[] {
        R.id.timeline_user,
        R.id.timeline_time,
        R.id.timeline_status
    };

    static class TimelineViewBinder implements SimpleCursorAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int colIndex) {
            if (R.id.timeline_time != view.getId()) { return false; }

            long t = cursor.getLong(colIndex);
            ((TextView) view).setText(
                (0 >= t)
                    ? "long ago"
                    : DateUtils.getRelativeTimeSpanString(t, System.currentTimeMillis(), 0));
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                 this,
                 R.layout.timeline_row,
                 null,
                 FROM,
                 TO,
                0);
        setListAdapter(adapter);
        adapter.setViewBinder(new TimelineViewBinder());
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                YambaContract.Timeline.URI,
                COLUMNS,
                null,
                null,
                YambaContract.Timeline.Columns.TIMESTAMP + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        ((SimpleCursorAdapter) getListAdapter()).swapCursor(c);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        ((SimpleCursorAdapter) getListAdapter()).swapCursor(null);
    }
}
