package com.example.cakerety;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cakerety.model.Cake;
import com.example.cakerety.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DataAllUserAdapter extends RecyclerView.Adapter<DataAllUserAdapter.DataAllUserViewHolder>{

    private DataAllUserActivity activity;
    private List<User> uList;

    private String userId;

    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    private String TAG = "AdapterAllUser";

    public DataAllUserAdapter(DataAllUserActivity activity, List<User> uList) {
        this.activity = activity;
        this.uList = uList;
    }

    @NonNull
    @NotNull
    @Override
    public DataAllUserViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.user_data_item, parent, false);
        return new DataAllUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DataAllUserAdapter.DataAllUserViewHolder holder, int position) {

        holder.fullName.setText(uList.get(position).getFullName());
        holder.email.setText(String.valueOf(uList.get(position).getEmail()));
        holder.alamat.setText(String.valueOf(uList.get(position).getAddress()));
        holder.phoneNum.setText(String.valueOf(uList.get(position).getPhone()));

        int type = uList.get(position).getType();

        if (type == 0){
            holder.type.setText("Customer");
        }else {
            holder.type.setText("Admin");
        }

        User user = uList.get(position);
        String imageUri = null;
        imageUri = user.getUrl();
        Picasso.get().load(imageUri).into(holder.userImage);

    }

    @Override
    public int getItemCount() {
        return uList.size();
    }

    public static class DataAllUserViewHolder extends RecyclerView.ViewHolder{

        TextView fullName, email, alamat, phoneNum, type;
        ImageView userImage;

        public DataAllUserViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            fullName = itemView.findViewById(R.id.fullNameAlluser);
            email = itemView.findViewById(R.id.emailAllUser);
            alamat = itemView.findViewById(R.id.addressAllUser);
            phoneNum = itemView.findViewById(R.id.phoneNumAllUser);
            userImage = itemView.findViewById(R.id.imageAllUser);
            type = itemView.findViewById(R.id.typeAllUser);
        }
    }
}
