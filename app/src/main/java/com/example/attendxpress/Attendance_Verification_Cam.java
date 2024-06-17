package com.example.attendxpress;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

public class Attendance_Verification_Cam extends AppCompatActivity {
    Button pitik;
    ImageView pitikPreview;
    Button bSubmit;
    ActivityResultLauncher<Intent> activityResultLauncher;
    Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_verification_cam);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pitik = findViewById(R.id.pitik);
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
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getOnBackPressedDispatcher().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
