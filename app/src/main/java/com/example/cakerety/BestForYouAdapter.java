package com.example.cakerety;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cakerety.model.Cake;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BestForYouAdapter extends RecyclerView.Adapter<BestForYouAdapter.BestForYouViewHolder>{

    private Context context;
    private List<Cake> bestForYouList;

    @NonNull
    @NotNull
    @Override
    public BestForYouViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BestForYouAdapter.BestForYouViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return bestForYouList.size();
    }

    public static class BestForYouViewHolder extends RecyclerView.ViewHolder{

        public BestForYouViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}
