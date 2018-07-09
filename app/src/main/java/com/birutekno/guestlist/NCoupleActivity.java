package com.birutekno.guestlist;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class NCoupleActivity extends AppCompatActivity {

    private ImageView img, imgCancel;
    private ImageView img1, imgCancel1;
    private TextView etNama;
    private TextView etNama1;
    private Button btnSubmit;

    private static final int CAM_REQ_CODE = 5;
    private StorageReference storage;
    private DatabaseReference database;
    private Bitmap reducedSizeBitmap;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private Uri imageToUploadUri;
    private Uri[] resultUri = new Uri[2];

    private String caption;
    private String sender;
    private String nama;
    private String nama1;
    private String img1Stat = null, img2Stat=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_couple);

        storage = FirebaseStorage.getInstance().getReference();
        img = (ImageView) findViewById(R.id.imgA);
        img1 = (ImageView) findViewById(R.id.imgB);
        imgCancel = (ImageView) findViewById(R.id.imgBtnCancel);
        imgCancel1 = (ImageView) findViewById(R.id.imgBtnCancel1);
        etNama = (TextView) findViewById(R.id.nama);
        etNama1 = (TextView) findViewById(R.id.namaB);
        btnSubmit = (Button) findViewById(R.id.uploadBtn);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        caption = bundle.getString("status");
        sender = bundle.getString("sender");
        nama = bundle.getString("nama");
        nama1 = bundle.getString("nama1");
        database = FirebaseDatabase.getInstance().getReference().child(sender);
        etNama.setText("Mr." + nama);
        etNama1.setText("Mrs." + nama1);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int PERMISSION_ALL = 1;
                String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_SMS, Manifest.permission.CAMERA};

                if (ContextCompat.checkSelfPermission(NCoupleActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(NCoupleActivity.this, PERMISSIONS, PERMISSION_ALL);
                }

                else {
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    Intent chooserIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(Environment.getExternalStorageDirectory(), "IMAGE " + new Date().getTime() + ".jpg");
                    chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    imageToUploadUri = Uri.fromFile(f);
                    if (chooserIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(chooserIntent, CAM_REQ_CODE);
                    }
                }
            }
        });

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                Intent chooserIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(Environment.getExternalStorageDirectory(), "IMAGE " + new Date().getTime() + ".jpg");
                chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                imageToUploadUri = Uri.fromFile(f);
                if (chooserIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(chooserIntent, CAM_REQ_CODE);
                }
            }
        });

        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img.setImageDrawable(getResources().getDrawable(R.drawable.border_mr));
                img1Stat = null;
            }
        });

        imgCancel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img1.setImageDrawable(getResources().getDrawable(R.drawable.border_mrs));
                img2Stat = null;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPosting();
            }
        });
    }

    private void startPosting() {
        progressDialog.setMessage("Uploading");
        if(!TextUtils.isEmpty(nama) && !TextUtils.isEmpty(nama1) && img1Stat != null && img2Stat != null) {
            progressDialog.show();

            final DatabaseReference newPost = database.push();

            final DatabaseReference uppPost = database.child(newPost.getKey());

            for(int i = 0; i < resultUri.length; i++){
                final StorageReference ref = storage.child("Image_Post").child(resultUri[i].getLastPathSegment());
                final int finalI = i;
                ref.putFile(resultUri[i]).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        if(finalI == 0){

                            database.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    newPost.child("nama").setValue(nama);
                                    newPost.child("nama1").setValue(nama1);
                                    newPost.child("status").setValue(caption);
                                    newPost.child("sender").setValue(sender);
                                    newPost.child("waktu").setValue(new Date().getHours() + "." +  new Date().getMinutes());
                                    newPost.child("foto").setValue(downloadUrl.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
//                                                startActivity(new Intent(CoupleActivity.this, MainActivity.class));
                                            }else {
                                                Toast.makeText(NCoupleActivity.this, "Error Posting", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }else if(finalI ==1){

                            database.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    uppPost.child("foto1").setValue(downloadUrl.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Intent intent = new Intent(NCoupleActivity.this, MainActivity.class);
                                                intent.putExtra("sender", sender);
                                                startActivity(intent);
                                                return;
                                            }else {
                                                Toast.makeText(NCoupleActivity.this, "Error Posting", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }

                        progressDialog.dismiss();
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAM_REQ_CODE && resultCode == RESULT_OK) {
            if(imageToUploadUri != null){
                Uri selectedImage = imageToUploadUri;
                getContentResolver().notifyChange(selectedImage, null);
                reducedSizeBitmap = getBitmap(imageToUploadUri.getPath());
                if(reducedSizeBitmap != null){
                    if(img1Stat == null){
                        resultUri[0] = imageToUploadUri;
                        img.setImageURI(resultUri[0]);
                        img1Stat = "ada";
                    }else if(img2Stat == null){
                        resultUri[1] = imageToUploadUri;
                        img1.setImageURI(resultUri[1]);
                        img2Stat = "ada";
                    }
                }else{
                    Toast.makeText(this,"Coba Lagi",Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this,"Coba Lagi",Toast.LENGTH_LONG).show();
            }
        }
    }

    private Bitmap getBitmap(String path) {

        Uri uri = Uri.fromFile(new File(path));
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
//            final int IMAGE_MAX_SIZE = 10000; // 1.2MP
//            final int IMAGE_MAX_SIZE = 1000000;
            in = getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d("", "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);

            Bitmap b = null;
            in = getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
                Log.d("", "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d("", "bitmap size - width: " + b.getWidth() + ", height: " +
                    b.getHeight());
            return b;
        } catch (IOException e) {
            Log.e("", e.getMessage(), e);
            return null;
        }
    }
}
