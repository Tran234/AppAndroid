package com.example.app01.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.app01.Model.Request;
import com.example.app01.Model.User;
import com.example.app01.Remote.APIService;
import com.example.app01.Remote.RetrofitClient;

public class Common {

    public static User currentUser;

    public static Request currentRequest;

    public static final String DELETE = "Xóa";

    public static boolean ConnectedToInter(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null){
            NetworkInfo [] infos = connectivityManager.getAllNetworkInfo();
            if (infos != null){
                for (int i=0; i<infos.length;i++){
                    if (infos[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    private static final String BASE_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMService(){
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
