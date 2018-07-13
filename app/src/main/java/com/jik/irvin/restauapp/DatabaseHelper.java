package com.jik.irvin.restauapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jik.irvin.restauapp.Model.PosModel;

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

    ///table name
    private static final String tbl_pos = "tbl_pos";
    ///columns...
    private static final String pos_id = "pos_id";
    private static final String last_receipt_number = "last_receipt_number";





    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_BASE_URL_TABLE = "CREATE TABLE " + tbl_base + "("
                + base_url + " TEXT );";
        db.execSQL(CREATE_BASE_URL_TABLE);

        String CREATE_POS_TABLE = "CREATE TABLE " + tbl_pos + "( pos_id integer primary key , "
                + last_receipt_number + " TEXT );";
        db.execSQL(CREATE_POS_TABLE);


    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + tbl_base);
        onCreate(db);

    }


    /**
     * METHODS FOR SETTINGS TABLE
     */

    public void addPos(PosModel posModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();


        String selectQuery = "SELECT  * FROM " + tbl_pos;
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Inserting Row
        if (cursor.getCount() <= 0) {

            values.put(pos_id , posModel.getPos_id());
            values.put(last_receipt_number, posModel.getLast_receipt_number());
            db.insert(tbl_pos, null, values);
        }
        cursor.close();
        db.close(); // Closing database connection
    }



    public void updateLastReceiptNumber(String lrn) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

            values.put(last_receipt_number, lrn);
        db.update(tbl_pos, values, "pos_id=1", null);


        db.close(); // Closing database connection
    }




    public int getLastReceiptNumber(){
        int result = 0;
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + tbl_pos;
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();

        Log.e("asd" , Integer.toString(cursor.getCount()));

        result = cursor.getInt(1) + 1;

        cursor.close();
        db.close();

        return result;
    }

    public void addBaseUrl(String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(base_url, url);
        // Inserting Row
        db.insert(tbl_base, null, values);
        db.close(); // Closing database connection
    }


    public void updateBaseUrl(String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(base_url, url);
        // Inserting Row
        db.update(tbl_base, values, null, null);
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
