package com.jik.irvin.restauapp.Model;

public class TransactionModel {

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    private String transId;
    private String dateTime;
    private String table;
    private String total;


    public TransactionModel(String transId, String dateTime, String table, String total) {
        this.transId = transId;
        this.dateTime = dateTime;
        this.table = table;
        this.total = total;
    }
}
