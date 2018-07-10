package com.jik.irvin.restauapp;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by john on 5/6/2017.
 */

public class CashierMenuAdapter extends RecyclerView.Adapter<CashierMenuAdapter.MyViewHolder> {

    private Context mContext;
    private List<MenuModel> menuModels;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView , bestSeller;
        TextView title, price, discount;
        CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.thumbnail);
            title = view.findViewById(R.id.title);
            discount = view.findViewById(R.id.discount);
            price = view.findViewById(R.id.price);
            bestSeller = view.findViewById(R.id.bestSeller);
        }
    }

    public CashierMenuAdapter(Context mContext, List<MenuModel> menuModels) {
        this.mContext = mContext;
        this.menuModels = menuModels;
    }

    @Override
    public CashierMenuAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_list, parent, false);

        return new CashierMenuAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CashierMenuAdapter.MyViewHolder holder, int position) {

        if(menuModels.get(position).isBestSelling()){
            holder.bestSeller.setVisibility(View.VISIBLE);
        }else
            holder.bestSeller.setVisibility(View.GONE);

        if (menuModels.get(position).getName().length() > 13)
            holder.title.setText(menuModels.get(position).getName().substring(0 , 13));
        else
            holder.title.setText(menuModels.get(position).getName());


        DecimalFormat dec = new DecimalFormat("#,##0.00");

        holder.title.setText(menuModels.get(position).getName());
        holder.price.setText("₱" + dec.format(Double.parseDouble(menuModels.get(position).getPrice())));




        if (menuModels.get(position).isDiscounted())
            holder.discount.setText("(-" + menuModels.get(position).getLessPrice() + ")");
        else
            holder.discount.setText(null);


        if (!menuModels.get(position).getCat_id().equals("200")) {
            Glide.with(mContext).load(ModGlobal.baseURL + "uploads/products/" + menuModels.get(position).getImg()).into(holder.imageView);
        } else
            Glide.with(mContext).load(ModGlobal.baseURL + "uploads/packages/" + menuModels.get(position).getImg()).into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return menuModels.size();
    }


    public void updateList(List<MenuModel> list) {
        menuModels = list;
        notifyDataSetChanged();
    }

}