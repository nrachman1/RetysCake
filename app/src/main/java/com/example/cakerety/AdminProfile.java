package com.example.cakerety;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class AdminProfile extends AppCompatActivity implements View.OnClickListener{

    private TextView fullnameTextView, emailTextView, addressTextView, phoneNumTextView, changePasswordTextView, editProfileTextView;
    private Button singOut;
    private ImageView userImageView;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userId;

    private StorageReference storageReference;

    private String TAG = "AdminProfileActivity";

    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        fullnameTextView = findViewById(R.id.fullName);
        emailTextView = findViewById(R.id.email);
        changePasswordTextView = findViewById(R.id.changePassword);
        changePasswordTextView.setOnClickListener(this);
        addressTextView = findViewById(R.id.address);
        phoneNumTextView = findViewById(R.id.phoneNum);
        singOut = findViewById(R.id.logOut);
        singOut.setOnClickListener(this);
        userImageView = findViewById(R.id.userImage);
        userImageView.setOnClickListener(this);
        editProfileTextView = findViewById(R.id.editProfile);
        editProfileTextView.setOnClickListener(this);

        mediaPlayer = MediaPlayer.create(this, R.raw.logout);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/Profile");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(userImageView);
            }
        });

        userId = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    fullnameTextView.setText(documentSnapshot.getString("fullName"));
                    emailTextView.setText(documentSnapshot.getString("email"));
                    addressTextView.setText(documentSnapshot.getString("address"));
                    phoneNumTextView.setText(documentSnapshot.getString("phoneNum"));

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d(TAG, "error saat ambil data");
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.logOut:
                SingOut();
                break;
            case R.id.changePassword:
                Log.d(TAG, "masuk switch case");
                startActivity(new Intent(getApplicationContext(), ChangePasswordAdmin.class));
                break;
            case R.id.editProfile:
                Intent i = new Intent(v.getContext(), EditProfileAdmin.class);
                i.putExtra("fullName", fullnameTextView.getText().toString());
                i.putExtra("email", emailTextView.getText().toString());
                i.putExtra("address", addressTextView.getText().toString());
                i.putExtra("phoneNum", phoneNumTextView.getText().toString());
                startActivity(i) ;
                break;
        }

    }


    private void SingOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), SingIn.class));
        finish();
        mediaPlayer.start();

    }
}