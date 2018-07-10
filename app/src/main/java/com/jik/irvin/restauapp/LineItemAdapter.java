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

public class LineItemAdapter extends RecyclerView.Adapter<LineItemAdapter.MyViewHolder> {

    private Context mContext;
    private List<ItemDetailsModel> itemDetailsModels;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, price, total , quantity;


        public MyViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.title);
            price = view.findViewById(R.id.price);
            total = view.findViewById(R.id.totalPrice);
            quantity = view.findViewById(R.id.quantity);


        }
    }

    public LineItemAdapter(Context mContext, List<ItemDetailsModel> itemDetailsModels) {
        this.mContext = mContext;
        this.itemDetailsModels = itemDetailsModels;
    }

    @Override
    public LineItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.line_item, parent, false);

        return new LineItemAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final LineItemAdapter.MyViewHolder holder, int position) {


        DecimalFormat dec=new DecimalFormat("#,##0.00");

        holder.quantity.setText(" X " + itemDetailsModels.get(position).getMenuQty());
        holder.total.setText("₱" + dec.format(Double.parseDouble(itemDetailsModels.get(position).getMenuPrice())*
                                 itemDetailsModels.get(position).getMenuQty()));
        holder.price.setText(itemDetailsModels.get(position).getMenuPrice());
        holder.title.setText(itemDetailsModels.get(position).getShortName());

    }


    @Override
    public int getItemCount() {
        return itemDetailsModels.size();
    }




    public void updateList(List<ItemDetailsModel> list) {
        itemDetailsModels = list;
        notifyDataSetChanged();
    }

}