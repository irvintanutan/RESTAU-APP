package com.jik.irvin.restauapp.Model;

/**
 * Created by john on 5/6/2017.
 */

public class MenuModel {


    private int prod_id;
    private String name;
    private String descr;
    private String cat_id;
    private String cat_name;
    private String price;
    private String sold;
    private String encoded;
    private String img;
    private int item_count;
    private int position;
    private String lessPrice;
    private boolean isDiscounted;
    private String shortName;
    private boolean isBestSelling;
    private String rank;


    public MenuModel(int prod_id, String name, String descr, String cat_id, String cat_name, String price,
                     String sold, String encoded, String img, int item_count, int position, String lessPrice, boolean isDiscounted, String shortName, boolean isBestSelling, String rank) {
        this.prod_id = prod_id;
        this.name = name;
        this.descr = descr;
        this.cat_id = cat_id;
        this.cat_name = cat_name;
        this.price = price;
        this.sold = sold;
        this.encoded = encoded;
        this.img = img;
        this.item_count = item_count;
        this.position = position;
        this.lessPrice = lessPrice;
        this.isDiscounted = isDiscounted;
        this.shortName = shortName;
        this.isBestSelling = isBestSelling;
        this.rank = rank;
    }

    public int getProd_id() {
        return prod_id;
    }

    public void setProd_id(int prod_id) {
        this.prod_id = prod_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSold() {
        return sold;
    }

    public void setSold(String sold) {
        this.sold = sold;
    }

    public String getEncoded() {
        return encoded;
    }

    public void setEncoded(String encoded) {
        this.encoded = encoded;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getItem_count() {
        return item_count;
    }

    public void setItem_count(int item_count) {
        this.item_count = item_count;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
    public String getLessPrice() {
        return lessPrice;
    }

    public void setLessPrice(String lessPrice) {
        this.lessPrice = lessPrice;
    }

    public boolean isDiscounted() {
        return isDiscounted;
    }

    public void setDiscounted(boolean discounted) {
        isDiscounted = discounted;
    }


    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }


    public boolean isBestSelling() {
        return isBestSelling;
    }

    public void setBestSelling(boolean bestSelling) {
        isBestSelling = bestSelling;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }


}



