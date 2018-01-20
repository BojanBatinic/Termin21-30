package com.example.androiddevelopment.glumcilegende.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by BBLOJB on 20.1.2018..
 */

public class GlumacContract {

    public static final String DATABASE_NAME = "MyDatabase";
    public static final int DATABASE_VERSION = 1;

    public static final String AUTORITY = "com.example.androiddevelopment.glumcilegende";

    public static class Glumac implements BaseColumns
    {
        public static final String TABLE_NAME = "glumci";

        public static final String CONTENT_URI_PATH = TABLE_NAME;

        public static final String MIMETYPE_TYPE = TABLE_NAME;
        public static final String MIMETYPE_NAME = AUTORITY + ".provider";

        public static final String FILED_NAME_NAME = "name";
        public static final String FILED_NAME_BIOGRAFIJA = "biografija";
        public static final String FILED_NAME_RATING = "rating";
        public static final String FILED_NAME_IMAGE = "image";

        public static final int CONTENT_URI_PATTERN_MANY = 1;
        public static final int CONTENT_URI_PATTERN_ONE = 2;

        public static final Uri contentUri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(AUTORITY)
                .appendPath(CONTENT_URI_PATH).build();
    }
}
