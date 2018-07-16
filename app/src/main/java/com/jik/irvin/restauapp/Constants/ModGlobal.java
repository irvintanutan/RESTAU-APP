package com.jik.irvin.restauapp.Constants;

import com.jik.irvin.restauapp.Adapter.CashieringTableAdapter;
import com.jik.irvin.restauapp.Adapter.TableAdapter;
import com.jik.irvin.restauapp.Model.CategoryModel;
import com.jik.irvin.restauapp.Model.CompanyConfigModel;
import com.jik.irvin.restauapp.Model.ItemDetailsModel;
import com.jik.irvin.restauapp.Model.MenuModel;
import com.jik.irvin.restauapp.Model.TableModel;
import com.jik.irvin.restauapp.Model.TransactionModel;

import java.util.ArrayList;
import java.util.List;

public class ModGlobal {

    public static String baseURL = "http://172.20.10.9/RESTAU-APP-BACKEND/resto-app/";
    public static List<MenuModel> menuModelList = new ArrayList<>();
    public static List<MenuModel> menuModelListCopy = new ArrayList<>();
    public static List<CategoryModel> categoryModelList = new ArrayList<>();
    public static List<ItemDetailsModel> itemDetailsModelList = new ArrayList<>();
    public static List<TableModel> tableModelList = new ArrayList<>();
    public static List<TransactionModel> transactionModelList = new ArrayList<>();
    public static List<CompanyConfigModel> companyConfigModels = new ArrayList<>();

    public static String transactionId = "";
    public static String transType = "NORMAL";
    public static TableAdapter tableAdapter;
    public static CashieringTableAdapter cashieringTableAdapter;

    public static String printerSetup = "";

    public static ArrayList<Integer> tableId = new ArrayList<>();


    public static String getLessPrice(int prod_id){

        String less = "";

        for (MenuModel menuModel : menuModelList){

                if (menuModel.getProd_id() == prod_id){
                    less = menuModel.getLessPrice();
                }

        }

        return  less;
    }

}
