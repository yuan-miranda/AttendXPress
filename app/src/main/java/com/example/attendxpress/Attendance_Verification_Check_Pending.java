package com.example.attendxpress;

import android.content.ContentValues;
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

public class Attendance_Verification_Check_Pending extends AppCompatActivity {
    SQLiteDatabase PendingAttendanceDB;
    LinearLayout attendanceStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_verification_check_pending);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        attendanceStack = findViewById(R.id.checkPendingAttendanceStack);
        PendingAttendanceDB = openOrCreateDatabase("PendingAttendanceDB", Context.MODE_PRIVATE, null);
        PendingAttendanceDB.execSQL("CREATE TABLE IF NOT EXISTS pending_attendance_records(id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT NOT NULL, date TEXT NOT NULL, day TEXT NOT NULL, pendingState TEXT NOT NULL, pitikImage TEXT NOT NULL)");

        constructPendingAttendanceElements();
    }

    private void constructPendingAttendanceElements() {
        try {
            Cursor findPendingAttendanceRecord = PendingAttendanceDB.rawQuery("SELECT * FROM pending_attendance_records WHERE email=?", new String[]{GlobalVariables.email});
            if (findPendingAttendanceRecord.moveToFirst()) {
                do {
                    int id = findPendingAttendanceRecord.getInt(findPendingAttendanceRecord.getColumnIndex("id"));
                    String date = findPendingAttendanceRecord.getString(findPendingAttendanceRecord.getColumnIndex("date"));
                    String day = findPendingAttendanceRecord.getString(findPendingAttendanceRecord.getColumnIndex("day"));
                    String pendingState = findPendingAttendanceRecord.getString(findPendingAttendanceRecord.getColumnIndex("pendingState"));

                    // create ConstraintLayout
                    ConstraintLayout constraintLayout = new ConstraintLayout(this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            (int) (64 * getResources().getDisplayMetrics().density)
                    );
                    layoutParams.setMargins(2, 2, 2, 2);
                    constraintLayout.setLayoutParams(layoutParams);

                    if (pendingState.equals("PENDING")) {
                        constraintLayout.setBackground(getResources().getDrawable(R.drawable.checkin_none, null));
                    }
                    else if (pendingState.equals("REJECTED")) {
                        constraintLayout.setBackground(getResources().getDrawable(R.drawable.checkin_absent, null));
                    }
                    else if (pendingState.equals("VERIFIED")) {
                        constraintLayout.setBackground(getResources().getDrawable(R.drawable.checkin_present, null));
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
                    displayIsPresentState.setText(pendingState);
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
                } while (findPendingAttendanceRecord.moveToNext());
            }
            else {
                TextView displayNoAttendance = findViewById(R.id.checkPendingDisplayNoAttendance);
                displayNoAttendance.setText("No records found.");
                displayNoAttendance.setVisibility(View.VISIBLE);
            }
            findPendingAttendanceRecord.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        constructPendingAttendanceElements();
    }
}
