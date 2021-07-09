package com.example.cakerety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cakerety.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SingUp extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;

    private TextView banner, registerUser;
    private EditText editTextEmail, editTextFullName, editTextAddress, editTextPassword, editTextPhone;
    private ProgressBar progressBar;
    private TextView loginBtn;

    private String email, fullName, address, phone, password;
    private int type;

    private static final String USERS = "users";

    private String TAG = "SingUpActivity";

    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private FirebaseFirestore fStore;

    private String userID;

//    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        mAuth = FirebaseAuth.getInstance();

        banner = (TextView)findViewById(R.id.banner);
        banner.setOnClickListener(this);

        registerUser = (TextView)findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        loginBtn = findViewById(R.id.login);
        loginBtn.setOnClickListener(this);

        editTextEmail = (EditText)findViewById(R.id.email);
        editTextFullName = (EditText)findViewById(R.id.fullName);
        editTextAddress = (EditText)findViewById(R.id.address);
        editTextPhone = (EditText)findViewById(R.id.phone);
        editTextPassword = (EditText)findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progressBarRegister);

        fStore = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference(USERS);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.banner:
                startActivity(new Intent(this, SingIn.class));
                break;
            case R.id.registerUser:
                registerUser();
                break;
            case R.id.login:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        email = editTextEmail.getText().toString().trim();
        fullName = editTextFullName.getText().toString().trim();
        address = editTextAddress.getText().toString().trim();
        phone = editTextPhone.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();
        //type 0 for customer and 1 for admin
        type = 0;

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
            editTextPhone.setError("Address is required!");
            editTextPhone.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length()<6){
            editTextPassword.setError("Min password length should be 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

//        user = new User(email, fullName, address, phone, type);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
//                            FirebaseUser user = mAuth.getCurrentUser();
                            userID = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("email", email);
                            user.put("fullName", fullName);
                            user.put("address", address);
                            user.put("phoneNum", phone);
                            user.put("type", type);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "OnSuccess: user profile is created for "+userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Log.d(TAG, "OnFailure "+e.toString());
                                }
                            });
                            Toast.makeText(SingUp.this, "User has been registered successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                          updateUI(user);
                        }else{
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SingUp.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }

//    public void updateUI(FirebaseUser currentUser) {
//        String keyid = mDatabase.push().getKey();
//        mDatabase.child(keyid).setValue(user); //adding user info to database
//        Intent loginIntent = new Intent(this, SingIn.class);
//        startActivity(loginIntent);
//    }
}