package com.birutekno.guestlist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class BitmapActivity extends AppCompatActivity {

    private Button mUpload;
    private ImageView mImage;

    private static final int CAMERA = 1;
    private StorageReference mStorage;
    private DatabaseReference database;
    private ProgressDialog mProgress;

    private String caption;
    private String sender;
    private String nama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        caption = bundle.getString("status");
        sender = bundle.getString("sender");
        nama = bundle.getString("nama");

        mStorage = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance().getReference().child(sender);
        mUpload = (Button) findViewById(R.id.uploadBtn);
        mImage = (ImageView) findViewById(R.id.imgView);
        mProgress = new ProgressDialog(this);

        mUpload.setText(nama);
        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA && resultCode == RESULT_OK){

            mProgress.setMessage("Uploading");
            mProgress.show();

            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
            byte[] dataBAOS = baos.toByteArray();

//            Uri uri = data.getData();

            StorageReference filepath = mStorage.child("BitmapPicture").child("filename" + new Date().getTime());

            filepath.putBytes(dataBAOS).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgress.dismiss();

                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    final DatabaseReference newPost = database.push();

                    database.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            newPost.child("nama").setValue(nama);
                            newPost.child("status").setValue(caption);
                            newPost.child("sender").setValue(sender);
                            newPost.child("waktu").setValue(new Date().getHours() + "." +  new Date().getMinutes());
                            newPost.child("foto").setValue(downloadUrl.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(BitmapActivity.this, MainActivity.class);
                                        intent.putExtra("sender", sender);
                                        startActivity(intent);
                                        return;
                                    }else {
                                        Toast.makeText(BitmapActivity.this, "Error Posting", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Toast.makeText(BitmapActivity.this, "SUCCES", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
