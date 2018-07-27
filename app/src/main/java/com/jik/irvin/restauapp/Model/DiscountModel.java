package com.jik.irvin.restauapp.Model;

public class DiscountModel {

    private int discId;
    private String name;
    private String desc;
    private double less_p;
    private double less_c;


    public DiscountModel(int discId, String name, String desc, double less_p, double less_c) {
        this.discId = discId;
        this.name = name;
        this.desc = desc;
        this.less_p = less_p;
        this.less_c = less_c;
    }


    public int getDiscId() {
        return discId;
    }

    public void setDiscId(int discId) {
        this.discId = discId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getLess_p() {
        return less_p;
    }

    public void setLess_p(double less_p) {
        this.less_p = less_p;
    }

    public double getLess_c() {
        return less_c;
    }

    public void setLess_c(double less_c) {
        this.less_c = less_c;
    }
}
