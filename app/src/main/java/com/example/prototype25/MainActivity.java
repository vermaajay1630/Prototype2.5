package com.example.prototype25;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView username;
    String address;
    ImageView img;
    Uri uri;
    Button cam, upload;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    private static final int CAM_REQ_CODE = 100;
    String currentPhotoPath;
    image_model image_model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.userName);
        img = findViewById(R.id.imageView);
        cam = findViewById(R.id.camBtn);
        upload = findViewById(R.id.uploadBtn);
        Intent main = getIntent();
        String unname = main.getStringExtra("username");
        username.setText(unname);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("imageDetail");
        image_model = new image_model();
        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCamPermission();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

    }



    private void uploadImage() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("image");
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY_MM_DD_HH_MM_SS", Locale.getDefault());
        Date now =  new Date();
        String filename =dateFormat.format(now);
        storageReference = FirebaseStorage.getInstance().getReference("images/"+filename);
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri url = uri;
                        String usernames = username.getText().toString();
                        addTodatabase(usernames, String.valueOf(uri));
                    }
                });
            }
        });

    }

    private void addTodatabase(String usernames, String url) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY_MM_DD_HH_MM_SS", Locale.getDefault());
        Date now =  new Date();
        String filename =dateFormat.format(now);
        image_model.setImageUri(url);
        image_model.setUserName(usernames);
        image_model.setLocation(address);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.child(filename).setValue(image_model);
                Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                Intent feed = new Intent(getApplicationContext(), Feeds.class);
                startActivity(feed);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void checkCamPermission() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            openCamera();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAM_REQ_CODE);
        }
    }

    private void openCamera() {
        String filename = "photo";
        File filestorage = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = null;
        try {
            Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            imageFile =File.createTempFile(filename, ".jpg", filestorage);
            Uri imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.prototype2.fileProvider", imageFile);
            currentPhotoPath = imageFile.getAbsolutePath();
            cam.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cam, CAM_REQ_CODE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAM_REQ_CODE && resultCode == RESULT_OK){
            Bitmap bm = BitmapFactory.decodeFile(currentPhotoPath);
            img.setImageBitmap(bm);
            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bm, "val", null);
            uri = Uri.parse(path);
        }
    }
}