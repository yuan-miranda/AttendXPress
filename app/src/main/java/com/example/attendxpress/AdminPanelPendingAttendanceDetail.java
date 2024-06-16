package com.example.attendxpress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AdminPanelPendingAttendanceDetail extends AppCompatActivity {
    ImageView adminPanelUserProfile;
    ImageView adminPanelPitikImage;
    TextView adminPanelEmailDisplay;
    TextView adminPanelDateDisplay;
    TextView adminPanelNameDisplay;
    Button acceptPitik;
    Button rejectPitik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminpanel_pending_attendance_detail);

        adminPanelUserProfile = findViewById(R.id.adminPanelUserProfile);
        adminPanelPitikImage = findViewById(R.id.adminPanelPitikImage);
        adminPanelEmailDisplay = findViewById(R.id.adminPanelEmailDisplay);
        adminPanelDateDisplay = findViewById(R.id.adminPanelDateDisplay);
        adminPanelNameDisplay = findViewById(R.id.adminPanelNameDisplay);
        acceptPitik = findViewById(R.id.acceptPitik);
        rejectPitik = findViewById(R.id.rejectPitik);

        // Retrieve data from Intent extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String userProfileUri = extras.getString("userProfileUri");
            Bitmap pitikBitmap = extras.getParcelable("pitikBitmap");
            String userEmail = extras.getString("userEmail");
            String date = extras.getString("date");
            String userName = extras.getString("userName");

            // Set data to views
            if (userProfileUri != null) {
                adminPanelUserProfile.setImageURI(Uri.parse(userProfileUri));
            }
            if (pitikBitmap != null) {
                adminPanelPitikImage.setImageBitmap(pitikBitmap);
            }
            adminPanelEmailDisplay.setText(userEmail);
            adminPanelDateDisplay.setText(date);
            adminPanelNameDisplay.setText(userName);
        }
    }

}
