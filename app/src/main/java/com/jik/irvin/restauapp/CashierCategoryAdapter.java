package com.jik.irvin.restauapp;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by john on 5/6/2017.
 */

public class CashierCategoryAdapter extends RecyclerView.Adapter<CashierCategoryAdapter.MyViewHolder> {

    private Context mContext;
    private List<CategoryModel> categoryModels;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView tableName;

        public MyViewHolder(View view) {
            super(view);

            cv = view.findViewById(R.id.cv);
            tableName = view.findViewById(R.id.table_name);

        }
    }

    public CashierCategoryAdapter(Context mContext, List<CategoryModel> categoryModels) {
        this.mContext = mContext;
        this.categoryModels = categoryModels;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cashier_category_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.tableName.setText(categoryModels.get(position).getName());

    }


    @Override
    public int getItemCount() {
        return categoryModels.size();
    }
}