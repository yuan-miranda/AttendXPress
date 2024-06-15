package com.example.attendxpress;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class Profile extends AppCompatActivity {
    Button logout;
    Button moveHome;
    TextView nameDisplay;
    TextView emailDisplay;
    SQLiteDatabase AttendXPressDB;
    Intent getEmail;
    Cursor getName;
    ImageView profile;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedIntanceState) {
        super.onCreate(savedIntanceState);
        setContentView(R.layout.profile);

        logout = findViewById(R.id.logout);
        moveHome = findViewById(R.id.moveHome);
        nameDisplay = findViewById(R.id.nameDisplay_profile);
        emailDisplay = findViewById(R.id.emailDisplay_profile);
        profile = findViewById(R.id.profile);

        AttendXPressDB = openOrCreateDatabase("AttendXPressDB", Context.MODE_PRIVATE, null);

        String sEmail = GlobalVariables.email;

        getName = AttendXPressDB.rawQuery("SELECT name FROM " + GlobalVariables.email + " WHERE email='" + sEmail + "'", null);
        if (getName != null) {
            nameDisplay.setText((getName.moveToFirst()) ? getName.getString(getName.getColumnIndex("name")) : "Name not found");
        }
        emailDisplay.setText(sEmail);

        Cursor loadedProfile = AttendXPressDB.rawQuery("SELECT profile_picture FROM " + GlobalVariables.email + " WHERE email='" + sEmail + "'", null);
        if (loadedProfile != null) {
            if (loadedProfile.moveToFirst()) {
                String sProfile = loadedProfile.getString(0);
                if (sProfile != null) {
                    Uri profileUri = Uri.parse(sProfile);
                    Bitmap profileBitmap = decodeUri(profileUri);
                    profile.setImageBitmap(profileBitmap);
                }
            }
        }

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                if (uri != null) {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    profile.setImageURI(uri);
                    AttendXPressDB.execSQL("UPDATE " + GlobalVariables.email + " SET profile_picture='" + uri + "' WHERE email='" + sEmail + "'");
                }
            }
        });

        logout.setOnClickListener(v -> {
            Intent i = new Intent(Profile.this, Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
        moveHome.setOnClickListener(v -> {
            Intent i = new Intent(Profile.this, Home.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
        nameDisplay.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Rename");

            View view = LayoutInflater.from(this).inflate(R.layout.rename_dialog, null);
            final EditText inputNewName = view.findViewById(R.id.inputNewName);
            builder.setView(view);

            builder.setPositiveButton(android.R.string.ok, ((dialog, which) -> {
                dialog.dismiss();
                String newName = inputNewName.getText().toString().trim();
                if (newName.length() == 0) {
                    Toast.makeText(this, "New name cant be empty.", Toast.LENGTH_SHORT).show();
                } else {
                    nameDisplay.setText(newName);
                    AttendXPressDB.execSQL("UPDATE " + GlobalVariables.email + " SET name='" + newName + "' WHERE email='" + sEmail + "'");
                }
            }));
            builder.setNegativeButton(android.R.string.cancel, ((dialog, which) -> dialog.cancel()));
            builder.show();
        });
        profile.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.setType("image/*");
            i.addCategory(Intent.CATEGORY_OPENABLE);
            activityResultLauncher.launch(i);
        });
    }

    private Bitmap decodeUri(Uri profileUri) {
        try {
            Log.e("Profile", "Decoding URI: " + profileUri.toString());

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getContentResolver().openInputStream(profileUri), null, options);

            final int SIZE = 200;
            int scale = 1;

            while (options.outWidth / scale / 2 >= SIZE && options.outHeight / scale / 2 >= SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(profileUri), null, o2);
            if(bitmap == null)
                Log.e("Profile", "Decoding failed: bitmap is null");
            else
                Log.e("Profile", "Decoding successful");

            return bitmap;
        } catch (Exception e) {
            Log.e("Profile", "Decoding error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
