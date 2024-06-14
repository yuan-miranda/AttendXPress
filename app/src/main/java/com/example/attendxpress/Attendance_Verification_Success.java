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
    SQLiteDatabase attendanceDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_verification_success);

        checkPendingAttendance = findViewById(R.id.checkPendingAttendance_avs);
        moveHome = findViewById(R.id.moveHome);

        attendanceDB = openOrCreateDatabase("attendanceDB" + GlobalVariables.email, Context.MODE_PRIVATE, null);
        attendanceDB.execSQL("CREATE TABLE IF NOT EXISTS attendancedb(id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT NOT NULL, day TEXT NOT NULL, isPresent INTEGER NOT NULL)");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        String dayOfWeek = dayFormat.format(calendar.getTime());

        insertAttendanceData(currentDate, dayOfWeek, true);

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

    private void insertAttendanceData(String date, String day, boolean isPresent) {
        ContentValues cv = new ContentValues();
        cv.put("date", date);
        cv.put("day", day);
        cv.put("isPresent", isPresent ? 1: 0);
        attendanceDB.insert("attendanceDB" + GlobalVariables.email, null, cv);
    }
}
