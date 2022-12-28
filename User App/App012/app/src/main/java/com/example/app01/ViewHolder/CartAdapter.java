package com.example.app01.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.app01.Common.Common;
import com.example.app01.Interface.ItemClickListener;
import com.example.app01.Model.Order;
import com.example.app01.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {

    private List<Order> listData = new ArrayList<>();
    private Context context;

    public CartAdapter(List<Order> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_layout, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(""+listData.get(position).getQuantity(), Color.RED);
        holder.imgCartCount.setImageDrawable(drawable);

        Locale locale = new Locale("vi", "VN");
        //Locale locale = new Locale("en", "US");
        //NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        DecimalFormat fmt = (DecimalFormat) DecimalFormat.getCurrencyInstance(locale);
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setCurrencySymbol("VND");
        fmt.setDecimalFormatSymbols(formatSymbols);

        int price = (Integer.parseInt(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuantity()));

        holder.txtPrice.setText(fmt.format(price));
        holder.txtCartName.setText(listData.get(position).getProductName());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public Order getItem(int postion){
        return listData.get(postion);
    }

    public void removeItem (int position){
        listData.remove(position);
        notifyItemChanged(position);
    }

    public void restoreItem (Order item,int position){
        listData.add(position,item);
        notifyItemInserted(position);
    }
}
