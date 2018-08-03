package com.jik.irvin.restauapp.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import com.bumptech.glide.Glide;
import com.jik.irvin.restauapp.Adapter.CategoryAdapter;
import com.jik.irvin.restauapp.Model.CategoryModel;
import com.jik.irvin.restauapp.Constants.ClickListener;
import com.jik.irvin.restauapp.DatabaseHelper;
import com.jik.irvin.restauapp.Model.ItemDetailsModel;
import com.jik.irvin.restauapp.Adapter.MenuAdapter;
import com.jik.irvin.restauapp.Model.MenuModel;
import com.jik.irvin.restauapp.Constants.ModGlobal;
import com.jik.irvin.restauapp.R;
import com.jik.irvin.restauapp.Constants.RecyclerTouchListener;
import com.jik.irvin.restauapp.Adapter.TransactionDataAdapter;
import com.jik.irvin.restauapp.Model.TransactionModel;
import com.jik.irvin.restauapp.Services.WebRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TableActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper = new DatabaseHelper(this);
    private RecyclerView recyclerViewMenu, recyclerViewCategory;
    private MenuAdapter menuAdapter;
    private CategoryAdapter categoryAdapter;
    private TransactionDataAdapter transactionDataAdapter;
    private boolean isExist = false;
    private int itemDetailsIndex = 0, itemDetailsQty = 1;
    private CardView proceed, transactionList, refundTransaction;
    private TextView cartItems;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_table);

        Toolbar tb = findViewById(R.id.app_bar);
        setSupportActionBar(tb);
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        //ab.setLogo(R.drawable.ic_timeline_white_24dp);

        ab.setTitle(ModGlobal.userModel.getFirstName() + " " + ModGlobal.userModel.getMiddleName() + " " +
                ModGlobal.userModel.getLastName() + " (WAITER)");
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(true); // disable the default title element here (for centered title)

        refundTransaction = findViewById(R.id.view_refund);
        proceed = findViewById(R.id.button_proceed);
        transactionList = findViewById(R.id.view_transaction);
        logo = findViewById(R.id.logo);
        cartItems = findViewById(R.id.cartItems);

        recyclerViewCategory = findViewById(R.id.recycler_view_table);
        recyclerViewMenu = findViewById(R.id.recycler_view_menu);


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerViewCategory.setLayoutManager(mLayoutManager);
        recyclerViewCategory.setItemAnimator(new DefaultItemAnimator());
        categoryAdapter = new CategoryAdapter(this, ModGlobal.categoryModelList);
        recyclerViewCategory.setAdapter(categoryAdapter);


        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ModGlobal.itemDetailsModelList.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TableActivity.this);

                    builder.setTitle("Warning");
                    builder.setMessage("Order/s List is EMPTY");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
                            // Do nothing
                        }

                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    startActivity(new Intent(TableActivity.this, CheckOutActivity.class));
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });


        refundTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ModGlobal.itemDetailsModelList.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TableActivity.this);

                    builder.setTitle("Warning");
                    builder.setMessage("You cannot view previous transactions if there is a pending transaction currently. " +
                            "Please cancel or complete the transaction first before doing this operation");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
                            // Do nothing
                            startActivity(new Intent(TableActivity.this, CheckOutActivity.class));
                            finish();
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }

                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                } else
                    showReceiptEntry();
            }
        });

        transactionList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (!ModGlobal.itemDetailsModelList.isEmpty() || ModGlobal.transType.equals("REFUND")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(TableActivity.this);

                        builder.setTitle("Warning");
                        builder.setMessage("You cannot view previous transactions if there is a pending transaction currently. " +
                                "Please cancel or complete the transaction first before doing this operation");

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing but close the dialog
                                // Do nothing
                                startActivity(new Intent(TableActivity.this, CheckOutActivity.class));
                                finish();
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }

                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                    } else
                        new SyncTransaction(TableActivity.this).execute("");

            }
        });

        //menuAdapter = new MenuAdapter(this, ModGlobal.menuModelList);


        recyclerViewCategory.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerViewCategory, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                CategoryModel categoryModel = ModGlobal.categoryModelList.get(position);

                filter(categoryModel.getCat_id());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        RecyclerView.LayoutManager mLayoutManager2 = new GridLayoutManager(this, 4);
        recyclerViewMenu.setLayoutManager(mLayoutManager2);
        //recyclerViewMenu.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerViewMenu.setItemAnimator(new DefaultItemAnimator());
        menuAdapter = new MenuAdapter(this, ModGlobal.menuModelList);
        recyclerViewMenu.setAdapter(menuAdapter);


        recyclerViewMenu.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerViewMenu, new ClickListener() {
            @Override
            public void onClick(View view, int position) {


                MenuModel menuModel = ModGlobal.menuModelListCopy.get(position);

                PopUpMenu(menuModel);


            }

            @Override
            public void onLongClick(View view, int position) {


            }
        }));


        Glide.with(TableActivity.this).load(ModGlobal.baseURL + "assets/img/" + ModGlobal.companyConfigModels.get(0).getImg_comp()).into(logo);


        filter("200");
        countItems();
    }


    void countItems() {
        cartItems.setText("VIEW ORDERS LIST (" + ModGlobal.itemDetailsModelList.size() + ")");
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
            Glide.with(TableActivity.this).load(ModGlobal.baseURL + "uploads/products/" + menuModel.getImg()).into(imageView);
        } else
            Glide.with(TableActivity.this).load(ModGlobal.baseURL + "uploads/packages/" + menuModel.getImg()).into(imageView);

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

        alert.setPositiveButton("Add to Cart", new DialogInterface.OnClickListener() {

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

                countItems();


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


    void filter(String text) {
        List<MenuModel> temp = new ArrayList();

        if (text.equals("100"))
            temp = ModGlobal.menuModelList;
        else {
            for (MenuModel d : ModGlobal.menuModelList) {
                //or use .contains(text)
                if (d.getCat_id().equals(text)) {
                    temp.add(d);
                }
            }
        }
        //update recyclerview
        menuAdapter.updateList(temp);
        ModGlobal.menuModelListCopy = temp;
    }


    @Override
    public void onBackPressed() {

        if (!ModGlobal.itemDetailsModelList.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(TableActivity.this);

            builder.setTitle("Warning");
            builder.setMessage("You cannot view previous transactions if there is a pending transaction currently. " +
                    "Please cancel or complete the transaction first before doing this operation");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog
                    // Do nothing
                    startActivity(new Intent(TableActivity.this, CheckOutActivity.class));
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }

            });

            AlertDialog alert = builder.create();
            alert.show();
        } else {


            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Confirm");
            builder.setMessage("Are you sure you want to quit ?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog
                    // Do nothing
                    startActivity(new Intent(TableActivity.this, MainActivity.class));
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Confirm");
            builder.setMessage("Are you sure you want to quit ?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog
                    // Do nothing
                    startActivity(new Intent(TableActivity.this, MainActivity.class));
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

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

        return super.onOptionsItemSelected(item);
    }


    class SyncTransaction extends AsyncTask<String, String, String> {

        Context serviceContext;
        WebRequest wr = new WebRequest();
        ProgressDialog progressDialog;

        public SyncTransaction(Context context) {
            this.serviceContext = context;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(serviceContext);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("PLEASE WAIT");
            progressDialog.setMessage("Fetching Transaction/s");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {
            String json = "";
            try {

                ModGlobal.transactionModelList.clear();
                JSONArray menuItems = new JSONArray(wr.makeWebServiceCall(databaseHelper.getBaseUrl() + "showlist-transactions-api", WebRequest.GET));

                Log.e("ad", menuItems.toString());


                for (int i = 0; i < menuItems.length(); i++) {
                    JSONObject c = menuItems.getJSONObject(i);
                    ModGlobal.transactionModelList.add(new TransactionModel(
                            c.getString("trans_id"),
                            c.getString("datetime"),
                            c.getString("table_str"),
                            c.getString("gross_total")));
                }

                Log.e("ad", ModGlobal.transactionModelList.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return json;
        }


        @Override
        protected void onPostExecute(String strFromDoInBg) {
            progressDialog.dismiss();

            viewTransaction();

        }


    }

    public void viewTransaction() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.view_transaction, null);

        final RecyclerView recyclerViewTransaction = alertLayout.findViewById(R.id.recycler_view_transaction_list);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerViewTransaction.setLayoutManager(mLayoutManager);
        recyclerViewTransaction.setItemAnimator(new DefaultItemAnimator());
        transactionDataAdapter = new TransactionDataAdapter(ModGlobal.transactionModelList);
        recyclerViewTransaction.setAdapter(transactionDataAdapter);


        recyclerViewTransaction.addOnItemTouchListener(new RecyclerTouchListener(TableActivity.this, recyclerViewTransaction, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Log.e("asd", ModGlobal.transactionModelList.toArray().toString());

                TransactionModel transactionModel = ModGlobal.transactionModelList.get(position);
                ModGlobal.transactionId = transactionModel.getTransId();
                new SyncTransactionDetails(TableActivity.this).execute(transactionModel.getTransId());
            }

            @Override
            public void onLongClick(View view, int position) {


            }
        }));

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Transaction List");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();


            }
        });


        AlertDialog dialog = alert.create();

        dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = (int) this.getResources().getDimension(R.dimen.width_transaction);
        lp.height = (int) this.getResources().getDimension(R.dimen.height);

        dialog.getWindow().setAttributes(lp);

    }


    class SyncTransactionDetails extends AsyncTask<String, String, String> {

        Context serviceContext;
        WebRequest wr = new WebRequest();
        ProgressDialog progressDialog;

        public SyncTransactionDetails(Context context) {
            this.serviceContext = context;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(serviceContext);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("PLEASE WAIT");
            progressDialog.setMessage("Fetching Transaction details");
            progressDialog.setCancelable(false);
            progressDialog.show();


        }


        @Override
        protected String doInBackground(String... params) {
            String json = "";
            try {


                JSONObject menuItems = new JSONObject(wr.makeWebServiceCall(databaseHelper.getBaseUrl() + "showlist-trans-details-api/" + params[0], WebRequest.GET));

                Log.e("transactiondetails", menuItems.toString());

                ModGlobal.itemDetailsModelList.clear();
                ModGlobal.tableId.clear();
                ModGlobal.isBillOutPrinted = 0;

                JSONObject isBillOutPrinted = menuItems.getJSONObject("details");
                ModGlobal.isBillOutPrinted = isBillOutPrinted.getInt("is_billout_printed");

                JSONArray trxDetailsProducts = menuItems.getJSONArray("products");

                for (int i = 0; i < trxDetailsProducts.length(); i++) {
                    JSONObject c = trxDetailsProducts.getJSONObject(i);
                    ModGlobal.itemDetailsModelList.add(new ItemDetailsModel(
                            c.getInt("prod_id"),
                            c.getString("price"),
                            c.getInt("qty"),
                            c.getString("img"),
                            c.getString("name"),
                            c.getString("cat_id"),
                            getPosition(c.getInt("prod_id")),
                            c.getString("short_name")));
                }

                JSONArray trxDetailsPackages = menuItems.getJSONArray("packages");


                for (int i = 0; i < trxDetailsPackages.length(); i++) {
                    JSONObject c = trxDetailsPackages.getJSONObject(i);
                    ModGlobal.itemDetailsModelList.add(new ItemDetailsModel(
                            c.getInt("pack_id") + 1000,
                            c.getString("price"),
                            c.getInt("qty"),
                            c.getString("img"),
                            c.getString("name"),
                            "200",
                            getPosition(c.getInt("pack_id") + 1000),

                            c.getString("short_name")));
                }


                JSONArray tables = menuItems.getJSONArray("tables");


                for (int i = 0; i < tables.length(); i++) {
                    JSONObject c = tables.getJSONObject(i);
                    addTable(c.getInt("table_id"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return json;
        }


        @Override
        protected void onPostExecute(String strFromDoInBg) {
            progressDialog.dismiss();
            ModGlobal.transType = "UPDATE";
            startActivity(new Intent(TableActivity.this, CheckOutActivity.class));
            finish();
            Log.e("itemDetails", Integer.toString(ModGlobal.itemDetailsModelList.size()));

        }


    }

    private int getPosition(int filter) {
        Log.e("filtervalue", Integer.toString(filter));
        int result = 0;

        List<MenuModel> menuModels = ModGlobal.menuModelList;

        for (MenuModel menuModel : menuModels) {
            if (menuModel.getProd_id() == filter) {
                result = menuModel.getPosition();
                Log.e("filtering", Integer.toString(menuModel.getProd_id()));
                break;
            }
        }


        return result;
    }


    void addTable(int id) {
        ModGlobal.tableId.add(id);
    }

    class RefundTransaction extends AsyncTask<String, String, String> {

        Context serviceContext;
        WebRequest wr = new WebRequest();
        ProgressDialog progressDialog;

        public RefundTransaction(Context context) {
            this.serviceContext = context;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(serviceContext);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("PLEASE WAIT");
            progressDialog.setMessage("Fetching Transaction/s");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {
            String json = "";
            try {

                ModGlobal.receiptNumber = "";
                JSONObject refundTransaction = new JSONObject(wr.makeWebServiceCall(databaseHelper.getBaseUrl() + "showlist-trans-details-by-receipt-no-api/" + params[0], WebRequest.GET));


                JSONObject jsonObject = refundTransaction.getJSONObject("details");

                ModGlobal.receiptNumber = jsonObject.getString("receipt_no");

                JSONArray prodArr = refundTransaction.getJSONArray("products");
                JSONArray packArr = refundTransaction.getJSONArray("packages");

                ModGlobal.itemDetailsModelList.clear();
                ModGlobal.tableId.clear();
                ModGlobal.isBillOutPrinted = 0;



                for (int i = 0; i < prodArr.length(); i++) {
                    JSONObject c = prodArr.getJSONObject(i);
                    ModGlobal.itemDetailsModelList.add(new ItemDetailsModel(
                            c.getInt("prod_id"),
                            c.getString("price"),
                            c.getInt("qty"),
                            c.getString("img"),
                            c.getString("name"),
                            c.getString("cat_id"),
                            getPosition(c.getInt("prod_id")),
                            c.getString("short_name")));
                }



                for (int i = 0; i < packArr.length(); i++) {
                    JSONObject c = packArr.getJSONObject(i);
                    ModGlobal.itemDetailsModelList.add(new ItemDetailsModel(
                            c.getInt("pack_id") + 1000,
                            c.getString("price"),
                            c.getInt("qty"),
                            c.getString("img"),
                            c.getString("name"),
                            "200",
                            getPosition(c.getInt("pack_id") + 1000),

                            c.getString("short_name")));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return json;
        }


        @Override
        protected void onPostExecute(String strFromDoInBg) {
            progressDialog.dismiss();
            ModGlobal.transType = "REFUND";
            startActivity(new Intent(TableActivity.this, CheckOutActivity.class));
            finish();
            Log.e("itemDetails", Integer.toString(ModGlobal.itemDetailsModelList.size()));

        }


    }

    private void showReceiptEntry() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.app_register, null);
        final EditText password = alertLayout.findViewById(R.id.et_password);
        password.setInputType(InputType.TYPE_CLASS_NUMBER);

        AlertDialog.Builder alert = new AlertDialog.Builder(TableActivity.this);
        alert.setIcon(TableActivity.this.getResources().getDrawable(R.drawable.baseline_autorenew_white_18));
        alert.setTitle("Enter Receipt Number");
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
                new RefundTransaction(TableActivity.this).execute(password.getText().toString());
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }


}
