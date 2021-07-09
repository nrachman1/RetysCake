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
import android.widget.Toast;

import com.example.cakerety.model.Cake;
import com.example.cakerety.model.Pastry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ViewPastry extends AppCompatActivity {

    private RecyclerView recyclerViewPastry;
    private Button buttonInput;

    private PastryAdapter adapter;
    private List<Pastry> list;

    private FirebaseFirestore fStore;
    private StorageReference storageReference;

    private String TAG = "ViewPastry";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pastry);


        recyclerViewPastry = findViewById(R.id.recyclerviewPastry);
        recyclerViewPastry.setHasFixedSize(true);
        recyclerViewPastry.setLayoutManager(new LinearLayoutManager(this));
        buttonInput = findViewById(R.id.input);

        fStore = FirebaseFirestore.getInstance();

        list = new ArrayList<>();
        adapter = new PastryAdapter(this, list);
        recyclerViewPastry.setAdapter(adapter);
        storageReference = FirebaseStorage.getInstance().getReference();

        ItemTouchHelper touchHelper = new ItemTouchHelper(new TouchHelperPastry(adapter));
        touchHelper.attachToRecyclerView(recyclerViewPastry);

        showData();

        buttonInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), InputPastry.class));
            }
        });
    }

    public void showData() {

        fStore.collection("pastrys").get()
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

                                                list.add(new Pastry(snapshot.getString("id"), snapshot.getString("name"), uri.toString(), snapshot.getLong("priceSmall"), snapshot.getLong("priceBig")));
                                                adapter.notifyDataSetChanged();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        Log.d(TAG, "OnFailed: failed show image ");
                                        Toast.makeText(ViewPastry.this, "Failed show image..........", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d(TAG, "OnFailed: failed get data ");
                Toast.makeText(ViewPastry.this, "Failed getdata..........", Toast.LENGTH_SHORT).show();
            }
        });
    }
}