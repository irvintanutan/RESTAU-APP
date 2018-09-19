package com.jik.irvin.restauapp.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.epson.eposprint.Print;
import com.jik.irvin.restauapp.Adapter.CashierCategoryAdapter;
import com.jik.irvin.restauapp.Adapter.CashierMenuAdapter;
import com.jik.irvin.restauapp.Adapter.CashieringTableAdapter;
import com.jik.irvin.restauapp.Adapter.ItemDetailsAdapter;
import com.jik.irvin.restauapp.Adapter.TableAdapter;
import com.jik.irvin.restauapp.DatabaseHelper;
import com.jik.irvin.restauapp.Model.CategoryModel;
import com.jik.irvin.restauapp.Constants.ClickListener;
import com.jik.irvin.restauapp.Model.CompanyConfigModel;
import com.jik.irvin.restauapp.Model.DiscountModel;
import com.jik.irvin.restauapp.Model.ItemDetailsModel;
import com.jik.irvin.restauapp.Adapter.LineItemAdapter;
import com.jik.irvin.restauapp.Model.MenuModel;
import com.jik.irvin.restauapp.Constants.ModGlobal;
import com.jik.irvin.restauapp.Model.PackageDetailsModel;
import com.jik.irvin.restauapp.Model.TableModel;
import com.jik.irvin.restauapp.R;
import com.jik.irvin.restauapp.Constants.RecyclerTouchListener;
import com.jik.irvin.restauapp.Services.DiscoveryActivity;
import com.jik.irvin.restauapp.Services.ShowMsg;
import com.jik.irvin.restauapp.Services.WebRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CashierActivity extends AppCompatActivity implements ReceiveListener {

    private RecyclerView recyclerViewMenu, recyclerViewCategory;
    private CashierMenuAdapter cashierMenuAdapter;
    private CashierCategoryAdapter cashierCategoryAdapter;
    private RecyclerView recyclerViewLineItem;
    private LineItemAdapter lineItemAdapter;
    private TextView totalPrice, cartItems, tableNumber;
    private CardView payment, cancelTransaction, cardTableNumber;
    DecimalFormat dec = new DecimalFormat("#,##0.00");
    private int itemDetailsIndex = 0, itemDetailsQty = 1;
    boolean isExist = false;
    private AlertDialog finalDialog = null;
    private String transType = "";

    private RecyclerView recyclerViewTable;
    private TableAdapter tableAdapter;

    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    boolean warning = false;

    private double finalSubTotal = 0.00, finalTotal = 0.00, finalCash = 0.00, finalChange = 0.00, finalDiscount = 0.00;

    private Printer mPrinter = null;


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
        ab.setTitle("  " + ModGlobal.userModel.getFirstName() + " " + ModGlobal.userModel.getMiddleName() + " "
                + ModGlobal.userModel.getLastName() + " (CASHIER)");
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(true); // disable the default title element here (for centered title)

        totalPrice = findViewById(R.id.totalPrice);
        cartItems = findViewById(R.id.cartItems);
        payment = findViewById(R.id.payment);
        cancelTransaction = findViewById(R.id.cancelTransaction);
        tableNumber = findViewById(R.id.tableNumber);
        cardTableNumber = findViewById(R.id.cardTableNumber);


        cardTableNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopUpTable();
            }
        });

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


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
// Set the content to appear under the system bars so that the
// content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
// Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
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
                ModGlobal.itemDetailsModelList.clear();
                ModGlobal.tableModelList.clear();
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
        getMenuInflater().inflate(R.menu.settings, menu);
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
        } else if (item.getItemId() == R.id.action_sync) {
            Intent intent = new Intent(this, DiscoveryActivity.class);
            startActivityForResult(intent, 0);
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


    public void PopUpTable() {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.table_view, null);


        final RecyclerView recyclerViewTable = alertLayout.findViewById(R.id.recycler_view_table_list);

        RecyclerView.LayoutManager mLayoutManager1 = new GridLayoutManager(this, 5);
        recyclerViewTable.setLayoutManager(mLayoutManager1);
        recyclerViewTable.setItemAnimator(new DefaultItemAnimator());
        ModGlobal.cashieringTableAdapter = new CashieringTableAdapter(this, ModGlobal.tableModelList);
        recyclerViewTable.setAdapter(ModGlobal.cashieringTableAdapter);

        recyclerViewTable.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerViewTable, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    TableModel table = ModGlobal.tableModelList.get(position);
                    ModGlobal.tableId.clear();
                    addTable(Integer.parseInt(table.getTableId()));
                    tableNumber.setText(table.getName());
                    finalDialog.dismiss();

                } catch (Exception e) {
                    Log.e("asd", "something went wrong");
                }

            }

            @Override
            public void onLongClick(View view, final int position) {


            }
        }));


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch


        finalDialog = alert.create();
        finalDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = (int) this.getResources().getDimension(R.dimen.width);
        lp.height = (int) this.getResources().getDimension(R.dimen.height_payment);

        finalDialog.getWindow().setAttributes(lp);
        //dialog.getWindow().setLayout(800, 400);
    }

    void addTable(int id) {
        ModGlobal.tableId.add(id);
    }


    public void PopUpDiscount() {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.discount_view, null);


        final Spinner spinner = alertLayout.findViewById(R.id.spinner);

        List<DiscountModel> discountModels = ModGlobal.discountModelList;
        ArrayList<String> values = new ArrayList<>();
        ArrayList<Integer> discountId = new ArrayList<>();

        for (DiscountModel discountModel : discountModels) {

            values.add(discountModel.getDesc());
            discountId.add(discountModel.getDiscId());

        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        // this is set the view from XML inside AlertDialog
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

        alert.setPositiveButton("Apply Discount", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


                dialog.dismiss();
            }

        });

        finalDialog = alert.create();
        finalDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = (int) this.getResources().getDimension(R.dimen.width);
        lp.height = (int) this.getResources().getDimension(R.dimen.height_payment);

        finalDialog.getWindow().setAttributes(lp);
        //dialog.getWindow().setLayout(800, 400);
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
        final CardView clearDiscount = alertLayout.findViewById(R.id.clearDiscount);
        final CardView discount = alertLayout.findViewById(R.id.discount);
        final CardView dineIn = alertLayout.findViewById(R.id.dineIn);
        final CardView takeOut = alertLayout.findViewById(R.id.takeOut);
        final CardView close = alertLayout.findViewById(R.id.close);

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

        dineIn.setOnClickListener(new View.OnClickListener() {
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


        takeOut.setOnClickListener(new View.OnClickListener() {
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

                    builder.setTitle("TAKE OUT");
                    builder.setMessage("Are you sure you want to place the order ?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
                            // Do nothing
                            new Sync(CashierActivity.this).execute("TAKE-OUT");
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

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalDialog.dismiss();
            }
        });


        clearDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ModGlobal.discount = 0.00;
                ModGlobal.discType = 0;
                discountValue.setText(dec.format(ModGlobal.discount));
                discountValue.setTextColor(Color.BLACK);

            }
        });


        discount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopUpDiscount();
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


      /*  alert.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                // Do nothing
                dialog.dismiss();

            }

        });*/

        finalDialog = alert.create();
        finalDialog.setCancelable(false);

        finalDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = (int) this.getResources().getDimension(R.dimen.width);
        lp.height = (int) this.getResources().getDimension(R.dimen.height_payment);

        finalDialog.getWindow().setAttributes(lp);
        //dialog.getWindow().setLayout(800, 400);

    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        if (data != null && resultCode == RESULT_OK) {
            String target = data.getStringExtra(getString(R.string.title_target));
            if (target != null) {
                ModGlobal.printerSetup = target;
            }
        }
    }

    @Override
    public void onPtrReceive(Printer printer, final int i, final PrinterStatusInfo status, String s) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                ShowMsg.showResult(i, makeErrorMessage(status), CashierActivity.this);


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectPrinter();
                    }
                }).start();
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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
                transType = params[0];
                JSONObject mainJsonObject = new JSONObject();
                JSONArray mainJsonArray = new JSONArray();

                JSONArray detailsArray = new JSONArray();
                JSONObject detailsObject = new JSONObject();
                detailsObject.put("order_type", params[0]);
                detailsObject.put("user_id", ModGlobal.userModel.getUserId());
                detailsObject.put("discount", ModGlobal.discount);
                detailsObject.put("disc_type", ModGlobal.discType);
                detailsObject.put("method", "Cash");
                detailsObject.put("cash_amt", finalCash);
                detailsObject.put("card_number", "");
                detailsObject.put("cust_name", "");
                detailsObject.put("cashier_id", ModGlobal.userModel.getUserId());
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

                runPrintReceiptSequence();

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
                        ModGlobal.discount = 0.00;
                        ModGlobal.discType = 0;
                        ModGlobal.discountLabel = "";
                        tableNumber.setText("Table #");
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


    private boolean printReceipt() {
        String method = "";
        StringBuilder textData = new StringBuilder();


        CompanyConfigModel c = ModGlobal.companyConfigModels.get(0);

        String companyName = wordwrap(c.getName(), 15);
        String companyAddress = wordwrap(c.getAddress(), 25);
        String companyTin = wordwrap(c.getTin(), 25);
        String companyCity = wordwrap(c.getCity(), 25);


        if (mPrinter == null) {
            return false;
        }

        try {
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            method = "Print Company Information";
            mPrinter.addTextSize(2, 1);
            textData.append(companyName + "\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            mPrinter.addTextSize(1, 1);
            textData.append(companyAddress + "\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            mPrinter.addTextSize(1, 1);
            textData.append(companyCity + "\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            mPrinter.addTextSize(1, 1);
            textData.append(companyTin + "\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            mPrinter.addTextSize(1, 1);
            textData.append("******************************\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            mPrinter.addTextSize(1, 1);
            textData.append(tableNumber.getText().toString() + "\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            mPrinter.addTextSize(1, 1);
            textData.append("******************************\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextSize(2, 1);
            textData.append(transType + "\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            mPrinter.addTextSize(1, 1);
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append("Receipt#: " + databaseHelper.getLastReceiptNumber() + "\n");
            textData.append("Staff: " + ModGlobal.userModel.getUsername() + "\n");
            textData.append("Cashier: " + ModGlobal.userModel.getUsername() + "\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            textData.append("===================================\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
            textData.append("Php\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            mPrinter.addTextAlign(Printer.ALIGN_LEFT);

            List<ItemDetailsModel> itemDetailsModels = ModGlobal.itemDetailsModelList;


            for (ItemDetailsModel itemDetailsModel : itemDetailsModels) {

                String textValue = "";
                String totalValue = "";
                String finalValue = "";
                int length = 0;

                if (itemDetailsModel.getCatID().equals("200")) {

                    textValue += itemDetailsModel.getMenuQty() + " " + itemDetailsModel.getShortName()
                            + " @" + itemDetailsModel.getMenuPrice();

                    totalValue = dec.format(Double.parseDouble(itemDetailsModel.getMenuPrice()) *
                            itemDetailsModel.getMenuQty());

                    length = 35 - (textValue.length() + totalValue.length());

                    finalValue = textValue + padding(length) + totalValue;

                    List<PackageDetailsModel> packageDetailsModels = ModGlobal.packageDetailsModelList;

                    for (PackageDetailsModel packageDetailsModel : packageDetailsModels) {
                        if (packageDetailsModel.getPackage_id().equals(Integer.toString(itemDetailsModel.getProdID()))) {
                            finalValue += "   " + (Integer.parseInt(packageDetailsModel.getProd_qty()) * itemDetailsModel.getMenuQty()) + " "
                                    + packageDetailsModel.getProd_short_name() + "\n";
                        }
                    }

                    if (packageDetailsModels.size() > 0) {
                        textData.append(finalValue);
                    } else
                        textData.append(finalValue + "\n");

                } else {
                    textValue += itemDetailsModel.getMenuQty() + " " + itemDetailsModel.getShortName()
                            + " @" + itemDetailsModel.getMenuPrice();

                    totalValue = dec.format(Double.parseDouble(itemDetailsModel.getMenuPrice()) *
                            itemDetailsModel.getMenuQty());

                    length = 35 - (textValue.length() + totalValue.length());

                    finalValue = textValue + padding(length) + totalValue;

                    textData.append(finalValue + "\n");

                }

            }

            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.COLOR_1);
            textData.append("===================================\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.TRUE, Printer.COLOR_1);
            double vat = finalTotal * .12;
            textData.append("Total Sales" + padding(35 - (11 + dec.format(finalTotal - vat).length())) + dec.format(finalTotal - vat) + "\n");

            textData.append("Vat" + padding(35 - (3 + dec.format(vat).length())) + dec.format(vat) + "\n");
            textData.append(padding(35 - 10) + "==========" + "\n");
            textData.append("Amount Due" + padding(35 - (10 + dec.format(finalTotal).length())) + dec.format(finalTotal) + "\n");
            textData.append("Cash" + padding(35 - (4 + dec.format(finalCash).length())) + dec.format(finalCash) + "\n");
            textData.append("Change" + padding(35 - (6 + dec.format(finalChange).length())) + dec.format(finalChange) + "\n\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());


            mPrinter.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.COLOR_1);
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            String pattern = "EEE, dd MMMM yyyy hh:mm aaa";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());
            textData.append(date + "\n\n");
            textData.append("Innotech Solutions\n");
            textData.append("Thank You Come Again\n");

            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());


            mPrinter.addFeedLine(1);




          /*  mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            textData.append("Sample 1 feed line\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            mPrinter.addFeedLine(1);


            mPrinter.addTextFont(Printer.FONT_A);
            textData.append("Sample FONT A\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            mPrinter.addTextFont(Printer.FONT_B);
            textData.append("Sample FONT B\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            mPrinter.addTextFont(Printer.FONT_C);
            textData.append("Sample FONT C\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            mPrinter.addTextFont(Printer.FONT_D);
            textData.append("Sample FONT D\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            mPrinter.addTextFont(Printer.FONT_E);
            textData.append("Sample FONT E\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            mPrinter.addLineSpace(5);
            textData.append("Sample Line Space 5\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            mPrinter.addLineSpace(10);
            textData.append("Sample Line Space 10\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            mPrinter.addLineSpace(15);
            textData.append("Sample Line Space 15\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            mPrinter.addTextSize(1 , 1);
            textData.append("Sample Text size 1\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());


            mPrinter.addTextSize(2, 2);
            textData.append("Sample Text size 2\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());


            mPrinter.addTextSize(3 , 3);
            textData.append("Sample Text size 3\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());


         */

            mPrinter.addCut(Printer.CUT_FEED);

        } catch (Exception e) {
            ShowMsg.showException(e, method, CashierActivity.this);
            return false;

        }

        return true;
    }

    String padding(int length) {
        String value = "";

        for (int a = 1; a <= length; a++)
            value += " ";

        return value;
    }


    private boolean runPrintReceiptSequence() {
        if (!initializeObject()) {
            return false;
        }

        if (!printReceipt()) {
            finalizeObject();
            return false;
        }

        if (!printData()) {
            finalizeObject();
            return false;
        }

        return true;
    }


    private boolean printData() {
        if (mPrinter == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        PrinterStatusInfo status = mPrinter.getStatus();


        if (!isPrintable(status)) {
            ShowMsg.showMsg(makeErrorMessage(status), CashierActivity.this);
            try {
                mPrinter.disconnect();
            } catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        try {
            mPrinter.sendData(Printer.PARAM_DEFAULT);
        } catch (Exception e) {
            ShowMsg.showException(e, "sendData", CashierActivity.this);
            try {
                mPrinter.disconnect();
            } catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        return true;
    }

    private boolean initializeObject() {
        try {
            mPrinter = new Printer(Printer.TM_U220, Printer.MODEL_SOUTHASIA, CashierActivity.this);
        } catch (Exception e) {
            ShowMsg.showException(e, "Printer", CashierActivity.this);
            return false;
        }

        mPrinter.setReceiveEventListener(this);

        return true;
    }

    private void finalizeObject() {
        if (mPrinter == null) {
            return;
        }

        mPrinter.clearCommandBuffer();

        mPrinter.setReceiveEventListener(null);

        mPrinter = null;
    }

    private boolean connectPrinter() {
        boolean isBeginTransaction = false;

        if (mPrinter == null) {
            return false;
        }

        try {
            mPrinter.connect(ModGlobal.printerSetup, Printer.PARAM_DEFAULT);
        } catch (Exception e) {
            ShowMsg.showException(e, "connect", CashierActivity.this);
            return false;
        }

        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
        } catch (Exception e) {
            ShowMsg.showException(e, "beginTransaction", CashierActivity.this);
        }

        if (isBeginTransaction == false) {
            try {
                mPrinter.disconnect();
            } catch (Epos2Exception e) {
                // Do nothing
                return false;
            }
        }

        return true;
    }

    private void disconnectPrinter() {
        if (mPrinter == null) {
            return;
        }

        try {
            mPrinter.endTransaction();
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    ShowMsg.showException(e, "endTransaction", CashierActivity.this);
                }
            });
        }

        try {
            mPrinter.disconnect();
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    ShowMsg.showException(e, "disconnect", CashierActivity.this);
                }
            });
        }

        finalizeObject();
    }

    private boolean isPrintable(PrinterStatusInfo status) {
        if (status == null) {
            return false;
        }

        if (status.getConnection() == Printer.FALSE) {
            return false;
        } else if (status.getOnline() == Printer.FALSE) {
            return false;
        } else {
            ;//print available
        }

        return true;
    }

    private String makeErrorMessage(PrinterStatusInfo status) {
        String msg = "";

        if (status.getOnline() == Printer.FALSE) {
            msg += getString(R.string.handlingmsg_err_offline);
        }
        if (status.getConnection() == Printer.FALSE) {
            msg += getString(R.string.handlingmsg_err_no_response);
        }
        if (status.getCoverOpen() == Printer.TRUE) {
            msg += getString(R.string.handlingmsg_err_cover_open);
        }
        if (status.getPaper() == Printer.PAPER_EMPTY) {
            msg += getString(R.string.handlingmsg_err_receipt_end);
        }
        if (status.getPaperFeed() == Printer.TRUE || status.getPanelSwitch() == Printer.SWITCH_ON) {
            msg += getString(R.string.handlingmsg_err_paper_feed);
        }
        if (status.getErrorStatus() == Printer.MECHANICAL_ERR || status.getErrorStatus() == Printer.AUTOCUTTER_ERR) {
            msg += getString(R.string.handlingmsg_err_autocutter);
            msg += getString(R.string.handlingmsg_err_need_recover);
        }
        if (status.getErrorStatus() == Printer.UNRECOVER_ERR) {
            msg += getString(R.string.handlingmsg_err_unrecover);
        }
        if (status.getErrorStatus() == Printer.AUTORECOVER_ERR) {
            if (status.getAutoRecoverError() == Printer.HEAD_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_head);
            }
            if (status.getAutoRecoverError() == Printer.MOTOR_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_motor);
            }
            if (status.getAutoRecoverError() == Printer.BATTERY_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_battery);
            }
            if (status.getAutoRecoverError() == Printer.WRONG_PAPER) {
                msg += getString(R.string.handlingmsg_err_wrong_paper);
            }
        }
        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_0) {
            msg += getString(R.string.handlingmsg_err_battery_real_end);
        }

        return msg;
    }


    public static String wordwrap(final String input, final int length) {
        if (input == null || length < 1) {
            throw new IllegalArgumentException("Invalid input args");
        }

        final String text = input.trim();

        if (text.length() > length && text.contains(" ")) {
            final String line = text.substring(0, length);
            final int lineBreakIndex = line.indexOf("\n");
            final int lineLastSpaceIndex = line.lastIndexOf(" ");
            final int inputFirstSpaceIndex = text.indexOf(" ");

            final int breakIndex = lineBreakIndex > -1 ? lineBreakIndex :
                    (lineLastSpaceIndex > -1 ? lineLastSpaceIndex : inputFirstSpaceIndex);

            return text.substring(0, breakIndex) + "\n" + wordwrap(text.substring(breakIndex + 1), length);
        } else {
            return text;
        }
    }


}
