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
package com.marakana.android.yamba.svc;

import java.util.ArrayList;
import java.util.List;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import com.marakana.android.yamba.BuildConfig;
import com.marakana.android.yamba.YambaApplication;
import com.marakana.android.yamba.clientlib.YambaClient.Status;
import com.marakana.android.yamba.data.YambaContract;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public class YambaService extends IntentService {
    private static final String TAG = "SVC";

    private static final String KEY_OP = "YambaService.OP";
    private static final String KEY_STATUS = "YambaService.STATUS";

    private static final int OP_POST = -1;
    private static final int OP_POLL = -2;
    private static final int OP_START_POLL = -3;

    private static final long POLL_INTERVAL = 3 * 60 * 1000;

    private static final int INTENT_TAG = 19;

    public static void post(Context ctxt, String status) {
        Intent i = new Intent(ctxt, YambaService.class);
        i.putExtra(KEY_OP, OP_POST);
        i.putExtra(KEY_STATUS, status);
        ctxt.startService(i);
    }

    public static void startPoller(Context ctxt) {
        Intent i = new Intent(ctxt, YambaService.class);
        i.putExtra(KEY_OP, OP_START_POLL);
        ctxt.startService(i);
    }

    public YambaService() {
        super(TAG);
        if (BuildConfig.DEBUG) { Log.d(TAG, "ctor"); }
    }

    @Override
    public void onCreate() {
        if (BuildConfig.DEBUG) { Log.d(TAG, "on create"); }
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "on start"); }
        return super.onStartCommand(intent, flags, startId);
    }

    // RUN ON DAEMON THREAD!!!
    @Override
    protected void onHandleIntent(Intent intent) {
        int op = intent.getIntExtra(KEY_OP, 0);
        switch (op) {
            case OP_POST:
                doPost(intent.getStringExtra(KEY_STATUS));
                break;

            case OP_POLL:
                doPoll();
                break;

            case OP_START_POLL:
                doStartPoll();
                break;

            default:
                Log.w(TAG, "Unrecognized op: " + op);
        }
    }

    // RUN ON DAEMON THREAD!!!
    private void doPost(String status) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "Posting: " + status); }

        try {
            ((YambaApplication) getApplication())
                .getClient().post(status);
        }
        catch (Exception e) { Log.e(TAG, "Post failed!", e); }
    }

    // RUN ON DAEMON THREAD!!!
    private void doPoll() {
        if (BuildConfig.DEBUG) { Log.d(TAG, "Poll"); }

        try {
            processTimeline(((YambaApplication) getApplication())
                .getClient().poll());
        }
        catch (Exception e) {

            Log.e(TAG, "Poll failed!", e);

            for (Throwable t = e; t != null; t.getCause()) {
                e.printStackTrace();
            }
        }
    }

    // RUN ON DAEMON THREAD!!!
    private void doStartPoll() {
        if (BuildConfig.DEBUG) { Log.d(TAG, "Start Polling"); }

        Intent intent = new Intent(this, YambaService.class);
        intent.putExtra(KEY_OP, OP_POLL);

        ((AlarmManager) getSystemService(Context.ALARM_SERVICE))
            .setRepeating(
                AlarmManager.RTC,
                System.currentTimeMillis() + 100,
                POLL_INTERVAL,
                PendingIntent.getService(
                        this,
                        INTENT_TAG,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT));
    }

    // RUN ON DAEMON THREAD!!!
    private void processTimeline(List<Status> timeline) {
        List<ContentValues> statuses = new ArrayList<ContentValues>();
        long mostRecent = getMostRecentStatusTime();
        for (Status status: timeline) {
            long createdAt = status.getCreatedAt().getTime();
            if (mostRecent < createdAt) {
                ContentValues vals = new ContentValues();
                vals.put(YambaContract.Timeline.Columns.ID, Long.valueOf(status.getId()));
                vals.put(YambaContract.Timeline.Columns.TIMESTAMP, Long.valueOf(createdAt));
                vals.put(YambaContract.Timeline.Columns.USER, status.getUser());
                vals.put(YambaContract.Timeline.Columns.STATUS, status.getMessage());
                statuses.add(vals);
            }
        }

        int recs = statuses.size();
        if (0 < recs) {
            ContentValues[] data = new ContentValues[recs];
            getContentResolver().bulkInsert(
                    YambaContract.Timeline.URI,
                    statuses.toArray(data));
        }
    }

    private long getMostRecentStatusTime() {
        Cursor c = getContentResolver().query(
                YambaContract.Timeline.URI,
                new String[] { YambaContract.Timeline.Columns.MAX_TIMESTAMP },
                null,
                null,
                null);
        try { return (c.moveToNext()) ? c.getLong(0) : Long.MIN_VALUE; }
        finally { c.close(); }
    }
}

