package com.example.myapplication.ViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Order;
import com.example.myapplication.R;

import java.util.List;

class MyViewHolder extends RecyclerView.ViewHolder{

    public TextView name,quantity,price,discount;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.product_name);
        quantity = itemView.findViewById(R.id.product_quantity);
        price = itemView.findViewById(R.id.product_price);
        discount = itemView.findViewById(R.id.product_discount);
    }
}

public class OrderDetailAdapter extends RecyclerView.Adapter<MyViewHolder>{

    List<Order> myOrders;

    public OrderDetailAdapter(List<Order> myOrders) {
        this.myOrders = myOrders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order order = myOrders.get(position);
        holder.name.setText(String.format("Món : %s",order.getProductName()));
        holder.quantity.setText(String.format("Số lượng : %s",order.getQuantity()));
        holder.price.setText(String.format("Tiền : %s",order.getPrice()));
        holder.discount.setText(String.format("Miễn giảm : %s",order.getDiscount()));
    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }
}
