package com.example.cakerety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cakerety.model.Cake;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ViewCake extends AppCompatActivity {

    private RecyclerView recyclerViewCake;
    private Button buttonInput;

    private FirebaseFirestore fStore;

    private CakeAdapter adapter;
    private List<Cake> list;

    private StorageReference storageReference;

    private String TAG = "ViewCake";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cake);

        recyclerViewCake = findViewById(R.id.recyclerviewCake);
        recyclerViewCake.setHasFixedSize(true);
        recyclerViewCake.setLayoutManager(new LinearLayoutManager(this));
        buttonInput = findViewById(R.id.input);

        fStore = FirebaseFirestore.getInstance();

        list = new ArrayList<>();
        adapter = new CakeAdapter(this, list);
        recyclerViewCake.setAdapter(adapter);
        storageReference = FirebaseStorage.getInstance().getReference();

        ItemTouchHelper touchHelper = new ItemTouchHelper(new TouchHelperCake(adapter));
        touchHelper.attachToRecyclerView(recyclerViewCake);

        showData();

        buttonInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), InputCake.class));
            }
        });
    }

    public void showData() {

        fStore.collection("cake").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> listt = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot snapshot : listt) {
                                storageReference.child(snapshot.getString("pathImage")).getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                list.add(new Cake(snapshot.getString("id"), snapshot.getString("name"), snapshot.getLong("price"), uri.toString()));
                                                adapter.notifyDataSetChanged();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        Log.d(TAG, "OnFailed: failed show image ");
                                        Toast.makeText(ViewCake.this, "Failed show image..........", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d(TAG, "OnFailed: failed get data ");
                Toast.makeText(ViewCake.this, "Failed getdata..........", Toast.LENGTH_SHORT).show();
            }
        });
    }
}