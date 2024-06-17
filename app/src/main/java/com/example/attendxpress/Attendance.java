package com.example.attendxpress;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class Attendance extends AppCompatActivity {
    Button checkInToday;
    Button checkPendingAttendance;
    LinearLayout attendanceStack;
    SQLiteDatabase AttendanceDB;
    SQLiteDatabase PendingAttendanceDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkInToday = findViewById(R.id.checkInToday);
        checkPendingAttendance = findViewById(R.id.checkPendingAttendance);
        attendanceStack = findViewById(R.id.attendanceStack);
        AttendanceDB = openOrCreateDatabase("AttendanceDB", Context.MODE_PRIVATE, null);
        AttendanceDB.execSQL("CREATE TABLE IF NOT EXISTS attendance_records(id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT NOT NULL, date TEXT NOT NULL, day TEXT NOT NULL, isPresent INTEGER NOT NULL)");
        PendingAttendanceDB = openOrCreateDatabase("PendingAttendanceDB", Context.MODE_PRIVATE, null);
        PendingAttendanceDB.execSQL("CREATE TABLE IF NOT EXISTS pending_attendance_records(id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT NOT NULL, date TEXT NOT NULL, day TEXT NOT NULL, pendingState TEXT NOT NULL, pitikImage TEXT NOT NULL)");

        // display the attendance stacks
        constructAttendanceElements();

        checkInToday.setOnClickListener(v -> {
            Cursor findUserPendingAttendance = PendingAttendanceDB.rawQuery("SELECT * FROM pending_attendance_records WHERE email=? AND date=?", new String[]{GlobalVariables.email, GlobalVariables.getCurrentDate()});
            Cursor findUserAttendance = AttendanceDB.rawQuery("SELECT * FROM attendance_records WHERE email=? AND date=?", new String[]{GlobalVariables.email, GlobalVariables.getCurrentDate()});

            if (findUserPendingAttendance.moveToFirst()) {
                String pendingState = findUserPendingAttendance.getString(findUserPendingAttendance.getColumnIndex("pendingState"));

                if (pendingState.equals("PENDING")) {
                    Toast.makeText(this, "You have pending attendance verification. Please wait.", Toast.LENGTH_SHORT).show();
                    findUserPendingAttendance.close();
                    findUserAttendance.close();
                    return;
                } else if (pendingState.equals("VERIFIED") || findUserAttendance.moveToFirst()) {
                    Toast.makeText(this, "You have already checked in for today.", Toast.LENGTH_SHORT).show();
                    findUserPendingAttendance.close();
                    findUserAttendance.close();
                    return;
                }
            }
            findUserPendingAttendance.close();
            findUserAttendance.close();
            Intent i = new Intent(Attendance.this, Attendance_Verification.class);
            startActivity(i);
        });
        checkPendingAttendance.setOnClickListener(v -> {
            Intent i = new Intent(Attendance.this, Attendance_Verification_Check_Pending.class);
            startActivity(i);
        });
    }

    private void constructAttendanceElements() {
        Cursor c = AttendanceDB.rawQuery("SELECT * FROM attendance_records WHERE email=?", new String[]{GlobalVariables.email});

        if (c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndex("id"));
                String date = c.getString(c.getColumnIndex("date"));
                String day = c.getString(c.getColumnIndex("day"));
                boolean isPresent = c.getInt(c.getColumnIndex("isPresent")) == 1;

                // create ConstraintLayout
                ConstraintLayout constraintLayout = new ConstraintLayout(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        (int) (64 * getResources().getDisplayMetrics().density)
                );
                layoutParams.setMargins(2, 2, 2, 2);
                constraintLayout.setLayoutParams(layoutParams);
                if (isPresent) {
                    constraintLayout.setBackground(getResources().getDrawable(R.drawable.checkin_present, null));
                } else {
                    constraintLayout.setBackground(getResources().getDrawable(R.drawable.checkin_absent, null));
                }

                TextView displayDate = new TextView(this);
                displayDate.setId(View.generateViewId());
                displayDate.setText(date);
                displayDate.setTextColor(Color.BLACK);

                TextView displayDay = new TextView(this);
                displayDay.setId(View.generateViewId());
                displayDay.setText(day.toUpperCase());
                displayDay.setTextSize(24);
                displayDay.setTextColor(Color.BLACK);

                TextView displayIsPresentState = new TextView(this);
                displayIsPresentState.setId(View.generateViewId());
                displayIsPresentState.setText(isPresent ? "PRESENT" : "ABSENT");
                displayIsPresentState.setTextSize(20);
                displayIsPresentState.setTextColor(Color.BLACK);

                constraintLayout.addView(displayDate);
                constraintLayout.addView(displayDay);
                constraintLayout.addView(displayIsPresentState);

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);

                constraintSet.connect(displayDay.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, (int) (24 * getResources().getDisplayMetrics().density));
                constraintSet.connect(displayDay.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, (int) (8 * getResources().getDisplayMetrics().density));

                constraintSet.connect(displayIsPresentState.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, (int) (24 * getResources().getDisplayMetrics().density));
                constraintSet.connect(displayIsPresentState.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                constraintSet.connect(displayIsPresentState.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

                constraintSet.connect(displayDate.getId(), ConstraintSet.START, displayDay.getId(), ConstraintSet.START);
                constraintSet.connect(displayDate.getId(), ConstraintSet.TOP, displayDay.getId(), ConstraintSet.BOTTOM, (int) (4 * getResources().getDisplayMetrics().density));

                constraintSet.applyTo(constraintLayout);
                attendanceStack.addView(constraintLayout);
            } while (c.moveToNext());
        } else {
            TextView displayNoAttendance = findViewById(R.id.attendanceDisplayNoAttendance);
            displayNoAttendance.setText("No records found.");
            displayNoAttendance.setVisibility(View.VISIBLE);
        }
        c.close(); // Close cursor after use
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

    @Override
    protected void onResume() {
        super.onResume();
        attendanceStack.removeAllViews();
        constructAttendanceElements();

    }
}