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
import android.view.MenuItem;
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
    TextView nameDisplay;
    TextView emailDisplay;
    SQLiteDatabase AttendXPressDB;
    ImageView profile;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedIntanceState) {
        super.onCreate(savedIntanceState);
        setContentView(R.layout.profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        logout = findViewById(R.id.logout);
        nameDisplay = findViewById(R.id.nameDisplay_profile);
        emailDisplay = findViewById(R.id.emailDisplay_profile);
        profile = findViewById(R.id.profile);

        AttendXPressDB = openOrCreateDatabase("AttendXPressDB", Context.MODE_PRIVATE, null);
        AttendXPressDB.execSQL("CREATE TABLE IF NOT EXISTS users(email VARCHAR PRIMARY KEY, password VARCHAR, name VARCHAR, profile_picture TEXT);");

        String sEmail = GlobalVariables.email;

        Cursor getName = AttendXPressDB.rawQuery("SELECT name FROM users WHERE email=?", new String[]{sEmail});
        if (getName != null) {
            nameDisplay.setText((getName.moveToFirst()) ? getName.getString(getName.getColumnIndex("name")) : "Name not found");
        }
        emailDisplay.setText(sEmail);

        Cursor loadedProfile = AttendXPressDB.rawQuery("SELECT profile_picture FROM users WHERE email=?", new String[]{sEmail});
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
        loadedProfile.close();

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                if (uri != null) {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    profile.setImageURI(uri);
                    AttendXPressDB.execSQL("UPDATE users SET profile_picture='" + uri + "' WHERE email='" + sEmail + "'");
                }
            }
        });

        logout.setOnClickListener(v -> {
            Intent i = new Intent(Profile.this, Login.class);
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
                    AttendXPressDB.execSQL("UPDATE users SET name='" + newName + "' WHERE email='" + sEmail + "'");
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
            return bitmap;
        } catch (Exception e) {
            Log.e("Profile", "Decoding error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
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
