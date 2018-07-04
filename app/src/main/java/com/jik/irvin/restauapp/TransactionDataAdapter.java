package com.jik.irvin.restauapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

/**
 * Created by irvin on 2/2/17.
 */
public class TransactionDataAdapter extends RecyclerView.Adapter<TransactionDataAdapter.ViewHolder> {

    private Context c;
    private List<TransactionModel> transactionModels;

    public TransactionDataAdapter(List<TransactionModel> transactionModels) {
        this.transactionModels = transactionModels;

    }

    @Override
    public TransactionDataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.transaction_card_view, viewGroup, false);
        return new TransactionDataAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TransactionDataAdapter.ViewHolder viewHolder, int i) {

        String clientName = "";

        viewHolder.clientCode.setText(transactionModels.get(i).getTransId());
        viewHolder.clientName.setText(transactionModels.get(i).getTable());
        viewHolder.eventType.setText("Php" +transactionModels.get(i).getTotal());
        viewHolder.timeStamp.setText(transactionModels.get(i).getDateTime());

    }

    @Override
    public int getItemCount() {
        return transactionModels.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView clientName, clientCode, timeStamp, eventType;

        public ViewHolder(View view) {
            super(view);

            clientName = view.findViewById(R.id.clientName);
            clientCode = view.findViewById(R.id.clientCode);
            timeStamp = view.findViewById(R.id.timeStamp);
            eventType = view.findViewById(R.id.eventType);

        }
    }

}