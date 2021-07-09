package com.example.cakerety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.cakerety.model.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;


import java.util.HashMap;
import java.util.List;

public class PlaceOrdeActivity extends AppCompatActivity {

    private FirebaseFirestore fStore;
    private FirebaseAuth auth;



//    private List<MyCartModel> list;

    private Button buttonMainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_orde);

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

//        list = (ArrayList<MyCartModel>) getIntent().getSerializableExtra("itemList");

        //final Object list = getIntent().getSerializableExtra("itemLise");

        List<MyCartModel> list = (List<MyCartModel>) getIntent().getSerializableExtra("itemList");
        buttonMainMenu = findViewById(R.id.mainMenuBtn);



        if (list != null && list.size() > 0){
            for (MyCartModel model : list){
                final HashMap<String,Object> cartMap = new HashMap<>();

                cartMap.put("productName", model.getProductName());
                cartMap.put("productPrice", model.getProductPrice());
                cartMap.put("currentDate", model.getCurrentDate());
                cartMap.put("currentTime", model.getCurrentTime());
                cartMap.put("totalQuantity", model.getTotalQuantity());
                cartMap.put("totalPrice", model.getTotalPrice());
                cartMap.put("type", model.getType());

                fStore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                        .collection("MyOrder").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                        Toast.makeText(PlaceOrdeActivity.this, "Pesanan Anda Sedang Dalam Perjalanan", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }

        buttonMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

    }
}