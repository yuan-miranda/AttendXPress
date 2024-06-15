package com.example.attendxpress;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity {
    Button viewProfile;
    Button checkInAttendance;
    TextView nameDisplay;
    TextView emailDisplay;
    TextView promptNameDisplay;
    SQLiteDatabase AttendXPressDB;
    Cursor getName;
    ImageView miniProfile;

    @Override
    protected void onCreate(Bundle savedIntanceState) {
        super.onCreate(savedIntanceState);
        setContentView(R.layout.home);

        viewProfile = findViewById(R.id.viewProfile);
        checkInAttendance = findViewById(R.id.checkInAttendance);
        nameDisplay = findViewById(R.id.nameDisplay);
        emailDisplay = findViewById(R.id.emailDisplay);
        promptNameDisplay = findViewById(R.id.promptNameDisplay);
        miniProfile = findViewById(R.id.miniProfile);

        AttendXPressDB = openOrCreateDatabase("AttendXPressDB", Context.MODE_PRIVATE, null);

        String sEmail = GlobalVariables.email;

        getName = AttendXPressDB.rawQuery("SELECT name FROM " + GlobalVariables.email + " WHERE email='" + sEmail + "'", null);
        if (getName != null) {
            nameDisplay.setText((getName.moveToFirst()) ? getName.getString(getName.getColumnIndex("name")) : "Name not found");
        }
        emailDisplay.setText(sEmail);
        promptNameDisplay.setText("Hi, " + nameDisplay.getText() + ".");

        Cursor loadedProfile = AttendXPressDB.rawQuery("SELECT profile_picture FROM " + GlobalVariables.email + " WHERE email='" + sEmail + "'", null);
        if (loadedProfile != null) {
            if (loadedProfile.moveToFirst()) {
                String sProfile = loadedProfile.getString(0);
                if (sProfile != null) {
                    Uri profileUri = Uri.parse(sProfile);
                    Bitmap profileBitmap = decodeUri(profileUri);
                    miniProfile.setImageBitmap(profileBitmap);
                }
            }
        }

        viewProfile.setOnClickListener(v -> {
            Intent i = new Intent(Home.this, Profile.class);
            startActivity(i);
        });
        checkInAttendance.setOnClickListener(v -> {
            Intent i = new Intent(Home.this, Attendance.class);
            startActivity(i);
        });
    }

    private Bitmap decodeUri(Uri profileUri) {
        try {
            Log.e("Profile", "Decoding URI: " + profileUri.toString());

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getContentResolver().openInputStream(profileUri), null, options);

            final int SIZE = 200;
            int scale = 1;

            while (options.outWidth / scale / 2 >= SIZE && options.outHeight / scale / 2 >= SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(profileUri), null, o2);
            if(bitmap == null)
                Log.e("Profile", "Decoding failed: bitmap is null");
            else
                Log.e("Profile", "Decoding successful");

            return bitmap;
        } catch (Exception e) {
            Log.e("Profile", "Decoding error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}

