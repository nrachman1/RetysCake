package com.example.cakerety;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboard extends AppCompatActivity implements View.OnClickListener{

    private ImageView registerAdmin, cake, pastry, profile, dataAllUser, history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        registerAdmin = findViewById(R.id.adminRegister);
        registerAdmin.setOnClickListener(this);

        cake = findViewById(R.id.cake);
        cake.setOnClickListener(this);

        pastry = findViewById(R.id.pastry);
        pastry.setOnClickListener(this);

        profile = findViewById(R.id.profileAdmin);
        profile.setOnClickListener(this);

        dataAllUser = findViewById(R.id.userData);
        dataAllUser.setOnClickListener(this);

        history = findViewById(R.id.history);
        history.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.adminRegister:
                startActivity(new Intent(getApplicationContext(), RegisterAdmin.class));
                break;
            case R.id.cake:
                startActivity(new Intent(getApplicationContext(), ViewCake.class));
                break;
            case R.id.pastry:
                startActivity(new Intent(getApplicationContext(), ViewPastry.class));
                break;
            case R.id.profileAdmin:
                startActivity(new Intent(getApplicationContext(), AdminProfile.class));
                break;
            case R.id.userData:
                startActivity(new Intent(getApplicationContext(), DataAllUserActivity.class));
                break;
            case R.id.history:
                startActivity(new Intent(getApplicationContext(), ChosseHistoryActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }


}