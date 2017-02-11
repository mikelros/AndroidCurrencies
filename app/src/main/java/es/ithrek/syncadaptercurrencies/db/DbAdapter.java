package es.ithrek.syncadaptercurrencies.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import es.ithrek.syncadaptercurrencies.models.Currency;

/**
 * Intermediary class between the Activity and the DB.
 * CRUD operations will be located here.
 * <p>
 * Created by Mikel on 11/02/17.
 */

public class DbAdapter {

    private SQLiteDatabase db;

    private SQLiteHelper dbHelper;

    private final Context context;

    /**
     * @param context
     */
    public DbAdapter(Context context) {
        this.context = context;
    }


    /**
     * SQLiteHelper opens the connection (creating the db if it doesn't exist).
     *
     * @return SQLiteDatabase object
     * @throws SQLException
     */
    public SQLiteDatabase open() throws SQLException {
        dbHelper = new SQLiteHelper(context);

        db = dbHelper.getWritableDatabase();

        Log.d("DEBUG", "DB received: " + db.toString());

        return db;
    }

    /**
     * Closes the connection to the db
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * Inserts a row given the currency
     *
     * @param currency
     * @return row ID
     */
    public long insertCurrency(Currency currency) {
        ContentValues row = new ContentValues();
        Log.d("DEBUG", "DbAdapter> Insert: " + currency.getName() + " | id: " + currency.getId());

        row.put("name", currency.getName());
        row.put("_id", currency.getId());
        row.put("abbreviation", currency.getAbbreviation());
        row.put("value", currency.getValue());
        row.put("id_backend", currency.getId_backend());

        return db.insert("currency", null, row);
    }

    /**
     * Removes the currency with the specified id
     *
     * @param id
     * @return # of modified rows
     */
    public int deleteCurrency(long id) {
        return db.delete("currency", "_id=?", new String[]{String.valueOf(id)});
    }

    /**
     * Gets all rows from currency
     *
     * @return Cursor
     */
    public Cursor getCurrencies() {
        return db.query("currency", new String[]{"_id", "name", "value", "abbreviation", "id_backend", "is_read"}, null, null, null, null, null);
    }

    /**
     * Gets the specified currency by its id
     *
     * @param id
     * @return Cursor
     * @throws SQLException
     */
    public Cursor getCurrency(long id) throws SQLException {
        Cursor row = db.query(true, "currency", new String[]{"_id", "name", "value", "abbreviation", "id_backend", "is_read"},
                "_id =?", new String[]{String.valueOf(id)}, null, null, null, null);

        if (row != null) {
            row.moveToFirst();
        }
        return row;
    }

    /**
     * Gets the last local row
     *
     * @return último id recibido del servidor ?????????????????????????????????????????????????????????????
     * @throws SQLException
     */
    public Cursor getLastLocalRow() throws SQLException {
        Cursor row = db.query(true, "currency", new String[]{"_id", "name", "abbreviation", "value", "id_backend", "is_read"},
                "id_backend = 0", null, null, null, null, null); // limit 1 ???

        if (row != null) {
            row.moveToFirst();
        }
        return row;
    }

    public int setCurrencyBackendReceived() throws SQLException {
        ContentValues row = new ContentValues();

        row.put("id_backend", -1);

        return db.update("currency", row, "id_backend=0", null);
    }

    /**
     * Gets the last row downloaded by the server
     *
     * @return last backend id
     * @throws SQLException
     */
    public Cursor getLastBackendRow() throws SQLException {
        Cursor row = db.query(true, "currency", new String[]{"_id", "name", "abbreviation", "value", "id_backend", "is_read"},
                null, null, null, null, "id_backend DESC", " 1");

        if (row != null) {
            row.moveToFirst();
        }

        return row;
    }

    /**
     * Updates the currency info given its id
     *
     * @param id
     * @param currency
     * @return int # of modified rows
     */
    public int updateCurrency(long id, Currency currency) {
        ContentValues row = new ContentValues();

        row.put("name", currency.getName());
        row.put("_id", id);
        row.put("abbreviation", currency.getAbbreviation());
        row.put("value", currency.getValue());
        row.put("id_backend", currency.getId_backend());
        row.put("is_read", currency.getIs_read());

        return db.update("currency", row, "_id=?", new String[]{String.valueOf(id)});
    }


}