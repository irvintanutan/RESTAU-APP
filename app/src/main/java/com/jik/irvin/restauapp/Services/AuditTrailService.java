package com.jik.irvin.restauapp.Services;

import android.content.Context;
import android.os.AsyncTask;

import com.jik.irvin.restauapp.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONObject;

public class AuditTrailService extends AsyncTask<String, String, String> {

    Context serviceContext;
    DatabaseHelper databaseHelper = null;

    public AuditTrailService(Context context) {
        this.serviceContext = context;
        this.databaseHelper = new DatabaseHelper(context);
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }




    @Override
    protected String doInBackground(String... params) {

        try {
            JSONArray mainArray = new JSONArray();
            JSONObject object = new JSONObject();
            object.put("username" , params[0]);
            object.put("log_type" , params[1]);
            object.put("details" , params[2]);

            mainArray.put(object);

            WebRequest.makePostRequest(databaseHelper.getBaseUrl() + "add-trans-logs/",
                    mainArray.toString(), serviceContext);


        } catch (Exception e) {
            e.printStackTrace();

        }

        return "";
    }


    @Override
    protected void onPostExecute(String strFromDoInBg) {


    }


}
