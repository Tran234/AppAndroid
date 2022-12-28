package com.example.myapplication.Common;


import com.example.myapplication.Model.Admin;
import com.example.myapplication.Model.Request;

public class Common {
    public static Admin currentAdmin;

    public static Request currentRequest;

    public static final String UPDATE = "Cập nhật";
    public static final String DELETE = "Xóa";

    public static String convertCodeToStatus(String code){
        if (code.equals("0"))
            return "Placed";
        else if (code.equals("1"))
            return "On my way";
        else
            return "Shipped";
    }
}
