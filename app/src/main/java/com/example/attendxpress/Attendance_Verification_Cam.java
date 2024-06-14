package com.example.attendxpress;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

public class Attendance_Verification_Cam extends AppCompatActivity {
    Button pitik;
    Button moveHome;
    ImageView pitikPreview;
    Button bSubmit;
    ActivityResultLauncher<Intent> activityResultLauncher;
    Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_verification_cam);

        pitik = findViewById(R.id.pitik);
        moveHome = findViewById(R.id.moveHome);
        pitikPreview = findViewById(R.id.pitikPreview);
        bSubmit = findViewById(R.id.bSubmit);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Bundle extras = result.getData().getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                pitikPreview.setImageBitmap(imageBitmap);
            }
        });

        pitik.setOnClickListener(v -> {
            Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activityResultLauncher.launch(takePhoto);
        });
        bSubmit.setOnClickListener(v -> {
            if (imageBitmap != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bytes = stream.toByteArray();

                Intent i = new Intent(Attendance_Verification_Cam.this, Attendance_Verification_Success.class);
                i.putExtra("profile_image", bytes);
                startActivity(i);
            }
            Intent i = new Intent(Attendance_Verification_Cam.this, Attendance_Verification_Success.class);
            startActivity(i);
        });
        moveHome.setOnClickListener(v -> {
            Intent i = new Intent(Attendance_Verification_Cam.this, Home.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }
}
