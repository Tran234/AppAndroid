package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.example.myapplication.Common.Common;
import com.example.myapplication.ViewHolder.OrderDetailAdapter;

public class OrderDetailActivity extends AppCompatActivity {
    private TextView order_detail_id, order_detail_name, order_detail_phone, order_detail_address, order_detail_total, order_detail_paymentMethod, order_detail_paymentState, order_detail_status, order_detail_comment, order_detail_date, order_detail_time;
    private String order_id_value="";
    private RecyclerView lstFoods;
    private RecyclerView.LayoutManager layoutManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        order_detail_id = findViewById(R.id.order_detail_id);
        order_detail_name = findViewById(R.id.order_detail_name);
        order_detail_phone = findViewById(R.id.order_detail_phone);
        order_detail_address = findViewById(R.id.order_detail_address);
        order_detail_total = findViewById(R.id.order_detail_total);
        order_detail_paymentMethod = findViewById(R.id.order_detail_paymentMethod);
        order_detail_paymentState = findViewById(R.id.order_detail_paymentState);
        order_detail_status = findViewById(R.id.order_detail_status);
        order_detail_comment = findViewById(R.id.order_detail_comment);
        order_detail_date = findViewById(R.id.order_detail_date);
        order_detail_time = findViewById(R.id.order_detail_time);

        lstFoods = findViewById(R.id.lstFoods);
        lstFoods.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lstFoods.setLayoutManager(layoutManager);

        if(getIntent() != null)
            order_id_value = getIntent().getStringExtra("OrderId");

        order_detail_id.setText(order_id_value);
        order_detail_name.setText("Tên: "+Common.currentRequest.getName());
        order_detail_phone.setText("Điện thoại : "+Common.currentRequest.getPhone());
        order_detail_address.setText("Địa chỉ : "+Common.currentRequest.getAddress());
        order_detail_total.setText("Tổng tiền : "+Common.currentRequest.getTotal());
        order_detail_paymentMethod.setText("Phương thức thanh toán: "+Common.currentRequest.getPaymentMethod());
        order_detail_paymentState.setText("Trạng thái: "+Common.currentRequest.getPaymentState());
        order_detail_status.setText("Phương thức nhận: "+Common.currentRequest.getStatus());
        order_detail_comment.setText("Ghi chú: "+Common.currentRequest.getComment());
        order_detail_date.setText("Ngày : "+Common.currentRequest.getDate());
        order_detail_time.setText("Giờ : "+Common.currentRequest.getTime());

        OrderDetailAdapter adapter = new OrderDetailAdapter(Common.currentRequest.getFoodRequest());
        adapter.notifyDataSetChanged();
        lstFoods.setAdapter(adapter);

    }
}