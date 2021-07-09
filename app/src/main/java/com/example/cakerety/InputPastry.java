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

public class InputPastry extends AppCompatActivity implements View.OnClickListener{

    private ImageView imageViewPastryImage;
    private EditText editTextPastryName, editTextPriceSmall, editTextPriceBig;
    private Button buttonStored;

    private String id, pastryName, priceSmall, priceBig, pathImage;
    private int finalPriceSmall, finalPriceBig;

    private ProgressBar progressBar;

    private String TAG = "InputPastryActivity";

    private FirebaseFirestore fStore;

    private String uId, uPastryName, uPathImage;
    private Long uPriceSmall, uPriceBig;

    private StorageReference storageReference;

    private Uri imageUri, uriUpdate;

    private SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy-HH:mm:ss");
    private Date date = new Date();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_pastry);

        imageViewPastryImage = findViewById(R.id.pastryImage);
        imageViewPastryImage.setOnClickListener(this);

        editTextPastryName = findViewById(R.id.pastryName);
        editTextPriceSmall = findViewById(R.id.priceSmall);
        editTextPriceBig = findViewById(R.id.priceBig);

        buttonStored = findViewById(R.id.storePastry);
        buttonStored.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        Bundle bundle = getIntent().getExtras();
        if (bundle !=null){
            buttonStored.setText("Update");

            uId = bundle.getString("uId");
            uPastryName = bundle.getString("uPastryName");
            uPriceSmall = bundle.getLong("uPriceSmall");
            uPriceBig = bundle.getLong("uPriceBig");
            uPathImage = bundle.getString("uPathImage");

            Log.d(TAG, "url gambar: "+uPathImage);

            editTextPastryName.setText(uPastryName);
            editTextPriceSmall.setText(String.valueOf(uPriceSmall));
            editTextPriceBig.setText(String.valueOf(uPriceBig));

            uriUpdate = Uri.parse(uPathImage);

            Picasso.get().load(uriUpdate).into(imageViewPastryImage);
        }else{
            buttonStored.setText("Input");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pastryImage:
                SelectImage();
                break;
            case R.id.storePastry:
                Bundle bundle1 = getIntent().getExtras();
                if (bundle1 != null){
                    UpdatePastry();
                    finish();
                }else{
                    StoredPastry();
                    finish();
                }
                break;
        }
    }

    private void UpdatePastry() {

        id = uId;
        pastryName = editTextPastryName.getText().toString().trim();
        priceSmall = editTextPriceSmall.getText().toString().trim();
        finalPriceSmall = Integer.parseInt(priceSmall);
        priceBig = editTextPriceBig.getText().toString().trim();
        finalPriceBig = Integer.parseInt(priceBig);

        pathImage = "pastrys/"+pastryName+id+formatter.format(date);

        if (pastryName.isEmpty()){
            editTextPastryName.setError("Pastry name is required!");
            editTextPastryName.requestFocus();
            return;
        }

        if (priceSmall.isEmpty()){
            editTextPriceSmall.setError("Price small is required!");
            editTextPriceSmall.requestFocus();
            return;
        }

        if (priceBig.isEmpty()){
            editTextPriceSmall.setError("Price big is required!");
            editTextPriceSmall.requestFocus();
            return;
        }

        if (imageUri == null){
            fStore.collection("pastrys").document(id).update("id", id, "name", pastryName, "priceSmall", finalPriceSmall, "priceBig", finalPriceBig)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(InputPastry.this, "Data Updated!!", Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(InputPastry.this, "Error : on data updated", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(InputPastry.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            StorageReference fileRef = storageReference.child(pathImage);

            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(InputPastry.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(InputPastry.this, "Failed Uploaded", Toast.LENGTH_SHORT).show();
                }
            });

            fStore.collection("pastrys").document(id).update("id", id, "name", pastryName, "priceSmall", finalPriceSmall, "priceBig", finalPriceBig, "pathImage", pathImage)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(InputPastry.this, "Data Updated!!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(InputPastry.this, "Error : on data updated", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(InputPastry.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private void StoredPastry() {
        id = UUID.randomUUID().toString();
        pastryName = editTextPastryName.getText().toString().trim();
        priceSmall = editTextPriceSmall.getText().toString().trim();
        finalPriceSmall = Integer.parseInt(priceSmall);
        priceBig = editTextPriceBig.getText().toString().trim();
        finalPriceBig = Integer.parseInt(priceBig);

        pathImage = "pastrys/"+pastryName+id;

        StorageReference fileRef = storageReference.child(pathImage);

        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(InputPastry.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(InputPastry.this, "Failed Uploaded", Toast.LENGTH_SHORT).show();
            }
        });

        Map<String, Object> pastry = new HashMap<>();
        pastry.put("id", id);
        pastry.put("name", pastryName);
        pastry.put("priceSmall", finalPriceSmall);
        pastry.put("priceBig", finalPriceBig);
        pastry.put("pathImage", pathImage);

        fStore.collection("pastrys").document(id).set(pastry)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(InputPastry.this, "Data Saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(InputPastry.this, "Data Failed", Toast.LENGTH_SHORT).show();
            }
        });
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
                imageViewPastryImage.setImageURI(imageUri);

            }
        }
    }
}