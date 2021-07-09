package com.example.cakerety;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InputCake extends AppCompatActivity implements View.OnClickListener{

    private ImageView imageViewCakeImage;
    private EditText editTextCakeName, editTextPrice;
    private Button buttonStored;
    
    private String id, cakeName, price, pathImage;
    private int finalPrice;

    private ProgressBar progressBar;

    private String TAG = "InputCakeActivity";

    private FirebaseFirestore fStore;

    private String uId, uCakeName, uPathImage;
    private Long uPrice;

    private StorageReference storageReference;

    private Uri imageUri, uriUpdate;

    private SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy-HH:mm:ss");
    private Date date = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_cake);
        
        imageViewCakeImage = findViewById(R.id.cakeImage);
        imageViewCakeImage.setOnClickListener(this);
        
        editTextCakeName = findViewById(R.id.cakeName);
        editTextPrice = findViewById(R.id.price);
        
        buttonStored = findViewById(R.id.storeCake);
        buttonStored.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        Bundle bundle = getIntent().getExtras();
        if (bundle !=null){
            buttonStored.setText("Update");

            uId = bundle.getString("uId");
            uCakeName = bundle.getString("uCakeName");
            uPrice = bundle.getLong("uPrice");
            uPathImage = bundle.getString("uPathImage");

            editTextCakeName.setText(uCakeName);
            editTextPrice.setText(String.valueOf(uPrice));

            uriUpdate = Uri.parse(uPathImage);

            Picasso.get().load(uriUpdate).into(imageViewCakeImage);
        }else{
            buttonStored.setText("Input");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cakeImage:
                SelectImage();
                break;
            case R.id.storeCake:
                Bundle bundle1 = getIntent().getExtras();
                if (bundle1 != null){
                    UpdateCake();
                }else{
                    StoredCake();
                }
                break;
        }
        
    }

    private void UpdateCake() {
        id = uId;
        cakeName = editTextCakeName.getText().toString().trim();
        price = editTextPrice.getText().toString().trim();
        finalPrice = Integer.parseInt(price);

        pathImage = "cake/"+cakeName+id+formatter.format(date);

        if (cakeName.isEmpty()){
            editTextCakeName.setError("Cake name is required!");
            editTextCakeName.requestFocus();
            return;
        }

        if (price.isEmpty()){
            editTextPrice.setError("Price is required!");
            editTextPrice.requestFocus();
            return;
        }

        if (imageUri == null){
            fStore.collection("cake").document(id).update("id", id, "name", cakeName, "price", finalPrice)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(InputCake.this, "Data Updated!!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(InputCake.this, "Error : on data updated", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(InputCake.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            StorageReference fileRef = storageReference.child(pathImage);

            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(InputCake.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(InputCake.this, "Failed Uploaded", Toast.LENGTH_SHORT).show();
                }
            });

            fStore.collection("cake").document(id).update("id", id, "name", cakeName, "price", finalPrice, "pathImage", pathImage)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(InputCake.this, "Data Updated!!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(InputCake.this, "Error : on data updated", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(InputCake.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }


    }

    private void SelectImage() {
        Intent openGaleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGaleryIntent, 1000);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000){
            if (resultCode == Activity.RESULT_OK){
                imageUri = data.getData();
                imageViewCakeImage.setImageURI(imageUri);

            }
        }
    }

    private void StoredCake() {
        id = UUID.randomUUID().toString();
        cakeName = editTextCakeName.getText().toString().trim();
        price = editTextPrice.getText().toString().trim();
        finalPrice = Integer.parseInt(price);

        pathImage = "cake/"+cakeName+id;

        StorageReference fileRef = storageReference.child(pathImage);

        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(InputCake.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(InputCake.this, "Failed Uploaded", Toast.LENGTH_SHORT).show();
            }
        });

        Map<String, Object> cake = new HashMap<>();
        cake.put("id", id);
        cake.put("name", cakeName);
        cake.put("price", finalPrice);
        cake.put("pathImage", pathImage);

        fStore.collection("cake").document(id).set(cake)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(InputCake.this, "Data Saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(InputCake.this, "Data Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }
}