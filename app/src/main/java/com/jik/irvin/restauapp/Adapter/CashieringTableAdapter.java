package com.jik.irvin.restauapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jik.irvin.restauapp.Model.TableModel;
import com.jik.irvin.restauapp.R;

import java.util.List;

/**
 * Created by john on 5/6/2017.
 */

public class CashieringTableAdapter extends RecyclerView.Adapter<CashieringTableAdapter.MyViewHolder> {

    private Context mContext;
    private List<TableModel> tableModelList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView tableName;

        public MyViewHolder(View view) {
            super(view);

            cv = view.findViewById(R.id.cv);
            tableName = view.findViewById(R.id.table_name);

        }
    }

    public CashieringTableAdapter(Context mContext, List<TableModel> categoryModels) {
        this.mContext = mContext;
        this.tableModelList = categoryModels;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.tableName.setText(tableModelList.get(position).getName());

            holder.cv.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));

    }


    @Override
    public int getItemCount() {
        return tableModelList.size();
    }
}