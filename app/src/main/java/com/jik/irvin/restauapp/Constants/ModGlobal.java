package com.jik.irvin.restauapp.Constants;

import com.jik.irvin.restauapp.Adapter.CashieringTableAdapter;
import com.jik.irvin.restauapp.Adapter.TableAdapter;
import com.jik.irvin.restauapp.Model.CategoryModel;
import com.jik.irvin.restauapp.Model.CompanyConfigModel;
import com.jik.irvin.restauapp.Model.DiscountModel;
import com.jik.irvin.restauapp.Model.ItemDetailsModel;
import com.jik.irvin.restauapp.Model.MenuModel;
import com.jik.irvin.restauapp.Model.PackageDetailsModel;
import com.jik.irvin.restauapp.Model.TableModel;
import com.jik.irvin.restauapp.Model.TransactionModel;
import com.jik.irvin.restauapp.Model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class ModGlobal {

    public static String baseURL = "http://192.168.88.27/RESTAU-APP-BACKEND/resto-app/";
    //public static String baseURL = "http://192.168.99.103/RESTAU-APP-BACKEND/resto-app/";
    public static List<MenuModel> menuModelList = new ArrayList<>();
    public static List<MenuModel> menuModelListCopy = new ArrayList<>();
    public static List<CategoryModel> categoryModelList = new ArrayList<>();
    public static List<ItemDetailsModel> itemDetailsModelList = new ArrayList<>();
    public static List<TableModel> tableModelList = new ArrayList<>();
    public static List<TransactionModel> transactionModelList = new ArrayList<>();
    public static List<CompanyConfigModel> companyConfigModels = new ArrayList<>();
    public static List<PackageDetailsModel> packageDetailsModelList = new ArrayList<>();
    public static List<UserModel> userModelList = new ArrayList<>();
    public static List<DiscountModel> discountModelList = new ArrayList<>();
    public static String receiptNumber = "";
    public static String orderType = "";


    public static UserModel userModel = null;

    public static String transactionId = "";
    public static String transType = "NORMAL";
    public static int isBillOutPrinted = 0;
    public static TableAdapter tableAdapter;
    public static CashieringTableAdapter cashieringTableAdapter;

    public static double discount = 0.00;
    public static int discType = 0;
    public static String discountLabel = "";

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
