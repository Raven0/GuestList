package com.birutekno.guestlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class NamaActivity extends AppCompatActivity {

    private TextView gender,shortcut;
    private ImageView img;
    private EditText mr, mrs;
    private Button nextButton;
    private String caption, sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nama);

        shortcut = (TextView) findViewById(R.id.shortcutBitmap);
        gender = (TextView) findViewById(R.id.gender);
        img = (ImageView) findViewById(R.id.imgView);
        mr = (EditText) findViewById(R.id.Mr);
        mrs = (EditText) findViewById(R.id.Mrs);
        nextButton = (Button) findViewById(R.id.nextButton);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        caption = bundle.getString("type");
        sender  = bundle.getString("sender");
        if(caption.equals("Family")){
            gender.setText("Family");
            mr.setHint("Name's");
        } else if(caption.equals("Couple")){
            gender.setText("Mr & Mrs");
            mrs.setVisibility(View.VISIBLE);
            mr.setHint("Mr.");
        }

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(caption.equals("Single")){
                    if (mr.getText().toString().trim().length() == 0){
                        Toast.makeText(NamaActivity.this, "Pastikan nama sudah terisi", Toast.LENGTH_SHORT).show();
                    } else{
                        Intent intent = new Intent(NamaActivity.this, NSingleActivity.class);
                        String nama = mr.getText().toString().trim();
                        intent.putExtra("nama", nama);
                        intent.putExtra("status", caption);
                        intent.putExtra("sender", sender);
                        startActivity(intent);
                    }
                }else if(caption.equals("Family")){
                    if (mr.getText().toString().trim().length() == 0){
                        Toast.makeText(NamaActivity.this, "Pastikan nama sudah terisi", Toast.LENGTH_SHORT).show();
                    } else{
                        Intent intent = new Intent(NamaActivity.this, NSingleActivity.class);
                        String nama = mr.getText().toString().trim();
                        intent.putExtra("nama", nama);
                        intent.putExtra("status", caption);
                        intent.putExtra("sender", sender);
                        startActivity(intent);
                    }
                }else {
                    if (mr.getText().toString().trim().length() == 0){
                        Toast.makeText(NamaActivity.this, "Pastikan nama tamu laki laki sudah terisi", Toast.LENGTH_SHORT).show();
                    } else if(mrs.getText().toString().trim().length() == 0){
                        Toast.makeText(NamaActivity.this, "Pastikan nama tamu perempuan sudah terisi", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(NamaActivity.this, NCoupleActivityNew.class);
                        String nama = mr.getText().toString().trim();
                        String nama1 = mrs.getText().toString().trim();
                        intent.putExtra("nama", nama);
                        intent.putExtra("nama1", nama1);
                        intent.putExtra("status", caption);
                        intent.putExtra("sender", sender);
                        startActivity(intent);
                    }
                }
            }
        });

    }
}
