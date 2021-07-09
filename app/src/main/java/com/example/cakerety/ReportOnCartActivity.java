package com.example.cakerety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.cakerety.model.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ReportOnCartActivity extends AppCompatActivity {

    private RecyclerView recyclerViewReport;

    private FirebaseFirestore fStore;
    private FirebaseAuth auth;

    private HistoryAllCartAdapter adapter;

    private List<MyCartModel> list;

    private String TAG = "ReportOnCartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_on_cart);

        recyclerViewReport = findViewById(R.id.recyclerviewReportAllCart);
        recyclerViewReport.setHasFixedSize(true);
        recyclerViewReport.setLayoutManager(new LinearLayoutManager(this));

        fStore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        list = new ArrayList<>();
        adapter = new HistoryAllCartAdapter(this, list);
        recyclerViewReport.setAdapter(adapter);

        showData();
    }

    private void showData() {

        ArrayList<String> idUser = new ArrayList<>();

        fStore.collection("users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> listt = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot : listt){
                            fStore.collection("CurrentUser").document(snapshot.getId())
                                    .collection("AddToCart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                @Override
                                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {

                                            String documentId = documentSnapshot.getId();

                                            MyCartModel cartModel = documentSnapshot.toObject(MyCartModel.class);

                                            cartModel.setDocumentId(documentId);

                                            list.add(cartModel);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Log.d(TAG, "error saat ambil data");
                                }
                            });
                        }
                    }
                });
    }
}