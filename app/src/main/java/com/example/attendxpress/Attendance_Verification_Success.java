package com.example.attendxpress;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Attendance_Verification_Success extends AppCompatActivity {
    Button checkPendingAttendance;
    Button moveHome;
    SQLiteDatabase PendingAttendanceDB;
    byte[] pitikImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_verification_success);

        checkPendingAttendance = findViewById(R.id.checkPendingAttendance_avs);
        moveHome = findViewById(R.id.moveHome);
        pitikImage = new byte[0];

        PendingAttendanceDB = openOrCreateDatabase("PendingAttendanceDB", Context.MODE_PRIVATE, null);

        Intent intent = getIntent();
        if (intent.hasExtra("pitikImage")) {
            pitikImage = intent.getByteArrayExtra("pitikImage");
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        String dayOfWeek = dayFormat.format(calendar.getTime());

        insertPendingAttendanceData(GlobalVariables.email, currentDate, dayOfWeek, "PENDING");

        checkPendingAttendance.setOnClickListener(v -> {
            Intent i = new Intent(Attendance_Verification_Success.this, Attendance_Verification_Check_Pending.class);
            startActivity(i);
        });
        moveHome.setOnClickListener(v -> {
            Intent i = new Intent(Attendance_Verification_Success.this, Home.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }

    private void insertPendingAttendanceData(String email, String date, String day, String pendingState) {
        PendingAttendanceDB.execSQL("CREATE TABLE IF NOT EXISTS pending_attendance_records(id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT NOT NULL, date TEXT NOT NULL, day TEXT NOT NULL, pendingState TEXT NOT NULL, pitikImage TEXT NOT NULL)");

        ContentValues cv = new ContentValues();
        cv.put("email", email);
        cv.put("date", date);
        cv.put("day", day);
        cv.put("pendingState", pendingState);
        cv.put("pitikImage", pitikImage);
        PendingAttendanceDB.insert("pending_attendance_records", null, cv);
    }
}
