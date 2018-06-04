package com.jik.irvin.restauapp;

import android.content.Context;
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
 * Created by wise01 on 5/13/2017.
 */

public class ItemDetailsAdapter extends RecyclerView.Adapter<ItemDetailsAdapter.MyViewHolder> {

    private Context mContext;
    private List<ItemDetailsModel> itemDetailsModels;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView title, price;


        public MyViewHolder(View view) {
            super(view);

            imageView = view.findViewById(R.id.thumbnail);
            title = view.findViewById(R.id.title);
            price = view.findViewById(R.id.price);

        }
    }

    public ItemDetailsAdapter(Context mContext, List<ItemDetailsModel> itemDetailsModels) {
        this.mContext = mContext;
        this.itemDetailsModels = itemDetailsModels;
    }

    @Override
    public ItemDetailsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_list2, parent, false);

        return new ItemDetailsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ItemDetailsAdapter.MyViewHolder holder, int position) {


        DecimalFormat dec=new DecimalFormat("#,##0.00");

        holder.price.setText("QTY : " + itemDetailsModels.get(position).getMenuQty()
                            + "     TOTAL :  ₱" + dec.format(Double.parseDouble(itemDetailsModels.get(position).getMenuPrice())*
                                 itemDetailsModels.get(position).getMenuQty()));

        holder.title.setText(itemDetailsModels.get(position).getMenuName());

        if (!itemDetailsModels.get(position).getCatID().equals("200")) {
            Glide.with(mContext).load(ModGlobal.baseURL + "uploads/products/" + itemDetailsModels.get(position).getUrl()).into(holder.imageView);
        }else
            Glide.with(mContext).load(ModGlobal.baseURL + "uploads/packages/" + itemDetailsModels.get(position).getUrl()).into(holder.imageView);

    }


    @Override
    public int getItemCount() {
        return itemDetailsModels.size();
    }


}