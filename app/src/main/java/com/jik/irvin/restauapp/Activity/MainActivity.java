package com.jik.irvin.restauapp.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.jik.irvin.restauapp.Constants.ModGlobal;
import com.jik.irvin.restauapp.DatabaseHelper;
import com.jik.irvin.restauapp.Model.CategoryModel;
import com.jik.irvin.restauapp.Model.CompanyConfigModel;
import com.jik.irvin.restauapp.Model.MenuModel;
import com.jik.irvin.restauapp.Model.PackageDetailsModel;
import com.jik.irvin.restauapp.Model.PosModel;
import com.jik.irvin.restauapp.Model.TableModel;
import com.jik.irvin.restauapp.Model.UserModel;
import com.jik.irvin.restauapp.R;
import com.jik.irvin.restauapp.Services.WebRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    CardView login;
    EditText username, password;
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    long lastDown, lastDuration;
    boolean isPressed = false;

    @SuppressLint("ClickableViewAccessibility")
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


        username.setText("xanderford");
        password.setText("xanderford");
    /*    username.setText("janedoe");
        password.setText("janedoe");*/

        /*login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                new SignInRequest(MainActivity.this).execute("");

            }
        });*/


        login.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    lastDown = System.currentTimeMillis();
                    isPressed = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    lastDuration = System.currentTimeMillis() - lastDown;
                    isPressed = false;
                }

                if (((lastDuration / 1000) > 3) && !isPressed) {


                    LayoutInflater inflater = getLayoutInflater();
                    View alertLayout = inflater.inflate(R.layout.app_register, null);
                    final EditText password = alertLayout.findViewById(R.id.et_password);


                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setIcon(MainActivity.this.getResources().getDrawable(R.drawable.ic_fingerprint_black_24dp));
                    alert.setTitle("Enter Password");
                    // this is set the view from XML inside AlertDialog
                    alert.setView(alertLayout);
                    // disallow cancel of AlertDialog on click of back button and outside touch
                    alert.setCancelable(false);
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    alert.setPositiveButton("Register", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String pass = password.getText().toString();

                            if (pass.equals("")) {
                                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(intent);
                                finish();
                            } else
                                dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = alert.create();
                    dialog.show();
                } else {
                    if (!isPressed)
                        new VerifyUser(MainActivity.this).execute(username.getText().toString() , password.getText().toString());
                }

                return true;
            }
        });

        String androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        databaseHelper.addPos(new PosModel("2", "2000000", androidId));


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
                Log.e("url", databaseHelper.getBaseUrl());

                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", "IRVIN");
                jsonArray.put(jsonObject);
                ModGlobal.companyConfigModels.clear();


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
                            i,
                            c.getString("less_price"),
                            c.getBoolean("is_discounted"),
                            c.getString("short_name"),
                            c.getBoolean("is_best_selling"),
                            c.getString("rank")));
                }

                int counter = ModGlobal.menuModelList.size();

                JSONArray packages = new JSONArray(wr.makeWebServiceCall(databaseHelper.getBaseUrl() + "showlist-packages-api", WebRequest.GET));

                for (int i = 0; i < packages.length(); i++, counter++) {
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
                            counter,
                            c.getString("less_price"),
                            c.getBoolean("is_discounted"),
                            c.getString("short_name"),
                            c.getBoolean("is_best_selling"),
                            c.getString("rank")));

                    JSONArray arr = c.getJSONArray("package_products");
                    for (int x = 0; x < arr.length(); x++) {
                        JSONObject b = arr.getJSONObject(x);

                        ModGlobal.packageDetailsModelList.add(new PackageDetailsModel(b.getString("pack_prod_id"),
                                b.getString("pack_prod_name"), b.getString("pack_prod_short_name"),
                                b.getString("pack_prod_qty"), Integer.toString(c.getInt("pack_id") + 1000)));
                    }
                }


                JSONArray categories = new JSONArray(wr.makeWebServiceCall(databaseHelper.getBaseUrl() + "showlist-categories-api", WebRequest.GET));

                ModGlobal.categoryModelList.add(new CategoryModel("100", "ALL", " ", " "));
                ModGlobal.categoryModelList.add(new CategoryModel("200", "PACKAGES", " ", " "));
                for (int i = 0; i < categories.length(); i++) {
                    JSONObject c = categories.getJSONObject(i);
                    ModGlobal.categoryModelList.add(new CategoryModel(
                            c.getString("cat_id"),
                            c.getString("name"),
                            c.getString("descr"),
                            c.getString("encoded")
                    ));
                }


                JSONArray tables = new JSONArray(wr.makeWebServiceCall(databaseHelper.getBaseUrl() + "showlist-tables-api", WebRequest.GET));


                for (int i = 0; i < tables.length(); i++) {
                    JSONObject c = tables.getJSONObject(i);
                    ModGlobal.tableModelList.add(new TableModel(
                            c.getString("tbl_id"),
                            c.getString("name"),
                            c.getString("status")
                    ));
                }


                JSONArray companyConfig = new JSONArray(wr.makeWebServiceCall(databaseHelper.getBaseUrl() + "showlist-store-config-api", WebRequest.GET));
                for (int i = 0; i < companyConfig.length(); i++) {
                    JSONObject c = companyConfig.getJSONObject(i);
                    ModGlobal.companyConfigModels.add(new CompanyConfigModel(
                            c.getString("name"),
                            c.getString("address"),
                            c.getString("city"),
                            c.getString("tin"),
                            c.getString("vat"),
                            c.getString("bs_price"),
                            c.getString("img"),
                            c.getString("password")));
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
                if (ModGlobal.userModelList.get(0).getUserType().equals("Staff")) {
                    startActivity(new Intent(MainActivity.this, TableActivity.class));
                    finish();
                } else if (ModGlobal.userModelList.get(0).getUserType().equals("Cashier")) {
                    startActivity(new Intent(MainActivity.this, CashierActivity.class));
                    finish();
                }
            }
        }
    }


    class VerifyUser extends AsyncTask<String, String, String> {
        WebRequest wr = new WebRequest();
        private Context context;
        ProgressDialog progressDialog;

        public VerifyUser(Context c) {
            this.context = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Please wait");
            progressDialog.setMessage("Verifying User Credentials");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }


        @Override
        protected String doInBackground(String... params) {
            String json = "0";
            try {
                Log.e("url", databaseHelper.getBaseUrl());

                ModGlobal.userModelList.clear();
                ModGlobal.userModel = null;
                JSONArray users = new JSONArray(wr.makeWebServiceCall(databaseHelper.getBaseUrl() + "showlist-users-api", WebRequest.GET));


                for (int i = 0; i < users.length(); i++) {
                    JSONObject c = users.getJSONObject(i);

                    if (c.getString("username").equals(params[0]) && c.getString("password").equals(params[1])) {
                        json = "1";

                        ModGlobal.userModelList.add(new UserModel(c.getString("user_id"),
                                c.getString("user_type"), c.getString("username"),
                                c.getString("password"), c.getString("lastname"),
                                c.getString("firstname"), c.getString("middlename")));

                        break;
                    }

                }


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
                ModGlobal.userModel = ModGlobal.userModelList.get(0);
                new SignInRequest(MainActivity.this).execute("");

            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("Warning");
                builder.setMessage("Unauthorized User");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        // Do nothing
                        dialog.dismiss();

                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }


    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to quit ?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                // Do nothing
                dialog.dismiss();
                finish();
                System.exit(0);

            }

        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

}
