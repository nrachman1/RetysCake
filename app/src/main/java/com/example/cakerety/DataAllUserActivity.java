package com.example.cakerety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.cakerety.model.Cake;
import com.example.cakerety.model.Pastry;
import com.example.cakerety.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataAllUserActivity extends AppCompatActivity {

    private RecyclerView recyclerViewDataAlluser;

    private DataAllUserAdapter adapter;
    private List<User> list;

    private FirebaseFirestore fStore;
    private StorageReference storageReference;

    private String TAG = "ViewPastry";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_all_user);

        recyclerViewDataAlluser = findViewById(R.id.dataAllUser);
        recyclerViewDataAlluser.setHasFixedSize(true);
        recyclerViewDataAlluser.setLayoutManager(new LinearLayoutManager(this));

        fStore = FirebaseFirestore.getInstance();

        list = new ArrayList<>();
        adapter = new DataAllUserAdapter(this, list);
        recyclerViewDataAlluser.setAdapter(adapter);
        storageReference = FirebaseStorage.getInstance().getReference();


        showData();
    }

    private void showData() {
        fStore.collection("users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> listt = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot snapshot : listt) {

                                String pathImage = "users/"+snapshot.getId()+"/Profile";

                                storageReference.child(pathImage).getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                list.add(new User(snapshot.getString("fullName"), snapshot.getString("email"), snapshot.getString("address"), snapshot.getString("phoneNum"), uri.toString() ,snapshot.getLong("type").intValue()));
                                                adapter.notifyDataSetChanged();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        String urlFromInter = "https://i.postimg.cc/fyF59V0w/profile.jpg";
                                        list.add(new User(snapshot.getString("fullName"), snapshot.getString("email"), snapshot.getString("address"), snapshot.getString("phoneNum"), urlFromInter ,snapshot.getLong("type").intValue()));
                                        Log.d(TAG, "OnFailed: failed show image ");
                                    }
                                });
                            }
                        }
                    }
                });
    }
}