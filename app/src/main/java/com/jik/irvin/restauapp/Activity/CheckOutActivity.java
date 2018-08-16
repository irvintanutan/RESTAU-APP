package com.jik.irvin.restauapp.Activity;

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
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jik.irvin.restauapp.Constants.ClickListener;
import com.jik.irvin.restauapp.DatabaseHelper;
import com.jik.irvin.restauapp.Adapter.ItemDetailsAdapter;
import com.jik.irvin.restauapp.Model.ItemDetailsModel;
import com.jik.irvin.restauapp.Model.MenuModel;
import com.jik.irvin.restauapp.Constants.ModGlobal;
import com.jik.irvin.restauapp.Model.TransactionModel;
import com.jik.irvin.restauapp.Model.UserModel;
import com.jik.irvin.restauapp.Services.AuditTrailService;
import com.jik.irvin.restauapp.Services.MyService;
import com.jik.irvin.restauapp.R;
import com.jik.irvin.restauapp.Constants.RecyclerTouchListener;
import com.jik.irvin.restauapp.Adapter.TableAdapter;
import com.jik.irvin.restauapp.Model.TableModel;
import com.jik.irvin.restauapp.Services.WebRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class CheckOutActivity extends AppCompatActivity {

    private CardView dineIn, cancel, takeOut, refund;
    private TextView totalPrice, transactionId;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewTable;
    private ItemDetailsAdapter itemDetailsAdapter;
    private TableAdapter tableAdapter;
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    boolean warning = false;
    private String orderType = "";
    private UserModel userModel = null;


    private boolean isExist = false;
    private double finalTotal = 0.00;
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
        refund = findViewById(R.id.refund);
        totalPrice = findViewById(R.id.totalPrice);
        transactionId = findViewById(R.id.transactionId);
        recyclerViewTable = findViewById(R.id.recycler_view_table_list);


        if (ModGlobal.transType.equals("REFUND")) {
            cancel.setVisibility(View.VISIBLE);
            refund.setVisibility(View.VISIBLE);
            dineIn.setVisibility(View.GONE);
            takeOut.setVisibility(View.GONE);
        } else {
            cancel.setVisibility(View.VISIBLE);
            refund.setVisibility(View.GONE);
            dineIn.setVisibility(View.VISIBLE);
            takeOut.setVisibility(View.VISIBLE);
        }

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
                        orderType = "DINE-IN";
                        if (ModGlobal.transType.equals("NORMAL")) {
                            new Sync(CheckOutActivity.this).execute("DINE-IN");
                        } else {
                            new VerifyTransactionDetails(CheckOutActivity.this).execute("");
                        }
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
                        ModGlobal.transactionId = "";
                        ModGlobal.transType = "NORMAL";
                        ModGlobal.isBillOutPrinted = 0;

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
                        orderType = "TAKE-OUT";
                        if (ModGlobal.transType.equals("NORMAL")) {
                            new Sync(CheckOutActivity.this).execute("TAKE-OUT");
                        } else {
                            new VerifyTransactionDetails(CheckOutActivity.this).execute("");
                        }

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


        refund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAuthenticationEntry();

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
            public void onClick(View view, final int position) {
                if (ModGlobal.isBillOutPrinted == 0) {
                    MenuModel menu = ModGlobal.menuModelList.get(ModGlobal.itemDetailsModelList.get(position).getPosition());
                    PopUpMenu(menu);
                } else {
                    LayoutInflater inflater = getLayoutInflater();
                    View alertLayout = inflater.inflate(R.layout.app_register, null);
                    final EditText password = alertLayout.findViewById(R.id.et_password);
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

                    AlertDialog.Builder alert = new AlertDialog.Builder(CheckOutActivity.this);
                    alert.setIcon(CheckOutActivity.this.getResources().getDrawable(R.drawable.ic_fingerprint_black_24dp));
                    alert.setTitle("Enter Master Pin");
                    // this is set the view from XML inside AlertDialog
                    alert.setView(alertLayout);
                    // disallow cancel of AlertDialog on click of back button and outside touch
                    alert.setCancelable(false);
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String pass = password.getText().toString();

                            if (pass.equals(ModGlobal.companyConfigModels.get(0).getPin())) {

                                MenuModel menu = ModGlobal.menuModelList.get(ModGlobal.itemDetailsModelList.get(position).getPosition());
                                PopUpMenu(menu);

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(CheckOutActivity.this);

                                builder.setTitle("WARNING");
                                builder.setMessage("Wrong Admin Password");


                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog alert = builder.create();
                                alert.show();
                            }

                        }
                    });
                    AlertDialog dialog = alert.create();
                    dialog.show();
                }
            }

            @Override
            public void onLongClick(View view, final int position) {
                ///do something here for long press
                if (ModGlobal.itemDetailsModelList.size() == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CheckOutActivity.this);

                    builder.setTitle("WARNING");
                    builder.setMessage("You are not allowed to remove single product in a transaction");


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

                            final ItemDetailsModel im = ModGlobal.itemDetailsModelList.get(position);

                            if (ModGlobal.isBillOutPrinted == 0) {
                                ModGlobal.itemDetailsModelList.remove(position);
                                itemDetailsAdapter.notifyDataSetChanged();
                                computeTotal();
                            } else {
                                LayoutInflater inflater = getLayoutInflater();
                                View alertLayout = inflater.inflate(R.layout.app_register, null);
                                final EditText password = alertLayout.findViewById(R.id.et_password);


                                AlertDialog.Builder alert = new AlertDialog.Builder(CheckOutActivity.this);
                                alert.setIcon(CheckOutActivity.this.getResources().getDrawable(R.drawable.ic_fingerprint_black_24dp));
                                alert.setTitle("Enter Master Pin");
                                // this is set the view from XML inside AlertDialog
                                alert.setView(alertLayout);
                                // disallow cancel of AlertDialog on click of back button and outside touch
                                alert.setCancelable(false);
                                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                                alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String pass = password.getText().toString();

                                        if (pass.equals(ModGlobal.companyConfigModels.get(0).getPin())) {

                                            ModGlobal.itemDetailsModelList.remove(position);
                                            itemDetailsAdapter.notifyDataSetChanged();
                                            computeTotal();

                                        } else {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(CheckOutActivity.this);

                                            builder.setTitle("WARNING");
                                            builder.setMessage("Wrong Admin Password");


                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });

                                            AlertDialog alert = builder.create();
                                            alert.show();
                                        }

                                    }
                                });
                                AlertDialog dialog1 = alert.create();
                                dialog1.show();
                            }

                            if (ModGlobal.transType.equals("UPDATE")) {

                                String product = "";

                                if (im.getCatID().equals("200")) {
                                    product = "Package: G" + (im.getProdID() - 1000);
                                } else {
                                    product = "Product: P" + im.getProdID();
                                }

                                new AuditTrailService(CheckOutActivity.this).execute(ModGlobal.userModel.getUsername(), "Void",
                                        "Item void S" + ModGlobal.transactionId + " by U" + ModGlobal.userModel.getUserId() + " - " +
                                                product);
                            }

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

                if (!ModGlobal.transType.equals("REFUND")) {

                    try {
                        TableModel table = ModGlobal.tableModelList.get(position);
                        if (table.getStatus().equals("Occupied") || table.getStatus().equals("Unavailable") ||
                                table.getStatus().equals("Reserved"))
                            Toast.makeText(getApplicationContext(), table.getName() + " is " + table.getStatus(), Toast.LENGTH_SHORT).show();
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


                        Log.e("asd", ModGlobal.tableId.toString());
                    } catch (Exception e) {
                        Log.e("asd", "something went wrong");
                    }
                }

            }

            @Override
            public void onLongClick(View view, final int position) {


            }
        }));


        computeTotal();

        if (ModGlobal.transType.equals("REFUND")) {

            transactionId.setText("REFUND receipt # : (" + ModGlobal.receiptNumber + ")");

        } else {

            if (ModGlobal.transactionId.isEmpty()) {
                transactionId.setText("transaction # : NEW");
            } else {
                transactionId.setText("transaction # : " + ModGlobal.transactionId);
            }
        }
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
            if (!ModGlobal.transType.equals("REFUND")) {

                startActivity(new Intent(CheckOutActivity.this, TableActivity.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!ModGlobal.transType.equals("REFUND")) {

            stopService(new Intent(CheckOutActivity.this, MyService.class));
            startActivity(new Intent(CheckOutActivity.this, TableActivity.class));
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        }
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
        price.setText("Php " + menuModel.getPrice());
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
                if (!ModGlobal.transType.equals("REFUND")) {
                    int qty = Integer.parseInt(quantity.getText().toString());
                    qty++;
                    quantity.setText(Integer.toString(qty));
                }
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
                            menuModel.getImg(), menuModel.getName(), menuModel.getCat_id(), menuModel.getPosition(), menuModel.getShortName()));

                } else {

                    ModGlobal.itemDetailsModelList.add(new ItemDetailsModel(menuModel.getProd_id(),
                            menuModel.getPrice(), Integer.parseInt(quantity.getText().toString()),
                            menuModel.getImg(), menuModel.getName(), menuModel.getCat_id(), menuModel.getPosition(), menuModel.getShortName()));

                }

                itemDetailsAdapter.notifyDataSetChanged();
                computeTotal();


            }


        });
        AlertDialog dialog = alert.create();

        dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = (int) this.getResources().getDimension(R.dimen.width);
        lp.height = (int) this.getResources().getDimension(R.dimen.height);

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

        finalTotal = total;
        totalPrice.setText("total price : Php " + dec.format(total));

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
                    detailsObject.put("order_type", orderType);
                    detailsObject.put("user_id", ModGlobal.userModel.getUserId());
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


                    if (orderType.equals("DINE-IN")) {

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
                ModGlobal.transactionId = "";
                ModGlobal.tableId.clear();
                ModGlobal.isBillOutPrinted = 0;
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


    class VerifyTransactionDetails extends AsyncTask<String, String, String> {

        Context serviceContext;
        WebRequest wr = new WebRequest();
        ProgressDialog progressDialog;

        public VerifyTransactionDetails(Context context) {
            this.serviceContext = context;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(serviceContext);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("PLEASE WAIT");
            progressDialog.setMessage("Verifying Transaction details");
            progressDialog.setCancelable(false);
            progressDialog.show();


        }


        @Override
        protected String doInBackground(String... params) {
            String json = "";
            try {

                JSONArray menuItems = new JSONArray(wr.makeWebServiceCall(databaseHelper.getBaseUrl() + "showlist-transactions-api", WebRequest.GET));

                Log.e("ad", menuItems.toString());


                for (int i = 0; i < menuItems.length(); i++) {
                    JSONObject c = menuItems.getJSONObject(i);

                    if (ModGlobal.transactionId.equals(c.getString("trans_id"))) {
                        json = "1";
                        break;
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return json;
        }


        @Override
        protected void onPostExecute(String strFromDoInBg) {
            progressDialog.dismiss();

            if (strFromDoInBg.equals("1")) {
                new SyncUpdate(CheckOutActivity.this).execute(orderType);
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(CheckOutActivity.this);

                builder.setTitle("WARNING");
                builder.setMessage("Transaction ID  " + ModGlobal.transactionId + " is already been cleared. This transaction will be cancelled");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        // Do nothing
                        ModGlobal.itemDetailsModelList.clear();
                        ModGlobal.tableId.clear();
                        ModGlobal.transactionId = "";
                        ModGlobal.transType = "NORMAL";

                        startActivity(new Intent(CheckOutActivity.this, TableActivity.class));
                        finish();

                    }

                });


                AlertDialog alert = builder.create();
                alert.setCancelable(false);
                alert.show();

            }
        }


    }


    class SyncUpdate extends AsyncTask<String, String, String> {

        Context serviceContext;
        WebRequest wr = new WebRequest();
        ProgressDialog progressDialog;

        public SyncUpdate(Context context) {
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

                JSONObject mainJsonObject = new JSONObject();
                JSONArray mainJsonArray = new JSONArray();

                JSONArray detailsArray = new JSONArray();
                JSONObject detailsObject = new JSONObject();
                detailsObject.put("order_type", params[0]);
                detailsObject.put("user_id", ModGlobal.userModel.getUserId());
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
                ModGlobal.transactionId = "";
                ModGlobal.tableId.clear();
                ModGlobal.isBillOutPrinted = 0;
                ModGlobal.transType = "NORMAL";
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

    class SyncRefund extends AsyncTask<String, String, String> {

        Context serviceContext;
        WebRequest wr = new WebRequest();
        ProgressDialog progressDialog;


        public SyncRefund(Context context) {
            this.serviceContext = context;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(serviceContext);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("PLEASE WAIT");
            progressDialog.setMessage("Processing Refund for " + ModGlobal.receiptNumber);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {
            String json = "";
            try {

                JSONObject mainJsonObject = new JSONObject();
                JSONArray mainJsonArray = new JSONArray();

                JSONArray detailsArray = new JSONArray();
                JSONObject detailsObject = new JSONObject();
                detailsObject.put("order_type", ModGlobal.orderType);
                detailsObject.put("user_id", ModGlobal.userModel.getUserId());
                detailsObject.put("cashier_id", params[0]);
                detailsObject.put("receipt_no", ModGlobal.receiptNumber);
                detailsObject.put("cash_amt", finalTotal);

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


                mainJsonArray.put(mainJsonObject);
                String load = mainJsonArray.toString();
                Log.e("asd", load);

                String response = WebRequest.makePostRequest(databaseHelper.getBaseUrl() + "add-transactions-refund-api/",
                        load, serviceContext);


                Log.e("response", response);
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
                ModGlobal.transactionId = "";
                ModGlobal.tableId.clear();
                ModGlobal.isBillOutPrinted = 0;
                ModGlobal.receiptNumber = "";
                ModGlobal.transType = "NORMAL";


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


    private void showAuthenticationEntry() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.user_refund_verification, null);

        final EditText password = alertLayout.findViewById(R.id.et_password);
        final EditText username = alertLayout.findViewById(R.id.et_username);


        AlertDialog.Builder alert = new AlertDialog.Builder(CheckOutActivity.this);
        alert.setIcon(CheckOutActivity.this.getResources().getDrawable(R.drawable.ic_fingerprint_black_24dp));
        alert.setTitle("User Authentication");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                new VerifyUser(CheckOutActivity.this).execute(username.getText().toString(), password.getText().toString());
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
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

                JSONArray users = new JSONArray(wr.makeWebServiceCall(databaseHelper.getBaseUrl() + "showlist-users-api", WebRequest.GET));


                for (int i = 0; i < users.length(); i++) {
                    JSONObject c = users.getJSONObject(i);

                    if (c.getString("username").equals(params[0]) && c.getString("password").equals(params[1])) {
                        json = "1";

                        userModel = new UserModel(c.getString("user_id"),
                                c.getString("user_type"), c.getString("username"),
                                c.getString("password"), c.getString("lastname"),
                                c.getString("firstname"), c.getString("middlename"));

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

            try {

                progressDialog.dismiss();
                if (strFromDoInBg.equals("1")) {


                    if (userModel.getUserType().equals("Administrator") ||
                            userModel.getUserType().equals("Cashier")) {

                        new SyncRefund(CheckOutActivity.this).execute(userModel.getUserId());

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(CheckOutActivity.this);

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

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CheckOutActivity.this);

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
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("refund", e.toString());
            }
        }
    }

}
