package fr.millenium_blitz.projectpicars.util.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class MySQLiteHelper extends SQLiteOpenHelper {

    static final String TABLE_CONNEXIONS = "connexions";
    static final String COLUMN_ID = "_id";
    static final String COLUMN_ADRESSE = "adresse";

    private static final String DATABASE_NAME = "connexions.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_CONNEXIONS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_ADRESSE
            + " text not null);";

    MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONNEXIONS);
        onCreate(db);
    }

}