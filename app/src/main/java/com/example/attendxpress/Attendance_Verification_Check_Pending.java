package com.example.attendxpress;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Attendance_Verification_Check_Pending extends AppCompatActivity {
    Button moveHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_verification_check_pending);

        moveHome = findViewById(R.id.moveHome);
        moveHome.setOnClickListener(v -> {
            Intent i = new Intent(Attendance_Verification_Check_Pending.this, Home.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }
}
