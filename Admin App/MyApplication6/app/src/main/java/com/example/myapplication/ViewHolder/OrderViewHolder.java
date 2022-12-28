package com.example.myapplication.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Interface.ItemClickListener;
import com.example.myapplication.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    ,View.OnCreateContextMenuListener{

    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress, txtPaymentState,txtDate, txtTime;
    private ItemClickListener itemClickListener;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderId = itemView.findViewById(R.id.order_id);
        txtOrderStatus = itemView.findViewById(R.id.order_status);
        txtOrderPhone = itemView.findViewById(R.id.order_phone);
        txtOrderAddress = itemView.findViewById(R.id.order_address);
        txtPaymentState = itemView.findViewById(R.id.order_paymentState);
        txtDate = itemView.findViewById(R.id.order_date);
        txtTime = itemView.findViewById(R.id.order_time);

        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),true);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Mời bạn chọn");

        contextMenu.add(0,0,getAdapterPosition(),"Xóa");
        //contextMenu.add(0,1,getAdapterPosition(),"Xem");

    }

}
