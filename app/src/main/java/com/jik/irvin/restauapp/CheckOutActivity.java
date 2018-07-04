package com.jik.irvin.restauapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class CheckOutActivity extends AppCompatActivity {

    private CardView dineIn, cancel, takeOut;
    private TextView totalPrice;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewTable;
    private ItemDetailsAdapter itemDetailsAdapter;
    private TableAdapter tableAdapter;
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    boolean warning = false;


    private boolean isExist = false;
    private int itemDetailsIndex = 0, itemDetailsQty = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_check_out);

        dineIn = findViewById(R.id.button);
        cancel = findViewById(R.id.cancel);
        takeOut = findViewById(R.id.takeOut);
        totalPrice = findViewById(R.id.totalPrice);
        recyclerViewTable = findViewById(R.id.recycler_view_table_list);

        Toolbar tb = findViewById(R.id.app_bar2);
        setSupportActionBar(tb);
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        //ab.setLogo(R.drawable.ic_timeline_white_24dp);
        ab.setTitle("BACK");
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(true); // disable the default title element here (for centered title)


        dineIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CheckOutActivity.this);

                builder.setTitle("DINE IN");
                builder.setMessage("Are you sure you want to place the order ?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        // Do nothing
                        new Sync(CheckOutActivity.this).execute("DINE-IN");
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
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CheckOutActivity.this);

                builder.setTitle("CANCEL");
                builder.setMessage("Are you sure you want to cancel this transaction ?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        // Do nothing
                        ModGlobal.itemDetailsModelList.clear();
                        ModGlobal.tableId.clear();
                        ModGlobal.transType = "NORMAL";

                        startActivity(new Intent(CheckOutActivity.this, TableActivity.class));
                        finish();

                    }

                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.setCancelable(false);
                alert.show();
            }
        });


        takeOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CheckOutActivity.this);

                builder.setTitle("TAKE OUT");
                builder.setMessage("Are you sure you want to place the order ?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        // Do nothing
                        ModGlobal.tableId.clear();
                        new Sync(CheckOutActivity.this).execute("TAKE-OUT");

                    }

                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.setCancelable(false);
                alert.show();
            }
        });


        recyclerView = findViewById(R.id.recycler_view_table);
        itemDetailsAdapter = new ItemDetailsAdapter(this, ModGlobal.itemDetailsModelList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemDetailsAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                MenuModel menu = ModGlobal.menuModelList.get(ModGlobal.itemDetailsModelList.get(position).getPosition());
                PopUpMenu(menu);
            }

            @Override
            public void onLongClick(View view, final int position) {
                ///do something here for long press
                if (ModGlobal.itemDetailsModelList.size() == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CheckOutActivity.this);

                    builder.setTitle("WARNING");
                    builder.setMessage("You are not allowed to remove single product in a transaction" + ModGlobal.itemDetailsModelList.get(position).getMenuName() + "?");


                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CheckOutActivity.this);

                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure you want to remove" + ModGlobal.itemDetailsModelList.get(position).getMenuName() + "?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
                            // Do nothing
                            ModGlobal.itemDetailsModelList.remove(position);
                            itemDetailsAdapter.notifyDataSetChanged();
                            computeTotal();

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
        }));


        RecyclerView.LayoutManager mLayoutManager1 = new GridLayoutManager(this, 2);
        recyclerViewTable.setLayoutManager(mLayoutManager1);
        recyclerViewTable.setItemAnimator(new DefaultItemAnimator());
        ModGlobal.tableAdapter = new TableAdapter(this, ModGlobal.tableModelList);
        recyclerViewTable.setAdapter(ModGlobal.tableAdapter);

        recyclerViewTable.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    TableModel table = ModGlobal.tableModelList.get(position);
                    if (table.getStatus().equals("Occupied"))
                        Toast.makeText(getApplicationContext(), table.getName() + " is not available", Toast.LENGTH_SHORT).show();
                    else {
                        if (table.getStatus().equals("Available")) {
                            table.setStatus("Selected");
                            addTable(Integer.parseInt(table.getTableId()));
                            ModGlobal.tableAdapter.notifyDataSetChanged();
                        } else {
                            table.setStatus("Available");
                            removeTable(Integer.parseInt(table.getTableId()));
                            ModGlobal.tableAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    Log.e("asd", "something went wrong");
                }

            }

            @Override
            public void onLongClick(View view, final int position) {


            }
        }));


        computeTotal();


        startService(new Intent(this, MyService.class));
    }


    void addTable(int id) {
        ModGlobal.tableId.add(id);
    }

    void removeTable(int id) {
        for (int a = 0; a < ModGlobal.tableId.size(); a++) {
            if (ModGlobal.tableId.get(a) == id)
                ModGlobal.tableId.remove(a);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(CheckOutActivity.this, TableActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        stopService(new Intent(CheckOutActivity.this, MyService.class));
        startActivity(new Intent(CheckOutActivity.this, TableActivity.class));
        finish();

    }


    public void PopUpMenu(final MenuModel menuModel) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.menu_details, null);
        final ImageView imageView = alertLayout.findViewById(R.id.thumbnail);
        final TextView title = alertLayout.findViewById(R.id.title);
        final TextView price = alertLayout.findViewById(R.id.price);
        final TextView description = alertLayout.findViewById(R.id.description);
        final ImageButton minus = alertLayout.findViewById(R.id.minus);
        final ImageButton plus = alertLayout.findViewById(R.id.plus);
        final TextView quantity = alertLayout.findViewById(R.id.quantity);


        if (!menuModel.getCat_id().equals("200")) {
            Glide.with(CheckOutActivity.this).load(ModGlobal.baseURL + "uploads/products/" + menuModel.getImg()).into(imageView);
        } else
            Glide.with(CheckOutActivity.this).load(ModGlobal.baseURL + "uploads/packages/" + menuModel.getImg()).into(imageView);

        title.setText(menuModel.getName());
        price.setText(menuModel.getPrice());
        description.setText(menuModel.getDescr());


        isExist = false;
        for (int a = 0; a < ModGlobal.itemDetailsModelList.size(); a++) {
            if (ModGlobal.itemDetailsModelList.get(a).getProdID() == menuModel.getProd_id()) {
                itemDetailsIndex = a;
                itemDetailsQty = ModGlobal.itemDetailsModelList.get(a).getMenuQty();
                isExist = true;
            }
        }


        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int qty = Integer.parseInt(quantity.getText().toString());

                if (qty > 1) {
                    qty--;
                    quantity.setText(Integer.toString(qty));
                }
            }
        });

        quantity.setText(Integer.toString(itemDetailsQty));

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(quantity.getText().toString());
                qty++;
                quantity.setText(Integer.toString(qty));
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Item Details");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (isExist) {

                    ModGlobal.itemDetailsModelList.set(itemDetailsIndex, new ItemDetailsModel(menuModel.getProd_id(),
                            menuModel.getPrice(), Integer.parseInt(quantity.getText().toString()),
                            menuModel.getImg(), menuModel.getName(), menuModel.getCat_id(), menuModel.getPosition()));

                } else {

                    ModGlobal.itemDetailsModelList.add(new ItemDetailsModel(menuModel.getProd_id(),
                            menuModel.getPrice(), Integer.parseInt(quantity.getText().toString()),
                            menuModel.getImg(), menuModel.getName(), menuModel.getCat_id(), menuModel.getPosition()));

                }

                itemDetailsAdapter.notifyDataSetChanged();
                computeTotal();


            }


        });
        AlertDialog dialog = alert.create();

        dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = ModGlobal.width;
        lp.height = ModGlobal.height;

        dialog.getWindow().setAttributes(lp);
        //dialog.getWindow().setLayout(800, 400);

    }


    void computeTotal() {

        DecimalFormat dec = new DecimalFormat("#,##0.00");
        Double total = 0.00;

        List<ItemDetailsModel> itemDetailsModels = ModGlobal.itemDetailsModelList;

        for (ItemDetailsModel itemDetailsModel : itemDetailsModels) {

            total += Double.parseDouble(itemDetailsModel.getMenuPrice()) *
                    itemDetailsModel.getMenuQty();

        }


        totalPrice.setText("Total price :    Php " + dec.format(total) + "   ");

    }


    class Sync extends AsyncTask<String, String, String> {

        Context serviceContext;
        WebRequest wr = new WebRequest();
        ProgressDialog progressDialog;

        public Sync(Context context) {
            this.serviceContext = context;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(serviceContext);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("PLEASE WAIT");
            progressDialog.setMessage("Placing Order");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {
            String json = "";
            try {
                if (ModGlobal.transType.equals("NORMAL")) {
                    JSONObject mainJsonObject = new JSONObject();
                    JSONArray mainJsonArray = new JSONArray();

                    JSONArray detailsArray = new JSONArray();
                    JSONObject detailsObject = new JSONObject();
                    detailsObject.put("order_type", params[0]);
                    detailsObject.put("user_id", 103);
                    detailsArray.put(detailsObject);


                    mainJsonObject.put("details", detailsArray);


                    JSONArray packageArray = new JSONArray();
                    JSONArray productsArray = new JSONArray();

                    List<ItemDetailsModel> itemDetailsModels = ModGlobal.itemDetailsModelList;


                    for (ItemDetailsModel itemDetailsModel : itemDetailsModels) {
                        JSONObject packageObject = new JSONObject();
                        JSONObject productsObject = new JSONObject();
                        if (itemDetailsModel.getCatID().equals("200")) {

                            packageObject.put("pack_id", itemDetailsModel.getProdID() - 1000);
                            packageObject.put("qty", itemDetailsModel.getMenuQty());
                            packageArray.put(packageObject);

                        } else {

                            productsObject.put("prod_id", itemDetailsModel.getProdID());
                            productsObject.put("qty", itemDetailsModel.getMenuQty());
                            productsArray.put(productsObject);

                        }

                    }

                    mainJsonObject.put("products", productsArray);
                    mainJsonObject.put("packages", packageArray);

                    JSONArray tableArray = new JSONArray();


                    if (params[0].equals("DINE-IN")) {

                        if (ModGlobal.tableId.isEmpty()) {
                            JSONObject tableObject = new JSONObject();
                            tableObject.put("tbl_id", 0);
                            tableArray.put(tableObject);
                        } else {
                            removeTable(0);
                            for (int a = 0; a < ModGlobal.tableId.size(); a++) {
                                JSONObject tableObject = new JSONObject();
                                tableObject.put("tbl_id", ModGlobal.tableId.get(a));
                                tableArray.put(tableObject);
                            }
                        }

                        mainJsonObject.put("tables", tableArray);
                    }


                    mainJsonArray.put(mainJsonObject);
                    String load = mainJsonArray.toString();
                    Log.e("asd", load);

                    String response = WebRequest.makePostRequest(databaseHelper.getBaseUrl() + "add-transactions-api",
                            load, serviceContext);


                    Log.e("response", response);
                } else {
                    JSONObject mainJsonObject = new JSONObject();
                    JSONArray mainJsonArray = new JSONArray();

                    JSONArray detailsArray = new JSONArray();
                    JSONObject detailsObject = new JSONObject();
                    detailsObject.put("order_type", params[0]);
                    detailsObject.put("status", "ONGOING");
                    detailsArray.put(detailsObject);


                    mainJsonObject.put("details", detailsArray);


                    JSONArray packageArray = new JSONArray();
                    JSONArray productsArray = new JSONArray();

                    List<ItemDetailsModel> itemDetailsModels = ModGlobal.itemDetailsModelList;


                    for (ItemDetailsModel itemDetailsModel : itemDetailsModels) {
                        JSONObject packageObject = new JSONObject();
                        JSONObject productsObject = new JSONObject();
                        if (itemDetailsModel.getCatID().equals("200")) {

                            packageObject.put("pack_id", itemDetailsModel.getProdID() - 1000);
                            packageObject.put("qty", itemDetailsModel.getMenuQty());
                            packageArray.put(packageObject);

                        } else {

                            productsObject.put("prod_id", itemDetailsModel.getProdID());
                            productsObject.put("qty", itemDetailsModel.getMenuQty());
                            productsArray.put(productsObject);

                        }

                    }

                    mainJsonObject.put("products", productsArray);
                    mainJsonObject.put("packages", packageArray);

                    JSONArray tableArray = new JSONArray();

                    if (params[0].equals("DINE-IN")) {

                        if (ModGlobal.tableId.isEmpty()) {
                            JSONObject tableObject = new JSONObject();
                            tableObject.put("tbl_id", 0);
                            tableArray.put(tableObject);
                        } else {
                            removeTable(0);
                            for (int a = 0; a < ModGlobal.tableId.size(); a++) {
                                JSONObject tableObject = new JSONObject();
                                tableObject.put("tbl_id", ModGlobal.tableId.get(a));
                                tableArray.put(tableObject);
                            }
                        }

                        mainJsonObject.put("tables", tableArray);
                    }
                    mainJsonArray.put(mainJsonObject);
                    String load = mainJsonArray.toString();
                    Log.e("asd", load);

                    String response = WebRequest.makePostRequest(databaseHelper.getBaseUrl() + "reset-transactions-api/" + ModGlobal.transactionId,
                            load, serviceContext);


                    Log.e("response", response);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                warning = true;
                Log.e("asd", e.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return json;
        }


        @Override
        protected void onPostExecute(String strFromDoInBg) {
            progressDialog.dismiss();


            if (!warning) {
                ModGlobal.itemDetailsModelList.clear();
                ModGlobal.tableId.clear();
                //ModGlobal.clear();
                updateTable("CHECKOUT");
                AlertDialog.Builder builder = new AlertDialog.Builder(serviceContext);

                builder.setTitle("Information");
                builder.setMessage("Transaction Successful!");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        // Do nothing

                        startActivity(new Intent(CheckOutActivity.this, TableActivity.class));
                        finish();
                        dialog.dismiss();

                    }

                });

                AlertDialog alert = builder.create();
                alert.setCancelable(false);
                alert.show();

            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(serviceContext);

                builder.setTitle("WARNING");
                builder.setMessage("Can't access server");

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

    void updateTable(String process) {
        switch (process) {
            case "CANCEL": {

                for (int a = 0; a < ModGlobal.tableModelList.size(); a++) {
                    TableModel table = ModGlobal.tableModelList.get(a);
                    if (table.getStatus().equals("Selected")) {
                        ModGlobal.tableModelList.set(a, new TableModel(
                                table.getTableId(), table.getName(), "Available"));
                    }
                }
            }
            case "CHECKOUT": {
                for (int a = 0; a < ModGlobal.tableModelList.size(); a++) {
                    TableModel table = ModGlobal.tableModelList.get(a);
                    if (table.getStatus().equals("Selected")) {
                        ModGlobal.tableModelList.set(a, new TableModel(
                                table.getTableId(), table.getName(), "Occupied"));
                    }
                }
            }

        }

    }
}
