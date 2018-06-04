package com.jik.irvin.restauapp;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class CheckOutActivity extends AppCompatActivity {

    private CardView checkout, cancel;
    private TextView totalPrice;
    private RecyclerView recyclerView;
    private ItemDetailsAdapter itemDetailsAdapter;

    private boolean isExist = false;
    private int itemDetailsIndex = 0 , itemDetailsQty = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_check_out);

        checkout = findViewById(R.id.button);
        cancel = findViewById(R.id.cancel);
        totalPrice = findViewById(R.id.totalPrice);

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



        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  AlertDialog.Builder builder = new AlertDialog.Builder(CheckOutActivity.this);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want to checkout ?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        // Do nothing
                        if (ModGlobal.TRANSACTION_TYPE.equals("NORMAL"))
                            new Sync(CheckoutActivity.this).execute("");
                        else
                            new SyncUpdate(CheckoutActivity.this).execute("");

                    }

                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();*/

            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CheckOutActivity.this);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want to cancel this transaction ?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        // Do nothing
                        ModGlobal.itemDetailsModelList.clear();
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
                alert.show();
            }
        });



        recyclerView = findViewById(R.id.recycler_view_table);
        itemDetailsAdapter = new ItemDetailsAdapter(this, ModGlobal.itemDetailsModelList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
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
              /*  AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want to remove" + ModGlobal.itemDetailsModelList.get(position).getMenuName() + "?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        // Do nothing

                        ItemDetailsModel itemDetailsModel = ModGlobal.itemDetailsModelList.get(position);
                        Log.e("asd" , Integer.toString(itemDetailsModel.getMenuID()));
                        HashMap<String , String> x = new HashMap<String , String>();
                        for (int a = 0 ; a < ModGlobal.runningTotal.size() ; a++){
                            x = ModGlobal.runningTotal.get(a);

                            Log.e("menusada" , x.get("ingredient_id") + " " + x.get("menu_id") + " " + itemDetailsModel.getMenuID());

                            if (Integer.parseInt(x.get("menu_id")) == itemDetailsModel.getMenuID()) {
                                Log.e("niusulod1" , "nisulod1");
                                HashMap<String , String> y = new HashMap<String , String>();
                                y.put("ingredient_id", x.get("ingredient_id"));
                                y.put("value", "0.00");
                                y.put("menu_id" , x.get("menu_id"));
                                ModGlobal.runningTotal.set(a , y);
                            }
                        }

                        x = new HashMap<String , String>();
                        for (int a = 0 ; a < ModGlobal.runningTotalWhenEnabled.size() ; a++){
                            x = ModGlobal.runningTotalWhenEnabled.get(a);

                            Log.e("menusada" , x.get("menu_id") + " " + itemDetailsModel.getMenuID());

                            if (Integer.parseInt(x.get("menu_id")) == itemDetailsModel.getMenuID()) {
                                Log.e("niusulod2" , "nisulod2");
                                HashMap<String , String> y = new HashMap<String , String>();
                                y.put("ingredient_id", x.get("ingredient_id"));
                                y.put("value", "0.00");
                                y.put("menu_id" , x.get("menu_id"));
                                ModGlobal.runningTotalWhenEnabled.set(a , y);
                            }
                        }


                        ModGlobal.itemDetailsModelList.remove(position);
                        itemDetailsAdapter.notifyDataSetChanged();
                        computeTotal();

                        if(ModGlobal.itemDetailsModelList.isEmpty()){
                            ModGlobal.runningTotal.clear();
                            ModGlobal.runningTotalWhenEnabled.clear();
                            new Intent(CheckoutActivity.this , TableActivity.class);
                            finish();
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
                alert.show();*/
            }
        }));

        computeTotal();
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
                            menuModel.getImg() , menuModel.getName() , menuModel.getCat_id() , menuModel.getPosition()));

                } else {

                    ModGlobal.itemDetailsModelList.add(new ItemDetailsModel(menuModel.getProd_id(),
                            menuModel.getPrice(), Integer.parseInt(quantity.getText().toString()),
                            menuModel.getImg() , menuModel.getName() , menuModel.getCat_id() , menuModel.getPosition()));

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



        totalPrice.setText("₱" + dec.format(total));

    }
}
