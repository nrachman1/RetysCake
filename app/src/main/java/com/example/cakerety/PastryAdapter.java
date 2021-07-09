package com.example.cakerety;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cakerety.model.Cake;
import com.example.cakerety.model.Pastry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PastryAdapter extends RecyclerView.Adapter<PastryAdapter.PastryViewHolder>{

    private ViewPastry activity;
    private List<Pastry> pList;

    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    public PastryAdapter(ViewPastry activity, List<Pastry> pList){
        this.activity = activity;
        this.pList = pList;
    }

    @NonNull
    @NotNull
    @Override
    public PastryViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.pastry, parent, false);
        return new PastryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PastryAdapter.PastryViewHolder holder, int position) {
        holder.pastryName.setText(pList.get(position).getPastryName());
        holder.priceSmall.setText(String.valueOf(pList.get(position).getPriceSmall()));
        holder.priceBig.setText(String.valueOf(pList.get(position).getPriceBig()));

//        Glide.with(context).load(cList.get(position).getPathImage()).into(holder.imageCake);
        Pastry pastry = pList.get(position);
        String imageUri = null;
        imageUri = pastry.getPathImage();
        Picasso.get().load(imageUri).into(holder.imagePastry);
    }

    public void updateData(int position){
        Pastry item = pList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("uId", item.getId());
        bundle.putString("uPastryName", item.getPastryName());
        bundle.putLong("uPriceSmall", item.getPriceSmall());
        bundle.putLong("uPriceBig", item.getPriceBig());
        bundle.putString("uPathImage", item.getPathImage());

        Intent intent = new Intent(activity, InputPastry.class);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public void deletedData(int position){
        Pastry item = pList.get(position);

        fStore.collection("pastrys").document(item.getId()).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(activity, "Data Deleted !!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void notifyRemoved(int position){
        pList.remove(position);
        notifyItemRemoved(position);
        activity.showData();
    }

    @Override
    public int getItemCount() {
        return pList.size();
    }

    public static class PastryViewHolder extends RecyclerView.ViewHolder{

        TextView pastryName, priceSmall, priceBig;
        ImageView imagePastry;
        public PastryViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            pastryName = itemView.findViewById(R.id.pastryName);
            priceSmall = itemView.findViewById(R.id.priceSmall);
            priceBig = itemView.findViewById(R.id.priceBig);
            imagePastry = itemView.findViewById(R.id.imagePastry);
        }
    }
}
