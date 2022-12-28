package com.example.app01;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.example.app01.Common.Common;
import com.example.app01.ViewHolder.OrderDetailAdapter;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView order_detail_total;
    private String order_id_value="";
    private RecyclerView lstFoods;
    private RecyclerView.LayoutManager layoutManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        order_detail_total = findViewById(R.id.order_detail_total);

        lstFoods = findViewById(R.id.lstFoods);
        lstFoods.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lstFoods.setLayoutManager(layoutManager);

        if(getIntent() != null)
            order_id_value = getIntent().getStringExtra("OrderId");

        order_detail_total.setText("Tổng : "+Common.currentRequest.getTotal());

        OrderDetailAdapter adapter = new OrderDetailAdapter(Common.currentRequest.getFoodRequest());
        adapter.notifyDataSetChanged();
        lstFoods.setAdapter(adapter);
    }
}