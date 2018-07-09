package com.birutekno.guestlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SetupActivity extends AppCompatActivity {

    private Button mProceedBtn;
    private EditText mSenderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mProceedBtn = (Button) findViewById(R.id.proceed);
        mSenderId = (EditText) findViewById(R.id.senderId);

        mProceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetupActivity.this, MainActivity.class);
                String sender = mSenderId.getText().toString().trim();
                intent.putExtra("sender", sender);
                startActivity(intent);
            }
        });
    }
}
