package com.mahoucoder.misakagate.data;

import android.provider.BaseColumns;

/**
 * Define a Schema and Contract
 *
 * A contract class is a container for constants that define names for URIs, tables, and columns.
 * The contract class allows you to use the same constants across all the other classes in the
 * same package. This lets you change a column name in one place and have it propagate throughout
 * your code.
 *
 * A good way to organize a contract class is to put definitions that are global to your whole
 * database in the root level of the class. Then create an inner class for each table that
 * enumerates its columns.
 */

public final class AnimeListContract {
    private AnimeListContract() {

    }

    public static class Anime implements BaseColumns {
        public static final String TABLE_NAME = "animations";
        public static final String COLUMN_NAME_FID = "fid";
        public static final String COLUMN_NAME_TID = "tid";
        public static final String COLUMN_NAME_SUBJECT = "subject";
        public static final String COLUMN_NAME_DATELINE = "dateline";
        public static final String COLUMN_NAME_LASTPOST = "lastpost";
        public static final String COLUMN_NAME_PIC = "pic";
        public static final String COLUMN_NAME_YEAR = "year";
        public static final String COLUMN_NAME_SEASON = "season";
        public static final String COLUMN_NAME_EXTRA = "extra";
    }
}
