package com.jik.irvin.restauapp;

import java.util.ArrayList;
import java.util.List;

public class ModGlobal {

    public static String baseURL = "http://192.168.88.24/~jik/RESTAU-APP-BACKEND/resto-app/";
    public static List<MenuModel> menuModelList = new ArrayList<>();
    public static List<MenuModel> menuModelListCopy = new ArrayList<>();
    public static List<CategoryModel> categoryModelList = new ArrayList<>();
    public static List<ItemDetailsModel> itemDetailsModelList = new ArrayList<>();
    public static List<TableModel> tableModelList = new ArrayList<>();
    public static List<TransactionModel> transactionModelList = new ArrayList<>();

    public static String transactionId = "";
    public static String transType = "NORMAL";
    public static TableAdapter tableAdapter;

    public static ArrayList<Integer> tableId = new ArrayList<>();

    public static int width = 800 , height = 500;

}
