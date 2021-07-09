package com.example.cakerety;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cakerety.model.Cake;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HoricakeAdapter extends RecyclerView.Adapter<HoricakeAdapter.HoriCakeViewHolder>{

    private List<Cake> cList;
    private Context context;

    public HoricakeAdapter(Context context ,List<Cake> cList){
        this.context = context;
        this.cList = cList;
    }

    @NonNull
    @NotNull
    @Override
    public HoriCakeViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_cake_row, parent, false);
        return new HoriCakeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HoricakeAdapter.HoriCakeViewHolder holder, int position) {
        holder.cakeName.setText(cList.get(position).getCakeName());
        holder.price.setText(String.valueOf(cList.get(position).getPrice()));

        Cake cake = cList.get(position);
        String imageUri = null;
        imageUri = cake.getPathImage();
        Picasso.get().load(imageUri).into(holder.imageCake);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, DetailProductActivity.class);
                intent.putExtra("detailCake", cList.get(position));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return cList.size();
    }

    public static class HoriCakeViewHolder extends RecyclerView.ViewHolder{

        TextView cakeName, price;
        ImageView imageCake;
        public HoriCakeViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            cakeName = itemView.findViewById(R.id.cakeName);
            price = itemView.findViewById(R.id.price);
            imageCake = itemView.findViewById(R.id.cakeImage);
        }
    }
}
