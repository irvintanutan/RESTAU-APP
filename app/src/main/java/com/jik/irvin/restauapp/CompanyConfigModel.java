package com.jik.irvin.restauapp;

public class CompanyConfigModel {


    private String name;
    private String address;
    private String city;
    private String tin;
    private String vat;
    private String bs_price;
    private String img_comp;


    public CompanyConfigModel(String name, String address, String city, String tin, String vat, String bs_price, String img_comp) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.tin = tin;
        this.vat = vat;
        this.bs_price = bs_price;
        this.img_comp = img_comp;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public String getBs_price() {
        return bs_price;
    }

    public void setBs_price(String bs_price) {
        this.bs_price = bs_price;
    }

    public String getImg_comp() {
        return img_comp;
    }

    public void setImg_comp(String img_comp) {
        this.img_comp = img_comp;
    }

}
