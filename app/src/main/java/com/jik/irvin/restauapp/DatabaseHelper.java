package com.jik.irvin.restauapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by irvin on 7/29/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    //database version
    private static final int DATABASE_VERSION = 1;
    ///database name
    private static final String DATABASE_NAME = "restauapp";




    ///table name
    private static final String tbl_base = "tbl_base";
    ///columns...
    private static final String base_url = "base_url";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_BASE_URL_TABLE = "CREATE TABLE " + tbl_base + "("
                + base_url + " TEXT );";
        db.execSQL(CREATE_BASE_URL_TABLE);


    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + tbl_base);
        onCreate(db);

    }


    /**
     * METHODS FOR SETTINGS TABLE
     */

    public void addBaseUrl(String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(base_url, url);
        // Inserting Row
        db.insert(tbl_base, null, values);
        db.close(); // Closing database connection
    }

    public boolean hasBaseUrl(){
        boolean ind = false;
        String selectQuery = "SELECT  * FROM " + tbl_base;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0)
            ind = true;

        return ind;
    }

    public String getBaseUrl() {
        String selectQuery = "SELECT  * FROM " + tbl_base;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        return cursor.getString(0);
    }


}
