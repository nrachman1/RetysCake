package com.example.cakerety;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cakerety.model.MyCartModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>{

    Context context;
    List<MyCartModel> cartModelList;
    int totalPrice = 0;

    public HistoryAdapter(Context context, List<MyCartModel> cartModelList) {
        this.context = context;
        this.cartModelList = cartModelList;
    }

    @NonNull
    @NotNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sucess_order_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HistoryAdapter.HistoryViewHolder holder, int position) {

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

    public class HistoryViewHolder extends RecyclerView.ViewHolder{

        TextView name,price,date,time,quantity,totalPrice;

        public HistoryViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.productNameHistory);
            price = itemView.findViewById(R.id.productPriceHistory);
            date = itemView.findViewById(R.id.currentDateHistory);
            quantity = itemView.findViewById(R.id.productQuantityHistory);
            totalPrice = itemView.findViewById(R.id.totalPriceCartHistory);
        }
    }
}
