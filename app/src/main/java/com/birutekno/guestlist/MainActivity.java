package com.birutekno.guestlist;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mSingleBtn;
    private Button mCoupleBtn;
    private Button mFamsBtn;
    private String sender;
//    private TextView textid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSingleBtn = (Button) findViewById(R.id.singleBtn);
        mCoupleBtn = (Button) findViewById(R.id.coupleBtn);
        mFamsBtn = (Button) findViewById(R.id.famsBtn);
//        textid = (TextView) findViewById(R.id.textId);

        checkPermission();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        sender  = bundle.getString("sender");
//        textid.setText("Kode : " + sender);

        mSingleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NamaActivity.class);
                String tipe = "Single";
                intent.putExtra("type", tipe);
                intent.putExtra("sender", sender);
                startActivity(intent);
            }
        });

        mCoupleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NamaActivity.class);
                String tipe = "Couple";
                intent.putExtra("type", tipe);
                intent.putExtra("sender", sender);
                startActivity(intent);
            }
        });

        mFamsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NamaActivity.class);
                String tipe = "Family";
                intent.putExtra("type", tipe);
                intent.putExtra("sender", sender);
                startActivity(intent);
            }
        });
    }

    private void checkPermission(){
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.WRITE_CONTACTS, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_SMS, android.Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, PERMISSION_ALL);
        }
    }
}
