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
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity {
    Button checkInAttendance;
    TextView nameDisplay;
    TextView emailDisplay;
    TextView promptNameDisplay;
    SQLiteDatabase AttendXPressDB;
    ImageView miniProfile;

    @Override
    protected void onCreate(Bundle savedIntanceState) {
        super.onCreate(savedIntanceState);
        setContentView(R.layout.home);

        checkInAttendance = findViewById(R.id.checkInAttendance);
        nameDisplay = findViewById(R.id.nameDisplay);
        emailDisplay = findViewById(R.id.emailDisplay);
        promptNameDisplay = findViewById(R.id.promptNameDisplay);
        miniProfile = findViewById(R.id.miniProfile);

        AttendXPressDB = openOrCreateDatabase("AttendXPressDB", Context.MODE_PRIVATE, null);
        AttendXPressDB.execSQL("CREATE TABLE IF NOT EXISTS users(email VARCHAR PRIMARY KEY, password VARCHAR, name VARCHAR, profile_picture TEXT);");

        miniProfile.setOnClickListener(v -> {
            Intent i = new Intent(Home.this, Profile.class);
            startActivity(i);
        });
        checkInAttendance.setOnClickListener(v -> {
            Intent i = new Intent(Home.this, Attendance.class);
            startActivity(i);
        });
    }

    private void updateHome() {
        String sEmail = GlobalVariables.email;

        Cursor getName = AttendXPressDB.rawQuery("SELECT name FROM users WHERE email=?", new String[]{sEmail});
        if (getName != null && getName.moveToFirst()) {
            String userName = getName.getString(getName.getColumnIndex("name"));
            nameDisplay.setText(userName);
            promptNameDisplay.setText("Hi, " + userName + ".");
        } else {
            nameDisplay.setText("Name not found");
            promptNameDisplay.setText("Hi there!");
        }
        getName.close();
        emailDisplay.setText(sEmail);

        Cursor loadedProfile = AttendXPressDB.rawQuery("SELECT profile_picture FROM users WHERE email=?", new String[]{sEmail});
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
        loadedProfile.close();
    }

    private Bitmap decodeUri(Uri profileUri) {
        try {
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
            return bitmap;
        } catch (Exception e) {
            Log.e("Profile", "Decoding error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHome();
    }
}

