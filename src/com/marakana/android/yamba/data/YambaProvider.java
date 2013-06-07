/* $Id: $
   Copyright 2013, G. Blake Meike

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.marakana.android.yamba.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.marakana.android.yamba.BuildConfig;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public class YambaProvider extends ContentProvider {
    private static final String TAG = "CP";

    private static final int TIMELINE_DIR = 1;
    private static final int TIMELINE_ITEM = 2;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
       uriMatcher.addURI(
                YambaContract.AUTHORITY,
                YambaContract.Timeline.TABLE,
                TIMELINE_DIR);
        uriMatcher.addURI(
                YambaContract.AUTHORITY,
                YambaContract.Timeline.TABLE + "/#",
                TIMELINE_ITEM);
    }

    private static final ProjectionMap PROJ_MAP_TIMELINE = new ProjectionMap.Builder()
        .addColumn(YambaContract.Timeline.Columns.ID, YambaDBHelper.COL_ID)
        .addColumn(YambaContract.Timeline.Columns.TIMESTAMP, YambaDBHelper.COL_CREATED_AT)
        .addColumn(YambaContract.Timeline.Columns.USER, YambaDBHelper.COL_USER)
        .addColumn(YambaContract.Timeline.Columns.STATUS, YambaDBHelper.COL_STATUS)
        .addColumn(YambaContract.Timeline.Columns.MAX_TIMESTAMP, "max(" + YambaDBHelper.COL_CREATED_AT + ")")
        .build();

    private static final ColumnMap COL_MAP_TIMELINE = new ColumnMap.Builder()
        .addColumn(
                YambaContract.Timeline.Columns.ID,
                YambaDBHelper.COL_ID,
                ColumnMap.Type.STRING)
        .addColumn(
                YambaContract.Timeline.Columns.TIMESTAMP,
                YambaDBHelper.COL_CREATED_AT,
                ColumnMap.Type.LONG)
        .addColumn(
                YambaContract.Timeline.Columns.USER,
                YambaDBHelper.COL_USER,
                ColumnMap.Type.STRING)
        .addColumn(
                YambaContract.Timeline.Columns.STATUS,
                YambaDBHelper.COL_STATUS,
                ColumnMap.Type.STRING)
        .build();

    private YambaDBHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new YambaDBHelper(getContext());
        return null != dbHelper;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case TIMELINE_ITEM:
                return YambaContract.Timeline.ITEM_TYPE;
            case TIMELINE_DIR:
                return YambaContract.Timeline.DIR_TYPE;
            default:
                return null;
        }
    }

    @SuppressWarnings("fallthrough")
    @Override
    public Cursor query(Uri uri, String[] proj, String sel, String[] selArgs, String sort) {
        long pk = -1;

        if (BuildConfig.DEBUG) { Log.d(TAG, "query: " + uri); }
        switch (uriMatcher.match(uri)) {
            case TIMELINE_ITEM:
                pk = ContentUris.parseId(uri);

            case TIMELINE_DIR:
                break;

            default:
                throw new IllegalArgumentException("URI unsupported in query: " + uri);
        }

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            qb.setStrict(true);
        }

        qb.setProjectionMap(PROJ_MAP_TIMELINE.getProjectionMap());

        qb.setTables(YambaDBHelper.TABLE);

        if (0 <= pk) { qb.appendWhere(YambaDBHelper.COL_ID + "=" + pk); }

        Cursor c = qb.query(getDb(), proj, sel, selArgs, null, null, sort);

        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] vals) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "bulk insert: " + uri); }

        switch (uriMatcher.match(uri)) {
            case TIMELINE_DIR:
                break;

            default:
                throw new UnsupportedOperationException("URI unsupported in bulk insert: " + uri);
        }


        SQLiteDatabase db = getDb();
        int count = 0;
        try {
            db.beginTransaction();
            for (ContentValues row: vals) {
                row = COL_MAP_TIMELINE.translateCols(row);
                if (0 <= db.insert(YambaDBHelper.TABLE, null, row))
                {
                    count++;
                }
            }
            db.setTransactionSuccessful();
        }
        finally { db.endTransaction(); }

        if (0 < count) {
            getContext().getContentResolver().notifyChange(YambaContract.Timeline.URI, null);
        }

        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("delete not supported");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("insert not supported");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("update not supported");
    }

    private SQLiteDatabase getDb() {
        return dbHelper.getWritableDatabase();
    }
}
