package com.birutekno.guestlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mSingleBtn;
    private Button mCoupleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSingleBtn = (Button) findViewById(R.id.singleBtn);
        mCoupleBtn = (Button) findViewById(R.id.coupleBtn);

        mSingleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NamaActivity.class);
                String tipe = "Single";
                intent.putExtra("type", tipe);
                startActivity(intent);
            }
        });

        mCoupleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NamaActivity.class);
                String tipe = "Couple";
                intent.putExtra("type", tipe);
                startActivity(intent);
            }
        });
    }
}
