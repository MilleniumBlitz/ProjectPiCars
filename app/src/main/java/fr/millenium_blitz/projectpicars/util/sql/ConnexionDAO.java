package fr.millenium_blitz.projectpicars.util.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ConnexionDAO {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_ADRESSE };

    public ConnexionDAO(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Connexion createConnexion(String adresse) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ADRESSE, adresse);
        long insertId = database.insert(MySQLiteHelper.TABLE_CONNEXIONS, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_CONNEXIONS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Connexion newConnexion = cursorToConnexion(cursor);
        cursor.close();
        return newConnexion;
    }

    public void deleteConnexion(Connexion connexion) {
        long id = connexion.getId();
        System.out.println("Connection deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_CONNEXIONS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Connexion> getAllConnexions() {
        List<Connexion> connexions = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_CONNEXIONS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Connexion connexion = cursorToConnexion(cursor);
            connexions.add(connexion);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return connexions;
    }

    private Connexion cursorToConnexion(Cursor cursor) {
        Connexion connexion = new Connexion();
        connexion.setId(cursor.getInt(0));
        connexion.setAdresse(cursor.getString(1));
        return connexion;
    }

}
