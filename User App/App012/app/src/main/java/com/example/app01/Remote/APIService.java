package com.example.app01.Remote;


import com.example.app01.Model.MyResponse;
import com.example.app01.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAPM3DzlE:APA91bEdoaxO432XLqnPC7zEZ3rspAAaggJ-PJbOdYjZ6-My6IN5i1E1R0x1XYT4CMmHUqJ37ENk8Hk0SD4AFkWSVTJSSBjEQi7JOHwJ72xi_wgyvox-2lsReV0MSci0eVPAp7CEVoco"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
