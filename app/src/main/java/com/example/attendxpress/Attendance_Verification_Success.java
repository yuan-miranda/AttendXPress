package com.example.attendxpress;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Attendance_Verification_Success extends AppCompatActivity {
    Button checkPendingAttendance;
    SQLiteDatabase PendingAttendanceDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_verification_success);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkPendingAttendance = findViewById(R.id.checkPendingAttendance_avs);
        byte[] pitikImage = new byte[0];

        PendingAttendanceDB = openOrCreateDatabase("PendingAttendanceDB", Context.MODE_PRIVATE, null);
        PendingAttendanceDB.execSQL("CREATE TABLE IF NOT EXISTS pending_attendance_records(id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT NOT NULL, date TEXT NOT NULL, day TEXT NOT NULL, pendingState TEXT NOT NULL, pitikImage TEXT NOT NULL)");

        Intent intent = getIntent();
        if (intent.hasExtra("pitikImage")) {
            pitikImage = intent.getByteArrayExtra("pitikImage");
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        String dayOfWeek = dayFormat.format(calendar.getTime());

        insertPendingAttendanceData(GlobalVariables.email, currentDate, dayOfWeek, "PENDING", pitikImage);

        checkPendingAttendance.setOnClickListener(v -> {
            Intent i = new Intent(Attendance_Verification_Success.this, Attendance_Verification_Check_Pending.class);
            startActivity(i);
            finish();
        });
    }

    private void insertPendingAttendanceData(String email, String date, String day, String pendingState, byte[] pitikImage) {
        ContentValues cv = new ContentValues();
        cv.put("email", email);
        cv.put("date", date);
        cv.put("day", day);
        cv.put("pendingState", pendingState);
        cv.put("pitikImage", pitikImage);
        PendingAttendanceDB.insert("pending_attendance_records", null, cv);
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
