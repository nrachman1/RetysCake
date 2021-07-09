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
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class EditProfileAdmin extends AppCompatActivity implements View.OnClickListener{

    EditText editTextFullName, editTextEmail, editTextAddress, editTextPhoneNum;

    ImageView userImageView;

    Button saveBtn;

    private String email, fullName, address, phone;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private FirebaseUser user;
    private String userId;

    private StorageReference storageReference;

    private String TAG = "EditAdminProfileActivity";

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_admin);

        editTextFullName = findViewById(R.id.fullName);
        editTextEmail = findViewById(R.id.email);
        editTextAddress = findViewById(R.id.address);
        editTextPhoneNum = findViewById(R.id.phoneNum);
        userImageView = findViewById(R.id.userImage);
        userImageView.setOnClickListener(this);
        saveBtn = findViewById(R.id.saveEdit);
        saveBtn.setOnClickListener(this);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user = fAuth.getCurrentUser();

        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/Profile");


        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(userImageView);
            }
        });

        Intent data = getIntent();
        String fullName = data.getStringExtra("fullName");
        String email = data.getStringExtra("email");
        String address = data.getStringExtra("address");
        String phoneNum = data.getStringExtra("phoneNum");

        editTextFullName.setText(fullName);
        editTextEmail.setText(email);
        editTextAddress.setText(address);
        editTextPhoneNum.setText(phoneNum);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.userImage:
                SelectImage();
                break;
            case R.id.saveEdit:
                UpdateDataUser();
                break;
        }
    }

    private void SelectImage() {
        Intent openGaleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGaleryIntent, 1000);
    }


    private void UpdateDataUser() {

        email = editTextEmail.getText().toString().trim();
        fullName = editTextFullName.getText().toString().trim();
        address = editTextAddress.getText().toString().trim();
        phone = editTextPhoneNum.getText().toString().trim();

        if(email.isEmpty()){
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please provide valid email");
            editTextEmail.requestFocus();
            return;
        }

        if(fullName.isEmpty()){
            editTextFullName.setError("Full name is required!");
            editTextFullName.requestFocus();
            return;
        }

        if(address.isEmpty()){
            editTextAddress.setError("Address is required!");
            editTextAddress.requestFocus();
            return;
        }

        if(phone.isEmpty()){
            editTextPhoneNum.setError("Address is required!");
            editTextPhoneNum.requestFocus();
            return;
        }


        user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                DocumentReference docRef = fStore.collection("users").document(user.getUid());
                Map<String,Object> edited = new HashMap<>();
                edited.put("email", email);
                edited.put("fullName", fullName);
                edited.put("address", address);
                edited.put("phoneNum", phone);
                docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(EditProfileAdmin.this, "Profile Updated", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
                Toast.makeText(EditProfileAdmin.this, "Data User Updated", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(EditProfileAdmin.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000){
            if (resultCode == Activity.RESULT_OK){
                imageUri = data.getData();

                UploadImageToFirebase(imageUri);
            }
        }
    }

    private void UploadImageToFirebase(Uri imageUri) {

        StorageReference fileref = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/Profile");
        fileref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(userImageView);
                        Toast.makeText(EditProfileAdmin.this, "Image Updated!!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d(TAG, "Upload image failed on admin profile class");
                Toast.makeText(EditProfileAdmin.this, "Image Updated!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}