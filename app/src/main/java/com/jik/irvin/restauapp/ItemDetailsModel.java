package com.jik.irvin.restauapp;

public class ItemDetailsModel {

    private String menuName;
    private int prodID;
    private String menuPrice;
    private int menuQty;
    private String catID;
    private String url;
    private int position;


    public ItemDetailsModel(int menuID, String menuPrice, int menuQty, String url, String menuName, String catID, int position) {
        this.prodID = menuID;
        this.menuPrice = menuPrice;
        this.menuQty = menuQty;
        this.url = url;
        this.menuName = menuName;
        this.catID = catID;
        this.position = position;

    }

    public int getProdID() {
        return prodID;
    }

    public void setProdID(int menuID) {
        this.prodID = menuID;
    }

    public String getMenuPrice() {
        return menuPrice;
    }

    public void setMenuPrice(String menuPrice) {
        this.menuPrice = menuPrice;
    }

    public int getMenuQty() {
        return menuQty;
    }

    public void setMenuQty(int menuQty) {
        this.menuQty = menuQty;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getCatID() {
        return catID;
    }

    public void setCatID(String catID) {
        this.catID = catID;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
