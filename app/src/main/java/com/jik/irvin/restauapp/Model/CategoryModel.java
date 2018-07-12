package com.jik.irvin.restauapp.Model;

/**
 * Created by wise01 on 5/27/2017.
 */

public class CategoryModel {

    public CategoryModel(String cat_id, String name, String descr, String encoded) {
        this.cat_id = cat_id;
        this.name = name;
        this.descr = descr;
        this.encoded = encoded;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
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

    public String getEncoded() {
        return encoded;
    }

    public void setEncoded(String encoded) {
        this.encoded = encoded;
    }

    private String cat_id;
    private String name;
    private String descr;
    private String encoded;




}
