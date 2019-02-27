package com.jik.irvin.restauapp.Services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.jik.irvin.restauapp.Constants.ModGlobal;
import com.jik.irvin.restauapp.DatabaseHelper;
import com.jik.irvin.restauapp.Model.TableModel;

import org.json.JSONArray;
import org.json.JSONObject;

public class MyService extends Service {

    DatabaseHelper databaseHelper = new DatabaseHelper(this);

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        //your work
        Log.e("this is a process", "asdasdasd");
        new RefreshTable().execute("");
        return super.onStartCommand(intent, flags, startId);
    }


    class RefreshTable extends AsyncTask<String, String, String> {
        WebRequest wr = new WebRequest();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... params) {
            String json = "0";
            try {

                JSONArray tables = new JSONArray(wr.makeWebServiceCall(databaseHelper.getBaseUrl() + "showlist-tables-api", WebRequest.GET));


                Log.e("tables " , tables.toString());

                for (int i = 0; i < tables.length(); i++) {
                    JSONObject c = tables.getJSONObject(i);

                        ModGlobal.tableModelList.set(i, new TableModel(
                                c.getString("tbl_id"),
                                c.getString("name"),
                                c.getString("status")));

                        if (ModGlobal.tableModelList.get(i).getStatus().equals("Available") &&
                                ModGlobal.isTableSelected(Integer.parseInt(ModGlobal.tableModelList.get(i).getTableId()))){

                            ModGlobal.tableModelList.set(i, new TableModel(
                                    c.getString("tbl_id"),
                                    c.getString("name"),
                                    "Selected"));
                        } else  if (ModGlobal.tableModelList.get(i).getStatus().equals("Occupied") &&
                                ModGlobal.isTableSelected(Integer.parseInt(ModGlobal.tableModelList.get(i).getTableId()))){

                            ModGlobal.tableModelList.set(i, new TableModel(
                                    c.getString("tbl_id"),
                                    c.getString("name"),
                                    "OnGoing"));
                        }


                }

           /*     JSONObject jsonObject = new JSONObject(wr.makeWebServiceCall( ModGlobal.baseUrl + "movel/getAllTables", WebRequest.POST));

                JSONArray tables = jsonObject.getJSONArray("tables");
                for (int i = 0; i < tables.length(); i++) {
                    JSONObject c = tables.getJSONObject(i);


                    if (!ModGlobal.tableModelList.get(i).getStatus().equals("Selected")) {
                        ModGlobal.tableModelList.set(i, new TableModel(
                                c.getString("table_name"), Integer.parseInt(c.getString("table_status")),
                                Integer.parseInt(c.getString("table_id")), c.getString("groupID")));
                    }
                }
*/

            } catch (Exception e) {
                Log.e("json error", e.toString());
                stopForeground(true);
                stopSelf();
                e.printStackTrace();
            }

            return json;
        }


        @Override
        protected void onPostExecute(String strFromDoInBg) {
            super.onPostExecute("");
            // Log.e("This is a process" , "asdasdasdasda");
            try {
                ModGlobal.tableAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Log.e("error", e.toString());
            }


        }
    }
}
