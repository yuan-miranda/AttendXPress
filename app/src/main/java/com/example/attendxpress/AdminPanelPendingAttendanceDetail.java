package com.example.attendxpress;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminPanelPendingAttendanceDetail extends AppCompatActivity {
    ImageView adminPanelUserProfile;
    ImageView adminPanelPitikImage;
    TextView adminPanelEmailDisplay;
    TextView adminPanelDateDisplay;
    TextView adminPanelNameDisplay;
    TextView fingerprintVerifiedDisplay;
    Button acceptPitik;
    Button rejectPitik;
    SQLiteDatabase PendingAttendanceDB;
    SQLiteDatabase AttendanceDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminpanel_pending_attendance_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adminPanelUserProfile = findViewById(R.id.adminPanelUserProfile);
        adminPanelPitikImage = findViewById(R.id.adminPanelPitikImage);
        adminPanelEmailDisplay = findViewById(R.id.adminPanelEmailDisplay);
        adminPanelDateDisplay = findViewById(R.id.adminPanelDateDisplay);
        adminPanelNameDisplay = findViewById(R.id.adminPanelNameDisplay);
        fingerprintVerifiedDisplay = findViewById(R.id.fingerprintVerifiedDisplay);
        acceptPitik = findViewById(R.id.acceptPitik);
        rejectPitik = findViewById(R.id.rejectPitik);
        AttendanceDB = openOrCreateDatabase("AttendanceDB", Context.MODE_PRIVATE, null);
        AttendanceDB.execSQL("CREATE TABLE IF NOT EXISTS attendance_records(id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT NOT NULL, date TEXT NOT NULL, day TEXT NOT NULL, isPresent INTEGER NOT NULL)");
        PendingAttendanceDB = openOrCreateDatabase("PendingAttendanceDB", Context.MODE_PRIVATE, null);
        PendingAttendanceDB.execSQL("CREATE TABLE IF NOT EXISTS pending_attendance_records(id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT NOT NULL, date TEXT NOT NULL, day TEXT NOT NULL, pendingState TEXT NOT NULL, pitikImage TEXT NOT NULL)");

        // Retrieve data from Intent extras
        Bundle extras = getIntent().getExtras();
        int id = extras.getInt("id");
        String userProfileUri = extras.getString("userProfileUri");
        Bitmap pitikBitmap = extras.getParcelable("pitikBitmap");
        String userEmail = extras.getString("userEmail");
        String date = extras.getString("date");
        String day = extras.getString("day");
        String userName = extras.getString("userName");
        String pendingState = extras.getString("pendingState");

        // Set data to views
        if (userProfileUri != null) {
            adminPanelUserProfile.setImageURI(Uri.parse(userProfileUri));
        }
        if (pitikBitmap != null) {
            adminPanelPitikImage.setImageBitmap(pitikBitmap);
            fingerprintVerifiedDisplay.setVisibility(View.INVISIBLE);
        } else {
            adminPanelPitikImage.setVisibility(View.INVISIBLE);
            fingerprintVerifiedDisplay.setVisibility(View.VISIBLE);
        }
        adminPanelEmailDisplay.setText(userEmail);
        adminPanelDateDisplay.setText(date);
        adminPanelNameDisplay.setText(userName);

        acceptPitik.setOnClickListener(v -> {
            if (pendingState.equals("VERIFIED") || pendingState.equals("REJECTED")) {
                Toast.makeText(this, "You cannot modify verified or rejected attendance.", Toast.LENGTH_SHORT).show();
            } else {
                updatePendingState(id, "VERIFIED");
                insertAttendanceData(date, day, true);
                Toast.makeText(this, "Accepted successfully.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        rejectPitik.setOnClickListener(v -> {
            if (pendingState.equals("VERIFIED") || pendingState.equals("REJECTED")) {
                Toast.makeText(this, "You cannot modify verified or rejected attendance.", Toast.LENGTH_SHORT).show();
            } else {
                updatePendingState(id, "REJECTED");
                Toast.makeText(this, "Rejected successfully.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updatePendingState(int id, String state) {
        String email = adminPanelEmailDisplay.getText().toString();
        String date = adminPanelDateDisplay.getText().toString();
        ContentValues cv = new ContentValues();
        cv.put("pendingState", state);
        PendingAttendanceDB.update("pending_attendance_records", cv, "id=? AND email=? AND date=?", new String[]{String.valueOf(id), email, date});
    }

    private void insertAttendanceData(String date, String day, boolean isPresent) {
        String email = adminPanelEmailDisplay.getText().toString();
        ContentValues cv = new ContentValues();
        cv.put("email", email);
        cv.put("date", date);
        cv.put("day", day);
        cv.put("isPresent", isPresent ? 1: 0);
        AttendanceDB.insert("attendance_records", null, cv);
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
