package com.example.attendxpress;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class Attendance extends AppCompatActivity {
    Button checkInToday;
    Button checkPendingAttendance;
    Button moveHome;
    SQLiteDatabase attendanceDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance);

        checkInToday = findViewById(R.id.checkInToday);
        checkPendingAttendance = findViewById(R.id.checkPendingAttendance);
        moveHome = findViewById(R.id.moveHome);

        attendanceDB = openOrCreateDatabase("attendanceDB" + GlobalVariables.email, Context.MODE_PRIVATE, null);
        attendanceDB.execSQL("CREATE TABLE IF NOT EXISTS attendancedb" + GlobalVariables.email + "(id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT NOT NULL, day TEXT NOT NULL, isPresent INTEGER NOT NULL)");

        // display the attendance stacks
        constructAttendanceElements();

        checkInToday.setOnClickListener(v -> {
            Cursor c = attendanceDB.rawQuery("SELECT * FROM attendanceDB" + GlobalVariables.email + " WHERE date=?", new String[]{GlobalVariables.getCurrentDate()});
            if (c.getCount() > 0) {
                Toast.makeText(this, "You have already checked in for today.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(Attendance.this, Attendance_Verification.class);
            startActivity(i);
        });
        checkPendingAttendance.setOnClickListener(v -> {
            Intent i = new Intent(Attendance.this, Attendance_Verification_Check_Pending.class);
            startActivity(i);
        });
        moveHome.setOnClickListener(v -> {
            Intent i = new Intent(Attendance.this, Home.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }

    private void constructAttendanceElements() {
        LinearLayout attendanceStack = findViewById(R.id.attendanceStack);
        Cursor c = attendanceDB.rawQuery("SELECT * FROM attendanceDB" + GlobalVariables.email, null);

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
                } else if (!(isPresent)) {
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
        }
        else {
            TextView displayNoAttendance = findViewById(R.id.displayNoAttendance);
            displayNoAttendance.setText("No records found.");
            displayNoAttendance.setVisibility(View.VISIBLE);
        }
    }
}
