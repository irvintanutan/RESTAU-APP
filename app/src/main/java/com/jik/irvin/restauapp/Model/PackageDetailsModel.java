package com.jik.irvin.restauapp.Model;

public class PackageDetailsModel {


    public String getProd_id() {
        return prod_id;
    }

    public void setProd_id(String prod_id) {
        this.prod_id = prod_id;
    }

    public String getProd_name() {
        return prod_name;
    }

    public void setProd_name(String prod_name) {
        this.prod_name = prod_name;
    }

    public String getProd_short_name() {
        return prod_short_name;
    }

    public void setProd_short_name(String prod_short_name) {
        this.prod_short_name = prod_short_name;
    }

    public String getProd_qty() {
        return prod_qty;
    }

    public void setProd_qty(String prod_qty) {
        this.prod_qty = prod_qty;
    }

    public String getPackage_id() {
        return package_id;
    }

    public void setPackage_id(String package_id) {
        this.package_id = package_id;
    }

    private String prod_id;
    private String prod_name;
    private String prod_short_name;
    private String package_id;
    private String prod_qty;

    public PackageDetailsModel(String prod_id, String prod_name, String prod_short_name, String prod_qty ,String package_id) {
        this.prod_id = prod_id;
        this.prod_name = prod_name;
        this.prod_short_name = prod_short_name;
        this.prod_qty = prod_qty;
        this.package_id = package_id;
    }
}
