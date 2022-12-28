package com.example.myapplication.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Common.Common;
import com.example.myapplication.Interface.ItemClickListener;
import com.example.myapplication.R;

public class BannerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        ,View.OnCreateContextMenuListener {

    public TextView txtBannerName;
    public ImageView imageView;
    private ItemClickListener itemClickListener;

    public BannerViewHolder(@NonNull View itemView) {
        super(itemView);

        txtBannerName = itemView.findViewById(R.id.banner_name);
        imageView = itemView.findViewById(R.id.banner_image);

        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(),false );
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Mời bạn chọn");
        contextMenu.add(0,0,getAdapterPosition(),Common.DELETE);
    }
}
