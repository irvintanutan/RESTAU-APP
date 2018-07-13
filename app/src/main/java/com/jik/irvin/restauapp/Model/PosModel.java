package com.jik.irvin.restauapp.Model;

public class PosModel {


    private String pos_id;
    private String last_receipt_number;


    public PosModel(String pos_id, String last_receipt_number) {
        this.pos_id = pos_id;
        this.last_receipt_number = last_receipt_number;
    }

    public String getPos_id() {
        return pos_id;
    }

    public void setPos_id(String pos_id) {
        this.pos_id = pos_id;
    }

    public String getLast_receipt_number() {
        return last_receipt_number;
    }

    public void setLast_receipt_number(String last_receipt_number) {
        this.last_receipt_number = last_receipt_number;
    }

}
