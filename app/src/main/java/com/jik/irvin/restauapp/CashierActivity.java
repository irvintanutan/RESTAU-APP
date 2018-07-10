package com.jik.irvin.restauapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CashierActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMenu, recyclerViewCategory;
    private CashierMenuAdapter cashierMenuAdapter;
    private CashierCategoryAdapter cashierCategoryAdapter;

    private RecyclerView recyclerViewLineItem;
    private LineItemAdapter lineItemAdapter;
    private TextView totalPrice;

    private int itemDetailsIndex = 0, itemDetailsQty = 1;
    boolean isExist = false;


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
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        //ab.setLogo(R.drawable.ic_timeline_white_24dp);
        ab.setTitle("Hi Mr. Irvin Tanutan (CASHIER)");
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(true); // disable the default title element here (for centered title)

        totalPrice = findViewById(R.id.totalPrice);

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


                    ModGlobal.itemDetailsModelList.set(itemDetailsIndex, new ItemDetailsModel(itemDetailsModel.getProdID(),
                            itemDetailsModel.getMenuPrice(), itemDetailsQty,
                            itemDetailsModel.getUrl(), itemDetailsModel.getMenuName(), itemDetailsModel.getCatID(), itemDetailsModel.getPosition(), itemDetailsModel.getShortName()));

                    lineItemAdapter.notifyDataSetChanged();
                    recyclerViewLineItem.smoothScrollToPosition(lineItemAdapter.getItemCount() - 1);
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
                    recyclerViewLineItem.smoothScrollToPosition(itemDetailsIndex);
                } else {

                    ModGlobal.itemDetailsModelList.add(new ItemDetailsModel(menuModel.getProd_id(),
                            menuModel.getPrice(), 1,
                            menuModel.getImg(), menuModel.getName(), menuModel.getCat_id(), menuModel.getPosition(), menuModel.getShortName()));

                    lineItemAdapter.notifyDataSetChanged();
                    recyclerViewLineItem.smoothScrollToPosition(lineItemAdapter.getItemCount() - 1);

                }


                computeTotal();
            }

            @Override
            public void onLongClick(View view, int position) {


            }
        }));


        filter("100");
        computeTotal();
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


        totalPrice.setText("Charge\n₱ " + dec.format(total));

    }

}
