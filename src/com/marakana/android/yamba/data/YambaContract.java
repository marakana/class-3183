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

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public final class YambaContract {
    private YambaContract() { }

    /** Authority for the Yamba Content Provider */
    public static final String AUTHORITY = "com.marakana.android.yamba.timeline";

    /** Base URI for the Yamba Content Provider */
    public static final Uri BASE_URI = new Uri.Builder()
        .scheme(ContentResolver.SCHEME_CONTENT)
        .authority(AUTHORITY)
        .build();

    /** The Timeline database **/
    public static final class Timeline {
        private Timeline() {}

        /** MIME sub-type for timeline content */
        public static final String MIME_SUBTYPE = "/vnd.com.marakana.yamba.timeline";
        /** Timeline table ITEM type */
        public static final String ITEM_TYPE
            = ContentResolver.CURSOR_ITEM_BASE_TYPE + MIME_SUBTYPE;
        /** Timeline table DIR type */
        public static final String DIR_TYPE
            = ContentResolver.CURSOR_DIR_BASE_TYPE + MIME_SUBTYPE;

        public static final String TABLE = "timeline";

        public static final Uri URI
        = BASE_URI.buildUpon().appendPath(TABLE).build();

        public static final class Columns {
            private Columns() {}

            public static final String ID = BaseColumns._ID;
            public static final String TIMESTAMP = "timestamp";
            public static final String USER = "user";
            public static final String STATUS = "status";
            public static final String MAX_TIMESTAMP = "max_timestamp";
        }
    }
}
