package com.jik.irvin.restauapp.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.jik.irvin.restauapp.Adapter.CashierCategoryAdapter;
import com.jik.irvin.restauapp.Adapter.CashierMenuAdapter;
import com.jik.irvin.restauapp.DatabaseHelper;
import com.jik.irvin.restauapp.Model.CategoryModel;
import com.jik.irvin.restauapp.Constants.ClickListener;
import com.jik.irvin.restauapp.Model.ItemDetailsModel;
import com.jik.irvin.restauapp.Adapter.LineItemAdapter;
import com.jik.irvin.restauapp.Model.MenuModel;
import com.jik.irvin.restauapp.Constants.ModGlobal;
import com.jik.irvin.restauapp.R;
import com.jik.irvin.restauapp.Constants.RecyclerTouchListener;
import com.jik.irvin.restauapp.Services.WebRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CashierActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMenu, recyclerViewCategory;
    private CashierMenuAdapter cashierMenuAdapter;
    private CashierCategoryAdapter cashierCategoryAdapter;
    private RecyclerView recyclerViewLineItem;
    private LineItemAdapter lineItemAdapter;
    private TextView totalPrice, cartItems;
    private CardView payment, cancelTransaction;
    DecimalFormat dec = new DecimalFormat("#,##0.00");
    private int itemDetailsIndex = 0, itemDetailsQty = 1;
    boolean isExist = false;
    private AlertDialog finalDialog = null;

    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    boolean warning = false;

    private double finalSubTotal = 0.00, finalTotal = 0.00, finalCash = 0.00, finalChange = 0.00;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_cashier);

        Toolbar tb = findViewById(R.id.app_bar);
        setSupportActionBar(tb);
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        //ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        //ab.setDisplayHomeAsUpEnabled(true);
        ab.setLogo(R.drawable.logo);
        ab.setTitle("  Hi Mr. Irvin Tanutan (CASHIER)");
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(true); // disable the default title element here (for centered title)

        totalPrice = findViewById(R.id.totalPrice);
        cartItems = findViewById(R.id.cartItems);
        payment = findViewById(R.id.payment);
        cancelTransaction = findViewById(R.id.cancelTransaction);

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!ModGlobal.itemDetailsModelList.isEmpty())
                    PopUpPayment();
            }
        });

        cancelTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!ModGlobal.itemDetailsModelList.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CashierActivity.this);

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
                            finalSubTotal = 0.00;
                            finalTotal = 0.00;
                            finalCash = 0.00;
                            finalChange = 0.00;
                            totalPrice.setText("Charge\n₱ " + dec.format(finalSubTotal));
                            cartItems.setText("# of items " + ModGlobal.itemDetailsModelList.size());


                            lineItemAdapter.notifyDataSetChanged();

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
            }
        });

        recyclerViewCategory = findViewById(R.id.recycler_view_category);
        recyclerViewMenu = findViewById(R.id.recycler_view_menu);
        recyclerViewLineItem = findViewById(R.id.recycler_view_line_item);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CashierActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCategory.setLayoutManager(mLayoutManager);
        recyclerViewCategory.setItemAnimator(new DefaultItemAnimator());
        cashierCategoryAdapter = new CashierCategoryAdapter(this, ModGlobal.categoryModelList);
        recyclerViewCategory.setAdapter(cashierCategoryAdapter);
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

        RecyclerView.LayoutManager mLayoutManager3 = new GridLayoutManager(this, 1);
        recyclerViewLineItem.setLayoutManager(mLayoutManager3);
        //recyclerViewMenu.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerViewLineItem.setItemAnimator(new DefaultItemAnimator());
        lineItemAdapter = new LineItemAdapter(this, ModGlobal.itemDetailsModelList);
        recyclerViewLineItem.setAdapter(lineItemAdapter);


        recyclerViewLineItem.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerViewLineItem, new ClickListener() {
            @Override
            public void onClick(View view, int position) {


                ItemDetailsModel itemDetailsModel = ModGlobal.itemDetailsModelList.get(position);

                if (itemDetailsModel.getMenuQty() > 1) {
                    itemDetailsQty = itemDetailsModel.getMenuQty() - 1;
                    isExist = true;


                    ModGlobal.itemDetailsModelList.set(position, new ItemDetailsModel(itemDetailsModel.getProdID(),
                            itemDetailsModel.getMenuPrice(), itemDetailsQty,
                            itemDetailsModel.getUrl(), itemDetailsModel.getMenuName(), itemDetailsModel.getCatID(), itemDetailsModel.getPosition(), itemDetailsModel.getShortName()));

                    lineItemAdapter.notifyDataSetChanged();
                    computeTotal();
                }
            }

            @Override
            public void onLongClick(View view, int position) {


            }
        }));

        RecyclerView.LayoutManager mLayoutManager2 = new GridLayoutManager(this, 4);
        recyclerViewMenu.setLayoutManager(mLayoutManager2);
        //recyclerViewMenu.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerViewMenu.setItemAnimator(new DefaultItemAnimator());
        cashierMenuAdapter = new CashierMenuAdapter(this, ModGlobal.menuModelList);
        recyclerViewMenu.setAdapter(cashierMenuAdapter);


        recyclerViewMenu.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerViewMenu, new ClickListener() {
            @Override
            public void onClick(View view, int position) {


                MenuModel menuModel = ModGlobal.menuModelListCopy.get(position);


                isExist = false;
                for (int a = 0; a < ModGlobal.itemDetailsModelList.size(); a++) {
                    if (ModGlobal.itemDetailsModelList.get(a).getProdID() == menuModel.getProd_id()) {
                        itemDetailsIndex = a;
                        itemDetailsQty = ModGlobal.itemDetailsModelList.get(a).getMenuQty() + 1;
                        isExist = true;
                    }
                }

                if (isExist) {

                    ModGlobal.itemDetailsModelList.set(itemDetailsIndex, new ItemDetailsModel(menuModel.getProd_id(),
                            menuModel.getPrice(), itemDetailsQty,
                            menuModel.getImg(), menuModel.getName(), menuModel.getCat_id(), menuModel.getPosition(), menuModel.getShortName()));
                    lineItemAdapter.notifyDataSetChanged();

                } else {

                    ModGlobal.itemDetailsModelList.add(new ItemDetailsModel(menuModel.getProd_id(),
                            menuModel.getPrice(), 1,
                            menuModel.getImg(), menuModel.getName(), menuModel.getCat_id(), menuModel.getPosition(), menuModel.getShortName()));

                    lineItemAdapter.notifyDataSetChanged();
                    recyclerViewLineItem.smoothScrollToPosition(lineItemAdapter.getItemCount() - 1);

                }


                computeTotal();
                countItems();
            }

            @Override
            public void onLongClick(View view, int position) {


            }
        }));


        filter("100");
        computeTotal();
        countItems();
    }


    void countItems() {
        cartItems.setText("# of items " + ModGlobal.itemDetailsModelList.size());
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
                startActivity(new Intent(CashierActivity.this, MainActivity.class));
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
                    startActivity(new Intent(CashierActivity.this, MainActivity.class));
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
        cashierMenuAdapter.updateList(temp);
        ModGlobal.menuModelListCopy = temp;
    }


    void computeTotal() {

        DecimalFormat dec = new DecimalFormat("#,##0.00");
        Double total = 0.00;

        List<ItemDetailsModel> itemDetailsModels = ModGlobal.itemDetailsModelList;

        for (ItemDetailsModel itemDetailsModel : itemDetailsModels) {

            total += Double.parseDouble(itemDetailsModel.getMenuPrice()) *
                    itemDetailsModel.getMenuQty();

        }

        finalSubTotal = total;
        totalPrice.setText("Charge\n₱ " + dec.format(total));


    }


    public void PopUpPayment() {

        finalTotal = finalSubTotal;
        finalCash = 0.00;
        finalChange = 0.00;

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.payment_view, null);


        final CardView pay1 = alertLayout.findViewById(R.id.pay1);
        final CardView pay5 = alertLayout.findViewById(R.id.pay5);
        final CardView pay10 = alertLayout.findViewById(R.id.pay10);
        final CardView pay20 = alertLayout.findViewById(R.id.pay20);
        final CardView pay50 = alertLayout.findViewById(R.id.pay50);
        final CardView pay100 = alertLayout.findViewById(R.id.pay100);
        final CardView pay200 = alertLayout.findViewById(R.id.pay200);
        final CardView pay500 = alertLayout.findViewById(R.id.pay500);
        final CardView pay1000 = alertLayout.findViewById(R.id.pay1000);


        final CardView clear = alertLayout.findViewById(R.id.clear);
        final CardView checkout = alertLayout.findViewById(R.id.checkout);

        final EditText subTotalValue = alertLayout.findViewById(R.id.subTotalValue);
        final EditText discountValue = alertLayout.findViewById(R.id.discountValue);
        final EditText totalValue = alertLayout.findViewById(R.id.totalValue);
        final EditText cashValue = alertLayout.findViewById(R.id.cashValue);
        final EditText changeValue = alertLayout.findViewById(R.id.changeValue);


        subTotalValue.setFocusable(false);
        discountValue.setFocusable(false);
        totalValue.setFocusable(false);
        cashValue.setFocusable(false);
        changeValue.setFocusable(false);

        subTotalValue.setText("₱ " + dec.format(finalTotal));
        discountValue.setText("₱ " + 0.00);
        totalValue.setText("₱ " + dec.format(finalTotal));
        cashValue.setText("₱ " + dec.format(finalCash));
        double ch = finalCash - finalTotal;
        finalChange = ch;
        changeValue.setText("₱ " + dec.format(ch));
        changeValue.setTextColor(Color.RED);


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finalTotal = finalSubTotal;
                finalCash = 0.00;
                finalChange = 0.00;

                subTotalValue.setText("₱ " + dec.format(finalTotal));
                discountValue.setText("₱ " + 0.00);
                totalValue.setText("₱ " + dec.format(finalTotal));
                cashValue.setText("₱ " + dec.format(finalCash));
                double ch = finalCash - finalTotal;
                finalChange = ch;
                changeValue.setText("₱ " + dec.format(ch));
                changeValue.setTextColor(Color.RED);

            }
        });

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (finalChange < 0) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(CashierActivity.this);

                    builder.setTitle("WARNING");
                    builder.setMessage("Insufficient Amount");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }

                    });

                    AlertDialog alert = builder.create();
                    alert.show();

                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(CashierActivity.this);

                    builder.setTitle("DINE IN");
                    builder.setMessage("Are you sure you want to place the order ?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
                            // Do nothing
                            new Sync(CashierActivity.this).execute("DINE-IN");
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
        });


        pay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finalCash += 1;
                cashValue.setText("₱ " + dec.format(finalCash));
                double ch = finalCash - finalTotal;
                finalChange = ch;
                changeValue.setText("₱ " + dec.format(ch));

                if (ch < 0) {
                    changeValue.setTextColor(Color.RED);
                } else {
                    changeValue.setTextColor(getApplicationContext().getResources().getColor(R.color.green));
                }

            }
        });


        pay5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finalCash += 5;
                cashValue.setText("₱ " + dec.format(finalCash));
                double ch = finalCash - finalTotal;
                finalChange = ch;
                changeValue.setText("₱ " + dec.format(ch));

                if (ch < 0) {
                    changeValue.setTextColor(Color.RED);
                } else {
                    changeValue.setTextColor(getApplicationContext().getResources().getColor(R.color.green));
                }


            }
        });

        pay10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finalCash += 10;
                cashValue.setText("₱ " + dec.format(finalCash));
                double ch = finalCash - finalTotal;
                finalChange = ch;
                changeValue.setText("₱ " + dec.format(ch));

                if (ch < 0) {
                    changeValue.setTextColor(Color.RED);
                } else {
                    changeValue.setTextColor(getApplicationContext().getResources().getColor(R.color.green));
                }


            }
        });


        pay20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finalCash += 20;
                cashValue.setText("₱ " + dec.format(finalCash));
                double ch = finalCash - finalTotal;
                finalChange = ch;
                changeValue.setText("₱ " + dec.format(ch));

                if (ch < 0) {
                    changeValue.setTextColor(Color.RED);
                } else {
                    changeValue.setTextColor(getApplicationContext().getResources().getColor(R.color.green));
                }


            }
        });

        pay50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finalCash += 50;
                cashValue.setText("₱ " + dec.format(finalCash));
                double ch = finalCash - finalTotal;
                finalChange = ch;
                changeValue.setText("₱ " + dec.format(ch));

                if (ch < 0) {
                    changeValue.setTextColor(Color.RED);
                } else {
                    changeValue.setTextColor(getApplicationContext().getResources().getColor(R.color.green));
                }


            }
        });

        pay100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finalCash += 100;
                cashValue.setText("₱ " + dec.format(finalCash));
                double ch = finalCash - finalTotal;
                finalChange = ch;
                changeValue.setText("₱ " + dec.format(ch));

                if (ch < 0) {
                    changeValue.setTextColor(Color.RED);
                } else {
                    changeValue.setTextColor(getApplicationContext().getResources().getColor(R.color.green));
                }


            }
        });

        pay200.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finalCash += 200;
                cashValue.setText("₱ " + dec.format(finalCash));
                double ch = finalCash - finalTotal;
                finalChange = ch;
                changeValue.setText("₱ " + dec.format(ch));

                if (ch < 0) {
                    changeValue.setTextColor(Color.RED);
                } else {
                    changeValue.setTextColor(getApplicationContext().getResources().getColor(R.color.green));
                }


            }
        });

        pay500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finalCash += 500;
                cashValue.setText("₱ " + dec.format(finalCash));
                double ch = finalCash - finalTotal;
                finalChange = ch;
                changeValue.setText("₱ " + dec.format(ch));

                if (ch < 0) {
                    changeValue.setTextColor(Color.RED);
                } else {
                    changeValue.setTextColor(getApplicationContext().getResources().getColor(R.color.green));
                }


            }
        });


        pay1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finalCash += 1000;
                cashValue.setText("₱ " + dec.format(finalCash));
                double ch = finalCash - finalTotal;
                finalChange = ch;
                changeValue.setText("₱ " + dec.format(ch));

                if (ch < 0) {
                    changeValue.setTextColor(Color.RED);
                } else {
                    changeValue.setTextColor(getApplicationContext().getResources().getColor(R.color.green));
                }


            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch


        alert.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                // Do nothing
                dialog.dismiss();

            }

        });

        finalDialog = alert.create();
        finalDialog.setCancelable(false);

        finalDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = (int) this.getResources().getDimension(R.dimen.width);
        lp.height = (int) this.getResources().getDimension(R.dimen.height_payment);

        finalDialog.getWindow().setAttributes(lp);
        //dialog.getWindow().setLayout(800, 400);

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

                JSONObject mainJsonObject = new JSONObject();
                JSONArray mainJsonArray = new JSONArray();

                JSONArray detailsArray = new JSONArray();
                JSONObject detailsObject = new JSONObject();
                detailsObject.put("order_type", params[0]);
                detailsObject.put("user_id", 103);
                detailsObject.put("discount", 0.00);
                detailsObject.put("disc_type", 0);
                detailsObject.put("method", "Cash");
                detailsObject.put("cash_amt", finalCash);
                detailsObject.put("card_number", "");
                detailsObject.put("cust_name", "");
                detailsObject.put("cashier_id", 103);
                detailsObject.put("amount_due", finalTotal);
                detailsObject.put("receipt_no", databaseHelper.getLastReceiptNumber());

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

                String response = WebRequest.makePostRequest(databaseHelper.getBaseUrl() + "set-payment-api",
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


                AlertDialog.Builder builder = new AlertDialog.Builder(serviceContext);

                builder.setTitle("Information");
                builder.setMessage("Transaction Successful!");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        // Do nothing

                        ModGlobal.itemDetailsModelList.clear();
                        ModGlobal.tableId.clear();
                        ModGlobal.transactionId = "";
                        ModGlobal.transType = "NORMAL";
                        finalSubTotal = 0.00;
                        finalTotal = 0.00;
                        finalCash = 0.00;
                        finalChange = 0.00;
                        totalPrice.setText("Charge\n₱ " + dec.format(finalSubTotal));
                        cartItems.setText("# of items " + ModGlobal.itemDetailsModelList.size());
                        databaseHelper.updateLastReceiptNumber(Integer.toString(databaseHelper.getLastReceiptNumber()));
                        finalDialog.dismiss();
                        lineItemAdapter.notifyDataSetChanged();

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


    void removeTable(int id) {
        for (int a = 0; a < ModGlobal.tableId.size(); a++) {
            if (ModGlobal.tableId.get(a) == id)
                ModGlobal.tableId.remove(a);
        }
    }


}
