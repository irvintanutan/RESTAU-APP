package com.jik.irvin.restauapp.Model;

public class TableModel {


    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String tableId;
    private String name;
    private String status;


    public TableModel(String tableId, String name, String status) {
        this.tableId = tableId;
        this.name = name;
        this.status = status;
    }




}
