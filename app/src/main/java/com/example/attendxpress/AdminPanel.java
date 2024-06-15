package com.example.attendxpress;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class AdminPanel extends AppCompatActivity {
    SQLiteDatabase AttendXPressDB;
    SQLiteDatabase PendingAttendanceDB;
    EditText adminPanelFindUser;
    LinearLayout adminPanelPendingAttendanceStack;
    TextView adminPanelDisplayNoAttendance;
    Button adminPanelLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminpanel);

        adminPanelFindUser = findViewById(R.id.adminPanelFindUser);
        adminPanelPendingAttendanceStack = findViewById(R.id.adminPanelPendingAttendanceStack);
        adminPanelDisplayNoAttendance = findViewById(R.id.adminPanelDisplayNoAttendance);
        adminPanelLogout = findViewById(R.id.adminPanelLogout);

        AttendXPressDB = openOrCreateDatabase("AttendXPressDB", Context.MODE_PRIVATE, null);
        PendingAttendanceDB = openOrCreateDatabase("PendingAttendanceDB", Context.MODE_PRIVATE, null);

        adminPanelLogout.setOnClickListener(v -> {
            Intent i = new Intent(AdminPanel.this, Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });

        adminPanelFindUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchFirstUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void searchFirstUser(String user) {
        adminPanelPendingAttendanceStack.removeAllViews();

        if (user.length() != 0) {
            try {
                Cursor findTable = AttendXPressDB.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name != 'android_metadata'", null);
                boolean found = false;
                if (findTable.moveToFirst()) {
                    do {
                        Cursor findUsers = AttendXPressDB.rawQuery("SELECT * FROM " + findTable.getString(0) + " WHERE name LIKE '%" + user + "%' OR email LIKE '%" + user + "%'", null);
                        if (findUsers.getCount() > 0) {
                            found = true;
                            while (findUsers.moveToNext()) {
                                String email = findUsers.getString(findUsers.getColumnIndex("email"));
                                TextView userTextView = new TextView(this);

                                userTextView.setText(email);
                                userTextView.setOnClickListener(v -> {
                                    adminPanelPendingAttendanceStack.removeAllViews();
                                    constructPendingAttendanceElements(email);
                                });

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                userTextView.setLayoutParams(params);
                                adminPanelPendingAttendanceStack.addView(userTextView);
                            }
                        }
                    } while (findTable.moveToNext());
                }

                if (!found) {
                    adminPanelDisplayNoAttendance.setVisibility(View.VISIBLE);
                    adminPanelDisplayNoAttendance.setText("User not found.");
                } else {
                    adminPanelDisplayNoAttendance.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            adminPanelPendingAttendanceStack.removeAllViews();
            adminPanelDisplayNoAttendance.setVisibility(View.GONE);
        }
    }

    private void constructPendingAttendanceElements(String user) {
        Cursor c = PendingAttendanceDB.rawQuery("SELECT * FROM " + user, null);

        if (c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndex("id"));
                String date = c.getString(c.getColumnIndex("date"));
                String day = c.getString(c.getColumnIndex("day"));
                String pendingState = c.getString(c.getColumnIndex("pendingState"));

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
                else if (pendingState.equals("FAILED")) {
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
                adminPanelPendingAttendanceStack.addView(constraintLayout);
            } while (c.moveToNext());
        }
        else {
            TextView displayNoAttendance = findViewById(R.id.adminPanelDisplayNoAttendance);
            displayNoAttendance.setText("No records found.");
            displayNoAttendance.setVisibility(View.VISIBLE);
        }
    }
}
