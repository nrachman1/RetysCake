package com.example.cakerety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cakerety.model.Cake;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class DetailProductActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView quantity;
    int totalQuantity = 1;
    int totalPrice = 0;
    private ImageView imageViewCakeImage;
    private TextView textViewCakeName, textViewPrice;
    private ImageView imageViewAddItem, imageViewRemoveItem;
    private Button btnAddToChart;

    private String TAG = "DetailProductActivity";

    private FirebaseFirestore fStore;
    private FirebaseAuth auth;
    private StorageReference storageReference;

    private Cake cake = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);

        final Object object = getIntent().getSerializableExtra("detailCake");

        if (object instanceof Cake){
            cake = (Cake) object;
        }

        imageViewCakeImage = findViewById(R.id.detailCakeImage);
        textViewCakeName = findViewById(R.id.detailCakeName);
        textViewPrice = findViewById(R.id.detailPrice);
        imageViewAddItem = findViewById(R.id.addItem);
        imageViewAddItem.setOnClickListener(this);
        imageViewRemoveItem = findViewById(R.id.removeItem);
        imageViewRemoveItem.setOnClickListener(this);
        btnAddToChart = findViewById(R.id.addToChart);
        btnAddToChart.setOnClickListener(this);
        quantity = findViewById(R.id.quantity);

        if (cake != null){
            Picasso.get().load(cake.getPathImage()).into(imageViewCakeImage);
            textViewCakeName.setText(cake.getCakeName());
            textViewPrice.setText(String.valueOf(cake.getPrice()));
            totalPrice = (int)cake.getPrice() * totalQuantity;
        }

        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addItem:
                if (totalQuantity < 10){
                    totalQuantity++;
                    quantity.setText(String.valueOf(totalQuantity));
                    totalPrice = (int)cake.getPrice() * totalQuantity;
                }
                break;
            case R.id.removeItem:
                if (totalQuantity > 0){
                    totalQuantity--;
                    quantity.setText(String.valueOf(totalQuantity));
                    totalPrice = (int)cake.getPrice() * totalQuantity;
                }
                break;
            case R.id.addToChart:
                AddToChart();
                break;    
        }
    }

    private void AddToChart() {
        String saveCurrentDate, saveCurrentTime;
        Calendar callForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(callForDate.getTime());

        final HashMap<String,Object> cartMap = new HashMap<>();

        cartMap.put("productName", cake.getCakeName());
        cartMap.put("productPrice", textViewPrice.getText().toString());
        cartMap.put("currentDate", saveCurrentDate);
        cartMap.put("currentTime", saveCurrentTime);
        cartMap.put("totalQuantity", quantity.getText().toString());
        cartMap.put("totalPrice", totalPrice);
        cartMap.put("type", "cake");

        fStore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("AddToCart").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                Toast.makeText(DetailProductActivity.this, "Added To A Cart", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}