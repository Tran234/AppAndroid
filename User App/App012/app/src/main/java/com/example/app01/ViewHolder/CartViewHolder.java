package com.example.app01.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app01.Common.Common;
import com.example.app01.Interface.ItemClickListener;
import com.example.app01.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnCreateContextMenuListener {

    public TextView txtCartName, txtPrice;
    public ImageView imgCartCount;
    private ItemClickListener itemClickListener;
    public RelativeLayout view_background;
    public LinearLayout view_foreground;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setTxtCartName(TextView txtCartName) {
        this.txtCartName = txtCartName;
    }


    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        txtCartName = itemView.findViewById(R.id.cart_item_name);
        txtPrice = itemView.findViewById(R.id.cart_item_price);
        imgCartCount = itemView.findViewById(R.id.cart_item_count);
        //view_background = itemView.findViewById(R.id.view_background_delete);
        //view_foreground = itemView.findViewById(R.id.view_foreground);

        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Mời bạn chọn:");
        contextMenu.add(0,0,getAdapterPosition(),Common.DELETE);
    }
}
