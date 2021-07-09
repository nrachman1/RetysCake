package com.example.cakerety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class StartedActivity extends AppCompatActivity {

    private Button btnStart;

    private MediaPlayer mediaPlayer;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;

    private static final String TAG = "StartedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_started);

        btnStart = findViewById(R.id.buttonStart);

        mediaPlayer = MediaPlayer.create(this, R.raw.welcome);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SingIn.class));
                mediaPlayer.start();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (fAuth.getCurrentUser() != null){
            fStore.collection("users").document(fAuth.getCurrentUser().getUid())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    int type = documentSnapshot.getLong("type").intValue();
                    if (type == 0){
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        Log.d(TAG, "user type = "+ type);
                        finish();
                    }else if (type == 1){
                        startActivity(new Intent(getApplicationContext(), AdminDashboard.class));
                        Log.d(TAG, "user type = "+ type);
                        finish();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Log.d(TAG, "gagal");
                }
            });
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }
}