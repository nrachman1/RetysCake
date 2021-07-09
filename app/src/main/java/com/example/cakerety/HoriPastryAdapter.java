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
import com.example.cakerety.model.Pastry;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HoriPastryAdapter extends RecyclerView.Adapter<HoriPastryAdapter.HoriPastryViewHolder>{

    private List<Pastry> pList;
    private Context context;

    public HoriPastryAdapter(List<Pastry> pList, Context context) {
        this.pList = pList;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public HoriPastryViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_pastry_row, parent, false);
        return new HoriPastryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HoriPastryAdapter.HoriPastryViewHolder holder, int position) {

        holder.pastryName.setText(pList.get(position).getPastryName());
        holder.priceSmall.setText(String.valueOf(pList.get(position).getPriceSmall()));
        holder.priceBig.setText(String.valueOf(pList.get(position).getPriceBig()));

        Pastry pastry = pList.get(position);
        String imageUri = null;
        imageUri = pastry.getPathImage();
        Picasso.get().load(imageUri).into(holder.imagePastry);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, DetailPastryActivity.class);
                intent.putExtra("detailPastry", pList.get(position));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return pList.size();
    }


    public static class HoriPastryViewHolder extends RecyclerView.ViewHolder{

        TextView pastryName, priceSmall, priceBig;
        ImageView imagePastry;
        public HoriPastryViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            pastryName = itemView.findViewById(R.id.pastryNameRow);
            priceSmall = itemView.findViewById(R.id.priceSmallRow);
            priceBig = itemView.findViewById(R.id.priceBigRow);
            imagePastry = itemView.findViewById(R.id.pastryImageRow);
        }
    }
}
