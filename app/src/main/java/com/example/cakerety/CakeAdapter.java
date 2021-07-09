package com.example.cakerety;

import android.content.Context;
import android.content.Intent;
import android.graphics.ColorSpace;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CakeAdapter extends RecyclerView.Adapter<CakeAdapter.CakeViewHolder> {
    private ViewCake activity;
    private List<Cake> cList;

    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    public CakeAdapter(ViewCake activity, List<Cake> cList){
        this.activity = activity;
        this.cList = cList;
    }
    @NonNull
    @NotNull
    @Override
    public CakeViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.cake, parent, false);
        return new CakeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CakeAdapter.CakeViewHolder holder, int position) {
        holder.cakeName.setText(cList.get(position).getCakeName());
        holder.price.setText(String.valueOf(cList.get(position).getPrice()));

//        Glide.with(context).load(cList.get(position).getPathImage()).into(holder.imageCake);
        Cake cake = cList.get(position);
        String imageUri = null;
        imageUri = cake.getPathImage();
        Picasso.get().load(imageUri).into(holder.imageCake);



    }

    public void updateData(int position){
        Cake item = cList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("uId", item.getId());
        bundle.putString("uCakeName", item.getCakeName());
        bundle.putLong("uPrice", item.getPrice());
        bundle.putString("uPathImage", item.getPathImage());

        Intent intent = new Intent(activity, InputCake.class);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public void deletedData(int position){
        Cake item = cList.get(position);

        fStore.collection("cake").document(item.getId()).delete()
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
        cList.remove(position);
        notifyItemRemoved(position);
        activity.showData();
    }

    @Override
    public int getItemCount() {
        return cList.size();
    }

    public static class CakeViewHolder extends RecyclerView.ViewHolder{


        TextView cakeName, price;
        ImageView imageCake;
        public CakeViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            cakeName = itemView.findViewById(R.id.nameCake);
            price = itemView.findViewById(R.id.price);
            imageCake = itemView.findViewById(R.id.imageCake);

        }
    }
}
