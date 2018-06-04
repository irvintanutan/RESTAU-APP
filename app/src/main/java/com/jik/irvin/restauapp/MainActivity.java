package com.jik.irvin.restauapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Button login;
    EditText username, password;
    DatabaseHelper databaseHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        ModGlobal.menuModelList.clear();
        ModGlobal.categoryModelList.clear();

        if (!databaseHelper.hasBaseUrl()) {
            databaseHelper.addBaseUrl(ModGlobal.baseURL);
        }

        login = findViewById(R.id.login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SignInRequest(MainActivity.this).execute("");

            }
        });
    }


    class SignInRequest extends AsyncTask<String, String, String> {
        WebRequest wr = new WebRequest();
        private Context context;
        ProgressDialog progressDialog;

        public SignInRequest(Context c) {
            this.context = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Please wait");
            progressDialog.setMessage("Downloading Credentials");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }


        @Override
        protected String doInBackground(String... params) {
            String json = "0";
            try {
                Log.e("url" , databaseHelper.getBaseUrl());
                JSONArray menuItems = new JSONArray(wr.makeWebServiceCall(databaseHelper.getBaseUrl() + "showlist-products-api", WebRequest.GET));


                for (int i = 0; i < menuItems.length(); i++) {
                    JSONObject c = menuItems.getJSONObject(i);
                    ModGlobal.menuModelList.add(new MenuModel(
                            c.getInt("prod_id"),
                            c.getString("name"),
                            c.getString("descr"),
                            c.getString("cat_id"),
                            c.getString("cat_name"),
                            c.getString("price"),
                            c.getString("sold"),
                            c.getString("encoded"),
                            c.getString("img"),
                            c.getInt("item_count"),
                            i

                    ));
                }

                int counter = ModGlobal.menuModelList.size();

                JSONArray packages = new JSONArray(wr.makeWebServiceCall(databaseHelper.getBaseUrl() + "showlist-packages-api", WebRequest.GET));

                for (int i = 0; i < packages.length(); i++ , counter++) {
                    JSONObject c = packages.getJSONObject(i);
                    ModGlobal.menuModelList.add(new MenuModel(
                            c.getInt("pack_id") + 1000,
                            c.getString("name"),
                            c.getString("descr"),
                            "200",
                            "PACKAGES",
                            c.getString("price"),
                            c.getString("sold"),
                            c.getString("encoded"),
                            c.getString("img"),
                            c.getInt("prod_count"),
                            counter

                    ));
                }


                JSONArray categories = new JSONArray(wr.makeWebServiceCall(databaseHelper.getBaseUrl() + "showlist-categories-api", WebRequest.GET));

                ModGlobal.categoryModelList.add(new CategoryModel("100" , "ALL" , " " , " "));
                ModGlobal.categoryModelList.add(new CategoryModel("200" , "PACKAGES" , " " , " "));
                for (int i = 0; i < categories.length(); i++) {
                    JSONObject c = categories.getJSONObject(i);
                    ModGlobal.categoryModelList.add(new CategoryModel(
                            c.getString("cat_id"),
                            c.getString("name"),
                            c.getString("descr"),
                            c.getString("encoded")
                    ));
                }




                json = "1";
            } catch (JSONException e) {
                progressDialog.dismiss();
                e.printStackTrace();
                Log.e("error on downloading", e.toString());
            }

            return json;
        }


        @Override
        protected void onPostExecute(String strFromDoInBg) {
            super.onPostExecute("");
            progressDialog.dismiss();
            if (strFromDoInBg.equals("1")) {
                startActivity(new Intent(MainActivity.this, TableActivity.class));
                finish();
            }

        }
    }
}
