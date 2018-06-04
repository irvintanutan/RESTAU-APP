package com.jik.irvin.restauapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class TableActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMenu, recyclerViewCategory;
    private MenuAdapter menuAdapter;
    private CategoryAdapter categoryAdapter;
    private boolean isExist = false;
    private int itemDetailsIndex = 0 , itemDetailsQty = 1;
    private CardView proceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_table);

        proceed = findViewById(R.id.button_proceed);
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
                startActivity(new Intent(TableActivity.this, CheckOutActivity.class));
                finish();
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


        filter("200");
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
                            menuModel.getImg() , menuModel.getName() , menuModel.getCat_id() , menuModel.getPosition()));

                } else {

                    ModGlobal.itemDetailsModelList.add(new ItemDetailsModel(menuModel.getProd_id(),
                            menuModel.getPrice(), Integer.parseInt(quantity.getText().toString()),
                            menuModel.getImg() , menuModel.getName() , menuModel.getCat_id() , menuModel.getPosition()));

                }


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

}
