package com.example.cakerety;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cakerety.model.MyCartModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HistoryAllCartAdapter extends RecyclerView.Adapter<HistoryAllCartAdapter.HistoryAllCartViewHolder>{

    Context context;
    List<MyCartModel> cartModelList;

    public HistoryAllCartAdapter(Context context, List<MyCartModel> cartModelList) {
        this.context = context;
        this.cartModelList = cartModelList;
    }

    @NonNull
    @NotNull
    @Override
    public HistoryAllCartViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.on_cart_all_user_item, parent, false);
        return new HistoryAllCartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HistoryAllCartAdapter.HistoryAllCartViewHolder holder, int position) {

        holder.name.setText(cartModelList.get(position).getProductName());
        holder.price.setText(cartModelList.get(position).getProductPrice());
        holder.date.setText(cartModelList.get(position).getCurrentDate());
        holder.quantity.setText(cartModelList.get(position).getTotalQuantity());
        holder.totalPrice.setText(String.valueOf(cartModelList.get(position).getTotalPrice()));

    }

    @Override
    public int getItemCount() {
        return cartModelList.size();
    }

    public static class HistoryAllCartViewHolder extends RecyclerView.ViewHolder{

        TextView name,price,date,time,quantity,totalPrice;

        public HistoryAllCartViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.productNameOnCartAllUser);
            price = itemView.findViewById(R.id.productPriceOnCartAllUser);
            date = itemView.findViewById(R.id.currentDateOnCartAllUser);
            quantity = itemView.findViewById(R.id.productQuantityOnCartAllUser);
            totalPrice = itemView.findViewById(R.id.totalPriceCartOnCartAllUser);

        }
    }
}
