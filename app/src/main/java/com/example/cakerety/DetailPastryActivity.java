package com.example.cakerety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cakerety.model.Cake;
import com.example.cakerety.model.Pastry;
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

public class DetailPastryActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView quantity;
    int totalQuantity = 1;
    int totalPrice = 0;
    int price = 0;
    private RadioGroup radioGroupPastry;
    private ImageView imageViewCakeImage;
    private TextView textViewCakeName;
    private RadioButton radioButtonSmall, radioButtonBig;
    private ImageView imageViewAddItem, imageViewRemoveItem;
    private Button btnAddToChart;

    private String TAG = "DetailPastryActivity";

    private FirebaseFirestore fStore;
    private FirebaseAuth auth;
    private StorageReference storageReference;

    private Pastry pastry = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pastry);

        final Object object = getIntent().getSerializableExtra("detailPastry");

        if (object instanceof Pastry){
            pastry = (Pastry) object;
        }

        imageViewCakeImage = findViewById(R.id.detailPastryImage);
        textViewCakeName = findViewById(R.id.detailPastryName);
        radioButtonSmall = findViewById(R.id.rBPriceSmall);
        radioButtonBig = findViewById(R.id.rBPriceBig);
        imageViewAddItem = findViewById(R.id.addItemPastry);
        imageViewAddItem.setOnClickListener(this);
        imageViewRemoveItem = findViewById(R.id.removeItemPastry);
        imageViewRemoveItem.setOnClickListener(this);
        btnAddToChart = findViewById(R.id.addToChartPastry);
        btnAddToChart.setOnClickListener(this);
        quantity = findViewById(R.id.quantityPastry);
        radioGroupPastry = findViewById(R.id.radioGroupPastry);


        TestRadioBtn();

        if (pastry != null){
            Picasso.get().load(pastry.getPathImage()).into(imageViewCakeImage);
            textViewCakeName.setText(pastry.getPastryName());
            String pSmall = String.valueOf(pastry.getPriceSmall());
            radioButtonSmall.setText("Small : "+ pSmall);
            String pBig = String.valueOf(pastry.getPriceBig());
            radioButtonBig.setText("Big : "+ pBig);
            totalPrice = price * totalQuantity;
            Log.d(TAG, "total harga : "+totalPrice);
        }

        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
    }

    private void TestRadioBtn() {
        radioGroupPastry.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rBPriceSmall:
                        price = (int)pastry.getPriceSmall();
                        Toast.makeText(DetailPastryActivity.this, "Smal Size"+price, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.rBPriceBig:
                        price = (int)pastry.getPriceBig();
                        Toast.makeText(DetailPastryActivity.this, "Big Size "+price, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.addItemPastry:
                if (totalQuantity < 10){
                    totalQuantity++;
                    quantity.setText(String.valueOf(totalQuantity));
                    totalPrice = price * totalQuantity;
                    Log.d(TAG, "total harga : "+totalPrice+ "--harga satuan :"+price);
                }
                break;
            case R.id.removeItemPastry:
                if (totalQuantity > 0){
                    totalQuantity--;
                    quantity.setText(String.valueOf(totalQuantity));
                    totalPrice = price * totalQuantity;
                    Log.d(TAG, "total harga : "+totalPrice);
                }
                break;
            case R.id.addToChartPastry:
                totalPrice = price * totalQuantity;
                Log.d(TAG, "total harga add: "+totalPrice);
                AddToChart();
                break;
        }

    }

    private void AddToChart() {

        if (!radioButtonBig.isChecked() && !radioButtonSmall.isChecked()){
            radioButtonSmall.setError("Pilihan size masih kosong");
            radioButtonSmall.requestFocus();
            return;

        }
        String saveCurrentDate, saveCurrentTime;
        Calendar callForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(callForDate.getTime());

        final HashMap<String,Object> cartMap = new HashMap<>();

        cartMap.put("productName", pastry.getPastryName());
        cartMap.put("productPrice", String.valueOf(price));
        cartMap.put("currentDate", saveCurrentDate);
        cartMap.put("currentTime", saveCurrentTime);
        cartMap.put("totalQuantity", quantity.getText().toString());
        cartMap.put("totalPrice", totalPrice);
        cartMap.put("type", "Pastry");

        fStore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("AddToCart").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                Toast.makeText(DetailPastryActivity.this, "Added To A Cart", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}