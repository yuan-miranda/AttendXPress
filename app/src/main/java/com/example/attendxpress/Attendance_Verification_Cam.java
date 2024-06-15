package com.example.attendxpress;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

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
            if (imageBitmap == null) {
                Toast.makeText(this, "Please capture an image first", Toast.LENGTH_SHORT).show();
                return;
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytes = stream.toByteArray();

            Intent i = new Intent(Attendance_Verification_Cam.this, Attendance_Verification_Success.class);
            i.putExtra("pitikImage", bytes);
            startActivity(i);
        });
        moveHome.setOnClickListener(v -> {
            Intent i = new Intent(Attendance_Verification_Cam.this, Home.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }
}
