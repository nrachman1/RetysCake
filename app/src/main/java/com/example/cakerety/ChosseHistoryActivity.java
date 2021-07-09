package com.example.cakerety;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class ChosseHistoryActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView imageViewOnCart, imageViewSucessTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosse_history);

        imageViewOnCart = findViewById(R.id.onCharImgView);
        imageViewOnCart.setOnClickListener(this);

        imageViewSucessTransaction = findViewById(R.id.sucessTransactionImgView);
        imageViewSucessTransaction.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.onCharImgView:
                startActivity(new Intent(getApplicationContext(), ReportOnCartActivity.class));
                break;
            case R.id.sucessTransactionImgView:
                Log.d("ChosseActivity", "klik succcccc");
                startActivity(new Intent(getApplicationContext(), ReportSucessTransactionActivity.class));
                break;
        }
    }
}